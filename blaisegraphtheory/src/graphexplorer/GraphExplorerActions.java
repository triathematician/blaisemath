/*
 * GraphExplorerActions.java
 * Created May 14, 2010
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
import org.bm.blaise.scio.graph.SimpleGraph;
import org.bm.blaise.scio.graph.io.SimpleGraphIO;
import org.bm.blaise.scio.graph.layout.StaticGraphLayout;

/**
 * Describes actions supporting the graph explorer app.
 * @author Elisha Peterson
 */
public class GraphExplorerActions {

    /** What this class works with */
    GraphExplorerMain main;
    public GraphExplorerActions() {
        this(null);
    }
    public GraphExplorerActions(GraphExplorerMain main) {
        this.main = main;
    }
    public GraphExplorerMain getMain() {
        return main;
    }
    public void setMain(GraphExplorerMain main) {
        this.main = main;
    }

    //
    // FILE HANDLING
    //

    JFileChooser fc;
    File openFile;

    void initFileChooser() {
        if (fc == null)
            fc = new JFileChooser();
    }

    public Action LOAD_ACTION = new AbstractAction("Load graph from file") {
        {
            putValue(SHORT_DESCRIPTION, "Load a graph from a file");
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
            putValue(MNEMONIC_KEY, KeyEvent.VK_O);
            setEnabled(true);
        }
        public void actionPerformed(ActionEvent e) {
            initFileChooser();
            if (openFile != null) {
                fc.setCurrentDirectory(openFile);
                fc.setSelectedFile(openFile);
            }
            if (fc.showOpenDialog(main) == JFileChooser.APPROVE_OPTION) {
                openFile = fc.getSelectedFile();
                SimpleGraph sg = SimpleGraphIO.importSimpleGraph(openFile);
                if (sg != null)
                    main.loadGraph(sg, openFile.getName());
                else
                    main.output("Attempt to load file " + openFile + " failed!");
            }
        }
    };


    //
    // LAYOUTS
    //

    public Action LAYOUT_CIRCULAR = new AbstractAction("Circular layout") {
        public void actionPerformed(ActionEvent e) {
            if (main.activeGraph != null)
                main.activeGraph.applyLayout(StaticGraphLayout.CIRCLE);
        }
    };

    public Action LAYOUT_RANDOM = new AbstractAction("Random layout") {
        public void actionPerformed(ActionEvent e) {
            if (main.activeGraph != null)
                main.activeGraph.applyLayout(StaticGraphLayout.RANDOM);
        }
    };


}
