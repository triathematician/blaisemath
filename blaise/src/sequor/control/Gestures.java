/**
 * Gestures.java
 * Created on Mar 25, 2008
 */

package sequor.control;

import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Vector;
import scio.coordinate.R2;
import scio.function.Function;
import scio.function.FunctionValueException;
import scio.matrix.HashMatrix;

/**
 * Class contains code for implementing mouse gestures.
 * @author Elisha Peterson
 */
public class Gestures {
    
    // REQUIRED ELEMENTS FOR THE MARKOV MODEL
    
    /** Underyling states. */
    public static String[] moveStates={"none","left","right","up","down"};     
    
    /** Starting probabilities. */
    public static HashMap<String,Double> getStartProb(double none){
        HashMap<String,Double> result=new HashMap<String,Double>();
        result.put("none",none);
        double move=(1-none)/4;
        result.put("left",move);
        result.put("right",move);
        result.put("up",move);
        result.put("down",move);
        return result;
    }  
    
    /** Transition probabilities.
     * @param none the probability that a stationary gesture remains stationary
     * @param same the probability that a movement right stays right
     * @param stop the probability that a moving pen stops
     * @param rev the probability that a moving pen reverses direction
     * @return appropriate transition matrix
     */
    public static HashMatrix<String,Double> getTransProb(double none,double same,double stop,double rev){
        double moveNone=(1-none)/4;
        double moveAdj=(1-same-stop-rev)/2;
        Double[][] tMx = {
            {none,moveNone,moveNone,moveNone,moveNone},
            {stop,same,rev,moveAdj,moveAdj},
            {stop,rev,same,moveAdj,moveAdj},
            {stop,moveAdj,moveAdj,same,rev},
            {stop,moveAdj,moveAdj,rev,same}
        };     
        return new HashMatrix<String,Double>(moveStates,tMx);
    }
    
    /** Emission probabilities for give states. */
    public static HashMap<String,Function<R2,Double>> getEmitProb(){
        HashMap<String,Function<R2,Double>> result=new HashMap<String,Function<R2,Double>>();
        result.put("none",staticFunction);
        result.put("left",new AngleFunction(Math.PI,Math.PI/3));
        result.put("right",new AngleFunction(0,Math.PI/3));
        result.put("up",new AngleFunction(1.5*Math.PI,Math.PI/3));
        result.put("down",new AngleFunction(.5*Math.PI,Math.PI/3));
        return result;        
    }
    
    
    
    // HELPER METHODS
    
    /** Returns probability of a point occurring given a movement. */
    public static Double getMoveProb(R2 pt){return pt.magnitude()/500;}
    /** Returns probability of an angle occurring given a movement. Zero if the angle difference is more than sensitivity. */
    public static Double getThetaProb(R2 pt,double theta,double sensitivity){
        theta=theta % (2*Math.PI);
        double ptheta=pt.angle() % (2*Math.PI);
        double dTheta=Math.min(Math.abs(ptheta-theta),Math.min(Math.abs(ptheta+2*Math.PI-theta),Math.abs(ptheta-2*Math.PI-theta)));
        if(dTheta>sensitivity){return 0.0;}
        if(sensitivity==0){return dTheta==0 ? 1 : 0.0;}
        return 1/sensitivity*(1-dTheta/sensitivity);
    }
    
    
    // POST-PROCESSING
    
    /** Clips path to just the essential elements. */
    public static Vector<String> clipOutput(Vector<String> output){
        Vector<String> result=new Vector<String>();
        String curState=null;
        int i=0;
        while(i<output.size()){
            while(i<output.size() && (output.get(i).equals(curState) || output.get(i).equals("none"))){i++;}
            if(i<output.size()){
                curState=output.get(i);
                result.add(curState);
            }
        }        
        return result;
    }
    
    
    final static String[] ccwCircle = {
        "left, down, right, up",
        "down, right, up, left",
        "right, up, left, down",
        "up, left, down, right"
    };    
    
    final static String[] cwCircle = {
        "left, up, right, down",
        "up, right, down, left",
        "right, down, left, up",
        "down, left, up, right"
    };
    
    final static String[] wobbleRight = {
        "up, right, down, right",
        "right, up, right, down",
        "down, right, up, right",
        "right, down, right, up"
    };
    
    final static String[] wobbleLeft = {
        "up, left, down, left",
        "left, up, left, down",
        "down, left, up, left",
        "left, down, left, up"
    };
    
    /** Preliminary function to recognize gestures. */
    public static Shape checkGesture(Vector<String> gesture){
        String gString=gesture.toString();
        for(int i=0;i<ccwCircle.length;i++){
            if(gString.contains(ccwCircle[i])){System.out.println(" CCW CIRCLE!! ");return new Ellipse2D.Double(50,50,100,100);}
        }
        for(int i=0;i<cwCircle.length;i++){
            if(gString.contains(cwCircle[i])){System.out.println(" CW CIRCLE!! ");return new Ellipse2D.Double(50,50,100,100);}
        }
        for(int i=0;i<wobbleLeft.length;i++){
            if(gString.contains(wobbleLeft[i])){System.out.println(" WOBBLE LEFT!! ");return new Rectangle2D.Double(50,50,100,100);}
        }
        for(int i=0;i<wobbleRight.length;i++){
            if(gString.contains(wobbleRight[i])){System.out.println(" WOBBLE RIGHT!! ");return new Rectangle2D.Double(50,50,100,100);}
        }
        return new Rectangle2D.Double(50,50,10,10);
    }
    
    
    
    // In the remainder of this class we implement functions necessary for the computations.
    // Each function represents the probability that a given point is expressed given the underlying state.
    // Thus, the probability that a given point occurs given no movement approaches 0 as r increases, etc.

    /** Function representing no movement. */
    public static Function<R2,Double> staticFunction=new Function<R2,Double>(){
        public Double getValue(R2 pt) throws FunctionValueException {return .4*(1-pt.magnitude()/5);}
        public Vector<Double> getValue(Vector<R2> x) throws FunctionValueException {return null;}
    };
    
    /** Returns function representing movement at a particular angle. */
    public static class AngleFunction implements Function<R2,Double> {
        double theta;
        double sens;
        public AngleFunction(double theta,double sens){this.theta=theta;this.sens=sens;}
        public Double getValue(R2 x) throws FunctionValueException {return getMoveProb(x)*getThetaProb(x,theta,sens);}
        public Vector<Double> getValue(Vector<R2> xx) throws FunctionValueException {return null;}
    }
}