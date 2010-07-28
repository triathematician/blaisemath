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
    GraphExplorerMain main;
    /** Construction requires a main class */
    public ExplorerStatActions(GraphExplorerMain main) { this.main = main; }

    private static final DecayCentrality DEC25 = DecayCentrality.getInstance(0.25);
    private static final DecayCentrality DEC50 = DecayCentrality.getInstance(0.50);
    private static final DecayCentrality DEC75 = DecayCentrality.getInstance(0.75);

    /** Encodes values for various actions that compute distributions. */
    public enum StatEnum {
        DEGREE("Degree"),
        DEGREE2("2nd Order Degree"),
        CLIQUE_COUNT("Clique Count"),
        CLIQUE_COUNT2("2nd Order Clique Count"),
        CLOSENESS("Closeness Centrality"),
        MAXCLOSE("Max-Distance Centrality"),
        BETWEEN("Betweenness Centrality"),
        EIGEN("Eigenvalue Centrality"),
        DECAY25("Decay Centrality (0.25)"),
        DECAY50("Decay Centrality (0.50)"),
        DECAY75("Decay Centrality (0.75)"),
        DECAY_CUSTOM("Decay Centrality (custom parameter)");

        String s;
        StatEnum(String s) { this.s = s; }
        @Override public String toString() { return s; }
    }

    public StatAction STAT_DEGREE = new StatAction("degree", GraphMetrics.DEGREE);
    public StatAction STAT_DEGREE2 = new StatAction("2nd-Order degree", GraphMetrics.DEGREE2);
    public StatAction STAT_CLIQUE = new StatAction("clique count", GraphMetrics.CLIQUE_COUNT);
    public StatAction STAT_CLIQUE2 = new StatAction("2nd-order clique count", GraphMetrics.CLIQUE_COUNT2);
    public StatAction STAT_CLOSENESS = new StatAction("closeness centrality", ClosenessCentrality.getInstance());
    public StatAction STAT_MAXCLOSE = new StatAction("max-closeness (graph) centrality", ClosenessCentrality.getMaxInstance());
    public StatAction STAT_BETWEEN = new StatAction("betweenness centrality", BetweenCentrality.getInstance());
    public StatAction STAT_EIGEN = new StatAction("clique count", EigenCentrality.getInstance());
    public StatAction STAT_DECAY_25 = new StatAction("decay centrality (0.25)", DEC25);
    public StatAction STAT_DECAY_50 = new StatAction("decay centrality (0.50)", DEC50);
    public StatAction STAT_DECAY_75 = new StatAction("decay centrality (0.75)", DEC75);

    static NodeMetric metricOf(StatEnum m) {
        switch(m) {
            case DEGREE: return GraphMetrics.DEGREE;
            case DEGREE2: return GraphMetrics.DEGREE2;
            case CLIQUE_COUNT: return GraphMetrics.CLIQUE_COUNT;
            case CLIQUE_COUNT2: return GraphMetrics.CLIQUE_COUNT2;
            case CLOSENESS: return ClosenessCentrality.getInstance();
            case MAXCLOSE: return ClosenessCentrality.getMaxInstance();
            case BETWEEN: return BetweenCentrality.getInstance();
            case EIGEN: return EigenCentrality.getInstance();
            case DECAY25: return DEC25;
            case DECAY50: return DEC50;
            case DECAY75: return DEC75;
            case DECAY_CUSTOM: return null;
            default:
                System.out.println("ExplorerStatActions: unsupported value " + m);
                return null;
        }
    }

    Action actionOf(StatEnum m) {
        switch(m) {
            case DEGREE: return STAT_DEGREE;
            case DEGREE2: return STAT_DEGREE2;
            case CLIQUE_COUNT: return STAT_CLIQUE;
            case CLIQUE_COUNT2: return STAT_CLIQUE2;
            case CLOSENESS: return STAT_CLOSENESS;
            case MAXCLOSE: return STAT_MAXCLOSE;
            case BETWEEN: return STAT_BETWEEN;
            case EIGEN: return STAT_EIGEN;
            case DECAY25: return STAT_DECAY_25;
            case DECAY50: return STAT_DECAY_50;
            case DECAY75: return STAT_DECAY_75;
            case DECAY_CUSTOM: return null;
            default:
                System.out.println("ExplorerStatActions: unsupported value " + m);
                return null;
        }
    }

    void actionPerformed(StatEnum m, ActionEvent evt) {
        actionOf(m).actionPerformed(evt);
    }

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
