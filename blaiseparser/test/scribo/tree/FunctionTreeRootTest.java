/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package scribo.tree;

import java.lang.Double;
import java.util.TreeMap;
import java.util.Vector;
import junit.framework.TestCase;
import scio.coordinate.R2;
import scio.coordinate.R3;
import scio.function.Function;
import scio.function.Function;
import scio.function.FunctionValueException;
import scribo.parser.FunctionSyntaxException;
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
     * Test of setParameters method, of class FunctionTreeRoot.
     */
    public void testParameters() {
        System.out.print("Parameter methods...");
        TreeMap<String, Double> values = new TreeMap<String,Double>();
        values.put("t",5.0);
        test1.setParameters(values);
        test2.setParameters(values);
        test3.setParameters(values);
        test4.setParameters(values);

        assertEquals("[x]",test1.getVariables().toString());
        assertEquals(1, test1.getNumParameters());
        assertEquals(1, test1.getNumVariables());

        assertEquals("[x, y]",test2.getVariables().toString());
        assertEquals(1, test2.getNumParameters());
        assertEquals(2, test2.getNumVariables());

        assertEquals("[x, y, z]",test3.getVariables().toString());
        assertEquals(1, test3.getNumParameters());
        assertEquals(3, test3.getNumVariables());

        assertEquals("[y]",test4.getVariables().toString());
        assertEquals("{t=5.0}",test4.getParameters().toString());
        assertEquals(1, test4.getNumParameters());
        assertEquals(1, test4.getNumVariables());
        System.out.println("passed!");
    }

    /**
     * Test of derivativeTree method, of class FunctionTreeRoot.
     */
    public void testDerivativeTree() {
        System.out.print("derivativeTree...");
        assertEquals("-sin(x)",((FunctionTreeRoot)test1.derivativeTree("x").fullSimplified()).argumentString());
        assertEquals("-sin(x*y)*x",((FunctionTreeRoot)test2.derivativeTree("y").fullSimplified()).argumentString());
        assertEquals("0",((FunctionTreeRoot)test3.derivativeTree("t").fullSimplified()).argumentString());
        assertEquals("1",((FunctionTreeRoot)test4.derivativeTree("t").fullSimplified()).argumentString());
        System.out.println("passed!");
    }

    /**
     * Test of simplified method, of class FunctionTreeRoot.
     */
    public void testSimplified() {
        System.out.println("simplified");
        System.out.print("...regular...");
        try {
            assertEquals("x", ((FunctionTreeRoot) new FunctionTreeRoot("-(-x)").simplified()).argumentString());
        } catch (FunctionSyntaxException ex) {
            assertEquals(true,false);
        }
        System.out.println("passed!");

        System.out.print("...full simplification...");
        try {
            assertEquals("x", ((FunctionTreeRoot) new FunctionTreeRoot("(x^.5)^2").fullSimplified()).argumentString());
            assertEquals("1", ((FunctionTreeRoot) new FunctionTreeRoot("x^2 x^-1 / x").fullSimplified()).argumentString());
            //assertEquals("3x^2", ((FunctionTreeRoot) new FunctionTreeRoot("x^2+2x^2").fullSimplified()).argumentString());
        } catch (FunctionSyntaxException ex) {
            assertEquals(true,false);
        }
        System.out.println("passed!");
    }

    /**
     * Test of getValue method, of class FunctionTreeRoot.
     */
    public void testGetValue() throws Exception {
        System.out.println("getValue");
        System.out.print("...(double)...");
        assertEquals(-0.9899924966004454, test1.getValue(3.0));
        try{ // ensure failure to get value in these cases
            assertEquals(1,test2.getValue(3.0));
            assertEquals(1,test3.getValue(3.0));
            assertEquals(1,test4.getValue(3.0));
        }catch(FunctionValueException e){
            assertEquals(true,true);
        }
        TreeMap<String, Double> values = new TreeMap<String,Double>();
        values.put("t",-5.0);
        test4.setParameters(values);
        assertEquals(-5.9899924966004454,test4.getValue(1.0));
        System.out.println("passed!");

        System.out.print("...(String,double)...");
        assertEquals(-0.9899924966004454,test1.getValue("x",3.0));
        System.out.println("passed!");
        
        System.out.print("...(String,Vector)...");
        Vector<Double> inputs=new Vector<Double>();
        inputs.add(0.0);
        inputs.add(3.0);
        assertEquals("[1.0, -0.9899924966004454]",test1.getValue("x",inputs).toString());
        System.out.println("passed!");

        System.out.print("...(TreeMap)...");
        values = new TreeMap<String,Double>();
        values.put("x",1.5);
        values.put("y",2.0);
        assertEquals(-0.9899924966004454,test2.getValue(values));
        System.out.println("passed!");
    }

    /**
     * Test of getFunction method, of class FunctionTreeRoot.
     */
    public void testGetFunction() {
        System.out.println("getFunction");

        TreeMap<String, Double> values = new TreeMap<String,Double>();
        values.put("t",5.0);
        test1.setParameters(values);
        test2.setParameters(values);
        test3.setParameters(values);
        test4.setParameters(values);

        Function fun1 = test1.getFunction();
        Function fun2 = test2.getFunction();
        Function fun3 = test3.getFunction();
        Function fun4 = test4.getFunction();

        System.out.print("...type checking...");
        assertEquals(FunctionTreeRoot.OneInput.class, fun1.getClass());
        assertEquals(FunctionTreeRoot.TwoInput.class, fun2.getClass());
        assertEquals(FunctionTreeRoot.NInput.class, fun3.getClass());
        assertEquals(FunctionTreeRoot.OneInput.class, fun4.getClass());
        System.out.println("passed!");

        System.out.print("...function evaluations with single input...");

        Vector<Double> x = new Vector<Double>(); x.add(0.0); x.add(2.0);
        Vector<R2> xy = new Vector<R2>(); xy.add(new R2(1.5, 2.0)); xy.add(new R2(2.0, 2.0));
        Vector<R3> xyz = new Vector<R3>(); xyz.add(new R3(1.5, 1.0, 2.0)); xyz.add(new R3(0.0, 0.0, 1.0));

        try { // test single inputs
            assertEquals(1.0, fun1.getValue(x.get(0)));
            assertEquals(-0.9899924966004454, fun2.getValue(xy.get(0)));
            assertEquals(-0.9899924966004454, fun3.getValue(xyz.get(0)));
            assertEquals(6.0, fun4.getValue(x.get(0)));
        } catch (FunctionValueException ex) {
            assertEquals(true,false);
        }
        System.out.println("passed!");

        System.out.print("...function evaluations with multiple inputs...");

        try { // test multiple inputs
            assertEquals("[1.0, -0.4161468365471424]", fun1.getValue(x).toString());
            assertEquals("[-0.9899924966004454, -0.6536436208636119]", fun2.getValue(xy).toString());
            assertEquals("[-0.9899924966004454, 1.0]", fun3.getValue(xyz).toString());
            assertEquals("[6.0, 5.960170286650366]", fun4.getValue(x).toString());
        } catch (FunctionValueException ex) {
            assertEquals(true,false);
        }
        System.out.println("passed!");
    }

}
