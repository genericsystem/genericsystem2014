package org.genericsystem.defaults;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.genericsystem.defaults.Generator.IntAutoIncrementGenerator.StringAutoIncrementGenerator;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface GenerateValue {

	Class<? extends Generator> clazz() default StringAutoIncrementGenerator.class;

}
