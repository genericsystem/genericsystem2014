package org.genericsystem.issuetracker.model;

import org.genericsystem.kernel.annotations.Components;
import org.genericsystem.kernel.annotations.SystemGeneric;
import org.genericsystem.mutability.Generic;

@SystemGeneric
@Components({ Issue.class, Version.class })
public class IssueVersion implements Generic {

}
