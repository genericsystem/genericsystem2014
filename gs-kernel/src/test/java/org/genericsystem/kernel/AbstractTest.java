package org.genericsystem.kernel;

import java.util.List;
import java.util.stream.Collectors;

import org.genericsystem.kernel.exceptions.RollbackException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractTest {

	protected static Logger log = LoggerFactory.getLogger(AbstractTest.class);

	public abstract static class RollbackCatcher {
		public void assertIsCausedBy(Class<? extends Throwable> clazz) {
			try {
				intercept();
			} catch (RollbackException ex) {
				if (ex.getCause() == null)
					throw new IllegalStateException("Rollback Exception has not any cause", ex);
				if (!clazz.isAssignableFrom(ex.getCause().getClass()))
					throw new IllegalStateException("Cause of rollback exception is not of type : " + clazz.getSimpleName(), ex);
				return;
			}
			assert false : "Unable to catch any rollback exception!";
		}

		public abstract void intercept();
	}

	@Deprecated
	private String printAll(Vertex vertex) {
		if (vertex == null)
			return "";
		StringBuffer print = new StringBuffer();
		print.append("\\\\ Informations on vertex");
		print.append(vertex.info());
		print.append(" //\n");
		print.append("- isAlive :");
		print.append(vertex.isAlive());
		print.append(" / meta :");
		print.append(vertex.getMeta());
		print.append(" / meta isAlive:");
		print.append(vertex.getMeta().isAlive());
		print.append("\n");

		addComponents(vertex, print);
		addDependencies(vertex, print);
		addInheritings(vertex, print);
		addInstances(vertex, print);
		addSupers(vertex, print);
		addComposites(vertex, print);
		print.append("\n");
		return print.toString();
	}

	@Deprecated
	private StringBuffer printInstances(Vertex vertex) {
		StringBuffer add = new StringBuffer();
		addInstances(vertex, add);
		return add;
	}

	@Deprecated
	private void addInstances(Vertex vertex, StringBuffer stringBuffer) {
		addLib("instances of", vertex, stringBuffer);
		addVertexList(vertex.getInstances().stream().collect(Collectors.toList()), stringBuffer);
		stringBuffer.append("\n");
	}

	@Deprecated
	private void addVertexList(List<Vertex> vertexList, StringBuffer stringBuffer) {
		for (Vertex vertexInstance : vertexList) {
			stringBuffer.append(vertexInstance.info());
			stringBuffer.append(" - alive : ");
			stringBuffer.append(vertexInstance.isAlive());
			stringBuffer.append(" / meta : ");
			stringBuffer.append(vertexInstance.getMeta().info());
			stringBuffer.append(" - meta alive : ");
			stringBuffer.append(vertexInstance.getMeta().isAlive());
			stringBuffer.append("\n");
		}
	}

	@Deprecated
	private void addLib(String lib, Vertex vertex, StringBuffer stringBuffer) {
		stringBuffer.append(lib);
		stringBuffer.append(" :-------------------");
		stringBuffer.append(vertex.info());
		stringBuffer.append("---------------------\n");
	}

	@Deprecated
	private StringBuffer printDependencies(Vertex vertex) {
		StringBuffer add = new StringBuffer();
		addDependencies(vertex, add);
		return add;
	}

	@Deprecated
	private void addDependencies(Vertex vertex, StringBuffer add) {
		addLib("dependencies of", vertex, add);
		addVertexList(vertex.computeAllDependencies().stream().collect(Collectors.toList()), add);
		add.append("\n");
	}

	@Deprecated
	private StringBuffer printInheritings(Vertex vertex) {
		StringBuffer add = new StringBuffer();
		addInheritings(vertex, add);
		return add;
	}

	@Deprecated
	private void addInheritings(Vertex vertex, StringBuffer add) {
		addLib("inheritings of", vertex, add);
		addVertexList(vertex.getInheritings().stream().collect(Collectors.toList()), add);
		add.append("\n");
	}

	@Deprecated
	private StringBuffer printSupers(Vertex vertex) {
		StringBuffer add = new StringBuffer();
		addSupers(vertex, add);
		return add;
	}

	@Deprecated
	private void addSupers(Vertex vertex, StringBuffer add) {
		addLib("supers of", vertex, add);
		addVertexList(vertex.getSupersStream().collect(Collectors.toList()), add);
		add.append("\n");
	}

	@Deprecated
	private StringBuffer printComponents(Vertex vertex) {
		StringBuffer add = new StringBuffer();
		addComponents(vertex, add);
		return add;
	}

	@Deprecated
	private void addComponents(Vertex vertex, StringBuffer add) {
		addLib("components of", vertex, add);
		addVertexList(vertex.getComponents(), add);
		add.append("\n");
	}

	@Deprecated
	private StringBuffer printComposites(Vertex vertex) {
		StringBuffer add = new StringBuffer();
		addComposites(vertex, add);
		return add;
	}

	private void addComposites(Vertex vertex, StringBuffer stringBuffer) {
		addLib("composites of", vertex, stringBuffer);
		addVertexList(vertex.getComposites().stream().collect(Collectors.toList()), stringBuffer);
		stringBuffer.append("\n");
	}

}
