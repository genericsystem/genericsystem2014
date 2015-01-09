package org.genericsystem.issuetracker.crud;

import org.genericsystem.issuetracker.crud.Issue.Descriptif;
import org.genericsystem.issuetracker.crud.Issue.Priority;
import org.genericsystem.issuetracker.crud.Issue.Type;
import org.genericsystem.kernel.annotations.Components;
import org.genericsystem.kernel.annotations.Dependencies;
import org.genericsystem.kernel.annotations.SystemGeneric;

@SystemGeneric
@Dependencies({ Descriptif.class, Priority.class, Type.class })
public class Issue {

	@SystemGeneric
	@Components(Issue.class)
	public static class Descriptif {

	}

	@SystemGeneric
	@Components(Issue.class)
	public static class Priority {

	}

	@SystemGeneric
	@Components(Issue.class)
	public static class Type {

	}

}
