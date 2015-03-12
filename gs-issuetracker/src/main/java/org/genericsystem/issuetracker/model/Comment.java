package org.genericsystem.issuetracker.model;

import org.genericsystem.kernel.annotations.InstanceClass;
import org.genericsystem.kernel.annotations.SystemGeneric;
import org.genericsystem.kernel.annotations.constraints.InstanceValueClassConstraint;
import org.genericsystem.mutability.Generic;

@SystemGeneric
@InstanceValueClassConstraint(String.class)
@InstanceClass(Comment.class)
public class Comment implements Generic {

}
