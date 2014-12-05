package org.genericsystem.kernel;

import org.genericsystem.kernel.KernelConfig.DefaultNoReferentialIntegrityProperty.DefaultValue;
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

public class KernelConfig {
	@SystemGeneric
	@Meta(MetaAttribute.class)
	@Supers(Root.class)
	@Components(Root.class)
	@EngineValue
	@Dependencies({ DefaultNoReferentialIntegrityProperty.class })
	public static class MetaAttribute extends Vertex {

	}

	@SystemGeneric
	@Meta(MetaAttribute.class)
	@Supers(SystemMap.class)
	@Components(Root.class)
	@AxedPropertyClassValue(propertyClass = NoReferentialIntegrityProperty.class, pos = Statics.BASE_POSITION)
	@Dependencies({ DefaultValue.class })
	public static class DefaultNoReferentialIntegrityProperty extends Vertex {

		@SystemGeneric
		@Meta(DefaultNoReferentialIntegrityProperty.class)
		@Components(MetaAttribute.class)
		@BooleanValue(true)
		public static class DefaultValue extends Vertex {

		}

	}

	@SystemGeneric
	@Meta(MetaRelation.class)
	@Supers(Root.class)
	@Components({ Root.class, Root.class })
	@EngineValue
	public static class MetaRelation extends Vertex {

	}

	@SystemGeneric
	@Meta(MetaAttribute.class)
	@Components(Root.class)
	@PropertyConstraint
	public static class SystemMap extends Vertex {}

}
