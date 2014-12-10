package org.genericsystem.kernel.systemproperty.constraints;

import java.io.Serializable;
import java.util.stream.Collectors;

import org.genericsystem.api.core.Snapshot;
import org.genericsystem.api.exception.ConstraintViolationException;
import org.genericsystem.api.exception.PropertyConstraintViolationException;
import org.genericsystem.kernel.AbstractVertex;
import org.genericsystem.kernel.systemproperty.constraints.Constraint.CheckableConstraint;

public class PropertyConstraint<T extends AbstractVertex<T>> implements CheckableConstraint<T> {

	@Override
	public void check(T modified, T attribute, Serializable value) throws ConstraintViolationException {
		T base = modified.getBaseComponent();
		Snapshot<T> snapshot =()->base.getHolders(attribute).get().filter(x->modified.getComponents().equals(x.getComponents()) && modified.getMeta().equals(x.getMeta()));
		if (snapshot.size()>1)
			throw new PropertyConstraintViolationException("For attribute : " + attribute+" these holders violates property constraint : "+snapshot.get().map(x->x.info()).collect(Collectors.toList()));
	}

	@Override
	public boolean isCheckable(T modified, boolean isOnAdd, boolean isFlushTime, boolean isRevert) {
		return isOnAdd || (modified.getValue() == null);
	}
}
