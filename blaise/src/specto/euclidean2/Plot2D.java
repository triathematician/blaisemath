/*
 * Plot2D.java
 * Created on Sep 14, 2007, 8:12:44 AM
 */

package specto.euclidean2;

import java.util.HashSet;
import javax.swing.event.ChangeEvent;
import sequor.VisualControl;
import specto.PlotPanel;

/**
 * The primary 2D plot window which should be used in applications. Will have support
 * for drawing grid, axes, and labels, plus animation and event handling.
 * @author Elisha Peterson
 */
public class Plot2D extends PlotPanel<Euclidean2> {
    
    // NATIVE OBJECTS
    
    /** Axes object */
    Axes2D axes;
    /** Grid object */
    StandardGrid2D grid;
    
    // CONSTRUCTOR
    
    /** Default constructor */
    public Plot2D(){
        super(new Euclidean2());
        axes=new Axes2D();
        add(axes);
        grid=new StandardGrid2D();
        add(grid);
    }
    
    // BEAN PATTERNS
    
    public int getAxisStyle(){return axes.style.getValue();}
    public void setAxisStyle(int newValue){axes.style.setValue(newValue);}

    
    // OVERRIDES
    
    @Override
    public void clearPlottables() {
        super.clearPlottables();
        add(axes);
        add(grid);
    } 
}
