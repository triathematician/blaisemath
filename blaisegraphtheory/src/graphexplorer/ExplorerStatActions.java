/*
 * ExplorerStatActions.java
 * Created Jul 13, 2010
 */

package graphexplorer;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.bm.blaise.scio.graph.metrics.BetweenCentrality;
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
    GraphExplorerMain main;
    /** Construction requires a main class */
    public ExplorerStatActions(GraphExplorerMain main) { this.main = main; }

    private static final DecayCentrality DEC25 = DecayCentrality.getInstance(0.25);
    private static final DecayCentrality DEC50 = DecayCentrality.getInstance(0.50);
    private static final DecayCentrality DEC75 = DecayCentrality.getInstance(0.75);

    /** Encodes values for various actions that compute distributions. */
    public enum StatEnum {
        DEGREE(GraphMetrics.DEGREE, "Degree") { public Action getAction(ExplorerStatActions gea) { return gea.STAT_DEGREE; } },
        DEGREE2(GraphMetrics.DEGREE2, "2nd Order Degree") { public Action getAction(ExplorerStatActions gea) { return gea.STAT_DEGREE2; } },
        CLIQUE_COUNT(GraphMetrics.CLIQUE_COUNT, "Clique Count") { public Action getAction(ExplorerStatActions gea) { return gea.STAT_CLIQUE; } },
        CLIQUE_COUNT2(GraphMetrics.CLIQUE_COUNT2, "2nd Order Clique Count") { public Action getAction(ExplorerStatActions gea) { return gea.STAT_CLIQUE2; } },
        BETWEEN(BetweenCentrality.getInstance(), "Betweenness Centrality") { public Action getAction(ExplorerStatActions gea) { return gea.STAT_BETWEEN; } },
        EIGEN(EigenCentrality.getInstance(), "Eigenvalue Centrality") { public Action getAction(ExplorerStatActions gea) { return gea.STAT_EIGEN; } },
        DECAY25(DEC25, "Decay Centrality (0.25)") { public Action getAction(ExplorerStatActions gea) { return gea.STAT_DECAY_25; } },
        DECAY50(DEC50, "Decay Centrality (0.50)") { public Action getAction(ExplorerStatActions gea) { return gea.STAT_DECAY_50; } },
        DECAY75(DEC75, "Decay Centrality (0.75)") { public Action getAction(ExplorerStatActions gea) { return gea.STAT_DECAY_75; } };

        String s;
        NodeMetric m;
        StatEnum(NodeMetric m, String s) { this.s = s; this.m = m; }
        @Override public String toString() { return s; }
        public NodeMetric getMetric() { return m; }
        abstract public Action getAction(ExplorerStatActions gea);
    }

    public Action STAT_DEGREE = new StatAction("degree", GraphMetrics.DEGREE);
    public Action STAT_DEGREE2 = new StatAction("2nd-Order degree", GraphMetrics.DEGREE2);
    public Action STAT_CLIQUE = new StatAction("clique count", GraphMetrics.CLIQUE_COUNT);
    public Action STAT_CLIQUE2 = new StatAction("2nd-order clique count", GraphMetrics.CLIQUE_COUNT2);
    public Action STAT_BETWEEN = new StatAction("betweenness centrality", BetweenCentrality.getInstance());
    public Action STAT_EIGEN = new StatAction("clique count", EigenCentrality.getInstance());
    public Action STAT_DECAY_25 = new StatAction("decay centrality (0.25)", DEC25);
    public Action STAT_DECAY_50 = new StatAction("decay centrality (0.50)", DEC50);
    public Action STAT_DECAY_75 = new StatAction("decay centrality (0.75)", DEC75);


    class StatAction extends AbstractAction {
        String name;
        NodeMetric metric;
        StatAction(String name, NodeMetric metric) {
            this.name = name;
            this.metric = metric;
            putValue(SHORT_DESCRIPTION, "Compute " + name + " and display distribution");
            setEnabled(true);
        }
        public void actionPerformed(ActionEvent e) {
            main.setActiveMetric(metric, name);
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
            main.setActiveMetric(new DecayCentrality(parameter), "decay centrality (" + parameter + ")");
        }
    };


}
