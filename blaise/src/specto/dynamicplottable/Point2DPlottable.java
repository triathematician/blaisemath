/*
 * Point2DPlottable.java
 * Created on Mar 2, 2008
 */

package specto.dynamicplottable;

import specto.*;
import java.awt.Graphics2D;
import javax.swing.JMenu;
import sequor.model.PointRangeModel;
import specto.visometry.Euclidean2;

/**
 * <p>
 * Point2DPlottable is ...
 * </p>
 * @author Elisha Peterson
 */
public class Point2DPlottable extends DynamicPlottable<Euclidean2> {
    protected PointRangeModel prm;
    
    public Point2DPlottable(PointRangeModel prm){this.prm=prm;}
    
    // Bean patterns
    
    public PointRangeModel getModel(){return prm;}
    

    @Override
    public JMenu getOptionsMenu() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void recompute() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void paintComponent(Graphics2D g, Euclidean2 v) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
