/*
 * DynamicPointSet2D.java
 * Created on Feb 11, 2008
 */

package specto.dynamicplottable;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.util.Vector;
import javax.swing.JMenu;
import scio.coordinate.R2;
import sequor.component.IntegerRangeTimer;
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
    public void add(R2 newPoint){
        Point2D point=new Point2D(newPoint);
        point.style.setValue(Point2D.MEDIUM);
        add(point);
    }
    public void add(double x,double y){add(new R2(x,y));}

    @Override
    public void paintComponent(Graphics2D g,Euclidean2 v) {
        switch(style.getValue()){
            case STYLE_OPEN:
            case STYLE_CYCLIC:
            case STYLE_FILLED:
            case STYLE_POINTS_ONLY:
                super.paintComponent(g,v);
                break;
        }
        if(style.getValue()==STYLE_POINTS_ONLY){return;}
        Vector<R2> points=new Vector<R2>();
        for(Plottable<Euclidean2> p:plottables){points.add(((Point2D)p).getPoint());}
        g.setColor(getColor());
        switch(style.getValue()){
            case STYLE_OPEN: 
            case STYLE_OPEN_NO_POINTS:
                g.draw(v.path(points));
                break;
            case STYLE_CYCLIC:
            case STYLE_CYCLIC_NO_POINTS:
                g.draw(v.closedPath(points));
                break;
            case STYLE_FILLED:          
            case STYLE_FILLED_NO_POINTS:
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.5f));
                g.fill(v.closedPath(points));
                break;
        }
    }

    @Override
    public void paintComponent(Graphics2D g, Euclidean2 v, IntegerRangeTimer t) {paintComponent(g,v);}
    
    
    // STYLES    
    public static final int STYLE_OPEN=0;
    public static final int STYLE_CYCLIC=1;
    public static final int STYLE_FILLED=2;
    public static final int STYLE_POINTS_ONLY=3;
    public static final int STYLE_OPEN_NO_POINTS=4;
    public static final int STYLE_CYCLIC_NO_POINTS=5;
    public static final int STYLE_FILLED_NO_POINTS=6;
    
    final static String[] styleStrings={"Open","Cyclic","Filled","Points Only","Open - edge only","Cyclic - edge only","Filled - edge only"};
    @Override
    public String[] getStyleStrings(){return styleStrings;}
    @Override
    public String toString(){return "Point Set";}
    @Override
    public JMenu getOptionsMenu() {
        JMenu result=new JMenu(toString()+" Options");       
        result.setForeground(getColor());
        result.add(getColorMenuItem());  
        if(style==null){return result;}
        return style.appendToMenu(result);
    }
    
}
