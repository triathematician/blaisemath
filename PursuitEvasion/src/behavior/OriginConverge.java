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
                start = 0.0;
                return R2.ORIGIN.minus(self.loc);
            
        }
      
        if(start>0){
            counter++;
        }   
        
            
            if(counter>=0 && counter < self.getSensorRange()){
                if(agent.toString().equals("Agent 1")) {
                     x = self.getSensorRange();
                     y = 0;
                }     
                else if(agent.toString().equals("Agent 2")) {
                     x = 0;
                     y = self.getSensorRange();
                }
                else if(agent.toString().equals("Agent 3")) {
                     x = -self.getSensorRange();
                     y = 0;
                }     
                else if(agent.toString().equals("Agent 3")) {
                     x = 0;
                     y = -self.getSensorRange(); 
                }
            }
                     return new R2 (x,y);
        }
                
        
    
        
        
        
        
        
        
        
        
        
        
        
        
        
        
            else{
                start = 0.0;
                counter =0.0;
                return R2.ORIGIN;
                
            }
            
        
      
        if (target.v.magnitude() == 0) {
            if (self.loc.distance(target) <= .5 * self.getSensorRange()) {
                return new R2(0, 0);
            }
            return target.minus(self.loc).normalized();
        } 
        else {
            counter = 0.0;
            start = 0.0; 
            return (target.plus(target.v.multipliedBy(self.getLeadFactor() * self.loc.distance(target) / self.getTopSpeed()))).minus(self.loc).normalized();
        }
        }
           
                
      
}


