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
 * Behavior moving directly towards the origin and then circling with double sensor radius circles, searching for targets
 */
public class QuadrantSearchLarge extends behavior.Behavior {

    double start = 0.0;
    double counter = 0.0;
    
    @Override
    public void reset() {
        counter = 0.0;
        start = 0.0;
    }

    public R2 direction(Agent self, V2 target, double t) {
        double x = 0.0;
        double y = 0.0;


        if (target == null) {
            if (self.loc.magnitude() < 1) {
                start++;
            }

            if (start == 0.0) {
                start = 0.0;
                return R2.ORIGIN.minus(self.loc);
            } else if (start > 0) {
                counter++;

           // once the agent is at the origin it travels on an assigned path, allowing the team to cover the map.
              // this is step one
                if (counter >= 0 && counter < 2*self.getSensorRange()/(self.getTopSpeed()*.1)) {
                    if (self.toString().equals("Agent 1")) {
                        x = self.getSensorRange();
                        y = 0;
                    } else if (self.toString().equals("Agent 2")) {
                        x = 0;
                        y = self.getSensorRange();
                    } else if (self.toString().equals("Agent 3")) {
                        x = -self.getSensorRange();
                        y = 0;
                    } else if (self.toString().equals("Agent 4")) {
                        x = 0;
                        y = -self.getSensorRange();
                    }
                    return new R2(x, y);
                }
         // step 2 of the search in an assigned quadrant       
                else if(counter>=2*self.getSensorRange()/(self.getTopSpeed()*.1) && counter < (2*self.getSensorRange()/(self.getTopSpeed()*.1)+(Math.PI * self.getSensorRange()) / (self.getTopSpeed() * .1))) {
                    if (self.toString().equals("Agent 1")) {
                        x = (2 * self.getSensorRange()) * Math.sin(((counter-(2*self.getSensorRange()/(self.getTopSpeed()*.1))) * self.getTopSpeed() * .1) / (2 * self.getSensorRange())- Math.PI);

                        y = - (2 * self.getSensorRange()) * Math.cos(((counter-(2*self.getSensorRange()/(self.getTopSpeed()*.1))) * self.getTopSpeed() * .1) / (2 * self.getSensorRange())-Math.PI);
                    } else if (self.toString().equals("Agent 2")) {
                        x = (2 * self.getSensorRange()) * Math.sin(((counter-(2*self.getSensorRange()/(self.getTopSpeed()*.1))) * self.getTopSpeed() * .1) / (2 * self.getSensorRange())- .5*Math.PI);

                        y = - (2 * self.getSensorRange()) * Math.cos(((counter-(2*self.getSensorRange()/(self.getTopSpeed()*.1))) * self.getTopSpeed() * .1) / (2 * self.getSensorRange())-.5*Math.PI);
                    } else if (self.toString().equals("Agent 3")) {
                       x = (-2 * self.getSensorRange()) * Math.sin(((counter-(2*self.getSensorRange()/(self.getTopSpeed()*.1))) * self.getTopSpeed() * .1) / (2 * self.getSensorRange())- Math.PI);

                        y = (2 * self.getSensorRange()) * Math.cos(((counter-(2*self.getSensorRange()/(self.getTopSpeed()*.1))) * self.getTopSpeed() * .1) / (2 * self.getSensorRange())-Math.PI);
                    } else if (self.toString().equals("Agent 4")) {
                       x = (-2 * self.getSensorRange()) * Math.sin(((counter-(2*self.getSensorRange()/(self.getTopSpeed()*.1))) * self.getTopSpeed() * .1) / (2 * self.getSensorRange())- .5*Math.PI);

                        y =  (2 * self.getSensorRange()) * Math.cos(((counter-(2*self.getSensorRange()/(self.getTopSpeed()*.1))) * self.getTopSpeed() * .1) / (2 * self.getSensorRange())-.5*Math.PI);
                    }
                    return new R2(x, y);
                }
              
            // step 3 of the quadrant search algorithm
                else if (counter >= (2*self.getSensorRange()/(self.getTopSpeed()*.1)+(Math.PI * self.getSensorRange()) / (self.getTopSpeed() * .1)) && counter < (4*(self.getSensorRange()/(self.getTopSpeed()*.1))+(Math.PI * self.getSensorRange()) / (self.getTopSpeed() * .1))){
                    if (self.toString().equals("Agent 1")) {
                        x = 0;
                        y = self.getSensorRange();
                    } else if (self.toString().equals("Agent 2")) {
                        x = -self.getSensorRange();
                        y = 0;
                    } else if (self.toString().equals("Agent 3")) {
                        x = 0;
                        y = -self.getSensorRange();
                    } else if (self.toString().equals("Agent 4")) {
                        x = self.getSensorRange();
                        y = 0;
                    }
                    return new R2(x, y);
                }
                
            // step 4 of the quadrant search algorithm    
                else if (counter >= (4*(self.getSensorRange()/(self.getTopSpeed()*.1))+(1*Math.PI * self.getSensorRange()) / (self.getTopSpeed() * .1)) && counter < (4*(self.getSensorRange()/(self.getTopSpeed()*.1))+(3*Math.PI * self.getSensorRange()) / (self.getTopSpeed() * .1))){
                    if (self.toString().equals("Agent 1")) {
                        x = ( 2*self.getSensorRange()) * Math.sin(((counter-4*(self.getSensorRange()/(self.getTopSpeed()*.1))) * self.getTopSpeed() * .1) / (2*self.getSensorRange())- .5*Math.PI);

                        y = -2*(self.getSensorRange())+(2*self.getSensorRange()) * Math.cos(((counter-4*(self.getSensorRange()/(self.getTopSpeed()*.1))) * self.getTopSpeed() * .1) / (2*self.getSensorRange())-.5*Math.PI);
                    }  else if(self.toString().equals("Agent 2")) {
                       x = ( -2*self.getSensorRange()) * Math.sin(((counter-4*(self.getSensorRange()/(self.getTopSpeed()*.1))) * self.getTopSpeed() * .1) / (2*self.getSensorRange())- 1.5*Math.PI);

                        y = 2*(self.getSensorRange())+(-2*self.getSensorRange()) * Math.cos(((counter-4*(self.getSensorRange()/(self.getTopSpeed()*.1))) * self.getTopSpeed() * .1) / (2*self.getSensorRange())-1.5*Math.PI);
                    } else if (self.toString().equals("Agent 3")) {
                        x = (- 2*self.getSensorRange()) * Math.sin(((counter-4*(self.getSensorRange()/(self.getTopSpeed()*.1))) * self.getTopSpeed() * .1) / (2*self.getSensorRange())- .5*Math.PI);

                        y = 2*(self.getSensorRange())+(-2*self.getSensorRange()) * Math.cos(((counter-4*(self.getSensorRange()/(self.getTopSpeed()*.1))) * self.getTopSpeed() * .1) / (2*self.getSensorRange())-.5*Math.PI);
                    } else if (self.toString().equals("Agent 4")) {
                       x = ( 2*self.getSensorRange()) * Math.sin(((counter-4*(self.getSensorRange()/(self.getTopSpeed()*.1))) * self.getTopSpeed() * .1) / (2*self.getSensorRange())- 1.5*Math.PI);

                        y = -2*(self.getSensorRange())+(2*self.getSensorRange()) * Math.cos(((counter-4*(self.getSensorRange()/(self.getTopSpeed()*.1))) * self.getTopSpeed() * .1) / (2*self.getSensorRange())-1.5*Math.PI);
                    }
                    return new R2(x, y);
                }
               
                
                
                
                
                else {
                    start = 0.0;
                    counter = 0.0;
                    return R2.ORIGIN;
                }
            }
        }
            counter = 0.0;
            start = 0.0;
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

    

           
                
      




