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
import java.util.List;
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
    GraphExplorerInterface main;
    /** Construction requires a main class */
    public ExplorerIOActions(GraphExplorerInterface main) { this.main = main; }


    JFileChooser fc;
    File openFile;

    void initFileChooser() {
        if (fc == null)
            fc = new JFileChooser();
    }

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
            if (fc.showOpenDialog(main.thisComponent()) == JFileChooser.APPROVE_OPTION) {
                openFile = fc.getSelectedFile();
                TreeMap<Integer, double[]> loc = new TreeMap<Integer, double[]>();
                GraphType loadType = type;
                Object loaded = loader.importGraph(loc, openFile, loadType);
                if (loaded == null) {
                    main.output("Attempt to load file " + openFile + " failed!");
                    return;
                }
                switch (loadType) {
                    case REGULAR:
                        Graph<Integer> sg = (Graph<Integer>) loaded;
                        main.loadGraph(sg, openFile.getName());
                        if (loc != null && loc.size() > 0) {
                            Point2D.Double[] pos = new Point2D.Double[loc.size()];
                            int i = 0;
                            for (double[] val : loc.values())
                                pos[i++] = new Point2D.Double(val[0], val[1]);
                            main.setActivePoints(pos);
                        }
                        break;
                    case LONGITUDINAL:
                        LongitudinalGraph<Integer> lg = (LongitudinalGraph<Integer>) loaded;
                        if (lg != null && lg.getTimes().size() > 0)
                            main.loadLongitudinalGraph(lg, openFile.getName());
                        else
                            main.output("Attempt to load file " + openFile + " failed!");
                        break;
                }
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
            if (main.activeGraph() == null || (type == GraphType.LONGITUDINAL && !main.isLongitudinal()))
                return;
            initFileChooser();
            fc.setApproveButtonText("Save");
            if (openFile != null) {
                fc.setCurrentDirectory(openFile);
                fc.setSelectedFile(openFile);
            }
            if (fc.showOpenDialog(main.thisComponent()) == JFileChooser.APPROVE_OPTION) {
                openFile = fc.getSelectedFile();
                Object active = null;
                switch (type) {
                    case REGULAR:
                        active = main.activeGraph();
                        break;
                    case LONGITUDINAL:
                        LongitudinalGraphPanel lgp = (LongitudinalGraphPanel) main.activePanel();
                        active = lgp.getLongitudinalGraph();
                        break;
                }
                GraphType saveType = loader.saveGraph(active, main.getActivePoints(), openFile);
                if (saveType == null)
                    main.output("Attempt to save file " + openFile + " failed!");
                else
                    main.output("Saved graph to file " + openFile + ".");
            }
        }
    }

    // movie export actions

    Action EXPORT_QT = new AbstractAction() {
        {
            putValue(SHORT_DESCRIPTION, "Export longitudinal graph as a QuickTime movie (.mov)");
        }
        public void actionPerformed(ActionEvent e) {
            if (main.isLongitudinal()) {
                initFileChooser();
                fc.setApproveButtonText("Export");
                if (openFile != null) {
                    fc.setCurrentDirectory(openFile);
                    fc.setSelectedFile(openFile);
                }
                if (fc.showOpenDialog(main.thisComponent()) == JFileChooser.APPROVE_OPTION) {
                    openFile = fc.getSelectedFile();
                    MovieExport me = new MovieExport(openFile);
                    ExportMovieTask emt = new ExportMovieTask(main, (LongitudinalGraphPanel) main.activePanel(), me);
                    emt.run();
                }
            }
        }
    };
}
