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
import scio.matrix.HashMatrix;

/**
 * Markov is used to implement Hidden Markov Models. V is the class of the underlying state. W is the class of the observed state.
 * The observed state may be continuous, hence is generically indicated by a function.
 * 
 * @author Elisha Peterson
 */
public class Markov<V,W> {

    /** Implements forward Viterbi algorithm for computing most likely underlying states in a Markov model.
     * @param observations the set of observations
     * @param hiddenStates the set of hidden states available in the model, as a set of Strings
     * @param startProb the starting probabilities of each of the states
     * @param transProb the transition probabilities taking one state into another
     * @param emitProb the probability of a given observation given a particular hidden staet
     */
    public CurrentState forwardViterbi(
            W[] observations,
            V[] hiddenStates,
            HashMap<V,Double> startProb,
            HashMatrix<V,Double> transProb,
            HashMap<V,Function<W,Double>> emitProb) throws FunctionValueException {
        
        int ns=hiddenStates.length;
        
        // Initialize the vector which will store optimal paths up to the current observation.
        Vector<CurrentState> tt=new Vector<CurrentState>(ns);
        for(int i=0;i<ns;i++){
            tt.add(new CurrentState(startProb,hiddenStates[i]));
        }
        
        // For each observation, update the values in tt with the optimal paths
        for(int obs=0;obs<observations.length;obs++){        
            //for(int i=0;i<ns;i++){System.out.println("  "+tt.get(i));}    
            // Compute best paths for each current possible state
            Vector<CurrentState> uu=new Vector<CurrentState>(ns);
            for(int nextState=0;nextState<ns;nextState++){                
                // Stores the most likely path up to the current observation
                CurrentState mostLikely=new CurrentState();                
                // Check through all possible current states to determine the most likely
                for(int sourceState=0;sourceState<ns;sourceState++){                    
                    CurrentState temp=new CurrentState(tt.get(sourceState));
                    double tp = emitProb.get(hiddenStates[sourceState]).getValue(observations[obs])
                            * transProb.get(hiddenStates[sourceState],hiddenStates[nextState]);
                    temp.totalProb*=tp;
                    temp.vitProb*=tp;
                    mostLikely.totalProb += temp.totalProb;
                    if(temp.vitProb>mostLikely.vitProb){
                        mostLikely.vitPath=new Vector<V>();
                        mostLikely.vitPath.addAll(temp.vitPath);
                        mostLikely.vitPath.add(hiddenStates[nextState]);
                        mostLikely.vitProb=temp.vitProb;
                    }                    
                }
                uu.add(mostLikely);
            }
            // store computation and normalize probabilities
            tt=uu;
            //double maxProb=0.0;
            //for(int i=0;i<ns;i++){maxProb=Math.max(maxProb,tt.get(i).vitProb);}
            //for(int i=0;i<ns;i++){tt.get(i).vitProb/=maxProb;}
        }
        
        /** Constructs the resulting path. */
        CurrentState result=new CurrentState();
        for(int st=0;st<ns;st++){
            result.totalProb += tt.get(st).totalProb;
            result.replaceIfGreater(tt.get(st));
        }
        if(result.vitPath.size()>1){
            result.vitPath.removeElementAt(result.vitPath.size()-1);
        }
        return result;
    }
    
    /** Helpful class for the Viterbi algorithm */
    public class CurrentState{
        public CurrentState(){vitPath=new Vector<V>();}
        public CurrentState(double p,Vector<V> path,double vp){totalProb=p;vitPath=path;vitProb=vp;}
        private CurrentState(CurrentState copee){totalProb=copee.totalProb;vitPath=copee.vitPath;vitProb=copee.vitProb;}
        private CurrentState(HashMap<V,Double> startProb, V s){this(startProb.get(s),s,startProb.get(s));}
        private CurrentState(double p, V s, double vp) {
            totalProb=p;
            vitProb=vp;
            vitPath=new Vector<V>();
            vitPath.add(s);
        }
        /** Total probability up to the current state. */
        public double totalProb=0;
        /** The current "viterbi path" giving the maximized probability. */
        public Vector<V> vitPath;
        /** The probability of the current "most likely" path. */
        public double vitProb=0;
        
        @Override
        public String toString(){return vitPath.toString()+" at "+vitProb;}
        
        public void replaceIfGreater(CurrentState state){
            if(state.vitProb>vitProb){
                vitPath=state.vitPath;
                vitProb=state.vitProb;
            }
        }
    }
}
