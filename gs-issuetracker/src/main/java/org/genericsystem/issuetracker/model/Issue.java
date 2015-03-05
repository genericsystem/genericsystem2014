package org.genericsystem.issuetracker.model;

import org.genericsystem.kernel.annotations.SystemGeneric;
import org.genericsystem.kernel.annotations.constraints.UniqueValueConstraint;
import org.genericsystem.mutability.Generic;

@SystemGeneric
@UniqueValueConstraint
public class Issue implements Generic {

}
