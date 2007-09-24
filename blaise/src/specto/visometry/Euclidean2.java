/*
 * Euclidean.java
 * Created on Sep 14, 2007, 8:10:03 AM
 */

package specto.visometry;

import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import javax.swing.JPanel;
import specto.Coordinate;
import specto.PlotPanel;
import specto.Visometry;
import specto.coordinate.R2;

/**
 * This class handles coordinate transformations betwen standard 2D Cartesian coordinates
 * and the display window. The transformation is determined by two sets of parameters: a minimum
 * and maximum coordinate, and the window dimensions.
 * <br><br>
 * This would make the translation easy, except that the aspect ratio of the underlying plot may
 * not match the aspect of the containing window. Hence, the actual window min/max coordinates are
 * computed to ensure that the desired min/max are within that containing window.
 * <br><br>
 * @author Elisha Peterson
 */
public class Euclidean2 extends Visometry<R2> {
    
    // PROPERTIES
    
    /** The transformation between Cartesian coordinates and the window */
    private AffineTransform at;
    
    /** Aspect ratio for use in the transform. */
    private double aspectRatio;
    
    /** Actual min,max,aspect of the containing panel. */
    private R2 actualMin,actualMax;
    private double windowAspect;
    
    /** Desired min,max of the containing panel. The plot MUST include these points!! */
    private R2 desiredMin=new R2(-10,-10);
    private R2 desiredMax=new R2(10,10);
    
    
    // CONSTRUCTORS
    
    public Euclidean2(){super();at=new AffineTransform();}
    public Euclidean2(PlotPanel p){super(p);at=new AffineTransform();}
    
    
    // INITIALIZERS    
    
    /** Sets the actual window boundaries using the window's aspect ratio and the
     * plot's aspect ratio, plus the desired coordinates within the window. */
    public void setBounds(R2 minPoint, R2 maxPoint){setDesiredMin(minPoint);setDesiredMax(maxPoint);}
        
    
    // BEAN PATTERNS: GETTERS & SETTERS
    
    public double getAspectRatio(){return aspectRatio;}
    public R2 getActualMin(){return actualMin;}
    public R2 getActualMax(){return actualMax;}
    public R2 getDesiredMin(){return desiredMin;}
    public R2 getDesiredMax(){return desiredMax;}
    public double getWindowAspect(){return windowAspect;}
    
    public void setAspectRatio(double newValue){if(newValue!=aspectRatio){newValue=aspectRatio;computeTransformation();}}
    //public void setActualMin(R2 newValue){if(!newValue.equals(actualMin)){newValue=actualMin;fireStateChanged();}}
    //public void setActualMax(R2 newValue){if(!newValue.equals(actualMax)){newValue=actualMax;fireStateChanged();}}
    public void setDesiredMin(R2 newValue){if(!newValue.equals(desiredMin)){newValue=desiredMin;computeTransformation();}}
    public void setDesiredMax(R2 newValue){if(!newValue.equals(desiredMax)){newValue=desiredMax;computeTransformation();}}
    public void setWindowAspect(double newValue){if(newValue!=windowAspect){newValue=windowAspect;computeTransformation();}}
    
    
    // CONVERT GEOMETRY COORDINATES TO WINDOW COORDINATES
    
    public int toWindowX(double vx){return (int)(vx*at.getScaleX()+at.getTranslateX());}
    public int toWindowY(double vy){return (int)(vy*at.getScaleY()+at.getScaleY());}
    public Point toWindow(double vx,double vy){return new Point(toWindowX(vx),toWindowY(vy));}
    public Point toWindow(R2 vp){return new Point(toWindowX(vp.x),toWindowY(vp.y));}
    
    // CONVERT WINDOW COORDINATES TO GEOMETRY COORDINATES
    
    public double toGeometryX(int wx){return (wx-at.getTranslateX())/at.getScaleX();}
    public double toGeometryY(int wy){return (wy-at.getTranslateY())/at.getScaleY();}
    public R2 toGeometry(int wx,int wy){return new R2(toGeometryX(wx),toGeometryY(wy));}
    public R2 toGeometry(Point wp){return new R2(toGeometryX(wp.x),toGeometryY(wp.y));}
    

    // METHODS TO HANDLE THE TRANSFORMATION COMPUTATION
    /** Computes the affine transform of the underlying window. */
    public double computeTransformation(){
        int windowWidth=container.getWidth();
        int windowHeight=container.getHeight();
        windowAspect=(double)windowWidth/(double)windowHeight;
        
        // Compute multiplier to determine how much the coordinates should be scaled.
        // This also computes the *actual* min and max points of the plot window.
        double multiplier=aspectRatio/windowAspect;
        actualMin=(R2)desiredMin.clone();
        actualMax=(R2)desiredMax.clone();
        
        // Need to add space in the x-direction... getPlotWidth should increase
        if (multiplier>1){
            double shift=0.5*(multiplier-1)*(actualMax.x-actualMin.x);
            actualMin.x-=shift;
            actualMax.x+=shift;
        }
        // Need to add space in the y-direction... getPlotHeight should increase
        else if (multiplier<1){
            double shift=0.5/(multiplier-1)*(actualMax.y-actualMin.y);
            actualMin.y-=shift;
            actualMax.y+=shift;
        }        
        
        // Now set the scale. Use x or y depending on which direction is the best fit.
        at.setToIdentity();        
        if (multiplier>1){multiplier=(double)windowWidth/(actualMax.x-actualMin.x);} 
        else{multiplier=(double)windowHeight/(actualMax.y-actualMin.y);}
        at.scale(multiplier,-multiplier);
        // Finally, work in the translation piece.
        at.translate(-actualMin.x,-actualMax.y);
        fireStateChanged();
        return multiplier;
    }
}
