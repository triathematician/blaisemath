/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package scio.function;

import java.util.Vector;
import junit.framework.TestCase;

/**
 *
 * @author ae3263
 */
public class MeshRootTest extends TestCase {
    
    public MeshRootTest(String testName) {
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
     * Test of findRoots method, of class MeshRoot.
     */
    public void testFindRoots() throws Exception {
        System.out.println("findRoots");
        Function<Double, Double> function = new Function<Double, Double> () {
            public Double getValue(Double x) throws FunctionValueException {
                return Math.cos(x)*Math.cos(x)-.001;
            }
            public Vector<Double> getValue(Vector<Double> xx) throws FunctionValueException {
                Vector<Double> result = new Vector<Double> ();
                for (Double x: xx){
                    result.add(Math.cos(x)*Math.cos(x)-.001);
                }
                return result;
            }
        };
        Vector<Double> expResult = null;
        Vector<Double> result = MeshRoot.findRoots(function, -10, 10, 0.01, 1e-20, 10000);
        assertEquals(expResult, result.toString());
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

}
