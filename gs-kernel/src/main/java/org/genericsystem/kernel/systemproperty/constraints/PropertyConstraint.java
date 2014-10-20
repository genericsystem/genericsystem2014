package org.genericsystem.kernel.systemproperty.constraints;

import java.util.Objects;
import java.util.stream.Stream;

import org.genericsystem.api.exception.ConstraintViolationException;
import org.genericsystem.api.exception.PropertyConstraintViolationException;
import org.genericsystem.kernel.AbstractVertex;
import org.genericsystem.kernel.AbstractVertex.SystemMap;
import org.genericsystem.kernel.DefaultRoot;
import org.genericsystem.kernel.DefaultVertex;
import org.genericsystem.kernel.Statics;

public class PropertyConstraint implements Constraint {

	@Override
	public <T extends AbstractVertex<T, U>, U extends DefaultRoot<T, U>> void check(DefaultVertex<T, U> modified, DefaultVertex<T, U> attribute) throws ConstraintViolationException {
		// TODO KK
		if (attribute.getValue().equals(SystemMap.class))
			return;
		T base = modified.getComposites().get(Statics.BASE_POSITION);
		Stream<T> snapshot = base.getHolders((T) attribute).stream().filter(x -> x.getComposites().get(Statics.BASE_POSITION).equals(base)).filter(next -> {
			for (int compositePos = Statics.TARGET_POSITION; compositePos < next.getComposites().size(); compositePos++)
				if (!Objects.equals(next.getComposites().get(compositePos), modified.getComposites().get(compositePos)))
					return false;
			return true;
		});
		if (snapshot.count() > 1)
			throw new PropertyConstraintViolationException(modified + " has more than one " + attribute);
	}

	@Override
	public <T extends AbstractVertex<T, U>, U extends DefaultRoot<T, U>> boolean isCheckedAt(DefaultVertex<T, U> modified, CheckingType checkingType) {
		return checkingType.equals(CheckingType.CHECK_ON_ADD) || (modified.getValue() == null && checkingType.equals(CheckingType.CHECK_ON_REMOVE));
	}
}
