/*
 * ExplorerStatActions.java
 * Created Jul 13, 2010
 */

package graphexplorer.actions;

import graphexplorer.views.GraphListModel;
import graphexplorer.controller.GraphDecorController;
import graphexplorer.dialogs.CooperationPanel;
import java.awt.event.ActionEvent;
import java.util.Collection;
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
public class ExplorerDecorActions {

    /** What this class works with */
    GraphDecorController controller;
    /** Construction requires a controller */
    public ExplorerDecorActions(GraphDecorController controller) {
        setController(controller);
    }

    public final void setController(GraphDecorController controller) {
        this.controller = controller;
        boolean nonNull = controller != null && controller.getBaseGraph() != null;
        HIGHLIGHT.setEnabled(nonNull);
    }

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

}
