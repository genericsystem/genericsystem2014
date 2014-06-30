package org.genericsystem.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * Interface used to resolve multiple constraints by ordering their priority. An element with a priority gives its priority to its children.
 * </p>
 * <p>
 * It is highly recommended (even if not mandatory) to specify a priority when using multiple constraints.
 * </p>
 */
// FIXME : Throws when value < 0
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
@Inherited
public @interface Priority {

	/**
	 * Returns the value of the priority, 0 is the highest priority then it is 1 and so on.
	 * 
	 * @return the value of the priority, 0 is the highest priority then it is 1 and so on
	 */
	int value();
}
