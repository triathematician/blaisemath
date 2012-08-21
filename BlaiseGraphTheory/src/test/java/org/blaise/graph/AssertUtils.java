package org.blaise.graph;


import java.util.Collection;
import static org.junit.Assert.*;

public class AssertUtils {
    /** Tests to see if all elements of one collection are contained in the other, and vice versa */
    public static void assertCollectionContentsSame(Collection expected, Collection found) {
        assertEquals(expected.size(), found.size());
        assertTrue(expected.containsAll(found));
        assertTrue(found.containsAll(expected));
    }
}
