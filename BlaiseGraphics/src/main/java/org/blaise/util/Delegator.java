/*
 * Delegator.java
 * Created Sep 29, 2011
 */
package org.blaise.util;

/**
 * Allows converting an object of one type to another on demand.
 *
 * @param <Src> source type of delegator
 * @param <Value> value type of delegator
 *
 * @author elisha
 *
 * @see NonDelegator
 */
public interface Delegator<Src, Value> {

    /**
     * Returns the delegator associated with given source object
     * @param src the source object
     * @return delegate object
     */
    public Value of(Src src);

}
