package org.genericsystem.cdi.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The class of the generic instances.
 *
 * @author Nicolas Feybesse
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface InstanceGenericClass {

	/**
	 * Define the class of the instance.
	 *
	 * @return the class of the components.
	 */
	Class<? extends org.genericsystem.cache.Generic> value();
}