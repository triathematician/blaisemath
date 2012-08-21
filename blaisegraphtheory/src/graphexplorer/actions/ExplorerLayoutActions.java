/*
 * GraphLayout.java
 * Created Jul 14, 2010
 */

package graphexplorer.actions;

import graphexplorer.controller.GraphLayoutController;
import graphexplorer.controller.LongitudinalGraphController;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.KeyStroke;
import javax.swing.ProgressMonitor;
import org.bm.blaise.scio.graph.layout.SimultaneousLayout;
import org.bm.blaise.scio.graph.layout.SpringLayout;
import org.bm.blaise.scio.graph.layout.StaticGraphLayout;

/**
 * Describes layout actions supporting the graph explorer app.
 * @author Elisha Peterson
 */
public class ExplorerLayoutActions {

    /** What this class works with */
    private GraphLayoutController controller;
    /** Construction requires a controller */
    public ExplorerLayoutActions(GraphLayoutController controller) {
        setController(controller);
    }
    
    public void setController(GraphLayoutController controller) {
        this.controller = controller;
        boolean nonNull = controller != null;
        LAYOUT_CIRCULAR.setEnabled(nonNull);
        LAYOUT_RANDOM.setEnabled(nonNull);
        LAYOUT_SPRING_STATIC.setEnabled(nonNull);
        LAYOUT_ENERGY_START.setEnabled(nonNull);
        LAYOUT_STOP.setEnabled(nonNull);
        LAYOUT_ITERATE.setEnabled(nonNull);
    }

    public Action LAYOUT_CIRCULAR = new AbstractAction("Circular layout", ExplorerActions.loadIcon("layout-circle18")) {
        {
            putValue(SHORT_DESCRIPTION, "Place nodes of current graph around a circle.");
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.ALT_MASK));
            putValue(MNEMONIC_KEY, KeyEvent.VK_C);
            setEnabled(true);
        }
        public void actionPerformed(ActionEvent e) {
            if (controller != null)
                controller.applyLayout(StaticGraphLayout.CIRCLE, 5.0);
        }
    };

    public Action LAYOUT_RANDOM = new AbstractAction("Random layout", ExplorerActions.loadIcon("layout-random18")) {
        {
            putValue(SHORT_DESCRIPTION, "Place nodes of current graph in random locations.");
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.ALT_MASK));
            putValue(MNEMONIC_KEY, KeyEvent.VK_R);
            setEnabled(true);
        }
        public void actionPerformed(ActionEvent e) {
            if (controller != null)
                controller.applyLayout(StaticGraphLayout.RANDOM, 5.0);
        }
    };

    public Action LAYOUT_SPRING_STATIC = new BackgroundLayoutAction(
            "Spring layout (static)", ExplorerActions.loadIcon("layout-spring18"),
            "Place nodes of current graph using an iterated spring layout algorithm (runs in background).");

    class BackgroundLayoutAction extends AbstractAction implements Runnable {
        Thread loaderThread = null;
        ProgressMonitor pm;
        public BackgroundLayoutAction(String text, Icon icon, String description) {
            super(text, icon);
            putValue(SHORT_DESCRIPTION, description);
        }
        public void actionPerformed(ActionEvent e) {
            if (loaderThread != null)
                return;
            pm = new ProgressMonitor(null, "Constructing layout...", null, 0, 1);
            loaderThread = new Thread(this);
            loaderThread.start();
        }
        public void run() {
            if (controller != null)
                controller.applyLayout(SpringLayout.STATIC_INSTANCE, 5.0);
            pm.setProgress(1);
            loaderThread = null;
        }
    };

    public Action LAYOUT_ENERGY_START = new AbstractAction("Spring layout - start", ExplorerActions.loadIcon("play18")) {
        {
            putValue(SHORT_DESCRIPTION, "Set the active layout algorithm to a spring-based layout algorithm," +
                    " and begin animation.");
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.ALT_MASK));
            putValue(MNEMONIC_KEY, KeyEvent.VK_E);
            setEnabled(true);
        }
        public void actionPerformed(ActionEvent e) {
            if (controller != null) {
                controller.setLayoutAlgorithm(new SpringLayout(controller.getPositions()));
                controller.setAnimating(true);
            }
        }
    };

    public Action LAYOUT_TIME_START = new AbstractAction("Time Spring layout - start", ExplorerActions.loadIcon("play18")) {
        {
            putValue(SHORT_DESCRIPTION, "Set the active layout algorithm to a time/spring-based layout algorithm," +
                    " and begin animation.");
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.ALT_MASK));
            putValue(MNEMONIC_KEY, KeyEvent.VK_E);
            setEnabled(false);
        }
        public void actionPerformed(ActionEvent e) {
//            if (controller != null && controller instanceof LongitudinalGraphController) {
//                final LongitudinalGraphController lgc = (LongitudinalGraphController) controller;
//                final SimultaneousLayout sl = new SimultaneousLayout(lgc.getLongitudinalGraph());
//                sl.setCurTime(lgc.getTime());
//                controller.addPropertyChangeListener(LongitudinalGraphController.$TIME, new PropertyChangeListener(){
//                    public void propertyChange(PropertyChangeEvent evt) {
//                        sl.setCurTime(lgc.getTime());
//                    }
//                });
//                controller.setLayoutAlgorithm(sl);
//                controller.setLayoutAnimating(true);
//            }
        }
    };

    public Action LAYOUT_ITERATE = new AbstractAction("Iterate layout", ExplorerActions.loadIcon("step18")) {
        {
            putValue(SHORT_DESCRIPTION, "Runs a single iteration of the currently active iterative layout algorithm.");
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.ALT_MASK));
            putValue(MNEMONIC_KEY, KeyEvent.VK_I);
            setEnabled(true);
        }
        public void actionPerformed(ActionEvent e) {
            if (controller != null)
                controller.stepLayout();
        }
    };

    public Action LAYOUT_STOP = new AbstractAction("Stop layout animation", ExplorerActions.loadIcon("stop18")) {
        {
            putValue(SHORT_DESCRIPTION, "Stop animation of the currently active iterative layout algorithm.");
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.ALT_MASK));
            putValue(MNEMONIC_KEY, KeyEvent.VK_S);
            setEnabled(true);
        }
        public void actionPerformed(ActionEvent e) {
            if (controller != null)
                controller.setAnimating(false);
        }
    };

}
