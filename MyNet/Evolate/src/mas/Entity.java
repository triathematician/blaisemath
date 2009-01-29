package mas;

/**
 * <p>
 * A generic class that contains state and control variables for a
 * multi-agent simulation
 * </p>
 * @author elisha
 */
public class Entity {
    protected ParameterSpace controlVars;
    protected ParameterSpace stateVars;

    /** Default constructor */
    public Entity() { this(new ParameterSpace(), new ParameterSpace()); }
    /** Initializes with state variables only */
    public Entity(ParameterSpace stateVars) { this(new ParameterSpace(),stateVars); }
    /** Initializes with given control and state variable types */
    public Entity(ParameterSpace controlVars, ParameterSpace stateVars) {
        this.controlVars = controlVars;
        this.stateVars = stateVars;
    }

    public ParameterSpace getControlVars() { return controlVars; }
    public ParameterSpace getStateVars() { return stateVars; }

    public Number valueOf(String key){
        Number n = stateVars.valueOf(key);
        return n==null ? controlVars.valueOf(key) : n;
    }

    /** Initializes all control variables. */
    protected void initControlVars() { controlVars.initialize(); }
    /** Initializes all state variables. Should be done before each run. */
    protected void initStateVars() { stateVars.initialize(); }

    /** Default to printing out state variables */
    @Override
    public String toString() { return stateVars.toString(); }
}
