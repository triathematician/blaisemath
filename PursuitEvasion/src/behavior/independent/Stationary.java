/*
 * Stationary.java
 * 
 * Created on Sep 18, 2007, 3:00:16 PM
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package behavior.independent;

import Euclidean.PPoint;
import Euclidean.PVector;
import behavior.Behavior;
import simulation.Agent;

/**
 * This class
 * <br><br>
 * @author Elisha Peterson
 */
public class Stationary extends Behavior {
    public PPoint direction(Agent self, PVector target, double t) {return new PPoint(0,0);}
}
