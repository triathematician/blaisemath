/*
 * Task.java 
 * Created on Aug 28, 2007, 10:26:52 AM
 */

// TODO Add originator of the task
// TODO Add time scale of the task

package tasking;

import scio.coordinate.V2;
import simulation.Agent;

/**
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
 * <br><br>
 * @author Elisha Peterson
 */
public class Task {
    
    // PROPERTIES
    
    /** The originator of the task. */
    TaskGenerator source;
    /** The targeting point of the task. */
    V2 target;    
    /** Type of goal. */
    int goalType;
    /** The priority weight of the task. */
    double priority;
    /** The timestamp of the task. */
    //double time;
//    /** Who the task is assigned to. */
//    Agent owner;
    

    // CONSTRUCTORS    
    
    /** 
     * Constructs given several settings.
     * @param target the object of the task
     * @param type 0 if trivial, 1 if pursue, 2 if evade
     * @param priority priority of the task, between 0 and 1 */
    public Task(TaskGenerator source,V2 target,int type,double priority){
        this.source=source;
        this.target=target;
        this.goalType=type;
        setPriority(priority);
    }

    
    // BEAN PATTERNS: GETTERS & SETTERS
    
    /** Returns generator of the task. */
    public TaskGenerator getGenerator(){return source;}

    public int getGoalType() {
        return goalType;
    }
    /** Returns target
     * @return object of the task */
    public V2 getTarget(){return target;}
    /** Returns priority of the target
     * @return priority as a double between 0 and 1 */
    public double getPriority(){return priority;}
    
    /** Sets target
     * @param target the new target */
    public void setTarget(V2 target){
        this.target=target;
    }
    /** Sets priority. Makes sure value is between 0 and 1.
     * @param priority a double between 0 and 1. */
    public void setPriority(double priority){
        this.priority=(priority<0)?0:(priority>1)?1:priority;
    }
    
    
    // OTHER METHODS
    
    @Override
    public String toString(){
        return "Task target="+target+", type="+goalType+", priority="+priority+" (orginated by "+source+")";
    }
}
