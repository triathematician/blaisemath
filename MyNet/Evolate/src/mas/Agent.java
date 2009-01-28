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
public class Agent {
    
    /** Agent control variables... do not change during the simulation. */
    protected ParameterSpace controlVars;    
    /** Agent state space... variables that change during the simulation. */
    protected ParameterSpace stateVars;
    
    
    /** Default constructor */
    public Agent() { this(new ParameterSpace(), new ParameterSpace()); }
    /** Initializes with state variables only */
    public Agent(ParameterSpace stateVars) { this(new ParameterSpace(),stateVars); }    
    /** Initializes with given control and state variable types */
    public Agent(ParameterSpace controlVars, ParameterSpace stateVars) {
        this.controlVars = controlVars;
        this.stateVars = stateVars;
    }
    
    /** Returns control variables. */
    public ParameterSpace getControlVars(){ return controlVars; }
    /** Returns state variables. */
    public ParameterSpace getStateVars(){ return stateVars; }
    
    
    /** Initializes the team's control variables, and all agent control variables. */
    protected void initializeControlVars() { controlVars.initialize(); }
    /** Initializes the team's state variables, and all agent state variables. */
    protected void initializeStateVars() { stateVars.initialize(); }
    
    /** Default to printing out state variables */
    @Override
    public String toString() { return controlVars.toString()+stateVars.toString(); }
}
