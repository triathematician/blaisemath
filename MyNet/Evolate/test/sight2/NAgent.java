package sight2;

import mas.Agent;
import mas.Parameter;
import mas.evol.Allele;

/**
 * Represents an agent on the interval [0,1].
 * @author elisha
 */
public class NAgent extends Agent {
    Allele.Flt spec;
    Parameter<Float> loc;

    public NAgent(float f){
        spec = new Allele.Flt(0.0f,1.0f,f);
        loc = new Parameter(0.0f,1.0f,0.5f);
        controlVars.set("p",spec);
        stateVars.set("loc",loc);
    }

    /** Determines whether target is in sensory range of specified agent. */
    public boolean isVisible(float target) {
        return Math.abs(target - loc.value) < spec.value;
    }

    /** Returns capture time for given agent. */
    public float timeTo(float target) {
        float speed = (1-spec.value)/200;
        return Math.abs(target-loc.value) / speed;
    }
}
