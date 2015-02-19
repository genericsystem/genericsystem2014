package org.genericsystem.kernel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NavigableSet;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.genericsystem.api.defaults.DefaultVertex;

public class RemoveHandler<T extends DefaultVertex<T>> extends GenericHandler<T> {

	private final T toRebuild;
	private final Supplier<T> rebuilder;
	private final NavigableSet<T> dependenciesToRebuild;

	RemoveHandler(Builder<T> builder, T toRebuild, Supplier<T> rebuilder, NavigableSet<T> dependenciesToRebuild) {
		super(builder);
		this.toRebuild = toRebuild;
		this.rebuilder = rebuilder;
		this.dependenciesToRebuild = dependenciesToRebuild;
	}

	T rebuildAll() {
		dependenciesToRebuild.descendingSet().forEach(builder.getContext()::unplug);
		if (rebuilder == null)
			return null;
		ConvertMap convertMap = new ConvertMap();
		T build = rebuilder.get();
		if (toRebuild != null) {
			dependenciesToRebuild.remove(toRebuild);
			convertMap.put(toRebuild, build);
		}
		dependenciesToRebuild.forEach(x -> convertMap.convert(x));
		return build;
	}

	public class ConvertMap extends HashMap<T, T> {
		private static final long serialVersionUID = 5003546962293036021L;

		public T convert(T oldDependency) {
			if (oldDependency.isAlive())
				return oldDependency;
			T newDependency = get(oldDependency);
			if (newDependency == null) {
				if (oldDependency.isMeta()) {
					assert oldDependency.getSupers().size() == 1;
					newDependency = builder.setMeta(oldDependency.getComponents().size());
				} else {
					List<T> overrides = reasignSupers(oldDependency, new ArrayList<>());
					List<T> components = reasignComponents(oldDependency);
					T meta = reasignMeta(components, convert(oldDependency.getMeta()));
					newDependency = GenericHandlerFactory.newHandlerWithComputeSupers(builder, oldDependency.getClass(), meta, overrides, oldDependency.getValue(), components).getOrBuild();
				}
				put(oldDependency, newDependency);// triggers mutation
			}
			return newDependency;
		}

		private List<T> reasignSupers(T oldDependency, List<T> supersReasign) {
			for (T ancestor : oldDependency.getSupers().stream().map(x -> convert(x)).collect(Collectors.toList()))
				if (!ancestor.isAlive())
					reasignSupers(ancestor, supersReasign);
				else
					supersReasign.add(ancestor);
			return supersReasign;
		}

		private List<T> reasignComponents(T oldDependency) {
			return oldDependency.getComponents().stream().map(x -> convert(x)).filter(x -> x.isAlive()).collect(Collectors.toList());
		}

		private T reasignMeta(List<T> components, T meta) {
			if (components.size() != meta.getComponents().size())
				return reasignMeta(components, meta.getSupers().get(0));
			return meta;
		}

		@Override
		public T put(T oldDependency, T newDependency) {
			T result = super.put(oldDependency, newDependency);
			builder.getContext().triggersMutation(oldDependency, newDependency);
			return result;
		}
	}

}
