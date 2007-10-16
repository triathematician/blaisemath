/*
 * Goal.java
 * Created on Aug 28, 2007, 10:26:56 AM
 */

package behavior;

import Model.ComboBoxRangeModel;
import Model.DoubleRangeModel;
import Model.IntegerRangeModel;
import Model.Settings;
import Model.SpinnerDoubleEditor;
import simulation.Team;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.SpinnerModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import simulation.Agent;
import utility.AgentPair;
import utility.DistanceTable;

/**
 * This represents a team goal. Goals are restricted by requiring that they be
 * related to the distance matrix between two teams. Every goal has a value method
 * which returns how close the given team is to reaching that goal... hence, a
 * "Goal" class will input two teams and output a value related to how close the
 * team is to obtaining that goal.
 * <br><br>
 * @author Elisha Peterson
 */
public class Goal extends Settings {
    
    
// PROPERTIES
    
    /** The team owning this goal. */
    private Team team;  
    /** The target of this goal. */
    private Team target;
    
    /** Whether or not the goal has been achieved. */
    private boolean achieved=false;
    /** The agent which achieved the goal. */
    private Agent which;
    
    /** Specifies whether goal is trivial (0), pursuit (1), or evade (2). */
    private ComboBoxRangeModel type=new ComboBoxRangeModel(TYPE_STRINGS,NONE,0,2);
    /** Specifies number of team members to consider: one (0) or all (1). */
    private ComboBoxRangeModel all=new ComboBoxRangeModel(NUMBER_STRINGS,ALL,ONE,ALL);
    /** The threshhold distance */
    private DoubleRangeModel threshhold=new DoubleRangeModel(5,0,5000);
    
    
// CONSTANTS
    
    public static final int NONE=0;
    public static final int PURSUE=1;
    public static final int EVADE=2;
    public static final String[] TYPE_STRINGS={"None","Pursue","Evade"};
    
    public static final int ONE=0;
    public static final int ALL=1;
    public static final String[] NUMBER_STRINGS={"One","All"};
    
    
// CONSTRUCTORS & INITIALIZERS   
    
    public Goal(){this(null,null,NONE,ONE,0.0);}  
    public Goal(int type,int all,double threshhold){this(null,null,type,all,threshhold);}
    public Goal(Team team,Team target,int type,int all,double threshhold){
        setGoal(team,target,type,all,threshhold);
        //addProperty("Target Team",null,Settings.EDIT_STRING);
        addProperty("Goal Type",this.type,Settings.EDIT_COMBO);
        addProperty("One or All",this.all,Settings.EDIT_COMBO);
        addProperty("Capture Distance",this.threshhold,Settings.EDIT_DOUBLE);
        initEventListening();
    }

    public void setGoal(Team team,Team target,int type,int all,double threshhold){setTeam(team);setTarget(target);setType(type);setAll(all);setThreshhold(threshhold);}
        
    public void reset(){achieved=false;which=null;}
    
// BEAN PATTERNS: GETTERS & SETTERS    
    
    public int getType(){return type.getValue();}
    public int getAll(){return all.getValue();}
    public double getThreshhold(){return threshhold.getValue();}
    public Team getTeam(){return team;}
    public Team getTarget(){return target;}
    public boolean isAchieved(){return achieved;}
    public boolean isTrivial(){return type.getValue()==NONE;}
 
    public void setType(int newValue){type.setValue(newValue);}
    public void setAll(int newValue){all.setValue(newValue);}
    public void setThreshhold(double newValue){threshhold.setValue(newValue);}
    public void setTeam(Team newValue){ 
        if(newValue==null){return;}
        if(newValue!=team){team=newValue;pcs.firePropertyChange("goalTeam",null,newValue);}
    }
    public void setTarget(Team newValue){
        if(newValue==null||newValue==team){return;}
        if(newValue!=target){target=newValue;pcs.firePropertyChange("goalTarget",null,newValue);}
    }
    public void setAchieved(boolean newValue){achieved=newValue;}
        
    
// SPECIAL METHODS: INDICATE PROGRESS TOWARDS GOAL   
    
    /** Measures closeness to obtaining the goal. This is measured by the distance to
     * close (or open up) between players in order for the goal to be reached.
     * @param d     the global table of distances
     * @return      numeric value indicating the closeness to the goal (positive if goal has been reached, otherwise negative) */
    public double value(DistanceTable d){
        if(type.getValue()==NONE){return 0;
    } else if(type.getValue()==PURSUE){
            AgentPair result=(all.getValue()==ALL?d.max(team,target):d.min(team,target));
            double dist=threshhold.getValue()-result.getDistance();
            if(dist>0&&!achieved){which=result.getFirst();}
            return dist;
        } else if(type.getValue()==EVADE){
            AgentPair result=(all.getValue()==ALL?d.max(team,target):d.min(team,target));
            double dist=result.getDistance()-threshhold.getValue();
            if(dist>0&&!achieved){which=result.getFirst();}
            return dist;
        }
        return -1;
    }
    
    /** Returns whether or not goal has been achieved
     * @param d     the global table of distances
     * @return      true if goal achieved, false otherwise */
    public boolean justAchieved(DistanceTable d){
        if(!achieved){
            if(value(d)>0){
                achieved=true;return true;
            }
        }
        return false;
    }    
    
    /** Returns agent which achieved the goal */
    public Agent getAgent(){return which;}
}
