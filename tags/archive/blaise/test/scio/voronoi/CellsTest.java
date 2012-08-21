/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package scio.voronoi;

import java.util.Vector;
import junit.framework.TestCase;
import scio.coordinate.R2;
import scio.random.Random2D;

/**
 *
 * @author elisha
 */
public class CellsTest extends TestCase {
    
    public CellsTest(String testName) {
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
     * Test of compute method, of class VoronoiCells.
     */
    public void testCompute() {
        System.out.println("compute");
        Vector<R2> points = new Vector<R2> ();
        for (int i = 0; i < 10; i++) { points.add(Random2D.rectangle(10)); }
        VoronoiCells instance = new VoronoiCells(points);
        System.out.println(""+points.toString());
        System.out.println(""+instance.toString());
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

}
