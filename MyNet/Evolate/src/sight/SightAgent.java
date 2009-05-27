/**
 * SightAgent.java
 * Created Feb 2009
 */

package sight;

import mas.Agent;

/**
 * A team contains agents of the following type...
 * their specs must be updated whenever the team DNA evolves.
 */
public class SightAgent extends Agent {

    /** The specialization parameter, determining sight radius and speed. */
    float spec;
    /** The agent's position (changes for each round). */
    float pos;

    public SightAgent(float spec) {
        super();
        this.spec = spec;
    }

    /** Runs before each simulation... randomize position */
    @Override
    public void initControlVars() {
        pos = (float) Math.random();
    }

    public boolean sees(float target) {
        return Math.abs(pos - target) <= spec;
    }

    /** Returns time to capture, absed on this spec. */
    public float timeTo(float target) {
        if (spec > 0.999F) {
            return Float.MAX_VALUE;
        }
        float result = Math.abs(pos - target) / (1 - spec);
        return result;
    }

    public void setSpec(float spec) {
        this.spec = spec;
    }

    @Override
    public String toString() {
        return "" + spec;
    }
}
