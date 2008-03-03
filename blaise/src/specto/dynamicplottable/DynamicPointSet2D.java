/*
 * DynamicPointSet2D.java
 * Created on Feb 11, 2008
 */

package specto.dynamicplottable;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.util.Vector;
import scio.coordinate.R2;
import specto.Plottable;
import specto.PlottableGroup;
import specto.visometry.Euclidean2;

/**
 * <b>DynamicPointSet2D</b> is a collection of points, \emph{all of which} can be moved.
 * @author Elisha Peterson
 */
public class DynamicPointSet2D extends PlottableGroup<Euclidean2>{
    public DynamicPointSet2D(){}

    @Override
    public void add(Plottable<Euclidean2> p) {if(p instanceof Point2D){super.add(p);}}
    public void add(R2 newPoint){add(new Point2D(newPoint));}
    public void add(double x,double y){add(new Point2D(new R2(x,y)));}

    @Override
    public void paintComponent(Graphics2D g,Euclidean2 v) {
        super.paintComponent(g,v);
        Vector<R2> points=new Vector<R2>();
        for(Plottable<Euclidean2> p:plottables){points.add(((Point2D)p).getPoint());}
        switch(cyclic){
            case STYLE_OPEN: 
                g.draw(v.path(points));
                break;
            case STYLE_CYCLIC:
                g.draw(v.closedPath(points));
                break;
            case STYLE_FILLED:
                
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.5f));
                g.fill(v.closedPath(points));
                break;
        }
    }
    
    // STYLES
    
    private int cyclic = STYLE_FILLED;
    
    public static final int STYLE_OPEN=0;
    public static final int STYLE_CYCLIC=1;
    public static final int STYLE_FILLED=2;
}
