/**
 * SimultaneousLayout.java
 * Created Feb 6, 2011
 */

package com.googlecode.blaisemath.graph.lon;

/*
 * #%L
 * BlaiseGraphTheory
 * --
 * Copyright (C) 2009 - 2015 Elisha Peterson
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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.googlecode.blaisemath.annotation.InvokedFromThread;
import com.googlecode.blaisemath.graph.Graph;
import com.googlecode.blaisemath.graph.mod.layout.CircleLayout;
import com.googlecode.blaisemath.graph.mod.layout.CircleLayout.CircleLayoutParameters;
import com.googlecode.blaisemath.graph.mod.layout.SpringLayout;
import java.awt.geom.Point2D;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.concurrent.ThreadSafe;

/**
 * Performs layout on a longitudinal graph by connecting nodes in adjacent slices.
 * @author elisha
 */
@ThreadSafe
public class SimultaneousLayout<C> {

    /** The graph's times */
    private final List<Double> times;
    /** Mapping of each slice of the graph to a layout */
    private final List<LayoutSlice<C>> slices = Lists.newArrayList();

    // ALGORITHM PARAMETERS

    //<editor-fold defaultstate="collapsed" desc="Parameters CLASS DEF">

    public static class Parameters extends SpringLayout.Parameters {
        /** Force per distance */
        double timeForce = .01;

        public double getTimeForce() {
            return timeForce;
        }

        public void setTimeForce(double timeForce) {
            this.timeForce = timeForce;
        }
    }
    //</editor-fold>

    protected Parameters parameters = new Parameters();

    // STATE VARIABLES

    /** Stores locations of all vertices at all times, after each iteration */
    final List<Map<C,Point2D.Double>> masterPos = Lists.newArrayList();

    // CONSTRUCTORS

    /** 
     * Construct simultaneous layout instance.
     * @param tg the longitudinal graph being laid out
     */
    public SimultaneousLayout(LonGraph<C> tg) {
        times = Collections.unmodifiableList(tg.getTimes());
        Map<Object,Point2D.Double> ip = CircleLayout.getInstance().layout(
                tg.slice(tg.getMaximumTime(), true), Collections.EMPTY_MAP, Collections.EMPTY_SET, new CircleLayoutParameters(100));
        for (int i = 0; i < times.size(); i++) {
            slices.add(new LayoutSlice(i, tg.slice(times.get(i), true), copy(ip)));
            masterPos.add(new HashMap<C,Point2D.Double>());
        }
    }

    public Parameters getParameters() {
        return parameters;
    }

    public void setParameters(Parameters parameters) {
        this.parameters = parameters;
    }

    private static <C> Map<C,Point2D.Double> copy(Map<C,Point2D.Double> ip) {
        Map<C,Point2D.Double> r = new HashMap<C,Point2D.Double>();
        for (Entry<C,Point2D.Double> en : ip.entrySet()) {
            r.put(en.getKey(), new Point2D.Double(en.getValue().x, en.getValue().y));
        }
        return r;
    }

    @InvokedFromThread("unknown")
    public synchronized Map<C, Point2D.Double> getPositionsCopy(double time) {
        for (int i = 0; i < masterPos.size(); i++) {
            if (times.get(i).equals(time)) {
                return copy(masterPos.get(i));
            }
        }
        return Collections.emptyMap();
    }

    //
    // ITERATION
    //

    @InvokedFromThread("unknown")
    public synchronized void iterate() {
        for (LayoutSlice s : slices) {
            s.iterate(s.graph);
        }
        masterPos.clear();
        for (LayoutSlice<C> s : slices) {
            HashMap<C,Point2D.Double> nueMap = Maps.newHashMap();
            for (Entry<C, Point2D.Double> en : s.getPositionsCopy().entrySet()) {
                nueMap.put(en.getKey(), new Point2D.Double(en.getValue().x, en.getValue().y));
            }
            masterPos.add(nueMap);
        }
    }

    /** 
     * Overrides SpringLayout to add time factor at each slice.
     */
    private class LayoutSlice<C> extends SpringLayout<C> {

        int tIndex;
        Graph<C> graph;

        LayoutSlice(int ti, Graph<C> g, Map<C,Point2D.Double> ip) {
            super(ip, SimultaneousLayout.this.parameters);
            this.tIndex = ti;
            this.graph = g;
        }

        @Override
        protected void addAdditionalForces(Graph<C> g, Point2D.Double sum, C io, Point2D.Double iLoc) {
            if (masterPos == null) {
                return;
            }
            try {
                iLoc = masterPos.get(tIndex).get(io);
            } catch (Exception ex) {
                Logger.getLogger(LayoutSlice.class.getName()).log(Level.SEVERE,
                        "Exception getting pos", ex);
                return;
            }
            if (iLoc == null) {
                return;
            }

            double iter = 1;

            for (int i = Math.max(tIndex-2, 0); i <= Math.min(tIndex+2,masterPos.size()-1); i++) {
                if (i == tIndex) {
                    continue;
                }
                double wt = 1.2-.2*Math.abs(tIndex-i);
                Point2D.Double adj1 = masterPos.get(i).get(io);
                if (adj1 != null) {
                    double dist = adj1.distance(iLoc);
                    if (dist > .002) {
                        sum.x += wt * SimultaneousLayout.this.parameters.timeForce * (adj1.x - iLoc.x) * dist * iter;
                        sum.y += wt * SimultaneousLayout.this.parameters.timeForce * (adj1.y - iLoc.y) * dist * iter;
                    }
                }
            }
        }

    }

}
