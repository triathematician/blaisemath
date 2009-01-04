/*
 * Axes3D2.java
 * Created on Mar 22, 2008
 */

package specto.euclidean3;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;
import scio.coordinate.R3;
import specto.DynamicPlottable;

/**
 * Axes2D represents the second generation of axes drawing, with support for multiple drawing modes, labels, etc. The Grid2D class separately
 * maintains the grid. The spacing of the two classes should be adjusted separately.
 * 
 * @author Elisha Peterson
 */
public class Axes3D2 extends DynamicPlottable<Euclidean3> {
    
    // CONSTRUCTOR
    public Axes3D2(){
        setColor(Color.GRAY);
        style.setValue(1);
    }    
    
    // PAINT METHODS
    
    private static final int TICK_HEIGHT=5;
    private static final int IDEAL_TICK_SPACE=40;    
    static Point2D XOFFSET=new Point2D.Double(5,-3);
    static Point2D YOFFSET=new Point2D.Double(10,-5);
    static Point2D ZOFFSET=new Point2D.Double(10,-5);
    
    private String xLabel="x";
    private String yLabel="y";
    private String zLabel="z";
    private static final DecimalFormat nf=new DecimalFormat("##0.#");   
    
    @Override
    public void paintComponent(Graphics2D g, Euclidean3 v) {
        R3 center = v.getCenter();
        double wid = v.getSceneSize();
        if (style.getValue() != AXES_BOX) { wid *= 1.1; }
        R3[] axes = { center.plus(wid,0,0), center.plus(0,wid,0), center.plus(0,0,wid) };
        R3[] axes2 = { center.plus(-wid,0,0), center.plus(0,-wid,0), center.plus(0,0,-wid) };
        switch (style.getValue()){
            case AXES_BOX:
                R3[] box = { center.plus(-wid,-wid,-wid), center.plus(-wid,-wid,wid), center.plus(-wid,wid,-wid), center.plus(-wid,wid,wid),  
                            center.plus(wid,-wid,-wid), center.plus(wid,-wid,wid), center.plus(wid,wid,-wid), center.plus(wid,wid,wid) };
                v.drawLineSegment(g, box[0], box[1]);
                v.drawLineSegment(g, box[0], box[2]);
                v.drawLineSegment(g, box[0], box[4]);
                v.drawLineSegment(g, box[1], box[3]);
                v.drawLineSegment(g, box[1], box[5]);
                v.drawLineSegment(g, box[2], box[3]);
                v.drawLineSegment(g, box[2], box[6]);
                v.drawLineSegment(g, box[4], box[5]);
                v.drawLineSegment(g, box[4], box[6]);
                v.drawLineSegment(g, box[3], box[7]);
                v.drawLineSegment(g, box[5], box[7]);
                v.drawLineSegment(g, box[6], box[7]);
                break;
            case AXES_OCTANT:
                v.drawLineSegment(g, center, axes[0]);
                v.drawLineSegment(g, center, axes[1]);
                v.drawLineSegment(g, center, axes[2]);
                break;
            case AXES_TOPHALF:
                v.drawLineSegment(g, axes2[0], axes[0]);
                v.drawLineSegment(g, axes2[1], axes[1]);
                v.drawLineSegment(g, center, axes[2]);
                break;
            case AXES_STANDARD:
            default:
                v.drawLineSegment(g, axes2[0], axes[0]);
                v.drawLineSegment(g, axes2[1], axes[1]);
                v.drawLineSegment(g, axes2[2], axes[2]);
                break;
        }
        java.awt.geom.Point2D.Double winCenter = v.toWindow(axes[0]);
        g.drawString(xLabel,(float)winCenter.x+5,(float)winCenter.y+5);
        winCenter = v.toWindow(axes[1]);
        g.drawString(yLabel,(float)winCenter.x+5,(float)winCenter.y+5);
        winCenter = v.toWindow(axes[2]);
        g.drawString(zLabel,(float)winCenter.x+5,(float)winCenter.y+5);
        v.fillDot(g, axes[0], 3);
        v.fillDot(g, axes[1], 3);
        v.fillDot(g, axes[2], 3);
    }
    
    
    // STYLE SETTINGS    
    
    public static final int AXES_STANDARD=0;
    public static final int AXES_BOX=1;
    public static final int AXES_OCTANT=2;
    public static final int AXES_TOPHALF=3;

    @Override
    public String[] getStyleStrings() {
        String[] result = {"Standard", "Box", "First Octant", "Upper Half Space"};
        return result;
    }
    
    @Override
    public String toString(){return "Axes";}

    // BEAN PATTERNS
    
    public String getXLabel() { return xLabel; }
    public void setXLabel(String xLabel) { this.xLabel = xLabel; }
    public String getYLabel() { return yLabel; }
    public void setYLabel(String yLabel) { this.yLabel = yLabel; }
    public String getZLabel() { return zLabel; }
    public void setZLabel(String zLabel) { this.zLabel = zLabel; }
}
