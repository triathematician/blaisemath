/*
 * Behavior.java
 * 
 * Created on Aug 28, 2007, 10:26:46 AM
 * 
 * Author: Elisha Peterson
 * 
 * A behavior is an algorithm for converting an agent's understanding of the playing field (Pitch)
 * into movement. This is accomplished by the method direction(Pitch,Time) whereby a desired direction
 * of travel (mathematically, a <b>unit vector</b>) is computed. This will later be normalized by the
 * player via their maximum speed and maneuvering constraints. 
 */

package behavior;

import sequor.model.ComboBoxRangeModel;
import simulation.Agent;
import scio.coordinate.R2;
import scio.coordinate.V2;

/**
 *
 * @author ae3263
 */
public abstract class Behavior {
    
// CONSTANTS
    
    public static final int FIRST=0;
    public static final int STATIONARY=0;
    public static final int SEEK=1;
    public static final int PURSUIT_LEADING=2;
    public static final int FLEE=3;
    public static final int FIXEDPATH=4;
    public static final int EVASION_RANDOMPATH=5;
    public static final int LAST=5;
    public static final String[] BEHAVIOR_STRINGS={"Stationary","Seek","Pursue with Lead Factor","Flee","Follow Fixed Path","Follow Random Path"};

    public static ComboBoxRangeModel getComboBoxModel(){
        return new ComboBoxRangeModel(BEHAVIOR_STRINGS,STATIONARY,FIRST,LAST);
    }
    
// CONSTRUCTORS    
    
    /** Return class with desired behavior 
     * @param behavior the behavioral code 
     * @return a subclass of behavior with the desired algorithm */
    public static Behavior getBehavior(int behavior){
        switch(behavior){
        case STATIONARY:            return new behavior.Stationary();
        case SEEK:                  return new behavior.Seek();
        case PURSUIT_LEADING:       return new behavior.Leading();
        case FLEE:                  return new behavior.Flee();
        case FIXEDPATH:             return new behavior.ApproachPath();
        case EVASION_RANDOMPATH:    return new behavior.RandomPath();
        }     
        return null;
    }
    
// METHODS    
    
    /**Computes desired direction of travel
     * @param self      the agent exhibiting this behavior
     * @param target    the agent targeted by the behavior
     * @param t         the current time stamp
     * @return          the direction of travel corresponding to this behavior */
    public abstract R2 direction(Agent self,V2 target,double t);
}
