package org.genericsystem.kernel.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.genericsystem.api.defaults.Generator;
import org.genericsystem.api.defaults.Generator.IntAutoIncrementGenerator.StringAutoIncrementGenerator;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Generate {

	Class<? extends Generator> clazz() default StringAutoIncrementGenerator.class;

}
