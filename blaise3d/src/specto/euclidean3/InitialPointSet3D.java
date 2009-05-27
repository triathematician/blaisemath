/**
 * InitialPointSet3D.java
 * Created on Feb 25, 2008
 */

package specto.euclidean3;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.util.Vector;
import javax.swing.JMenu;
import javax.swing.event.ChangeListener;
import scio.coordinate.R3;
import sequor.component.RangeTimer;
import sequor.control.NumberSlider;
import sequor.model.ColorModel;
import sequor.model.IntegerRangeModel;
import specto.Animatable;

/**
 * This class represents any path which depends upon an initial point. A call to recompute() is made whenever the initial point is moved.
 * @author Elisha Peterson
 */
public class InitialPointSet3D extends Point3D implements Animatable<Euclidean3>,ChangeListener{
    
    /** The number of points in the set. */
    protected IntegerRangeModel length;

    /** The path "decorating" the initial point. */
    protected PointSet3D path;

    public InitialPointSet3D(){
        path=new PointSet3D();
        length=new IntegerRangeModel(100,0,50000,1);
    }

    public InitialPointSet3D(Color value) {
        super(value);
        path=new PointSet3D();
        length=new IntegerRangeModel(100,0,50000,1);
    }

    public InitialPointSet3D(R3 prm){
        super(prm);
        path=new PointSet3D();
        length=new IntegerRangeModel(100,0,50000,1);
    }

    public InitialPointSet3D(Point3D parent){
        super(parent.prm);
        path=new PointSet3D();
        length=new IntegerRangeModel(100,0,50000,1);
    }

    public InitialPointSet3D(Point3D parent, Vector<R3> points,Color c){
        this(parent.prm,points,c);
    }

    public InitialPointSet3D(R3 prm, Vector<R3> points, Color c){
        super(prm,c);
        path=new PointSet3D(points,c);
        length=new IntegerRangeModel(100,0,50000,1);
    }
    public InitialPointSet3D(Point3D parent, Vector<R3> points, ColorModel colorModel) {
        super(parent.prm);
        path=new PointSet3D(points,colorModel);
        setColorModel(colorModel);
    }
    
    // BEAN PATTERNS
    
    /** Whether this element animates. */    
    public boolean animationOn=true;
    public void setAnimationOn(boolean newValue) { animationOn=newValue; }
    public boolean isAnimationOn() { return animationOn; }

    public IntegerRangeModel getLengthModel(){return length;}
    
    /** Returns plottable path element. */
    public PointSet3D getPathPlottable(){return path; }
    /** Returns path. */
    public Vector<R3> getPath(){return path.getPath();}
    /** Replaces the path contained here with a new one. */
    public void setPath(Vector<R3> newPoints){path.setPath(newPoints);length.setValue(newPoints.size());}
    /** Replaces the path's color with current color. */
    @Override
    public void setColor(Color newValue){
        super.setColor(newValue);
        if(path!=null){path.setColor(newValue);}
    }
    
    @Override
    public void paintComponent(Graphics2D g,Euclidean3 v) {
        super.paintComponent(g,v);
        g.setColor(path.getColor());
        path.paintComponent(g,v);
    }
    @Override
    public void paintComponent(Graphics2D g,Euclidean3 v,RangeTimer t){
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
    public String[] getStyleStrings() {return PointSet3D.styleStrings;}
    @Override
    public NumberSlider getStyleSlider(int x, int y) {return path.getStyleSlider(x, y);}
    @Override
    public String toString(){return "Initial Point Set";}
}
