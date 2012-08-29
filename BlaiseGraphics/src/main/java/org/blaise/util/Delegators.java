/**
 * Delegators.java
 * Created Aug 29, 2012
 */

package org.blaise.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 *  Utility class for delegator objects.
 * </p>
 * @author elisha
 */
public class Delegators {
    
    /**
     * Applies a delegator to a set, returning a key-value map of results
     * @param del delegator to apply
     * @param set set of objects
     * @return map associating objects with delegated values
     */
    public static <A,B> Map<A,B> apply(Delegator<A,B> del, Set<? extends A> set) {
        Map<A,B> res = new HashMap<A,B>();
        for (A a : set) {
            res.put(a, del.of(a));
        }
        return res;
    }
    
}
