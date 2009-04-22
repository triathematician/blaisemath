/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package scio.coordinate;

import junit.framework.TestCase;

/**
 *
 * @author ae3263
 */
public class R2Test extends TestCase {
    
    public R2Test(String testName) {
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
     * Test of translate method, of class R2.
     */
    public void testTranslateBy_double_double() {
        System.out.println("translate");
        double x = 0.0;
        double y = 0.0;
        R2 instance = new R2();
        instance.translateBy(x, y);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of invertInCircleOfRadius method, of class R2.
     */
    public void testInvertInCircleOfRadius() {
        System.out.println("invertInCircleOfRadius");
        double r = 0.0;
        R2 instance = new R2();
        instance.invertInCircleOfRadius(r);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of dot method, of class R2.
     */
    public void testDot() {
        System.out.println("dot");
        R2 b = null;
        R2 instance = new R2();
        double expResult = 0.0;
        double result = instance.dot(b);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of magnitude method, of class R2.
     */
    public void testMagnitude() {
        System.out.println("magnitude");
        R2 instance = new R2();
        double expResult = 0.0;
        double result = instance.magnitude();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of magnitudeSq method, of class R2.
     */
    public void testMagnitudeSq() {
        System.out.println("magnitudeSq");
        R2 instance = new R2();
        double expResult = 0.0;
        double result = instance.magnitudeSq();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of angle method, of class R2.
     */
    public void testAngle_0args() {
        System.out.println("angle");
        R2 instance = new R2();
        double expResult = 0.0;
        double result = instance.angle();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of plus method, of class R2.
     */
    public void testPlus_R2() {
        System.out.println("plus");
        R2 b = null;
        R2 instance = new R2();
        R2 expResult = null;
        R2 result = instance.plus(b);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of minus method, of class R2.
     */
    public void testMinus_R2() {
        System.out.println("minus");
        R2 b = null;
        R2 instance = new R2();
        R2 expResult = null;
        R2 result = instance.minus(b);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of times method, of class R2.
     */
    public void testTimes() {
        System.out.println("times");
        double m = 0.0;
        R2 instance = new R2();
        R2 expResult = null;
        R2 result = instance.times(m);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of toXY method, of class R2.
     */
    public void testToXY() {
        System.out.println("toXY");
        R2 instance = new R2();
        R2 expResult = null;
        R2 result = instance.toXY();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of toRTheta method, of class R2.
     */
    public void testToRTheta() {
        System.out.println("toRTheta");
        R2 instance = new R2();
        R2 expResult = null;
        R2 result = instance.toRTheta();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of scaledToLength method, of class R2.
     */
    public void testScaledToLength() {
        System.out.println("scaledToLength");
        double l = 0.0;
        R2 instance = new R2();
        R2 expResult = null;
        R2 result = instance.scaledToLength(l);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of normalized method, of class R2.
     */
    public void testNormalized() {
        System.out.println("normalized");
        R2 instance = new R2();
        R2 expResult = null;
        R2 result = instance.normalized();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of multipliedBy method, of class R2.
     */
    public void testMultipliedBy() {
        System.out.println("multipliedBy");
        double c = 0.0;
        R2 instance = new R2();
        R2 expResult = null;
        R2 result = instance.multipliedBy(c);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of closestOnLine method, of class R2.
     */
    public void testClosestOnLine() {
        System.out.println("closestOnLine");
        R2 point1 = null;
        R2 point2 = null;
        R2 instance = new R2();
        R2 expResult = null;
        R2 result = instance.closestOnLine(point1, point2);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of closestOnSegment method, of class R2.
     */
    public void testClosestOnSegment() {
        System.out.println("closestOnSegment");
        R2 point1 = null;
        R2 point2 = null;
        R2 instance = new R2();
        R2 expResult = null;
        R2 result = instance.closestOnSegment(point1, point2);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of closestOnRay method, of class R2.
     */
    public void testClosestOnRay() {
        System.out.println("closestOnRay");
        R2 point1 = null;
        R2 point2 = null;
        R2 instance = new R2();
        R2 expResult = null;
        R2 result = instance.closestOnRay(point1, point2);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of equals method, of class R2.
     */
    public void testEquals() {
        System.out.println("equals");
        Coordinate c2 = null;
        R2 instance = new R2();
        boolean expResult = false;
        boolean result = instance.equals(c2);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of toString method, of class R2.
     */
    public void testToString() {
        System.out.println("toString");
        R2 instance = new R2();
        String expResult = "";
        String result = instance.toString();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of copy method, of class R2.
     */
    public void testCopy() {
        System.out.println("copy");
        R2 instance = new R2();
        Coordinate expResult = null;
        Coordinate result = instance.copy();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getLength method, of class R2.
     */
    public void testGetLength() {
        System.out.println("getLength");
        R2 instance = new R2();
        int expResult = 0;
        int result = instance.getLength();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getElement method, of class R2.
     */
    public void testGetElement() {
        System.out.println("getElement");
        int position = 0;
        R2 instance = new R2();
        double expResult = 0.0;
        double result = instance.getElement(position);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setElement method, of class R2.
     */
    public void testSetElement() {
        System.out.println("setElement");
        int position = 0;
        double value = 0.0;
        R2 instance = new R2();
        instance.setElement(position, value);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addToElement method, of class R2.
     */
    public void testAddToElement() {
        System.out.println("addToElement");
        int position = 0;
        double value = 0.0;
        R2 instance = new R2();
        instance.addToElement(position, value);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of multiplyElement method, of class R2.
     */
    public void testMultiplyElement() {
        System.out.println("multiplyElement");
        int position = 0;
        double value = 0.0;
        R2 instance = new R2();
        instance.multiplyElement(position, value);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of dotProduct method, of class R2.
     */
    public void testDotProduct() {
        System.out.println("dotProduct");
        R2 p2 = new R2(1,2);
        R2 instance = new R2(-1,2);
        double expResult = 3.0;
        double result = instance.dotProduct(p2);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of distanceTo method, of class R2.
     */
    public void testDistanceTo() {
        System.out.println("distanceTo");
        R2 p2 = new R2(1,2);
        R2 instance = new R2(-2,6);
        double expResult = 5.0;
        double result = instance.distanceTo(p2);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of zero method, of class R2.
     */
    public void testZero() {
        System.out.println("zero");
        R2 instance = new R2();
        R2 expResult = null;
        R2 result = instance.zero();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of plus method, of class R2.
     */
    public void testPlus_VectorSpaceElement() {
        System.out.println("plus");
        R2 p2 = new R2(1,2);
        R2 instance = new R2(-1,2);
        R2 expResult = new R2(0,4);
        R2 result = instance.plus(p2);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of minus method, of class R2.
     */
    public void testMinus_VectorSpaceElement() {
        System.out.println("minus");
        R2 p2 = new R2(1,2);
        R2 instance = new R2(-1,2);
        R2 expResult = new R2(2,0);
        R2 result = instance.minus(p2);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of translateBy method, of class R2.
     */
    public void testTranslateBy() {
        System.out.println("translateBy");
        R2 p2 = new R2(1,2);
        R2 instance = new R2(-1,2);
        R2 expResult = new R2(0,4);
        R2 result = instance.translateBy(p2);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of multiplyBy method, of class R2.
     */
    public void testMultiplyBy() {
        System.out.println("multiplyBy");
        double d = 0.0;
        R2 instance = new R2();
        VectorSpaceElement expResult = null;
        VectorSpaceElement result = instance.multiplyBy(d);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of slope method, of class R2.
     */
    public void testSlope() {
        System.out.println("slope");
        R2 p1 = null;
        R2 p2 = null;
        Double expResult = null;
        Double result = R2.slope(p1, p2);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of angle method, of class R2.
     */
    public void testAngle_double_double() {
        System.out.println("angle");
        double x = 0.0;
        double y = 0.0;
        double expResult = 0.0;
        double result = R2.angle(x, y);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of threePointCircleCenter method, of class R2.
     */
    public void testThreePointCircleCenter() {
        System.out.println("threePointCircleCenter");
        R2 result = R2.threePointCircleCenter(new R2(Math.sqrt(3)/2,.5), new R2(0,-1), new R2(1,0));
        assertEquals("(0, -0)", result.toString());
        result = R2.threePointCircleCenter(new R2(.3,.6), new R2(.2, -.5), new R2(1,0));
        assertEquals("(0.422, 0.034)", result.toString());
        // horizontal line
        result = R2.threePointCircleCenter(new R2(0,1), new R2(1,1), new R2(2,1));
        assertEquals(R2.INFINITY, result);
        // vertical line
        result = R2.threePointCircleCenter(new R2(1,1), new R2(1,-1), new R2(1,3));
        assertEquals(R2.INFINITY, result);
        result = R2.threePointCircleCenter(new R2(1,1), new R2(1,-1), new R2(-1,-1));
        assertEquals("(0, 0)", result.toString());
    }

}
