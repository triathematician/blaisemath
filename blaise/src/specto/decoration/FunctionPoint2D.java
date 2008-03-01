/**
 * FunctionPoint2D.java
 * Created on Feb 28, 2008
 */

package specto.decoration;

import java.awt.Graphics2D;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JMenu;
import scio.function.FunctionValueException;
import specto.Decoration;
import specto.dynamicplottable.Point2D;
import specto.plottable.Function2D;
import specto.visometry.Euclidean2;

/**
 * Represents a point on a function; may also display the tangent line of the function if possible.
 * @author Elisha Peterson
 */
public class FunctionPoint2D extends Decoration<Euclidean2> {
    /** The point displayed. */
    Point2D point;

    public FunctionPoint2D(Function2D parent) {
        super(parent);
        try {
            point = new Point2D(visometry, parent.getFunctionPoint(0.0));
        } catch (FunctionValueException ex) {
            point=null;
        }
    }
    
    @Override
    public JMenu getOptionsMenu() {return null;}

    @Override
    public void recompute() {}

    @Override
    public void paintComponent(Graphics2D g) {point.paintComponent(g);}
}
