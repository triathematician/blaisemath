/*
 * ExplorerActions.java
 * Created May 14, 2010
 */

package graphexplorer;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.URL;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

/**
 * Describes actions supporting the graph explorer app.
 * @see GraphExplorerFileActions
 * @author Elisha Peterson
 */
class ExplorerActions {

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
    // HELP ACTIONS
    //

    public Action ABOUT_ACTION = new AbstractAction("About", loadIcon("info18")) {
        public void actionPerformed(ActionEvent e) {
            JOptionPane.showMessageDialog(main.dialogComponent(), "GraphExplorer 0.59\nCreated by Elisha Peterson");
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
