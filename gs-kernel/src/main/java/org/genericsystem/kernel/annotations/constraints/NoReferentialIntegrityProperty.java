package org.genericsystem.kernel.annotations.constraints;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface NoReferentialIntegrityProperty {

	/**
	 * Returns the positions of the composites.
	 * 
	 * @return An array of composite position.
	 */
	int[] value();

}
