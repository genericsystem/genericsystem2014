package org.genericsystem.issuetracker.model;

import org.genericsystem.api.core.ApiStatics;
import org.genericsystem.kernel.annotations.Components;
import org.genericsystem.kernel.annotations.InstanceClass;
import org.genericsystem.kernel.annotations.SystemGeneric;
import org.genericsystem.kernel.annotations.constraints.SingularConstraint;
import org.genericsystem.mutability.Generic;

@SystemGeneric
@Components({ Issue.class, Comment.class })
@SingularConstraint(ApiStatics.TARGET_POSITION)
@InstanceClass(IssueComment.class)
public class IssueComment implements Generic {

}
