/*
 * GraphExplorerFileActions.java
 * Created Jul 12, 2010
 */

package graphexplorer;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.KeyStroke;
import javax.swing.ProgressMonitor;
import org.bm.blaise.scio.graph.Graph;
import org.bm.blaise.scio.graph.LongitudinalGraph;
import org.bm.blaise.scio.graph.ValuedGraph;
import org.bm.blaise.scio.graph.io.*;
import org.bm.blaise.scio.graph.io.AbstractGraphIO.GraphType;
import visometry.PlotComponent;

/**
 * Describes file/IO actions supporting the graph explorer app.
 * @author Elisha Peterson
 */
class ExplorerIOActions {

    /** What this class works with */
    GraphControllerMaster master;

    /** Construction requires a master controller class */
    public ExplorerIOActions(final GraphControllerMaster master) {
        this.master = master;
        master.addPropertyChangeListener("active", new PropertyChangeListener(){
            public void propertyChange(PropertyChangeEvent evt) {
                GraphController active = master.getActiveController();
                boolean status = active != null;
                SAVE_ACTION.setEnabled(status);
                SAVE_AS_ACTION.setEnabled(status);
                CLOSE_ACTION.setEnabled(status);
            }
        });
    }
    
    transient static GraphFileChooser fc;
    transient static File openFile;

    /** Lazy initialization */
    static void initFileChooser() {
        if (fc == null)
            fc = new GraphFileChooser();
    }

    public Action LOAD_ACTION = new LoadAction(KeyEvent.VK_O);
    public Action SAVE_ACTION = new SaveAction(KeyEvent.VK_S);
    public Action SAVE_AS_ACTION = new SaveAction(-1);

    public Action CLOSE_ACTION = new AbstractAction("Close graph", ExplorerActions.loadIcon("close18")) {
        {
            putValue(SHORT_DESCRIPTION, "Closes current graph window");
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.CTRL_MASK));
            putValue(MNEMONIC_KEY, KeyEvent.VK_W);
            setEnabled(master != null && master.getActiveController() != null);
        }
        public void actionPerformed(ActionEvent e) {
            master.closeController();
        }
    };

    class LoadAction extends AbstractAction {
        LoadAction(int accelerator) {
            super("Load graph", ExplorerActions.loadIcon("load-graph18"));
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
                AbstractGraphIO loader = fc.activeIO();
                Object loaded = loader.importGraph(loc, openFile, null);
                if (loaded == null) {
                    master.reportStatus("Attempt to load file " + openFile + " failed!");
                    return;
                }
                GraphController gc = null;
                if (loaded instanceof LongitudinalGraph)
                    gc = GraphController.getInstance((LongitudinalGraph<Integer>) loaded, openFile.getName());
                else if (loaded instanceof Graph) {
                    Graph<Integer> gl = (Graph<Integer>) loaded;
                    gc = GraphController.getInstance(gl, openFile.getName());
                    if (loc != null && loc.size() > 0) {
                        LinkedHashMap<Object,Point2D.Double> pos = new LinkedHashMap<Object,Point2D.Double>();
                        for (Entry<Integer,double[]> en : loc.entrySet())
                            pos.put(en.getKey(), new Point2D.Double(en.getValue()[0], en.getValue()[1]));
                        gc.setPositions(pos);
                    }
                    // look for images attached with file
                    List nodes = gl.nodes();
                    if (gl instanceof ValuedGraph && nodes.size() > 0) {
                        ValuedGraph vg = (ValuedGraph) gl;
                        Object o1 = vg.getValue(nodes.get(0));
                        if (o1.getClass().equals(Object[].class)) {
                            Object[] o2 = (Object[]) o1;
                            if (o2.length >= 2 && o2[1].getClass().equals(String.class)) {
                                ValuedGraph<Integer,Object[]> vg2 = (ValuedGraph<Integer,Object[]>) vg;
                                for (Integer i : vg2.nodes()) {
                                    String fileName = (String) vg2.getValue(i)[1];
                                    File imageFile = new File(openFile.getParent(), fileName);
                                    try {
                                        Image image = ImageIO.read(imageFile);
                                        vg2.getValue(i)[1] = image;
                                    } catch (Exception ex) {
                                        System.out.println("Failed to read image file " + imageFile);
                                    }
                                }
                            }
                        }
                    }
                }
                
                if (gc != null)
                    master.setActiveController(gc);
                else
                    master.reportStatus("Attempt to load file " + openFile + " failed!");
            }
        }
    }

    class SaveAction extends AbstractAction {
        SaveAction(int accelerator) {
            super("Save graph", ExplorerActions.loadIcon("save-graph18"));
            if (accelerator != -1) {
                putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(accelerator, InputEvent.CTRL_MASK));
                putValue(MNEMONIC_KEY, accelerator);
            }
            setEnabled(master != null && master.getActiveController() != null);
        }
        public void actionPerformed(ActionEvent e) {
            GraphController gc = master.getActiveController();
            if (gc == null || !(gc.valid())) {
                master.reportStatus("Save failed: no graph active");
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
                AbstractGraphIO loader = fc.activeIO();
                Object active = gc.getPrimaryGraph();
                GraphType saveType = loader.saveGraph(active, gc.getPositions(), openFile);
                if (saveType == null)
                    gc.reportStatus("Attempt to save file " + openFile + " failed!");
                else
                    gc.reportStatus("Saved graph to file " + openFile + ".");
            }
        }
    }

    // image actions

    Action[] imageActions(PlotComponent plot) {
        if (plot == null)
            return new Action[]{};
        List supportedFormats = Arrays.asList(ImageIO.getWriterFormatNames());
        ArrayList<Action> result = new ArrayList<Action>();
        if (supportedFormats.contains("jpg")) result.add(new ExportImageAction("JPEG format (.jpg, .jpeg)", "jpg", plot));
        if (supportedFormats.contains("png")) result.add(new ExportImageAction("Portable Network Graphics format (.png)", "png", plot));
        if (supportedFormats.contains("gif")) result.add(new ExportImageAction("Graphics Interchange Format (.gif)", "gif", plot));
        if (supportedFormats.contains("bmp")) result.add(new ExportImageAction("Bitmap format (.bmp)", "bmp", plot));
        return result.toArray(new Action[]{});
    }

    static class ExportImageAction extends AbstractAction {
        PlotComponent plot;
        String ext;
        ExportImageAction(String text, String ext, PlotComponent plot) {
            super(text);
            this.plot = plot;
            this.ext = ext;
        }
        public void actionPerformed(ActionEvent e) {
            initFileChooser();
            fc.setApproveButtonText("Export");
            if (openFile != null) {
                fc.setCurrentDirectory(openFile);
                fc.setSelectedFile(openFile);
            }
            if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                openFile = fc.getSelectedFile();
                BufferedImage image = new BufferedImage(plot.getWidth(), plot.getHeight(), BufferedImage.TYPE_INT_RGB);
                Graphics2D canvas = image.createGraphics();
                canvas.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                plot.renderTo(canvas);
                canvas.dispose();
                try {
                    ImageIO.write(image, ext, openFile);
                } catch (IOException ex) {
                    System.out.println(ex);
                }
            }
        }
    }

    // movie export actions

    static class MovieAction extends AbstractAction implements Runnable {
        GraphController gc;
        LongitudinalGraphPanel lgp;
        Thread loaderThread = null;
        ProgressMonitor pm;
        public MovieAction(GraphController gc, LongitudinalGraphPanel lgp) {
            super("Quicktime movie (.mov)", ExplorerActions.loadIcon("export-mov18"));
            this.gc = gc;
            this.lgp = lgp;
            putValue(SHORT_DESCRIPTION, "Export longitudinal graph as a QuickTime movie (.mov)");
        }
        public void actionPerformed(ActionEvent e) {
            if (loaderThread != null)
                return;
            if (gc.isLongitudinal()) {
                initFileChooser();
                fc.setApproveButtonText("Export");
                if (openFile != null) {
                    fc.setCurrentDirectory(openFile);
                    fc.setSelectedFile(openFile);
                }
                if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    openFile = fc.getSelectedFile();
                    pm = new ProgressMonitor(lgp, "Exporting graph to .mov file", "", 0, 1);
                    loaderThread = new Thread(this);
                    loaderThread.start();
                }
            }
        }
        public void run() {
            MovieExport me = new MovieExport(openFile);
            new ExportMovieTask(lgp, me, pm).run();
            loaderThread = null;
        }
    }


    //
    // INNER CLASSES
    //

    /** File chooser that is intended for use with graphs */
    public static class GraphFileChooser extends javax.swing.JFileChooser {

        /** Input/output algorithms */
        ArrayList<AbstractGraphIO> ios;

        /** Constructs file chooser w/ current directory & default file formats */
        public GraphFileChooser() {
            ios = new ArrayList<AbstractGraphIO>();
            ios.add(EdgeListGraphIO.getInstance());
            ios.add(PajekGraphIO.getInstance());
            ios.add(UCINetGraphIO.getInstance());
            ios.add(PajekGraphIO.getExtendedInstance());
            ios.add(PajekLongGraphIO.getInstance());
            ios.add(GraphMLIO.getInstance());

            initFilters();
        }

        private void initFilters() {
            for (AbstractGraphIO agio : ios)
                addChoosableFileFilter(agio.getFileFilter());
            setFileFilter(ios.get(1).getFileFilter());
        }

        /** @return the io method corresponding to the selected filter, or null if the "allow all" option is selected */
        AbstractGraphIO activeIO() {
            javax.swing.filechooser.FileFilter activeFilter = getFileFilter();
            for (AbstractGraphIO agio : ios)
                if (activeFilter == agio.getFileFilter())
                    return agio;
            return null;
        }

    }
}
