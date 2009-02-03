/**
 * ParameterSpace.java
 * Created on Dec 9, 2008
 */

package mas;

import java.util.TreeMap;

/**
 * <p>Represents a collection of settings for a given simulation, agent, or team.</p>
 * @author Elisha Peterson
 */
public class ParameterSpace {
    
    /** Lookup table of parameter space (by strings). */
    protected TreeMap<String,Parameter> p;
    
    /** Constructor */
    public ParameterSpace() { p = new TreeMap<String,Parameter>(); initialize(); }

    /** Initializes the parameters to default values; or resets to zero. */
    public void initialize() {}
    
    /** Sets parameter. */
    public Parameter set(String key, Number val) {
        Parameter pm = Parameter.getInstance(val);
        p.put(key, pm);
        return pm;
    }
    /** Sets parameter. */
    public Parameter set(String key, Parameter pm) {
        p.put(key, pm);
        return pm;
    }
    /** Returns a parameter. */
    public Parameter get(String key) { return p.get(key); }
    /** Returns value of a parameter. */
    public Number valueOf(String key) { return p.get(key).value; }

    /** Returns number of parameters. */
    public int size() { return p.size(); }

    /** Returns copy */
    public ParameterSpace copy() {
        ParameterSpace ps2 = new ParameterSpace();
        for(String s:p.keySet()) { ps2.p.put(s,get(s).copy()); }
        return ps2;
    }

    /** Returns randomized parameters. */
    public ParameterSpace getRandom() {
        ParameterSpace ps2 = new ParameterSpace();
        for(String s:p.keySet()) { ps2.p.put(s,get(s).getRandom()); }
        return ps2;
    }
    /** Returns random agent. */
    public Agent getRandomAgent() { return new Agent(getRandom()); }
    
    /** Default print. */
    @Override public String toString() { return p.toString(); }
}
