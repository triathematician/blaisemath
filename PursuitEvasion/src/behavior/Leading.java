/*
 * Leading.java 
 * Created on Aug 28, 2007, 11:21:25 AM
 */
package behavior;

import simulation.Agent;
import scio.coordinate.R2;
import scio.coordinate.V2;

/**
 * @author Elisha Peterson<br><br>
 * 
 * Moves towards a position in front of the target which depends upon the leadFactor.
 */
public class Leading extends behavior.Behavior {

    /**
     * Computes desired direction of travel
     * @param self      the agent exhibiting this myBehavior
     * @param target    the agent targeted by the myBehavior
     * @param t         the current time stamp
     * @return          the direction of travel corresponding to this myBehavior
     */
    public R2 direction(Agent self, V2 target, double t) {
        if (target == null) {
            return new R2();
        }
        if (target.v.magnitude() == 0) {
            if (self.loc.distance(target) < 15) {
                return R2.ORIGIN;
            }
            return target.minus(self.loc).normalized();
        } else {
            return (target.plus(target.v.multipliedBy(self.getLeadFactor() * self.loc.distance(target) / self.getTopSpeed()))).minus(self.loc).normalized();
        }

    }
}
