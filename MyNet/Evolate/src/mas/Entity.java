package mas;

/**
 * <p>
 * A generic class that contains state and control variables for a
 * multi-agent simulation
 * </p>
 * @author elisha
 */
public abstract class Entity {
    protected ParameterSpace controlVars;

    /** Default constructor */
    public Entity() { this(new ParameterSpace()); }
    /** Initializes with given control and state variable types */
    public Entity(ParameterSpace controlVars) { this.controlVars = controlVars; }

    public ParameterSpace getControlVars() { return controlVars; }

    public Number valueOf(String key){ return controlVars.valueOf(key); }

    /** Initializes all control variables. */
    protected void initControlVars() { controlVars.initialize(); }

    /** Default to printing out state variables */
    @Override
    public String toString() { return controlVars.toString(); }
}
