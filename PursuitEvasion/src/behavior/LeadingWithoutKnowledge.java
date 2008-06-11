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
public class LeadingWithoutKnowledge extends behavior.Behavior {
double counter = 0.0;
   

    /**
     * Computes desired direction of travel
     * @param self      the agent exhibiting this myBehavior
     * @param target    the agent targeted by the myBehavior
     * @param t         the current time stamp
     * @return          the direction of travel corresponding to this myBehavior
     */
    public R2 direction(Agent self, V2 target, double t) {
        double x = 0;
        double y = 0;
        //System.out.println("counter: "+counter);
        if (target == null) {
            if (counter >= 0 && counter <= (Math.PI * .5 * self.getSensorRange()) / (self.getTopSpeed() * .1)) {
                x = .5 * self.getSensorRange() * Math.sin((counter * self.getTopSpeed() * .1) / (.5 * self.getSensorRange()));

                y = .5 * self.getSensorRange() - .5 * self.getSensorRange() * Math.cos((counter * self.getTopSpeed() * .1) / (.5 * self.getSensorRange()));
                counter++;
                return new R2(x, y);
            } else if (counter > (Math.PI * .5 * self.getSensorRange()) / (self.getTopSpeed() * .1) && counter <= (Math.PI * self.getSensorRange()) / (self.getTopSpeed() * .1)) {
                x = -1 * self.getSensorRange() * Math.sin((counter * self.getTopSpeed() * .1) / (.5 * self.getSensorRange()) - Math.PI);

                y = self.getSensorRange() * Math.cos((counter * self.getTopSpeed() * .1) / (.5 * self.getSensorRange()) - Math.PI);
              
                return new R2(x, y);
            } 
            else {
                return R2.ORIGIN;
            }
            counter = 0.0;
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


    

