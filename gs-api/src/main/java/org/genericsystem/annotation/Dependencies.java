package org.genericsystem.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Interface used to manage generic dependency.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
@Inherited
public @interface Dependencies {

	/**
	 * Returns the classes of all the dependencies of generic.
	 * 
	 * @return the classes of all the dependencies of generic.
	 */
	Class<?>[] value();
}