package org.genericsystem.examplejsf.crud;

import org.genericsystem.examplejsf.crud.Color.White;
import org.genericsystem.kernel.annotations.Components;
import org.genericsystem.kernel.annotations.Meta;
import org.genericsystem.kernel.annotations.SystemGeneric;
import org.genericsystem.kernel.annotations.constraints.SingularConstraint;
import org.genericsystem.kernel.annotations.value.StringValue;

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
