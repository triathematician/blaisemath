package fit;

import mas.Agent;
import mas.Simulation;
import mas.evol.DNA;
import mas.evol.GenePool;

class FunctionPool extends GenePool {

    // Simulation Constants

    static int[] NRULE = {5,4,3,2,1};
    final static float ERROR = 0.1f;
    final static float POINTPROB = 0.5f;
    final static float[] COEFFS = {-1.0f, 2.0f, -5.0f, 2.0f};
    final static String FUNC = COEFFS[0]+"x^3+"+COEFFS[1]+"x^2"+COEFFS[2]+"x"+COEFFS[3];

    abstract static class Function { abstract public double getValue(double x); }
    static Function testFunction = new Function(){ public double getValue(double x) { return COEFFS[0]*x*x*x+COEFFS[1]*x*x+COEFFS[2]*x+COEFFS[3]; } };
    final static float[] TEST_VALUES = {-2.0f, -0.5f, 0.0f, 0.5f, 2.0f};

    /** Constructs pool of functions of default size. */
    public FunctionPool() {
        super(NRULE, POINTPROB, ERROR,
            new DNA(){
                @Override public void initialize(){ set("a",0.0); set("b",0.0); set("c",0.0); set("d",0.0); }
            });
    }

    /** Assigns fitness score */
    @Override public float getFitness(final Agent a, Simulation sim) {
        return Math.max(0.0f, norm(testFunction,
            new Function() {
                public double getValue(double x) {
                    return (Double)a.valueOf("a")*x*x*x + (Double)a.valueOf("b")*x*x + (Double)a.valueOf("c")*x + (Double)a.valueOf("d");
                }},
            TEST_VALUES));
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
