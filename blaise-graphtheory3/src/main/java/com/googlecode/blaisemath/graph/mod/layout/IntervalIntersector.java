package com.googlecode.blaisemath.graph.mod.layout;

/*
 * #%L
 * blaise-graphtheory3
 * --
 * Copyright (C) 2009 - 2019 Elisha Peterson
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

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.primitives.Doubles;
import java.util.List;
import java.util.Set;

/**
 * Computation that finds pairs of intersecting intervals within a larger set.
 * @author petereb1
 */
public class IntervalIntersector {
    
    private IntervalIntersector() {}

    /**
     * Compute all overlapping intervals, returning collisions by value objects.
     * @param intervals input list of intervals
     * @return set of overlapping pairs, by value object
     */
    public static List<TwoInts> findIntersections(List<Interval> intervals) {
        Set<Endpoint> endpoints = sortedEndpoints(intervals);
        Set<Interval> activeIntervals = Sets.newLinkedHashSet();
        List<TwoInts> result = Lists.newArrayList();
        for (Endpoint e : endpoints) {
            Interval interval = intervals.get(e.index);
            if (e.begin) {
                activeIntervals.forEach(a -> result.add(new TwoInts(a.index, interval.index)));
                activeIntervals.add(interval);
            } else {
                activeIntervals.remove(interval);
            }
        }
        return result;
    }

    /** Gets sorted set of endpoints corresponding to given intervals. */
    private static Set<Endpoint> sortedEndpoints(List<Interval> intervals) {
        Set<Endpoint> endpoints = Sets.newTreeSet();
        intervals.forEach(i -> {
            endpoints.add(i.begin);
            endpoints.add(i.end);
        });
        return endpoints;
    }
    
    /** Immutable set of two ints */
    public static class TwoInts {
        public final int x;
        public final int y;

        public TwoInts(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public int hashCode() {
            return x + y;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof TwoInts && ((TwoInts) obj).x == x && ((TwoInts) obj).y == y;
        }
    }
    
    /** Utility class for intervals with pairs of endpoints. */
    public static class Interval {
        private final int index;
        private final Endpoint begin;
        private final Endpoint end;

        public Interval(int index, double min, double max) {
            this.index = index;
            this.begin = new Endpoint(index, EndpointType.BEGIN, min);
            this.end = new Endpoint(index, EndpointType.END, max);
        }
        
        @Override
        public String toString() {
            return "["+begin.value+","+end.value+"]";
        }
    }

    /** Endpoint with type, sortable to ensure proper iteration order for above algorithm. */
    public static class Endpoint implements Comparable<Endpoint> {
        private int index;
        private EndpointType type;
        private double value;
        private boolean begin;
        private boolean end;

        public Endpoint(int index, EndpointType type, double value) {
            this.index = index;
            this.type = type;
            this.value = value;
            this.begin = type == EndpointType.BEGIN;
            this.end = type == EndpointType.END;
        }

        @Override
        public int compareTo(Endpoint o) {
            int c0 = Doubles.compare(value, o.value);
            return c0 == 0 ? type.compareTo(o.type) : c0;
        }
    }
    
    /** Utility enum endpoint type. */
    public static enum EndpointType {
        BEGIN, END
    }

}
