package org.genericsystem.cache;

import java.util.List;

public class SystemCache<T extends AbstractGeneric<T>> extends org.genericsystem.kernel.SystemCache<T>  {

	public SystemCache(DefaultEngine<T> root, Class<?> rootClass) {
		super(root, rootClass);
	}
	
	public void mount(List<Class<?>> systemClasses, Class<?>... userClasses) {
		Cache<T> cache = ((DefaultEngine<T>) root).newCache().start();
		super.mount(systemClasses, userClasses);
		cache.flush();
	}

}
