/**
 * InitialPointSet2D.java
 * Created on Feb 25, 2008
 */

package specto.decoration;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Vector;
import javax.swing.JMenu;
import javax.swing.event.ChangeListener;
import scio.coordinate.R2;
import sequor.component.RangeTimer;
import specto.Animatable;
import specto.Decoration;
import specto.dynamicplottable.Point2D;
import specto.plottable.PointSet2D;
import specto.visometry.Euclidean2;

/**
 * This class represents any path which depends upon an initial point. A call to recompute() is made whenever the initial point is moved.
 * @author Elisha Peterson
 */
public class InitialPointSet2D extends Decoration<Euclidean2> implements Animatable,ChangeListener{
    /** The path "decorating" the initial point. */
    PointSet2D path;

    public InitialPointSet2D(Point2D parent){super(parent);}
    public InitialPointSet2D(Point2D parent,Vector<R2> points,Color c){
        super(parent);
        path=new PointSet2D(parent.getVisometry(),points,parent.getColor());
    }
    
    /** Replaces the path contained here with a new one. */
    public void setPath(Vector<R2> newPoints){path.setPath(newPoints);}
    /** Replaces the path's color with current color. */
    @Override
    public void setColor(Color newValue){
        super.setColor(newValue);
        path.setColor(newValue);
    }
    
    @Override
    public void paintComponent(Graphics2D g) {path.paintComponent(g);}
    @Override
    public void paintComponent(Graphics2D g, RangeTimer t) {path.paintComponent(g,t);}
    @Override
    public JMenu getOptionsMenu() {return path.getOptionsMenu();}
    @Override
    public void recompute() {}
}
