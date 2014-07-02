package org.genericsystem.security.structure;

import org.genericsystem.core.GenericImpl;
import org.genericsystem.kernel.annotation.Meta;
import org.genericsystem.kernel.annotation.SystemGeneric;
import org.genericsystem.kernel.annotations.value.StringValue;
import org.genericsystem.security.structure.Types.Roles;

public class Instances extends GenericImpl {

	@SystemGeneric
	@Meta(Roles.class)
	@StringValue("Admin")
	public static class RoleAdmin extends GenericImpl {

	}

}
