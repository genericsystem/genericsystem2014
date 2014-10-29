package org.genericsystem.kernel.systemproperty.constraints;

import org.genericsystem.api.exception.ConstraintViolationException;
import org.genericsystem.kernel.AbstractVertex;

public class UniqueValueConstraint<T extends AbstractVertex<T>> implements Constraint<T> {

	@Override
	public void check(T modified, T attribute) throws ConstraintViolationException {
		// Set<Serializable> values = new HashSet<>();
		// for (T vertex : modified.getAllInstances()) {
		// if (vertex.getValue() != null && !values.add(vertex.getValue()))
		// throw new UniqueValueConstraintViolationException("Duplicate value : " + vertex.getValue());
		// }
	}

	// @Override
	// public <T extends AbstractVertex<T>> boolean isCheckedAt(DefaultVertex<T> modified, CheckingType checkingType) {
	// // TODO Auto-generated method stub
	// // appelé quand on créé un nouveau vertex
	// return false;
	// }

}
