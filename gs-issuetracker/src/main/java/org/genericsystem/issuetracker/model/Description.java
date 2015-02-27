package org.genericsystem.issuetracker.model;

import org.genericsystem.kernel.annotations.Components;
import org.genericsystem.kernel.annotations.SystemGeneric;
import org.genericsystem.kernel.annotations.constraints.InstanceValueClassConstraint;
import org.genericsystem.kernel.annotations.constraints.PropertyConstraint;

@SystemGeneric
@Components(Issue.class)
@PropertyConstraint
@InstanceValueClassConstraint(String.class)
public class Description {

}
