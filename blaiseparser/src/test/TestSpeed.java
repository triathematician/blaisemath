package test;

import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import scio.function.Function;
import scio.function.FunctionValueException;
import scribo.parser.FunctionSyntaxException;
import scribo.tree.FunctionTreeRoot;

/**
 * The purpose of this is to test the speed of function evaluation.
 * @author ae3263
 */
public class TestSpeed {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        FunctionTreeRoot rt = null;
        String test1 = "x^2+2x+cos(x*t)+3t-2s*exp(s*x)";
        //String test1 = "x^2";
//        String test1 = "sum(x^n,n,1,10)";
        Object[][] parms = {{"t",1.0},{"s",4.0}};

        try { rt = (FunctionTreeRoot) new FunctionTreeRoot(test1).fullSimplified(); } catch (FunctionSyntaxException ex) { System.out.println("SYNTAX!"); }
        rt.setParameters(parms);

        Vector<Double> inputs = new Vector<Double>();
        for (int i = 0; i < 10000; i++) {
            inputs.add(Math.random());
        }

        try {

//        long t0 = System.nanoTime();
//
//        double t=1.0;
//        double s=4.0;
//        Vector<Double> result0 = new Vector<Double>(inputs.size());
//        double sum;
//        for (Double x : inputs) {
//            sum = 0;
//             //result0.add(x*x+2*x+Math.cos(x*t)+3*t-2*s*Math.exp(s*x));
//            //result0.add(x*x);
//            for (int i = 1; i <= 10; i++) {
//                sum += Math.pow(x, i);
//            }
//            result0.add(sum);
//        }

        long t1 = System.nanoTime();

        Vector<Double> result1 = rt.getValue(inputs);

        long t2 = System.nanoTime();
//
//        Vector<Double> result2 = new Vector<Double>(inputs.size());
//        for (Double x : inputs) {
//            result2.add(rt.getValue(x));
//        }
//
//        long t3 = System.nanoTime();
//
//        Function<Double,Double> fun1 = (Function<Double, Double>) rt.getFunction();
//        Vector<Double> result3 = fun1.getValue(inputs);
//
//        long t4 = System.nanoTime();
//
//        Function<Double,Double> fun2 = (Function<Double, Double>) rt.getFunction();
//        Vector<Double> result4 = new Vector<Double>(inputs.size());
//        for (Double x : inputs) {
//            result4.add(fun2.getValue(x));
//        }
//
//        long t5 = System.nanoTime();
//
//        System.out.println("direct code time:     " + (t1-t0));
        System.out.println("time vector input:    " + (t2-t1));
//        System.out.println("time each input:      " + (t3-t2));
//        System.out.println("time fn vector input: " + (t4-t3));
//        System.out.println("time fn each input:   " + (t5-t4));

        } catch (FunctionValueException ex) {
            Logger.getLogger(TestSpeed.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
