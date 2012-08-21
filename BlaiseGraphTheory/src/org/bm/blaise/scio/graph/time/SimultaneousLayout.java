/**
 * SimultaneousLayout.java
 * Created Feb 6, 2011
 */

package org.bm.blaise.scio.graph.time;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.bm.blaise.scio.graph.FilteredWeightedGraph;
import org.bm.blaise.scio.graph.Graph;
import org.bm.blaise.scio.graph.WeightedGraph;
import org.bm.blaise.scio.graph.layout.SpringLayout;
import org.bm.blaise.scio.graph.layout.StaticGraphLayout;

/**
 *
 * @author elisha
 */
public class SimultaneousLayout {

    /** The graph */
    private final TimeGraph tg;
    /** The graph's times */
    private final List<Double> times;
    /** Mapping of each slice of the graph to a layout */
    private final List<LayoutSlice> slices = new ArrayList<LayoutSlice>();

    // ALGORITHM PARAMETERS

    //<editor-fold defaultstate="collapsed" desc="Parameters CLASS DEF">
    
    public static class Parameters extends SpringLayout.Parameters {
        /** Force per distance */
        double springC = 5;

        public double getTimeForce() {
            return springC;
        }

        public void setTimeForce(double springC) {
            this.springC = springC;
        }
    }
    //</editor-fold>
    
    protected Parameters parameters = new Parameters();

    // STATE VARIABLES

    /** Stores locations of all vertices at all times, after each iteration */
    final List<Map<Object,Point2D.Double>> masterPos = new ArrayList<Map<Object,Point2D.Double>>();

    // CONSTRUCTORS

    /** Construct simultaneous layout instance */
    public SimultaneousLayout(TimeGraph tg) {
        this.tg = tg;
        times = tg.getTimes();
        Map<Object,Point2D.Double> ip = StaticGraphLayout.RANDOM.layout(tg.slice(tg.getMaximumTime(), true), 4.0);
        for (int i = 0; i < times.size(); i++) {
            slices.add(new LayoutSlice(i, tg.slice(times.get(i), true), copy(ip)));
            masterPos.add(new HashMap<Object,Point2D.Double>());
        }
        parameters.setGlobalForce(1.0);
//            super.setSpringForce(25.0);
//            super.setSpringLength(1.0);
        parameters.setDampingConstant(.6);
        parameters.setRepulsiveForce(1.0);
    }

    public Parameters getParameters() {
        return parameters;
    }

    public void setParameters(Parameters parameters) {
        this.parameters = parameters;
    }

    private Map<Object,Point2D.Double> copy(Map<Object,Point2D.Double> ip) {
        HashMap<Object,Point2D.Double> r = new HashMap<Object,Point2D.Double>();
        for (Entry<Object,Point2D.Double> en : ip.entrySet()) {
            r.put(en.getKey(), new Point2D.Double(en.getValue().x, en.getValue().y));
        }
        return r;
    }

    public Map<Object, Point2D.Double> getPositionMap(double time) {
        synchronized(masterPos) {
            for (int i = 0; i < masterPos.size(); i++) {
                if (times.get(i).equals(time))
                    return masterPos.get(i);
            }
        }
        return Collections.emptyMap();
    }
    
    //
    // ITERATION
    //

    public void iterate() {
        iterate(null);
    }

    /** Iterates with minimum threshold for adjacency */
    public void iterate(Double thresh) {
        for (LayoutSlice s : slices)
            s.iterate(thresh);
        synchronized(masterPos) {
            masterPos.clear();
            for (LayoutSlice s : slices) {
                HashMap<Object,Point2D.Double> nueMap = new HashMap<Object,Point2D.Double>();
                for (Entry<Object, Point2D.Double> en : s.getPositions().entrySet())
                    nueMap.put(en.getKey(), new Point2D.Double(en.getValue().x, en.getValue().y));
                masterPos.add(nueMap);
//                masterPos.add(new HashMap<Object,Point2D.Double>(s.getPositions()));
            }
        }
    }

    /** Overrides SpringLayout to add time factor at each slice */
    private class LayoutSlice extends SpringLayout {
        
        int tIndex;
        Graph graph;

        public LayoutSlice(int ti, Graph g, Map<Object,Point2D.Double> ip) {
            super(ip);
            this.tIndex = ti;
            this.graph = g;
            this.parameters = SimultaneousLayout.this.parameters;
        }

        /** Hack job to iterate filtered graphs */
        public void iterate(Double thresh) {
            if (thresh == null || !(graph instanceof WeightedGraph))
                iterate(graph);
            else
                iterate(new FilteredWeightedGraph((WeightedGraph)graph, thresh));
        }

        @Override
        protected void addAdditionalForces(Graph g, Point2D.Double sum, Object io, Point2D.Double iLoc) {
            if (masterPos == null)
                return;
            try {
                iLoc = masterPos.get(tIndex).get(io);
            } catch (Exception ex) {
                System.err.println("Exception getting pos: " + ex);
                return;
            }
            if (iLoc == null)
                return;

            double iter = 1;
//                    getIteration()/500.0;
//            iter = Math.min(5, iter * iter);

            double m0 = sum.distance(0, 0);
            for (int i = Math.max(tIndex-2, 0); i <= Math.min(tIndex+2,masterPos.size()-1); i++) {
                if (i == tIndex)
                    continue;
//                double wt = 1.5-.5*Math.abs(tIndex-i);
                double wt = 1.2-.2*Math.abs(tIndex-i);
//                double wt = .2*(tIndex-i+3);
//                double wt = i-tIndex+3;
                Point2D.Double adj1 = masterPos.get(i).get(io);
                if (adj1 != null) {
                    double dist = adj1.distance(iLoc);
                    if (dist > .002) {
                        sum.x += wt * SimultaneousLayout.this.parameters.springC * (adj1.x - iLoc.x) * dist * iter;
                        sum.y += wt * SimultaneousLayout.this.parameters.springC * (adj1.y - iLoc.y) * dist * iter;
                    }
                }
            }
            double m1 = sum.distance(0, 0);
            if (iter % 300 == 0) {
                System.err.println("m0: " + m0);
                System.err.println("m1: " + m1);
            }
        }

    }

}
