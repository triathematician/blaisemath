/*
 * GraphExplorerFileActions.java
 * Created Jul 12, 2010
 */

package graphexplorer;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.KeyStroke;
import org.bm.blaise.scio.graph.Graph;
import org.bm.blaise.scio.graph.LongitudinalGraph;
import org.bm.blaise.scio.graph.io.EdgeListGraphIO;
import org.bm.blaise.scio.graph.io.GraphIO;
import org.bm.blaise.scio.graph.io.LongitudinalGraphIO;
import org.bm.blaise.scio.graph.io.PajekGraphIO;

/**
 * Describes file/IO actions supporting the graph explorer app.
 * @author Elisha Peterson
 */
class ExplorerIOActions {

    /** What this class works with */
    GraphExplorerMain main;
    /** Construction requires a main class */
    public ExplorerIOActions(GraphExplorerMain main) { this.main = main; }


    JFileChooser fc;
    File openFile;

    void initFileChooser() {
        if (fc == null)
            fc = new JFileChooser();
    }

    public Action LOAD_EDGELIST_ACTION = new LoadAction(-1, EdgeListGraphIO.getInstance());
    public Action SAVE_EDGELIST_ACTION = new SaveAction(-1, EdgeListGraphIO.getInstance());

    public Action LOAD_PAJEK_ACTION = new LoadAction(KeyEvent.VK_O, PajekGraphIO.getInstance());
    public Action SAVE_PAJEK_ACTION = new SaveAction(KeyEvent.VK_S, PajekGraphIO.getInstance());

    public Action LOAD_PAJEKX_ACTION = new LoadAction(-1, PajekGraphIO.getExtendedInstance());
    public Action SAVE_PAJEKX_ACTION = new SaveAction(-1, PajekGraphIO.getExtendedInstance());

    public Action LOAD_PAJEKXLONG_ACTION = new LoadLongAction(LongitudinalGraphIO.getInstance());

    public Action CLOSE_ACTION = new AbstractAction("Close graph") {
        {
            putValue(SHORT_DESCRIPTION, "Closes current graph window");
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.CTRL_MASK));
            putValue(MNEMONIC_KEY, KeyEvent.VK_W);
        }
        public void actionPerformed(ActionEvent e) {
            main.closeActiveGraph();
        }
    };

    public Action QUIT_ACTION = new AbstractAction("Exit") {
        {
            putValue(SHORT_DESCRIPTION, "Exit Graph Explorer");
            putValue(MNEMONIC_KEY, KeyEvent.VK_X);
        }
        public void actionPerformed(ActionEvent e) {
            System.exit(0);
        }
    };

    class LoadAction extends AbstractAction {
        GraphIO loader;
        LoadAction(int accelerator, GraphIO loader) {
            super("Load graph");
            this.loader = loader;
            putValue(SHORT_DESCRIPTION, "Load a graph from a file");
            if (accelerator != -1) {
                putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(accelerator, InputEvent.CTRL_MASK));
                putValue(MNEMONIC_KEY, accelerator);
            }
        }
        public void actionPerformed(ActionEvent e) {
            initFileChooser();
            fc.setApproveButtonText("Load");
            if (openFile != null) {
                fc.setCurrentDirectory(openFile);
                fc.setSelectedFile(openFile);
            }
            if (fc.showOpenDialog(main) == JFileChooser.APPROVE_OPTION) {
                openFile = fc.getSelectedFile();
                Graph<Integer> sg = loader.importGraph(openFile);
                if (sg != null)
                    main.loadGraph(sg, openFile.getName());
                else
                    main.output("Attempt to load file " + openFile + " failed!");
            }
        }
    }

    class LoadLongAction extends AbstractAction {
        GraphIO loader;
        LoadLongAction(GraphIO loader) {
            super("Load longitudinal graph");
            this.loader = loader;
            putValue(SHORT_DESCRIPTION, "Load a longitudinal graph from a file");
        }
        public void actionPerformed(ActionEvent e) {
            initFileChooser();
            fc.setApproveButtonText("Load");
            if (openFile != null) {
                fc.setCurrentDirectory(openFile);
                fc.setSelectedFile(openFile);
            }
            if (fc.showOpenDialog(main) == JFileChooser.APPROVE_OPTION) {
                openFile = fc.getSelectedFile();
                LongitudinalGraph<Integer> sg = loader.importLongitudinalGraph(openFile);
                if (sg != null && sg.getTimes().size() > 0)
                    main.loadLongitudinalGraph(sg, openFile.getName());
                else
                    main.output("Attempt to load file " + openFile + " failed!");
            }
        }
    }

    class SaveAction extends AbstractAction {
        GraphIO loader;
        SaveAction(int accelerator, GraphIO loader) {
            super("Save graph");
            this.loader = loader;
            putValue(SHORT_DESCRIPTION, "Save current graph to a file");
            if (accelerator != -1) {
                putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(accelerator, InputEvent.CTRL_MASK));
                putValue(MNEMONIC_KEY, accelerator);
            }
        }
        public void actionPerformed(ActionEvent e) {
            initFileChooser();
            fc.setApproveButtonText("Save");
            if (openFile != null) {
                fc.setCurrentDirectory(openFile);
                fc.setSelectedFile(openFile);
            }
            if (fc.showOpenDialog(main) == JFileChooser.APPROVE_OPTION) {
                openFile = fc.getSelectedFile();
                Graph active = main.activeGraph.getGraph();
                if (active != null) {
                    loader.saveGraph(active, openFile);
                    main.output("Saved graph to file " + openFile + ".");
                }
            }
        }
    }
}
