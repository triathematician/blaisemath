package com.googlecode.blaisemath.graph.layout;

/*
 * #%L
 * BlaiseGraphTheory
 * --
 * Copyright (C) 2009 - 2025 Elisha Peterson
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
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Multiset;
import com.google.common.collect.Sets;
import com.google.common.graph.EndpointPair;
import com.google.common.graph.Graph;
import com.googlecode.blaisemath.geom.Points;
import com.googlecode.blaisemath.graph.GraphUtils;
import com.googlecode.blaisemath.graph.OptimizedGraph;
import com.googlecode.blaisemath.graph.StaticGraphLayout;
import com.googlecode.blaisemath.graph.layout.CircleLayout.CircleLayoutParameters;
import com.googlecode.blaisemath.graph.layout.StaticSpringLayout.StaticSpringLayoutParameters;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.stream.Collectors.toList;

/**
 * Positions nodes in a graph using a force-based layout technique.
 *
 * @author Elisha Peterson
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
    public <N> Map<N, Point2D.Double> layout(Graph<N> originalGraph, @Nullable Map<N, Point2D.Double> ic,
                                             StaticSpringLayoutParameters parameters) {
        LOG.log(Level.FINE, "originalGraph, |N|={0}, |E|={1}, #components={2}, degrees={3}\n",
                new Object[]{originalGraph.nodes().size(), originalGraph.edges().size(),
                        GraphUtils.components(originalGraph).size(),
                        nicer(GraphUtils.degreeDistribution(originalGraph))});

        // reduce graph size for layout, by removing degree 1 nodes
        OptimizedGraph<N> graphForInfo = new OptimizedGraph<>(originalGraph);
        Set<N> keepNodes = Sets.newHashSet(graphForInfo.connectorNodes());
        keepNodes.addAll(graphForInfo.coreNodes());
        Iterable<EndpointPair<N>> keepEdges = graphForInfo.edges().stream()
                .filter(input -> keepNodes.contains(input.nodeU()) && keepNodes.contains(input.nodeV()))
                .collect(toList());

        OptimizedGraph<N> graphForLayout = new OptimizedGraph<>(false, keepNodes, keepEdges);
        LOG.log(Level.FINE, "graphForLayout, |N|={0}, |E|={1}, #components={2}, degrees={3}\n",
                    new Object[] { graphForLayout.nodes().size(), graphForLayout.edges().size(),
                    GraphUtils.components(graphForLayout).size(), 
                    nicer(GraphUtils.degreeDistribution(graphForLayout)) 
                    });

        // perform the physics-based layout
        Map<N, Point2D.Double> initialLocations = INITIAL_LAYOUT.layout(graphForLayout, null, parameters.initialLayoutParams);

        IterativeGraphLayoutManager mgr = new IterativeGraphLayoutManager();
        mgr.setLayout(new SpringLayout());
        mgr.setGraph(graphForLayout);
        SpringLayoutParameters params = (SpringLayoutParameters) mgr.getParameters();
        SpringLayoutState<N> state = (SpringLayoutState<N>) mgr.getState();
        params.setDistScale(parameters.distScale);
        state.requestPositions(initialLocations, false);
        double lastEnergy = Double.MAX_VALUE;
        double energyChange = Double.MAX_VALUE;
        int step = 0;
        while (step < parameters.minSteps || (step < parameters.maxSteps && Math.abs(energyChange) > parameters.energyChangeThreshold)) {
            // adjust cooling parameter
            double coolingAt = 1.0 - step * step / (double) (parameters.maxSteps * parameters.maxSteps);
            params.dampingC = parameters.coolStart * coolingAt + parameters.coolEnd * (1 - coolingAt);
            double energy;
            try {
                energy = mgr.runOneLoop();
            } catch (InterruptedException ex) {
                throw new IllegalStateException("Unexpected interrupt", ex);
            }
            energyChange = energy - lastEnergy;
            lastEnergy = energy;
            step += mgr.getIterationsPerLoop();
            if (step % 500 == 0) {
                LOG.log(Level.INFO, "|Energy at step {0}: {1} {2}",
                        new Object[]{step, energy, energyChange});
            }
        }
        
        // add positions of isolates and leaf nodes back in
        Map<N, Point2D.Double> res = state.getPositionsCopy();
        double distScale = params.getDistScale();
        addLeafNodes(graphForInfo, res, distScale, distScale*parameters.leafScale);
        addIsolates(graphForInfo.isolates(), res, distScale, distScale*parameters.isolateScale);
        
        // report and clean up
        LOG.log(Level.FINE, "StaticSpringLayout completed in {0} steps. The final energy change was {1}, and the " +
                        "final energy was {2}", new Object[]{step, energyChange, lastEnergy});
        return res;
    }

    //region ADD ELEMENTS AFTER MAIN LAYOUT

    /**
     * Add leaf nodes that are adjacent to the given positions.
     * @param og the graph
     * @param pos current positions
     * @param distScale distance between nodes
     * @param leafScale distance between leaves and adjacent nodes
     * @param <N> graph node type
     */
    private static <N> void addLeafNodes(OptimizedGraph<N> og, Map<N, Point2D.Double> pos, double distScale, double leafScale) {
        Set<N> leafs = og.leafNodes();
        int n = leafs.size();
        if (n > 0) {
            Rectangle2D bounds = Points.boundingBox(pos.values(), distScale);
            if (bounds == null) {
                // no points exist, so must be all pairs
                double sqSide = leafScale * Math.sqrt(n);
                Rectangle2D pairRegion = new Rectangle2D.Double(-sqSide, -sqSide, 2*sqSide, 2*sqSide);
                Set<N> orderedLeafs = orderByAdjacency(leafs, og);
                addPointsToBox(orderedLeafs, pairRegion, pos, leafScale, true);
            } else {
                // add close to their neighboring point
                Set<N> cores = Sets.newHashSet();
                Set<N> pairs = Sets.newHashSet();
                for (N o : leafs) {
                    N nbr = og.neighborOfLeaf(o);
                    if (leafs.contains(nbr)) {
                        pairs.add(o);
                        pairs.add(nbr);
                    } else {
                        cores.add(nbr);
                    }
                }
                for (N o : cores) {
                    Set<N> leaves = og.leavesAdjacentTo(o);
                    Point2D.Double ctr = pos.get(o);
                    double theta = Math.atan2(ctr.y, ctr.x);
                    if (leaves.size() == 1) {
                        pos.put(Iterables.getFirst(leaves, null), new Point2D.Double(
                                ctr.getX() + leafScale * Math.cos(theta), ctr.getY() + leafScale * Math.sin(theta)));
                    } else {
                        double th0 = theta - Math.PI / 3;
                        double dth = (2 * Math.PI / 3) / (leaves.size() - 1);
                        for (N l : leaves) {
                            pos.put(l, new Point2D.Double(ctr.getX() + leafScale * Math.cos(th0), ctr.getY() + leafScale * Math.sin(th0)));
                            th0 += dth;
                        }
                    }
                }
                
                // put the pairs to the right side
                double area = n * leafScale * leafScale;
                double ht = Math.min(bounds.getHeight(), 2*Math.sqrt(area));
                double wid = area/ht;
                Rectangle2D pairRegion = new Rectangle2D.Double(
                        bounds.getMaxX() + .1*bounds.getWidth(), bounds.getCenterY()-ht/2,
                        wid, ht);
                Set<N> orderedPairs = orderByAdjacency(pairs, og);
                addPointsToBox(orderedPairs, pairRegion, pos, leafScale, true);
            }
        }
    }

    private static <N> Set<N> orderByAdjacency(Set<N> leafs, OptimizedGraph<N> og) {
        Set<N> res = Sets.newLinkedHashSet();
        for (N o : leafs) {
            if (!res.contains(o)) {
                res.add(o);
                res.add(og.neighborOfLeaf(o));
            }
        }
        return res;
    }
    
    /**
     * Add isolate nodes in the given graph based on the current positions in the map
     * @param <N> graph node type
     * @param isolates the isolate nodes
     * @param pos position map
     * @param distScale distance between nodes
     * @param isoScale distance between isolates
     */
    private static <N> void addIsolates(Set<N> isolates, Map<N, Point2D.Double> pos, double distScale, double isoScale) {
        int n = isolates.size();
        if (n > 0) {
            Rectangle2D bounds = Points.boundingBox(pos.values(), isoScale);
            
            Rectangle2D isolateRegion;
            if (bounds == null) {
                // put them all in the middle
                double sqSide = isoScale * Math.sqrt(n);
                isolateRegion = new Rectangle2D.Double(-sqSide, -sqSide, 2*sqSide, 2*sqSide);
            } else {
                // put them to the right side
                double area = n * isoScale * isoScale;
                double ht = Math.min(bounds.getHeight(), 2*Math.sqrt(area));
                double wid = area/ht;
                isolateRegion = new Rectangle2D.Double(
                        bounds.getMaxX() + .1*bounds.getWidth(), bounds.getCenterY()-ht/2,
                        wid, ht);
            }
            addPointsToBox(isolates, isolateRegion, pos, isoScale, false);
        }
    }

    private static <N> void addPointsToBox(Set<N> is, Rectangle2D rect, Map<N, Point2D.Double> pos,
                                           double nomSz, boolean even) {
        double x = rect.getMinX();
        double y = rect.getMinY();
        int added = 0;
        for (N o : is) {
            pos.put(o, new Point2D.Double(x, y));
            added++;
            x += nomSz;
            if (x > rect.getMaxX() && (!even || added % 2 == 0)) {
                x = rect.getMinX();
                y += nomSz;
            }
        }
    }

    //endregion

    //region UTILS

    private static String nicer(Multiset set) {
        List<String> ss = Lists.newArrayList();
        for (Object el : Sets.newTreeSet(set.elementSet())) {
            ss.add(el+":"+set.count(el));
        }
        return "["+Joiner.on(",").join(ss)+"]";
    }

    //endregion

    //region INNER CLASSES
    
    /** Parameters associated with the static spring layout. */
    public static class StaticSpringLayoutParameters {
        
        private CircleLayoutParameters initialLayoutParams = new CircleLayoutParameters();
        
        /** Approximate distance between nodes */
        private double distScale = SpringLayoutParameters.DEFAULT_DIST_SCALE;
        /** Distance between leaf and adjacent node, as percentage of distScale */
        private double leafScale = .5;
        /** Distance between isolates, as percentage of distScale */
        private double isolateScale = .5;
        private int minSteps = 100;
        private int maxSteps = 5000;
        private double coolStart = 0.5;
        private double coolEnd = 0.05;    
        private double energyChangeThreshold = distScale*distScale*1e-6;

        //region PROPERTIES

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

        public double getLeafScale() {
            return leafScale;
        }

        public void setLeafScale(double leafScale) {
            this.leafScale = leafScale;
        }

        public double getIsolateScale() {
            return isolateScale;
        }

        public void setIsolateScale(double isolateScale) {
            this.isolateScale = isolateScale;
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

        //endregion
        
    }
    
    //endregion
}
