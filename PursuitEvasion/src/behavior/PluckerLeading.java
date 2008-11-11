/*
 * Leading.java 
 * Created on Aug 28, 2007, 11:21:25 AM
 */
package behavior;

import simulation.Agent;
import scio.coordinate.R2;
import scio.coordinate.V2;

/**
 * <p>
 * This class defines leading as heading to the point ahead of the evader defined such that
 * the time it takes the evader to get there is the same as the time it would take the pursuer
 * to reach the evader's initial location. This is as defined in Andrew's thesis.
 * </p>
 * @author Elisha Peterson, Andrew Plucker
 */
public class PluckerLeading extends behavior.Behavior {

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
        if (target.v.magnitude() == 0 || self.getTopSpeed() == 0) {
            if (self.loc.distance(target) < .5*self.getSensorRange()) {
                return R2.ORIGIN;
            }
            return target.minus(self.loc).normalized();
        } else {           
            R2 diff = self.loc.minus(target);
            double mui = target.v.magnitude() / self.getTopSpeed();
            // this is the distance from evader's current point to the hypothetical capture point
            double dE = diff.magnitude()*mui;
            R2 result = target.plus(target.v.scaledToLength(self.getLeadFactor()*dE)).minus(self.loc).normalized();
            return result;
        }

    }
}
