/*
 * ExplorerActions.java
 * Created May 14, 2010
 */

package graphexplorer;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;

/**
 * Describes actions supporting the graph explorer app.
 * @see GraphExplorerFileActions
 * @author Elisha Peterson
 */
class ExplorerActions {

    /** What this class works with */
    GraphExplorerMain main;
    /** Construction requires a main class */
    public ExplorerActions(GraphExplorerMain main) { this.main = main; }
    
    //
    // HELP ACTIONS
    //

    public Action ABOUT_ACTION = new AbstractAction("About") {
        {
            putValue(SHORT_DESCRIPTION, "About GraphExplorer");
        }
        public void actionPerformed(ActionEvent e) {
            JOptionPane.showMessageDialog(main, "GraphExplorer 0.2\nCreated by Elisha Peterson");
        }
    };

    public Action HELP_ACTION = new AbstractAction("Help") {
        {
            putValue(SHORT_DESCRIPTION, "Load help file");
        }
        public void actionPerformed(ActionEvent e) {
            JOptionPane.showMessageDialog(main, "Sorry, the help feature is not yet available!");
        }
    };

}
