/**
 * ShapeGestureGenerator.java
 * Created on Mar 26, 2008
 */

package sequor.control.gestures;

import java.util.HashMap;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import scio.function.Function;
import scio.function.FunctionValueException;
import scio.matrix.HashMatrix;
import scio.random.Markov;

/**
 * This class takes input from the Gesture computations and predicts arc vs. straight paths.
 * 
 * @author Elisha Peterson
 */
public class ShapeGestureGenerator {
    
    public static Function<Integer,Double> lineProb = new Function<Integer,Double>(){
        public Double getValue(Integer x) throws FunctionValueException {
            double[] table={0.9,.04,.02};
            try{ 
                return table[Math.abs(x)];
            }catch(Exception e){return 0.0;}
        }

        public Vector<Double> getValue(Vector<Integer> xx) throws FunctionValueException {return null;}
    };
        
    public static Function<Integer,Double> arcRightProb = new Function<Integer,Double>(){
        public Double getValue(Integer x) throws FunctionValueException {
            double[] posTable={0.4,.3,.15,.075,.025};
            double[] negTable={0.4,.04,.01};
            try{ 
                return (x>0) ? posTable[x] : negTable[-x];
            }catch(Exception e){return 0.0;}
        }
        public Vector<Double> getValue(Vector<Integer> xx) throws FunctionValueException {return null;}
    };
        
    public static Function<Integer,Double> arcLeftProb = new Function<Integer,Double>(){
        public Double getValue(Integer x) throws FunctionValueException {
            double[] posTable={0.4,.04,.01};
            double[] negTable={0.4,.3,.15,.075,.025};
            try{ 
                return (x>0) ? posTable[x] : negTable[-x];
            }catch(Exception e){return 0.0;}
        }
        public Vector<Double> getValue(Vector<Integer> xx) throws FunctionValueException {return null;}
    };
    
    public static Function<Integer,Double> cornerProb = new Function<Integer,Double>(){
        public Double getValue(Integer x) throws FunctionValueException {
            double[] table = {0.0,0.0,0.0,0.025,0.075,0.10,0.10,0.10,0.10};
            try{ 
                return table[Math.abs(x)];
            }catch(Exception e){return 0.0;}
        }
        public Vector<Double> getValue(Vector<Integer> xx) throws FunctionValueException {return null;}
    };
    
    /** Computes estimated arc vs. corner vs. straight states given a vector of differences between angle states. */
    public static Vector<String> compute(Vector<Integer> diffs) {
        
        Integer[] obs = new Integer[diffs.size()];
        for(int i=0;i<diffs.size();i++){obs[i]=diffs.get(i);}
        
        String[] states = {"Corner", "ArcLeft", "ArcRight", "Line"};

        HashMap<String, Double> startProb = new HashMap<String, Double>();
        startProb.put("Corner", .10);
        startProb.put("ArcLeft", .275);
        startProb.put("ArcRight", .275);
        startProb.put("Line", .35);

        Double[][] tp = {{.01, .25, .25, .49}, {.1, .7, .1, .1}, {.1, .1, .7, .1}, {.25, .025, .025, .7}};
        HashMatrix<String, Double> transProb = new HashMatrix<String, Double>(states, tp);

        HashMap<String, Function<Integer, Double>> emitProb = new HashMap<String, Function<Integer, Double>>();
        emitProb.put("Corner", cornerProb);
        emitProb.put("ArcLeft", arcLeftProb);
        emitProb.put("ArcRight", arcRightProb);
        emitProb.put("Line", lineProb);

        try {
            return new Markov<String, Integer>().forwardViterbi(obs, states, startProb, transProb, emitProb).vitPath;
        } catch (Exception ex) {
            Logger.getLogger(ShapeGestureGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
