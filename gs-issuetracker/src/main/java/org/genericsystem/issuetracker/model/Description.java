package org.genericsystem.issuetracker.model;

import org.genericsystem.kernel.annotations.Components;
import org.genericsystem.kernel.annotations.SystemGeneric;
import org.genericsystem.kernel.annotations.constraints.InstanceValueClassConstraint;
import org.genericsystem.kernel.annotations.constraints.PropertyConstraint;
import org.genericsystem.mutability.Generic;

@SystemGeneric
@Components(Issue.class)
@PropertyConstraint
@InstanceValueClassConstraint(String.class)
public class Description implements Generic {

}
