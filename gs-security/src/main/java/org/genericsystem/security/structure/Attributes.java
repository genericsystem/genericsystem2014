package org.genericsystem.security.structure;

import org.genericsystem.core.GenericImpl;
import org.genericsystem.kernel.annotation.Components;
import org.genericsystem.kernel.annotation.SystemGeneric;
import org.genericsystem.kernel.annotations.constraints.UniqueValueConstraint;
import org.genericsystem.security.structure.Types.Users;

public class Attributes {

	@SystemGeneric
	@Components(Users.class)
	@UniqueValueConstraint
	public static class Login extends GenericImpl {

	}

	@SystemGeneric
	@Components(Users.class)
	public static class Password extends GenericImpl {

	}

}
