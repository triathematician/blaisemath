/*
 * ExplorerStatActions.java
 * Created Jul 13, 2010
 */

package graphexplorer.actions;

import graphexplorer.controller.GraphController;
import graphexplorer.GraphListModel;
import graphexplorer.dialogs.CooperationPanel;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.HashMap;
import java.util.TreeSet;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import org.bm.blaise.scio.graph.Graph;
import org.bm.blaise.scio.graph.metrics.AdditiveSubsetMetric;
import org.bm.blaise.scio.graph.metrics.BetweenCentrality;
import org.bm.blaise.scio.graph.metrics.ClosenessCentrality;
import org.bm.blaise.scio.graph.metrics.ContractiveSubsetMetric;
import org.bm.blaise.scio.graph.metrics.CooperationSubsetMetric;
import org.bm.blaise.scio.graph.metrics.DecayCentrality;
import org.bm.blaise.scio.graph.metrics.EigenCentrality;
import org.bm.blaise.scio.graph.metrics.GlobalMetric;
import org.bm.blaise.scio.graph.metrics.GraphMetrics;
import org.bm.blaise.scio.graph.metrics.NodeMetric;

/**
 * Describes statistical & metric actions to perform in explorer app
 * @author Elisha Peterson
 */
public class ExplorerStatActions {

    /** What this class works with */
    GraphController controller;
    /** Mapping of enums to actions */
    HashMap<Object, AbstractAction> actionCache;
    /** Construction requires a controller */
    public ExplorerStatActions(GraphController controller) {
        actionCache = new HashMap<Object, AbstractAction>();
        setController(controller);
    }

    public void setController(GraphController controller) {
        this.controller = controller;
        boolean nonNull = controller != null && controller.getViewGraph() != null;
        STAT_DECAY_CUSTOM.setEnabled(nonNull);
        HIGHLIGHT.setEnabled(nonNull);
        COOPERATION.setEnabled(nonNull);
        for (AbstractAction aa : actionCache.values())
            aa.setEnabled(nonNull);
    }

    /** @return action corresponding to specified enum */
    public Action actionOf(StatEnum se) {
        if (!actionCache.containsKey(se)) {
            StatAction sa = new StatAction(se.s, se.metric);
            sa.setEnabled(controller != null && controller.getViewGraph() != null);
            actionCache.put(se, sa);
            return sa;
        } else
            return actionCache.get(se);
    }

    /** @return action corresponding to specified enum */
    public Action actionOf(GlobalStatEnum se) {
        if (!actionCache.containsKey(se)) {
            GlobalStatAction sa = new GlobalStatAction(se.s, se.metric);
            sa.setEnabled(controller != null && controller.getViewGraph() != null);
            actionCache.put(se, sa);
            return sa;
        } else
            return actionCache.get(se);
    }

    private static final DecayCentrality DEC25 = DecayCentrality.getInstance(0.25);
    private static final DecayCentrality DEC50 = DecayCentrality.getInstance(0.50);
    private static final DecayCentrality DEC75 = DecayCentrality.getInstance(0.75);

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

    public Action HIGHLIGHT = new AbstractAction("Highlight subset of nodes", ExplorerActions.loadIcon("highlight18")) {
        {
            putValue(SHORT_DESCRIPTION, "Select 1 or more nodes to display as highlighted.");
            setEnabled(true);
        }
        public void actionPerformed(ActionEvent e) {
            GraphListModel glm = new GraphListModel(controller);
            JList jl = new JList(glm);
            jl.setLayoutOrientation(JList.VERTICAL_WRAP);
            int result = JOptionPane.showConfirmDialog(null, new JScrollPane(jl), "Select 1 or more nodes",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (result == JOptionPane.OK_OPTION) {
                int[] selected = jl.getSelectedIndices();
                TreeSet set = new TreeSet();
                for (int i : selected)
                    set.add(glm.getElementAt(i));
                controller.setHighlightNodes(set);
            }

//            int[] subset = ExplorerGenerateActions.showIntegerArrayInputDialog(
//                    "Enter comma-separated list of vertices (by integer index, 0-" + (nodes.size()-1) + ")",
//                    0, nodes.size()-1);
//            if (subset == null)
//                activeController().setNodeSubset(null);
//            else {
//                TreeSet set = new TreeSet();
//                for (int i : subset)
//                    set.add(nodes.get(i));
//                activeController().setNodeSubset(set);
//            }
        }
    };

    public Action COOPERATION = new AbstractAction("Compute cooperation scores (experimental)", ExplorerActions.loadIcon("cooperation18")) {
        {
            putValue(SHORT_DESCRIPTION, "Compute cooperation scores for active graph, based upon a metric and a selected subset.");
            setEnabled(true);
        }
        public void actionPerformed(ActionEvent e) {
            Graph active = controller.getViewGraph();
            if (active == null)
                return;
            CooperationPanel cp = new CooperationPanel();
            int result = JOptionPane.showConfirmDialog(null, cp, "Cooperation parameters", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (result == JOptionPane.OK_OPTION) {
                NodeMetric m = cp.getMetric();
                Collection<Integer> subset = cp.getSubset();
                CooperationSubsetMetric m1 = new CooperationSubsetMetric(new AdditiveSubsetMetric(m));
                CooperationSubsetMetric m2 = new CooperationSubsetMetric(new ContractiveSubsetMetric(m));
                double[] v1 = m1.getValue(active, subset);
                double[] v2 = m2.getValue(active, subset);
                controller.reportOutput("Computed additive metric with " + m + "   : selfish = " + v1[0] + ", altruistic = " + v1[1] + ", total = " + (v1[0]+v1[1]));
                controller.reportOutput("Computed contractive metric with " + m + ": selfish = " + v2[0] + ", altruistic = " + v2[1] + ", total = " + (v2[0]+v2[1]));
            }
        }
    };

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
        OTHER("Other", null);

        NodeMetric metric;
        String s;
        StatEnum(String s, NodeMetric metric) { this.s = s; this.metric = metric; }
        @Override public String toString() { return s; }
        /** @return metric corresponding to this enum */
        public NodeMetric getMetric() { return metric; }
        /** @return enum corresponding to specified metric, if it exists; null otherwise */
        public static StatEnum itemOf(NodeMetric m) {
            if (m == null)
                return NONE;
            for (StatEnum s : values())
                if (s.metric == m)
                    return s;
            return OTHER;
        }
    }

    /** Encodes global metric options */
    public enum GlobalStatEnum {
//        NONE("None", null),
//        CUSTOM("Custom", null),
        ORDER("Order", GraphMetrics.ORDER),
        EDGE_NUMBER("# of Edges", GraphMetrics.EDGE_NUMBER),
        AVERAGE_DEGREE("Average degree", GraphMetrics.DEGREE_AVERAGE),
        DENSITY("Density", GraphMetrics.DENSITY),
        DIAMETER("Diameter", GraphMetrics.DIAMETER),
        CLUSTERING("Clustering Coefficient", GraphMetrics.CLUSTERING);

        GlobalMetric metric;
        String s;
        GlobalStatEnum(String s, GlobalMetric metric) { this.s = s; this.metric = metric; }
        @Override public String toString() { return s; }
        public GlobalMetric getMetric() { return metric; }
    }

    /** Uses controller to implement a metric calculation action */
    class StatAction extends AbstractAction {
        String name;
        NodeMetric metric;
        StatAction(String name, NodeMetric metric) {
            super(name);
            this.name = name;
            this.metric = metric;
            putValue(SHORT_DESCRIPTION, "Compute " + name + " and display distribution");
        }
        public void actionPerformed(ActionEvent e) {
            controller.setMetric(metric);
        }
    };

    /** Uses controller to implement a metric calculation action */
    class GlobalStatAction extends AbstractAction {
        String name;
        GlobalMetric metric;
        GlobalStatAction(String name, GlobalMetric metric) {
            super(name);
            this.name = name;
            this.metric = metric;
            putValue(SHORT_DESCRIPTION, "Compute " + name);
            setEnabled(true);
        }
        public void actionPerformed(ActionEvent e) {
            if (controller != null && controller.getViewGraph() != null) {
                Object value = metric.value(controller.getViewGraph());
                controller.reportOutput("Value of metric " + name + " on active graph: " + value);
            }
        }
    };


}
