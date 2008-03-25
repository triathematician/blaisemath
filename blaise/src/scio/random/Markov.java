/*
 * Markov.java
 * Created on Mar 22, 2008
 */

package scio.random;

import java.util.HashMap;
import java.util.Vector;
import scio.coordinate.R2;
import scio.function.Function;
import scio.function.FunctionValueException;

/**
 * <p>
 * Markov is ...
 * </p>
 * @author Elisha Peterson
 */
public class Markov {
    
    /** Implements forward Viterbi algorithm for computing most likely underlying states in a Markov model.
     * @param observations the set of observations
     * @param hiddenStates the set of hidden states available in the model, as a set of Strings
     * @param startProb the starting probabilities of each of the states
     * @param transProb the transition probabilities taking one state into another
     * @param emitProb the probability of a given observation given a particular hidden staet
     */
    public static CurrentState forwardViterbi(
            R2[] observations,
            String[] hiddenStates,
            HashMap<String,Double> startProb,
            HashMap<String,HashMap<String,Double>> transProb,
            HashMap<String,Function<R2,Double>> emitProb) throws FunctionValueException {
        
        int ns=hiddenStates.length;
        
        // Initialize the vector which will store optimal paths up to the current observation.
        CurrentState[] tt=new CurrentState[ns];
        for(int i=0;i<ns;i++){
            tt[i]=new CurrentState(startProb,hiddenStates[i]);
        }
        
        // For each observation, update the values in tt with the optimal paths
        for(int obs=0;obs<observations.length;obs++){
            
            // Compute best paths for each current possible state
            CurrentState[] uu=new CurrentState[ns];
            for(int nextState=0;nextState<ns;nextState++){
                
                // Stores the most likely path up to the current observation
                CurrentState mostLikely=new CurrentState();
                
                // Check through all possible current states to determine the most likely
                for(int sourceState=0;sourceState<ns;sourceState++){
                    
                    CurrentState temp=tt[sourceState];
                    double tp = emitProb.get(hiddenStates[sourceState]).getValue(observations[obs])
                            * transProb.get(hiddenStates[sourceState]).get(hiddenStates[nextState]);
                    temp.totalProb*=tp;
                    temp.vitProb*=tp;
                    mostLikely.totalProb += temp.totalProb;
                    if(temp.vitProb>mostLikely.vitProb){
                        mostLikely.vitPath=new Vector<String>();
                        mostLikely.vitPath.addAll(temp.vitPath);
                        mostLikely.vitPath.add(hiddenStates[nextState]);
                        mostLikely.vitProb=temp.vitProb;
                    }                    
                }
                uu[nextState]=mostLikely;
            }
            tt=uu;
        }
        
        /** Constructs the resulting path. */
        CurrentState result=new CurrentState();
        for(int st=0;st<ns;st++){
            result.totalProb += tt[st].totalProb;
            result.replaceIfGreater(tt[st]);
        }
        result.vitPath.removeElementAt(result.vitPath.size()-1);
        return result;
    }
    
    /** Helpful class for the Viterbi algorithm */
    public static class CurrentState{
        public CurrentState(){vitPath=new Vector<String>();}
        public CurrentState(double p,Vector<String> path,double vp){totalProb=p;vitPath=path;vitProb=vp;}
        private CurrentState(HashMap<String, Double> startProb, String s){this(startProb.get(s),s,startProb.get(s));}
        private CurrentState(double p, String s, double vp) {
            totalProb=p;
            vitProb=vp;
            vitPath=new Vector<String>();
            vitPath.add(s);
        }
        /** Total probability up to the current state. */
        public double totalProb=0;
        /** The current "viterbi path" giving the maximized probability. */
        public Vector<String> vitPath;
        /** The probability of the current "most likely" path. */
        public double vitProb=0;
        
        public String toString(){return vitPath.toString();}
        
        public void replaceIfGreater(CurrentState state){
            if(state.vitProb>vitProb){
                vitPath=state.vitPath;
                vitProb=state.vitProb;
            }
        }
    }
    
    public static String[] moveStrings={"none","left","right","up","down"};
            
    /** Function representing no movement. */
    public static Function<R2,Double> staticFunction=new Function<R2,Double>(){
        public Double getValue(R2 pt) throws FunctionValueException {return (5-pt.toRTheta().x)*(4/25);}
        public Vector<Double> getValue(Vector<R2> x) throws FunctionValueException {return null;}
        public Double minValue() {return null;}
        public Double maxValue() {return null;}
    };
    
    public Double getMoveProb(R2 pt){return pt.toRTheta().x/1000;}
    public Double getThetaProb(R2 pt,double theta){
        R2 rt=pt.toRTheta();
        return null;        
    }
    
    /** Function representing a particular theta range. */
    public static Function<R2,Double> thetaFunction=new Function<R2,Double>(){
        
        public Double getValue(R2 pt) throws FunctionValueException{return pt.toRTheta().x/1000;}
        public Vector<Double> getValue(Vector<R2> x) throws FunctionValueException {return null;}
        public Double minValue() {return null;}
        public Double maxValue() {return null;}
    };
    
    /** Function representing a "left" movement. */
    public static Function<R2,Double> leftFunction=new Function<R2,Double>(){
        public Double getValue(R2 pt) throws FunctionValueException {
            double r=pt.toRTheta().x;
            double p=(5-r)*(4/25);
            double x=r;
            return (x<0)?0.8:(x<.5)?0.8-x*8/5.0:0.0;
        }
        public Vector<Double> getValue(Vector<R2> x) throws FunctionValueException {return null;}
        public Double minValue() {return null;}
        public Double maxValue() {return null;}
    };
    /** Function representing a "right" movement. */
    public static Function<R2,Double> rightFunction=new Function<R2,Double>(){
        public Double getValue(R2 pt) throws FunctionValueException {
            double x=pt.x;
            return (x<0)?0.8:(x<.5)?0.8-x*8/5.0:0.0;
        }
        public Vector<Double> getValue(Vector<R2> x) throws FunctionValueException {return null;}
        public Double minValue() {return null;}
        public Double maxValue() {return null;}
    };
    
    /** Function representing an "up" movement. */
    public static Function<R2,Double> upFunction=new Function<R2,Double>(){
        public Double getValue(R2 pt) throws FunctionValueException {
            double x=pt.x;
            return (x<0)?0.8:(x<.5)?0.8-x*8/5.0:0.0;
        }
        public Vector<Double> getValue(Vector<R2> x) throws FunctionValueException {return null;}
        public Double minValue() {return null;}
        public Double maxValue() {return null;}
    };
    
    /** Fucntion representing a "down" movement. */
    public static Function<R2,Double> downFunction=new Function<R2,Double>(){
        public Double getValue(R2 pt) throws FunctionValueException {
            double x=pt.x;
            return (x<0)?0.8:(x<.5)?0.8-x*8/5.0:0.0;
        }
        public Vector<Double> getValue(Vector<R2> x) throws FunctionValueException {return null;}
        public Double minValue() {return null;}
        public Double maxValue() {return null;}
    };
}
