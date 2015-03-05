package org.genericsystem.issuetracker.model;

import org.genericsystem.kernel.annotations.Components;
import org.genericsystem.kernel.annotations.SystemGeneric;
import org.genericsystem.kernel.annotations.constraints.RequiredConstraint;
import org.genericsystem.kernel.annotations.constraints.SingularConstraint;
import org.genericsystem.mutability.Generic;

@SystemGeneric
@Components({ Issue.class, Priority.class })
@SingularConstraint
@RequiredConstraint
public class IssuePriority implements Generic {

}
