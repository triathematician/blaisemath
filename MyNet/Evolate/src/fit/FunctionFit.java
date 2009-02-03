/**
 * FunctionFit.java
 * Created on Dec 10, 2008
 */

package fit;

import mas.*;
import mas.evol.*;

/**
 * <p>This test class generates quadratic polynomial fits to a specified function using
 * an evolutionary algorithm where the "DNA" is the values of a,b,c in a*x^2+b*x+c. Fitness
 * is determined by the chi^2 value.</p>
 * @author Elisha Peterson
 */
public class FunctionFit {

    // Simulation Constants
    static int NSTEPS = 100;

    static int[] NRULE = {200,100,100,100,500};
    final static float POINTPROB = 0.5f;
    final static float ERROR = 0.1f;
    final static float[] COEFFS = {-1.0f, 2.0f, -5.0f, 2.0f};
    final static String FUNC = COEFFS[0]+"x^3+"+COEFFS[1]+"x^2"+COEFFS[2]+"x"+COEFFS[3];

    abstract static class Function { abstract public double getValue(double x); }
    static Function testFunction = new Function(){ public double getValue(double x) { return COEFFS[0]*x*x*x+COEFFS[1]*x*x+COEFFS[2]*x+COEFFS[3]; } };
    final static float[] TEST_VALUES = {-2.0f, -0.5f, 0.0f, 0.5f, 2.0f};


    /** The main method, which runs the simulation */
    public static void main(String[] args) {
        System.out.println("actual function: "+FUNC);
        GenePool gp = new GenePool(NRULE, POINTPROB, ERROR,
            new DNA(){
                @Override public void initialize(){ set("a",0.0); set("b",0.0); set("c",0.0); set("d",0.0); }
                //@Override public String toString(){ return valueOf("a")+"x^3"+valueOf("b")+"x^2"+valueOf("c")+"x"+valueOf("d"); }
            })
        {
            @Override
            public float getFitness(final Agent a, Simulation sim) {
                return Math.max(0.0f, norm(testFunction,
                    new Function() { public double getValue(double x) { return (Double)a.valueOf("a")*x*x*x + (Double)a.valueOf("b")*x*x + (Double)a.valueOf("c")*x + (Double)a.valueOf("d"); }},
                    TEST_VALUES));
            }
        };
        new EvolSim(NSTEPS, gp).run();
        gp.printLog(System.out);
        gp.printAgentList(System.out);
    }


    /** Computes coefficient of determination based on data samples. */
    static float norm(Function f1, Function f2, float[] samples){
        float meanF2 = 0.0f;
        float[] devs = new float[samples.length];
        for (int i = 0; i < samples.length; i++) {
            meanF2 += f2.getValue(samples[i]);
            devs[i] = (float) Math.abs(f1.getValue(samples[i])-f2.getValue(samples[i]));
        }
        meanF2 /= samples.length;

        float sumSqErr = 0.0f, sumSqRes = 0.0f;
        for(int i=0; i<samples.length;i++) {
            sumSqErr += (f2.getValue(samples[i])-meanF2)*(f2.getValue(samples[i])-meanF2);
            sumSqRes += devs[i]*devs[i];
        }

        return 1-sumSqRes/sumSqErr;
    }
}
