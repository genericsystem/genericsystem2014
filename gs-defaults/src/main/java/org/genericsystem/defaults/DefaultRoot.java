package org.genericsystem.defaults;

import org.genericsystem.api.core.IRoot;
import org.genericsystem.defaults.DefaultConfig.MetaAttribute;
import org.genericsystem.defaults.DefaultConfig.MetaRelation;
import org.genericsystem.defaults.DefaultConfig.Sequence;
import org.genericsystem.defaults.DefaultConfig.SystemMap;

public interface DefaultRoot<T extends DefaultVertex<T>> extends IRoot<T> {

	@Override
	DefaultContext<T> getCurrentCache();

	public DefaultContext<T> newCache();

	@Override
	default T getMetaAttribute() {
		return find(MetaAttribute.class);
	}

	@Override
	default T getMetaRelation() {
		return find(MetaRelation.class);
	}

	@Override
	default T getMap() {
		return find(SystemMap.class);
	}

	default T getSequence() {
		return find(Sequence.class);

	}

}
