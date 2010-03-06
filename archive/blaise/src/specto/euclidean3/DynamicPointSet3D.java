/*
 * DynamicPointSet2D.java
 * Created on Feb 11, 2008
 */

package specto.euclidean3;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.util.Vector;
import javax.swing.JMenu;
import scio.coordinate.R2;
import scio.coordinate.R3;
import sequor.component.RangeTimer;
import specto.Plottable;
import specto.PlottableGroup;
import specto.euclidean2.Euclidean2;
import specto.euclidean2.Point2D;
import specto.style.PointStyle;

/**
 * <b>DynamicPointSet2D</b> is a collection of points, \emph{all of which} can be moved.
 * @author Elisha Peterson
 */
public class DynamicPointSet3D extends PlottableGroup<Euclidean3>{
    public DynamicPointSet3D(){
        setName("Dynamic Point Set");
    }

    @Override
    public void add(Plottable<Euclidean3> p) {if(p instanceof Point3D){super.add(p);}}
    public void add(R3 newPoint){
        Point3D point=new Point3D(newPoint);
        point.style.setValue(PointStyle.MEDIUM);
        add(point);
    }
    public void add(double x,double y,double z){add(new R3(x,y,z));}
    
    public R3 getPoint(int i) throws ArrayIndexOutOfBoundsException {
        return ((Point3D)plottables.get(i)).getPoint();
    }
    
    /** Sets path to the specified list of points. */
    public void setPath(Vector<R3> path) {
        clear();
        for (R3 p : path) { add(p); }
    }

    @Override
    public void paintComponent(Graphics2D g,Euclidean3 v) {
        switch(style.getValue()){
            case STYLE_OPEN:
            case STYLE_CYCLIC:
            case STYLE_FILLED:
            case STYLE_POINTS_ONLY:
                super.paintComponent(g,v);
                break;
        }
        if(style.getValue()==STYLE_POINTS_ONLY){return;}
        Vector<R3> points=new Vector<R3>();
        for(Plottable<Euclidean3> p:plottables){points.add(((Point3D)p).getPoint());}
        g.setColor(getColor());
        switch(style.getValue()){
            case STYLE_OPEN: 
            case STYLE_OPEN_NO_POINTS:
                v.drawPath(g, points);
                break;
            case STYLE_CYCLIC:
            case STYLE_CYCLIC_NO_POINTS:
                v.drawPath(g, points);
                break;
            case STYLE_FILLED:          
            case STYLE_FILLED_NO_POINTS:
                v.fillPath(g, points);
                break;
        }
    }

    @Override
    public void paintComponent(Graphics2D g, Euclidean3 v, RangeTimer t) {paintComponent(g,v);}
    
    
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
    public JMenu getOptionsMenu() {
        JMenu result=new JMenu(toString()+" Options");       
        result.add(getVisibleMenuItem());
        result.setForeground(getColor());
        result.add(getColorMenuItem());  
        if(style==null){return result;}
        return style.appendToMenu(result);
    }
    
}
