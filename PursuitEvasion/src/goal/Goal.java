/*
 * Goal.java
 * Created on Aug 28, 2007, 10:26:56 AM
 */

package goal;

import sequor.model.DoubleRangeModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import javax.swing.JPanel;
import javax.swing.event.EventListenerList;
import scio.function.Function;
import sequor.component.Settings;
import sequor.model.ComboBoxRangeModel;
import simulation.Team;
import simulation.Agent;
import tasking.Tasking;
import utility.AgentPair;
import utility.DistanceTable;

/**
 * Represents a team level goal. Such a goal is measured by whether a value function
 * between two teams reaches a particular threshhold. For more specialized types of
 * goals, implement a child-class of this one.
 * <br><br>
 * All settings are encapsulated within a subclass GoalSettings.
 * <br><br>
 * @author Elisha Peterson
 */
public class Goal implements Function<DistanceTable,Double>{
        
    // PROPERTIES
    
    /** Static settings. */
    private GoalSettings gs;    
    /** Tasking class. */
    private Tasking tasker;
    /** Whether or not the goal has been achieved. */
    private boolean achieved=false;    
    /** The agent which achieved the goal. */
    private Agent which=null;    
    
    
    // CONSTANTS
    
    public static final int SEEK=0;
    public static final int FLEE=1;
    public static final String[] TYPE_STRINGS={"Seek","Flee"};
    
    
    // CONSTRUCTORS & INITIALIZERS   
    
    /** 
     * Default initializer requires several parameters.
     * @param weight    a constant factor (between 0 and 1) associated with the goal
     * @param owner     the team whose goal this is
     * @param target    the target of this goal
     * @param type      whether this is a "SEEK" goal or an "EVADE" goal
     * @param thresh    specifies at what point the goal has been reached... sometimes a more generic parameter
     */
    public Goal(double weight,Team owner,Team target,int type,double thresh){
        gs=new GoalSettings(weight,owner,target,type,thresh);
        tasker=gs.getTasking();
        addActionListener(owner);
    }
        
    /**
     * Resets the goal after each simulation is run.
     */
    public void reset(){
        achieved=false;
        which=null;
    }
    
    /**
     * Assigns tasks to team based upon the tasking algorithm.
     */
    public void assignTasks(){
        tasker.assign(gs.owner,this,getWeight());
    }
    
    /** 
     * String representation of the goal.
     */
    @Override
    public String toString(){return "Goal";}        
    
    
    // IMPLEMENT BASIC FUNCTIONALITY OF THE GOAL   
    
    /** 
     * Measures closeness to obtaining this particular goal. This is measured by the distance to
     * close (or open up) between players in order for the goal to be reached.
     * @param d     the global table of distances
     * @return      numeric value indicating the closeness to the goal (positive if goal has been reached, otherwise negative) 
     */
    public Double getValue(DistanceTable d){
        AgentPair result=d.min(getOwner(),getTarget());
        double dist=getThreshhold()-result.getDistance();
        if(getType()==FLEE){dist=-dist;}
        if(dist>0&&!achieved){which=result.getFirst();}
        return dist;
    }    
    
    /** 
     * Returns whether or not goal has been achieved
     * @param d     the global table of distances
     * @return      true if goal achieved, false otherwise 
     */
    public boolean justAchieved(DistanceTable d){
        if(!achieved&&getValue(d)>0){
            achieved=true;
            return true;
        }
        return false;
    }    
    
    
    // UNSUPPORTED OPERATIONS

    public Double minValue() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Double maxValue() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    
    // BEAN PATTERNS  
    
    public int getType(){return gs.type.getValue();}
    public double getWeight(){return gs.weight.getValue();}
    public double getThreshhold(){return gs.threshhold.getValue();}
    public Team getOwner(){return gs.owner;}
    public Team getTarget(){return gs.target;}
    public boolean isAchieved(){return achieved;}
    public Agent getAgent(){return which;}
 
    public void setType(int newValue){gs.type.setValue(newValue);}
    public void setWeight(double newValue){gs.weight.setValue(newValue);}
    public void setThreshhold(double newValue){gs.threshhold.setValue(newValue);}
    public void setOwner(Team newValue){gs.setOwner(newValue);}
    public void setTarget(Team newValue){gs.setTarget(newValue);}
    
    public JPanel getPanel(){return gs.getPanel();}
    
    
    // EVENT HANDLING
    
    boolean editing;
    // Remaining code deals with action listening
    protected EventListenerList listenerList = new EventListenerList();
    public void addActionListener(ActionListener l){listenerList.add(ActionListener.class, l);}
    public void removeActionListener(ActionListener l){listenerList.remove(ActionListener.class, l);}
    protected void fireActionPerformed(String s){
        if(editing){return;}
        Object[] listeners=listenerList.getListenerList();
        for(int i=listeners.length-2;i>=0;i-=2){
            if(listeners[i]==ActionListener.class){
                ((ActionListener)listeners[i+1]).actionPerformed(new ActionEvent(this,ActionEvent.ACTION_PERFORMED,s));
            }
        }
    }
    
    
    // SUB-CLASS GoalSettings
    
    /** Wrapper for the static settings of the goal. */
    private class GoalSettings extends Settings {
        
        /** The team owning this goal. */
        private Team owner;  
        /** The target of this goal. */
        private Team target;
    
        /** Specifies the weighting/priority of the goal. */
        private DoubleRangeModel weight=new DoubleRangeModel(0,1,.01);
        /** Specifies whether goal is pursuit (0) or evade (1). */
        private ComboBoxRangeModel type=new ComboBoxRangeModel(TYPE_STRINGS,SEEK,0,1);
        /** 
         * Another parameter to use in specifying the goal. Usually the "target"
         * distance required to reach the goal.
         */
        private DoubleRangeModel threshhold=new DoubleRangeModel(0,1,.01);

        /** The team's tasking algorithm default */
        private ComboBoxRangeModel tasking=new ComboBoxRangeModel(Tasking.TASKING_STRINGS,Tasking.AUTO_CLOSEST,Tasking.FIRST,Tasking.LAST);

        /** Specifies the function describing whether the goal has been achieved. */
        private Function<DistanceTable,Double> value;
        
        public GoalSettings(double weight,Team owner,Team target,int type,double threshhold){
            this.weight.setValue(weight);
            this.owner=owner;
            this.target=target;
            this.type.setValue(type);
            this.threshhold.setValue(threshhold);
            addProperty("Goal Type",this.type,Settings.EDIT_COMBO);
            addProperty("Goal Weight",this.weight,Settings.EDIT_DOUBLE);
            addProperty("Goal Cutoff",this.threshhold,Settings.EDIT_DOUBLE);
            addProperty("Tasking",tasking,Settings.EDIT_COMBO);
            initEventListening();
        }
        
        /** Listens for changes to settings */
        public void propertyChange(PropertyChangeEvent evt) {
            String ac=null;
            if(evt.getSource()==weight){                                    ac="teamAgentsChange";
            } else if(evt.getSource()==type){                               ac="teamSetupChange";
            } else if(evt.getSource()==threshhold){                         ac="teamSetupChange";
            } else if(evt.getSource()==tasking){    tasker=getTasking();    ac="teamSetupChange";}
            fireActionPerformed(ac);
        }
        
        private Tasking getTasking(){
            return Tasking.getTasking(tasking.getValue());
        }
        public void setOwner(Team newValue){
            if(newValue!=null&&!newValue.equals(owner)){
                owner=newValue;
                pcs.firePropertyChange("goalOwner",null,newValue);
            }
        }
        public void setTarget(Team newValue){
            if(newValue!=null&&!newValue.equals(target)){
                target=newValue;
                pcs.firePropertyChange("goalTarget",null,newValue);
            }
        }

    } // SUB-CLASS GoalSettings
}
