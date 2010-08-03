/*
 * GraphExplorerFileActions.java
 * Created Jul 12, 2010
 */

package graphexplorer;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.TreeMap;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.KeyStroke;
import org.bm.blaise.scio.graph.Graph;
import org.bm.blaise.scio.graph.LongitudinalGraph;
import org.bm.blaise.scio.graph.io.*;
import org.bm.blaise.scio.graph.io.AbstractGraphIO.GraphType;

/**
 * Describes file/IO actions supporting the graph explorer app.
 * @author Elisha Peterson
 */
class ExplorerIOActions {

    /** What this class works with */
    GraphControllerMaster master;

    /** Construction requires a master controller class */
    public ExplorerIOActions(GraphControllerMaster master) { this.master = master; }
    
    transient JFileChooser fc;
    transient File openFile;

    /** Lazy initialization */
    void initFileChooser() { if (fc == null) fc = new JFileChooser(); }

    public Action LOAD_EDGELIST_ACTION = new LoadAction(-1, EdgeListGraphIO.getInstance(), GraphType.REGULAR);
    public Action SAVE_EDGELIST_ACTION = new SaveAction(-1, EdgeListGraphIO.getInstance(), GraphType.REGULAR);

    public Action LOAD_PAJEK_ACTION = new LoadAction(KeyEvent.VK_O, PajekGraphIO.getInstance(), GraphType.REGULAR);
    public Action SAVE_PAJEK_ACTION = new SaveAction(KeyEvent.VK_S, PajekGraphIO.getInstance(), GraphType.REGULAR);

    public Action LOAD_PAJEKLONG_ACTION = new LoadAction(-1, PajekGraphIO.getInstance(), GraphType.LONGITUDINAL);
    public Action SAVE_PAJEKLONG_ACTION = new SaveAction(-1, PajekGraphIO.getInstance(), GraphType.LONGITUDINAL);

    public Action LOAD_PAJEKX_ACTION = new LoadAction(-1, PajekGraphIO.getExtendedInstance(), GraphType.REGULAR);
    public Action SAVE_PAJEKX_ACTION = new SaveAction(-1, PajekGraphIO.getExtendedInstance(), GraphType.REGULAR);

    public Action LOAD_PAJEKXLONG_ACTION = new LoadAction(-1, LongitudinalGraphIO.getInstance(), GraphType.LONGITUDINAL);
    public Action SAVE_PAJEKXLONG_ACTION = new SaveAction(-1, LongitudinalGraphIO.getInstance(), GraphType.LONGITUDINAL);

    public Action LOAD_UCINET_ACTION = new LoadAction(-1, UCINetGraphIO.getInstance(), GraphType.REGULAR);
    public Action SAVE_UCINET_ACTION = new SaveAction(-1, UCINetGraphIO.getInstance(), GraphType.REGULAR);

    public Action CLOSE_ACTION = new AbstractAction("Close graph") {
        {
            putValue(SHORT_DESCRIPTION, "Closes current graph window");
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.CTRL_MASK));
            putValue(MNEMONIC_KEY, KeyEvent.VK_W);
        }
        public void actionPerformed(ActionEvent e) {
            master.closeController();
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
        AbstractGraphIO loader;
        GraphType type;
        LoadAction(int accelerator, AbstractGraphIO loader, GraphType type) {
            super("Load graph");
            this.loader = loader;
            this.type = type;
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
            if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                openFile = fc.getSelectedFile();
                TreeMap<Integer, double[]> loc = new TreeMap<Integer, double[]>();
                GraphType loadType = type;
                Object loaded = loader.importGraph(loc, openFile, loadType);
                if (loaded == null) {
                    master.output("Attempt to load file " + openFile + " failed!");
                    return;
                }
                GraphController gc = null;
                switch (loadType) {
                    case REGULAR:
                        gc = GraphController.getInstance((Graph<Integer>) loaded, openFile.getName());
                        if (loc != null && loc.size() > 0) {
                            LinkedHashMap<Object,Point2D.Double> pos = new LinkedHashMap<Object,Point2D.Double>();
                            for (Entry<Integer,double[]> en : loc.entrySet())
                                pos.put(en.getKey(), new Point2D.Double(en.getValue()[0], en.getValue()[1]));
                            gc.setPositions(pos);
                        }
                        break;
                    case LONGITUDINAL:
                        gc = GraphController.getInstance((LongitudinalGraph<Integer>) loaded, openFile.getName());
                        break;
                }
                if (gc != null)
                    master.setActiveController(gc);
                else
                    master.output("Attempt to load file " + openFile + " failed!");
            }
        }
    }

    class SaveAction extends AbstractAction {
        AbstractGraphIO loader;
        GraphType type;
        SaveAction(int accelerator, AbstractGraphIO loader, GraphType type) {
            super("Save graph");
            this.loader = loader;
            this.type = type;
            putValue(SHORT_DESCRIPTION, "Save current graph to a file");
            if (accelerator != -1) {
                putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(accelerator, InputEvent.CTRL_MASK));
                putValue(MNEMONIC_KEY, accelerator);
            }
        }
//        @Override
//        public boolean isEnabled() {
//            return main.activeGraph() != null;
//        }
        public void actionPerformed(ActionEvent e) {
            GraphController gc = master.getActiveController();
            if (gc == null || !(gc.valid())) {
                master.output("Save failed: no graph active");
                return;
            }

            initFileChooser();
            fc.setApproveButtonText("Save");
            if (openFile != null) {
                fc.setCurrentDirectory(openFile);
                fc.setSelectedFile(openFile);
            }
            if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                openFile = fc.getSelectedFile();
                Object active = gc.getPrimaryGraph();
                GraphType saveType = loader.saveGraph(active, gc.getPositions(), openFile);
                if (saveType == null)
                    gc.output("Attempt to save file " + openFile + " failed!");
                else
                    gc.output("Saved graph to file " + openFile + ".");
            }
        }
    }

    // movie export actions

    Action EXPORT_QT = new AbstractAction() {
        {
            putValue(SHORT_DESCRIPTION, "Export longitudinal graph as a QuickTime movie (.mov)");
        }
        public void actionPerformed(ActionEvent e) {
            GraphController gc = master.getActiveController();
            if (!gc.isLongitudinal()) {
                gc.output("Movie output not supported: QTJava not found");
//                initFileChooser();
//                fc.setApproveButtonText("Export");
//                if (openFile != null) {
//                    fc.setCurrentDirectory(openFile);
//                    fc.setSelectedFile(openFile);
//                }
//                if (fc.showOpenDialog(main.thisComponent()) == JFileChooser.APPROVE_OPTION) {
//                    openFile = fc.getSelectedFile();
//                    MovieExport me = new MovieExport(openFile);
//                    ExportMovieTask emt = new ExportMovieTask(main, (LongitudinalGraphPanel) main.activePanel(), me);
//                    emt.run();
//                }
            }
        }
    };
}
