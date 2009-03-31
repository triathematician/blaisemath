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
 * This class defines leading as predicting the nearest point of intercept and heading to that point.
 * This is problematic when the point is very far away, so it is capped at a certain distance from
 * the present location.
 * </p>
 * @author Elisha Peterson
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
            //if (self.loc.distance(target) < .5*self.getSensorRange()) {
            //    return R2.ORIGIN;
            //}
            return target.minus(self.loc).normalized();
        } else {           
            R2 diff = self.loc.minus(target);
            double mu = self.getTopSpeed() / target.v.magnitude();
            double costh = target.v.normalized().dot(diff);
            // this is the distance from evader's current point to the hypothetical capture point
            double dE =
                    Math.abs(mu-1) < .01 ? Math.abs(diff.magnitudeSq() / (2 * costh))
                        : (costh-Math.sqrt(costh*costh-(1-mu*mu)*diff.magnitudeSq()))/(1-mu*mu);
            // modify by capping dE to within 100 time units (temporary restriction)
            if (dE > self.getTopSpeed() * 100) { dE = self.getTopSpeed() * 100; }
            R2 result = target.plus(target.v.scaledToLength(self.getLeadFactor()*dE)).minus(self.loc).normalized();
            return result;
        }

    }
}
