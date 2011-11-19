/*
 * GraphServices.java
 * Created Oct 29, 2011
 */
package org.bm.blaise.scio.graph;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;
import org.bm.blaise.scio.graph.metrics.GlobalMetric;
import org.bm.blaise.scio.graph.metrics.NodeMetric;

/**
 * Provides access to various graph algorithms/metrics.
 * 
 * @author Elisha Peterson
 */
public class GraphServices {
    
    private static final ServiceLoader node = ServiceLoader.load(NodeMetric.class);
    private static final ServiceLoader global = ServiceLoader.load(GlobalMetric.class);
    
    /**
     * Locate, initialize, and return the list of registered {@link NodeMetric}
     * implementations via the {@link ServiceLoader} class.
     * @return list of {@code NodeMetric}s
     */
    public static List<NodeMetric> getAvailableNodeMetrics() {
        List<NodeMetric> list = new ArrayList<NodeMetric>();
        Iterator<NodeMetric> it = node.iterator();
        while (it.hasNext())
            list.add(it.next());
        return list;        
    }
    
    /**
     * Locate, initialize, and return the list of registered {@link GlobalMetric}
     * implementations via the {@link ServiceLoader} class.
     * @return list of {@code GlobalMetric}s
     */
    public static List<GlobalMetric> getAvailableGlobalMetrics() {
        List<GlobalMetric> list = new ArrayList<GlobalMetric>();
        Iterator<GlobalMetric> it = global.iterator();
        while (it.hasNext()) {
            list.add(it.next());
        }
        return list;        
    }
    
}
