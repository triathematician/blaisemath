/*
 * GraphExplorerInterface.java
 * Created Jul 2010
 */

package graphexplorer;

import java.awt.Component;

/**
 * Provides methods that actions classes can use, regardless of the GUI.
 * @author Elisha Peterson
 */
interface GraphExplorerInterface {

    /** Returns component-parent for dialogs */
    Component dialogComponent();
    /** @return active controller */
    GraphController activeController();
    
    /** Adds a message to the output window. */
    void output(String output);

}
