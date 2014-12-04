package com.googlecode.blaisemath.graph;

/*
 * #%L
 * BlaiseGraphTheory
 * --
 * Copyright (C) 2009 - 2014 Elisha Peterson
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


import java.util.Collection;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AssertUtils {
    /** Tests to see if all elements of one collection are contained in the other, and vice versa */
    public static void assertCollectionContentsSame(Collection expected, Collection found) {
        assertEquals("Collection size mismatch. Expected "+expected+" but was "+found, expected.size(), found.size());
        assertTrue("Collection contents mismatch. Expected "+expected+" but was "+found, expected.containsAll(found));
        assertTrue("Collection contents mismatch. Expected "+expected+" but was "+found, found.containsAll(expected));
    }
}
