/*
 * Euclidean.java
 * Created on Sep 14, 2007, 8:10:03 AM
 */

package specto.euclidean3;

import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import scio.coordinate.R2;
import scio.coordinate.R3;
import scio.function.Function;
import scio.function.FunctionValueException;
import sequor.model.DoubleRangeModel;
import sequor.model.IntegerRangeModel;
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
    
    
    /** The proj determining how the plot is displayed. */
    ViewProjection proj = new ViewProjection();
    
    
    // CONSTRUCTORS
    
    /** Default constructor */
    public Euclidean3(){
        initProjListening();
    }
    
    
    // INITIALIZERS
    
    /** Handles listening for chi, theta, zeta. */
    public void initProjListening() {
        ChangeListener cl = new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                fireStateChanged();
            }            
        };
        proj.clipDist.addChangeListener(cl);
        proj.viewDist.addChangeListener(cl);
        proj.sceneSize.addChangeListener(cl);
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
    

    
    // HELPER METHODS
    
    public R3 getRandom() { return new R3(xRange.getRandom(), yRange.getRandom(), zRange.getRandom()); }
    
    
    // DRAW METHODS
    
    Shape dot(R3 pt, double d) {
        try {
            return dot(proj.getValue(pt), d);
        } catch (FunctionValueException ex) {
            Logger.getLogger(Euclidean3.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    Shape lineSegment(R3 pt1, R3 pt2) {
        try {
            return lineSegment(proj.getValue(pt1), proj.getValue(pt2));
        } catch (FunctionValueException ex) {
            Logger.getLogger(Euclidean3.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    
    
    // MOUSE EVENT HANDLING
    
    boolean rotating = false;
    
    R3 initialPoint;
    R3 finalPoint;
    R3 priorPoint;
    
    R3 t0;
    R3 n0;
    R3 b0;
        
    @Override
    public void mousePressed(MouseEvent e){
        rotating = false;
        pressedAt=e.getPoint();
        mode=MouseEvent.getModifiersExText(e.getModifiersEx());
        if(mode.equals("Ctrl+Button1")){
            initialPoint = proj.getIValue(toGeometry(pressedAt));   
            t0 = proj.tDir;
            n0 = proj.nDir;
            b0 = proj.bDir;
        }else {
            super.mousePressed(e);
        }
    }
    
    @Override
    public void mouseDragged(MouseEvent e){
        if(pressedAt!=null){
            if(mode.equals("Ctrl+Button1")){
                priorPoint = finalPoint;
                finalPoint = proj.getIValue(toGeometry(e.getPoint()), t0, n0, b0);
                proj.rotate(initialPoint, finalPoint, t0, n0, b0);
            }else {
                super.mouseDragged(e);
            }
        }
    }
    
    @Override
    public void mouseReleased(MouseEvent e){
        //mouseDragged(e);
        if(pressedAt!=null){
            if (mode.equals("Alt+Button1")){
                container.remove(zoomBox);
                zoomBoxAnimated(zoomBox);
                zoomBox=null;
            } else if (mode.equals("Ctrl+Button1") && priorPoint != null && finalPoint != null) {
                proj.animateRotation(priorPoint, finalPoint);
            }
        }
        pressedAt=null;
        oldMin=null;
        oldMax=null;
        mode=null;
        initialPoint=null;
        finalPoint=null;
        t0=null;
        n0=null;
        b0=null;
        System.out.println("t,n,b:"+proj.tDir+","+proj.nDir+","+proj.bDir);
    }
    
    
    
    // INNER CLASSES
    
    /** Handles the projection onto the viewing plane. */
    public class ViewProjection implements Function<R3,R2> {

        /** Distance to clipping plane (in cm) */
        DoubleRangeModel clipDist = new DoubleRangeModel(2.0,0.1,10.0,0.1);
        /** Distance to view plane (in cm) */
        DoubleRangeModel viewDist = new DoubleRangeModel(50.0,0.01,100.0,0.1);
        /** Distance from the scene of interest */
        DoubleRangeModel sceneSize = new DoubleRangeModel(5.0,0.01,100.0,0.1);
        
        /** Determines whether stereographic projection mode is on. */
        boolean stereographic = true;
        /** Distance from the scene of interest */
        DoubleRangeModel eyeSep = new DoubleRangeModel(2.0,0.01,20.0,0.02);
        
        /** Central point of interest. */
        R3 center = new R3(0,0,0);
        /** View direction. */
        R3 tDir = new R3(-3/4.,-1/2.,-Math.sqrt(3)/4.);
        /** Normal direction. */
        R3 nDir = new R3(-1/2.,3/4.,0);
        /** Binormal direction. */
        R3 bDir = new R3(-3*Math.sqrt(3)/16.,-Math.sqrt(3)/8.,13/16.);
        
        /** Controls speed of timer */
        IntegerRangeModel timerDelay = new IntegerRangeModel(50,1,500,1);

        double la = 2;
        double lb = 2;
    
        /** Returns projection of a 3d point into 2d */
        public R2 getValue(R3 pt) throws FunctionValueException {
            R3 cE = center.minus(tDir.times(viewDist.getValue()+sceneSize.getValue()));
            R3 diff = pt.minus(cE);
            return new R2(diff.dot(nDir)*la, diff.dot(bDir)*lb).times(viewDist.getValue()/diff.dot(tDir));
        }
        
        /** Returns projection of several 3d points into 2d */
        public Vector<R2> getValue(Vector<R3> pts) throws FunctionValueException {
            R3 cE = (R3) center.minus(tDir.times(viewDist.getValue()+sceneSize.getValue()));
            R3 diff;
            Vector<R2> result = new Vector<R2>();
            for (R3 pt : pts) {
                diff = pt.minus(cE);
                result.add(new R2(diff.dot(nDir)*la, diff.dot(bDir)*lb).times(viewDist.getValue()/diff.dot(tDir)));
            }   
            return result;
        }
        
        /** Returns shifted stereo point (left eye) */
        public R2 getValueLeft(R3 pt) throws FunctionValueException {
            R3 cE = center.minus(tDir.times(viewDist.getValue()+sceneSize.getValue()))
                    .plus(nDir.times(-eyeSep.getValue()/2));
            R3 diff = pt.minus(cE);
            return new R2(diff.dot(nDir)*la, diff.dot(bDir)*lb).times(viewDist.getValue()/diff.dot(tDir));
        }
        
        /** Returns projection of several 3d points into 2d */
        public Vector<R2> getValueLeft(Vector<R3> pts) throws FunctionValueException {
            R3 cE = center.minus(tDir.times(viewDist.getValue()+sceneSize.getValue()))
                    .plus(nDir.times(-eyeSep.getValue()/2));
            R3 diff;
            Vector<R2> result = new Vector<R2>();
            for (R3 pt : pts) {
                diff = pt.minus(cE);
                result.add(new R2(diff.dot(nDir)*la, diff.dot(bDir)*lb).times(viewDist.getValue()/diff.dot(tDir)));
            }   
            return result;
        }
        
        /** Returns shifted stereo point (right eye) */
        public R2 getValueRight(R3 pt) throws FunctionValueException {
            R3 cE = center.minus(tDir.times(viewDist.getValue()+sceneSize.getValue()))
                    .plus(nDir.times(eyeSep.getValue()/2));
            R3 diff = pt.minus(cE);
            return new R2(diff.dot(nDir)*la, diff.dot(bDir)*lb).times(viewDist.getValue()/diff.dot(tDir));
        }
        
        /** Returns projection of several 3d points into 2d */
        public Vector<R2> getValueRight(Vector<R3> pts) throws FunctionValueException {
            R3 cE = center.minus(tDir.times(viewDist.getValue()+sceneSize.getValue()))
                    .plus(nDir.times(eyeSep.getValue()/2));
            R3 diff;
            Vector<R2> result = new Vector<R2>();
            for (R3 pt : pts) {
                diff = pt.minus(cE);
                result.add(new R2(diff.dot(nDir)*la, diff.dot(bDir)*lb).times(viewDist.getValue()/diff.dot(tDir)));
            }   
            return result;
        }
        
        /** Takes an element in the viewing plane and converts it into the 3d point on that plane. */
        public R3 getIValue(R2 pt) { return getIValue(pt,tDir,nDir,bDir); }
        
        /** Takes an element in the viewing plane and converts it into the 3d point on that plane, for specified direction vectors. */
        public R3 getIValue(R2 pt, R3 t0, R3 n0, R3 b0) {
            return center.minus(t0.times(sceneSize.getValue()))
                    .plus( n0.times(pt.x/la) )
                    .plus (b0.times(pt.y/lb) );
        }
        
        
        // ADJUSTMENTS TO THE CAMERA        
        
        /** Rotates the projection along the great circle with center at the scene's sphere and from one outer point to another.
         * @param p1 the first point along the circle of rotation
         * @param p2 the second point along the circle of rotation
         */
        public void rotate(R3 p1, R3 p2) { rotate(p1, p2, tDir, nDir, bDir); }
        
        /** Rotates with respect to given initial frame.
         * @param p1 the first point along the circle of rotation
         * @param p2 the second point along the circle of rotation
         */
        public void rotate(R3 p1, R3 p2, R3 t0, R3 n0, R3 b0) {
            R3 u1 = p1.minus(center);
            R3 u2 = p2.minus(center);
            R3 n = u1.cross(u2).normalized();
            double cosphi = u1.dot(u2)/(u1.magnitude()*u2.magnitude());
            double sinphi = Math.sqrt(1-cosphi*cosphi);
            // can use either single or double rotation here
            tDir = doubleRotate(t0, n, cosphi, sinphi);
            nDir = doubleRotate(n0, n, cosphi, sinphi);
            bDir = doubleRotate(b0, n, cosphi, sinphi);         
            fireStateChanged();
        }
        
        /** Formula for a double rotation... makes it quicker to rotate the figure */
        public R3 doubleRotate (R3 pt, R3 n, double cosphi, double sinphi) {
            return eulerRotation(eulerRotation(pt,n,cosphi,sinphi),n,cosphi,sinphi);
        }
        
        /** Formula for the rotation about a specific axis given by a normal vector n */
        public R3 eulerRotation (R3 pt, R3 n, double cosphi, double sinphi) {
            return pt.times(cosphi)
                    .plus( n.times(n.dot(pt)*(1-cosphi)) )
                    .plus( (pt.cross(n)).times(sinphi) );
        }
        
        
        // HANDLES ANIMATED ROTATIONS
        
        /** Begins rotation animating if the points are far enough apart. */
        public void animateRotation(R3 p1, R3 p2) {
            R3 u1 = p1.minus(center);
            R3 u2 = p2.minus(center);
            R3 n = u1.cross(u2).normalized();
            double cosphi = u1.dot(u2)/(u1.magnitude()*u2.magnitude());
            double sinphi = Math.sqrt(1-cosphi*cosphi);
            if (Math.abs(sinphi) > .01) {
                rotating = true;
                animateRotation(n, cosphi, sinphi);
            }
        }
        
        /** Animates a rotation. */
        public void animateRotation(final R3 n, final double cosphi, final double sinphi) {
            Thread runner=new Thread(new Runnable(){
                public void run() {
                    while(rotating){
                        try{Thread.sleep(timerDelay.getValue());}catch(Exception e){}
                        tDir = eulerRotation(tDir, n, cosphi, sinphi);
                        nDir = eulerRotation(nDir, n, cosphi, sinphi);
                        bDir = eulerRotation(bDir, n, cosphi, sinphi);  
                        fireStateChanged();
                    }
                }            
            });
            runner.start();
        }
    }    
}
