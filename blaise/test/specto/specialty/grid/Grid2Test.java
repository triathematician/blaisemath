/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package specto.specialty.grid;

import java.awt.Point;
import java.awt.geom.Point2D.Double;
import java.util.Vector;
import javax.swing.JMenuItem;
import javax.swing.event.ChangeEvent;
import junit.framework.TestCase;
import scio.coordinate.I2;

/**
 *
 * @author ae3263
 */
public class Grid2Test extends TestCase {
    
    public Grid2Test(String testName) {
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
     * Test of toWindow method, of class Grid2.
     */
    public void testToWindow() {
        System.out.println("toWindow");
        I2 cp = new I2(3,4);
        Grid2 instance = new Grid2(new Grid2Panel());
        Double result = instance.toWindow(cp);
        assertEquals("Point2D.Double[3.0, 4.0]", result.toString());
    }

    /**
     * Test of toGeometry method, of class Grid2.
     */
    public void testToGeometry() {
        System.out.println("toGeometry");
        Point wp = null;
        Grid2 instance = new Grid2();
        I2 expResult = null;
        I2 result = instance.toGeometry(wp);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setBounds method, of class Grid2.
     */
    public void testSetBounds() {
        System.out.println("setBounds");
        I2 minPoint = null;
        I2 maxPoint = null;
        Grid2 instance = new Grid2();
        instance.setBounds(minPoint, maxPoint);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of computeTransformation method, of class Grid2.
     */
    public void testComputeTransformation() {
        System.out.println("computeTransformation");
        Grid2 instance = new Grid2();
        double expResult = 0.0;
        double result = instance.computeTransformation();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of stateChanged method, of class Grid2.
     */
    public void testStateChanged() {
        System.out.println("stateChanged");
        ChangeEvent e = null;
        Grid2 instance = new Grid2();
        instance.stateChanged(e);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getMenuItems method, of class Grid2.
     */
    public void testGetMenuItems() {
        System.out.println("getMenuItems");
        Grid2 instance = new Grid2();
        Vector<JMenuItem> expResult = null;
        Vector<JMenuItem> result = instance.getMenuItems();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

}
