package org.genericsystem.kernel.systemproperty.constraints;

import java.io.Serializable;
import java.util.Objects;
import java.util.stream.Stream;
import org.genericsystem.api.exception.ConstraintViolationException;
import org.genericsystem.api.exception.PropertyConstraintViolationException;
import org.genericsystem.kernel.AbstractVertex;
import org.genericsystem.kernel.AbstractVertex.SystemMap;
import org.genericsystem.kernel.Statics;

public class PropertyConstraint<T extends AbstractVertex<T>> implements Constraint<T> {

	@Override
	public void check(T modified, T attribute, Serializable value, int axe, boolean isOnAdd, boolean isFlushTime, boolean isRevert) throws ConstraintViolationException {
		// TODO KK
		if (attribute.getValue().equals(SystemMap.class))
			return;
		T base = modified.getBaseComponent();
		Stream<T> snapshot = base.getHolders(attribute).get().filter(x -> x.getBaseComponent().equals(base)).filter(next -> {
			for (int componentPos = Statics.TARGET_POSITION; componentPos < next.getComponents().size(); componentPos++)
				if (!Objects.equals(next.getComponents().get(componentPos), modified.getComponents().get(componentPos)))
					return false;
			return true;
		});
		if (snapshot.count() > 1)
			throw new PropertyConstraintViolationException(modified + " has more than one " + attribute);
	}

	@Override
	public boolean isCheckedAt(T modified, boolean isOnAdd) {
		return isOnAdd || (modified.getValue() == null);
	}
}
