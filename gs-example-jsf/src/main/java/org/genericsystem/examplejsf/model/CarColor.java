package org.genericsystem.examplejsf.model;

import org.genericsystem.api.core.annotations.Components;
import org.genericsystem.api.core.annotations.Meta;
import org.genericsystem.api.core.annotations.SystemGeneric;
import org.genericsystem.api.core.annotations.constraints.SingularConstraint;
import org.genericsystem.api.core.annotations.value.StringValue;
import org.genericsystem.examplejsf.model.Color.White;

@SystemGeneric
@Components({ Car.class, Color.class })
@SingularConstraint
public class CarColor {

	@SystemGeneric
	@Meta(CarColor.class)
	@StringValue("DefaultCarColor")
	@Components({ Car.class, White.class })
	public static class DefaultCarColor {
	}

}
