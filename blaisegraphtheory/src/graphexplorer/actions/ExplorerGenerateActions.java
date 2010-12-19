/*
 * ExplorerGenerateActions.java
 * Created May 14, 2010
 */

package graphexplorer.actions;

import graphexplorer.controller.GraphController;
import graphexplorer.controller.GraphControllerMaster;
import graphexplorer.controller.LongitudinalGraphController;
import graphexplorer.panels.NewWSGraphPanel;
import graphexplorer.panels.NewDegreeSequenceGraphPanel;
import graphexplorer.panels.NewSimpleGraphPanel;
import graphexplorer.panels.NewPreferentialGraphPanel;
import graphexplorer.panels.NewRandomGraphPanel;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;
import org.bm.blaise.scio.graph.Graph;
import org.bm.blaise.scio.graph.GraphFactory;
import org.bm.blaise.scio.graph.LongitudinalGraph;

/**
 * Describes graph-generation actions supporting the graph explorer app.
 * @author Elisha Peterson
 */
public class ExplorerGenerateActions {

    /** What this class works with */
    GraphControllerMaster master;

    /** Construction requires a controller */
    public ExplorerGenerateActions(GraphControllerMaster controller) { 
        this.master = controller;
    }

    public Action GENERATE_EMPTY = new AbstractAction("New empty graph...", ExplorerActions.loadIcon("new-empty18")) {
        public void actionPerformed(ActionEvent e) {
            NewSimpleGraphPanel nsgp = new NewSimpleGraphPanel();
            int n = JOptionPane.showOptionDialog(null, nsgp, "New empty graph",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, JOptionPane.OK_OPTION);
            if (n == JOptionPane.OK_OPTION) {
                Graph<Integer> graph = GraphFactory.getEmptyGraph(nsgp.getOrder(), nsgp.getDirected());
                GraphController newC = new GraphController(graph, "Empty graph (" + nsgp.getOrder() + " vertices)");
                master.setActiveController(newC);
            }
        }
    };

    public Action GENERATE_COMPLETE = new AbstractAction("New complete graph...", ExplorerActions.loadIcon("new-complete18")) {
        public void actionPerformed(ActionEvent e) {
            NewSimpleGraphPanel nsgp = new NewSimpleGraphPanel(1000);
            int n = JOptionPane.showOptionDialog(null, nsgp, "New complete graph",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, JOptionPane.OK_OPTION);
            if (n == JOptionPane.OK_OPTION) {
                Graph<Integer> graph = GraphFactory.getCompleteGraph(nsgp.getOrder(), nsgp.getDirected());
                GraphController newC = new GraphController(graph, "Complete graph (" + nsgp.getOrder() + " vertices)");
                master.setActiveController(newC);
            }
        }
    };

    public Action GENERATE_CIRCLE = new AbstractAction("New circle graph...", ExplorerActions.loadIcon("new-circle18")) {
        public void actionPerformed(ActionEvent e) {
            NewSimpleGraphPanel nsgp = new NewSimpleGraphPanel();
            int n = JOptionPane.showOptionDialog(null, nsgp, "New circle graph",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, JOptionPane.OK_OPTION);
            if (n == JOptionPane.OK_OPTION) {
                Graph<Integer> graph = GraphFactory.getCycleGraph(nsgp.getOrder(), nsgp.getDirected());
                GraphController newC = new GraphController(graph, "Circle graph (" + nsgp.getOrder() + " vertices)");
                master.setActiveController(newC);
            }
        }
    };

    public Action GENERATE_STAR = new AbstractAction("New star graph...", ExplorerActions.loadIcon("new-star18")) {
        public void actionPerformed(ActionEvent e) {
            NewSimpleGraphPanel nsgp = new NewSimpleGraphPanel();
            int n = JOptionPane.showOptionDialog(null, nsgp, "New star graph",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, JOptionPane.OK_OPTION);
            if (n == JOptionPane.OK_OPTION) {
                Graph<Integer> graph = GraphFactory.getStarGraph(nsgp.getOrder(), nsgp.getDirected());
                GraphController newC = new GraphController(graph, "Star graph (" + nsgp.getOrder() + " vertices)");
                master.setActiveController(newC);
            }
        }
    };

    public Action GENERATE_WHEEL = new AbstractAction("New wheel graph...", ExplorerActions.loadIcon("new-wheel18")) {
        public void actionPerformed(ActionEvent e) {
            NewSimpleGraphPanel nsgp = new NewSimpleGraphPanel();
            int n = JOptionPane.showOptionDialog(null, nsgp, "New wheel graph",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, JOptionPane.OK_OPTION);
            if (n == JOptionPane.OK_OPTION) {
                Graph<Integer> graph = GraphFactory.getWheelGraph(nsgp.getOrder(), nsgp.getDirected());
                GraphController newC = new GraphController(graph, "Wheel graph (" + nsgp.getOrder() + " vertices)");
                master.setActiveController(newC);
            }
        }
    };

    //
    // RANDOM GRAPHS
    //

    public Action GENERATE_RANDOM = new AbstractAction("New uniform random graph...", ExplorerActions.loadIcon("random-uniform18")) {
        public void actionPerformed(ActionEvent e) {
            NewRandomGraphPanel ngp = new NewRandomGraphPanel();
            int n = JOptionPane.showOptionDialog(null, ngp, "New uniform random graph",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, JOptionPane.OK_OPTION);
            if (n == JOptionPane.OK_OPTION) {
                Graph<Integer> result = ngp.getInstance();
                GraphController newC = new GraphController(result, "Random Graph (" + result.order() + " vertices)");
                master.setActiveController(newC);
            }
        }
    };

    public Action GENERATE_SEQUENCE = new AbstractAction("New degree-sequence random graph...", ExplorerActions.loadIcon("random-sequence18")) {
        public void actionPerformed(ActionEvent e) {
            NewDegreeSequenceGraphPanel ngp = new NewDegreeSequenceGraphPanel();
            int n = JOptionPane.showOptionDialog(null, ngp, "New degree-sequence random graph",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, JOptionPane.OK_OPTION);
            if (n == JOptionPane.OK_OPTION) {
                Graph<Integer> result = ngp.getInstance();
                if (result != null) {
                    GraphController newC = new GraphController(result, "Degree-Sequence Random Graph (" + result.order() + " vertices)");
                    master.setActiveController(newC);
                }
            }
        }
    };

    public Action GENERATE_WS = new AbstractAction("New Watts-Strogatz random graph...", ExplorerActions.loadIcon("random-watts18")) {
        public void actionPerformed(ActionEvent e) {
            NewWSGraphPanel ngp = new NewWSGraphPanel();
            int n = JOptionPane.showOptionDialog(null, ngp, "New Watts-Strogatz random graph",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, JOptionPane.OK_OPTION);
            if (n == JOptionPane.OK_OPTION) {
                Graph<Integer> result = ngp.getInstance();
                GraphController newC = new GraphController(result, "Watts-Strogatz Random Graph (" + result.order() + " vertices)");
                master.setActiveController(newC);
            }
        }
    };

    public Action GENERATE_PREFERENTIAL = new AbstractAction("New preferential attachment graph...", ExplorerActions.loadIcon("random-pref18")) {
        public void actionPerformed(ActionEvent e) {
            NewPreferentialGraphPanel ngp = new NewPreferentialGraphPanel();
            int n = JOptionPane.showOptionDialog(null, ngp, "New preferential attachment graph",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, JOptionPane.OK_OPTION);
            if (n == JOptionPane.OK_OPTION) {
                Object result = ngp.getInstance();
                GraphController newC = null;
                if (result instanceof Graph)
                    newC = new GraphController((Graph) result, "Preferential Random Graph ("
                            + ((Graph)result).order() + " vertices)");
                else if (result instanceof LongitudinalGraph) {
                    LongitudinalGraph lg = (LongitudinalGraph) result;
                    newC = new LongitudinalGraphController(lg, "Preferential Random Graph ("
                            + lg.getAllNodes().size() + " vertices, " + lg.getTimes().size() + " time steps)");
                }
                master.setActiveController(newC);
            }
        }
    };

    //
    // UTILITY METHODS
    //

    /**
     * Shows option pane to retrieve an integer value in provided range.
     * @return a value between min and max, or -1 if the user cancelled the dialog
     */
    public static int showIntegerInputDialog(String message, int min, int max) {
        int num = 0;
        do {
            String response = JOptionPane.showInputDialog(message);
            if (response == null) return -1;
            try { num = Integer.decode(response); } catch (NumberFormatException ex) { System.out.println("Improperly formatted number..."); }
        } while (num < min || num > max);
        return num;
    }

    /**
     * Shows option pane to retrieve an array of int values in specified range
     * @return a value between min and max, or null if the user cancelled the dialog
     */
    public static int[] showIntegerArrayInputDialog(String message, int min, int max) {
        int[] result = null;
        boolean valid;
        do {
            valid = true;
            String response = JOptionPane.showInputDialog(message);
            if (response == null) return null;
            String[] responseArr = response.split(",");
            result = new int[responseArr.length];
            try {
                for (int i = 0; i < responseArr.length; i++) {
                    result[i] = Integer.decode(responseArr[i].trim());
                    valid = valid && result[i] >= min && result[i] <= max;
                    if (!valid) System.out.println("showIntegerArrayInputDialog: Element " + responseArr[i] + " interpreted as " + result[i] + " deemed to be outside the boundaries [" + min + "," + max + "]");
                }
            } catch (NumberFormatException ex) { 
                System.out.println("showIntegerArrayInputDialog: Improperly formatted number with input: " + message);
            }
        } while (!valid);
        return result;
    }

    /** 
     * Shows option pane to retrieve a float value in provided range.
     * @return a value between min and max, or -1 if the user cancelled the dialog
     */
    public static float showFloatInputDialog(String message, float min, float max) {
        float num = 0;
        do {
            String response = JOptionPane.showInputDialog(message);
            if (response == null) return -1;
            try { num = Float.parseFloat(response); } catch (NumberFormatException ex) { System.out.println("Improperly formatted number..."); }
        } while (num < min || num > max);
        return num;
    }

    /** 
     * Shows option pane to retrieve a float value in provided range.
     * @return a value between min and max, or null if the user cancelled the dialog
     */
    public static float[] showFloatArrayInputDialog(String message, float min, float max) {
        float[] result = null;
        boolean valid = false;
        do {
            String response = JOptionPane.showInputDialog(message);
            if (response == null) return null;
            String[] responseArr = response.split(",");
            result = new float[responseArr.length];
            try {
                float sum = 0f;
                for (int i = 0; i < responseArr.length; i++) {
                    result[i] = Float.valueOf(responseArr[i].trim());
                    sum += result[i];
                    valid = valid && (result[i] >= min && result[i] <= max);
                }
                valid = valid && sum == 1f;
            } catch (NumberFormatException ex) { System.out.println("Improperly formatted number..."); }
        } while (!valid);
        return result;
    }

}
