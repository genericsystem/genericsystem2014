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
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
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

import org.genericsystem.kernel.Archiver.AbstractWriterLoader.ZipWriterLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Nicolas Feybesse
 * @author Michael Ory
 */
public class Archiver {

	private static final Logger log = LoggerFactory.getLogger(Archiver.class);

	private static final long ARCHIVER_COEFF = 5L;

	private static final String PATTERN = "yyyy.MM.dd_HH-mm-ss.SSS";
	private static final String MATCHING_REGEX = "[0-9]{4}.[0-9]{2}.[0-9]{2}_[0-9]{2}-[0-9]{2}-[0-9]{2}.[0-9]{3}---[0-9]+";
	private static final String GS_EXTENSION = ".gs";
	private static final String PART_EXTENSION = ".part";
	private static final String LOCK_FILE_NAME = ".lock";

	private static final long SNAPSHOTS_PERIOD = 1000L;
	private static final long SNAPSHOTS_INITIAL_DELAY = 1000L;

	private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

	private final File directory;

	private final AbstractWriterLoader writerLoader;

	private FileLock lockFile;

	public Archiver(Root root, String directoryPath) {
		this(new ZipWriterLoader(root), directoryPath);
	}

	public Archiver(AbstractWriterLoader writerLoader, String directoryPath) {
		this.writerLoader = writerLoader;
		directory = prepareAndLockDirectory(directoryPath);
		if (directory != null) {
			String snapshotPath = getSnapshotPath(directory);
			if (snapshotPath != null)
				writerLoader.loadSnapshot(snapshotPath);
		}
	}

	public void startScheduler() {
		if (lockFile != null)
			if (SNAPSHOTS_PERIOD > 0L) {
				scheduler.scheduleAtFixedRate(new Runnable() {
					@Override
					public void run() {
						writerLoader.writeSnapshot(directory);
					}
				}, SNAPSHOTS_INITIAL_DELAY, SNAPSHOTS_PERIOD, TimeUnit.MILLISECONDS);
			}
	}

	public void close() {
		if (lockFile != null) {
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
		NavigableMap<Long, File> snapshotsMap = snapshotsMap(directory, writerLoader.getExtension());
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

	public abstract static class AbstractWriterLoader {

		private final Root root;

		private ObjectOutputStream outputStream;
		private ObjectInputStream inputStream;

		public AbstractWriterLoader(Root root) {
			this.root = root;
		}

		public void loadSnapshot(String path) {
			try (FileInputStream fileInputStream = new FileInputStream(new File(path + getExtension()))) {
				inputStream = newInputStream(fileInputStream);
				Map<Long, Vertex> vertexMap = new HashMap<>();
				for (;;)
					loadDependency(vertexMap);
			} catch (EOFException ignore) {
			} catch (ClassNotFoundException | IOException e) {
				log.error(e.getMessage(), e);
			}
		}

		protected void loadDependency(Map<Long, Vertex> vertexMap) throws IOException, ClassNotFoundException {
			int sizeDependencies = inputStream.readInt();
			for (int i = 0; i < sizeDependencies; i++)
				if (inputStream.readBoolean()) {
					long ts = inputStream.readLong();
					Serializable value = (Serializable) inputStream.readObject();
					Class clazz = (Class) inputStream.readObject();
					Vertex meta = loadAncestor(vertexMap);
					List<Vertex> overrides = loadAncestors(vertexMap);
					List<Vertex> components = loadAncestors(vertexMap);
					vertexMap.put(ts, meta.setInstance(null, overrides, value, components.toArray(new Vertex[components.size()])));
				}
		}

		private List<Vertex> loadAncestors(Map<Long, Vertex> vertexMap) throws IOException {
			List<Vertex> ancestors = new ArrayList<>();
			int sizeComponents = inputStream.readInt();
			for (int j = 0; j < sizeComponents; j++)
				ancestors.add(loadAncestor(vertexMap));
			return ancestors;
		}

		protected Vertex loadAncestor(Map<Long, Vertex> vertexMap) throws IOException {
			long ts = inputStream.readLong();
			return ts == -1 ? root : vertexMap.get(ts);
		}

		public void writeSnapshot(File directory) {
			String fileName = getFilename(pickNewTs());
			try (FileOutputStream fileOutputStream = new FileOutputStream(directory.getAbsolutePath() + File.separator + fileName + getExtension() + PART_EXTENSION);) {
				outputStream = newOutputStream(fileOutputStream, fileName);
				writeDependencies(new DependenciesOrder().visit(root), new HashSet<>());
				outputStream.flush();
				outputStream.close();
				new File(directory.getAbsolutePath() + File.separator + fileName + getExtension() + PART_EXTENSION).renameTo(new File(directory.getAbsolutePath() + File.separator + fileName + getExtension()));
				manageOldSnapshots(directory);
			} catch (IOException e) {
				log.error(e.getMessage(), e);
			}
		}

		protected long pickNewTs() {
			return System.currentTimeMillis();
		}

		private void manageOldSnapshots(File directory) {
			NavigableMap<Long, File> snapshotsMap = snapshotsMap(directory, getExtension());
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

		static class DependenciesOrder extends ArrayDeque<Vertex> {
			private static final long serialVersionUID = -5970021419012502402L;

			DependenciesOrder visit(Vertex node) {
				if (!contains(node)) {
					node.getComposites().forEach(this::visit);
					node.getInheritings().forEach(this::visit);
					node.getInstances().forEach(this::visit);
					if (!node.isRoot())
						super.push(node);
				}
				return this;
			}
		}

		private void writeDependencies(DependenciesOrder dependencies, Set<Vertex> vertexSet) throws IOException {
			outputStream.writeInt(dependencies.size());
			for (Vertex dependency : dependencies)
				if (vertexSet.add(dependency)) {
					outputStream.writeBoolean(true);
					writeDependency(dependency);
				} else
					outputStream.writeBoolean(false);
		}

		protected void writeDependency(Vertex dependency) throws IOException {
			outputStream.writeLong(getTs(dependency));
			outputStream.writeObject(dependency.getValue());
			outputStream.writeObject(dependency.getClass());
			writeAncestor(dependency.getMeta());
			writeAncestors(dependency.getSupers());
			writeAncestors(dependency.getComponents());
		}

		protected long getTs(Vertex dependency) {
			return System.identityHashCode(dependency);
		}

		private void writeAncestors(List<Vertex> ancestors) throws IOException {
			outputStream.writeInt(ancestors.size());
			for (Vertex ancestor : ancestors)
				writeAncestor(ancestor);
		}

		protected void writeAncestor(Vertex ancestor) throws IOException {
			outputStream.writeLong(ancestor.isRoot() ? -1 : getTs(ancestor));
		}

		protected abstract ObjectOutputStream newOutputStream(FileOutputStream fileOutputStream, String fileName) throws IOException;

		protected abstract ObjectInputStream newInputStream(FileInputStream fileInputStream) throws IOException;

		public abstract String getExtension();

		public static class ZipWriterLoader extends AbstractWriterLoader {

			public static final String EXTENSION = GS_EXTENSION + ".zip";

			public ZipWriterLoader(Root root) {
				super(root);
			}

			@Override
			public String getExtension() {
				return EXTENSION;
			}

			@Override
			protected ObjectOutputStream newOutputStream(FileOutputStream fileOutputStream, String fileName) throws IOException {
				ZipOutputStream zipOutput = new ZipOutputStream(fileOutputStream);
				zipOutput.putNextEntry(new ZipEntry(fileName));
				return new ObjectOutputStream(zipOutput);
			}

			@Override
			protected ObjectInputStream newInputStream(FileInputStream fileInputStream) throws IOException {
				ZipInputStream inputStream = new ZipInputStream(fileInputStream);
				inputStream.getNextEntry();
				return new ObjectInputStream(inputStream);
			}

		}

	}

}
