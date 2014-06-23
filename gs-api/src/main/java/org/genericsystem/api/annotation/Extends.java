package org.genericsystem.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Interface used to manage generic inheritance.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface Extends {

	/**
	 * Returns the classes of all the supers of generic.
	 * 
	 * @return the classes of all the supers of generic.
	 */
	Class<?>[] value() default {};
}
