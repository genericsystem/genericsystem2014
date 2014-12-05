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

	private static final Logger log = LoggerFactory.getLogger(Archiver.class);

	private static final long ARCHIVER_COEFF = 5L;

	private static final String PATTERN = "yyyy.MM.dd_HH-mm-ss.SSS";
	private static final String MATCHING_REGEX = "[0-9]{4}.[0-9]{2}.[0-9]{2}_[0-9]{2}-[0-9]{2}-[0-9]{2}.[0-9]{3}---[0-9]+";

	protected static final String GS_EXTENSION = ".gs";
	private static final String PART_EXTENSION = ".part";
	private static final String LOCK_FILE_NAME = ".lock";

	private static final long SNAPSHOTS_PERIOD = 1000L;
	private static final long SNAPSHOTS_INITIAL_DELAY = 1000L;

	private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

	private final File directory;

	private final WriterLoaderManager<T> writerLoader;

	private FileLock lockFile;

	public Archiver(T root, String directoryPath) {
		this(root, new ZipFileManager(), directoryPath);
	}

	public Archiver(T root, FileManager fileManager, String directoryPath) {
		this(new WriterLoaderManager<>(root, fileManager), directoryPath);
	}

	public Archiver(WriterLoaderManager<T> writerLoader, String directoryPath) {
		this.writerLoader = writerLoader;
		directory = prepareAndLockDirectory(directoryPath);
		if (directory != null) {
			String snapshotPath = getSnapshotPath(directory);
			if (snapshotPath != null)
				writerLoader.loadSnapshot(snapshotPath);
		}
	}

	public Archiver<T> startScheduler() {
		if (lockFile != null && directory != null)
			if (SNAPSHOTS_PERIOD > 0L) {
				scheduler.scheduleAtFixedRate(new Runnable() {
					@Override
					public void run() {
						writerLoader.writeSnapshot(directory);
					}
				}, SNAPSHOTS_INITIAL_DELAY, SNAPSHOTS_PERIOD, TimeUnit.MILLISECONDS);
			}
		return this;
	}

	public void close() {
		if (lockFile != null && directory != null) {
			scheduler.shutdown();
			writerLoader.writeSnapshot(directory);
			try {
				lockFile.close();
				lockFile = null;
			} catch (IOException e) {
				log.error(e.getMessage(), e);
			}
		}
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
		NavigableMap<Long, File> snapshotsMap = snapshotsMap(directory, writerLoader.fileManager.getExtension());
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

	public static class WriterLoaderManager<T extends AbstractVertex<T>> {

		private final FileManager fileManager;

		private ObjectOutputStream outputStream;
		private ObjectInputStream inputStream;

		protected final T root;

		protected long ts;

		public WriterLoaderManager(T root, FileManager fileManager) {
			this.root = root;
			this.fileManager = fileManager;
		}

		public void loadSnapshot(String path) {
			try (FileInputStream fileInputStream = new FileInputStream(new File(path + fileManager.getExtension()))) {
				inputStream = fileManager.newInputStream(fileInputStream);
				Map<Long, T> vertexMap = new HashMap<>();
				for (;;)
					loadDependency(vertexMap);
			} catch (EOFException ignore) {
			} catch (ClassNotFoundException | IOException e) {
				log.error(e.getMessage(), e);
			}
		}

		private void loadDependency(Map<Long, T> vertexMap) throws IOException, ClassNotFoundException {
			int sizeDependencies = inputStream.readInt();
			for (int i = 0; i < sizeDependencies; i++)
				if (inputStream.readBoolean()) {
					long birthTs = inputStream.readLong();
					long designTs = inputStream.readLong();
					long lastReadTs = inputStream.readLong();
					long deathTs = inputStream.readLong();
					Serializable value = (Serializable) inputStream.readObject();
					T meta = loadAncestor(vertexMap);
					List<T> supers = loadAncestors(vertexMap);
					List<T> components = loadAncestors(vertexMap);
					T instance = meta == null ? root.getMeta(components.size()) : meta.getDirectInstance(value, components);
					assert false;
					T vertex = root.getCurrentCache().getBuilder().newT(null, meta, supers, value, components).restore(designTs, birthTs, lastReadTs, deathTs);
					vertexMap.put(birthTs, instance != null ? instance : root.getCurrentCache().plug(vertex));
				}
		}

		private List<T> loadAncestors(Map<Long, T> vertexMap) throws IOException {
			List<T> ancestors = new ArrayList<>();
			int sizeComponents = inputStream.readInt();
			for (int j = 0; j < sizeComponents; j++)
				ancestors.add(loadAncestor(vertexMap));
			return ancestors;
		}

		private T loadAncestor(Map<Long, T> vertexMap) throws IOException {
			long ts = inputStream.readLong();
			return ts == -1 ? (T) root : vertexMap.get(ts);
		}

		private void writeSnapshot(File directory) {
			ts = pickNewTs();
			String fileName = getFilename(ts);
			try (FileOutputStream fileOutputStream = new FileOutputStream(directory.getAbsolutePath() + File.separator + fileName + fileManager.getExtension() + PART_EXTENSION);) {
				outputStream = fileManager.newOutputStream(fileOutputStream, fileName);
				writeDependencies(getOrderedVertex(), new HashSet<>());
				outputStream.flush();
				outputStream.close();
				new File(directory.getAbsolutePath() + File.separator + fileName + fileManager.getExtension() + PART_EXTENSION).renameTo(new File(directory.getAbsolutePath() + File.separator + fileName + fileManager.getExtension()));
				manageOldSnapshots(directory);
			} catch (IOException e) {
				log.error(e.getMessage(), e);
			}
		}

		protected long pickNewTs() {
			return System.currentTimeMillis();
		}

		private void manageOldSnapshots(File directory) {
			NavigableMap<Long, File> snapshotsMap = snapshotsMap(directory, fileManager.getExtension());
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

		protected List<T> getOrderedVertex() {
			return Statics.reverseCollections(new DependenciesOrder<T>().visit(root));
		}

		public static class DependenciesOrder<T extends AbstractVertex<T>> extends LinkedHashSet<T> {
			private static final long serialVersionUID = -5970021419012502402L;

			public DependenciesOrder<T> visit(T node) {
				if (!contains(node)) {
					Iterator<T> iterator = node.getCompositesDependencies().iterator(0);
					while (iterator.hasNext())
						visit(iterator.next());
					iterator = node.getInheritingsDependencies().iterator(0);
					while (iterator.hasNext())
						visit(iterator.next());
					iterator = node.getInstancesDependencies().iterator(0);
					while (iterator.hasNext())
						visit(iterator.next());
					if (!node.isRoot())
						add(node);
				}
				return this;
			}
		}

		private void writeDependencies(List<T> dependencies, Set<T> vertexSet) throws IOException {
			outputStream.writeInt(dependencies.size());
			for (T dependency : dependencies)
				if (vertexSet.add(dependency)) {
					outputStream.writeBoolean(true);
					writeDependency(dependency);
				} else
					outputStream.writeBoolean(false);
		}

		private void writeDependency(T dependency) throws IOException {
			long[] ts = getTs(dependency);
			outputStream.writeLong(ts[0]);
			outputStream.writeLong(ts[1]);
			outputStream.writeLong(ts[2]);
			outputStream.writeLong(ts[3]);
			outputStream.writeObject(dependency.getValue());
			writeAncestor(dependency.getMeta());
			writeAncestors(dependency.getSupers());
			writeAncestors(dependency.getComponents());
		}

		protected long[] getTs(T dependency) {
			return new long[] { 0L, 0L, 0L, Long.MAX_VALUE };
		}

		private void writeAncestors(List<T> ancestors) throws IOException {
			outputStream.writeInt(ancestors.size());
			for (T ancestor : ancestors)
				writeAncestor(ancestor);
		}

		private void writeAncestor(T ancestor) throws IOException {
			outputStream.writeLong(ancestor == null ? 0 : (ancestor.isRoot() ? -1 : getTs(ancestor)[0]));
		}

	}

	public interface FileManager {

		ObjectOutputStream newOutputStream(FileOutputStream fileOutputStream, String fileName) throws IOException;

		ObjectInputStream newInputStream(FileInputStream fileInputStream) throws IOException;

		String getExtension();
	}

	public static class ZipFileManager implements FileManager {

		@Override
		public String getExtension() {
			return GS_EXTENSION + ".zip";
		}

		@Override
		public ObjectOutputStream newOutputStream(FileOutputStream fileOutputStream, String fileName) throws IOException {
			ZipOutputStream zipOutput = new ZipOutputStream(fileOutputStream);
			zipOutput.putNextEntry(new ZipEntry(fileName));
			return new ObjectOutputStream(zipOutput);
		}

		@Override
		public ObjectInputStream newInputStream(FileInputStream fileInputStream) throws IOException {
			ZipInputStream inputStream = new ZipInputStream(fileInputStream);
			inputStream.getNextEntry();
			return new ObjectInputStream(inputStream);
		}

	}

}
