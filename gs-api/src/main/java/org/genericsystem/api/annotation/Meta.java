package org.genericsystem.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.genericsystem.api.Engine;

/**
 * <p>
 * Interface used to declare the meta of generic(s).
 * </p>
 * <p>
 * A meta instance is associated with an information context and is considered the root of it. The meta is <tt>Engine</tt> by default.
 * </p>
 * 
 * @see Engine
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface Meta {

	/**
	 * The meta class, Engine by default.
	 * 
	 * @return the meta class, Engine by default.
	 * 
	 * @see Engine
	 */
	Class<?> value() default Engine.class;
}