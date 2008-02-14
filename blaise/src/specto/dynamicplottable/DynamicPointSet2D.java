/*
 * DynamicPointSet2D.java
 * Created on Feb 11, 2008
 */

package specto.dynamicplottable;

import java.awt.Graphics2D;
import specto.plottable.PlottableCollection;
import specto.visometry.Euclidean2;

/**
 * <p>
 * <b>DynamicPointSet2D</b> is a collection of points, \emph{all of which} can be moved.
 * </p>
 * @author Elisha Peterson
 */
public class DynamicPointSet2D extends PlottableCollection<Euclidean2>{
    public DynamicPointSet2D(){
        super();
        addPlottable(new Point2D());
    }

    @Override
    public void paintComponent(Graphics2D g) {
        super.paintComponent(g);
        switch(cyclic){
            case STYLE_OPEN: drawEdgesOpen(g); break;
            case STYLE_CYCLIC: drawEdgesCyclic(g); break;
        }
    }
    
    /** Draws edges between the points as currently ordered. */
    public void drawEdgesOpen(Graphics2D g){
        // TODO add draw method
    }
    
    /** Draws edges between the points as currently ordered; also draws between the last element and the first. */    
    public void drawEdgesCyclic(Graphics2D g){
        // TODO add draw method
    }
    
    // STYLES
    
    private int cyclic = STYLE_OPEN;
    
    public static final int STYLE_OPEN=0;
    public static final int STYLE_CYCLIC=1;
}
