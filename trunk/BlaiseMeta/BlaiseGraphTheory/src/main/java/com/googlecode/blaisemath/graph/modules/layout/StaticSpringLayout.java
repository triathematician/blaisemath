 /**
 * StaticSpringLayout.java
 * Created Feb 6, 2011
 */

package com.googlecode.blaisemath.graph.modules.layout;

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

import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Multiset;
import com.google.common.collect.Sets;
import com.googlecode.blaisemath.graph.Graph;
import com.googlecode.blaisemath.graph.GraphUtils;
import com.googlecode.blaisemath.graph.OptimizedGraph;
import com.googlecode.blaisemath.graph.StaticGraphLayout;
import com.googlecode.blaisemath.util.Edge;
import com.googlecode.blaisemath.util.Points;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.PrintStream;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nullable;

/**
 * Lays out nodes in a graph using a force-based layout technique.
 * @author elisha
 */
public class StaticSpringLayout implements StaticGraphLayout {
   
    /** How long to wait between reporting status */
    private static final int STATUS_REPORT_STEPS = 100;
    
    /** Maintain singleton instance of the class */
    private static StaticSpringLayout INST;
    
    public int minSteps = 100;
    public int maxSteps = 5000;
    public double energyChangeThreshold = SpringLayout.DIST_SCALE*SpringLayout.DIST_SCALE*1e-6;
    public double coolStart = 0.65;
    public double coolEnd = 0.1;
    
    private int lastStepCount = 0;
    
    /** Used to print status */
    @Nullable
    private PrintStream statusStream;
    /** Used to notify status */
    @Nullable
    private ActionListener al;
    
    public StaticSpringLayout() {
        this(null, null);
    }
    
    public StaticSpringLayout(PrintStream status, ActionListener al) {
        this.statusStream = status;
    }
    
    /** 
     * Return a singleton instance of the class (using default settings).
     * Logs notifications to System.out.
     * @return instance
     */
    public static StaticSpringLayout getInstance() {
        if (INST == null) {
            INST = new StaticSpringLayout(System.out, null);
        }
        return INST;        
    }
    
    @Override
    public String toString() {
        return "StaticSpringLayout";
    }
    
    /** Sets output stream for updates */
    public void setStatusStream(PrintStream s) {
        this.statusStream = s;
    }
    /** Sets listener for layout updates */
    public void setLayoutListener(ActionListener al) {
        this.al = al;
    }

    public int getLastStepCount() {
        return lastStepCount;
    }

    public synchronized Map<Object, Point2D.Double> layout(Graph originalGraph, double... parameters) {
        Logger.getLogger(StaticSpringLayout.class.getName()).log(Level.INFO, 
                "originalGraph, |V|={0}, |E|={1}, #components={2}, degrees={3}\n", 
                    new Object[] { originalGraph.nodeCount(), originalGraph.edgeCount(), 
                    GraphUtils.components(originalGraph).size(), 
                    nicer(GraphUtils.degreeDistribution(originalGraph)) 
                    });

        // reduce graph size for layout
        OptimizedGraph graphForInfo = new OptimizedGraph(false, originalGraph.nodes(), originalGraph.edges());
        final Set keepNodes = Sets.newHashSet(graphForInfo.getConnectorNodes());
        keepNodes.addAll(graphForInfo.getCoreNodes());
        Iterable<Edge> keepEdges = Iterables.filter(graphForInfo.edges(),
            new Predicate<Edge>(){
                public boolean apply(Edge input) {
                    return keepNodes.contains(input.getNode1())
                            && keepNodes.contains(input.getNode2());
                }
            });

        OptimizedGraph graphForLayout = new OptimizedGraph(false, keepNodes, keepEdges);
        Logger.getLogger(StaticSpringLayout.class.getName()).log(Level.INFO, 
                "graphForLayout, |V|={0}, |E|={1}, #components={2}, degrees={3}\n", 
                    new Object[] { graphForLayout.nodeCount(), graphForLayout.edgeCount(), 
                    GraphUtils.components(graphForLayout).size(), 
                    nicer(GraphUtils.degreeDistribution(graphForLayout)) 
                    });
        
        // perform the physics-based layout
        double irad = parameters.length > 0 ? parameters[0] : SpringLayout.DIST_SCALE;
        SpringLayout sl = new SpringLayout(graphForLayout, StaticGraphLayout.CIRCLE, irad);
        double lastEnergy = Double.MAX_VALUE;
        double energyChange = 9999;
        int step = 0;
        while (step < minSteps || (step < maxSteps && Math.abs(energyChange) > energyChangeThreshold)) {
            // adjust cooling parameter
            double cool01 = 1-step*step/(maxSteps*maxSteps);
            sl.parameters.dampingC = coolStart*cool01 + coolEnd*(1-cool01);
            sl.iterate(graphForLayout);
            energyChange = sl.energy - lastEnergy;
            lastEnergy = sl.energy;
            step++;
            if (step % STATUS_REPORT_STEPS == 0) {
                reportStatus("", step, lastEnergy);
            }
        }
        reportStatus("stop, ", step, lastEnergy);
        
        // add positions of isolates and leaf nodes back in
        Map<Object, Point2D.Double> res = sl.getPositions();
        addLeafNodes(graphForInfo, res);
        addIsolates(graphForInfo.getIsolates(), res);
        
        // report and clean up
        lastStepCount = step;
        Logger.getLogger(StaticSpringLayout.class.getName()).log(Level.INFO, 
                "StaticSpringLayout completed in {0} steps. The final energy "
                        + "change was {1}, and the final energy was {2}", 
                new Object[]{step, energyChange, lastEnergy});
        return res;
    }
    
    void reportStatus(String prefix, int step, double lastEnergy) {
        String status = String.format("%sstep = %d/%d, energy=%.2f (threshold=%.2f)", 
                prefix, step, maxSteps, lastEnergy, energyChangeThreshold);
        if (statusStream != null) {
            statusStream.println(status);
        }
        if (al != null) {
            al.actionPerformed(new ActionEvent(this, 0, status));
        }
    }
    
    /**
     * Add leaf nodes that are adjacent to the given positions.
     * @param og
     * @param pos 
     */
    public static void addLeafNodes(OptimizedGraph og, Map<Object, Point2D.Double> pos) {
        double nomSz = SpringLayout.DIST_SCALE/2;
        Set leafs = og.getLeafNodes();
        int n = leafs.size();
        if (n > 0) {
            Rectangle2D bounds = Points.boundingBox(pos.values(), SpringLayout.DIST_SCALE);
            if (bounds == null) {
                // no points exist, so must be all pairs
                double sqSide = nomSz * Math.sqrt(n);
                Rectangle2D pairRegion = new Rectangle2D.Double(-sqSide, -sqSide, 2*sqSide, 2*sqSide);
                LinkedHashSet orderedLeafs = orderByAdjacency(leafs, og);
                addPointsToBox(orderedLeafs, pairRegion, pos, nomSz, true);
            } else {
                // add close to their neighboring point
                Set cores = Sets.newHashSet();
                Set pairs = Sets.newHashSet();
                for (Object o : leafs) {
                    Object nbr = og.getNeighborOfLeaf(o);
                    if (leafs.contains(nbr)) {
                        pairs.add(o);
                        pairs.add(nbr);
                    } else {
                        cores.add(nbr);
                    }
                }
                for (Object o : cores) {
                    Set leaves = og.getLeavesAdjacentTo(o);
                    Point2D.Double ctr = pos.get(o);
                    double r = nomSz;
                    double theta = Math.atan2(ctr.y, ctr.x);
                    if (leaves.size() == 1) {
                        pos.put(Iterables.getFirst(leaves, null), new Point2D.Double(
                                ctr.getX()+r*Math.cos(theta), ctr.getY()+r*Math.sin(theta)));
                    } else {
                        double th0 = theta-Math.PI/3;
                        double dth = (2*Math.PI/3)/(leaves.size()-1);
                        for (Object l : leaves) {
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
     * @param isolates the isolate nodes
     * @param pos 
     */
    public static void addIsolates(Set isolates, Map<Object, Point2D.Double> pos) {
        double nomSz = SpringLayout.DIST_SCALE/2;
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

    private static void addPointsToBox(Set is, Rectangle2D rect, Map<Object, Point2D.Double> pos, double nomSz,
            boolean even) {
        double x = rect.getMinX();
        double y = rect.getMinY();
        int added = 0;
        for (Object o : is) {
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
}
