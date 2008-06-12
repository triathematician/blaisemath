/**
 * InitialPointSet2D.java
 * Created on Feb 25, 2008
 */

package specto.euclidean2;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.util.Vector;
import javax.swing.JMenu;
import javax.swing.event.ChangeListener;
import scio.coordinate.R2;
import sequor.component.RangeTimer;
import sequor.control.NumberSlider;
import sequor.model.ColorModel;
import sequor.model.IntegerRangeModel;
import sequor.model.PointRangeModel;
import specto.Animatable;

/**
 * This class represents any path which depends upon an initial point. A call to recompute() is made whenever the initial point is moved.
 * @author Elisha Peterson
 */
public class InitialPointSet2D extends Point2D implements Animatable<Euclidean2>,ChangeListener{
    /** The number of points in the set. */
    protected IntegerRangeModel length;
    /** The path "decorating" the initial point. */
    protected PointSet2D path;

    public InitialPointSet2D(){super();path=new PointSet2D();length=new IntegerRangeModel(100,0,50000,1);}
    public InitialPointSet2D(Color value) {super(value);path=new PointSet2D();length=new IntegerRangeModel(100,0,50000,1);}
    public InitialPointSet2D(PointRangeModel prm){super(prm);path=new PointSet2D();length=new IntegerRangeModel(100,0,50000,1);}
    public InitialPointSet2D(Point2D parent){super(parent.prm);path=new PointSet2D();length=new IntegerRangeModel(100,0,50000,1);}
    public InitialPointSet2D(Point2D parent,Vector<R2> points,Color c){this(parent.prm,points,c);}
    public InitialPointSet2D(PointRangeModel prm,Vector<R2> points,Color c){
        super(prm,c);
        path=new PointSet2D(points,c);
        length=new IntegerRangeModel(100,0,50000,1);
    }
    public InitialPointSet2D(Point2D parent, Vector<R2> points, ColorModel colorModel) {
        super(parent.prm);
        path=new PointSet2D(points,colorModel);
        setColorModel(colorModel);
    }
    
    // BEAN PATTERNS
    
    public IntegerRangeModel getLengthModel(){return length;}
    
    /** Returns plottable path element. */
    public PointSet2D getPathPlottable(){return path; }
    /** Returns path. */
    public Vector<R2> getPath(){return path.getPath();}
    /** Replaces the path contained here with a new one. */
    public void setPath(Vector<R2> newPoints){path.setPath(newPoints);length.setValue(newPoints.size());}
    /** Replaces the path's color with current color. */
    @Override
    public void setColor(Color newValue){
        super.setColor(newValue);
        if(path!=null){path.setColor(newValue);}
    }
    
    @Override
    public void paintComponent(Graphics2D g,Euclidean2 v) {
        super.paintComponent(g,v);
        g.setColor(path.getColor());
        path.paintComponent(g,v);
    }
    @Override
    public void paintComponent(Graphics2D g,Euclidean2 v,RangeTimer t){
        super.paintComponent(g,v);
        g.setColor(path.getColor());
        path.paintComponent(g,v,t);
    }
    @Override
    public JMenu getOptionsMenu() {
        JMenu menu = super.getOptionsMenu();
        menu.addSeparator();
        for (Component mi : path.getOptionsMenu().getMenuComponents()) {
            menu.add(mi);
        }
        return menu;
    }
    public int getAnimatingSteps() {return path.getAnimatingSteps();}
    @Override
    public String[] getStyleStrings() {return PointSet2D.styleStrings;}
    @Override
    public NumberSlider getStyleSlider(int x, int y) {return path.getStyleSlider(x, y);}
    @Override
    public String toString(){return "Initial Point Set";}
}
