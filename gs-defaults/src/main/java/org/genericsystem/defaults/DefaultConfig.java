package org.genericsystem.defaults;

import org.genericsystem.api.core.ApiStatics;
import org.genericsystem.api.core.annotations.Components;
import org.genericsystem.api.core.annotations.Dependencies;
import org.genericsystem.api.core.annotations.Meta;
import org.genericsystem.api.core.annotations.Supers;
import org.genericsystem.api.core.annotations.SystemGeneric;
import org.genericsystem.api.core.annotations.constraints.InstanceValueClassConstraint;
import org.genericsystem.api.core.annotations.constraints.PropertyConstraint;
import org.genericsystem.api.core.annotations.value.AxedPropertyClassValue;
import org.genericsystem.api.core.annotations.value.BooleanValue;
import org.genericsystem.api.core.annotations.value.EngineValue;
import org.genericsystem.api.core.systemproperty.NoReferentialIntegrityProperty;

public class DefaultConfig {
	@SystemGeneric
	@Meta(MetaAttribute.class)
	@Supers(DefaultRoot.class)
	@Components(DefaultRoot.class)
	@EngineValue
	@Dependencies({ DefaultNoReferentialIntegrityProperty.class })
	public static class MetaAttribute {
	}

	@SystemGeneric
	@Meta(MetaAttribute.class)
	@Supers(SystemMap.class)
	@Components(DefaultRoot.class)
	@AxedPropertyClassValue(propertyClass = NoReferentialIntegrityProperty.class, pos = ApiStatics.BASE_POSITION)
	@Dependencies({ DefaultValue.class })
	public static class DefaultNoReferentialIntegrityProperty {
	}

	@SystemGeneric
	@Meta(DefaultNoReferentialIntegrityProperty.class)
	@Components(MetaAttribute.class)
	@BooleanValue(true)
	public static class DefaultValue {
	}

	@SystemGeneric
	@Meta(MetaRelation.class)
	@Supers(MetaAttribute.class)
	@Components({ DefaultRoot.class, DefaultRoot.class })
	@EngineValue
	public static class MetaRelation {
	}

	@SystemGeneric
	@Meta(MetaAttribute.class)
	@Components(DefaultRoot.class)
	@PropertyConstraint
	public static class SystemMap {

	}

	@SystemGeneric
	@Components(DefaultRoot.class)
	@InstanceValueClassConstraint(Integer.class)
	@PropertyConstraint
	public static class Sequence {

	}

}
