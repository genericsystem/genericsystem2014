package org.genericsystem.examplejsf.crud;

import org.genericsystem.kernel.annotations.Meta;
import org.genericsystem.kernel.annotations.SystemGeneric;
import org.genericsystem.kernel.annotations.value.StringValue;

@SystemGeneric
public class Color {

	@SystemGeneric
	@Meta(Color.class)
	@StringValue("White")
	public static class White {
	}

	@SystemGeneric
	@Meta(Color.class)
	@StringValue("Red")
	public static class Red {
	}

	@SystemGeneric
	@Meta(Color.class)
	@StringValue("Blue")
	public static class Blue {
	}

	@SystemGeneric
	@Meta(Color.class)
	@StringValue("Yellow")
	public static class Yellow {
	}

	@SystemGeneric
	@Meta(Color.class)
	@StringValue("Pink")
	public static class Pink {
	}

	@SystemGeneric
	@Meta(Color.class)
	@StringValue("Purple")
	public static class Purple {
	}

	@SystemGeneric
	@Meta(Color.class)
	@StringValue("Green")
	public static class Green {
	}

	@SystemGeneric
	@Meta(Color.class)
	@StringValue("Grey")
	public static class Grey {
	}

	@SystemGeneric
	@Meta(Color.class)
	@StringValue("Black")
	public static class Black {
	}
}
