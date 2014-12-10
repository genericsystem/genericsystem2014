package org.genericsystem.concurrency;

import org.genericsystem.concurrency.Config.DefaultNoReferentialIntegrityProperty.DefaultValue;
import org.genericsystem.kernel.Statics;
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
	@Supers(Engine.class)
	@Components(Engine.class)
	@EngineValue
	@Dependencies({ DefaultNoReferentialIntegrityProperty.class })
	public static class MetaAttribute extends Generic {

	}

	@SystemGeneric
	@Meta(MetaAttribute.class)
	@Supers(SystemMap.class)
	@Components(Engine.class)
	@AxedPropertyClassValue(propertyClass = NoReferentialIntegrityProperty.class, pos = Statics.BASE_POSITION)
	@Dependencies({ DefaultValue.class })
	public static class DefaultNoReferentialIntegrityProperty extends Generic {

		@SystemGeneric
		@Meta(DefaultNoReferentialIntegrityProperty.class)
		@Components(MetaAttribute.class)
		@BooleanValue(true)
		public static class DefaultValue extends Generic {

		}

	}

	@SystemGeneric
	@Meta(MetaRelation.class)
	@Supers(MetaAttribute.class)
	@Components({ Engine.class, Engine.class })
	@EngineValue
	public static class MetaRelation extends Generic {

	}

	@SystemGeneric
	@Meta(MetaAttribute.class)
	@Components(Engine.class)
	@PropertyConstraint
	public static class SystemMap extends Generic {}

}
