/*
 * BPlottable.java
 *
 * Created on March 8, 2007, 4:53 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package Interface;

import java.awt.Graphics2D;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import javax.swing.event.MouseInputListener;

/**
 *
 * @author ae3263
 */
public interface BPlottable extends ActionListener,BEditor,MouseInputListener{
    /** Coordinates */
    public abstract double getGeoX();
    public abstract double getGeoY();
    public abstract void setX(double wx);
    public abstract void setY(double wy);
    /** Display routines */
    public abstract void paintComponent(Graphics2D g, AffineTransform at);
    /** Mouse routines */
    public static final int CLICK_EDIT_RANGE=8;
    public abstract boolean clicked(MouseEvent e);   
}
