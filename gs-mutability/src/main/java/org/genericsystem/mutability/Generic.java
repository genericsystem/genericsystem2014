package org.genericsystem.mutability;

import java.io.Serializable;
import java.util.List;

import javax.json.JsonObject;

import org.genericsystem.api.core.IVertex;
import org.genericsystem.api.core.Snapshot;

public interface Generic extends IVertex<Generic> {

	default Engine getEngine() {
		throw new IllegalStateException();
	}

	// @Override
	// default String toString() {
	// return getCurrentCache().unwrap(this).toString();
	// }

	@Override
	default Cache getCurrentCache() {
		return getEngine().getCurrentCache();
	}

	@Override
	default boolean isRoot() {
		return getCurrentCache().unwrap(this).isRoot();
	}

	@Override
	default Generic getMeta() {
		return getCurrentCache().wrap(getCurrentCache().unwrap(this).getMeta());
	}

	@Override
	default Engine getRoot() {
		return getCurrentCache().getRoot();
	}

	@Override
	default List<Generic> getSupers() {
		return getCurrentCache().wrap(getCurrentCache().unwrap(this).getSupers());
	}

	@Override
	default boolean isAlive() {
		return getCurrentCache().isAlive(this);
	}

	@Override
	default Serializable getValue() {
		return getCurrentCache().unwrap(this).getValue();
	}

	@Override
	default List<Generic> getComponents() {
		return getCurrentCache().wrap(getCurrentCache().unwrap(this).getComponents());
	}

	@Override
	default JsonObject toJSonId() {
		return getCurrentCache().unwrap(this).toJSonId();
	}

	@Override
	default Generic[] coerceToTArray(Object... array) {
		return getCurrentCache().wrap(getCurrentCache().unwrap(this).coerceToTArray(array));
	}

	@Override
	default Generic[] addThisToTargets(Generic... targets) {
		return getCurrentCache().wrap(getCurrentCache().unwrap(this).addThisToTargets(getCurrentCache().unwrap(targets)));
	}

	@Override
	default Generic[] addThisToTargets(Generic firstTarget, Generic... otherTargets) {
		return getCurrentCache().wrap(getCurrentCache().unwrap(this).addThisToTargets(getCurrentCache().unwrap(firstTarget), getCurrentCache().unwrap(otherTargets)));
	}

	@Override
	default int getLevel() {
		return getCurrentCache().unwrap(this).getLevel();
	}

	@Override
	default boolean isMeta() {
		return getCurrentCache().unwrap(this).isMeta();
	}

	@Override
	default boolean isStructural() {
		return getCurrentCache().unwrap(this).isStructural();
	}

	@Override
	default boolean isConcrete() {
		return getCurrentCache().unwrap(this).isConcrete();
	}

	@Override
	default boolean inheritsFrom(Generic superVertex) {
		return getCurrentCache().unwrap(this).inheritsFrom(getCurrentCache().unwrap(superVertex));
	}

	@Override
	default boolean isInstanceOf(Generic metaVertex) {
		return getCurrentCache().unwrap(this).isInstanceOf(getCurrentCache().unwrap(metaVertex));
	}

	@Override
	default boolean isSpecializationOf(Generic vertex) {
		return getCurrentCache().unwrap(this).isSpecializationOf(getCurrentCache().unwrap(vertex));
	}

	@Override
	default Generic getInstance(Serializable value, Generic... components) {
		return getCurrentCache().wrap(getCurrentCache().unwrap(this).getInstance(value, getCurrentCache().unwrap(components)));
	}

	@Override
	default Generic getInstance(Generic superT, Serializable value, Generic... components) {
		return getCurrentCache().wrap(getCurrentCache().unwrap(this).getInstance(getCurrentCache().unwrap(superT), value, getCurrentCache().unwrap(components)));
	}

	@Override
	default Generic getInstance(List<Generic> supers, Serializable value, Generic... components) {
		return getCurrentCache().wrap(getCurrentCache().unwrap(this).getInstance(getCurrentCache().unwrap(supers), value, getCurrentCache().unwrap(components)));
	}

	@Override
	default Generic getAttribute(Serializable value, Generic... components) {
		return getCurrentCache().wrap(getCurrentCache().unwrap(this).getAttribute(value, getCurrentCache().unwrap(components)));
	}

	@Override
	default Generic getHolder(Generic attribute, Serializable value, Generic... components) {
		return getCurrentCache().wrap(getCurrentCache().unwrap(this).getHolder(getCurrentCache().unwrap(attribute), value, getCurrentCache().unwrap(components)));
	}

	@Override
	default Generic getRelation(Serializable value, Generic... components) {
		return getCurrentCache().wrap(getCurrentCache().unwrap(this).getRelation(value, getCurrentCache().unwrap(components)));
	}

	@Override
	default Generic getLink(Generic relation, Serializable value, Generic... components) {
		return getCurrentCache().wrap(getCurrentCache().unwrap(this).getLink(getCurrentCache().unwrap(relation), value, getCurrentCache().unwrap(components)));
	}

	@Override
	default boolean isCompositeOf(Generic vertex) {
		return getCurrentCache().unwrap(this).isCompositeOf(getCurrentCache().unwrap(vertex));
	}

	@Override
	default Snapshot<Generic> getAttributes() {
		return () -> getCurrentCache().unwrap(this).getAttributes().get().map(getCurrentCache()::wrap);
	}

	@Override
	default Snapshot<Generic> getAttributes(int pos) {
		return () -> getCurrentCache().unwrap(this).getAttributes(pos).get().map(getCurrentCache()::wrap);
	}

	@Override
	default Snapshot<Generic> getAttributes(Generic attribute) {
		return () -> getCurrentCache().unwrap(this).getAttributes(getCurrentCache().unwrap(attribute)).get().map(getCurrentCache()::wrap);
	}

	@Override
	default Snapshot<Generic> getHolders(Generic attribute) {
		return () -> getCurrentCache().unwrap(this).getHolders(getCurrentCache().unwrap(attribute)).get().map(getCurrentCache()::wrap);
	}

	@Override
	default Snapshot<Generic> getHolders(Generic attribute, int pos) {
		return () -> getCurrentCache().unwrap(this).getHolders(getCurrentCache().unwrap(attribute), pos).get().map(getCurrentCache()::wrap);
	}

	@Override
	default Snapshot<Generic> getRelations() {
		return () -> getCurrentCache().unwrap(this).getRelations().get().map(getCurrentCache()::wrap);
	}

	@Override
	default Snapshot<Generic> getRelations(int pos) {
		return () -> getCurrentCache().unwrap(this).getRelations(pos).get().map(getCurrentCache()::wrap);
	}

	@Override
	default Snapshot<Generic> getRelations(Generic relation) {
		return () -> getCurrentCache().unwrap(this).getRelations(getCurrentCache().unwrap(relation)).get().map(getCurrentCache()::wrap);
	}

	@Override
	default Snapshot<Generic> getLinks(Generic link) {
		return () -> getCurrentCache().unwrap(this).getLinks(getCurrentCache().unwrap(link)).get().map(getCurrentCache()::wrap);
	}

	@Override
	default Snapshot<Generic> getLinks(Generic link, int pos) {
		return () -> getCurrentCache().unwrap(this).getLinks(getCurrentCache().unwrap(link), pos).get().map(getCurrentCache()::wrap);
	}

	@Override
	default Snapshot<Serializable> getValues(Generic attribute) {
		return () -> getCurrentCache().unwrap(this).getValues(getCurrentCache().unwrap(attribute)).get();
	}

	@Override
	default Snapshot<Serializable> getValues(Generic attribute, int pos) {
		return () -> getCurrentCache().unwrap(this).getValues(getCurrentCache().unwrap(attribute), pos).get();
	}

	@Override
	default Snapshot<Generic> getInstances() {
		return () -> getCurrentCache().unwrap(this).getInstances().get().map(getCurrentCache()::wrap);
	}

	@Override
	default Snapshot<Generic> getAllInstances() {
		return () -> getCurrentCache().unwrap(this).getAllInstances().get().map(getCurrentCache()::wrap);
	}

	@Override
	default Snapshot<Generic> getInheritings() {
		return () -> getCurrentCache().unwrap(this).getInheritings().get().map(getCurrentCache()::wrap);
	}

	@Override
	default Snapshot<Generic> getAllInheritings() {
		return () -> getCurrentCache().unwrap(this).getAllInheritings().get().map(getCurrentCache()::wrap);
	}

	@Override
	default Snapshot<Generic> getComposites() {
		return () -> getCurrentCache().unwrap(this).getComposites().get().map(getCurrentCache()::wrap);
	}

	@Override
	default boolean isAncestorOf(Generic dependency) {
		return getCurrentCache().unwrap(this).isAncestorOf(getCurrentCache().unwrap(dependency));
	}

	@Override
	default String info() {
		return getCurrentCache().unwrap(this).info();
	}

	@Override
	default String detailedInfo() {
		return getCurrentCache().unwrap(this).detailedInfo();
	}

	@Override
	default String toPrettyString() {
		return getCurrentCache().unwrap(this).toPrettyString();
	}

	@Override
	default JsonObject toPrettyJSon() {
		return getCurrentCache().unwrap(this).toPrettyJSon();
	}

	@Override
	default Serializable getSystemPropertyValue(Class<? extends org.genericsystem.api.core.IVertex.SystemProperty> propertyClass, int pos) {
		return getCurrentCache().unwrap(this).getSystemPropertyValue(propertyClass, pos);
	}

	@Override
	default Generic setSystemPropertyValue(Class<? extends org.genericsystem.api.core.IVertex.SystemProperty> propertyClass, int pos, Serializable value, Generic... targets) {
		return getCurrentCache().wrap(getCurrentCache().unwrap(this).setSystemPropertyValue(propertyClass, pos, value, getCurrentCache().unwrap(targets)));
	}

	@Override
	default Generic enableSystemProperty(Class<? extends org.genericsystem.api.core.IVertex.SystemProperty> propertyClass, int pos, Generic... targets) {
		return getCurrentCache().wrap(getCurrentCache().unwrap(this).enableSystemProperty(propertyClass, pos, getCurrentCache().unwrap(targets)));
	}

	@Override
	default Generic disableSystemProperty(Class<? extends org.genericsystem.api.core.IVertex.SystemProperty> propertyClass, int pos, Generic... targets) {
		return getCurrentCache().wrap(getCurrentCache().unwrap(this).disableSystemProperty(propertyClass, pos, getCurrentCache().unwrap(targets)));
	}

	@Override
	default boolean isSystemPropertyEnabled(Class<? extends org.genericsystem.api.core.IVertex.SystemProperty> propertyClass, int pos) {
		return getCurrentCache().unwrap(this).isSystemPropertyEnabled(propertyClass, pos);
	}

	@Override
	default Generic enableReferentialIntegrity(int pos) {
		return getCurrentCache().wrap(getCurrentCache().unwrap(this).enableReferentialIntegrity(pos));
	}

	@Override
	default Generic disableReferentialIntegrity(int pos) {
		return getCurrentCache().wrap(getCurrentCache().unwrap(this).disableReferentialIntegrity(pos));
	}

	@Override
	default boolean isReferentialIntegrityEnabled(int pos) {
		return getCurrentCache().unwrap(this).isReferentialIntegrityEnabled(pos);
	}

	@Override
	default Generic enableSingularConstraint(int pos) {
		return getCurrentCache().wrap(getCurrentCache().unwrap(this).enableSingularConstraint(pos));
	}

	@Override
	default Generic disableSingularConstraint(int pos) {
		return getCurrentCache().wrap(getCurrentCache().unwrap(this).disableSingularConstraint(pos));
	}

	@Override
	default boolean isSingularConstraintEnabled(int pos) {
		return getCurrentCache().unwrap(this).isSingularConstraintEnabled(pos);
	}

	@Override
	default Generic enablePropertyConstraint() {
		return getCurrentCache().wrap(getCurrentCache().unwrap(this).enablePropertyConstraint());
	}

	@Override
	default Generic disablePropertyConstraint() {
		return getCurrentCache().wrap(getCurrentCache().unwrap(this).disablePropertyConstraint());
	}

	@Override
	default boolean isPropertyConstraintEnabled() {
		return getCurrentCache().unwrap(this).isPropertyConstraintEnabled();
	}

	@Override
	default Generic enableUniqueValueConstraint() {
		return getCurrentCache().wrap(getCurrentCache().unwrap(this).enableUniqueValueConstraint());
	}

	@Override
	default Generic disableUniqueValueConstraint() {
		return getCurrentCache().wrap(getCurrentCache().unwrap(this).disableUniqueValueConstraint());
	}

	@Override
	default boolean isUniqueValueEnabled() {
		return getCurrentCache().unwrap(this).isUniqueValueEnabled();
	}

	@Override
	default Class<?> getClassConstraint() {
		return getCurrentCache().unwrap(this).getClassConstraint();
	}

	@Override
	default Generic setClassConstraint(Class<?> constraintClass) {
		return getCurrentCache().wrap(getCurrentCache().unwrap(this).setClassConstraint(constraintClass));
	}

	@Override
	default Generic enableRequiredConstraint(int pos) {
		return getCurrentCache().wrap(getCurrentCache().unwrap(this).enableRequiredConstraint(pos));
	}

	@Override
	default Generic disableRequiredConstraint(int pos) {
		return getCurrentCache().wrap(getCurrentCache().unwrap(this).disableRequiredConstraint(pos));
	}

	@Override
	default boolean isRequiredConstraintEnabled(int pos) {
		return getCurrentCache().unwrap(this).isRequiredConstraintEnabled(pos);
	}

	@Override
	default Generic enableCascadeRemove(int pos) {
		return getCurrentCache().wrap(getCurrentCache().unwrap(this).enableCascadeRemove(pos));
	}

	@Override
	default Generic disableCascadeRemove(int pos) {
		return getCurrentCache().wrap(getCurrentCache().unwrap(this).disableCascadeRemove(pos));
	}

	@Override
	default boolean isCascadeRemoveEnabled(int pos) {
		return getCurrentCache().unwrap(this).isCascadeRemoveEnabled(pos);
	}

	@Override
	default void remove() {
		getCurrentCache().unwrap(this).remove();
	}

	@Override
	default Generic addInstance(Serializable value, Generic... components) {
		return getCurrentCache().wrap(getCurrentCache().unwrap(this).addInstance(value, getCurrentCache().unwrap(components)));
	}

	@Override
	default Generic addInstance(Generic override, Serializable value, Generic... components) {
		return getCurrentCache().wrap(getCurrentCache().unwrap(this).addInstance(getCurrentCache().unwrap(override), value, getCurrentCache().unwrap(components)));
	}

	@Override
	default Generic addInstance(List<Generic> overrides, Serializable value, Generic... components) {
		return getCurrentCache().wrap(getCurrentCache().unwrap(this).addInstance(getCurrentCache().unwrap(overrides), value, getCurrentCache().unwrap(components)));
	}

	@Override
	default Generic setInstance(Serializable value, Generic... components) {
		return getCurrentCache().wrap(getCurrentCache().unwrap(this).setInstance(value, getCurrentCache().unwrap(components)));
	}

	@Override
	default Generic setInstance(Generic override, Serializable value, Generic... components) {
		return getCurrentCache().wrap(getCurrentCache().unwrap(this).setInstance(getCurrentCache().unwrap(override), value, getCurrentCache().unwrap(components)));
	}

	@Override
	default Generic setInstance(List<Generic> overrides, Serializable value, Generic... components) {
		return getCurrentCache().wrap(getCurrentCache().unwrap(this).setInstance(getCurrentCache().unwrap(overrides), value, getCurrentCache().unwrap(components)));
	}

	@Override
	default Generic addRoot(Serializable value) {
		return getCurrentCache().wrap(getCurrentCache().unwrap(this).addRoot(value));
	}

	@Override
	default Generic setRoot(Serializable value) {
		return getCurrentCache().wrap(getCurrentCache().unwrap(this).setRoot(value));
	}

	@Override
	default Generic addChild(Serializable value, Generic... targets) {
		return getCurrentCache().wrap(getCurrentCache().unwrap(this).addChild(value, getCurrentCache().unwrap(targets)));
	}

	@Override
	default Generic setChild(Serializable value, Generic... targets) {
		return getCurrentCache().wrap(getCurrentCache().unwrap(this).setChild(value, getCurrentCache().unwrap(targets)));
	}

	@Override
	default Generic addInheritingChild(Serializable value, Generic... targets) {
		return getCurrentCache().wrap(getCurrentCache().unwrap(this).addInheritingChild(value, getCurrentCache().unwrap(targets)));
	}

	@Override
	default Generic setInheritingChild(Serializable value, Generic... targets) {
		return getCurrentCache().wrap(getCurrentCache().unwrap(this).setInheritingChild(value, getCurrentCache().unwrap(targets)));
	}

	@Override
	default Snapshot<Generic> getAllChildren() {
		return () -> getCurrentCache().unwrap(this).getAllChildren().get().map(getCurrentCache()::wrap);
	}

	@Override
	default Snapshot<Generic> getChildren() {
		return () -> getCurrentCache().unwrap(this).getChildren().get().map(getCurrentCache()::wrap);
	}

	@Override
	default void traverse(Visitor<Generic> visitor) {
		visitor.traverse(this);
	}

	@Override
	default Generic addAttribute(Serializable value, Generic... targets) {
		return getCurrentCache().wrap(getCurrentCache().unwrap(this).addAttribute(value, getCurrentCache().unwrap(targets)));
	}

	@Override
	default Generic addAttribute(Generic override, Serializable value, Generic... targets) {
		return getCurrentCache().wrap(getCurrentCache().unwrap(this).addAttribute(getCurrentCache().unwrap(override), value, getCurrentCache().unwrap(targets)));
	}

	@Override
	default Generic addAttribute(List<Generic> overrides, Serializable value, Generic... targets) {
		return getCurrentCache().wrap(getCurrentCache().unwrap(this).addAttribute(getCurrentCache().unwrap(overrides), value, getCurrentCache().unwrap(targets)));
	}

	@Override
	default Generic setAttribute(Serializable value, Generic... targets) {
		return getCurrentCache().wrap(getCurrentCache().unwrap(this).setAttribute(value, getCurrentCache().unwrap(targets)));
	}

	@Override
	default Generic setAttribute(Generic override, Serializable value, Generic... targets) {
		return getCurrentCache().wrap(getCurrentCache().unwrap(this).setAttribute(getCurrentCache().unwrap(override), value, getCurrentCache().unwrap(targets)));
	}

	@Override
	default Generic setAttribute(List<Generic> overrides, Serializable value, Generic... targets) {
		return getCurrentCache().wrap(getCurrentCache().unwrap(this).setAttribute(getCurrentCache().unwrap(overrides), value, getCurrentCache().unwrap(targets)));
	}

	@Override
	default Generic addHolder(Generic attribute, Serializable value, Generic... targets) {
		return getCurrentCache().wrap(getCurrentCache().unwrap(this).addHolder(getCurrentCache().unwrap(attribute), value, getCurrentCache().unwrap(targets)));
	}

	@Override
	default Generic addHolder(Generic attribute, Generic override, Serializable value, Generic... targets) {
		return getCurrentCache().wrap(getCurrentCache().unwrap(this).addHolder(getCurrentCache().unwrap(attribute), getCurrentCache().unwrap(override), value, getCurrentCache().unwrap(targets)));
	}

	@Override
	default Generic addHolder(Generic attribute, List<Generic> overrides, Serializable value, Generic... targets) {
		return getCurrentCache().wrap(getCurrentCache().unwrap(this).addHolder(getCurrentCache().unwrap(attribute), getCurrentCache().unwrap(overrides), value, getCurrentCache().unwrap(targets)));
	}

	@Override
	default Generic setHolder(Generic attribute, Serializable value, Generic... targets) {
		return getCurrentCache().wrap(getCurrentCache().unwrap(this).setHolder(getCurrentCache().unwrap(attribute), value, getCurrentCache().unwrap(targets)));
	}

	@Override
	default Generic setHolder(Generic attribute, Generic override, Serializable value, Generic... targets) {
		return getCurrentCache().wrap(getCurrentCache().unwrap(this).setHolder(getCurrentCache().unwrap(attribute), getCurrentCache().unwrap(override), value, getCurrentCache().unwrap(targets)));
	}

	@Override
	default Generic setHolder(Generic attribute, List<Generic> overrides, Serializable value, Generic... targets) {
		return getCurrentCache().wrap(getCurrentCache().unwrap(this).setHolder(getCurrentCache().unwrap(attribute), getCurrentCache().unwrap(overrides), value, getCurrentCache().unwrap(targets)));
	}

	@Override
	default Generic addRelation(Serializable value, Generic firstTarget, Generic... otherTargets) {
		return getCurrentCache().wrap(getCurrentCache().unwrap(this).addRelation(value, getCurrentCache().unwrap(firstTarget), getCurrentCache().unwrap(otherTargets)));
	}

	@Override
	default Generic addRelation(Generic override, Serializable value, Generic firstTarget, Generic... otherTargets) {
		return getCurrentCache().wrap(getCurrentCache().unwrap(this).addRelation(getCurrentCache().unwrap(override), value, getCurrentCache().unwrap(firstTarget), getCurrentCache().unwrap(otherTargets)));
	}

	@Override
	default Generic addRelation(List<Generic> overrides, Serializable value, Generic firstTarget, Generic... otherTargets) {
		return getCurrentCache().wrap(getCurrentCache().unwrap(this).addRelation(getCurrentCache().unwrap(overrides), value, getCurrentCache().unwrap(firstTarget), getCurrentCache().unwrap(otherTargets)));
	}

	@Override
	default Generic setRelation(Serializable value, Generic firstTarget, Generic... otherTargets) {
		return getCurrentCache().wrap(getCurrentCache().unwrap(this).setRelation(value, getCurrentCache().unwrap(firstTarget), getCurrentCache().unwrap(otherTargets)));
	}

	@Override
	default Generic setRelation(Generic override, Serializable value, Generic firstTarget, Generic... otherTargets) {
		return getCurrentCache().wrap(getCurrentCache().unwrap(this).setRelation(getCurrentCache().unwrap(override), value, getCurrentCache().unwrap(firstTarget), getCurrentCache().unwrap(otherTargets)));
	}

	@Override
	default Generic setRelation(List<Generic> overrides, Serializable value, Generic firstTarget, Generic... otherTargets) {
		return getCurrentCache().wrap(getCurrentCache().unwrap(this).setRelation(getCurrentCache().unwrap(overrides), value, getCurrentCache().unwrap(firstTarget), getCurrentCache().unwrap(otherTargets)));
	}

	@Override
	default Generic addLink(Generic relation, Serializable value, Generic firstTarget, Generic... otherTargets) {
		return getCurrentCache().wrap(getCurrentCache().unwrap(this).addLink(getCurrentCache().unwrap(relation), value, getCurrentCache().unwrap(firstTarget), getCurrentCache().unwrap(otherTargets)));
	}

	@Override
	default Generic addLink(Generic relation, Generic override, Serializable value, Generic firstTarget, Generic... otherTargets) {
		return getCurrentCache().wrap(getCurrentCache().unwrap(this).addLink(getCurrentCache().unwrap(relation), getCurrentCache().unwrap(override), value, getCurrentCache().unwrap(firstTarget), getCurrentCache().unwrap(otherTargets)));
	}

	@Override
	default Generic addLink(Generic relation, List<Generic> overrides, Serializable value, Generic firstTarget, Generic... otherTargets) {
		return getCurrentCache().wrap(getCurrentCache().unwrap(this).addLink(getCurrentCache().unwrap(relation), getCurrentCache().unwrap(overrides), value, getCurrentCache().unwrap(firstTarget), getCurrentCache().unwrap(otherTargets)));
	}

	@Override
	default Generic setLink(Generic relation, Serializable value, Generic firstTarget, Generic... otherTargets) {
		return getCurrentCache().wrap(getCurrentCache().unwrap(this).setLink(getCurrentCache().unwrap(relation), value, getCurrentCache().unwrap(firstTarget), getCurrentCache().unwrap(otherTargets)));
	}

	@Override
	default Generic setLink(Generic link, Generic override, Serializable value, Generic firstTarget, Generic... otherTargets) {
		return getCurrentCache().wrap(getCurrentCache().unwrap(this).setLink(getCurrentCache().unwrap(link), getCurrentCache().unwrap(override), value, getCurrentCache().unwrap(firstTarget), getCurrentCache().unwrap(otherTargets)));
	}

	@Override
	default Generic setLink(Generic relation, List<Generic> overrides, Serializable value, Generic firstTarget, Generic... otherTargets) {
		return getCurrentCache().wrap(getCurrentCache().unwrap(this).setLink(getCurrentCache().unwrap(relation), getCurrentCache().unwrap(overrides), value, getCurrentCache().unwrap(firstTarget), getCurrentCache().unwrap(otherTargets)));
	}

	@Override
	default Generic updateValue(Serializable newValue) {
		return getCurrentCache().wrap(getCurrentCache().unwrap(this).updateValue(newValue));
	}

	@Override
	default Generic updateSupers(Generic... overrides) {
		return getCurrentCache().wrap(getCurrentCache().unwrap(this).updateSupers(getCurrentCache().unwrap(overrides)));
	}

	@Override
	default Generic updateComponents(Generic... newComposites) {
		return getCurrentCache().wrap(getCurrentCache().unwrap(this).updateComponents(getCurrentCache().unwrap(newComposites)));
	}

	@Override
	default Generic update(List<Generic> overrides, Serializable newValue, Generic... newComposites) {
		return getCurrentCache().wrap(getCurrentCache().unwrap(this).update(getCurrentCache().unwrap(overrides), newValue, getCurrentCache().unwrap(newComposites)));
	}

	@Override
	default Generic update(Serializable newValue, Generic... newComposites) {
		return getCurrentCache().wrap(getCurrentCache().unwrap(this).update(newValue, getCurrentCache().unwrap(newComposites)));
	}

	@Override
	default Generic update(Generic override, Serializable newValue, Generic... newComposites) {
		return getCurrentCache().wrap(getCurrentCache().unwrap(this).update(getCurrentCache().unwrap(override), newValue, getCurrentCache().unwrap(newComposites)));
	}

	@Override
	default Generic getBaseComponent() {
		return getCurrentCache().wrap(getCurrentCache().unwrap(this).getBaseComponent());
	}

	@Override
	default Generic getTargetComponent() {
		return getCurrentCache().wrap(getCurrentCache().unwrap(this).getTargetComponent());
	}

	@Override
	default Generic getTernaryComponent() {
		return getCurrentCache().wrap(getCurrentCache().unwrap(this).getTernaryComponent());
	}

	@Override
	default Generic getComponent(int pos) {
		return getCurrentCache().wrap(getCurrentCache().unwrap(this).getComponent(pos));
	}

	@Override
	default Generic enableClassConstraint(Class<?> constraintClass) {
		return getCurrentCache().wrap(getCurrentCache().unwrap(this).enableClassConstraint(constraintClass));
	}

	@Override
	default Generic disableClassConstraint() {
		return getCurrentCache().wrap(getCurrentCache().unwrap(this).disableClassConstraint());
	}

}
