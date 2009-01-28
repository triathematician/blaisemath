/**
 * DNA.java
 * Created on Dec 9, 2008
 */

package mas.evol;

/**
 * <p>Extension of parameter space with variables that can be recombined.</p>
 * @author Elisha Peterson
 */
public class DNA extends mas.ParameterSpace {
    /** Stores probability of point mutation. */
    public double pointMutationProb = 0.01;
    
    /** Returns a completely random agent. */
    public DNA getRandom() { return null; }
    /** Returns exact copy of this DNA */
    public DNA copy() { return null; }
    /** Returns agent with this DNA and a certain amount of error introduced. */
    public DNA mutation(float error) {
        DNA result = new DNA();
        
        return result;
    }
    /** Returns DNA cross of two different agents. */
    public DNA cross(mas.Agent agent2, float error) {
        return null;
    }
    
}
