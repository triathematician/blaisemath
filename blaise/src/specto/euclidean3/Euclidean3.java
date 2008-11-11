/*
 * Euclidean.java
 * Created on Sep 14, 2007, 8:10:03 AM
 */

package specto.euclidean3;

import java.awt.Shape;
import java.util.Vector;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import scio.coordinate.R2;
import scio.coordinate.R3;
import scio.function.Function;
import scio.function.FunctionValueException;
import sequor.model.DoubleRangeModel;
import specto.euclidean2.Euclidean2;

/**
 * This class handles coordinate transformations betwen standard 3D Cartesian coordinates
 * and the display window.
 * <br><br>
 * @author Elisha Peterson
 */
public class Euclidean3 extends Euclidean2 {

    // PARAMETERS OF THE DISPLAY

    DoubleRangeModel xRange = new DoubleRangeModel(0.0,-5.0,5.0,1.0);
    DoubleRangeModel yRange = new DoubleRangeModel(0.0,-5.0,5.0,1.0);
    DoubleRangeModel zRange = new DoubleRangeModel(0.0,-5.0,5.0,1.0);
    
    /** Determines rotation about the z axis. */
    DoubleRangeModel theta = new DoubleRangeModel(Math.PI/4,0.0,2*Math.PI,.0001);
    /** Determines rotation about the y axis. */
    DoubleRangeModel phi = new DoubleRangeModel(Math.PI/2,0.0,Math.PI,.0001);
    /** Determines zoom factor relative to underlying xy coordinates. */
    DoubleRangeModel zoom = new DoubleRangeModel(1.0,0.1,5.0,0.0001);
    /** Determines relative length of x and y axis. */
    DoubleRangeModel chi = new DoubleRangeModel(Math.sqrt(2),0,2*Math.sqrt(2),0.0001);
    /** Determines angle between x and y axis. */
    DoubleRangeModel zeta = new DoubleRangeModel(Math.PI/2,0.0,Math.PI,.0001);
    
    /** The proj determining how the plot is displayed. */
    Function<R3,R2> proj;
    
    
    // CONSTRUCTORS
    
    /** Default constructor */
    public Euclidean3(){
        resetProjectionFunction();
        initProjListening();
    }
    
    
    // INITIALIZERS
    
    /** Handles listening for chi, theta, zeta. */
    public void initProjListening() {
        ChangeListener cl = new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                resetProjectionFunction();
                fireStateChanged();
            }            
        };
        theta.addChangeListener(cl);
        phi.addChangeListener(cl);
        zoom.addChangeListener(cl);
    }
    
    // METHODS: PROJECTION HANDLERS
    
    public void resetProjectionFunction() {
        final double s2 = Math.sqrt(2)/2.0;
        final double z2 = Math.PI/2;
        proj = new Function<R3,R2>(){
            final R2 ihat = new R2(
                    Math.cos(theta.getValue()+z2)*Math.sin(phi.getValue()),
                    -s2*Math.sin(theta.getValue()+z2)*Math.sin(phi.getValue())-Math.cos(phi.getValue())
                    ).multipliedBy(zoom.getValue());
            final R2 jhat = new R2(
                    Math.cos(theta.getValue()),
                    -s2*Math.sin(theta.getValue())
                    ).multipliedBy(zoom.getValue());
            final R2 khat = new R2(
                    Math.cos(theta.getValue()+z2)*Math.cos(phi.getValue()),
                    -s2*Math.sin(theta.getValue()+z2)*Math.cos(phi.getValue())+Math.sin(phi.getValue())
                    ).multipliedBy(zoom.getValue());
            public R2 getValue(R3 pt) throws FunctionValueException {
                return new R2(
                        ihat.x*pt.getX()+jhat.x*pt.getY()+khat.x*pt.getZ(),
                        ihat.y*pt.getX()+jhat.y*pt.getY()+khat.y*pt.getZ()
                        );
            }
            public Vector<R2> getValue(Vector<R3> pts) throws FunctionValueException {
                Vector<R2> result = new Vector<R2>();
                for(R3 pt : pts) {
                    result.add(new R2(
                        ihat.x*pt.getX()+jhat.x*pt.getY()+khat.x*pt.getZ(),
                        ihat.y*pt.getX()+jhat.y*pt.getY()+khat.y*pt.getZ()
                        ));
                }
                return result;
            }    
        };
    }    
    
    // VISOMETRY ADJUSTMENTS
    
    java.awt.geom.Point2D.Double toWindow(R3 prm) {
        try {
            return toWindow(proj.getValue(prm));
        } catch (FunctionValueException ex) {
            return null;
        }
    }
    
    
    // BEAN PATTERNS
    
    public double getTheta(){ return theta.getValue(); }
    public void setTheta(double newTheta) { theta.setValue(newTheta % (2*Math.PI)); }

    public double getPhi(){ return phi.getValue(); }
    public void setPhi(double newTheta) { phi.setValue(newTheta % (Math.PI)); }
    
    public double getZoom() { return zoom.getValue(); }
    public void setZoom(double newZoom) { zoom.setValue(newZoom); }
    
    
    // HELPER METHODS
    
    public R3 getRandom() { return new R3(xRange.getRandom(), yRange.getRandom(), zRange.getRandom()); }
    
    
    // DRAW METHODS
    
    Shape dot(R3 pt, double d) {
        try { 
            return dot(proj.getValue(pt), d); 
        } catch (FunctionValueException ex) { 
            return null; 
        }
    }
    
    Shape lineSegment(R3 pt1, R3 pt2) {
        try {
            return lineSegment(proj.getValue(pt1), proj.getValue(pt2));
        } catch (Exception ex) {
            return null;
        }
    }

}
