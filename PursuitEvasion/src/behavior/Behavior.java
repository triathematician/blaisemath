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

import Euclidean.PPoint;
import Model.ComboBoxRangeModel;
import agent.Agent;

/**
 *
 * @author ae3263
 */
public class Behavior {
    
// CONSTANTS
    
    public static final int FIRST=0;
    public static final int STATIONARY=0;
    public static final int SEEK=1;
    public static final int PURSUIT_LEADING=2;
    public static final int FLEE=3;
    public static final int EVASION_FIXEDPATH=4;
    public static final int EVASION_RANDOMPATH=5;
    public static final int LAST=5;
    public static final String[] BEHAVIOR_STRINGS={"Stationary","Seek","Pursue with Lead Factor","Flee","Follow Fixed Path","Follow Random Path"};

    public static ComboBoxRangeModel getComboBoxModel(){return new ComboBoxRangeModel(BEHAVIOR_STRINGS,STATIONARY,FIRST,LAST);}
    
// CONSTRUCTORS    

    /** Default constructor */
    public Behavior(){}
    
    /** Return class with desired behavior 
     * @param behavior the behavioral code 
     * @return a subclass of behavior with the desired algorithm */
    public static Behavior getBehavior(int behavior){
        switch(behavior){
        case SEEK:                  return new behavior.pursuit.Seek();
        case PURSUIT_LEADING:       return new behavior.pursuit.Leading();
        case FLEE:                  return new behavior.evasion.Flee();
        case EVASION_FIXEDPATH:     return new behavior.evasion.FixedPath();
        case EVASION_RANDOMPATH:    return new behavior.evasion.RandomPath();
        }
        return new Behavior();        
    }
    
// METHODS    
    
    /**Computes desired direction of travel
     * @param self      the agent exhibiting this behavior
     * @param target    the agent targeted by the behavior
     * @param t         the current time stamp
     * @return          the direction of travel corresponding to this behavior */
    public PPoint direction(Agent self,Agent target,double t){
        return new PPoint();
    }
}
