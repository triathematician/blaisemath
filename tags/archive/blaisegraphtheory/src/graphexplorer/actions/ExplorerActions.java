/*
 * ExplorerActions.java
 * Created May 14, 2010
 */

package graphexplorer.actions;

import graphexplorer.controller.GraphController;
import graphexplorer.GraphExplorerInterface;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import visometry.plane.PlanePlotComponent;

/**
 * Describes actions supporting the graph explorer app.
 * @see GraphExplorerFileActions
 * @author Elisha Peterson
 */
public class ExplorerActions {

    /** What this class works with */
    GraphExplorerInterface main;
    /** Construction requires a main class */
    public ExplorerActions(GraphExplorerInterface main) { 
        this.main = main;
    }

    public Action QUIT_ACTION = new AbstractAction("Exit", ExplorerActions.loadIcon("exit18")) {
        {
            putValue(SHORT_DESCRIPTION, "Exit Graph Explorer (unsaved changes will be lost).");
            putValue(MNEMONIC_KEY, KeyEvent.VK_X);
        }
        public void actionPerformed(ActionEvent e) {
            System.exit(0);
        }
    };

    //
    // GRAPH ACTIONS
    //

    public Action FIT_ACTION = new AbstractAction("Fit to Window", ExplorerActions.loadIcon("fit18")) {
        {
            putValue(SHORT_DESCRIPTION, "Fits the active graph within the displayed window.");
            putValue(MNEMONIC_KEY, KeyEvent.VK_Z);
        }
        public void actionPerformed(ActionEvent e) {
            GraphController gc = main.activeController();
            if (gc != null) {
                PlanePlotComponent plot = (PlanePlotComponent) main.activePlotComponent();
                List nodes = gc.getViewNodes();
                Map<Object,Point2D.Double> pos = gc.getNodePositions();
                double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE,
                        maxX = -Double.MAX_VALUE, maxY = -Double.MAX_VALUE;
                for (Object o : nodes) {
                    Point2D.Double p = pos.get(o);
                    if (p != null) {
                        minX = Math.min(minX, p.x);
                        minY = Math.min(minY, p.y);
                        maxX = Math.max(maxX, p.x);
                        maxY = Math.max(maxY, p.y);
                    }
                }
                double rangeX = minX == maxX ? 1 : maxX - minX;
                double rangeY = minY == maxY ? 1 : maxY - minY;
                plot.setDesiredRange(minX - rangeX/10., minY - rangeY/10., maxX + rangeX/10., maxY + rangeY/10.);
            }
        }
    };

    //
    // HELP ACTIONS
    //

    public Action ABOUT_ACTION = new AbstractAction("About", loadIcon("info18")) {
        public void actionPerformed(ActionEvent e) {
            JOptionPane.showMessageDialog(main.dialogComponent(), "GraphExplorer 0.60\nCreated by Elisha Peterson");
        }
    };

    public Action HELP_ACTION = new AbstractAction("Show Help File", loadIcon("help18")) {
        public void actionPerformed(ActionEvent e) {
            JEditorPane editorPane = new JEditorPane();
            editorPane.setEditable(false);
            java.net.URL helpURL = ExplorerActions.class.getResource("/graphexplorer/resources/HelpFile.html");
            if (helpURL != null)
                try {
                    editorPane.setPage(helpURL);
                } catch (IOException ex) {
                    System.err.println("Attempted to read a bad URL: " + helpURL);
                }
            else
                System.err.println("Couldn't find file: /graphexplorer/resources/HelpFile.html");

            //Put the editor pane in a scroll pane.
            JScrollPane editorScrollPane = new JScrollPane(editorPane);
            editorScrollPane.setVerticalScrollBarPolicy(
                            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            editorScrollPane.setPreferredSize(new Dimension(480, 640));

            JOptionPane.showMessageDialog(null,
                    editorScrollPane,
                    "Pursuit Simulator Help", JOptionPane.QUESTION_MESSAGE);
        }
    };

    //
    // UTILITIES
    //

    /** Loads an image icon for the specified relative path */
    static ImageIcon loadIcon(String path) {
        URL url = ExplorerActions.class.getResource("/graphexplorer/resources/"+path+".png");
        if (url == null) {
            System.out.println("Unable to load icon /graphexplorer/resources/" + path + ".png");
            return null;
        }
        return new ImageIcon(url);
    }

}
