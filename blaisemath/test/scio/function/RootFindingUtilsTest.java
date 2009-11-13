/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package scio.function;

import deprecated.RealFunction;
import java.util.List;
import java.util.Vector;
import junit.framework.TestCase;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.UnivariateRealSolverFactory;

/**
 *
 * @author ae3263
 */
public class RootFindingUtilsTest extends TestCase {
    
    public RootFindingUtilsTest(String testName) {
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
     * Test of findRoots method, of class RootFindingUtils.
     */
    public void testFindRoots() throws Exception {
        System.out.println("findRoots");
        RealFunction function = new RealFunction () {
            public List<Double> getValue(List<Double> xx) throws FunctionEvaluationException {
                Vector<Double> result = new Vector<Double> ();
                for (Double x: xx){
                    result.add(Math.cos(x)*Math.cos(x)-.001);
                }
                return result;
            }

            public double value(double x) throws FunctionEvaluationException {
                return Math.cos(x)*Math.cos(x)-.001;
            }

            public Double getValue(Double x) throws FunctionEvaluationException {
                return Math.cos(x)*Math.cos(x)-.001;
            }
        };

        List<Double> result = RootFindingUtils.findAllRoots(function,
                UnivariateRealSolverFactory.newInstance().newDefaultSolver(function),
                -10, 10, 0.01);

        assertEquals(
                "[-7.885609703928792, -7.8223535806577935, -4.7440170048199, -4.68076093087397, -1.6024243668387612, -1.5391684391593137, 1.5391684391593137, 1.6024243668387612, 4.68076093087397, 4.7440170048199, 7.8223535806577935, 7.885609703928792]",
                result.toString());
    }

}
