package org.genericsystem.impl;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.genericsystem.api.core.Cache;
import org.genericsystem.api.core.Engine;
import org.genericsystem.api.core.Generic;
import org.genericsystem.api.map.MapProvider;
import org.genericsystem.api.model.Attribute;
import org.genericsystem.api.model.Holder;
import org.genericsystem.api.model.Link;
import org.genericsystem.api.model.Relation;
import org.genericsystem.api.model.Snapshot;
import org.genericsystem.api.model.Type;
import org.genericsystem.api.statics.RemoveStrategy;

/**
 * Default implementation of <tt>Generics</tt>.
 */
public class GenericImpl implements Generic, Type, Link, Relation, Holder, Attribute {

	private static final long serialVersionUID = -7421589436345797803L;

	private GenericService<? extends GenericService<?>> genericService;

	@Override
	public <T extends Generic> T enableSizeConstraint(int basePos, Integer size) {
		// old :
		// setConstraintValue(SizeConstraintImpl.class, basePos, size);
		// return (T) this;
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Generic> T disableSizeConstraint(int basePos) {
		// old :
		// setConstraintValue(SizeConstraintImpl.class, basePos, false);
		// return (T) this;
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Type> T enableSingularConstraint() {
		return (T) genericService.enableSingularConstraint(Statics.BASE_POSITION);
	}

	@Override
	public <T extends Type> T disableSingularConstraint() {
		return (T) genericService.disableSingularConstraint(Statics.BASE_POSITION);
	}

	@Override
	public <T extends Type> T enableSingularConstraint(int componentPos) {
		return (T) genericService.enableSingularConstraint(componentPos);
	}

	@Override
	public <T extends Type> T disableSingularConstraint(int componentPos) {
		return (T) genericService.disableSingularConstraint(componentPos);
	}

	@Override
	public <T extends Type> T enablePropertyConstraint() {
		return (T) genericService.enablePropertyConstraint();
	}

	@Override
	public <T extends Type> T disablePropertyConstraint() {
		return (T) genericService.disablePropertyConstraint();
	}

	@Override
	public <T extends Type> T enableRequiredConstraint() {
		return (T) genericService.enableRequiredConstraint(Statics.BASE_POSITION);
	}

	@Override
	public <T extends Type> T disableRequiredConstraint() {
		return (T) genericService.disableRequiredConstraint(Statics.BASE_POSITION);
	}

	@Override
	public <T extends Type> T enableRequiredConstraint(int componentPos) {
		return (T) genericService.enableRequiredConstraint(componentPos);
	}

	@Override
	public <T extends Type> T disableRequiredConstraint(int componentPos) {
		return (T) genericService.disableRequiredConstraint(componentPos);
	}

	@Override
	public <T extends Type> T enableUniqueValueConstraint() {
		// old
		// setConstraintValue(UniqueValueConstraintImpl.class, Statics.MULTIDIRECTIONAL, true);
		// return (T) this;
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Type> T disableUniqueValueConstraint() {
		// old
		// setConstraintValue(UniqueValueConstraintImpl.class, Statics.MULTIDIRECTIONAL, false);
		// return (T) this;
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer getSizeConstraint(int basePos) {
		// old
		// Serializable result = getConstraintValue(SizeConstraintImpl.class, basePos);
		// return result instanceof Integer ? (Integer) result : null;
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isSingularConstraintEnabled() {
		return genericService.isSingularConstraintEnabled(Statics.BASE_POSITION);
	}

	@Override
	public boolean isSingularConstraintEnabled(int componentPos) {
		return genericService.isSingularConstraintEnabled(componentPos);
	}

	@Override
	public boolean isPropertyConstraintEnabled() {
		// old
		// return isConstraintEnabled(PropertyConstraintImpl.class, Statics.MULTIDIRECTIONAL);
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isRequiredConstraintEnabled() {
		return genericService.isRequiredConstraintEnabled(Statics.BASE_POSITION);
	}

	@Override
	public boolean isRequiredConstraintEnabled(int componentPos) {
		return genericService.isRequiredConstraintEnabled(componentPos);
	}

	@Override
	public boolean isUniqueValueConstraintEnabled() {
		// old
		// return isConstraintEnabled(UniqueValueConstraintImpl.class, Statics.MULTIDIRECTIONAL);
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public <T extends Generic> T getBaseComponent() {
		// old
		// return genericService.getComponent(Statics.BASE_POSITION);
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Generic> T getComponent(int basePos) {
		// old
		// return genericService.getComponent(basePos);
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Relation> T enableCascadeRemove(int componentPos) {
		return (T) genericService.enableCascadeRemove(componentPos);
	}

	@Override
	public <T extends Relation> T disableCascadeRemove(int componentPos) {
		return (T) genericService.disableCascadeRemove(componentPos);
	}

	@Override
	public boolean isCascadeRemove(int componentPos) {
		return genericService.isCascadeRemove(componentPos);
	}

	@Override
	public <T extends Generic> T getTargetComponent() {
		// old
		// return genericService.getComponent(Statics.TARGET_COMPONENT);
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Generic addSubType(Serializable value) {
		// FIXME
		// return (Generic) genericService.addInstance(value);
		return null;
	}

	@Override
	public <T extends Type> T addSubType(Serializable value, Generic[] satifies, Generic... components) {
		// old
		// if (isMeta())
		// getCurrentCache().rollback(new UnsupportedOperationException("Derive a meta is not allowed"));
		// return getCurrentCache().bind(getMeta(), value, null, this, satifies, true, components);
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Generic addAttribute(Serializable value, Generic... targets) {
		// old
		// return addHolder((Holder) getEngine(), value, Statics.BASE_POSITION, Statics.STRUCTURAL, targets);
		// TODO Auto-generated method stub
		return null;
		// return (Generic) genericService.addInstance(value, targets);
	}

	@Override
	public Generic addProperty(Serializable value, Generic... targets) {
		return ((Attribute) addAttribute(value, targets)).enablePropertyConstraint();
	}

	@Override
	public Generic addRelation(Serializable value, Generic... bounds) {
		return addAttribute(value, bounds);
	}

	@Override
	public <T extends Generic> T addInstance(Serializable value, Generic... components) {
		// old
		// return getCurrentCache().bind(this, value, null, this, Statics.EMPTY_GENERIC_ARRAY, true, components);
		// TODO Auto-generated method stub
		// return (T) genericService.addInstance(value, components);
		return null;
	}

	@Override
	public <T extends Generic> T addAnonymousInstance(Generic... components) {
		// old
		// return addInstance(getEngine().pickNewAnonymousReference(), components);
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Type> T getSubType(Serializable value) {
		// old
		// return unambigousFirst(this.<T> getSubTypesSnapshot().filter(next -> Objects.equals(value, next.getValue())));
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Generic getAttribute(Serializable value, Generic... holders) {
		// old
		// return getAttributes(getCurrentCache().getMetaAttribute());
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Generic getProperty(Serializable value, Generic... holders) {
		return getAttribute(value, holders);
	}

	@Override
	public Generic getRelation(Serializable value, Generic... bounds) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Generic getRelation(Generic... bounds) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Type> Snapshot<T> getSubTypes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Type> Snapshot<T> getAllSubTypes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Type> Snapshot<T> getAllSubTypes(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Type> T getAllSubType(Serializable value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Attribute> Snapshot<T> getAttributes(Attribute attribute) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Type> T setSubType(Serializable value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Type> T setSubType(Serializable value, Generic[] satifies, Generic... components) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Attribute> T setAttribute(Serializable value, Generic... targets) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Attribute> T setProperty(Serializable value, Generic... targets) {
		return setAttribute(value, targets).enablePropertyConstraint();
	}

	@Override
	public <T extends Relation> T setRelation(Serializable value, Generic... bounds) {
		return setAttribute(value, bounds);
	}

	@Override
	public <T extends Generic> T setInstance(Serializable value, Generic... components) {
		// old
		// return getCurrentCache().bind(this, value, null, this, Statics.EMPTY_GENERIC_ARRAY, false, components);
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Generic> T setAnonymousInstance(Generic... components) {
		// old
		// return setInstance(getEngine().pickNewAnonymousReference(), components);
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Relation> T enableInheritance() {
		// old
		// setSystemPropertyValue(NoInheritanceProperty.class, Statics.BASE_POSITION, false);
		// return (T) this;
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Relation> T disableInheritance() {
		// old
		// setSystemPropertyValue(NoInheritanceProperty.class, Statics.BASE_POSITION, true);
		// return (T) this;
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isInheritanceEnabled() {
		// old
		// if (!GenericImpl.class.equals(getClass()))
		// return !getClass().isAnnotationPresent(NoInheritance.class);
		// return !isSystemPropertyEnabled(NoInheritanceProperty.class, Statics.BASE_POSITION);
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public <T extends Type> T enableSingletonConstraint() {
		// old
		// setConstraintValue(SingletonConstraintImpl.class, Statics.MULTIDIRECTIONAL, true);
		// return (T) this;
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Type> T disableSingletonConstraint() {
		// old
		// setConstraintValue(SingletonConstraintImpl.class, Statics.MULTIDIRECTIONAL, false);
		// return (T) this;
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isSingletonConstraintEnabled() {
		// old
		// return genericService.isConstraintEnabled(SingletonConstraintImpl.class, Statics.MULTIDIRECTIONAL);
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public <T extends Type> T enableVirtualConstraint() {
		// old
		// setConstraintValue(VirtualConstraintImpl.class, Statics.MULTIDIRECTIONAL, true);
		// return (T) this;
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Type> T disableVirtualConstraint() {
		// old
		// setConstraintValue(VirtualConstraintImpl.class, Statics.MULTIDIRECTIONAL, false);
		// return (T) this;
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isVirtualConstraintEnabled() {
		// old
		// return isConstraintEnabled(VirtualConstraintImpl.class, Statics.MULTIDIRECTIONAL);
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Class<?> getConstraintClass() {
		// old
		// Serializable value = getConstraintValue(InstanceClassConstraintImpl.class, Statics.MULTIDIRECTIONAL);
		// return null == value ? Object.class : (Class<?>) value;
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Type> T setConstraintClass(Class<?> constraintClass) {
		// old
		// setConstraintValue(InstanceClassConstraintImpl.class, Statics.MULTIDIRECTIONAL, constraintClass);
		// return (T) this;
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isSystem() {
		// old
		// return getClass().isAnnotationPresent(SystemGeneric.class);
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isInstance() {
		// old
		// return getMetaLevel() - meta.getMetaLevel() == 1 ? this.inheritsFrom(meta) : false;
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isType() {
		return genericService.getComponents().size() == 0;
	}

	@Override
	public boolean isAttribute() {
		return genericService.getComponents().size() >= 1;
	}

	@Override
	public boolean isRelation() {
		return genericService.getComponents().size() > 1;
	}

	@Override
	public boolean isAttributeOf(Generic generic) {
		// old
		// for (Generic component : getComponents())
		// if (generic.inheritsFrom(component))
		// return true;
		// return false;

		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Generic addInstance(Serializable value) {
		// FIXME
		// return (Generic) genericService.addInstance(value);
		return null;
	}

	@Override
	public Generic updateValue(Serializable value) {
		// FIXME
		// return (Generic) genericService.update(value);
		return null;
	}

	@Override
	public Serializable getValue() {
		return genericService.getValue();
	}

	@Override
	public Generic getInstance(Serializable value, Generic... holders) {
		// old
		// for (Generic component : getComponents())
		// if (generic.inheritsFrom(component))
		// return true;
		// return false;
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Generic getSubType(Serializable value, Generic... holders) {
		// old
		// return unambigousFirst(this.<T> getSubTypesSnapshot().filter(next -> Objects.equals(value, next.getValue())));
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Snapshot<Generic> getAttributes() {
		// old
		// return getAttributes(getCurrentCache().getMetaAttribute());
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Snapshot<Generic> getRelations() {
		return getAttributes();
	}

	@Override
	public Snapshot<Generic> getInstances() {
		// old
		// return () -> instancesIterator();
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Snapshot<Generic> getAllInstances() {
		// old
		// return this.<T> allInheritingsAboveSnapshot().filter(next -> next.getMetaLevel() == getMetaLevel() + 1);
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Snapshot<Generic> getInheritings() {
		// old
		// return () -> getCurrentCache().inheritingsIterator(this);
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Snapshot<Generic> getSupers() {
		// old
		// return vertex.supers();
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isAlive() {
		return getCurrentCache().isAlive(this);
	}

	@Override
	public void remove() {
		remove(RemoveStrategy.NORMAL);
	}

	@Override
	public void remove(RemoveStrategy removeStrategy) {
		genericService.remove(Statics.convert(removeStrategy));
	}

	@Override
	public Generic remove(Generic... toRemove) {
		// TODO Auto-generated method stub
		// update todo here, approximately :
		// genericService.update(getSupers(), getValue(), getComponents().remove(toRemove);)
		return null;
	}

	@Override
	public Cache getCurrentCache() {
		return getEngine().getCurrentCache();
	}

	@Override
	public Engine getEngine() {
		// old
		// return (EngineImpl) getSupers().get(0).getEngine();
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <S extends Serializable> S getValue(Holder holder) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Serializable> Snapshot<T> getValues(Holder holder) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isEngine() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isInstanceOf(Generic meta) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getMetaLevel() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isAttributeOf(Generic generic, int basePos) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public <T extends Holder> T flag(Holder attribute, Generic... targets) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Link> T bind(Link relation, Generic... targets) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Link> T getLink(Link relation, int basePos, Generic... targets) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Link> T getLink(Link relation, Generic... targets) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Link> Snapshot<T> getLinks(Relation relation, int basePos, Generic... targets) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Link> Snapshot<T> getLinks(Relation relation, Generic... targets) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Link> T addLink(Link relation, Serializable value, Generic... targets) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Link> T setLink(Link relation, Serializable value, Generic... targets) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Link> T setLink(Link relation, Serializable value, int basePos, Generic... targets) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Link> T setLink(Link relation, Serializable value, int basePos, int metaLevel, Generic... targets) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Holder> T getHolder(Holder structural, Generic... targets) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Holder> T getHolder(Holder structural, int basePos, Generic... targets) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Holder> T getHolder(Holder structural, int basePos, int metaLevel, Generic... targets) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Holder> T addHolder(Holder structural, Serializable value, Generic... targets) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Holder> T addHolder(Holder attribute, int basePos, Serializable value, Generic... targets) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Holder> T addHolder(Holder attribute, Serializable value, int basePos, int metaLevel, Generic... targets) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Holder> T setHolder(Holder attribute, Serializable value, Generic... targets) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Holder> T setHolder(Holder attribute, Serializable value, int basePos, Generic... targets) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Holder> T setHolder(Holder attribute, Serializable value, int metaLevel, int basePos, Generic... targets) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Holder> Snapshot<T> getHolders(Holder attribute, Generic... targets) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Holder> Snapshot<T> getHolders(Holder structural, int basePos, Generic... targets) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Generic> Snapshot<T> getTargets(Relation relation) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Generic> Snapshot<T> getTargets(Relation relation, int basePos, int targetPos) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean inheritsFrom(Generic generic) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean inheritsFromAll(Generic... generics) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public <T extends Generic> T enableReferentialIntegrity() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Generic> T disableReferentialIntegrity() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Generic> T enableReferentialIntegrity(int componentPos) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Generic> T disableReferentialIntegrity(int componentPos) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isReferentialIntegrity() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isReferentialIntegrity(int componentPos) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public <T extends Generic> List<T> getComponents() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getBasePos(Holder attribute) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public <T extends Generic> T getMeta() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Generic> Snapshot<T> getComposites() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isStructural() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isConcrete() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isMeta() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isMapProvider() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isTree() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isNode() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isRoot() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isRemovable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void log() {
		// TODO Auto-generated method stub

	}

	@Override
	public String info() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void cancel(Holder holder) {
		// TODO Auto-generated method stub

	}

	@Override
	public void cancelAll(Holder holder, Generic... targets) {
		// TODO Auto-generated method stub

	}

	@Override
	public void clear(Holder holder) {
		// TODO Auto-generated method stub

	}

	@Override
	public void clearAll(Holder holder, Generic... targets) {
		// TODO Auto-generated method stub

	}

	@Override
	public <Key extends Serializable, Value extends Serializable> Map<Key, Value> getMap(Class<? extends MapProvider> mapClass) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <Key extends Serializable, Value extends Serializable> Map<Key, Value> getPropertiesMap() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Generic> T addComponent(Generic component, int pos) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Generic> T removeComponent(Generic component, int pos) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Generic> T addSuper(Generic newSuper) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Generic> T removeSuper(int pos) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Generic> Snapshot<T> getOtherTargets(Holder holder) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Generic> T removeComponent(Generic component) {
		// TODO Auto-generated method stub
		return null;
	}

}
