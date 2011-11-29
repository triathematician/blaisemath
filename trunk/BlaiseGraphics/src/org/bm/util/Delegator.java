/*
 * Delegator.java
 * Created Sep 29, 2011
 */
package org.bm.util;

/**
 * Allows converting an object of one type to another on demand.
 * 
 * @author elisha
 */
public interface Delegator<Src, Rend> {
    
    /**
     * Returns the delegator associated with given source object
     * @param src the source object
     * @return delegate object
     */
    public Rend of(Src src);
    
}
