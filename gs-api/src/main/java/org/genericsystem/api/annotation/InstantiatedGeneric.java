package org.genericsystem.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.genericsystem.api.Generic;

/**
 * <p>
 * Interface used to declare generic instances.
 * </p>
 * 
 * <p>
 * Only basic operations can be done on instances such as reading it, updating the value or removing the instance. A generic instance cannot instantiate another generic or be used as an attribute or relation.
 * </p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface InstantiatedGeneric {

	/**
	 * Defines the class of the element. Should extends Generic.
	 * 
	 * @return the class of the element.
	 */
	Class<? extends Generic> value();
}