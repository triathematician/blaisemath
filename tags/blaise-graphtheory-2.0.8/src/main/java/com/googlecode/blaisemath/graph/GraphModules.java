/*
 * GraphModules.java
 * Created Oct 29, 2011
 */
package com.googlecode.blaisemath.graph;

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

import com.google.common.base.Supplier;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.ServiceLoader;

/**
 * Provides access to various graph algorithms/metrics.
 * 
 * @author Elisha Peterson
 */
public class GraphModules {
    
    private static ServiceLoader<Supplier> builders;
    private static ServiceLoader<GraphMetric> global;
    private static ServiceLoader<GraphNodeMetric> node;
    private static ServiceLoader<StaticGraphLayout> staticLayout;
    private static ServiceLoader<IterativeGraphLayout> iterativeLayout;
    
    /**
     * Locate, initialize, and return the list of registered {@link GraphNodeMetric}
     * implementations via the {@link ServiceLoader} class.
     * @return list of {@code GraphNodeMetric}s
     */
    public static List<Supplier> graphBuilders() {
        if (builders == null) {
            builders = ServiceLoader.load(Supplier.class);
        }
        return Lists.newArrayList(builders);
    }
     
    /**
     * Locate, initialize, and return the list of registered {@link GraphNodeMetric}
     * implementations via the {@link ServiceLoader} class.
     * @return list of {@code GraphNodeMetric}s
     */
    public static List<GraphNodeMetric> nodeMetrics() {
        if (node == null) {
            node = ServiceLoader.load(GraphNodeMetric.class);
        }
        return Lists.newArrayList(node);
    }
    
    /**
     * Locate, initialize, and return the list of registered {@link GraphMetric}
     * implementations via the {@link ServiceLoader} class.
     * @return list of {@code GraphMetric}s
     */
    public static List<GraphMetric> globalMetrics() {
        if (global == null) {
            global = ServiceLoader.load(GraphMetric.class);
        }
        return Lists.newArrayList(global);
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
