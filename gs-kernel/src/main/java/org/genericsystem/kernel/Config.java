package org.genericsystem.kernel;

import org.genericsystem.api.core.ApiStatics;
import org.genericsystem.kernel.annotations.Components;
import org.genericsystem.kernel.annotations.Dependencies;
import org.genericsystem.kernel.annotations.Meta;
import org.genericsystem.kernel.annotations.Supers;
import org.genericsystem.kernel.annotations.SystemGeneric;
import org.genericsystem.kernel.annotations.value.AxedPropertyClassValue;
import org.genericsystem.kernel.annotations.value.BooleanValue;
import org.genericsystem.kernel.annotations.value.EngineValue;
import org.genericsystem.kernel.systemproperty.NoReferentialIntegrityProperty;
import org.genericsystem.kernel.systemproperty.constraints.PropertyConstraint;

public class Config {
	@SystemGeneric
	@Meta(MetaAttribute.class)
	@Supers(Root.class)
	@Components(Root.class)
	@EngineValue
	@Dependencies({ DefaultNoReferentialIntegrityProperty.class })
	public static class MetaAttribute {}

	@SystemGeneric
	@Meta(MetaAttribute.class)
	@Supers(SystemMap.class)
	@Components(Root.class)
	@AxedPropertyClassValue(propertyClass = NoReferentialIntegrityProperty.class, pos = ApiStatics.BASE_POSITION)
	@Dependencies({ DefaultValue.class })
	public static class DefaultNoReferentialIntegrityProperty {}

	@SystemGeneric
	@Meta(DefaultNoReferentialIntegrityProperty.class)
	@Components(MetaAttribute.class)
	@BooleanValue(true)
	public static class DefaultValue {}

	@SystemGeneric
	@Meta(MetaRelation.class)
	@Supers(MetaAttribute.class)
	@Components({ Root.class, Root.class })
	@EngineValue
	public static class MetaRelation {}

	@SystemGeneric
	@Meta(MetaAttribute.class)
	@Components(Root.class)
	@Dependencies({ DefaultPropertyConstraint.class })
	public static class SystemMap {

	}

	@SystemGeneric
	@Meta(MetaAttribute.class)
	@Supers(SystemMap.class)
	@Components(Root.class)
	@AxedPropertyClassValue(propertyClass = PropertyConstraint.class, pos = ApiStatics.NO_POSITION)
	@Dependencies({ DefaultPropertyConstraintValue.class })
	public static class DefaultPropertyConstraint {}

	@SystemGeneric
	@Meta(DefaultPropertyConstraint.class)
	@Components(SystemMap.class)
	@BooleanValue(true)
	public static class DefaultPropertyConstraintValue {}

}
