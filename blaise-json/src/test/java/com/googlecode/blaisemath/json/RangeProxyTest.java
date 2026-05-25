package com.googlecode.blaisemath.json;

/*-
 * #%L
 * blaise-json
 * --
 * Copyright (C) 2019 - 2026 Elisha Peterson
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

import com.google.common.collect.BoundType;
import com.google.common.collect.Range;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class RangeProxyTest {

    @Test
    public void testToRange() {
        RangeProxy rp = new RangeProxy();
        rp.setMin(1);
        rp.setMinType(BoundType.OPEN);
        rp.setMax(2);
        rp.setMaxType(BoundType.CLOSED);
        assertEquals(Range.openClosed(1, 2), rp.toRange());
    }

    @Test
    public void testCycle() {
        testRecycle(Range.greaterThan(1));
        testRecycle(Range.lessThan(1));
        testRecycle(Range.openClosed(1, 2));
        testRecycle(Range.singleton(1));
        testRecycle(Range.closed(1, 2));
        assertException(() -> new RangeProxy(Range.<Integer>all()).toRange());
    }

    private void testRecycle(Range r) {
        assertEquals(r, new RangeProxy(r).toRange());
    }

    private void assertException(Runnable r) {
        try {
            r.run();
            fail("Should throw exception");
        } catch (Exception x) {
            // expected
        }
    }

    @Test
    public void testCreate() {
        RangeProxy rp = new RangeProxy(Range.openClosed(1, 2));
        assertEquals(1, rp.getMin());
        assertEquals(2, rp.getMax());
        assertEquals(BoundType.OPEN, rp.getMinType());
        assertEquals(BoundType.CLOSED, rp.getMaxType());
    }

    @Test
    public void testRoundtrip() throws IOException {
        assertRoundtrip(Range.greaterThan(1));
        assertRoundtrip(Range.lessThan(1));
        assertRoundtrip(Range.openClosed(1, 2));
        assertRoundtrip(Range.singleton(1));
        assertRoundtrip(Range.closed(1, 2));
        assertRoundtrip(Range.open(1, 5));
        assertRoundtrip(Range.closedOpen(0, 10));
    }

    @SuppressWarnings("rawtypes")
    private void assertRoundtrip(Range r) throws IOException {
        String json = BlaiseJson.allMapper().writeValueAsString(r);
        assertEquals(r, BlaiseJson.allMapper().readValue(json, Range.class));
    }

}
