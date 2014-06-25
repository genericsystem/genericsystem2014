package org.genericsystem.api.statics;

import org.genericsystem.api.core.Engine;

/**
 * Strategy used when removing elements of GenericSystem.
 * 
 * <tt>RemoveStrategy</tt> has three options :
 * <ul>
 * <li>NORMAL : default remove. Removes the element(s) specified as long as it doesn't break or remove anything else,</li>
 * <li>FORCE : Removes the element(s) specified, removing the elements involved in cascade,</li>
 * <li>CONSERVE : removes the element(s) specified, trying to resolve all the other elements broken and which should have been removed with a complex algorithm. Shouldn't be used by default, this is surgical strike.</li>
 * </ul>
 * 
 * @see Engine
 */
public enum RemoveStrategy {

	NORMAL, FORCE, CONSERVE;

}
