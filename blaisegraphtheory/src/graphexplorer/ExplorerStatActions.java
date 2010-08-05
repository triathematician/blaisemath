/*
 * ExplorerStatActions.java
 * Created Jul 13, 2010
 */

package graphexplorer;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.bm.blaise.scio.graph.metrics.BetweenCentrality;
import org.bm.blaise.scio.graph.metrics.ClosenessCentrality;
import org.bm.blaise.scio.graph.metrics.DecayCentrality;
import org.bm.blaise.scio.graph.metrics.EigenCentrality;
import org.bm.blaise.scio.graph.metrics.GraphMetrics;
import org.bm.blaise.scio.graph.metrics.NodeMetric;

/**
 * Describes statistical & metric actions to perform in explorer app
 * @author Elisha Peterson
 */
class ExplorerStatActions {

    /** What this class works with */
    GraphController controller;
    /** Construction requires a controller */
    public ExplorerStatActions(GraphController controller) { this.controller = controller; }

    private static final DecayCentrality DEC25 = DecayCentrality.getInstance(0.25);
    private static final DecayCentrality DEC50 = DecayCentrality.getInstance(0.50);
    private static final DecayCentrality DEC75 = DecayCentrality.getInstance(0.75);

    /** Encodes values for various actions that compute distributions. */
    public enum StatEnum {
        NONE("None", null),
        DEGREE("Degree", GraphMetrics.DEGREE),
        DEGREE2("2nd Order Degree", GraphMetrics.DEGREE2),
        CLIQUE_COUNT("Clique Count", GraphMetrics.CLIQUE_COUNT),
        CLIQUE_COUNT2("2nd Order Clique Count", GraphMetrics.CLIQUE_COUNT2),
        CLOSENESS("Closeness Centrality", ClosenessCentrality.getInstance()),
        MAXCLOSE("Max-Distance Centrality", ClosenessCentrality.getMaxInstance()),
        BETWEEN("Betweenness Centrality", BetweenCentrality.getInstance()),
        EIGEN("Eigenvalue Centrality", EigenCentrality.getInstance()),
        DECAY25("Decay Centrality (0.25)", DEC25),
        DECAY50("Decay Centrality (0.50)", DEC50),
        DECAY75("Decay Centrality (0.75)", DEC75),
        DECAY_CUSTOM("Decay Centrality (custom parameter)", null);

        NodeMetric metric;
        String s;
        StatEnum(String s, NodeMetric metric) { this.s = s; this.metric = metric; }
        @Override public String toString() { return s; }
        public NodeMetric getMetric() { return metric; }
    }
    
    /** @return action corresponding to specified enum */
    Action actionOf(StatEnum se) { return new StatAction(se.s, se.metric); }

    class StatAction extends AbstractAction {
        String name;
        NodeMetric metric;
        StatAction(String name, NodeMetric metric) {
            super(name);
            this.name = name;
            this.metric = metric;
            putValue(SHORT_DESCRIPTION, "Compute " + name + " and display distribution");
            setEnabled(true);
        }
        public void actionPerformed(ActionEvent e) {
            controller.setMetric(metric);
        }
    };

    public Action STAT_DECAY_CUSTOM = new AbstractAction("Compute decay centrality distribution (custom)") {
        {
            putValue(SHORT_DESCRIPTION, "Compute decay centrality distribution with custom parameter and set up metric table with results");
            setEnabled(true);
        }
        public void actionPerformed(ActionEvent e) {
            float parameter = ExplorerGenerateActions.showFloatInputDialog("Enter parameter for decay centrality (between 0 and 1)", 0, 1);
            if (parameter == -1) return;
            controller.setMetric(new DecayCentrality(parameter));
        }
    };


}
