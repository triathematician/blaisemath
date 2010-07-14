/*
 * GraphLayout.java
 * Created Jul 14, 2010
 */

package graphexplorer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;
import org.bm.blaise.scio.graph.Graph;
import org.bm.blaise.scio.graph.layout.EnergyLayout;
import org.bm.blaise.scio.graph.layout.StaticGraphLayout;
import stormtimer.BetterTimer;

/**
 * Describes layout actions supporting the graph explorer app.
 * @author Elisha Peterson
 */
class ExplorerLayoutActions {

    /** What this class works with */
    GraphExplorerMain main;
    /** Construction requires a main class */
    public ExplorerLayoutActions(GraphExplorerMain main) { this.main = main; }

    BetterTimer timer;
    boolean updateEL;

    public Action LAYOUT_CIRCULAR = new AbstractAction("Circular layout") {
        {
            putValue(SHORT_DESCRIPTION, "Layout vertices of current graph around a circle");
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.ALT_MASK));
            putValue(MNEMONIC_KEY, KeyEvent.VK_C);
            setEnabled(true);
        }
        public void actionPerformed(ActionEvent e) {
            if (main.activeGraph != null) {
                main.activeGraph.applyLayout(StaticGraphLayout.CIRCLE, 5.0);
                updateEL = true;
            }
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
            if (main.activeGraph != null) {
                main.activeGraph.applyLayout(StaticGraphLayout.RANDOM, 5.0);
                updateEL = true;
            }
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
            if (main.activeGraph != null) {
                if (main.activeLayout == null) {
                    main.activeLayout = new EnergyLayout(main.activeGraph.getGraph(), main.activeGraph.getPoint());
                } else if (updateEL)
                    main.activeLayout.reset(main.activeGraph.getGraph(), main.activeGraph.getPoint());
                timer = new BetterTimer(100);
                timer.addActionListener(new ActionListener(){
                    public void actionPerformed(ActionEvent e) {
                        if (updateEL)
                            main.activeLayout.reset(main.activeGraph.getGraph(), main.activeGraph.getPoint());
                        main.activeGraph.setPoint(main.activeLayout.iterate(main.activeGraph.getGraph()));
                    }
                });
                timer.start();
            }
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
            if (timer != null)
                timer.stop();
            timer = null;
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
            if (main.activeGraph != null) {
                Graph graph = main.activeGraph.getGraph();
                if (main.activeLayout == null) {
                    main.activeLayout = new EnergyLayout(graph, main.activeGraph.getPoint());
                } else if (updateEL)
                    main.activeLayout.reset(graph, main.activeGraph.getPoint());
                main.activeGraph.setPoint(main.activeLayout.iterate(graph));
            }
        }
    };

}
