/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * AddMenu.java
 * Created on Mar 24, 2008
 */

package sequor.component;

import javax.swing.JMenu;
import specto.PlotPanel;
import specto.dynamicplottable.Point2D;
import specto.dynamicplottable.Segment2D;
import specto.plottable.Function2D;

/**
 *
 * @author Elisha Peterson
 */
public class AddMenu extends JMenu {
    public AddMenu(final PlotPanel panel){
        super("Add");
        add(new Point2D().getAddMenuItem(panel));
        add(new Segment2D().getAddMenuItem(panel));
        add(new Function2D().getAddMenuItem(panel));
    }
}
