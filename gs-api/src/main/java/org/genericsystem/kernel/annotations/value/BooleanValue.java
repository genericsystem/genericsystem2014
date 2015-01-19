package org.genericsystem.kernel.annotations.value;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates the value is a <code>boolean</code> value.
 * 
 * @author Nicolas Feybesse
 * @author Michael Ory
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface BooleanValue {
	/**
	 * The <code>boolean</code> value.
	 * 
	 * @return the <code>boolean</code> value.
	 */
	boolean value();
}
