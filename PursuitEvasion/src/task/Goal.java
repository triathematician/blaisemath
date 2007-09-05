/*
 * Goal.java
 * Created on Aug 28, 2007, 10:26:56 AM
 */

package task;

import agent.Team;
import utility.DistanceTable;

/**
 * @author Elisha Peterson
 * <br><br>
 * This represents a team goal. Goals are restricted by requiring that they be
 * related to the distance matrix between two teams. Every goal has a value method
 * which returns how close the given team is to reaching that goal... hence, a
 * "Goal" class will input two teams and output a value related to how close the
 * team is to obtaining that goal.
 * <br><br>
 * Goals are specified by three parameters:
 * <ul>
 *   <li>use the <i>minimum</i> or <i>maximum</i> distance between teams?
 *   <li>what is the <i>threshhold distance</i> past which the goal is reached?
 *   <li>is the desire that the distance be <i>greater than</i> or <i>less than</i>
 *     the threshhold distance?
 * </ul>
 * The four settings of min/max, greater/lesser correspond to the following methods:
 * <ul>
 *   <li>setCaptureAll: maximum/less than
 *   <li>setCaptureOne: minimum/less than
 *   <li>setEvadeCapture: minimum/greater than
 *   <li>setEscapeAll: maximum/greater than
 * </ul>
 */
public class Goal {
    
    
// PROPERTIES
    
    /** The team owning this goal. */
    Team team;    
    /** The target of this goal. */
    Team target;
    
    /** Is the goal trivial? */
    boolean trivial;
    /** Whether to use the minimum (true) or maximum (false) */
    boolean minimum;
    /** Whether to desire greater than (true) or less than (false).
     * Usually true indicates flee and false indicates seek. */
    boolean greater;
    /** The threshhold distance */
    double threshhold;
    
    
// CONSTANTS
    
    /** Specifies a pursuing team */
    public static final int TRIVIAL=0;
    /** Specifies a pursuing team */
    public static final int CAPTURE_ALL=101;
    /** Specifies a pursuing team */
    public static final int CAPTURE_ONE=102;
    /** Specifies an evading team */
    public static final int ALL_ESCAPE=201;
    /** Specifies an evading team */
    public static final int ONE_ESCAPE=202;
    
    
// CONSTRUCTORS    

    /** Default constructor */
    public Goal() {
        setTeam(null);
        setTarget(null);
        setType(CAPTURE_ALL);
        setThreshhold(0);
    }     
    /** Constructor with goalType and threshhold only. The team/target
     * must be set later!
     * @param goalType
     * @param threshhold */
    public Goal(int goalType,double threshhold){
        setTeam(null);
        setTarget(null);
        setType(goalType);
        setThreshhold(threshhold);
    }
    /** Constructor with team, target, goalType, and threshhold
     * @param team 
     * @param target
     * @param goalType
     * @param threshhold */
    public Goal(Team team,Team target,int goalType,double threshhold){
        setTeam(team);
        setTarget(target);
        setType(goalType);
        setThreshhold(threshhold);
    }

        
// BEAN PATTERNS: GETTERS & SETTERS    
    
    public Team getTeam(){return team;}
    public Team getTarget(){return target;}
    public boolean isMinimum(){return minimum;}
    public boolean isGreater(){return greater;}
    public boolean isSeek(){return !greater;}
    public double getThreshhold(){return threshhold;}
 
    public void setTeam(Team team){this.team=team;}
    public void setTarget(Team target){this.target=target;}
    public void setMinimum(boolean minimum){this.minimum=minimum;}
    public void setGreater(boolean greater){this.greater=greater;}
    public void setThreshhold(double threshhold){this.threshhold=threshhold;}
    
    public void setType(int goalType){
        switch(goalType){
                                                case TRIVIAL:       trivial=true; return;
            case CAPTURE_ALL:   setMinimum(false);  setGreater(false);  break;
            case CAPTURE_ONE:   setMinimum(true);   setGreater(false);  break;
            case ALL_ESCAPE: setMinimum(true);   setGreater(true);   break;
            case ONE_ESCAPE:    setMinimum(false);  setGreater(true);   break;
        }
        trivial=false;
    }


// METHODS FOR MEASURING PROGRESS TO GOAL   
    
    /** 
     * Measures closeness to obtaining the goal. This is measured by the distance to
     * close (or open up) between players in order for the goal to be reached.
     * @param d     the global table of distances
     * @return      numeric value indicating the closeness to the goal (positive if goal has been reached, otherwise negative)
     */
    public double value(DistanceTable d){
        if(trivial){return 0;}
        double value;
        if(minimum){value=d.min(team,target).getDistance();}
        else{value=d.max(team,target).getDistance();}
        if(greater){value=value-threshhold;}
        else{value=threshhold-value;}
        return value;
    }
    
    /**
     * Returns whether or not goal has been achieved
     * @param d     the global table of distances
     * @return      true if goal achieved, false otherwise
     */
    public boolean isAchieved(DistanceTable d){
        return value(d)>0;
    }
}
