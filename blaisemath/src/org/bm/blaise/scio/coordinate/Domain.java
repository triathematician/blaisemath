/*
 * Domain.java
 * Created on Nov 18, 2009
 */

package org.bm.blaise.scio.coordinate;

/**
 * <p>
 *   Provides a method describing whether a point of specified type is in the domain.
 * </p>
 * @author Elisha Peterson
 */
public interface Domain<C> {

    /**
     * Determines whether provided coordinate is in the domain
     * @param coord coordinate
     * @return <code>true</code> if <code>coord</code> is in the domain, otherwise false
     */
    public boolean contains(C coord);

}
