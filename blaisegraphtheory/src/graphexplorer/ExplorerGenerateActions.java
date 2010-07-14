/*
 * ExplorerGenerateActions.java
 * Created May 14, 2010
 */

package graphexplorer;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;
import org.bm.blaise.scio.graph.Graph;
import org.bm.blaise.scio.graph.GraphFactory;
import org.bm.blaise.scio.graph.PreferentialAttachment;
import org.bm.blaise.scio.graph.RandomGraph;

/**
 * Describes graph-generation actions supporting the graph explorer app.
 * @author Elisha Peterson
 */
class ExplorerGenerateActions {

    /** What this class works with */
    GraphExplorerMain main;
    /** Construction requires a main class */
    public ExplorerGenerateActions(GraphExplorerMain main) { this.main = main; }

    public Action GENERATE_EMPTY = new AbstractAction("Empty graph") {
        {
            putValue(SHORT_DESCRIPTION, "Generates an empty graph with specified number of vertices");
            putValue(MNEMONIC_KEY, KeyEvent.VK_E);
            setEnabled(true);
        }
        public void actionPerformed(ActionEvent e) {
            int num = showIntegerInputDialog("Enter number of vertices for empty graph (up to 1 million).", 1, 1000000);
            if (num == -1) return;
            main.loadGraph(GraphFactory.getEmptyGraph(num, false), "Empty graph");
        }
    };

    public Action GENERATE_COMPLETE = new AbstractAction("Complete graph") {
        {
            putValue(SHORT_DESCRIPTION, "Generates a complete graph with specified number of vertices");
            putValue(MNEMONIC_KEY, KeyEvent.VK_X);
            setEnabled(true);
        }
        public void actionPerformed(ActionEvent e) {
            int num = showIntegerInputDialog("Enter number of vertices for complete graph (up to 1000).", 1, 1000);
            if (num == -1) return;
            main.loadGraph(GraphFactory.getCompleteGraph(num, false), "Complete graph");
        }
    };

    public Action GENERATE_CIRCLE = new AbstractAction("Circle graph") {
        {
            putValue(SHORT_DESCRIPTION, "Generates a circle graph with specified number of vertices");
            putValue(MNEMONIC_KEY, KeyEvent.VK_C);
            setEnabled(true);
        }
        public void actionPerformed(ActionEvent e) {
            int num = showIntegerInputDialog("Enter number of vertices for circle graph (up to 1 million).", 1, 1000000);
            if (num == -1) return;
            main.loadGraph(GraphFactory.getCycleGraph(num, false), "Circle graph");
        }
    };

    public Action GENERATE_STAR = new AbstractAction("Star graph") {
        {
            putValue(SHORT_DESCRIPTION, "Generates graph with central hub connected to all other vertices");
            putValue(MNEMONIC_KEY, KeyEvent.VK_H);
            setEnabled(true);
        }
        public void actionPerformed(ActionEvent e) {
            int num = showIntegerInputDialog("Enter number of vertices for star graph (up to 1 million).", 1, 1000000);
            if (num == -1) return;
            main.loadGraph(GraphFactory.getStarGraph(num), "Star graph");
        }
    };

    public Action GENERATE_WHEEL = new AbstractAction("Wheel graph") {
        {
            putValue(SHORT_DESCRIPTION, "Generates a wheel graph with specified number of vertices");
            putValue(MNEMONIC_KEY, KeyEvent.VK_W);
            setEnabled(true);
        }
        public void actionPerformed(ActionEvent e) {
            int num = showIntegerInputDialog("Enter number of vertices for wheel graph (up to 1 million).", 1, 1000000);
            if (num == -1) return;
            main.loadGraph(GraphFactory.getWheelGraph(num), "Wheel graph");
        }
    };

    public Action GENERATE_RANDOM_EDGE = new AbstractAction("Uniform (by number of edges)") {
        {
            putValue(SHORT_DESCRIPTION, "Generate random graph by selecting edges at random");
            putValue(MNEMONIC_KEY, KeyEvent.VK_U);
            setEnabled(true);
        }
        public void actionPerformed(ActionEvent e) {
            int num = showIntegerInputDialog("Enter number of vertices for random graph (up to 1 million).", 1, 1000000);
            if (num == -1) return;
            int maxEdges = Math.max(num*(num-1)/2,10000000);
            int numE = showIntegerInputDialog("Enter number of edges in random graph (up to " + maxEdges + ").", 0, maxEdges);
            if (numE == -1) return;
            main.loadGraph(RandomGraph.getInstance(num, numE, false), "Random graph");
        }
    };

    public Action GENERATE_RANDOM_PROBABILITY = new AbstractAction("Uniform (by probability)") {
        {
            putValue(SHORT_DESCRIPTION, "Generate random graph by applying a uniform probability to each edge");
            putValue(MNEMONIC_KEY, KeyEvent.VK_V);
            setEnabled(true);
        }
        public void actionPerformed(ActionEvent e) {
            int num = showIntegerInputDialog("Enter number of vertices for random graph (up to 100000).", 1, 100000);
            if (num == -1) return;
            float prob = showFloatInputDialog("Enter probability for each edge", 0f, 1f);
            if (prob == -1) return;
            main.loadGraph(RandomGraph.getInstance(num, prob, false), "Random graph");
        }
    };

    public Action GENERATE_PREFERENTIAL = new AbstractAction("Preferential attachment") {
        {
            putValue(SHORT_DESCRIPTION, "Generate random graph using preferential attachment algorithm");
            putValue(MNEMONIC_KEY, KeyEvent.VK_P);
            setEnabled(true);
        }
        public void actionPerformed(ActionEvent e) {
            int num = showIntegerInputDialog("Enter number of vertices in seed graph (should be small).", 1, 10000);
            if (num == -1) return;
            float prob = showFloatInputDialog("Enter probability for each edge in seed graph", 0f, 1f);
            if (prob == -1) return;
            int num2 = showIntegerInputDialog("Enter number of vertices in final graph (up to 1000000)", 1, 1000000);
            if (num2 == -1) return;
            int num3 = showIntegerInputDialog("Enter number of edges to add with each vertex", 0, 1000);
            if (num3 == -1) return;
            Graph<Integer> seed = RandomGraph.getInstance(num, prob, false);
            main.loadGraph(PreferentialAttachment.getSeededInstance(seed, num2, num3), "Preferential attachment graph");
        }
    };


    public Action GENERATE_PREFERENTIAL_LONG = new AbstractAction("Preferential attachment (longitudinal)") {
        {
            putValue(SHORT_DESCRIPTION, "Generate random graph using preferential attachment algorithm");
            putValue(MNEMONIC_KEY, KeyEvent.VK_P);
            setEnabled(true);
        }
        public void actionPerformed(ActionEvent e) {
            int num = showIntegerInputDialog("Enter number of vertices in seed graph (should be small).", 1, 10000);
            if (num == -1) return;
            float prob = showFloatInputDialog("Enter probability for each edge in seed graph", 0f, 1f);
            if (prob == -1) return;
            int num2 = showIntegerInputDialog("Enter number of vertices in final graph (up to 1000000)", 1, 1000000);
            if (num2 == -1) return;
            int num3 = showIntegerInputDialog("Enter number of edges to add with each vertex", 0, 1000);
            if (num3 == -1) return;
            Graph<Integer> seed = RandomGraph.getInstance(num, prob, false);
            main.loadLongitudinalGraph(PreferentialAttachment.getLongitudinalSeededInstance(seed, num2, num3), "Preferential attachment graph");
        }
    };

    public Action GENERATE_PREFERENTIAL2 = new AbstractAction("Preferential attachment (varied edge connections)") {
        {
            putValue(SHORT_DESCRIPTION, "Generate random graph using preferential attachment algorithm, with varied numbers of addon edge connections");
            putValue(MNEMONIC_KEY, KeyEvent.VK_P);
            setEnabled(true);
        }
        public void actionPerformed(ActionEvent e) {
            int num = showIntegerInputDialog("Enter number of vertices in seed graph (should be small).", 1, 10000);
            if (num == -1) return;
            float prob = showFloatInputDialog("Enter probability for each edge in seed graph", 0f, 1f);
            if (prob == -1) return;
            int num2 = showIntegerInputDialog("Enter number of vertices in final graph (up to 1000000)", 1, 1000000);
            if (num2 == -1) return;
            float[] probs = showFloatArrayInputDialog("Enter probabilities of connecting to i vertices as a list of values adding up to 1, e.g. '0.25,0.5,0.25'.", 0f, 1f);
            if (probs == null) return;
            Graph<Integer> seed = RandomGraph.getInstance(num, prob, false);
            main.loadGraph(PreferentialAttachment.getSeededInstance(seed, num2, probs), "Preferential attachment graph");
        }
    };

    public Action GENERATE_PREFERENTIAL2_LONG = new AbstractAction("Preferential attachment (longitudinal, varied edge connections)") {
        {
            putValue(SHORT_DESCRIPTION, "Generate random graph using preferential attachment algorithm, with varied numbers of addon edge connections");
            putValue(MNEMONIC_KEY, KeyEvent.VK_P);
            setEnabled(true);
        }
        public void actionPerformed(ActionEvent e) {
            int num = showIntegerInputDialog("Enter number of vertices in seed graph (should be small).", 1, 10000);
            if (num == -1) return;
            float prob = showFloatInputDialog("Enter probability for each edge in seed graph", 0f, 1f);
            if (prob == -1) return;
            int num2 = showIntegerInputDialog("Enter number of vertices in final graph (up to 1000000)", 1, 1000000);
            if (num2 == -1) return;
            float[] probs = showFloatArrayInputDialog("Enter probabilities of connecting to i vertices as a list of values adding up to 1, e.g. '0.25,0.5,0.25'.", 0f, 1f);
            if (probs == null) return;
            Graph<Integer> seed = RandomGraph.getInstance(num, prob, false);
            main.loadLongitudinalGraph(PreferentialAttachment.getLongitudinalSeededInstance(seed, num2, probs), "Preferential attachment graph");
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
                    result[i] = Float.valueOf(responseArr[i]);
                    sum += result[i];
                }
                valid = sum == 1f;
            } catch (NumberFormatException ex) { System.out.println("Improperly formatted number..."); }
        } while (!valid);
        return result;
    }

}
