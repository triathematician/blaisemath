/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package scio.algorithm;

import java.util.Vector;
import junit.framework.TestCase;
import scio.coordinate.R2;

/**
 *
 * @author ae3263
 */
public class PointsInPlaneTest extends TestCase {
    
    public PointsInPlaneTest(String testName) {
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
     * Test of convexHull method, of class PointsInPlane.
     */
    public void testConvexHull() {
        System.out.println("convexHull");
        Vector<R2> points = null;
        Vector<R2> expResult = null;
        Vector<R2> result = PointsInPlane.convexHull(points);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

}
