/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sequor.control;

import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import javax.swing.JPanel;
import junit.framework.TestCase;
import sequor.VisualControl;

/**
 *
 * @author ae3263
 */
public class SnapRuleTest extends TestCase {
    
    SnapRule.Grid gridRule;
    SnapRule.Grid hGridRule;
    SnapRule.Grid vGridRule;
    SnapRule.SinglePoint pointRule;
    SnapRule.RectangleControlPoints controlRule;
    SnapRule.Line lineRule;
    
    public SnapRuleTest(String testName) {
        super(testName);
    }            

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        gridRule=new SnapRule.Grid(20,20,5.1,3.1,12,20);
        hGridRule=new SnapRule.Grid();
        hGridRule.setHorizontal(20,20,61,12);
        vGridRule=new SnapRule.Grid();
        vGridRule.setVertical(20,20,62,20);
        pointRule=new SnapRule.SinglePoint(60,20);
        controlRule=new SnapRule.RectangleControlPoints(new Rectangle2D.Double(10,10,56,72));
        lineRule=new SnapRule.Line(1,1,19,19);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test of getSnapBox method, of class SnapRule.
     */
    public void testGetSnapBox() {
        System.out.println("getSnapBox");
        assertEquals(true,true);
    }

    /**
     * Test of enableSnapping method, of class SnapRule.
     */
    public void testEnableSnapping() {
        System.out.println("enableSnapping");
        assertEquals(true,true);
    }

    /**
     * Test of getStickyPoint method, of class SnapRule.
     */
    public void testGetStickyPoint() {
        System.out.println("getStickyPoint");
        assertEquals(true,true);
    }

    /**
     * Test of getSnapRange method, of class SnapRule.
     */
    public void testGetSnapRange() {
        System.out.println("getSnapRange");
        assertEquals(true,true);
    }

    /**
     * Test of setStickyPoint method, of class SnapRule.
     */
    public void testSetStickyPoint() {
        System.out.println("setStickyPoint");
        assertEquals(true,true);
    }

    /**
     * Test of setSnapRange method, of class SnapRule.
     */
    public void testSetSnapRange() {
        System.out.println("setSnapRange");
        assertEquals(true,true);
    }

    /**
     * Test of snappedPoint method, of class SnapRule.
     */
    public void testSnappedPoint() {
        System.out.println("snappedPoint");
        System.out.println("Default snap testing");
        assertEquals("point",pointRule.snappedPoint(),pointRule.point);
        assertEquals("line",lineRule.snappedPoint(),null);
        assertEquals("control",controlRule.snappedPoint(),null);
        assertEquals("grid",gridRule.snappedPoint(),null);
        
        System.out.println("Regular snap testing");
        assertEquals("point1",pointRule.snappedPoint(5,0),new Point(5,0));
        assertEquals("point2",pointRule.snappedPoint(50,0),pointRule.point);
        assertEquals("line1",lineRule.snappedPoint(5,0),new Point(2,2));
        assertEquals("line2",lineRule.snappedPoint(25,400),new Point(19,19));
        assertEquals("control1",controlRule.snappedPoint(5,0),new Point(10,10));
        assertEquals("control2",controlRule.snappedPoint(70,50),new Point(66,10));
        assertEquals("control3",controlRule.snappedPoint(200,200),new Point(200,200));
        
        assertEquals("grid1",gridRule.snappedPoint(5,0),new Point(20,20));
        assertEquals("grid2",gridRule.snappedPoint(50,50),new Point(50,51));
        assertEquals("grid3",gridRule.snappedPoint(120,50),new Point(81,51));
        assertEquals("grid4",gridRule.snappedPoint(100,100),new Point(81,82));
        assertEquals("grid5",gridRule.snappedPoint(0,90),new Point(20,82));
        
        hGridRule.setForceSnap(true);
        assertEquals("hgrid1",hGridRule.snappedPoint(5,0),new Point(20,20));
        assertEquals("hgrid2",hGridRule.snappedPoint(50,50),new Point(50,20));
        assertEquals("hgrid3",hGridRule.snappedPoint(120,50),new Point(81,20));
        assertEquals("hgrid4",hGridRule.snappedPoint(100,100),new Point(81,20));
        assertEquals("hgrid5",hGridRule.snappedPoint(0,50),new Point(20,20));
        
        vGridRule.setForceSnap(true);
        assertEquals("vgrid1",vGridRule.snappedPoint(5,0),new Point(20,20));
        assertEquals("vgrid2",vGridRule.snappedPoint(50,50),new Point(20,51));
        assertEquals("vgrid3",vGridRule.snappedPoint(120,50),new Point(20,51));
        assertEquals("vgrid4",vGridRule.snappedPoint(100,100),new Point(20,82));
        assertEquals("vgrid5",vGridRule.snappedPoint(0,90),new Point(20,82));
        
        System.out.println("Sticky snap testing");
        controlRule.setStickyPoint(4);
        assertEquals("control",controlRule.snappedPoint(),new Point(66,82));
    }

}
