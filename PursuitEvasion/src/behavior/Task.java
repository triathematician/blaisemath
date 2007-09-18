/*
 * Task.java 
 * Created on Aug 28, 2007, 10:26:52 AM
 */

package behavior;

import Euclidean.PVector;
import simulation.Agent;

/**
 * @author Elisha Peterson
 * <br><br>
 * Contains a task, whose object is some target agent. The target be a stationary goal, a moving
 * player, or some other kind of point (e.g. center of mass of a team). The task also has
 * a priority level (between 0 and 1), and a boolean describing whether the player should
 * seek or flee the target. End behaviors will usually arise based on a combination of different
 * tasks with different priority levels. Tasks will be assigned by either a single player, a team
 * control element, or by communication between players. These are done using the Autonomous class,
 * the Control class, and the Cooperation task, respectively.
 * <br><br>
 * Eventually, this class will also implement <i>cooperative tasks</i>. As an example, two players
 * might want to work together to surround a second player. As there is still a single target player,
 * however, this can currently be implemented with identical tasks between the players and an agreement
 * to carry out a "Surround" behavior.
 */
public class Task {
    
// PROPERTIES
    
    /** Target agent of the task */
    PVector target;    
    /** Whether to seek or flee the target */
    boolean seek=true;
    /** Priority level of the task (between 0 and 1) */
    double priority=1;
    

// CONSTRUCTORS    
    
    /** Default constructor */
    public Task(){}
    /** Constructs with settings.
     * @param target the object of the task
     * @param type 0 if trivial, 1 if pursue, 2 if evade
     * @param priority priority of the task, between 0 and 1 */
    public Task(PVector target,int type,double priority){setTarget(target);setSeek(type);setPriority(priority);}
    
    
// BEAN PATTERNS: GETTERS & SETTERS
    
    /** Returns target
     * @return object of the task */
    public PVector getTarget(){return target;}
    /** Returns seeking or fleeing task
     * @return true if task is seek; false if task is flee */
    public boolean getSeek(){return seek;}
    /** Returns priority of the target
     * @return priority as a double between 0 and 1 */
    public double getPriority(){return priority;}
    
    /** Sets target
     * @param target the new target */
    public void setTarget(PVector target){this.target=target;}
    /** Sets seeking vs fleeing parameter
     * @param type 0 if trivial, 1 if pursue, 2 if evade */
    public void setSeek(int type){seek=(type==1);}
    /** Sets priority. Makes sure value is between 0 and 1.
     * @param priority a double between 0 and 1. */
    public void setPriority(double priority){this.priority=(priority<0)?0:(priority>1)?1:priority;}
}
