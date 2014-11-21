package org.genericsystem.mutability;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.genericsystem.api.core.IContext;
import org.genericsystem.api.core.IRoot;
import org.genericsystem.api.core.Snapshot;
import org.genericsystem.kernel.DefaultVertex;

public class Generic implements DefaultVertex<Generic> {
	protected Engine engine;

	@Override
	public Generic getInstance(List<Generic> supers, Serializable value, Generic... components) {
		return null;
	}

	// private org.genericsystem.concurrency.Generic[] convert(Generic... generics){
	// engine.getCache().get(mutable)
	// }

	@Override
	public Generic[] addThisToTargets(Generic... targets) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Serializable getSystemPropertyValue(Class<? extends org.genericsystem.api.core.IVertex.SystemProperty> propertyClass, int pos) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IRoot<Generic> getRoot() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Generic[] coerceToTArray(Object... array) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IContext<Generic> getCurrentCache() {
		return engine.getCurrentCache();
	}

	@Override
	public Snapshot<Generic> getHolders(Generic attribute) {
		return null;
	};

	@Override
	public Generic setInstance(List<Generic> overrides, Serializable value, Generic... components) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Snapshot<Generic> getAttributes(Generic attribute) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Generic update(List<Generic> overrides, Serializable newValue, Generic... newComposites) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Generic setSystemPropertyValue(java.lang.Class<? extends SystemProperty> propertyClass, int pos, Serializable value, Generic[] targets) {
		return engine;
	};

	Generic(Engine engine) {
		this.engine = engine;
	}

	@Override
	public Snapshot<Generic> getInstances() {
		return () -> engine.getCache().get(this).getInstances().get().map(x -> engine.getCache().getByValue(x));
	}

	@Override
	public Snapshot<Generic> getInheritings() {
		return () -> engine.getCache().get(this).getInheritings().get().map(x -> engine.getCache().getByValue(x));
	}

	@Override
	public Snapshot<Generic> getComposites() {
		return () -> engine.getCache().get(this).getComposites().get().map(x -> engine.getCache().getByValue(x));
	}

	@Override
	public boolean isAlive() {
		return engine.getCache().get(this).isAlive();
	}

	@Override
	public Generic addInstance(List<Generic> override, Serializable value, Generic... components) {
		org.genericsystem.concurrency.Generic genericT = engine
				.getCache()
				.get(this)
				.addInstance(override.stream().map(x -> engine.getCache().get(x)).collect(Collectors.toList()), value,
						engine.getCache().get(engine).coerceToTArray(Arrays.asList(components).stream().map(x -> engine.getCache().get(x)).collect(Collectors.toList()).toArray()));
		Generic genericM = new Generic(engine);
		engine.getCache().put(genericM, genericT);
		return genericM;
	}

	// TODO: surcharge addInstance(Generic override, Serializable value, Generic... components)
	@Override
	public Generic addInstance(Serializable value, Generic... components) {
		org.genericsystem.concurrency.Generic genericT = engine.getCache().get(this)
				.addInstance(value, engine.getCache().get(engine).coerceToTArray(Arrays.asList(components).stream().map(x -> engine.getCache().get(x)).collect(Collectors.toList()).toArray()));
		Generic genericM = new Generic(engine);
		engine.getCache().put(genericM, genericT);
		return genericM;
	}

	@Override
	public Generic getMeta() {
		return engine.getCache().getByValue(engine.getCache().get(this).getMeta());
	}

	@Override
	public List<Generic> getSupers() {
		List<Generic> genericsM = new LinkedList<>();
		engine.getCache().get(this).getSupers().stream().map(x -> genericsM.add(engine.getCache().getByValue(x)));
		return genericsM;
	}

	@Override
	public Serializable getValue() {
		return engine.getCache().get(this).getValue();
	}

	@Override
	public List<Generic> getComponents() {
		List<Generic> genericsM = new LinkedList<>();
		engine.getCache().get(this).getComponents().stream().map(x -> genericsM.add(engine.getCache().getByValue(x)));
		return genericsM;
	}

	@Override
	public void remove() {

	}

	@Override
	public Generic updateValue(Serializable newValue) {
		engine.getCache().put(this, engine.getCache().get(this).updateValue(newValue));
		return this;
	}

	public Generic update(Generic override, Serializable newValue, Generic... components) {
		engine.getCache().put(
				this,
				engine.getCache()
						.get(this)
						.update(Collections.singletonList(engine.getCache().get(override)), newValue,
								engine.getCache().get(engine).coerceToTArray(Arrays.asList(components).stream().map(x -> engine.getCache().get(x)).collect(Collectors.toList()).toArray())));
		return this;
	}
}
