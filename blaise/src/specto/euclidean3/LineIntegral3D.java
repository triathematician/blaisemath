/**
 * LineIntegral3D.java
 * Created on Nov 19, 2008
 */

package specto.euclidean3;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import scio.coordinate.R3;
import scio.function.FunctionValueException;
import sequor.component.RangeTimer;
import sequor.style.VisualStyle;
import specto.euclidean2.VectorField2D;

/**
 * <p>
 * This class is used to visualize line integrals along a curve.
 * </p>
 * @author Elisha Peterson
 */
public class LineIntegral3D extends ParametricCurve3D {
    
    VectorField3D velocity;

    public LineIntegral3D(VectorField3D vf3) {
        super();
        setForce(vf3);
        setColor(new Color(128,0,128));
    }

    public LineIntegral3D(ParametricCurve3D pc1, VectorField3D vf3) {
        super();
        this.function = pc1.function;
        this.tRange = pc1.tRange;
        setForce(vf3);
        setColor(new Color(128,0,128).brighter());
    }

    public VectorField3D getForce() { return velocity; }
    public void setForce(VectorField3D forceFunction) { this.velocity = forceFunction; }
    
    /** Moves a particle given the applied velocity through a given arc length. */
    public R3 moveParticle(double t, double ds) throws FunctionValueException {
        double dt = velocity.getFunction().getValue(function.getValue(t)).dot(getTangentVector(function, t))*ds;
        return function.getValue(t+dt);
    }
    
    /** Moves a particle given the applied velocity through a given arc length. */
    public Vector<Double> getParticlePath(double t, double ds, int n) throws FunctionValueException {
        Vector<Double> result = new Vector<Double>();
        result.add(t);
        double dt;
        for (int i = 0; i < n; i++) {
            dt = velocity.getFunction().getValue(function.getValue(t)).dot(getTangentVector(function, t))*ds;
            t+=dt;
            result.add(t);            
        }
        return result;
    }
        
    /** Moves a particle given the applied velocity through a given arc length. */
    public Vector<Double> moveParticlePath(Vector<Double> ts, double ds, int n) throws FunctionValueException {
        double t = ts.lastElement();
        double dt;
        for (int i = 0; i < n; i++) {
            dt = velocity.getFunction().getValue(function.getValue(t)).dot(getTangentVector(function, t))*ds;
            t += dt;
            ts.add(t);  
            ts.remove(0);
        }
        return ts;
    }
    
    
    // COMPUTATION OF PARTICLE FLOW
    
    /** Stores the sample points. */
    Vector<R3> particles;    
    /** Stores scaling multiplier. */
    double step = .05;    
    /** Stores paths representing flowlines. */
    Vector<Vector<Double>> flows;    
    /** The length of the flows. */
    int NUM=2;
    /** Determines the number of random flows. */
    int NUM_RANDOM = 30;    
    /** Number to remove each time. */
    int RANDOM_TURNOVER = 2;
        
    @Override
    public void paintComponent(Graphics2D g, Euclidean3 v, RangeTimer t) {
        if(flows==null){
            flows = new Vector<Vector<Double>>();
            try {
                for (int i = 0; i < NUM_RANDOM; i++) {
                    flows.add(getParticlePath(tRange.getRandom(), step, NUM));
                }
            } catch (FunctionValueException ex) {
                Logger.getLogger(VectorField2D.class.getName()).log(Level.SEVERE, null, ex);
            }
            t.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e) {
                    if (e.getActionCommand()==null){return;}
                    if (e.getActionCommand().equals("restart")){
                        flows = new Vector<Vector<Double>>();
                        try {
                            for (int i = 0; i < NUM_RANDOM; i++) {
                                flows.add(getParticlePath(tRange.getRandom(), step, NUM));
                            }
                        } catch (FunctionValueException ex) {
                            Logger.getLogger(VectorField2D.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }                    
            });
        } else {
            try {
                for (int i = 0; i < RANDOM_TURNOVER; i++) {
                    flows.remove(0);
                }
                for (int i = 0; i < flows.size(); i++) {
                    moveParticlePath(flows.get(i), step, t.getSpeed()+2);
                }
                for (int i = 0; i < RANDOM_TURNOVER; i++) {
                    flows.add(getParticlePath(tRange.getRandom(), step, NUM));
                }   
            } catch (FunctionValueException ex) {
                Logger.getLogger(VectorField3D.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        for (int i = 0; i < flows.size(); i++) {
            try {
                g.setColor(getColor());
                g.setStroke(VisualStyle.VERY_THICK_STROKE);
                v.drawPath(g, function.getValue(flows.get(i)));
            } catch (FunctionValueException ex) {
                Logger.getLogger(LineIntegral3D.class.getName()).log(Level.SEVERE, null, ex);
            }
        }   
    }
}
