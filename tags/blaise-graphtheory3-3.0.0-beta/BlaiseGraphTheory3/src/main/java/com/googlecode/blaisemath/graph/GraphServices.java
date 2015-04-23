/**
 * GraphServices.java
 * Created Mar 23, 2015
 */
package com.googlecode.blaisemath.graph;

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
import java.util.List;
import java.util.ServiceLoader;

/**
 * Provides instances of registered services of various types.
 * 
 * @author elisha
 */
public final class GraphServices {
    
    private static ServiceLoader<GraphMetric> globalMetrics;
    private static ServiceLoader<GraphNodeMetric> nodeMetrics;
    private static ServiceLoader<GraphSubsetMetric> subsetMetrics;
    private static ServiceLoader<GraphGenerator> generators;
    private static ServiceLoader<IterativeGraphLayout> iterativeLayout;
    private static ServiceLoader<StaticGraphLayout> staticLayout;
    
    /**
     * Locate, initialize, and return the list of registered {@link GraphGenerator}
     * implementations via the {@link ServiceLoader} class.
     * @return list of {@code GraphNodeMetric}s
     */
    public static List<GraphGenerator> generators() {
        if (generators == null) {
            generators = ServiceLoader.load(GraphGenerator.class);
        }
        return Lists.newArrayList(generators);
    }
    
    /**
     * Locate, initialize, and return the list of registered {@link GraphMetric}
     * implementations via the {@link ServiceLoader} class.
     * @return list of {@code GraphMetric}s
     */
    public static List<GraphMetric> globalMetrics() {
        if (globalMetrics == null) {
            globalMetrics = ServiceLoader.load(GraphMetric.class);
        }
        return Lists.newArrayList(globalMetrics);
    }
     
    /**
     * Locate, initialize, and return the list of registered {@link GraphNodeMetric}
     * implementations via the {@link ServiceLoader} class.
     * @return list of {@code GraphNodeMetric}s
     */
    public static List<GraphNodeMetric> nodeMetrics() {
        if (nodeMetrics == null) {
            nodeMetrics = ServiceLoader.load(GraphNodeMetric.class);
        }
        return Lists.newArrayList(nodeMetrics);
    }
     
    /**
     * Locate, initialize, and return the list of registered {@link GraphSubsetMetric}
     * implementations via the {@link ServiceLoader} class.
     * @return list of {@code GraphSubsetMetric}s
     */
    public static List<GraphSubsetMetric> subsetMetrics() {
        if (subsetMetrics == null) {
            subsetMetrics = ServiceLoader.load(GraphSubsetMetric.class);
        }
        return Lists.newArrayList(subsetMetrics);
    }
    
    /**
     * Locate, initialize, and return the list of registered {@link StaticGraphLayout}
     * implementations via the {@link ServiceLoader} class.
     * @return list of {@code StaticGraphLayout}s
     */
    public static List<StaticGraphLayout> staticLayouts() {
        if (staticLayout == null) {
            staticLayout = ServiceLoader.load(StaticGraphLayout.class);
        }
        return Lists.newArrayList(staticLayout);
    }
    
    /**
     * Locate, initialize, and return the list of registered {@link IterativeGraphLayout}
     * implementations via the {@link ServiceLoader} class.
     * @return list of {@code IterativeGraphLayout}s
     */
    public static List<IterativeGraphLayout> iterativeLayouts() {
        if (iterativeLayout == null) {
            iterativeLayout = ServiceLoader.load(IterativeGraphLayout.class);
        }
        return Lists.newArrayList(iterativeLayout);
    }
    
}
