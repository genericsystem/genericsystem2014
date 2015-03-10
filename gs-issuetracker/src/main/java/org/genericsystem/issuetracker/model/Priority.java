package org.genericsystem.issuetracker.model;

import org.genericsystem.kernel.annotations.Meta;
import org.genericsystem.kernel.annotations.SystemGeneric;
import org.genericsystem.kernel.annotations.constraints.InstanceValueClassConstraint;
import org.genericsystem.kernel.annotations.value.StringValue;

@SystemGeneric
@InstanceValueClassConstraint(String.class)
public class Priority {

	@SystemGeneric
	@Meta(Priority.class)
	@StringValue("Blocker")
	public static class Blocker {
	}

	@SystemGeneric
	@Meta(Priority.class)
	@StringValue("Critical")
	public static class Critical {
	}

	@SystemGeneric
	@Meta(Priority.class)
	@StringValue("Major")
	public static class Major {
	}

	@SystemGeneric
	@Meta(Priority.class)
	@StringValue("Minor")
	public static class Minor {
	}

	@SystemGeneric
	@Meta(Priority.class)
	@StringValue("Optional")
	public static class Optional {
	}

	@SystemGeneric
	@Meta(Priority.class)
	@StringValue("Trivial")
	public static class Trivial {
	}
}
