package org.genericsystem.issuetracker.model;

import org.genericsystem.issuetracker.model.Statut.Open;
import org.genericsystem.kernel.annotations.Components;
import org.genericsystem.kernel.annotations.Meta;
import org.genericsystem.kernel.annotations.SystemGeneric;
import org.genericsystem.kernel.annotations.constraints.RequiredConstraint;
import org.genericsystem.kernel.annotations.constraints.SingularConstraint;
import org.genericsystem.kernel.annotations.value.StringValue;
import org.genericsystem.mutability.Generic;

@SystemGeneric
@Components({ Issue.class, Statut.class })
@SingularConstraint
@RequiredConstraint
public class IssueStatut implements Generic {

	@SystemGeneric
	@Meta(IssueStatut.class)
	@StringValue("defaultIssueStatut")
	@Components({ Issue.class, Open.class })
	public static class DefaultIssueStatut {
	}

}
