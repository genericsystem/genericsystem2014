package org.genericsystem.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.genericsystem.api.Generic;

/**
 * Interface used to declare generic instances.
 * 
 * Only basic operations can be done on instances such as reading it, updating the value or removing the instance. A generic instance cannot instantiate another generic or be used as an attribute or relation.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface InstanciatedGeneric {

	/**
	 * Defines the class of the instance. Should extends Generic.
	 * 
	 * @return the class of the instance.
	 */
	Class<? extends Generic> value();
}