package org.genericsystem.kernel;

import org.genericsystem.kernel.annotations.Components;
import org.genericsystem.kernel.annotations.Dependencies;
import org.genericsystem.kernel.annotations.Meta;
import org.genericsystem.kernel.annotations.Supers;
import org.genericsystem.kernel.annotations.SystemGeneric;
import org.genericsystem.kernel.annotations.constraints.PropertyConstraint;
import org.genericsystem.kernel.annotations.value.AxedPropertyClassValue;
import org.genericsystem.kernel.annotations.value.BooleanValue;
import org.genericsystem.kernel.annotations.value.EngineValue;
import org.genericsystem.kernel.systemproperty.NoReferentialIntegrityProperty;

public class Config {
	@SystemGeneric
	@Meta(MetaAttribute.class)
	@Supers(Root.class)
	@Components(Root.class)
	@EngineValue
	@Dependencies({ DefaultNoReferentialIntegrityProperty.class })
	public static class MetaAttribute {
	}

	@SystemGeneric
	@Meta(MetaAttribute.class)
	@Supers(SystemMap.class)
	@Components(Root.class)
	@AxedPropertyClassValue(propertyClass = NoReferentialIntegrityProperty.class, pos = Statics.BASE_POSITION)
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
	@Components({ Root.class, Root.class })
	@EngineValue
	public static class MetaRelation {
	}

	@SystemGeneric
	@Meta(MetaAttribute.class)
	@Components(Root.class)
	@PropertyConstraint
	// TODO do this with annotated classes
	public static class SystemMap {
	}

}
