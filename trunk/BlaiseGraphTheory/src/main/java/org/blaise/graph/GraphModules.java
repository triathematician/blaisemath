/*
 * GraphModules.java
 * Created Oct 29, 2011
 */
package org.blaise.graph;

import com.google.common.base.Supplier;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;
import org.blaise.graph.metrics.GraphMetric;
import org.blaise.graph.metrics.GraphNodeMetric;

/**
 * Provides access to various graph algorithms/metrics.
 * 
 * @author Elisha Peterson
 */
public class GraphModules {
    
    private static ServiceLoader builders;
    private static ServiceLoader node;
    private static ServiceLoader global;
    
    
    /**
     * Locate, initialize, and return the list of registered {@link GraphNodeMetric}
     * implementations via the {@link ServiceLoader} class.
     * @return list of {@code GraphNodeMetric}s
     */
    public static List<Supplier> graphBuilders() {
        if (builders == null) {
            builders = ServiceLoader.load(Supplier.class);
        }
        return list(builders, Supplier.class);
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
        return list(node, GraphNodeMetric.class);
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
        return list(global, GraphMetric.class);
    }
    
    
    //
    // UTILITIES
    //    
    private static <V> List<V> list(ServiceLoader sl, Class<V> cls) {
        List<V> list = new ArrayList<V>();
        Iterator<V> it = sl.iterator();
        while (it.hasNext()) {
            list.add(it.next());
        }
        return list;        
    }
}
