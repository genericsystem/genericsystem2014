package org.genericsystem.mutability;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.json.JsonObject;
import org.genericsystem.api.core.IVertex;
import org.genericsystem.api.core.Snapshot;
import org.genericsystem.api.exception.AliveConstraintViolationException;

public class Generic implements IVertex<Generic> {

	protected Engine engine;

	Generic(Engine engine) {
		this.engine = engine;
	}

	protected Generic wrap(org.genericsystem.concurrency.Generic genericT) {
		return getCurrentCache().getByValue(genericT);
	}

	protected org.genericsystem.concurrency.Generic unwrap(Generic genericM) {
		org.genericsystem.concurrency.Generic generic = getCurrentCache().getByMutable(genericM);
		if (generic == null)
			engine.getConcurrencyEngine().getCurrentCache().discardWithException(new AliveConstraintViolationException("Your mutable is not still available. No generic matched"));
		return generic;
	}


	protected List<Generic> wrap(List<org.genericsystem.concurrency.Generic> listT) {
		return listT.stream().map(this::wrap).collect(Collectors.toList());
	}

	protected List<org.genericsystem.concurrency.Generic> unwrap(List<Generic> listM) {
		return listM.stream().map(this::unwrap).collect(Collectors.toList());
	}

	protected Generic[] wrap(org.genericsystem.concurrency.Generic... listT) {
		return engine.coerceToTArray(Arrays.asList(listT).stream().map(this::wrap).collect(Collectors.toList()).toArray());
	}

	protected org.genericsystem.concurrency.Generic[] unwrap(Generic... listM) {
		return unwrap(engine).coerceToTArray(Arrays.asList(listM).stream().map(this::unwrap).collect(Collectors.toList()).toArray());
	}

	@Override
	public boolean isRoot() {
		return unwrap(this).isRoot();
	}

	@Override
	public Generic getMeta() {
		return wrap(unwrap(this).getMeta());
	}

	@Override
	public Engine getRoot() {
		return (Engine) wrap((org.genericsystem.concurrency.Generic) unwrap(this).getRoot());
	}

	@Override
	public List<Generic> getSupers() {
		return wrap(unwrap(this).getSupers());
	}

	@Override
	public boolean isAlive() {
		org.genericsystem.concurrency.Generic generic = getCurrentCache().getByMutable(this);
		return generic!=null ? generic.isAlive() : false;
	}

	@Override
	public Serializable getValue() {
		return unwrap(this).getValue();
	}

	@Override
	public List<Generic> getComponents() {
		return wrap(unwrap(this).getComponents());
	}

	@Override
	public JsonObject toJSonId() {
		return unwrap(this).toJSonId();
	}

	@Override
	public Generic[] coerceToTArray(Object... array) {
		return wrap(unwrap(this).coerceToTArray(array));
	}

	@Override
	public Generic[] addThisToTargets(Generic... targets) {
		return wrap(unwrap(this).addThisToTargets(unwrap(targets)));
	}

	@Override
	public int getLevel() {
		return unwrap(this).getLevel();
	}

	@Override
	public boolean isMeta() {
		return unwrap(this).isMeta();
	}

	@Override
	public boolean isStructural() {
		return unwrap(this).isStructural();
	}

	@Override
	public boolean isConcrete() {
		return unwrap(this).isConcrete();
	}

	@Override
	public boolean inheritsFrom(Generic superVertex) {
		return unwrap(this).inheritsFrom(unwrap(superVertex));
	}

	@Override
	public boolean isInstanceOf(Generic metaVertex) {
		return unwrap(this).isInstanceOf(unwrap(metaVertex));
	}

	@Override
	public boolean isSpecializationOf(Generic vertex) {
		return unwrap(this).isSpecializationOf(unwrap(vertex));
	}

	@Override
	public Generic getInstance(Serializable value, Generic... components) {
		return wrap(unwrap(this).getInstance(value, unwrap(components)));
	}

	@Override
	public Generic getInstance(Generic superT, Serializable value, Generic... components) {
		return wrap(unwrap(this).getInstance(unwrap(superT), value, unwrap(components)));
	}

	@Override
	public Generic getInstance(List<Generic> supers, Serializable value, Generic... components) {
		return wrap(unwrap(this).getInstance(unwrap(supers), value, unwrap(components)));
	}

	@Override
	public boolean isCompositeOf(Generic vertex) {
		return unwrap(this).isCompositeOf(unwrap(vertex));
	}

	@Override
	public Snapshot<Generic> getAttributes() {
		return () -> unwrap(this).getAttributes().get().map(this::wrap);
	}

	@Override
	public Snapshot<Generic> getAttributes(int pos) {
		return () -> unwrap(this).getAttributes(pos).get().map(this::wrap);
	}

	@Override
	public Snapshot<Generic> getAttributes(Generic attribute) {
		return () -> unwrap(this).getAttributes(unwrap(attribute)).get().map(this::wrap);
	}

	@Override
	public Snapshot<Generic> getHolders(Generic attribute) {
		return () -> unwrap(this).getHolders(unwrap(attribute)).get().map(this::wrap);
	}

	@Override
	public Snapshot<Generic> getHolders(Generic attribute, int pos) {
		return () -> unwrap(this).getHolders(unwrap(attribute), pos).get().map(this::wrap);
	}

	@Override
	public Snapshot<Serializable> getValues(Generic attribute) {
		return () -> unwrap(this).getValues(unwrap(attribute)).get();
	}

	@Override
	public Snapshot<Serializable> getValues(Generic attribute, int pos) {
		return () -> unwrap(this).getValues(unwrap(attribute), pos).get();
	}

	@Override
	public Snapshot<Generic> getInstances() {
		return () -> unwrap(this).getInstances().get().map(this::wrap);
	}

	@Override
	public Snapshot<Generic> getAllInstances() {
		return () -> unwrap(this).getAllInstances().get().map(this::wrap);
	}

	@Override
	public Snapshot<Generic> getInheritings() {
		return () -> unwrap(this).getInheritings().get().map(this::wrap);
	}

	@Override
	public Snapshot<Generic> getAllInheritings() {
		return () -> unwrap(this).getAllInheritings().get().map(this::wrap);
	}

	@Override
	public Snapshot<Generic> getComposites() {
		return () -> unwrap(this).getComposites().get().map(this::wrap);
	}

	@Override
	public boolean isAncestorOf(Generic dependency) {
		return unwrap(this).isAncestorOf(unwrap(dependency));
	}

	@Override
	public String info() {
		return unwrap(this).info();
	}

	@Override
	public String detailedInfo() {
		return unwrap(this).detailedInfo();
	}

	@Override
	public String toPrettyString() {
		return unwrap(this).toPrettyString();
	}

	@Override
	public JsonObject toPrettyJSon() {
		return unwrap(this).toPrettyJSon();
	}

	@Override
	public Serializable getSystemPropertyValue(Class<? extends org.genericsystem.api.core.IVertex.SystemProperty> propertyClass, int pos) {
		return unwrap(this).getSystemPropertyValue(propertyClass, pos);
	}

	@Override
	public Generic setSystemPropertyValue(Class<? extends org.genericsystem.api.core.IVertex.SystemProperty> propertyClass, int pos, Serializable value, Generic... targets) {
		return wrap(unwrap(this).setSystemPropertyValue(propertyClass, pos, value, unwrap(targets)));
	}

	@Override
	public Generic enableSystemProperty(Class<? extends org.genericsystem.api.core.IVertex.SystemProperty> propertyClass, int pos, Generic... targets) {
		return wrap(unwrap(this).enableSystemProperty(propertyClass, pos, unwrap(targets)));
	}

	@Override
	public Generic disableSystemProperty(Class<? extends org.genericsystem.api.core.IVertex.SystemProperty> propertyClass, int pos, Generic... targets) {
		return wrap(unwrap(this).disableSystemProperty(propertyClass, pos, unwrap(targets)));
	}

	@Override
	public boolean isSystemPropertyEnabled(Class<? extends org.genericsystem.api.core.IVertex.SystemProperty> propertyClass, int pos) {
		return unwrap(this).isSystemPropertyEnabled(propertyClass, pos);
	}

	@Override
	public Generic enableReferentialIntegrity(int pos) {
		return wrap(unwrap(this).enableReferentialIntegrity(pos));
	}

	@Override
	public Generic disableReferentialIntegrity(int pos) {
		return wrap(unwrap(this).disableReferentialIntegrity(pos));
	}

	@Override
	public boolean isReferentialIntegrityEnabled(int pos) {
		return unwrap(this).isReferentialIntegrityEnabled(pos);
	}

	@Override
	public Generic enableSingularConstraint(int pos) {
		return wrap(unwrap(this).enableSingularConstraint(pos));
	}

	@Override
	public Generic disableSingularConstraint(int pos) {
		return wrap(unwrap(this).disableSingularConstraint(pos));
	}

	@Override
	public boolean isSingularConstraintEnabled(int pos) {
		return unwrap(this).isSingularConstraintEnabled(pos);
	}

	@Override
	public Generic enablePropertyConstraint() {
		return wrap(unwrap(this).enablePropertyConstraint());
	}

	@Override
	public Generic disablePropertyConstraint() {
		return wrap(unwrap(this).disablePropertyConstraint());
	}

	@Override
	public boolean isPropertyConstraintEnabled() {
		return unwrap(this).isPropertyConstraintEnabled();
	}

	@Override
	public Generic enableUniqueValueConstraint() {
		return wrap(unwrap(this).enableUniqueValueConstraint());
	}

	@Override
	public Generic disableUniqueValueConstraint() {
		return wrap(unwrap(this).disableUniqueValueConstraint());
	}

	@Override
	public boolean isUniqueValueEnabled() {
		return unwrap(this).isUniqueValueEnabled();
	}

	@Override
	public Class<?> getClassConstraint() {
		return unwrap(this).getClassConstraint();
	}

	@Override
	public Generic setClassConstraint(Class<?> constraintClass) {
		return wrap(unwrap(this).setClassConstraint(constraintClass));
	}

	@Override
	public Generic enableRequiredConstraint(int pos) {
		return wrap(unwrap(this).enableRequiredConstraint(pos));
	}

	@Override
	public Generic disableRequiredConstraint(int pos) {
		return wrap(unwrap(this).disableRequiredConstraint(pos));
	}

	@Override
	public boolean isRequiredConstraintEnabled(int pos) {
		return unwrap(this).isRequiredConstraintEnabled(pos);
	}

	@Override
	public Generic enableCascadeRemove(int pos) {
		return wrap(unwrap(this).enableCascadeRemove(pos));
	}

	@Override
	public Generic disableCascadeRemove(int pos) {
		return wrap(unwrap(this).disableCascadeRemove(pos));
	}

	@Override
	public boolean isCascadeRemove(int pos) {
		return unwrap(this).isCascadeRemove(pos);
	}

	@Override
	public void remove() {
		unwrap(this).remove();
	}

	@Override
	public Generic addInstance(Serializable value, Generic... components) {
		return wrap(unwrap(this).addInstance(value, unwrap(components)));
	}

	@Override
	public Generic addInstance(Generic override, Serializable value, Generic... components) {
		return wrap(unwrap(this).addInstance(unwrap(override), value, unwrap(components)));
	}

	@Override
	public Generic addInstance(List<Generic> overrides, Serializable value, Generic... components) {
		return wrap(unwrap(this).addInstance(unwrap(overrides), value, unwrap(components)));
	}

	@Override
	public Generic setInstance(Serializable value, Generic... components) {
		return wrap(unwrap(this).setInstance(value, unwrap(components)));
	}

	@Override
	public Generic setInstance(Generic override, Serializable value, Generic... components) {
		return wrap(unwrap(this).setInstance(unwrap(override), value, unwrap(components)));
	}

	@Override
	public Generic setInstance(List<Generic> overrides, Serializable value, Generic... components) {
		return wrap(unwrap(this).setInstance(unwrap(overrides), value, unwrap(components)));
	}

	@Override
	public Generic addRoot(Serializable value) {
		return wrap(unwrap(this).addRoot(value));
	}

	@Override
	public Generic setRoot(Serializable value) {
		return wrap(unwrap(this).setRoot(value));
	}

	@Override
	public Generic addNode(Serializable value) {
		return wrap(unwrap(this).addNode(value));
	}

	@Override
	public Generic setNode(Serializable value) {
		return wrap(unwrap(this).setNode(value));
	}

	@Override
	public Generic addInheritingNode(Serializable value) {
		return wrap(unwrap(this).addInheritingNode(value));
	}

	@Override
	public Generic setInheritingNode(Serializable value) {
		return wrap(unwrap(this).setInheritingNode(value));
	}

	@Override
	public Snapshot<Generic> getAllSubNodes() {
		return () -> unwrap(this).getAllSubNodes().get().map(this::wrap);
	}

	@Override
	public Snapshot<Generic> getSubNodes() {
		return () -> unwrap(this).getSubNodes().get().map(this::wrap);
	}

	@Override
	public Generic addAttribute(Serializable value, Generic... targets) {
		return wrap(unwrap(this).addAttribute(value, unwrap(targets)));
	}

	@Override
	public Generic addAttribute(Generic override, Serializable value, Generic... targets) {
		return wrap(unwrap(this).addAttribute(unwrap(override), value, unwrap(targets)));
	}

	@Override
	public Generic addAttribute(List<Generic> overrides, Serializable value, Generic... targets) {
		return wrap(unwrap(this).addAttribute(unwrap(overrides), value, unwrap(targets)));
	}

	@Override
	public Generic setAttribute(Serializable value, Generic... targets) {
		return wrap(unwrap(this).setAttribute(value, unwrap(targets)));
	}

	@Override
	public Generic setAttribute(Generic override, Serializable value, Generic... targets) {
		return wrap(unwrap(this).setAttribute(unwrap(override), value, unwrap(targets)));
	}

	@Override
	public Generic setAttribute(List<Generic> overrides, Serializable value, Generic... targets) {
		return wrap(unwrap(this).setAttribute(unwrap(overrides), value, unwrap(targets)));
	}

	@Override
	public Generic addHolder(Generic attribute, Serializable value, Generic... targets) {
		return wrap(unwrap(this).addHolder(unwrap(attribute), value, unwrap(targets)));
	}

	@Override
	public Generic addHolder(Generic attribute, Generic override, Serializable value, Generic... targets) {
		return wrap(unwrap(this).addHolder(unwrap(attribute), unwrap(override), value, unwrap(targets)));
	}

	@Override
	public Generic addHolder(Generic attribute, List<Generic> overrides, Serializable value, Generic... targets) {
		return wrap(unwrap(this).addHolder(unwrap(attribute), unwrap(overrides), value, unwrap(targets)));
	}

	@Override
	public Generic setHolder(Generic attribute, Serializable value, Generic... targets) {
		return wrap(unwrap(this).setHolder(unwrap(attribute), value, unwrap(targets)));
	}

	@Override
	public Generic setHolder(Generic attribute, Generic override, Serializable value, Generic... targets) {
		return wrap(unwrap(this).setHolder(unwrap(attribute), unwrap(override), value, unwrap(targets)));
	}

	@Override
	public Generic setHolder(Generic attribute, List<Generic> overrides, Serializable value, Generic... targets) {
		return wrap(unwrap(this).setHolder(unwrap(attribute), unwrap(overrides), value, unwrap(targets)));
	}

	@Override
	public Generic updateValue(Serializable newValue) {
		return wrap(unwrap(this).updateValue(newValue));
	}

	@Override
	public Generic updateSupers(Generic... overrides) {
		return wrap(unwrap(this).updateSupers(unwrap(overrides)));
	}

	@Override
	public Generic updateComposites(Generic... newComposites) {
		return wrap(unwrap(this).updateComposites(unwrap(newComposites)));
	}

	@Override
	public Generic update(List<Generic> overrides, Serializable newValue, Generic... newComposites) {
		return wrap(unwrap(this).update(unwrap(overrides), newValue, unwrap(newComposites)));
	}

	@Override
	public Generic update(Serializable newValue, Generic... newComposites) {
		return wrap(unwrap(this).update(newValue, unwrap(newComposites)));
	}

	@Override
	public Generic update(Generic override, Serializable newValue, Generic... newComposites) {
		return wrap(unwrap(this).update(unwrap(override), newValue, unwrap(newComposites)));
	}

	@Override
	public Generic getBaseComponent() {
		return wrap(unwrap(this).getBaseComponent());
	}

	@Override
	public Generic getTargetComponent() {
		return wrap(unwrap(this).getTargetComponent());
	}

	@Override
	public Generic getTernaryComponent() {
		return wrap(unwrap(this).getTernaryComponent());
	}

	@Override
	public Generic getComponent(int pos) {
		return wrap(unwrap(this).getComponent(pos));
	}

	@Override
	public Generic enableClassConstraint(Class<?> constraintClass) {
		return wrap(unwrap(this).enableClassConstraint(constraintClass));
	}

	@Override
	public Generic disableClassConstraint() {
		return wrap(unwrap(this).disableClassConstraint());
	}

	@Override
	public Cache getCurrentCache() {
		return engine.getCurrentCache();
	}

	@Override
	public String toString() {
		return unwrap(this).toString();
	}

}
