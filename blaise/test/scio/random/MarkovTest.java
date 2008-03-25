/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package scio.random;

import java.util.HashMap;
import junit.framework.TestCase;
import scio.coordinate.R2;
import scio.function.Function;
import scio.matrix.HashMatrix;
import scio.random.Markov.CurrentState;
import sequor.control.Gestures;

/**
 *
 * @author elisha
 */
public class MarkovTest extends TestCase {
    private HashMap<String, Double> startProb;
    private HashMatrix<String, Double> transProb;
    private HashMap<String, Function<R2, Double>> emitProb;
    
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
        
        R2[] observations = {
            new R2(5,0),
            new R2(10,0), new R2(20,0), new R2(30,10),
            new R2(-20,1),
            new R2(-1,30),
            new R2(0,0),
            new R2(15,0),
            new R2(10,-20),
            new R2(90,1)};              
        String[] hiddenStates=Gestures.moveStates;
        startProb=Gestures.getStartProb(0.2);
        transProb=Gestures.getTransProb(0.1,0.4,0.1,0.1);
        emitProb=Gestures.getEmitProb();
        
        System.out.println(hiddenStates.toString());
        System.out.println(observations.toString());
        System.out.println(startProb.toString());
        System.out.println(transProb.toString());
        System.out.println(emitProb.toString());
        
        CurrentState result = new Markov<String,R2>().forwardViterbi(observations,hiddenStates,startProb,transProb,emitProb);
        
        System.out.println(result.toString());
            
        assertEquals("[right, right, right, none, left, down, none, none, up, none]", result.toString());
        assertEquals("[right, left, down, up]", Gestures.clipOutput(result.vitPath).toString());
    }
}
