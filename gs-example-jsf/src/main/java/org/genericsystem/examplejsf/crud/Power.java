package org.genericsystem.examplejsf.crud;

import org.genericsystem.kernel.annotations.Components;
import org.genericsystem.kernel.annotations.SystemGeneric;
import org.genericsystem.kernel.annotations.constraints.InstanceValueClassConstraint;
import org.genericsystem.kernel.annotations.constraints.PropertyConstraint;

@SystemGeneric
@Components(Car.class)
@PropertyConstraint
@InstanceValueClassConstraint(Integer.class)
public class Power {

}
