/**
 * QuadFit.java
 * Created on Dec 10, 2008
 */

package test;

import java.util.Vector;
import mas.*;
import mas.evol.*;
import java.text.NumberFormat;

/**
 * <p>This test class generates quadratic polynomial fits to a specified function using
 * an evolutionary algorithm where the "DNA" is the values of a,b,c in a*x^2+b*x+c. Fitness
 * is determined by the chi^2 value.</p>
 * @author Elisha Peterson
 */
public class QuadFit {
        static int NSTEPS = 1000;
        static int POOLSIZE = 400;
        static float ERROR = 0.1f;
        static float[] TEST_VALUES = {-2.0f, -0.5f, 0.0f, 0.5f, 2.0f};
   
    public static void main(String[] args) {
        System.out.println("actual function: -x^3+2x^2-5x+2");
        GenePool gp = new QuadPool(POOLSIZE);
        new NSimulation(new EvolStep(), NSTEPS, gp).run();
        gp.printLog(System.out);
        gp.printAgentList(System.out);
    }
    
    
    static Function testFunction = new Function(){
        public double getValue(double x) { return -x*x*x+2*x*x-5*x+2; }
    };
    
    
    static class QuadDNA extends DNA {
        public double a=0.0, b=0.0, c=0.0, d=0.0;
        public Function function = new Function(){ public double getValue(double x) { return a*x*x*x+b*x*x+c*x+d; } };
        
        public QuadDNA() { this(0,0,0,0); }
        public QuadDNA(double a,double b,double c,double d) { this.a=a; this.b=b; this.c=c; this.d=d; }
        
        double random() { return Math.random()*20-10; }
        
        @Override
        public DNA getRandom() { return new QuadDNA(random(),random(),random(),random()); }
        @Override
        public DNA mutation(float error) { 
            double rand = Math.random();
            if (4*rand<1) {
                return new QuadDNA(a*(1+error*Math.random()/2)+error*Math.random()/2,b,c,d);
            } else if (4*rand<2) {
                return new QuadDNA(a,b*(1+error*Math.random()/2)+error*Math.random()/2,c,d);                
            } else if (4*rand<3) {
                return new QuadDNA(a,b,c*(1+error*Math.random()/2)+error*Math.random()/2,d); 
            } else {
                return new QuadDNA(a,b,c,d*(1+error*Math.random()/2)+error*Math.random()/2); 
            }
            //return new QuadDNA(a+error*Math.random(),b+error*Math.random(),c+error*Math.random()); 
        }
        @Override
        public DNA cross(Agent agent2, float error) {
            QuadDNA d2 = (QuadDNA) agent2.getStateVars();            
            return new QuadDNA((a+d2.a)/2,(b+d2.b)/2,(c+d2.c)/2,(d+d2.d)/2).mutation(0);
        }
        
        //public float getProduct(){return (float) (a * b * c * d); }
        
        final NumberFormat nf = NumberFormat.getInstance();
        
        public String toString() { return nf.format(a)+"x^3+"+nf.format(b)+"x^2+"+nf.format(c)+"x+"+nf.format(d); }
    }    
    
    final static QuadDNA BASE = new QuadDNA();
    static Agent getRandomQuad() { return new Agent(BASE.getRandom()); } 
    
    static class QuadPool extends GenePool {
        public QuadPool(int size) {
            agents = new Vector<Agent>();
            for (int i = 0; i < size; i++) { agents.add(getRandomQuad()); }
            controlVars = new ParameterSpace();
            stateVars = new ParameterSpace();
        }

        @Override
        public float getFitness(Agent agent, Simulation sim) {
            return Math.max(0.0f,
                    //10-Math.abs(5-((QuadDNA)agent.getStateVars()).getProduct()));
                    norm(testFunction, ((QuadDNA)agent.getStateVars()).function, TEST_VALUES));
        }

        @Override
        public float getError() { return ERROR; }        
        
        @Override
        public void evolve() {
            super.evolve();
            //super.evolve(2*agents.size()/3,agents.size()/6,agents.size()-2*agents.size()/3-agents.size()/6);
        }
    }
    
    
    
    abstract static class Function {
        abstract public double getValue(double x);
    }
    
    static float deviation(Function f1, Function f2, float x) {
        return (float) Math.abs(f1.getValue(x)-f2.getValue(x));
    }
    static float[] deviation(Function f1, Function f2, float[] xs) {
        float[] result = new float[xs.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = deviation(f1, f2, xs[i]);            
        }
        return result;
    }
    
    /** Computes coefficient of determination based on data samples.
     * @param f1 the model function
     * @param f2 the "observed" function
     * @param samples sample points to use
     * @return coefficient of determination
     */
    static float norm(Function f1, Function f2, float[] samples){
        float meanF2 = 0.0f;
        for (int i = 0; i < samples.length; i++) { meanF2 += f2.getValue(samples[i]); }
        meanF2 /= samples.length;        
        
        float[] devs = deviation(f1,f2,samples);
        
        float sumSqErr = 0.0f, sumSqRes = 0.0f;
        for(int i=0; i<samples.length;i++) {
            sumSqErr += (f2.getValue(samples[i])-meanF2)*(f2.getValue(samples[i])-meanF2);
            sumSqRes += devs[i]*devs[i];
        }
        
        return 1-sumSqRes/sumSqErr;
    }
    
}
