/*
 * NewGrid2D.java
 * Created on Mar 22, 2008
 */

package specto.euclidean3;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import scio.coordinate.R2;
import scio.coordinate.R3;
import scio.function.FunctionValueException;
import specto.DynamicPlottable;

/**
 * Axes2D represents the second generation of axes drawing, with support for multiple drawing modes, labels, etc. The Grid2D class separately
 * maintains the grid. The spacing of the two classes should be adjusted separately.
 * 
 * @author Elisha Peterson
 */
public class Axes3D extends DynamicPlottable<Euclidean3> {
    
    // CONSTRUCTOR
    public Axes3D(){
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
        R3 center = new R3(0,0,0);
        R3[] axes = { new R3(6,0,0), new R3(0,6,0), new R3(0,0,6) };
        R3[] box = { new R3(-6,-6,-6), new R3(-6,-6,6), new R3(-6,6,-6), new R3(-6,6,6),  
                    new R3(6,-6,-6), new R3(6,-6,6), new R3(6,6,-6), new R3(6,6,6) };
        if (v.proj.stereographic) {            
            v.drawLineSegmentStereo(g, center, axes[0]);
            v.drawLineSegmentStereo(g, center, axes[1]);
            v.drawLineSegmentStereo(g, center, axes[2]);
            v.drawLineSegmentStereo(g, box[0], box[1]);
            v.drawLineSegmentStereo(g, box[0], box[2]);
            v.drawLineSegmentStereo(g, box[0], box[4]);
            v.drawLineSegmentStereo(g, box[1], box[3]);
            v.drawLineSegmentStereo(g, box[1], box[5]);
            v.drawLineSegmentStereo(g, box[2], box[3]);
            v.drawLineSegmentStereo(g, box[2], box[6]);
            v.drawLineSegmentStereo(g, box[4], box[5]);
            v.drawLineSegmentStereo(g, box[4], box[6]);
            v.drawLineSegmentStereo(g, box[3], box[7]);
            v.drawLineSegmentStereo(g, box[5], box[7]);
            v.drawLineSegmentStereo(g, box[6], box[7]);
        } else {
            v.drawLineSegment(g, center, axes[0]);
            v.drawLineSegment(g, center, axes[1]);
            v.drawLineSegment(g, center, axes[2]);
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
        }
        java.awt.geom.Point2D.Double winCenter = v.toWindow(new R3(6,0,0));
        g.drawString("x",(float)winCenter.x+5,(float)winCenter.y+5);
        winCenter = v.toWindow(new R3(0,6,0));
        g.drawString("y",(float)winCenter.x+5,(float)winCenter.y+5);
        winCenter = v.toWindow(new R3(0,0,6));
        g.drawString("z",(float)winCenter.x+5,(float)winCenter.y+5);
        // draws the scene's ellipse
        double sp = v.proj.viewDist.getValue()*v.proj.sceneSize.getValue()/(v.proj.viewDist.getValue()+v.proj.sceneSize.getValue());
        g.draw(v.ellipse(new R2(), v.proj.la * sp, v.proj.lb * sp));
        try {
            g.draw(v.dot(v.proj.getValue(new R3(v.proj.sceneSize.getValue(), 0, 0)), 5));
            g.draw(v.dot(v.proj.getValue(new R3(0, v.proj.sceneSize.getValue(), 0)), 5));
            g.draw(v.dot(v.proj.getValue(new R3(0, 0, v.proj.sceneSize.getValue() )), 5));
        } catch (FunctionValueException ex) {
            Logger.getLogger(Axes3D.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    // STYLE SETTINGS    
    
    @Override
    public String toString(){return "Axes";}

    @Override
    public String[] getStyleStrings() {
        String[] result = {"Standard", "Box"};
        return result;
    }
}
