package mas;

/**
 * <p>Define for any class which can ascertain the value of a simulation's outcome.</p>
 * @author ae3263
 */
public interface Values<S extends Simulation> {
    /** Returns perceived value of the simulation. */
    public float getValue(S sim);
}
