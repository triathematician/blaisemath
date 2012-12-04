/*
 * NonDelegator.java
 * Created on Nov 29, 2011
 */
package org.blaise.util;

/**
 * Implementation of {@link Delegator} that returns the same value no matter what.
 *
 * @param <Src> source type of delegator
 * @param <Value> value type of delegator
 *
 * @author petereb1
 */
public final class NonDelegator<Src,Value> implements Delegator<Src,Value> {

    /** The value */
    private Value value;

    /**
     * Construct the delegator for the specified value.
     * @param value value to return.
     */
    public NonDelegator(Value value) {
        this.value = value;
    }

    public Value getValue() {
        return value;
    }

    public void setValue(Value value) {
        this.value = value;
    }

    public Value of(Src src) {
        return value;
    }

}
