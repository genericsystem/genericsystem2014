package org.genericsystem.api.defaults.constraints;

import java.io.Serializable;
import java.util.stream.Collectors;

import org.genericsystem.api.core.Snapshot;
import org.genericsystem.api.defaults.DefaultVertex;
import org.genericsystem.api.defaults.constraints.Constraint.CheckableConstraint;
import org.genericsystem.api.defaults.exceptions.PropertyConstraintViolationException;
import org.genericsystem.api.exception.ConstraintViolationException;

/**
 * Represents the constraint to allow only one value for an attribute.
 * 
 * @author Nicolas Feybesse
 *
 * @param <T>
 *            the implementation of DefaultVertex.
 */
public class PropertyConstraint<T extends DefaultVertex<T>> implements CheckableConstraint<T> {
	@Override
	public void check(T modified, T attribute, Serializable value) throws ConstraintViolationException {
		T base = modified.getBaseComponent();
		Snapshot<T> snapshot = () -> base.getHolders(attribute).get().filter(x -> modified.getComponents().equals(x.getComponents()) && modified.getMeta().equals(x.getMeta()));
		if (snapshot.size() > 1)
			throw new PropertyConstraintViolationException("For attribute : " + attribute + " these holders violates property constraint : " + snapshot.get().map(x -> x.info()).collect(Collectors.toList()));
	}

	@Override
	public boolean isCheckable(T modified, boolean isOnAdd, boolean isFlushTime, boolean isRevert) {
		return isOnAdd || (modified.getValue() == null);
	}
}
