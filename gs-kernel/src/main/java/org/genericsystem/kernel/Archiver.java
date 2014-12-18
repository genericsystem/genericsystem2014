package org.genericsystem.kernel;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Nicolas Feybesse
 * @author Michael Ory
 */
public class Archiver<T extends AbstractVertex<T>> {

	public static final Logger log = LoggerFactory.getLogger(Archiver.class);

	private static final long ARCHIVER_COEFF = 5L;

	private static final String PATTERN = "yyyy.MM.dd_HH-mm-ss.SSS";
	private static final String MATCHING_REGEX = "[0-9]{4}.[0-9]{2}.[0-9]{2}_[0-9]{2}-[0-9]{2}-[0-9]{2}.[0-9]{3}---[0-9]+";

	protected static final String GS_EXTENSION = ".gs";
	protected static final String ZIP_EXTENSION = ".zip";
	private static final String PART_EXTENSION = ".part";
	private static final String LOCK_FILE_NAME = ".lock";

	private static final long SNAPSHOTS_PERIOD = 1000L;
	private static final long SNAPSHOTS_INITIAL_DELAY = 1000L;

	private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

	protected final DefaultRoot<T> root;
	private final File directory;
	private FileLock lockFile;

	private final ZipFileManager zipFileManager = new ZipFileManager(new FileManager());

	public static String getFileExtension() {
		return GS_EXTENSION + ZIP_EXTENSION;
	}

	protected Archiver(DefaultRoot<T> root, String directoryPath) {
		this.root = root;
		directory = prepareAndLockDirectory(directoryPath);
		if (directory != null) {
			String snapshotPath = getSnapshotPath(directory);
			if (snapshotPath != null) {
				try {
					getLoader(zipFileManager.getObjectInputStream(snapshotPath + getFileExtension())).loadSnapshot();
				} catch (IOException | ClassNotFoundException e) {
					log.error(e.getMessage(), e);
				}
			}
		}
		startScheduler();
	}

	protected Loader getLoader(ObjectInputStream objectInputStream) {
		return new Loader(objectInputStream);
	}

	protected Saver getSaver(ObjectOutputStream objectOutputStream, long ts) {
		return new Saver(objectOutputStream, ts);
	}

	private Archiver<T> startScheduler() {
		if (directory != null && lockFile != null && SNAPSHOTS_PERIOD > 0L)
			scheduler.scheduleAtFixedRate(new Runnable() {
				@Override
				public void run() {
					try {
						doSnapshot();
					} catch (IOException e) {
						log.error(e.getMessage(), e);
					}
				}
			}, SNAPSHOTS_INITIAL_DELAY, SNAPSHOTS_PERIOD, TimeUnit.MILLISECONDS);
		return this;
	}

	public void close() {
		if (directory != null && lockFile != null) {
			scheduler.shutdown();
			try {
				doSnapshot();
				lockFile.close();
				lockFile = null;
			} catch (IOException e) {
				// TODO rollback here
				throw new IllegalStateException(e);
			}
		}
	}

	protected long pickTs() {
		return 0L;
	}

	private void doSnapshot() throws IOException {
		long ts = pickTs();
		String fileName = getFilename(ts);
		String partFileName = directory.getAbsolutePath() + File.separator + fileName + getFileExtension() + PART_EXTENSION;
		ObjectOutputStream outputStream = zipFileManager.getObjectOutputStream(partFileName);
		getSaver(outputStream, ts).saveSnapshot(directory);
		new File(partFileName).renameTo(new File(directory.getAbsolutePath() + File.separator + fileName + getFileExtension()));
		manageOldSnapshots(directory);
	}

	private void manageOldSnapshots(File directory) {
		NavigableMap<Long, File> snapshotsMap = snapshotsMap(directory, getFileExtension());
		long lastTs = snapshotsMap.lastKey();
		long firstTs = snapshotsMap.firstKey();
		long ts = firstTs;
		for (long snapshotTs : new TreeSet<>(snapshotsMap.keySet()))
			if (snapshotTs != lastTs && snapshotTs != firstTs)
				if ((snapshotTs - ts) < minInterval((lastTs - snapshotTs)))
					removeSnapshot(snapshotsMap, snapshotTs);
				else
					ts = snapshotTs;
	}

	private long minInterval(long periodNumber) {
		return (long) Math.floor(periodNumber / ARCHIVER_COEFF);
	}

	private void removeSnapshot(NavigableMap<Long, File> snapshotsMap, long ts) {
		snapshotsMap.get(ts).delete();
		snapshotsMap.remove(ts);
	}

	private File prepareAndLockDirectory(String directoryPath) {
		if (directoryPath == null)
			return null;
		File directory = new File(directoryPath);
		if (directory.exists()) {
			if (!directory.isDirectory())
				throw new IllegalStateException("Datasource path : " + directoryPath + " is not a directory");
		} else if (!directory.mkdirs())
			throw new IllegalStateException("Can't make directory : " + directoryPath);
		try {
			lockFile = new FileOutputStream(directoryPath + File.separator + LOCK_FILE_NAME).getChannel().tryLock();
			return directory;
		} catch (OverlappingFileLockException | IOException e) {
			throw new IllegalStateException("Locked directory : " + directoryPath);
		}
	}

	private String getSnapshotPath(File directory) {
		NavigableMap<Long, File> snapshotsMap = snapshotsMap(directory, getFileExtension());
		return snapshotsMap.isEmpty() ? null : directory.getAbsolutePath() + File.separator + getFilename(snapshotsMap.lastKey());
	}

	private static NavigableMap<Long, File> snapshotsMap(File directory, String extension) {
		NavigableMap<Long, File> snapshotsMap = new TreeMap<>();
		for (File file : directory.listFiles()) {
			String filename = file.getName();
			if (!file.isDirectory() && filename.endsWith(extension)) {
				filename = filename.substring(0, filename.length() - extension.length());
				if (filename.matches(MATCHING_REGEX))
					try {
						snapshotsMap.put(getTimestamp(filename), file);
					} catch (ParseException pe) {
						throw new IllegalStateException(pe);
					}
			}
		}
		return snapshotsMap;
	}

	private static long getTimestamp(final String filename) throws ParseException {
		return Long.parseLong(filename.substring(filename.lastIndexOf("---") + 3));
	}

	private static String getFilename(final long ts) {
		return new SimpleDateFormat(PATTERN).format(new Date(ts / Statics.MILLI_TO_NANOSECONDS)) + "---" + ts;
	}

	public class Saver {

		protected final ObjectOutputStream objectOutputStream;
		// protected final long ts;
		protected final Transaction<T> transaction;

		protected Saver(ObjectOutputStream objectOutputStream, long ts) {
			this.objectOutputStream = objectOutputStream;
			this.transaction = new Transaction<>(root, ts);
		}

		private void saveSnapshot(File directory) throws IOException {
			writeDependencies(getOrderedVertices(), new HashSet<>());
			objectOutputStream.flush();
			objectOutputStream.close();
		}

		private void writeDependencies(List<T> dependencies, Set<T> vertexSet) throws IOException {
			for (T dependency : dependencies)
				if (vertexSet.add(dependency))
					writeDependency(dependency);
		}

		private void writeDependency(T dependency) throws IOException {
			writeAncestorId(dependency);
			writeOtherTs(dependency);
			objectOutputStream.writeObject(dependency.getValue());
			writeAncestorId(dependency.getMeta());
			writeAncestorsId(dependency.getSupers());
			writeAncestorsId(dependency.getComponents());
			log.info("write dependency " + dependency.info());
		}

		protected void writeOtherTs(T dependency) throws IOException {

		}

		private void writeAncestorsId(List<T> ancestors) throws IOException {
			objectOutputStream.writeInt(ancestors.size());
			for (T ancestor : ancestors)
				writeAncestorId(ancestor);
		}

		protected void writeAncestorId(T ancestor) throws IOException {
			objectOutputStream.writeLong(System.identityHashCode(ancestor));
		}

		@SuppressWarnings("unchecked")
		protected List<T> getOrderedVertices() {
			return Statics.reverseCollections(new OrderedDependencies<T>(transaction.getTs()).visit((T) root));
		}
	}

	public static class OrderedDependencies<T extends AbstractVertex<T>> extends LinkedHashSet<T> {
		private static final long serialVersionUID = -5970021419012502402L;

		private final long ts;

		OrderedDependencies(long ts) {
			this.ts = ts;
		}

		public OrderedDependencies<T> visit(T node) {
			if (!contains(node)) {
				Iterator<T> iterator = node.getCompositesDependencies().iterator(ts);
				while (iterator.hasNext())
					visit(iterator.next());
				iterator = node.getInheritingsDependencies().iterator(ts);
				while (iterator.hasNext())
					visit(iterator.next());
				iterator = node.getInstancesDependencies().iterator(ts);
				while (iterator.hasNext())
					visit(iterator.next());
				add(node);
			}
			return this;
		}
	}

	protected class Loader {

		protected final ObjectInputStream objectInputStream;
		protected final Transaction<T> transaction;

		protected Loader(ObjectInputStream objectInputStream) {
			this.objectInputStream = objectInputStream;
			this.transaction = buildTransaction();
		}

		protected Transaction<T> buildTransaction() {
			return new Transaction<>(root, 0L);
		}

		private void loadSnapshot() throws ClassNotFoundException, IOException {
			try {
				Map<Long, T> vertexMap = new HashMap<>();
				for (;;)
					loadDependency(vertexMap);
			} catch (EOFException ignore) {}
		}

		protected long loadId() throws IOException {
			return objectInputStream.readLong();
		}

		protected void loadDependency(Map<Long, T> vertexMap) throws IOException, ClassNotFoundException {
			Long id = loadId();
			Serializable value = (Serializable) objectInputStream.readObject();
			T meta = loadAncestor(vertexMap);
			List<T> supers = loadAncestors(vertexMap);
			List<T> components = loadAncestors(vertexMap);
			vertexMap.put(id, transaction.getBuilder().getOrBuild(null, meta, supers, value, components));
			log.info("load dependency " + vertexMap.get(id).info() + " " + id);
		}

		protected List<T> loadAncestors(Map<Long, T> vertexMap) throws IOException {
			List<T> ancestors = new ArrayList<>();
			int sizeComponents = objectInputStream.readInt();
			for (int j = 0; j < sizeComponents; j++)
				ancestors.add(loadAncestor(vertexMap));
			return ancestors;
		}

		protected T loadAncestor(Map<Long, T> vertexMap) throws IOException {
			long designTs = objectInputStream.readLong();
			return vertexMap.get(designTs);
		}
	}

	protected static class ZipFileManager {

		private final FileManager fileManager;

		protected ZipFileManager(FileManager fileManager) {
			this.fileManager = fileManager;
		}

		protected ObjectOutputStream getObjectOutputStream(String fileName) throws IOException {
			ZipOutputStream zipOutput = new ZipOutputStream(fileManager.getFileOutputStream(fileName));
			zipOutput.putNextEntry(new ZipEntry(fileName));
			return new ObjectOutputStream(zipOutput);
		}

		protected ObjectInputStream getObjectInputStream(String fileName) throws IOException {
			ZipInputStream inputStream = new ZipInputStream(fileManager.getFileInputStream(fileName));
			inputStream.getNextEntry();
			return new ObjectInputStream(inputStream);
		}
	}

	protected static class FileManager {

		protected FileOutputStream getFileOutputStream(String fileName) throws IOException {
			return new FileOutputStream(fileName);
		}

		protected FileInputStream getFileInputStream(String fileName) throws IOException {
			return new FileInputStream(new File(fileName));
		}
	}

}
