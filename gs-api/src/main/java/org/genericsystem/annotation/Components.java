package org.genericsystem.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Interface used to manage generic components.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface Components {

	/**
	 * Returns the classes of all the components of generic.
	 * 
	 * @return the classes of all the components of generic.
	 */
	Class<?>[] value();
}
