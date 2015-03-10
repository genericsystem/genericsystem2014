package org.genericsystem.issuetracker.model;

import org.genericsystem.kernel.annotations.Meta;
import org.genericsystem.kernel.annotations.SystemGeneric;
import org.genericsystem.kernel.annotations.constraints.InstanceValueClassConstraint;
import org.genericsystem.kernel.annotations.value.StringValue;

@SystemGeneric
@InstanceValueClassConstraint(String.class)
public class Statut {

	@SystemGeneric
	@Meta(Statut.class)
	@StringValue("Open")
	public static class Open {
	}

	@SystemGeneric
	@Meta(Statut.class)
	@StringValue("Coding in progress")
	public static class CodingInProgress {
	}

	@SystemGeneric
	@Meta(Statut.class)
	@StringValue("Reopened")
	public static class Reopened {
	}

	@SystemGeneric
	@Meta(Statut.class)
	@StringValue("Resolved")
	public static class Resolved {
	}

	@SystemGeneric
	@Meta(Statut.class)
	@StringValue("Closed")
	public static class Closed {
	}

}
