package org.genericsystem.kernel.systemproperty.constraints;

import org.genericsystem.api.exception.ConstraintViolationException;
import org.genericsystem.api.exception.UniqueValueConstraintViolationException;
import org.genericsystem.kernel.AbstractVertex;

public class UniqueValueConstraint<T extends AbstractVertex<T>> implements Constraint<T> {

	@Override
	public void check(T modified, T attribute) throws ConstraintViolationException {

		System.out.println("meta: " + modified.getMeta());
		for (T v : modified.getMeta().getInstances())
			if (v.getValue() == modified.getValue() && !v.equals(modified))
				throw new UniqueValueConstraintViolationException("Duplicate value : " + v.getValue());

		// Set<Serializable> values = new HashSet<>();
		// for (T vertex : modified.getAllInstances()) {
		// if (vertex.getValue() != null && !values.add(vertex.getValue()))
		// throw new UniqueValueConstraintViolationException("Duplicate value : " + vertex.getValue());
		// }
	}

}
