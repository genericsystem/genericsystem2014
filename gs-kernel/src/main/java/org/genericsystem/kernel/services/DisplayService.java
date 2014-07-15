package org.genericsystem.kernel.services;

import org.genericsystem.kernel.Statics;

public interface DisplayService<T extends VertexService<T>> extends ApiService<T> {

	@Override
	default String info() {
		return "(" + getMeta().getValue() + ")" + getSupers() + this + getComponents() + " ";
	}

	@Override
	default void log() {
		log.info(detailedInfo());
	}

	@Override
	default void log(String prefix) {
		log.info(prefix + detailedInfo());
	}

	@Override
	default String detailedInfo() {
		String s = "\n\n*******************************" + System.identityHashCode(this) + "******************************\n";
		s += " Value       : " + getValue() + "\n";
		s += " Meta        : " + getMeta() + " (" + System.identityHashCode(getMeta()) + ")\n";
		s += " MetaLevel   : " + Statics.getMetaLevelString(getLevel()) + "\n";
		s += " Category    : " + Statics.getCategoryString(getLevel(), getComponents().size()) + "\n";
		s += " Class       : " + getClass().getName() + "\n";
		s += "**********************************************************************\n";
		for (T superGeneric : getSupers())
			s += " Super       : " + superGeneric + " (" + System.identityHashCode(superGeneric) + ")\n";
		for (T component : getComponents())
			s += " Component   : " + component + " (" + System.identityHashCode(component) + ")\n";
		s += "**********************************************************************\n";

		// for (Attribute attribute : getAttributes())
		// if (!(attribute.getValue() instanceof Class) /* || !Constraint.class.isAssignableFrom((Class<?>) attribute.getValue()) */) {
		// s += ((GenericImpl) attribute).getCategoryString() + "   : " + attribute + " (" + System.identityHashCode(attribute) + ")\n";
		// for (Holder holder : getHolders(attribute))
		// s += "                          ----------> " + ((GenericImpl) holder).getCategoryString() + " : " + holder + "\n";
		// }
		// s += "**********************************************************************\n";
		// s += "design date : " + new SimpleDateFormat(Statics.LOG_PATTERN).format(new Date(getDesignTs() / Statics.MILLI_TO_NANOSECONDS)) + "\n";
		// s += "birth date  : " + new SimpleDateFormat(Statics.LOG_PATTERN).format(new Date(getBirthTs() / Statics.MILLI_TO_NANOSECONDS)) + "\n";
		// s += "death date  : " + new SimpleDateFormat(Statics.LOG_PATTERN).format(new Date(getDeathTs() / Statics.MILLI_TO_NANOSECONDS)) + "\n";
		// s += "**********************************************************************\n";

		return s;
	}
}
