/*
 * addsToPlotContextMenu.java
 * 
 * Created on Sep 26, 2007, 1:47:08 PM
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package specto;

import java.awt.event.ActionListener;
import java.util.Vector;
import javax.swing.JComponent;
import javax.swing.JMenuItem;

/**
 *
 * @author ae3263
 */
public interface BuildsContextMenu extends ActionListener {
    public Vector<JMenuItem> getMenuItems();
}
