/**
 * Agent.java
 * Created on Dec 9, 2008
 */

package mas;

/**
 * <p>
 * Represents a general agent in a multi-agent system.
 * </p>
 * @author Elisha Peterson
 */
public class Agent extends Entity {
    /** Default constructor */
    public Agent() { super(new ParameterSpace(), new ParameterSpace()); }
    /** Initializes with state variables only */
    public Agent(ParameterSpace stateVars) { super(new ParameterSpace(),stateVars); }
    /** Initializes with given control and state variable types */
    public Agent(ParameterSpace controlVars, ParameterSpace stateVars) { super(controlVars,stateVars); }
}
