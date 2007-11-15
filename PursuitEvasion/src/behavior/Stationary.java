/*
 * Stationary.java
 * 
 * Created on Sep 18, 2007, 3:00:16 PM
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package behavior;

import behavior.Behavior;
import simulation.Agent;
import scio.coordinate.R2;
import scio.coordinate.V2;

/**
 * This class
 * <br><br>
 * @author Elisha Peterson
 */
public class Stationary extends Behavior {
    public R2 direction(Agent self, V2 target, double t) {return new R2();}
}
