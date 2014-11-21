package org.genericsystem.kernel;

import java.io.Serializable;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;
import javax.json.JsonWriter;
import javax.json.stream.JsonGenerator;

import org.genericsystem.api.core.IVertex;
import org.genericsystem.api.core.IteratorSnapshot;
import org.genericsystem.api.core.Snapshot;
import org.genericsystem.api.exception.AliveConstraintViolationException;
import org.genericsystem.api.exception.NotFoundException;
import org.genericsystem.kernel.iterator.AbstractProjectionIterator;
import org.genericsystem.kernel.systemproperty.CascadeRemoveProperty;
import org.genericsystem.kernel.systemproperty.NoReferentialIntegrityProperty;
import org.genericsystem.kernel.systemproperty.constraints.InstanceValueClassConstraint;
import org.genericsystem.kernel.systemproperty.constraints.PropertyConstraint;
import org.genericsystem.kernel.systemproperty.constraints.RequiredConstraint;
import org.genericsystem.kernel.systemproperty.constraints.SingularConstraint;
import org.genericsystem.kernel.systemproperty.constraints.UniqueValueConstraint;

public interface DefaultVertex<T extends AbstractVertex<T>> extends IVertex<T> {

	@Override
	default T addRoot(Serializable value) {
		return addInstance(value, coerceToTArray(new Object[getMeta().getComponents().size()]));
	}

	@Override
	default T setRoot(Serializable value) {
		return setInstance(value, coerceToTArray(new Object[getMeta().getComponents().size()]));
	}

	@Override
	default T addNode(Serializable value) {
		return addHolder(getMeta(), value, coerceToTArray());
	}

	@Override
	default T setNode(Serializable value) {
		return setHolder(getMeta(), value, coerceToTArray());
	}

	@SuppressWarnings("unchecked")
	@Override
	default T addInheritingNode(Serializable value) {
		return addHolder(getMeta(), (T) this, value, coerceToTArray());
	}

	@SuppressWarnings("unchecked")
	@Override
	default T setInheritingNode(Serializable value) {
		return setHolder(getMeta(), (T) this, value, coerceToTArray());
	}

	@Override
	default Snapshot<T> getSubNodes() {
		return () -> getComposites().get().filter(x -> x.getMeta().equals(getMeta()));
	}

	@Override
	@SuppressWarnings("unchecked")
	default Snapshot<T> getAllSubNodes() {
		return () -> Stream.concat(Stream.of((T) this), getSubNodes().get().flatMap(node -> node.getAllSubNodes().get())).distinct();
	}

	@SuppressWarnings("unchecked")
	default T getAlive() {
		if (isRoot())
			return (T) this;
		if (isMeta()) {
			T aliveMeta = getSupers().get(0).getAlive();
			if (aliveMeta != null)
				for (T inheritings : aliveMeta.getInheritings())
					if (equals(inheritings))
						return inheritings;
		} else {
			T aliveMeta = getMeta().getAlive();
			if (aliveMeta != null)
				for (T instance : aliveMeta.getInstances())
					if (equals(instance))
						return instance;
		}
		return null;
	}

	@Override
	default boolean isRoot() {
		return this.equals(getRoot());
	}

	@SuppressWarnings("unchecked")
	@Override
	default boolean isAlive() {
		return equals(((T) this).getAlive());
	}

	@Override
	default void checkIsAlive() {
		if (!isAlive())
			getRoot().discardWithException(new AliveConstraintViolationException(info()));
	}

	@Override
	default int getLevel() {
		return this == getMeta() ? 0 : getMeta().getLevel() + 1;
	}

	@Override
	default boolean isMeta() {
		return getLevel() == Statics.META;
	}

	@Override
	default boolean isStructural() {
		return getLevel() == Statics.STRUCTURAL;
	}

	@Override
	default boolean isConcrete() {
		return getLevel() == Statics.CONCRETE;
	}

	@Override
	default boolean inheritsFrom(T superVertex) {
		if (equals(superVertex))
			return true;
		if (getLevel() != superVertex.getLevel())
			return false;
		return getSupers().stream().anyMatch(vertex -> vertex.inheritsFrom(superVertex));
	}

	@Override
	default boolean isInstanceOf(T metaVertex) {
		return getMeta().inheritsFrom(metaVertex);
	}

	@Override
	default boolean isSpecializationOf(T supra) {
		return getLevel() == supra.getLevel() ? inheritsFrom(supra) : (getLevel() > supra.getLevel() && getMeta().isSpecializationOf(supra));
	}

	@Override
	default boolean isCompositeOf(T vertex) {
		return isRoot() || getComponents().stream().anyMatch(component -> vertex.isSpecializationOf(component));
	}

	@Override
	default T getBaseComponent() {
		return getComponent(Statics.BASE_POSITION);
	}

	@Override
	default T getTargetComponent() {
		return getComponent(Statics.TARGET_POSITION);
	}

	@Override
	default T getTernaryComponent() {
		return getComponent(Statics.TERNARY_POSITION);
	}

	@Override
	default T getComponent(int pos) {
		return pos >= 0 && pos < getComponents().size() ? getComponents().get(pos) : null;
	}

	@Override
	default boolean isAncestorOf(T dependency) {
		return equals(dependency) || (!dependency.isMeta() && isAncestorOf(dependency.getMeta())) || dependency.getSupers().stream().anyMatch(this::isAncestorOf)
				|| dependency.getComponents().stream().filter(component -> !dependency.equals(component)).anyMatch(this::isAncestorOf);
	}

	@SuppressWarnings("unchecked")
	@Override
	default Snapshot<T> getAllInheritings() {
		return () -> Stream.concat(Stream.of((T) this), getInheritings().get().flatMap(inheriting -> inheriting.getAllInheritings().get())).distinct();
	}

	@Override
	default Snapshot<T> getAllInstances() {
		return () -> getAllInheritings().get().flatMap(inheriting -> inheriting.getInstances().get());
	}

	@SuppressWarnings("unchecked")
	@Override
	default T getInstance(T superT, Serializable value, T... composites) {
		return getInstance(Collections.singletonList(superT), value, composites);
	}

	@SuppressWarnings("unchecked")
	@Override
	default T getInstance(Serializable value, T... composites) {
		return getInstance(Collections.emptyList(), value, composites);
	}

	@Override
	default String info() {
		return "(" + getMeta().getValue() + ")" + getSupers() + this + getComponents() + " ";
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
		// s += "**********************************************************************\n";
		// s += "design date : " + new SimpleDateFormat(Statics.LOG_PATTERN).format(new Date(getDesignTs() / Statics.MILLI_TO_NANOSECONDS)) + "\n";
		// s += "birth date  : " + new SimpleDateFormat(Statics.LOG_PATTERN).format(new Date(getBirthTs() / Statics.MILLI_TO_NANOSECONDS)) + "\n";
		// s += "death date  : " + new SimpleDateFormat(Statics.LOG_PATTERN).format(new Date(getDeathTs() / Statics.MILLI_TO_NANOSECONDS)) + "\n";
		// s += "**********************************************************************\n";
		return s;
	}

	@Override
	default String toPrettyString() {
		StringWriter writer = new StringWriter();
		JsonWriter jsonWriter = Json.createWriterFactory(new HashMap<String, JsonValue>() {
			private static final long serialVersionUID = -8719498570554805477L;
			{
				put(JsonGenerator.PRETTY_PRINTING, JsonValue.TRUE);
			}
		}).createWriter(writer);
		// jsonWriter.write(toPrettyJSon());
		jsonWriter.write(toPrettyJSon());
		jsonWriter.close();
		return writer.toString();
	}

	@Override
	@SuppressWarnings("unchecked")
	default JsonObject toPrettyJSon() {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		builder.add("Value", toString());
		for (T attribute : getAttributes()) {
			JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
			for (T holder : getHolders(attribute)) {
				if (holder.getComponents().get(0).isSpecializationOf((T) this))
					arrayBuilder.add(holder.toPrettyJSon());
				builder.add(attribute.toString(), arrayBuilder);
			}
		}
		return builder.build();
	}

	@Override
	default JsonObject toJSonId() {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		builder.add("Id", System.identityHashCode(this));
		builder.add("Value", toString());
		builder.add("Meta", System.identityHashCode(getMeta()));
		JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
		for (T superVertex : getSupers())
			arrayBuilder.add(System.identityHashCode(superVertex));
		builder.add("Supers", arrayBuilder);

		for (T composite : getComponents())
			arrayBuilder.add(System.identityHashCode(composite));
		builder.add("Composites", arrayBuilder);
		return builder.build();
	}

	@Override
	@SuppressWarnings("unchecked")
	default T enableSystemProperty(Class<? extends SystemProperty> propertyClass, int pos, T... targets) {
		if (pos != Statics.NO_POSITION && getComponent(pos) == null)
			getRoot().discardWithException(new NotFoundException("System property is not apply because no component exists for position : " + pos));
		setSystemPropertyValue(propertyClass, pos, Boolean.TRUE, targets);
		return (T) this;
	}

	@Override
	@SuppressWarnings("unchecked")
	default T disableSystemProperty(Class<? extends SystemProperty> propertyClass, int pos, T... targets) {
		setSystemPropertyValue(propertyClass, pos, Boolean.FALSE, targets);
		return (T) this;
	}

	@Override
	default boolean isSystemPropertyEnabled(Class<? extends SystemProperty> propertyClass, int pos) {
		Serializable value = getSystemPropertyValue(propertyClass, pos);
		return value != null && !Boolean.FALSE.equals(value);
	}

	@Override
	default T enableReferentialIntegrity(int pos) {
		return disableSystemProperty(NoReferentialIntegrityProperty.class, pos);
	}

	@Override
	default T disableReferentialIntegrity(int pos) {
		return enableSystemProperty(NoReferentialIntegrityProperty.class, pos);
	}

	@Override
	default boolean isReferentialIntegrityEnabled(int pos) {
		return !isSystemPropertyEnabled(NoReferentialIntegrityProperty.class, pos);
	}

	@Override
	default T enableSingularConstraint(int pos) {
		return enableSystemProperty(SingularConstraint.class, pos);
	}

	@Override
	default T disableSingularConstraint(int pos) {
		return disableSystemProperty(SingularConstraint.class, pos);
	}

	@Override
	default boolean isSingularConstraintEnabled(int pos) {
		return isSystemPropertyEnabled(SingularConstraint.class, pos);
	}

	@Override
	default T enablePropertyConstraint() {
		return enableSystemProperty(PropertyConstraint.class, Statics.NO_POSITION);
	}

	@Override
	default T disablePropertyConstraint() {
		return disableSystemProperty(PropertyConstraint.class, Statics.NO_POSITION);
	}

	@Override
	default boolean isPropertyConstraintEnabled() {
		return isSystemPropertyEnabled(PropertyConstraint.class, Statics.NO_POSITION);
	}

	@Override
	default T enableUniqueValueConstraint() {
		return enableSystemProperty(UniqueValueConstraint.class, Statics.NO_POSITION);
	}

	@Override
	default T disableUniqueValueConstraint() {
		return disableSystemProperty(UniqueValueConstraint.class, Statics.NO_POSITION);
	}

	@Override
	default boolean isUniqueValueEnabled() {
		return isSystemPropertyEnabled(UniqueValueConstraint.class, Statics.NO_POSITION);
	}

	@Override
	default Class<?> getClassConstraint() {
		return (Class<?>) getSystemPropertyValue(InstanceValueClassConstraint.class, Statics.NO_POSITION);
	}

	@Override
	@SuppressWarnings("unchecked")
	default T setClassConstraint(Class<?> constraintClass) {
		setSystemPropertyValue(InstanceValueClassConstraint.class, Statics.NO_POSITION, constraintClass);
		return (T) this;
	}

	@Override
	default T enableClassConstraint(Class<?> constraintClass) {
		return setClassConstraint(constraintClass);
	}

	@Override
	default T disableClassConstraint() {
		return setClassConstraint(null);
	}

	@Override
	default T enableRequiredConstraint(int pos) {
		return enableSystemProperty(RequiredConstraint.class, pos, coerceToTArray(this.getComponents().get(pos)));
		// return enableSystemProperty(RequiredConstraint.class, pos);
	}

	@Override
	default T disableRequiredConstraint(int pos) {
		// return disableSystemProperty(RequiredConstraint.class, pos);
		return disableSystemProperty(RequiredConstraint.class, pos, coerceToTArray(this.getComponents().get(pos)));
	}

	@Override
	default boolean isRequiredConstraintEnabled(int pos) {
		return isSystemPropertyEnabled(RequiredConstraint.class, pos);
	}

	@Override
	default T enableCascadeRemove(int pos) {
		return enableSystemProperty(CascadeRemoveProperty.class, pos);
	}

	@Override
	default T disableCascadeRemove(int pos) {
		return disableSystemProperty(CascadeRemoveProperty.class, pos);
	}

	@Override
	default boolean isCascadeRemove(int pos) {
		return isSystemPropertyEnabled(CascadeRemoveProperty.class, pos);
	}

	@SuppressWarnings("unchecked")
	@Override
	default Snapshot<T> getAttributes() {
		return getAttributes(getRoot().getMetaAttribute());
	}

	@SuppressWarnings("unchecked")
	@Override
	default Snapshot<T> getAttributes(int pos) {
		return () -> getAttributes().get().filter(attribute -> attribute.getComponent(pos) != null && ((T) this).isSpecializationOf(attribute.getComponent(pos)));
	}

	@SuppressWarnings("unchecked")
	@Override
	default Snapshot<T> getHolders(T attribute, int pos) {
		return () -> getHolders(attribute).get().filter(holder -> holder.getComponent(pos) != null && ((T) this).isSpecializationOf(holder.getComponent(pos)));

	}

	@Override
	default Snapshot<Serializable> getValues(T attribute) {
		return (IteratorSnapshot<Serializable>) (() -> new AbstractProjectionIterator<T, Serializable>(getHolders(attribute).get().iterator()) {
			@Override
			public Serializable project(T generic) {
				return generic.getValue();
			}
		});
	}

	@Override
	default Snapshot<Serializable> getValues(T attribute, int pos) {
		return () -> getHolders(attribute, pos).get().map(T::getValue);
	}

	@Override
	default T updateValue(Serializable newValue) {
		return update(getSupers(), newValue, coerceToTArray(getComponents().toArray()));
	}

	@Override
	@SuppressWarnings("unchecked")
	default T updateSupers(T... supers) {
		return update(Arrays.asList(supers), getValue(), coerceToTArray(getComponents().toArray()));
	}

	@Override
	@SuppressWarnings("unchecked")
	default T updateComposites(T... newComposites) {
		return update(getSupers(), getValue(), newComposites);
	}

	@Override
	@SuppressWarnings("unchecked")
	default T update(Serializable newValue, T... newComposites) {
		return update(Collections.emptyList(), newValue, newComposites);
	}

	@Override
	@SuppressWarnings("unchecked")
	default T setInstance(Serializable value, T... components) {
		return setInstance(Collections.emptyList(), value, components);
	}

	@Override
	@SuppressWarnings("unchecked")
	default T setInstance(T override, Serializable value, T... components) {
		return setInstance(Collections.singletonList(override), value, components);
	}

	@Override
	@SuppressWarnings("unchecked")
	default T addInstance(Serializable value, T... components) {
		return addInstance(Collections.emptyList(), value, components);
	}

	@Override
	@SuppressWarnings("unchecked")
	default T addInstance(T override, Serializable value, T... components) {
		return addInstance(Collections.singletonList(override), value, components);
	}

	@Override
	@SuppressWarnings("unchecked")
	default T addAttribute(Serializable value, T... targets) {
		return addAttribute(Collections.emptyList(), value, targets);
	}

	@Override
	@SuppressWarnings("unchecked")
	default T setAttribute(Serializable value, T... targets) {
		return setAttribute(Collections.emptyList(), value, targets);
	}

	@Override
	@SuppressWarnings("unchecked")
	default T addHolder(T attribute, Serializable value, T... targets) {
		return attribute.addInstance(value, addThisToTargets(targets));
	}

	@Override
	@SuppressWarnings("unchecked")
	default T setHolder(T attribute, Serializable value, T... targets) {
		return attribute.setInstance(value, addThisToTargets(targets));
	}

	@Override
	@SuppressWarnings("unchecked")
	default T addAttribute(T override, Serializable value, T... targets) {
		return addAttribute(Collections.singletonList(override), value, targets);
	}

	@Override
	@SuppressWarnings("unchecked")
	default T setAttribute(T override, Serializable value, T... targets) {
		return setAttribute(Collections.singletonList(override), value, targets);
	}

	@Override
	@SuppressWarnings("unchecked")
	default T addHolder(T attribute, T override, Serializable value, T... targets) {
		return attribute.addInstance(override, value, addThisToTargets(targets));
	}

	@Override
	@SuppressWarnings("unchecked")
	default T setHolder(T attribute, T override, Serializable value, T... targets) {
		return attribute.setInstance(override, value, addThisToTargets(targets));
	}

	@Override
	@SuppressWarnings("unchecked")
	default T addAttribute(List<T> overrides, Serializable value, T... targets) {
		return getRoot().addInstance(overrides, value, addThisToTargets(targets));
	}

	@Override
	@SuppressWarnings("unchecked")
	default T setAttribute(List<T> overrides, Serializable value, T... targets) {
		return getRoot().setInstance(overrides, value, addThisToTargets(targets));
	}

	@Override
	@SuppressWarnings("unchecked")
	default T addHolder(T attribute, List<T> overrides, Serializable value, T... targets) {
		return attribute.addInstance(overrides, value, addThisToTargets(targets));
	}

	@Override
	@SuppressWarnings("unchecked")
	default T setHolder(T attribute, List<T> overrides, Serializable value, T... targets) {
		return attribute.setInstance(overrides, value, addThisToTargets(targets));
	}

}