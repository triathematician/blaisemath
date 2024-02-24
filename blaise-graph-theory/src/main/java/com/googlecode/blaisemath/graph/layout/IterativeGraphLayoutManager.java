package com.googlecode.blaisemath.graph.layout;

/*
 * #%L
 * BlaiseGraphTheory (v3)
 * --
 * Copyright (C) 2009 - 2024 Elisha Peterson
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

import com.google.common.graph.Graph;
import com.googlecode.blaisemath.coordinate.CoordinateListener;
import com.googlecode.blaisemath.coordinate.CoordinateManager;
import com.googlecode.blaisemath.graph.IterativeGraphLayout;
import com.googlecode.blaisemath.graph.IterativeGraphLayoutState;

import java.awt.geom.Point2D;
import java.util.Map;
import java.util.function.Function;

/**
 * Manages an iterative graph layout, including its state and parameters.
 * This class is not thread safe.
 *
 * @author Elisha Peterson
 */
public final class IterativeGraphLayoutManager {
    
    /** Default # iterations per layout step */
    private static final int DEFAULT_ITERATIONS_PER_LOOP = 2;
    /** Cooling curve. Determines the cooling parameter at each step, as a product of initial cooling parameter. */
    private static final Function<Integer, Double> COOLING_CURVE = x -> .1 + .9/Math.log10(x+10.0);
        
    /** The graph for layouts */
    private Graph graph;
    /** The coordinate manager to update after layout */
    private CoordinateManager coordinateManager;
    /** Listener for coordinate updates */
    private final CoordinateListener coordinateListener;
    
    /** Contains the algorithm for iterating graph layouts. */
    private IterativeGraphLayout layout;
    /** State for the iterative layout */
    private IterativeGraphLayoutState state;
    /** Parameters for the iterative layout */
    private Object params;

    /** Layout iteration number. */
    private int iteration = 0;
    
    /** # of iterations per loop */
    private int iterationsPerLoop = DEFAULT_ITERATIONS_PER_LOOP;

    public IterativeGraphLayoutManager() {
        this.coordinateListener = evt -> requestPositions(((CoordinateManager) evt.getSource()).getActiveLocationCopy(), true);
        setCoordinateManager(CoordinateManager.create(GraphLayoutManager.NODE_CACHE_SIZE));
    }
    
    //region PROPERTIES

    public IterativeGraphLayout getLayout() {
        return layout;
    }

    public void setLayout(IterativeGraphLayout layout) {
        if (this.layout != layout) {
            this.layout = layout;
            reset();
        }
    }

    public Graph getGraph() {
        return graph;
    }

    public void setGraph(Graph graph) {
        this.graph = graph;
    }

    public CoordinateManager getCoordinateManager() {
        return coordinateManager;
    }

    public void setCoordinateManager(CoordinateManager manager) {
        CoordinateManager old = this.coordinateManager;
        if (old != manager) {
            if (old != null) {
                old.removeCoordinateListener(coordinateListener);
            }
            this.coordinateManager = manager;
            manager.addCoordinateListener(coordinateListener);
        }
    }

    public IterativeGraphLayoutState getState() {
        return state;
    }

    /**
     * Get parameters associated with the current layout.
     * @return parameters
     */
    public Object getParameters() {
        return params;
    }
    
    /**
     * Set parameters for the current layout
     * @param params new parameters
     */
    public void setParameters(Object params) {
        this.params = params;
    }

    /**
     * Get the # of algorithm iterations per update loop
     * @return iterations
     */
    public int getIterationsPerLoop() {
        return iterationsPerLoop;
    }

    /**
     * Set the # of algorithm iterations per update loop
     * @param n # of iterations
     */
    public void setIterationsPerLoop(int n) {
        this.iterationsPerLoop = n;
    }
    
    //endregion

    /** 
     * Re-initialize the graph layout, resetting iteration, state, and params.
     * If the type of params is still valid for the new layout, it is not changed.
     */
    void reset() {
        if (layout == null) {
            state = null;
            params = null;
        } else {
            state = layout.createState();
            state.requestPositions(coordinateManager.getActiveLocationCopy(), true);
            Object newParams = layout.createParameters();
            Class oldParamsType = params == null ? null : params.getClass();
            Class newParamsType = newParams == null ? null : newParams.getClass();
            if (newParamsType != oldParamsType) {
                params = newParams;
            }
        }
        iteration = 0;        
    }
    
    /**
     * Perform a single layout loop. Depending on the manager's settings, this
     * may invoke the background graph layout 1 or more times.
     * @return energy of last loop
     * @throws InterruptedException if layout interrupted after an intermediate iteration
     */
    public double runOneLoop() throws InterruptedException {
        double energy = 0;
        for (int i = 0; i < iterationsPerLoop; i++) {
            energy = layout.iterate(graph, state, params);
            checkInterrupt();
        }
        coordinateManager.setCoordinateMap(state.getPositionsCopy());
        
        iteration += iterationsPerLoop;
        state.setCoolingParameter(COOLING_CURVE.apply(Math.max(0, iteration - 100)));
        checkInterrupt();
        
        return energy;
    }

    private void checkInterrupt() throws InterruptedException {
        if (Thread.interrupted()) {
            throw new InterruptedException("Layout canceled");
        }
    }
    
    //region ThreadSafe PROPERTIES

    public void requestPositions(Map<?, Point2D.Double> loc, boolean resetNodes) {
        if (state != null) {
            state.requestPositions(loc, resetNodes);
        }
    }
    
    //endregion
    
}
