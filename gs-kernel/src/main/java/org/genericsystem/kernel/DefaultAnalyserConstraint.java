package org.genericsystem.kernel;

import org.genericsystem.api.core.ApiStatics;
import org.genericsystem.api.defaults.DefaultVertex;
import org.genericsystem.kernel.annotations.constraints.InstanceValueClassConstraint;
import org.genericsystem.kernel.annotations.constraints.NoReferentialIntegrityProperty;
import org.genericsystem.kernel.annotations.constraints.PropertyConstraint;
import org.genericsystem.kernel.annotations.constraints.RequiredConstraint;
import org.genericsystem.kernel.annotations.constraints.SingularConstraint;
import org.genericsystem.kernel.annotations.constraints.UniqueValueConstraint;

public interface DefaultAnalyserConstraint<T extends DefaultVertex<T>> {

	default void mountConstraints(Class<?> clazz, T result) {
		if (clazz.getAnnotation(PropertyConstraint.class) != null)
			result.enablePropertyConstraint();

		if (clazz.getAnnotation(UniqueValueConstraint.class) != null)
			result.enableUniqueValueConstraint();

		if (clazz.getAnnotation(InstanceValueClassConstraint.class) != null)
			result.setClassConstraint(clazz.getAnnotation(InstanceValueClassConstraint.class).value());

		if (clazz.getAnnotation(RequiredConstraint.class) != null)
			result.enableRequiredConstraint(ApiStatics.NO_POSITION);

		NoReferentialIntegrityProperty referentialIntegrity = clazz.getAnnotation(NoReferentialIntegrityProperty.class);
		if (referentialIntegrity != null)
			for (int axe : referentialIntegrity.value())
				result.disableReferentialIntegrity(axe);

		SingularConstraint singularTarget = clazz.getAnnotation(SingularConstraint.class);
		if (singularTarget != null)
			for (int axe : singularTarget.value())
				result.enableSingularConstraint(axe);
	}

}
