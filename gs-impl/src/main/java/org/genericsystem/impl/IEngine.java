package org.genericsystem.impl;

import org.genericsystem.api.exception.RollbackException;
import org.genericsystem.impl.constraints.AbstractConstraintImpl.CheckingType;
import org.genericsystem.kernel.IRoot;

public interface IEngine<T extends AbstractGeneric<T, U, ?, ?>, U extends IEngine<T, U>> extends IRoot<T, U>, IGeneric<T, U> {

	<subT extends T> subT find(Class<subT> clazz);

	default void check(CheckingType checkingType, boolean isFlushTime, T t) throws RollbackException {
		check(t);
		checkConsistency(checkingType, isFlushTime, t);
		checkConstraints(checkingType, isFlushTime, t);
	}

	default void checkConsistency(CheckingType checkingType, boolean isFlushTime, T t) {
	}

	default void checkConstraints(CheckingType checkingType, boolean isFlushTime, T t) {
		// PriorityConstraintMap<T, U> constraints = new PriorityConstraintMap<>();
		// for (final T attribute : t.getAttributes()) {
		// AbstractExtendedMap<AxedPropertyClass, Serializable> constraintMap = ((GenericImpl) attribute).getConstraintsMap();
		// for (AxedPropertyClass key : constraintMap.keySet()) {
		// Holder valueHolder = constraintMap.getValueHolder(key);
		// GenericImpl keyHolder = valueHolder.getBaseComponent();
		// AbstractConstraintImpl constraint = keyHolder.getMeta();
		// AxedPropertyClass axedPropertyClass = keyHolder.getValue();
		// if (isCheckable(constraint, attribute, checkingType, isFlushTime) && (constraint instanceof AbstractAxedConstraintImpl || constraint instanceof PropertyConstraintImpl) && isInstanceOf(generic, attribute, axedPropertyClass.getAxe()))
		// constraints.put(constraint, valueHolder);
		// }
		// }
		// AbstractExtendedMap<AxedPropertyClass, Serializable> constraintMap = ((GenericImpl) generic).getConstraintsMap();
		// for (AxedPropertyClass key : constraintMap.keySet()) {
		// Holder valueHolder = constraintMap.getValueHolder(key);
		// GenericImpl keyHolder = valueHolder.getBaseComponent();
		// AbstractConstraintImpl constraint = keyHolder.getMeta();
		// if (isCheckable(constraint, generic, checkingType, isFlushTime) && generic.getMetaLevel() - ((Holder) keyHolder.getBaseComponent()).getBaseComponent().getMetaLevel() >= 1)
		// constraints.put(constraint, valueHolder);
		// }
		// for (Entry<AbstractConstraintImpl, Holder> entry : constraints.entrySet())
		// entry.getKey().check(generic, entry.getValue());
	}
	//
	// static class PriorityConstraintMap<T extends AbstractGeneric<T, U, ?, ?>, U extends IEngine<T, U>> extends TreeMap<AbstractConstraintImpl<T, U, ?, ?>, T> {
	//
	// private static final long serialVersionUID = -1661589109737403438L;
	//
	// @Override
	// public Comparator<? super AbstractConstraintImpl<T, U, ?, ?>> comparator() {
	// return new Comparator<AbstractConstraintImpl<T, U, ?, ?>>() {
	// @Override
	// public int compare(AbstractConstraintImpl<T, U, ?, ?> constraint, AbstractConstraintImpl<T, U, ?, ?> compareConstraint) {
	// if (constraint.getPriority() == compareConstraint.getPriority())
	// return constraint.getClass().getSimpleName().compareTo(compareConstraint.getClass().getSimpleName());
	// return Integer.compare(constraint.getPriority(), compareConstraint.getPriority());
	// }
	// };
	// }
	//
	// }

}
