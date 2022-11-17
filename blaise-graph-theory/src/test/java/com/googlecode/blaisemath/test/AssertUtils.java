package com.googlecode.blaisemath.test;

/*
 * #%L
 * BlaiseGraphTheory
 * --
 * Copyright (C) 2009 - 2022 Elisha Peterson
 * --
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import static org.junit.Assert.*;

public class AssertUtils {

    /** Tests to see if all elements of one set are contained in the other, and vice versa. */
    public static <X> void assertSets(Collection<Set<X>> sets, Set... test) {
        assertCollectionContentsSame(Arrays.asList(test), sets);
    }

    /** Tests to see if all elements of one collection are contained in the other, and vice versa. */
    public static<X> void assertCollectionContentsSame(Collection<X> expected, Collection<X> found) {
        assertEquals("Collection size mismatch. Expected "+expected+" but was "+found, expected.size(), found.size());
        assertTrue("Collection contents mismatch. Expected "+expected+" but was "+found, expected.containsAll(found));
        assertTrue("Collection contents mismatch. Expected "+expected+" but was "+found, found.containsAll(expected));
    }

    /** Tests to see if runnable throws an exception. */
    public static void assertIllegalArgumentException(Runnable r) {
        try {
            r.run();
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException x) {
            // passes
        }
    }
}
