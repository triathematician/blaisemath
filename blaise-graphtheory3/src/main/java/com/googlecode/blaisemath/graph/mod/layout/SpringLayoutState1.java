package com.googlecode.blaisemath.graph.mod.layout;

/*
 * #%L
 * BlaiseGraphTheory (v3)
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


import com.googlecode.blaisemath.graph.IterativeGraphLayoutState;
import java.awt.geom.Point2D;
import java.util.Map;
import java.util.logging.Logger;
import javax.annotation.concurrent.ThreadSafe;

/**
 * <p>
 *   State object for spring layout. This tracks node locations and velocities,
 *   and divides node space up into regions to allow for more efficient
 *   layout calculations.
 * </p>
 * <p>
 *   This class may be safely modified by multiple threads simultaneously.
 * </p>
 * @param <C> graph node type
 * @author elisha
 */
@ThreadSafe
public final class SpringLayoutState1<C> extends IterativeGraphLayoutState<C> {
    
    //<editor-fold defaultstate="collapsed" desc="STATIC CONSTANTS">
    
    private static final Logger LOG = Logger.getLogger(SpringLayoutState1.class.getName());
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="LOCATION UPDATES">

    public Map<C, Point2D.Double> getLoc() {
        return loc;
    }
    
    Point2D.Double getLoc(C io) {
        return loc.get(io);
    }
    
    void putLoc(C io, Point2D.Double pt) {
        loc.put(io, pt);
    }
    
    Point2D.Double getVel(C io) {
        return vel.get(io);
    }
    
    void putVel(C io, Point2D.Double pt) {
        vel.put(io, pt);
    }
    
    //</editor-fold>
    
}
