/*
 * Tasking.java
 * Created on Sep 4, 2007, 12:52:52 PM
 */

package behavior;

import task.*;
import agent.Team;

/**
 * @author Elisha Peterson
 * <br><br>
 * Super-class which contains default for assigning tasks to all team members. Specialized
 * algorithms for this assignment should override this method.
 */
public class Tasking {
    
// CONSTANTS
    
    public static final int FIRST=0;
    public static final int NO_TASKING=0;
    public static final int AUTO_CLOSEST=1;
    public static final int AUTO_TWO_LINE=2;
    public static final int AUTO_FARTHEST=3;
    public static final int AUTO_COM=4;
    public static final int AUTO_GRADIENT=5;
    public static final int CONTROL_CLOSEST=6;
    public static final int LAST=6;
    public static final String[] TASKING_STRINGS={"None","Autonomous Closest","Autonomous Two Closest","Autonomous Farthest","Autonomous Center-of-Mass","Autonomous Gradient","Control Closest"};
    
    
// CONSTRUCTORS    
    
    /** Return class with desired tasking
     * @param tasking the tasking code
     * @return subclass of tasking with desired algorithm */
    public static Tasking getTasking(int tasking){
        switch(tasking){
        case AUTO_CLOSEST:      return new behavior.autonomy.Closest();
        case AUTO_TWO_LINE:     return new behavior.autonomy.TwoLine();
        case AUTO_FARTHEST:     return new behavior.autonomy.Farthest();
        case AUTO_COM:          return new behavior.autonomy.CenterOfMass();
        case AUTO_GRADIENT:     return new behavior.autonomy.Gradient();
        case CONTROL_CLOSEST:   return new behavior.control.Closest();
        }
        return new Tasking();
    }
    
// METHODS
    
    /** Assigns agents of team a to targets, given the specified goal
     * @param team  team to assign tasks to
     * @param goal  the goal used to make the assignments */
    public void assign(Team team,Goal goal){}
}
