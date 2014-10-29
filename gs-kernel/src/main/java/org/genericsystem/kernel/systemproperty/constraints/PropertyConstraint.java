package org.genericsystem.kernel.systemproperty.constraints;

import java.util.Objects;
import java.util.stream.Stream;
import org.genericsystem.api.exception.ConstraintViolationException;
import org.genericsystem.api.exception.PropertyConstraintViolationException;
import org.genericsystem.kernel.AbstractVertex;
import org.genericsystem.kernel.AbstractVertex.SystemMap;
import org.genericsystem.kernel.Statics;

public class PropertyConstraint<T extends AbstractVertex<T>> implements Constraint<T> {

	@Override
	public void check(T modified, T attribute) throws ConstraintViolationException {
		// TODO KK
		if (attribute.getValue().equals(SystemMap.class))
			return;
		T base = modified.getComponents().get(Statics.BASE_POSITION);
		Stream<T> snapshot = base.getHolders(attribute).get().filter(x -> x.getComponents().get(Statics.BASE_POSITION).equals(base)).filter(next -> {
			for (int componentPos = Statics.TARGET_POSITION; componentPos < next.getComponents().size(); componentPos++)
				if (!Objects.equals(next.getComponents().get(componentPos), modified.getComponents().get(componentPos)))
					return false;
			return true;
		});
		if (snapshot.count() > 1)
			throw new PropertyConstraintViolationException(modified + " has more than one " + attribute);
	}

	@Override
	public boolean isCheckedAt(T modified, CheckingType checkingType) {
		return checkingType.equals(CheckingType.CHECK_ON_ADD) || (modified.getValue() == null && checkingType.equals(CheckingType.CHECK_ON_REMOVE));
	}

}
