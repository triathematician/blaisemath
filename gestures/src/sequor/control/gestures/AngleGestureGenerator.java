/*
 * AngleGestureGenerator.java
 * Created on Mar 25, 2008
 */

package sequor.control.gestures;

import java.util.HashMap;
import scio.coordinate.R2;
import scio.function.Function;
import scio.matrix.HashMatrix;

/**
 * AngleGestureGenerator is a class which can be used to construct objects based on mouse input (gestures).
 * This class supports multiple angles of movement.
 * 
 * @author Elisha Peterson
 */
public class AngleGestureGenerator extends GestureGenerator {
    /** The set of angles. Should be in increasing/cyclic order. */
    double[] angles;
    /** The set of labels for the angles. */
    String[] labels;
    
    // PARAMETERS
    
    /** Starting probability of no movement. */
    double startStaticProb=.01;
    /** Probability of transition from movement to stationary. */
    double moveToStaticProb=.05;
    /** Probability of remaining in same (moving) state. */
    double moveSame=.5;
    /** Probability of moving to an adjacent state. */
    double moveAdjacent=.15;
    
    
    // CONSTRUCTORS
    
    /** Constructor requires labels and angles. */    
    public AngleGestureGenerator(double[] angles,String[] labels){
        this.angles=angles;
        this.labels=labels;
    }

    // REQUIRED METHODS
    
    /** Returns label string. */
    @Override
    public String[] getStates() {return labels;}

    /** Returns starting probabilities. These are determined by startStaticProb. */
    @Override
    public HashMap<String, Double> getStartProb() {
        int n=angles.length;
        HashMap<String,Double> result=new HashMap<String,Double>();
        // static probability
        result.put(labels[0], startStaticProb);
        // angle probability is uniformly distributed
        for(int i=1;i<=n;i++){result.put(labels[i],(1-startStaticProb)/n);}
        return result;
    }

    @Override
    public HashMatrix<String, Double> getTransProb() {        
        int n=angles.length;
        HashMatrix<String,Double> result=new HashMatrix<String,Double>(labels);
        result.put(0,0,startStaticProb);
        // assign values between static and moving states
        for(int i=1;i<=n;i++){
            result.put(0,i,(1-startStaticProb)/n);
            result.put(i,0,moveToStaticProb);
        }
        // assign values between angle states according to function above
        try{
            double moveGeneral=(1-moveSame-moveToStaticProb-2*moveAdjacent)/(n-3);
            for(int i=1;i<=n;i++){
                for(int j=1;j<=n;j++){
                    if(i==j){
                        result.put(i,j,moveSame);
                    }else if(Math.abs(i-j)==1 || Math.abs(i+n-j)==1 || Math.abs(i-n-j)==1){
                        result.put(i,j,moveAdjacent);
                    }else{
                        result.put(i,j,moveGeneral);
                    }
                }
            }
        }catch(Exception e){}
        return result;
    }

    @Override
    public HashMap<String, Function<R2, Double>> getEmitProb() {      
        int n=angles.length;     
        HashMap<String,Function<R2,Double>> result=new HashMap<String,Function<R2,Double>>();
        result.put(labels[0],staticFunction);
        for(int i=1;i<=n;i++){
            result.put(labels[i],new AngleFunction(angles[i-1],Math.min(Math.PI/(n-1),Math.PI)));
        }
        return result;        
    }    
    
    
    // STATIC INSTANCES OF THIS CLASS
    
    public static class UpDown extends AngleGestureGenerator {
        static String[] lab={"0","S","N"};
        static double[] ang={0.5*Math.PI,1.5*Math.PI};
        public UpDown(){super(ang,lab);moveSame=.55;moveAdjacent=.35;}
    }
    
    public static class LeftRight extends AngleGestureGenerator {
        static String[] lab={"0","E","W"};
        static double[] ang={0.0,Math.PI};
        public LeftRight(){super(ang,lab);moveSame=.55;moveAdjacent=.35;}
    }
    
    public static class FourDir extends AngleGestureGenerator {
        static String[] lab={"0","E","S","W","N"};
        static double[] ang={0.0,0.5*Math.PI,Math.PI,1.5*Math.PI};
        public FourDir(){super(ang,lab);}
    }    
    
    public static class EightDir extends AngleGestureGenerator {
        static String[] lab={"0","E","SE","S","SW","W","NW","N","NE"};
        static double[] ang={0.0,0.25*Math.PI,0.5*Math.PI,0.75*Math.PI,1.0*Math.PI,1.25*Math.PI,1.5*Math.PI,1.75*Math.PI};
        public EightDir(){super(ang,lab);moveSame=.35;moveAdjacent=.15;}
    }
    
    public static class SixteenDir extends AngleGestureGenerator {
        static String[] lab={"0","E","ESE","SE","SSE","S","SSW","SW","WSW","W","WNW","NW","NNW","N","NNE","NE","ENE"};
        static double[] ang={
            0.0,Math.PI/8,2*Math.PI/8,3*Math.PI/8,4*Math.PI/8,5*Math.PI/8,6*Math.PI/8,7*Math.PI/8,
            8*Math.PI/8,9*Math.PI/8,10*Math.PI/8,11*Math.PI/8,12*Math.PI/8,13*Math.PI/8,14*Math.PI/8,15*Math.PI/8
        };        
        public SixteenDir(){super(ang,lab);moveSame=.15;moveAdjacent=.05;}
    }

}
