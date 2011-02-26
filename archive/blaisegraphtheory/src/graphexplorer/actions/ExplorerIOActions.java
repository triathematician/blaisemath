/*
 * GraphExplorerFileActions.java
 * Created Jul 12, 2010
 */

package graphexplorer.actions;

import graphexplorer.controller.GraphController;
import graphexplorer.controller.GraphControllerMaster;
import graphexplorer.LongitudinalGraphPanel;
import graphexplorer.controller.LongitudinalGraphController;
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
import javax.swing.filechooser.FileFilter;
import org.bm.blaise.scio.graph.Graph;
import org.bm.blaise.scio.graph.LongitudinalGraph;
import org.bm.blaise.scio.graph.ValuedGraph;
import org.bm.blaise.scio.graph.io.*;
import org.bm.blaise.scio.graph.io.AbstractGraphIO.GraphType;
import util.io.FileNameExtensionFilter;
import visometry.PlotComponent;

/**
 * Describes file/IO actions supporting the graph explorer app.
 * @author Elisha Peterson
 */
public class ExplorerIOActions {

    /** What this class works with */
    GraphControllerMaster master;

    /** The file chooser box */
    transient static GraphFileChooser fc;
    /** The currently open file */
    transient static File openFile;

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

    /** Lazy initialization will cause a bit of a delay when first loaded */
    static void initFileChooser() {
        if (fc == null)
            fc = new GraphFileChooser();
    }
    
    /** 
     * Derives a new file name from the open file 
     * @param file the file
     * @param newExtension a new extension for the file
     * @return file representing same base, new extension
     */
    static File deriveFile(File file, String newExtension) {
        String name = file.getName();
        int pos = name.indexOf(".");
        String newName = name.substring(0, pos) + "." + newExtension;
        return new File(file.getParent(), newName);
    }

    /** Loads a file */
    public Action LOAD_ACTION = new LoadAction(KeyEvent.VK_O);
    /** Saves a file */
    public Action SAVE_ACTION = new SaveAction(KeyEvent.VK_S);
    /** Saves a file under a new name */
    public Action SAVE_AS_ACTION = new SaveAction(-1);

    /** Closes the current active graph */
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

    /** Action to load a graph */
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
            fc.setFilterGroup(ChooserFilter.GRAPH);
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
                    gc = new LongitudinalGraphController((LongitudinalGraph<Integer>) loaded, openFile.getName());
                else if (loaded instanceof Graph) {
                    Graph<Integer> gl = (Graph<Integer>) loaded;
                    gc = new GraphController(gl, openFile.getName());
                    if (loc != null && loc.size() > 0) {
                        LinkedHashMap<Object,Point2D.Double> pos = new LinkedHashMap<Object,Point2D.Double>();
                        for (Entry<Integer,double[]> en : loc.entrySet())
                            pos.put(en.getKey(), new Point2D.Double(en.getValue()[0], en.getValue()[1]));
                        gc.setNodePositions(pos);
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

    /** Action to save the current graph, or to save graph under a new name */
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
            if (gc == null) {
                master.reportStatus("Save failed: no graph active");
                return;
            }

            initFileChooser();
            fc.setFilterGroup(ChooserFilter.GRAPH);
            fc.setApproveButtonText("Save");
            if (openFile != null) {
                fc.setCurrentDirectory(openFile);
                fc.setSelectedFile(openFile);
            }
            if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                openFile = fc.getSelectedFile();
                AbstractGraphIO loader = fc.activeIO();
                Object active = gc.getBaseGraph();
                GraphType saveType = loader.saveGraph(active, gc.getNodePositions(), openFile);
                if (saveType == null)
                    gc.reportStatus("Attempt to save file " + openFile + " failed!");
                else
                    gc.reportStatus("Saved graph to file " + openFile + ".");
            }
        }
    }

    //
    // Image Actions
    //

    /** Stores the image actions */
    transient List<ExportImageAction> imageActions;

    /** 
     * This array consists of the available image export options;
     * this might depend on which image formats have registered writers.
     * @param plot the underlying plot (required for the image actions)
     * @return list of supported image export actions
     */
    public List<ExportImageAction> imageActions(PlotComponent plot) {
        if (imageActions == null) {
            imageActions = new ArrayList<ExportImageAction>();
            List supportedFormats = Arrays.asList(ImageIO.getWriterFormatNames());
            if (supportedFormats.contains("jpg"))
                imageActions.add(new ExportImageAction(plot, "JPEG format (.jpg)", "jpg"));
            if (supportedFormats.contains("png"))
                imageActions.add(new ExportImageAction(plot, "Portable Network Graphics format (.png)", "png"));
            if (supportedFormats.contains("gif"))
                imageActions.add(new ExportImageAction(plot, "Graphics Interchange Format (.gif)", "gif"));
            if (supportedFormats.contains("bmp"))
                imageActions.add(new ExportImageAction(plot, "Bitmap format (.bmp)", "bmp"));
        } else {
            for (ExportImageAction action : imageActions)
                action.plot = plot;
        }
        return imageActions;
    }

    /** 
     * Exports an image of the current graph; the size is the same size as
     * the currently displayed canvas.
     */
    public static class ExportImageAction extends AbstractAction {
        PlotComponent plot;
        String[] ext;
        FileFilter filter;
        /**
         * Construct a new export action
         * @param text text description of the format
         * @param ext extension of the format (for filtering)
         * @param plot plot to be used for export
         */
        ExportImageAction(PlotComponent plot, String text, String... ext) {
            super(text);
            this.plot = plot;
            this.ext = ext;
            filter = new FileNameExtensionFilter(text, ext);
        }

        public void actionPerformed(ActionEvent e) {
            initFileChooser();
            fc.setFilterGroup(ChooserFilter.IMAGE, filter);
            if (openFile != null) {
                fc.setCurrentDirectory(openFile);
                fc.setSelectedFile(deriveFile(openFile, ext[0]));
            }
            if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                BufferedImage image = new BufferedImage(plot.getWidth(), plot.getHeight(), BufferedImage.TYPE_INT_RGB);
                Graphics2D canvas = image.createGraphics();
                canvas.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                plot.renderTo(canvas);
                canvas.dispose();
                try {
                    ImageIO.write(image, ext[0], fc.getSelectedFile());
                } catch (IOException ex) {
                    System.out.println(ex);
                }
            }
        }
    }

    //
    // Movie Actions
    //

    /** 
     * Exports the current graph view as a movie,
     * provided the current view is a longitudinal graph.
     */
    public static class MovieAction extends AbstractAction implements Runnable {
        GraphController gc;
        LongitudinalGraphPanel lgp;
        FileFilter filter;
        Thread loaderThread = null;
        ProgressMonitor pm;
        File movFile;

        /** Construct a new movie action */
        public MovieAction(GraphController gc, LongitudinalGraphPanel lgp) {
            super("Quicktime movie (.mov)", ExplorerActions.loadIcon("export-mov18"));
            this.gc = gc;
            this.lgp = lgp;
            filter = new FileNameExtensionFilter("Quicktime movie (.mov)", "mov");
            putValue(SHORT_DESCRIPTION, "Export longitudinal graph as a QuickTime movie (.mov)");
        }

        public void actionPerformed(ActionEvent e) {
            if (loaderThread != null)
                return;
            if (gc instanceof LongitudinalGraphController) {
                initFileChooser();
                fc.setFilterGroup(ChooserFilter.MOVIE, filter);
                fc.setApproveButtonText("Export");
                if (openFile != null) {
                    fc.setCurrentDirectory(openFile);
                    fc.setSelectedFile(deriveFile(openFile,".mov"));
                }
                if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    movFile = fc.getSelectedFile();
                    pm = new ProgressMonitor(lgp, "Exporting graph to .mov file", "", 0, 1);
                    loaderThread = new Thread(this);
                    loaderThread.start();
                }
            }
        }
        public void run() {
            MovieExport me = new MovieExport(movFile);
            new ExportMovieTask(lgp, me, pm).run();
            loaderThread = null;
        }
    } // INNER CLASS MovieAction


    //
    // INNER CLASSES
    //

    /** File chooser state */
    enum ChooserFilter { GRAPH, IMAGE, MOVIE; }

    /** File chooser that is intended for use with graphs */
    public static class GraphFileChooser extends javax.swing.JFileChooser {

        /** Type of file being saved/exported/etc. */
        ChooserFilter filterGroup;
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
            ios.add(DynetMLGraphIO.getInstance());

            setFilterGroup(ChooserFilter.GRAPH);
        }

        /**
         * Sets the filter used in the chooser
         * @param group the type of filter being used
         * @param filters zero or more filters, the first of which will be
         *   used for the active selected filter
         */
        private void setFilterGroup(ChooserFilter group, FileFilter... filters) {
            filterGroup = group;
            resetChoosableFileFilters();
            for (FileFilter ff : filters)
                addChoosableFileFilter(ff);
            switch(group) {
                case GRAPH:
                    setApproveButtonText("OK");
                    for (AbstractGraphIO agio : ios)
                        addChoosableFileFilter(agio.getFileFilter());
                    setFileFilter(ios.get(1).getFileFilter());
                    break;
                case IMAGE:
                    setApproveButtonText("Export");
                    break;
                case MOVIE:
                    setApproveButtonText("Export");
                    break;
            }
            if (filters.length > 0)
                setFileFilter(filters[0]);
        }

        /** @return the io method corresponding to the selected filter, or null if the "allow all" option is selected */
        AbstractGraphIO activeIO() {
            javax.swing.filechooser.FileFilter activeFilter = getFileFilter();
            for (AbstractGraphIO agio : ios)
                if (activeFilter == agio.getFileFilter())
                    return agio;
            return null;
        }

    } // INNER CLASS GraphFileChooser
}
