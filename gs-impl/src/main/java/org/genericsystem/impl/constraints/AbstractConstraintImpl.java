package org.genericsystem.impl.constraints;

import org.genericsystem.impl.AbstractGeneric;
import org.genericsystem.impl.IEngine;
import org.genericsystem.impl.IGeneric;
import org.genericsystem.kernel.AbstractVertex;
import org.genericsystem.kernel.IRoot;

public abstract class AbstractConstraintImpl<T extends AbstractGeneric<T, U, V, W>, U extends IEngine<T, U>, V extends AbstractVertex<V, W>, W extends IRoot<V, W>> implements IGeneric<T, U> {

	public enum CheckingType {
		CHECK_ON_ADD_NODE, CHECK_ON_REMOVE_NODE
	}

	// public final int getPriority() {
	// Priority annotation = getClass().getAnnotation(Priority.class);
	// return annotation != null ? annotation.value() : 0;
	// }
	//
	// public boolean isCheckedAt(T modified, CheckingType checkingType) {
	// return checkingType.equals(CheckingType.CHECK_ON_ADD_NODE);
	// }
	//
	// public boolean isImmediatelyCheckable() {
	// return true;
	// }
	//
	// public boolean isImmediatelyConsistencyCheckable() {
	// return true;
	// }
	//
	// protected T getConstraintBase(T constraintValue) {
	// return constraintValue.<Holder> getBaseComponent().<Holder> getBaseComponent().getBaseComponent();
	// }
	//
	// protected int getAxe(T constraintValue) {
	// return constraintValue.<Holder> getBaseComponent().<AxedPropertyClass> getValue().getAxe();
	// }
	//
	// public abstract void check(T modified, T constraintValue) throws ConstraintViolationException;
	//
	// public void checkConsistency(T constraintValue) throws ConstraintViolationException {
	// check(constraintValue, constraintValue);
	// }
	//
	// public abstract static class AbstractAxedConstraintImpl<T extends AbstractGeneric<T, U, V, W>, U extends IEngine<T, U>, V extends AbstractVertex<V, W>, W extends IRoot<V, W>> extends AbstractConstraintImpl<T, U, V, W> {
	//
	// @Override
	// public void check(T modified, T constraintValue) throws ConstraintViolationException {
	// T component = modified.getComponent(getAxe(constraintValue));
	// internalCheck(component != null ? component : modified, constraintValue);
	// }
	//
	// @Override
	// public void checkConsistency(T constraintValue) throws ConstraintViolationException {
	// T constraintBase = getConstraintBase(constraintValue);
	// T component = constraintBase.getComponent(getAxe(constraintValue));
	// if (component != null)
	// for (T instance : component.getAllInstances())
	// internalCheck(instance, constraintValue);
	// }
	//
	// public abstract void internalCheck(T modified, T constraintValue) throws ConstraintViolationException;
	// }
	//
	// public abstract static class AbstractBooleanAxedConstraintImpl<T extends AbstractGeneric<T, U, V, W>, U extends IEngine<T, U>, V extends AbstractVertex<V, W>, W extends IRoot<V, W>> extends AbstractAxedConstraintImpl<T, U, V, W> {
	// @Override
	// public void internalCheck(T modified, T constraintValue) throws ConstraintViolationException {
	// if (!Boolean.FALSE.equals(constraintValue.getValue()))
	// check(getConstraintBase(constraintValue), modified);
	// }
	//
	// @Override
	// public abstract void check(T constraintBase, T modified) throws ConstraintViolationException;
	// }
	//
	// public abstract static class AbstractBooleanNoAxedConstraintImpl<T extends AbstractGeneric<T, U, V, W>, U extends IEngine<T, U>, V extends AbstractVertex<V, W>, W extends IRoot<V, W>> extends AbstractConstraintImpl<T, U, V, W> {
	// @Override
	// public void check(T modified, T constraintValue) throws ConstraintViolationException {
	// if (!Boolean.FALSE.equals(constraintValue.getValue()))
	// check(getConstraintBase(constraintValue), modified);
	// }
	//
	// public abstract void check(T constraintBase, T modified) throws ConstraintViolationException;
	// }

}