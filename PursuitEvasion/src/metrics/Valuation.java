/**
 * Valuation.java
 * Created on Apr 25, 2008
 */

package metrics;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.util.Vector;
import javax.swing.event.EventListenerList;
import scio.function.Function;
import scio.function.FunctionValueException;
import sequor.Settings;
import sequor.SettingsProperty;
import sequor.model.DoubleRangeModel;
import sequor.model.StringRangeModel;
import simulation.Team;
import utility.DistanceTable;

/**
 * This class describes how to assign a value to a particular configuration. We assume for now that it depends entirely
 * upon the distance table, so that this is essentially just a function which inputs a DistanceTable and outputs an AgentPair containing
 * the resulting value and agents which are responsible for giving that value (possibly null).
 * <br><br>
 * By default, the function returns the minimum distance between two specified teams.
 * 
 * @author Elisha Peterson
 */
public class Valuation implements Function<DistanceTable,Double> {
    
    public ValuationSettings vs;
    public Team owner;
    public Team target;
    
    public Valuation(Team owner, Team target, double threshold) {
        this.owner = owner;
        this.target = target;
        vs = new ValuationSettings(owner, target, DIST_MIN, threshold);
        addActionListener(owner);
    }
    public Valuation(Team owner, Team target, int type){
        this.owner = owner;
        this.target = target;
        vs = new ValuationSettings(owner, target, type, 0.0);
        addActionListener(owner);
    }
    
    public Double getValue(DistanceTable dt) throws FunctionValueException {
        switch(vs.type.getValue()){
            case DIST_MIN:
                return dt.min(owner.getActiveAgents(),target.getActiveAgents()).getDistance();
            case DIST_MAX:
                return dt.max(owner.getActiveAgents(),target.getActiveAgents()).getDistance();
            case DIST_AVG:
                return dt.average(owner.getActiveAgents(),target.getActiveAgents());
            case NUM_TEAM:
                return (double)owner.getActiveAgents().size();
            case NUM_OPPONENT:
                return (double)target.getActiveAgents().size();
            case NUM_CAP:
                return (double)(target.getSize()-target.getActiveAgents().size());
            case NUM_DIFF:
                return (double)(owner.getActiveAgents().size()-target.getActiveAgents().size());
            case TIME_TOTAL:
                return -1.0;
            case TIME_SINCE_CAP:
                return -1.0;
        }
        return -1.0;
    }
    
    public Vector<Double> getValue(Vector<DistanceTable> xx) throws FunctionValueException {return null;}    
    
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
    
    
    // SETTINGS
    
    public double getThreshold(){return vs.threshold.getValue();}
            
    
    // CONSTANTS
    
    public static final int DIST_MIN = 0;
    public static final int DIST_MAX = 1;
    public static final int DIST_AVG = 2;
    public static final int NUM_TEAM = 3;
    public static final int NUM_OPPONENT = 4;
    public static final int NUM_CAP = 5;
    public static final int NUM_DIFF = 6;
    public static final int TIME_TOTAL = 7;
    public static final int TIME_SINCE_CAP = 8;
    
    public static final String[] typeStrings = {
        "Minimum distance between teams", "Maximum distance between teams", "Average distance between teams",
        "Team size", "Opponent team size", "Opponents captured", "Advantage in player numbers",
        "Time of simulation", "Time since last capture"
    };    
    
    
    
    // SUB-CLASS ValuationSettings
    
    /** Wrapper for the static settings of the goal. */
    private class ValuationSettings extends Settings {
        
        /** The team owning this goal. */
        private Team owner;  
        /** The target of this goal. */
        private Team target;
    
        /** 
         * Another parameter to use in specifying the goal. Usually the "target"
         * distance required to reach the goal.
         */
        private DoubleRangeModel threshold = new DoubleRangeModel(1,0,1000,.1);
        /** Parameter describing the type of valuation. */
        private StringRangeModel type = new StringRangeModel(typeStrings,0,0,7);
        
        public ValuationSettings(Team owner,Team target,int type,double threshold){
            setName("Valuation");
            this.owner = owner;
            this.target = target;
            this.threshold.setValue(threshold);
            this.type.setValue(type);
            add(new SettingsProperty("Threshhold",this.threshold,Settings.EDIT_DOUBLE));
            add(new SettingsProperty("Type",this.type,Settings.EDIT_COMBO));
            initEventListening();
        }
        
        /** Listens for changes to settings */
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            String ac=null;
            if(evt.getSource()==threshold){ac="teamSetupChange";}
            else if(evt.getSource()==type){ac="teamSetupChange";}
            fireActionPerformed(ac);
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

    } // SUB-CLASS ValuationSettings
}
