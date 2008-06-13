/*
 * Behavior.java
 * Created on Aug 28, 2007, 10:26:46 AM
 */

package behavior;

import sequor.model.StringRangeModel;
import simulation.Agent;
import scio.coordinate.R2;
import scio.coordinate.V2;

/**
 * A behavior is an algorithm for converting an agent's understanding of the playing field (Pitch)
 * into movement. This is accomplished by the method direction(Pitch,Time) whereby a desired direction
 * of travel (mathematically, a <b>unit vector</b>) is computed. This will later be normalized by the
 * player via their maximum speed and maneuvering constraints. 
 * @author Elisha Peterson
 */
public abstract class Behavior {
    
// CONSTANTS
    
    public static final int STATIONARY=0;
    public static final int STRAIGHT=1;
    public static final int REVERSE=2;
    public static final int LEADING=3;
    public static final int LARGECIRCLESEARCH=4;
    public static final int SMALLCIRCLESEARCH=5;
    public static final int APPROACHPATH=6;
    public static final int RANDOMPATH=7;
    public static final String[] BEHAVIOR_STRINGS={"Stationary","Straight","Reverse","Leading", "Large Circle Search","Small Circle Search","Approach Path","Random Path"};

    public static StringRangeModel getComboBoxModel(){
        return new StringRangeModel(BEHAVIOR_STRINGS,STATIONARY,0,6);
    }
    
// CONSTRUCTORS    
    
    /** Return class with desired behavior 
     * @param behavior the behavioral code 
     * @return a subclass of behavior with the desired algorithm */
    public static Behavior getBehavior(int behavior){
        switch(behavior){
        case STATIONARY:                return new behavior.Stationary();
        case STRAIGHT:                  return new behavior.Straight();
        case REVERSE:                   return new behavior.Straight();
        case LEADING:                   return new behavior.Leading();
        case LARGECIRCLESEARCH:         return new behavior.LargeCircleSearch();
        case SMALLCIRCLESEARCH:         return new behavior.SmallCircleSearch(); 
        case APPROACHPATH:              return new behavior.ApproachPath();
        case RANDOMPATH:                return new behavior.RandomPath();
        }     
        return null;
    }
    
// METHODS    
    
    /** Resets behavior. */
    public void reset(){}
    
    /**Computes desired direction of travel
     * @param self      the agent exhibiting this behavior
     * @param target    the agent targeted by the behavior
     * @param t         the current time stamp
     * @return          the direction of travel corresponding to this behavior */
    public abstract R2 direction(Agent self,V2 target,double t);
}
