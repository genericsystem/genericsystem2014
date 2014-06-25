package org.genericsystem.api.exception;

import org.genericsystem.api.core.Cache;

/**
 * Is triggered if current thread is not aware of current Cache
 * 
 * @see Cache start(Cache cache)
 * 
 * @author Nicolas Feybesse
 */
public class CacheAwareException extends RuntimeException {

	private static final long serialVersionUID = 1105784547365240043L;

	public CacheAwareException(String msg) {
		super(msg);
	}
}