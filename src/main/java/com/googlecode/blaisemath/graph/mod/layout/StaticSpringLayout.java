 /**
 * StaticSpringLayout.java
 * Created Feb 6, 2011
 */

package com.googlecode.blaisemath.graph.mod.layout;

/*
 * #%L
 * BlaiseGraphTheory
 * --
 * Copyright (C) 2009 - 2016 Elisha Peterson
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

import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Multiset;
import com.google.common.collect.Sets;
import com.googlecode.blaisemath.graph.Graph;
import com.googlecode.blaisemath.graph.GraphUtils;
import com.googlecode.blaisemath.graph.IterativeGraphLayoutManager;
import com.googlecode.blaisemath.graph.OptimizedGraph;
import com.googlecode.blaisemath.graph.StaticGraphLayout;
import com.googlecode.blaisemath.graph.mod.layout.CircleLayout.CircleLayoutParameters;
import com.googlecode.blaisemath.graph.mod.layout.StaticSpringLayout.StaticSpringLayoutParameters;
import com.googlecode.blaisemath.util.Edge;
import com.googlecode.blaisemath.util.Points;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nullable;

/**
 * Positions nodes in a graph using a force-based layout technique.
 * @author elisha
 */
public class StaticSpringLayout implements StaticGraphLayout<StaticSpringLayoutParameters> {

    private static final Logger LOG = Logger.getLogger(StaticSpringLayout.class.getName());
    
    private static final CircleLayout INITIAL_LAYOUT = CircleLayout.getInstance();
    
    @Override
    public String toString() {
        return "Position nodes using \"spring layout\" algorithm";
    }

    @Override
    public StaticSpringLayoutParameters createParameters() {
        return new StaticSpringLayoutParameters();
    }

    @Override
    public <C> Map<C, Point2D.Double> layout(
            Graph<C> originalGraph, 
            @Nullable Map<C, Point2D.Double> ic, 
            StaticSpringLayoutParameters parm) {
        LOG.log(Level.FINE, "originalGraph, |V|={0}, |E|={1}, #components={2}, degrees={3}\n", 
                    new Object[] { originalGraph.nodeCount(), originalGraph.edgeCount(), 
                    GraphUtils.components(originalGraph).size(), 
                    nicer(GraphUtils.degreeDistribution(originalGraph)) 
                    });

        // reduce graph size for layout
        OptimizedGraph graphForInfo = new OptimizedGraph(false, originalGraph.nodes(), originalGraph.edges());
        final Set<C> keepNodes = Sets.newHashSet(graphForInfo.getConnectorNodes());
        keepNodes.addAll(graphForInfo.getCoreNodes());
        Iterable<Edge<C>> keepEdges = Iterables.filter(graphForInfo.edges(),
            new Predicate<Edge<C>>(){
                @Override
                public boolean apply(Edge<C> input) {
                    return keepNodes.contains(input.getNode1())
                            && keepNodes.contains(input.getNode2());
                }
            });

        OptimizedGraph<C> graphForLayout = new OptimizedGraph<C>(false, keepNodes, keepEdges);
        LOG.log(Level.FINE, "graphForLayout, |V|={0}, |E|={1}, #components={2}, degrees={3}\n", 
                    new Object[] { graphForLayout.nodeCount(), graphForLayout.edgeCount(), 
                    GraphUtils.components(graphForLayout).size(), 
                    nicer(GraphUtils.degreeDistribution(graphForLayout)) 
                    });
        
        // perform the physics-based layout
        Map<C,Point2D.Double> initialLocs = INITIAL_LAYOUT.layout(graphForLayout, 
                null, parm.initialLayoutParams);
        
        IterativeGraphLayoutManager mgr = new IterativeGraphLayoutManager();
        mgr.setLayout(new SpringLayout());
        mgr.setGraph(graphForLayout);
        SpringLayoutParameters params = (SpringLayoutParameters) mgr.getParameters();
        SpringLayoutState state = (SpringLayoutState) mgr.getState();
        params.setDistScale(parm.distScale);
        state.requestPositions(initialLocs, false);
        double lastEnergy = Double.MAX_VALUE;
        double energyChange = Double.MAX_VALUE;
        int step = 0;
        while (step < parm.minSteps || (step < parm.maxSteps && Math.abs(energyChange) > parm.energyChangeThreshold)) {
            // adjust cooling parameter
            double cool01 = 1-step*step/(parm.maxSteps*parm.maxSteps);
            params.dampingC = parm.coolStart*cool01 + parm.coolEnd*(1-cool01);
            double energy;
            try {
                energy = mgr.runOneLoop();
            } catch (InterruptedException ex) {
                throw new IllegalStateException("Unexpected interrupt", ex);
            }
            energyChange = energy - lastEnergy;
            lastEnergy = energy;
            step++;
        }
        
        // add positions of isolates and leaf nodes back in
        Map<C, Point2D.Double> res = state.getPositionsCopy();
        addLeafNodes(graphForInfo, res, params.getDistScale());
        addIsolates(graphForInfo.getIsolates(), res, params.getDistScale());
        
        // report and clean up
        LOG.log(Level.FINE, "StaticSpringLayout completed in {0} steps. The final energy "
                        + "change was {1}, and the final energy was {2}", 
                new Object[]{step, energyChange, lastEnergy});
        return res;
    }
    
    /**
     * Add leaf nodes that are adjacent to the given positions.
     * @param og the graph
     * @param pos current positions
     * @param distScale distance between noddes
     * @param <C> graph node type
     */
    public static <C> void addLeafNodes(OptimizedGraph<C> og, Map<C, Point2D.Double> pos, double distScale) {
        double nomSz = distScale/2;
        Set<C> leafs = og.getLeafNodes();
        int n = leafs.size();
        if (n > 0) {
            Rectangle2D bounds = Points.boundingBox(pos.values(), distScale);
            if (bounds == null) {
                // no points exist, so must be all pairs
                double sqSide = nomSz * Math.sqrt(n);
                Rectangle2D pairRegion = new Rectangle2D.Double(-sqSide, -sqSide, 2*sqSide, 2*sqSide);
                LinkedHashSet orderedLeafs = orderByAdjacency(leafs, og);
                addPointsToBox(orderedLeafs, pairRegion, pos, nomSz, true);
            } else {
                // add close to their neighboring point
                Set<C> cores = Sets.newHashSet();
                Set<C> pairs = Sets.newHashSet();
                for (C o : leafs) {
                    C nbr = og.getNeighborOfLeaf(o);
                    if (leafs.contains(nbr)) {
                        pairs.add(o);
                        pairs.add(nbr);
                    } else {
                        cores.add(nbr);
                    }
                }
                for (C o : cores) {
                    Set<C> leaves = og.getLeavesAdjacentTo(o);
                    Point2D.Double ctr = pos.get(o);
                    double r = nomSz;
                    double theta = Math.atan2(ctr.y, ctr.x);
                    if (leaves.size() == 1) {
                        pos.put(Iterables.getFirst(leaves, null), new Point2D.Double(
                                ctr.getX()+r*Math.cos(theta), ctr.getY()+r*Math.sin(theta)));
                    } else {
                        double th0 = theta-Math.PI/3;
                        double dth = (2*Math.PI/3)/(leaves.size()-1);
                        for (C l : leaves) {
                            pos.put(l, new Point2D.Double(ctr.getX()+r*Math.cos(th0), ctr.getY()+r*Math.sin(th0)));
                            th0 += dth;
                        }                        
                    }
                }
                
                // put the pairs to the right side
                double area = n * nomSz * nomSz;
                double ht = Math.min(bounds.getHeight(), 2*Math.sqrt(area));
                double wid = area/ht;
                Rectangle2D pairRegion = new Rectangle2D.Double(
                        bounds.getMaxX() + .1*bounds.getWidth(), bounds.getCenterY()-ht/2,
                        wid, ht);
                LinkedHashSet orderedPairs = orderByAdjacency(pairs, og);
                addPointsToBox(orderedPairs, pairRegion, pos, nomSz, true);
            }
        }
    }

    private static LinkedHashSet orderByAdjacency(Set leafs, OptimizedGraph og) {
        LinkedHashSet res = Sets.newLinkedHashSet();
        for (Object o : leafs) {
            if (!res.contains(o)) {
                res.add(o);
                res.add(og.getNeighborOfLeaf(o));
            }
        }
        return res;
    }
    
    /**
     * Add isolate nodes in the given graph based on the current positions in the map
     * @param <C> graph node type
     * @param isolates the isolate nodes
     * @param pos position map
     * @param distScale distance between nodes
     */
    public static <C> void addIsolates(Set<C> isolates, Map<C, Point2D.Double> pos, double distScale) {
        double nomSz = distScale/2;
        int n = isolates.size();
        if (n > 0) {
            Rectangle2D bounds = Points.boundingBox(pos.values(), nomSz);
            
            Rectangle2D isolateRegion;
            if (bounds == null) {
                // put them all in the middle
                double sqSide = nomSz * Math.sqrt(n);
                isolateRegion = new Rectangle2D.Double(-sqSide, -sqSide, 2*sqSide, 2*sqSide);
            } else {
                // put them to the right side
                double area = n * nomSz * nomSz;
                double ht = Math.min(bounds.getHeight(), 2*Math.sqrt(area));
                double wid = area/ht;
                isolateRegion = new Rectangle2D.Double(
                        bounds.getMaxX() + .1*bounds.getWidth(), bounds.getCenterY()-ht/2,
                        wid, ht);
            }
            addPointsToBox(isolates, isolateRegion, pos, nomSz, false);
        }
    }

    private static <C> void addPointsToBox(Set<C> is, Rectangle2D rect, Map<C, Point2D.Double> pos, 
            double nomSz, boolean even) {
        double x = rect.getMinX();
        double y = rect.getMinY();
        int added = 0;
        for (C o : is) {
            pos.put(o, new Point2D.Double(x, y));
            added++;
            x += nomSz;
            if (x > rect.getMaxX() && (!even || added % 2 == 0)) {
                x = rect.getMinX();
                y += nomSz;
            }
        }
    }

    private static String nicer(Multiset set) {
        List<String> ss = Lists.newArrayList();
        for (Object el : Sets.newTreeSet(set.elementSet())) {
            ss.add(el+":"+set.count(el));
        }
        return "["+Joiner.on(",").join(ss)+"]";
    }
    
    
    //<editor-fold defaultstate="collapsed" desc="INNER CLASSES">
    
    /** Parameters assoicated with the static spring layout. */
    public static class StaticSpringLayoutParameters {
        
        private CircleLayoutParameters initialLayoutParams = new CircleLayoutParameters();
        
        private double distScale = SpringLayoutParameters.DEFAULT_DIST_SCALE;
        private int minSteps = 100;
        private int maxSteps = 5000;
        private double coolStart = 0.65;
        private double coolEnd = 0.1;    
        private double energyChangeThreshold = distScale*distScale*1e-6;

        //<editor-fold defaultstate="collapsed" desc="PROPERTIES">
        //
        // PROPERTIES
        //

        public CircleLayoutParameters getInitialLayoutParams() {
            return initialLayoutParams;
        }

        public void setInitialLayoutParams(CircleLayoutParameters initialLayoutParams) {
            this.initialLayoutParams = initialLayoutParams;
        }

        public double getDistScale() {
            return distScale;
        }

        public void setDistScale(double distScale) {
            this.distScale = distScale;
            this.energyChangeThreshold = distScale*distScale*1e-6;
        }

        public int getMinSteps() {
            return minSteps;
        }

        public void setMinSteps(int minSteps) {
            this.minSteps = minSteps;
        }

        public int getMaxSteps() {
            return maxSteps;
        }

        public void setMaxSteps(int maxSteps) {
            this.maxSteps = maxSteps;
        }

        public double getEnergyChangeThreshold() {
            return energyChangeThreshold;
        }

        public void setEnergyChangeThreshold(double energyChangeThreshold) {
            this.energyChangeThreshold = energyChangeThreshold;
        }

        public double getCoolStart() {
            return coolStart;
        }

        public void setCoolStart(double coolStart) {
            this.coolStart = coolStart;
        }

        public double getCoolEnd() {
            return coolEnd;
        }

        public void setCoolEnd(double coolEnd) {
            this.coolEnd = coolEnd;
        }

        //</editor-fold>
        
    }
    
    //</editor-fold>
}
