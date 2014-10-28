package org.genericsystem.cache.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The composites of a generic.
 * 
 * @author Nicolas Feybesse
 * @author Michael Ory
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface Components {

	/**
	 * Returns the class of the composites.
	 * 
	 * @return An array of class of the composites.
	 */
	Class<?>[] value();
}
