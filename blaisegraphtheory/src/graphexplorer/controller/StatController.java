/*
 * StatController.java
 * Created Nov 11, 2010
 */

package graphexplorer.controller;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.bm.blaise.scio.graph.metrics.NodeMetric;

/**
 * <p>
 * This controller is used to compute getValues of metrics on a graph.
 * </p>
 * <p>
 * The getValues of the metric are not stored by default, but they are cached in case the
 * user requests the same getValues more than once. The computation of metric getValues is done
 * in a background thread. If a request is sent while the thread is running, the getValues
 * returns as null. Otherwise, a property change is sent to notify listeners when the
 * computed getValues are available.
 * </p>
 * @author elisha
 */
public class StatController extends AbstractGraphController {

    //
    // PROPERTY CHANGE NAMES
    //

    /** Changes to the active metric */
    public static final String $METRIC = "metric";
    /** Computed values */
    public static final String $VALUES = "metric values";

    //
    // PROPERTIES
    //
    
    /** Stores the active metric (may be null) */
    private NodeMetric metric = null;
    /** Stores getValues of the metric */
    private List values = null;

    /** Constructs a new instance. */
    public StatController(GraphController gc) {
        gc.addViewGraphFollower(this);
        addPropertyChangeListener(gc);
        setBaseGraph(gc.getBaseGraph());
    }

    //
    // METRIC COMPUTATIONS
    //

    @Override
    protected void updateGraph() {
        // TODO - need to rethink how these metric computations are done.
        // Perhaps provide a single static thread within this class that does the updates.
        getValues();
    }

    //
    // PROPERTY PATTERNS
    //

    /** @return active metric (maybe null) */
    public synchronized NodeMetric getMetric() {
        return metric;
    }

    /**
     * Sets the currently active metric
     * @param metric the new metric
     */
    public synchronized void setMetric(NodeMetric metric) {
        if (this.metric != metric) {
            Object oldMetric = this.metric;
            this.metric = metric;
            updateGraph();
            pcs.firePropertyChange($METRIC, oldMetric, metric);
        }
    }

    //
    // COMPUTATION
    //

    /** Flag to prevent running the same computation more than once. */
    boolean running = false;

    /** @return active metric getValues (the last ones that have been computed) */
    public List getValues() {
        if (running)
            return null;
        else if (values == null && metric != null && baseGraph != null) {
            running = true;
//            System.out.println("getValues computing");
//            System.out.println(".. metric: " + metric);
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(new Runnable(){
                public void run() {
                    pcs.firePropertyChange($STATUS, null, "Computing metric " + metric + "...");
                    long t0 = System.currentTimeMillis();
                    values = (metric == null || baseGraph == null) ? null
                            : metric.allValues(baseGraph);
                    running = false;
                    long t = System.currentTimeMillis() - t0;
                    pcs.firePropertyChange($STATUS, null, metric + " computation completed (" + t + "ms).");
                    pcs.firePropertyChange($VALUES, null, values);
                }
            });
            executor.shutdown();
        } else
            return values;
        return null;
    }
}
