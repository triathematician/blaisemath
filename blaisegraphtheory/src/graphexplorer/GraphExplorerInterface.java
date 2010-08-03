/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package graphexplorer;

import java.awt.Component;

/**
 * Provides methods that actions classes can use.
 * 
 * @author elisha
 */
interface GraphExplorerInterface {

    /** Returns component-parent for dialogs */
    Component dialogComponent();
    /** Adds a message to the output window. */
    void output(String output);
    /** @return active controller */
    GraphController activeController();

}
