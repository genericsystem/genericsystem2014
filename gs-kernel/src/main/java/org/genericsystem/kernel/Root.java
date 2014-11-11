package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Root extends Vertex implements DefaultRoot<Vertex> {

	protected final static Logger log = LoggerFactory.getLogger(Root.class);

	private Archiver archiver;

	public Root(Class<?>... userClasses) {
		this(Statics.ENGINE_VALUE, userClasses);
	}

	public Root(Serializable value, Class<?>... userClasses) {
		this(value, null, userClasses);
	}

	public Root(Serializable value, String persistentDirectoryPath, Class<?>... userClasses) {
		init(null, Collections.emptyList(), value, Collections.emptyList());;
		Vertex metaAttribute = setMeta(Statics.ATTRIBUTE_SIZE);
		Vertex metaRelation = setMeta(Statics.RELATION_SIZE);
		setInstance(SystemMap.class, coerceToTArray(this)).enablePropertyConstraint();
		metaAttribute.disableReferentialIntegrity(Statics.BASE_POSITION);
		if (persistentDirectoryPath != null) {
			archiver = new Archiver(this, persistentDirectoryPath);
			archiver.startScheduler();
		}
	}

	// protected T setInstance(Class<?> clazz, List<T> overrides, Serializable value, T... components) {
	// List<T> componentList = Arrays.asList(components);
	// checkSameEngine(componentList);
	// checkSameEngine(overrides);
	// T adjustedMeta = adjustMeta(value, components);
	// T equivInstance = adjustedMeta.getDirectEquivInstance(value, componentList);
	// if (equivInstance != null)
	// return equivInstance.equalsRegardlessSupers(adjustedMeta, value, componentList) && Statics.areOverridesReached(overrides, equivInstance.getSupers()) ? equivInstance : equivInstance.update(overrides, value, components);
	// return rebuildAll(() -> adjustedMeta.build(clazz, adjustedMeta, overrides, value, componentList).plug(), adjustedMeta.computePotentialDependencies(overrides, value, componentList));
	// }

	@Override
	public Root getRoot() {
		return (Root) super.getRoot();
	}

	// TODO mount this in API
	public void close() {
		if (archiver != null)
			archiver.close();
	}

	public static class MetaAttribute {}

}
