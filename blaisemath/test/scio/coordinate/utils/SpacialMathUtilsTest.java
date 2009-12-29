/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package scio.coordinate.utils;

import scio.coordinate.*;
import scio.coordinate.utils.SpacialMathUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ae3263
 */
public class SpacialMathUtilsTest {

    public SpacialMathUtilsTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Test
    public void testTripleProduct() {
        System.out.println("tripleProduct");
        Point3D p1 = new Point3D(2,1,4);
        Point3D p2 = new Point3D(5,-1,-3);
        Point3D p3 = new Point3D(2,6,5.5);
        double expResult = 119.5;
        double result = SpacialMathUtils.tripleProduct(p1, p2, p3);
        assertEquals(expResult, result, 1e-12);
    }

}