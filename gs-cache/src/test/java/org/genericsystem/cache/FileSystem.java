package org.genericsystem.cache;

import java.util.Optional;

import org.genericsystem.api.core.Snapshot;
import org.genericsystem.cache.FileSystem.Directory;
import org.genericsystem.cache.FileSystem.FileType;
import org.genericsystem.kernel.annotations.Components;
import org.genericsystem.kernel.annotations.Dependencies;
import org.genericsystem.kernel.annotations.InstanceClass;
import org.genericsystem.kernel.annotations.SystemGeneric;
import org.genericsystem.kernel.annotations.constraints.InstanceValueClassConstraint;
import org.genericsystem.kernel.annotations.constraints.SingularConstraint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SystemGeneric
@InstanceValueClassConstraint(String.class)
// @Components(FileSystem.class)
@Dependencies(FileType.class)
@InstanceClass(Directory.class)
public class FileSystem extends Generic {
	protected static Logger log = LoggerFactory.getLogger(FileSystem.class);
	private static final String SEPARATOR = "/";
	private static final byte[] EMPTY = "<html/>".getBytes();

	public static class Directory extends Generic {
		public Snapshot<File> getFiles() {
			return (Snapshot) getHolders(getRoot().find(FileType.class));
		}

		public File getFile(String name) {
			Optional<Generic> holder = getHolders(getRoot().find(FileType.class)).get().filter(x -> name.equals(x.getValue())).findFirst();
			return holder.isPresent() ? (File) holder.get() : null;
		}

		public File addFile(String name) {
			return addFile(name, EMPTY);
		}

		public File addFile(String name, byte[] content) {
			File result = (File) addHolder(getRoot().find(FileType.class), name);
			result.setContent(content);
			return result;
		}

		public File setFile(String name) {
			return setFile(name, EMPTY);
		}

		public File setFile(String name, byte[] content) {
			File result = (File) setHolder(getRoot().find(FileType.class), name);
			result.setContent(content);
			return result;
		}

		public Snapshot<Directory> getDirectories() {
			return (Snapshot) getInheritings();
		}

		public Directory getDirectory(String name) {
			Optional<Generic> optional = getInheritings().get().filter(x -> x.getValue().equals(name)).findFirst();
			return optional.isPresent() ? (Directory) optional.get() : null;

		}

		public Directory addDirectory(String name) {
			return (Directory) getMeta().addInstance(this, name);

		}

		public Directory setDirectory(String name) {
			return (Directory) getMeta().setInstance(this, name);
		}

		public String getShortPath() {
			return (String) getValue();
		}
	}

	@SystemGeneric
	@Components(FileSystem.class)
	@InstanceValueClassConstraint(String.class)
	@InstanceClass(File.class)
	@Dependencies(FileContent.class)
	public static class FileType extends Generic {

	}

	public static class File extends Generic {
		public byte[] getContent() {
			return (byte[]) getHolders(getRoot().find(FileContent.class)).get().findFirst().get().getValue();
		}

		public Generic setContent(byte[] content) {
			return setHolder(getRoot().find(FileContent.class), content);
		}

		public String getShortPath() {
			return (String) getValue();
		}
	}

	@SystemGeneric
	@SingularConstraint
	@Components(FileType.class)
	@InstanceValueClassConstraint(byte[].class)
	public static class FileContent extends Generic {
	}

	public Snapshot<Generic> getRootDirectories() {
		return getInstances();
	}

	public Directory getRootDirectory(String name) {
		Optional<Generic> optional = getRootDirectories().get().filter(x -> x.getValue().equals(name)).findFirst();
		return optional.isPresent() ? (Directory) optional.get() : null;
	}

	public Directory addRootDirectory(String name) {
		if (getRootDirectory(name) != null)
			throw new IllegalStateException("Root directory : " + name + " already exists");
		return (Directory) addInstance(name);
	}

	public Directory setRootDirectory(String name) {
		return (Directory) setInstance(name);
	}

	public byte[] getFileContent(String resource) {
		if (resource.startsWith(SEPARATOR))
			resource = resource.substring(1);
		String[] files = resource.split(SEPARATOR);
		Directory directory = getRootDirectory(files[0]);
		if (directory == null)
			return null;
		for (int i = 1; i < files.length - 1; i++) {
			directory = directory.getDirectory(files[i]);
			if (directory == null)
				return null;
		}
		File file = directory.getFile(files[files.length - 1]);
		if (file == null)
			return null;
		return file.getContent();
	}

	public Generic setFile(String resource) {
		return setFile(resource, EMPTY);
	}

	public Generic setFile(String resource, byte[] content) {
		if (resource.startsWith(SEPARATOR))
			resource = resource.substring(1);
		String[] pathToResource = resource.split(SEPARATOR);
		Directory directory = setRootDirectory(pathToResource[0]);
		for (int i = 1; i < pathToResource.length - 1; i++)
			directory = directory.setDirectory(pathToResource[i]);
		return directory.setFile(pathToResource[pathToResource.length - 1], content);
	}
}
