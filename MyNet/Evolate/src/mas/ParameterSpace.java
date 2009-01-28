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
    TreeMap<String,Parameter> p;
    
    /** Intializes */
    public ParameterSpace() { p = new TreeMap<String,Parameter>(); }
    
    /** Initializes the parameters to default values; or resets to zero. */
    public void initialize() {}
    
    /** Sets parameter. */
    public Parameter setParameter(String key, Number val) { return p.put(key, Parameter.getInstance(val)); }
    /** Returns a parameter. */
    public Parameter getParameter(String key) { return p.get(key); }
    /** Returns value of a parameter. */
    public Number getValue(String key) { return p.get(key).value; }
    
    /** Default print. */
    @Override public String toString() { return p.toString(); }
}
