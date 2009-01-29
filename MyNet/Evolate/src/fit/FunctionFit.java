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
    static int NSTEPS = 10000;

    /** The main method, which runs the simulation */
    public static void main(String[] args) {
        System.out.println("actual function: "+FunctionPool.FUNC);
        GenePool gp = new FunctionPool();
        new NSimulation(new EvolSim(), NSTEPS, gp).run();
        gp.printLog(System.out);
        gp.printAgentList(System.out);
    }    
}
