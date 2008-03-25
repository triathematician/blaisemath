/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package scio.random;

import java.util.HashMap;
import java.util.Vector;
import junit.framework.TestCase;
import scio.coordinate.R2;
import scio.function.Function;
import scio.function.FunctionValueException;
import scio.random.Markov.CurrentState;

/**
 *
 * @author elisha
 */
public class MarkovTest extends TestCase {
    
    public MarkovTest(String testName) {
        super(testName);
    }            

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test of forwardViterbi method, of class Markov.
     */
    public void testForwardViterbi() throws Exception {
        System.out.println("forwardViterbi");
        
        String[] hiddenStates = {"left","right"};
        R2[] observations = {
            new R2(.5,0),
            new R2(1,0), new R2(.5,0),
            new R2(-.3,.1), new R2(-.8,1), new R2(-.1,1), new R2(0,0), new R2(.1,0),
            new R2(.5,0), new R2(.9,1)};
        HashMap<String,Double> startProb = new HashMap<String,Double>();
        {
            startProb.put("left", .5);
            startProb.put("right",.5);
        }
        HashMap<String,HashMap<String,Double>> transProb = new HashMap<String,HashMap<String,Double>>();
        {
            HashMap<String,Double> left=new HashMap<String,Double>();
            left.put("left",.3);
            left.put("right",.7);
            HashMap<String,Double> right=new HashMap<String,Double>();
            right.put("left",.3);
            right.put("right",.7);            
            transProb.put("left",left);
            transProb.put("right",right);
        }
        HashMap<String, Function<R2, Double>> emitProb = new HashMap<String,Function<R2,Double>>();
        {
            emitProb.put("left",new Function<R2,Double>(){
                public Double getValue(R2 pt) throws FunctionValueException {
                    double x=pt.x;
                    return (x<0)?0.8:(x<.5)?0.8-x*8/5.0:0.0;
                }
                public Vector<Double> getValue(Vector<R2> x) throws FunctionValueException {return null;}
                public Double minValue() {return null;}
                public Double maxValue() {return null;}
            });
            emitProb.put("right",new Function<R2,Double>(){
                public Double getValue(R2 pt) throws FunctionValueException {
                    double x=pt.x;
                    return (x>0)?0.8:(x>-.5)?0.8+x*8/5.0:0.0;
                }
                public Vector<Double> getValue(Vector<R2> x) throws FunctionValueException {return null;}
                public Double minValue() {return null;}
                public Double maxValue() {return null;}
            });
        }
        System.out.println(hiddenStates.toString());
        System.out.println(observations.toString());
        System.out.println(startProb.toString());
        System.out.println(transProb.toString());
        System.out.println(emitProb.toString());
        CurrentState result = Markov.forwardViterbi(observations, hiddenStates, startProb, transProb, emitProb);
        assertEquals("[left, right, right, left, left, left, left, left, right, right]", result.toString());
    }

}
