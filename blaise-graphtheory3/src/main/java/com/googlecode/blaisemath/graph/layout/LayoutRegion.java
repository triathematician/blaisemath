/**
 * LayoutRegion.java
 * Created Jan 16, 2016
 */
package com.googlecode.blaisemath.graph.layout;

/*
 * #%L
 * BlaiseGraphTheory (v3)
 * --
 * Copyright (C) 2009 - 2018 Elisha Peterson
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


import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.awt.geom.Point2D;
import java.util.Map;
import java.util.Set;

/**
 * Utility class for decomposing a graph background into separate regions.
 *
 * @author Elisha Peterson
 */
class LayoutRegion<C> {

    private final Map<C,Point2D.Double> points = Maps.newHashMap();
    private final Set<LayoutRegion<C>> adjacentRegions = Sets.newLinkedHashSet();
    
    Set<C> points() {
        return points.keySet();
    }

    Iterable<Map.Entry<C, Point2D.Double>> entries() {
        return points.entrySet();
    }

    void clear() {
        points.clear();
    }
    
    Point2D.Double get(C io) {
        return points.get(io);
    }
    
    void put(C io, Point2D.Double iLoc) {
        points.put(io, iLoc);
    }

    Set<LayoutRegion<C>> adjacentRegions() {
        return adjacentRegions;
    }
    
    void addAdjacentRegion(LayoutRegion<C> reg) {
        adjacentRegions.add(reg);
    }

}
