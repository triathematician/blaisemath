/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package scribo.tree;

import java.util.TreeMap;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.TestCase;
import scio.coordinate.Euclidean;
import scio.coordinate.R1;
import scio.function.Function;
import scio.function.FunctionValueException;
import scribo.parser.Parser;

/**
 *
 * @author ae3263
 */
public class FunctionTreeRootTest extends TestCase {
    
    FunctionTreeRoot test1;
    FunctionTreeRoot test2;
    FunctionTreeRoot test3;
    FunctionTreeRoot test4;
    
    public FunctionTreeRootTest(String testName) {
        super(testName);
    }            

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        test1=new FunctionTreeRoot(Parser.parseExpression("cos(x)"));
        test2=new FunctionTreeRoot(Parser.parseExpression("cos(x*y)"));
        test3=new FunctionTreeRoot(Parser.parseExpression("cos(x*y*z)"));
        test4=new FunctionTreeRoot(Parser.parseExpression("cos(3*y)+t"));
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test of setUnknowns method, of class FunctionTreeRoot.
     */
    public void testSetUnknowns() {
        System.out.println("setUnknowns");
        TreeMap<String, Double> values = new TreeMap<String,Double>();
        values.put("t",5.0);
        test1.setUnknowns(values);
        assertEquals("[x]",test1.variables.toString());
        test2.setUnknowns(values);
        assertEquals("[x, y]",test2.variables.toString());
        test3.setUnknowns(values);
        assertEquals("[x, y, z]",test3.variables.toString());
        test4.setUnknowns(values);
        assertEquals("[y]",test4.variables.toString());
    }

    /**
     * Test of toString method, of class FunctionTreeRoot.
     */
    public void testToString() {
        System.out.println("toString");
        assertEquals(true,true);
    }

    /**
     * Test of derivativeTree method, of class FunctionTreeRoot.
     */
    public void testDerivativeTree() {
        System.out.println("derivativeTree");
        assertEquals("-sin(x)",test1.derivativeTree("x").toString());
        assertEquals("-sin(x*y)*(x)",test2.derivativeTree("y").simplified().toString());
        assertEquals("",test3.derivativeTree("t").simplified().toString());
        assertEquals("1",test4.derivativeTree("t").simplified().toString());
    }

    /**
     * Test of isValidSubNode method, of class FunctionTreeRoot.
     */
    public void testIsValidSubNode() {
        System.out.println("isValidSubNode");
        assertEquals(true,true);
    }

    /**
     * Test of simplified method, of class FunctionTreeRoot.
     */
    public void testSimplified() {
        System.out.println("simplified");
        System.out.println("...no test here yet...");
        assertEquals(true,true);
    }

    /**
     * Test of initFunctionType method, of class FunctionTreeRoot.
     */
    public void testInitFunctionType() {
        System.out.println("initFunctionType");
        System.out.println("...no test here yet...");
        assertEquals(true,true);
    }

    /**
     * Test of getValue method, of class FunctionTreeRoot.
     */
    public void testGetValue() throws Exception {
        System.out.println("getValue(double)");
        assertEquals(-0.9899924966004454,test1.getValue(3.0));
        try{
            assertEquals(1,test2.getValue(3.0));
            assertEquals(1,test3.getValue(3.0));
            assertEquals(1,test4.getValue(3.0));
        }catch(FunctionValueException e){
            assertEquals(true,true);
        }
        TreeMap<String, Double> values = new TreeMap<String,Double>();
        values.put("t",-5.0);
        test4.setUnknowns(values);
        assertEquals(-5.9899924966004454,test4.getValue(1.0));
        
        System.out.println("getValue(String,double)");
        assertEquals(-0.9899924966004454,test1.getValue("x",3.0));   
        
        System.out.println("getValue(TreeMap)");
        values = new TreeMap<String,Double>();
        values.put("x",1.5);
        values.put("y",2.0);
        assertEquals(-0.9899924966004454,test2.getValue(values));   
        
        System.out.println("getValue(String,Vector)");
        Vector<Double> inputs=new Vector<Double>();
        inputs.add(0.0);
        inputs.add(3.0);
        assertEquals("[1.0, -0.9899924966004454]",test1.getValue("x",inputs).toString()); 
    }

    /**
     * Test of getDoubleFunction method, of class FunctionTreeRoot.
     */
    public void testGetDoubleFunction() {
        System.out.println("getDoubleFunction");

        System.out.println("...variable size...");
        assertEquals(1, test1.variables.size());
        assertEquals(2, test2.variables.size());
        assertEquals(3, test3.variables.size());
        assertEquals(2, test4.variables.size());

        System.out.println("...functions...");
        Function fun1 = test1.getDoubleFunction();
        Function fun2 = test2.getDoubleFunction();
        Function fun3 = test3.getDoubleFunction();
        Function fun4 = test4.getDoubleFunction();

        R1 x = new R1(0.0);
        Double[] xxyy = {1.5, 2.0};
        Euclidean xy = new Euclidean(xxyy);
        Double[] xxyyzz = {1.5, 1.0, 2.0};
        Euclidean xyz = new Euclidean(xxyyzz);

        try {
            assertEquals(1.0, fun1.getValue(x));
            assertEquals(-0.9899924966004454, fun2.getValue(xy));
            assertEquals(-0.9899924966004454, fun3.getValue(xyz));
            TreeMap<String, Double> values = new TreeMap<String,Double>();
            values.put("t",-5.0);
            test4.setUnknowns(values);
            assertEquals(-5.9899924966004454, fun4.getValue(new R1(1.0)));
        } catch (FunctionValueException ex) {
            assertEquals(true,false);
        }
    }

}
