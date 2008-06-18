/*
 * Straight.java 
 * Created on Aug 28, 2007, 11:21:25 AM
 */

package behavior;

import simulation.Agent;
import scio.coordinate.R2;
import scio.coordinate.V2;

/**
 * @author Elisha Peterson<br><br>
 * 
 * Behavior moving directly towards the origin and then circling, searching for targets
 */
public class OriginConverge extends behavior.Behavior { 
    double start = 0.0;
    double counter = 0.0;
    public R2 direction(Agent self,V2 target,double t){
        double x = 0.0;
        double y = 0.0;
        if (target == null){
        if (self.loc.magnitude() < 1) {
            start++;        
        }   
        if (start == 0.0){
            
                return R2.ORIGIN.minus(self.loc);
            
        }
        
        if(start>0){
            counter++;
        }
            if (counter >= 0 && counter <= ((Math.PI * self.getSensorRange()) / (self.getTopSpeed() * .1))) {
                x = (.5 * self.getSensorRange()) * Math.sin((counter * self.getTopSpeed() * .1) / (.5 * self.getSensorRange()));

                y = .5 * self.getSensorRange() - (.5 * self.getSensorRange()) * Math.cos((counter * self.getTopSpeed() * .1) / (.5 * self.getSensorRange()));

                return new R2(x, y);

            } else if (counter > (Math.PI * self.getSensorRange()) / (self.getTopSpeed() * .1) && counter <= (3 * Math.PI * self.getSensorRange()) / (self.getTopSpeed() * .1)) {
                x = (-2 * self.getSensorRange()) * Math.sin((counter * self.getTopSpeed() * .1) / (2 * self.getSensorRange()));

                y = (2 * self.getSensorRange()) * Math.cos((counter * self.getTopSpeed() * .1) / (2 * self.getSensorRange()));

                return new R2(x, y);

            } else if (counter > (Math.PI * 3 * self.getSensorRange()) / (self.getTopSpeed() * .1) && counter <= (Math.PI * 6 * self.getSensorRange()) / (self.getTopSpeed() * .1)) {

                x = -3 * self.getSensorRange() * Math.sin((counter * self.getTopSpeed() * .1) / (3 * self.getSensorRange()) + .5 * Math.PI);

                y = 3 * self.getSensorRange() * Math.cos((counter * self.getTopSpeed() * .1) / (3 * self.getSensorRange()) + .5 * Math.PI);

                return new R2(x, y);


            } else if (counter > (Math.PI * 6 * self.getSensorRange()) / (self.getTopSpeed() * .1) && counter <= (Math.PI * 10 * self.getSensorRange()) / (self.getTopSpeed() * .1)) {

                x = -4 * self.getSensorRange() * Math.sin((counter * self.getTopSpeed() * .1) / (4 * self.getSensorRange()) + Math.PI);

                y = 4 * self.getSensorRange() * Math.cos((counter * self.getTopSpeed() * .1) / (4 * self.getSensorRange()) + Math.PI);

                return new R2(x, y);
            } 
            else{
                counter =0.0;
                return R2.ORIGIN;
                
            }
            
        }
      
        if (target.v.magnitude() == 0) {
            if (self.loc.distance(target) <= .5 * self.getSensorRange()) {
                return new R2(0, 0);
            }
            return target.minus(self.loc).normalized();
        } else {
            counter = 0.0;
            start = 0.0; 
            return (target.plus(target.v.multipliedBy(self.getLeadFactor() * self.loc.distance(target) / self.getTopSpeed()))).minus(self.loc).normalized();
        }
        }
           
                
      
}


