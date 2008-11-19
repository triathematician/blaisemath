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
import scio.coordinate.R2;
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
public class FluxIntegral3D extends ParametricSurface3D {
    
    VectorField3D velocity;

    public FluxIntegral3D(VectorField3D vf3) {
        super();
        setForce(vf3);
        setColor(new Color(200,100,250,200));
    }

    public FluxIntegral3D(ParametricSurface3D ps1, VectorField3D vf3) {
        super();
        this.function = ps1.function;
        this.uvRange = ps1.uvRange;
        setForce(vf3);
        setColor(new Color(200,100,250,200));
    }

    public VectorField3D getForce() { return velocity; }
    public void setForce(VectorField3D forceFunction) { this.velocity = forceFunction; }
    
    /** Moves a particle given the applied velocity through a given arc length. */
    public R3 moveParticle(double u, double v, double ds) throws FunctionValueException {
        R3 pt = function.getValue(new R2(u,v));
        double du = velocity.getFunction().getValue(pt).dot(getTangentX(function,u,v))*ds;
        double dv = velocity.getFunction().getValue(pt).dot(getTangentY(function,u,v))*ds;
        return function.getValue(new R2(u+du, v+dv));
    }
    
    /** Moves a particle given the applied velocity through a given arc length. */
    public Vector<R2> getParticlePath(double u, double v, double ds, int n) throws FunctionValueException {
        Vector<R2> result = new Vector<R2>();
        R2 pt = new R2(u,v);
        result.add(pt);
        double du,dv;
        for (int i = 0; i < n; i++) {
            du = velocity.getFunction().getValue(function.getValue(pt)).dot(getTangentX(function,u,v))*ds;
            dv = velocity.getFunction().getValue(function.getValue(pt)).dot(getTangentY(function,u,v))*ds;
            pt = pt.plus(du, dv);
            result.add(pt);
        }
        return result;
    }
        
    /** Moves a particle given the applied velocity through a given arc length. */
    public Vector<R2> moveParticlePath(Vector<R2> pts, double ds, int n) throws FunctionValueException {
        R2 pt = pts.lastElement();
        double du,dv;
        for (int i = 0; i < n; i++) {
            du = velocity.getFunction().getValue(function.getValue(pt)).dot(getTangentX(function,pt.x,pt.y))*ds;
            dv = velocity.getFunction().getValue(function.getValue(pt)).dot(getTangentY(function,pt.x,pt.y))*ds;
            pt = pt.plus(du, dv);
            pts.add(pt);
            pts.remove(0);
        }
        return pts;
    }
    
    
    // COMPUTATION OF PARTICLE FLOW
    
    /** Stores scaling multiplier. */
    double step = .05;    
    /** Stores paths representing flowlines. */
    Vector<Vector<R2>> flows;    
    /** The length of the flows. */
    int NUM=2;
    /** Determines the number of random flows. */
    int NUM_RANDOM = 200;    
    /** Number to remove each time. */
    int RANDOM_TURNOVER = 2;
    
    @Override
    public void paintComponent(Graphics2D g, Euclidean3 v) { }
            
    @Override
    public void paintComponent(Graphics2D g, Euclidean3 v, RangeTimer t) {
        if(flows==null){
            flows = new Vector<Vector<R2>>();
            try {
                for (int i = 0; i < NUM_RANDOM; i++) {
                    flows.add(getParticlePath(uvRange.xModel.getRandom(),
                            uvRange.yModel.getRandom(), step, NUM));
                }
            } catch (FunctionValueException ex) {
                Logger.getLogger(VectorField2D.class.getName()).log(Level.SEVERE, null, ex);
            }
            t.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e) {
                    if (e.getActionCommand()==null){return;}
                    if (e.getActionCommand().equals("restart")){
                        flows = new Vector<Vector<R2>>();
                        try {
                            for (int i = 0; i < NUM_RANDOM; i++) {
                                flows.add(getParticlePath(uvRange.xModel.getRandom(),
                                        uvRange.yModel.getRandom(), step, NUM));
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
                    flows.add(getParticlePath(uvRange.xModel.getRandom(),
                            uvRange.yModel.getRandom(), step, NUM));
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
                Logger.getLogger(FluxIntegral3D.class.getName()).log(Level.SEVERE, null, ex);
            }
        }   
    }
}
