/*
 * StandardGrid2D.java
 * Created on Mar 22, 2008
 */

package specto.euclidean3;

import java.awt.Color;
import scio.coordinate.R3;
import specto.PlottableGroup;

/**
 * <p>
 * StandardGrid3D will display a grid in 3D... currently supports display of the coordinate
 * planes only.
 * </p>
 * @author Elisha Peterson
 */
public class StandardGrid3D extends PlottableGroup<Euclidean3> {

    
    // CONSTRUCTOR
    public StandardGrid3D(){
        setColor(Color.getHSBColor(.4f,.3f,0.9f));
    }    
    
    // PAINT METHODS    

    @Override
    public void recompute(Euclidean3 v) {
        clear();
        double min1,max1,min2,max2;
        if (xyStyle != PLANE_NONE) {
            min1 = xyStyle < 2 ? v.xRange.getMinimum() : 0;
            max1 = v.xRange.getMaximum();
            min2 = xyStyle < 1 ? v.yRange.getMinimum() : 0;
            max2 = v.yRange.getMaximum();
            add(new Plane3D(R3.ZERO, R3.I, min1, max1, R3.J, min2, max2));
        }
        if (xzStyle != PLANE_NONE) {
            min1 = xzStyle < 2 ? v.xRange.getMinimum() : 0;
            max1 = v.xRange.getMaximum();
            min2 = xzStyle < 1 ? v.zRange.getMinimum() : 0;
            max2 = v.zRange.getMaximum();
            add(new Plane3D(R3.ZERO, R3.I, min1, max1, R3.K, min2, max2));
        }
        if (yzStyle != PLANE_NONE) {
            min1 = yzStyle < 2 ? v.yRange.getMinimum() : 0;
            max1 = v.yRange.getMaximum();
            min2 = yzStyle < 1 ? v.zRange.getMinimum() : 0;
            max2 = v.zRange.getMaximum();
            add(new Plane3D(R3.ZERO, R3.J, min1, max1, R3.K, min2, max2));
        }
        super.recompute(v);
    }
   
    
//    @Override
//    public void paintComponent(Graphics2D g, Euclidean3 v) {
//        NiceRangeGenerator spacing=new NiceRangeGenerator.StandardRange();
//        Vector<Double> xGrid=spacing.niceRange(v.xRange.getMinimum(), v.xRange.getMaximum(), v.getSceneSize()/10);
//        Vector<Double> yGrid=spacing.niceRange(v.yRange.getMinimum(), v.yRange.getMaximum(), v.getSceneSize()/10);
//        Vector<Double> zGrid=spacing.niceRange(v.zRange.getMinimum(), v.zRange.getMaximum(), v.getSceneSize()/10);
//        R3[] dirs = { new R3(1,0,0), new R3(0,1,0), new R3(0,0,1) };
//    }
    
    
    // STYLE SETTINGS

    @Override
    public String[] getStyleStrings() {return null;}
    @Override
    public String toString(){return "Grid";}
        
    public static final int PLANE_WHOLE = 0;
    public static final int PLANE_HALF = 1;
    public static final int PLANE_QUARTER = 2;
    public static final int PLANE_NONE = 3;
    
    int xyStyle = 0;
    int xzStyle = 3;
    int yzStyle = 3;

    public int getXyStyle() { return xyStyle; }
    public void setXyStyle(int xyStyle) { this.xyStyle = xyStyle; }
    public int getXzStyle() { return xzStyle; }
    public void setXzStyle(int xzStyle) { this.xzStyle = xzStyle; }
    public int getYzStyle() { return yzStyle; }
    public void setYzStyle(int yzStyle) { this.yzStyle = yzStyle; }
}
