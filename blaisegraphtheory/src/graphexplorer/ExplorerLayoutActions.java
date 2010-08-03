/*
 * GraphLayout.java
 * Created Jul 14, 2010
 */

package graphexplorer;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;
import org.bm.blaise.scio.graph.layout.EnergyLayout;
import org.bm.blaise.scio.graph.layout.StaticGraphLayout;

/**
 * Describes layout actions supporting the graph explorer app.
 * @author Elisha Peterson
 */
class ExplorerLayoutActions {

    /** What this class works with */
    GraphController controller;
    /** Construction requires a controller */
    public ExplorerLayoutActions(GraphController controller) { this.controller = controller; }

    public Action LAYOUT_CIRCULAR = new AbstractAction("Circular layout") {
        {
            putValue(SHORT_DESCRIPTION, "Layout vertices of current graph around a circle");
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.ALT_MASK));
            putValue(MNEMONIC_KEY, KeyEvent.VK_C);
            setEnabled(true);
        }
        public void actionPerformed(ActionEvent e) {
            if (controller.valid())
                controller.applyLayout(StaticGraphLayout.CIRCLE, 5.0);
        }
    };

    public Action LAYOUT_RANDOM = new AbstractAction("Random layout") {
        {
            putValue(SHORT_DESCRIPTION, "Layout vertices in random locations");
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.ALT_MASK));
            putValue(MNEMONIC_KEY, KeyEvent.VK_R);
            setEnabled(true);
        }
        public void actionPerformed(ActionEvent e) {
            if (controller.valid())
                controller.applyLayout(StaticGraphLayout.RANDOM, 5.0);
        }
    };

    public Action LAYOUT_ENERGY_START = new AbstractAction("Energy layout - start") {
        {
            putValue(SHORT_DESCRIPTION, "Starts energy layout animation");
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.ALT_MASK));
            putValue(MNEMONIC_KEY, KeyEvent.VK_E);
            setEnabled(true);
        }
        public void actionPerformed(ActionEvent e) {
            if (controller.valid()) {
                controller.setIterativeLayout(new EnergyLayout(controller.getPositions()));
                controller.animateLayout();
            }
        }
    };

    public Action LAYOUT_ENERGY_ITERATE = new AbstractAction("Energy layout - iterate") {
        {
            putValue(SHORT_DESCRIPTION, "Iterates energy layout animation");
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.ALT_MASK));
            putValue(MNEMONIC_KEY, KeyEvent.VK_I);
            setEnabled(true);
        }
        public void actionPerformed(ActionEvent e) {
            if (controller.valid())
                controller.stepLayout();
        }
    };

    public Action LAYOUT_ENERGY_STOP = new AbstractAction("Energy layout - stop") {
        {
            putValue(SHORT_DESCRIPTION, "Stops energy layout animation");
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.ALT_MASK));
            putValue(MNEMONIC_KEY, KeyEvent.VK_S);
            setEnabled(true);
        }
        public void actionPerformed(ActionEvent e) {
            controller.stopLayout();
        }
    };

}
