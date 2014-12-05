package org.genericsystem.kernel.annotations.value;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.genericsystem.api.core.IVertex.SystemProperty;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface AxedPropertyClassValue {

	Class<? extends SystemProperty> propertyClass();

	int pos();
}
