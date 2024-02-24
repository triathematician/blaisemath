package com.googlecode.blaisemath.graph;

/*
 * #%L
 * BlaiseGraphTheory
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


import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * Provides instances of registered services of various types.
 * 
 * @author Elisha Peterson
 */
public final class GraphServices {

    private static final Map<Class, ServiceLoader> SERVICE_CACHE = Maps.newHashMap();

    /** Utility class cannot be instantiated */
    private GraphServices() {
    }

    //region SERVICE PROVIDERS
    
    /**
     * Locate, initialize, and return the list of registered {@link GraphGenerator} implementations via the
     * {@link ServiceLoader} class.
     * @return list of {@code GraphNodeMetric}s
     */
    public static List<GraphGenerator> generators() {
        return services(GraphGenerator.class);
    }

    /**
     * Locate, initialize, and return the list of registered {@link StaticGraphLayout} implementations via the
     * {@link ServiceLoader} class.
     * @return list of {@code StaticGraphLayout}s
     */
    public static List<StaticGraphLayout> staticLayouts() {
        return services(StaticGraphLayout.class);
    }

    /**
     * Locate, initialize, and return the list of registered {@link IterativeGraphLayout} implementations via the
     * {@link ServiceLoader} class.
     * @return list of {@code IterativeGraphLayout}s
     */
    public static List<IterativeGraphLayout> iterativeLayouts() {
        return services(IterativeGraphLayout.class);
    }
    
    /**
     * Locate, initialize, and return the list of registered {@link GraphMetric} implementations via the
     * {@link ServiceLoader} class.
     * @return list of {@code GraphMetric}s
     */
    public static List<GraphMetric> globalMetrics() {
        return services(GraphMetric.class);
    }
     
    /**
     * Locate, initialize, and return the list of registered {@link GraphNodeMetric}
     * implementations via the {@link ServiceLoader} class.
     * @return list of {@code GraphNodeMetric}s
     */
    public static List<GraphNodeMetric> nodeMetrics() {
        return services(GraphNodeMetric.class);
    }
     
    /**
     * Locate, initialize, and return the list of registered {@link GraphSubsetMetric} implementations via the
     * {@link ServiceLoader} class.
     * @return list of {@code GraphSubsetMetric}s
     */
    public static List<GraphSubsetMetric> subsetMetrics() {
        return services(GraphSubsetMetric.class);
    }

    //endregion

    /** Utility method to dynamically get list of services. */
    private static <X> List<X> services(Class<X> type) {
        if (SERVICE_CACHE.get(type) == null) {
            SERVICE_CACHE.put(type, ServiceLoader.load(type));
        }
        return Lists.newArrayList((Iterable<X>) SERVICE_CACHE.get(type));
    }
    
}
