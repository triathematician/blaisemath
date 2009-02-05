/**
 * Valuation.java
 * Created on Apr 25, 2008
 */

package metrics;

import analysis.Cooperation;
import analysis.Cooperation.SplitContribution;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.util.Collection;
import java.util.HashSet;
import java.util.Vector;
import javax.swing.event.EventListenerList;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import scio.function.Function;
import scio.function.FunctionValueException;
import sequor.Settings;
import sequor.SettingsProperty;
import sequor.model.BooleanModel;
import sequor.model.DoubleRangeModel;
import sequor.model.StringRangeModel;
import sequor.model.SubsetModel;
import simulation.Agent;
import simulation.Simulation;
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
@XmlAccessorType(XmlAccessType.NONE)
public class Valuation implements Function<DistanceTable,Double> {
    
    public ValuationSettings vs;

    public Valuation() {
        this(new Vector<Team>(),new Team(),new Team(),DIST_MIN,0.0);
    }
    
    public Valuation(Vector<Team> teams, Team owner, Team target, double threshold) { this(teams, owner, target, DIST_MIN, threshold); }
    public Valuation(Vector<Team>  teams, Team owner, Team target, int type){ this(teams, owner, target, type, 0.0); }
    public Valuation(Vector<Team>  teams, Team owner, Team target, int type, double threshold){
        vs = new ValuationSettings(teams, owner, target, type, 0.0);
        vs.threshold.setValue(threshold);
        addActionListener(owner);
    }    
    
    /** Returns team's valuation of the current status of the game.
     * @param dt table of distances between players
     * @return Double value representing the measure, or NaN if there is no possible measure.
     * @throws scio.function.FunctionValueException
     */
    public Double getValue(DistanceTable dt) throws FunctionValueException {
        return getValue(dt, vs.valueAgents.getSubset());
    }    
    
    /** Returns team's valuation of the current status of the game.
     * @param dt table of distances between players
     * @return Double value representing the measure, or NaN if there is no possible measure.
     * @throws scio.function.FunctionValueException
     */
    public Double getValue(Simulation sim, Collection<Agent> subset) throws FunctionValueException {
        return getValue(sim.dist, subset);
    }
    
    /** Returns current valuation of the game, as viewed by a particular subset.
     * @param subset subset of agents "measuring" the value of the simulation
     * @param dt table of distances between players
     * @return Double value representing the measure, or NaN if there is no possible measure.
     * @throws scio.function.FunctionValueException
     */
    public Double getValue(DistanceTable dt, Collection<Agent> subset) throws FunctionValueException {
        try {
            HashSet<Agent> activeSubset = new HashSet<Agent>();
            activeSubset.addAll(vs.owner.getActiveAgents());
            activeSubset.retainAll(subset);
            switch(vs.type.getValue()){
                case DIST_MIN:
                    return dt.min(activeSubset,vs.target.getActiveAgents()).getDistance();
                case DIST_MAX:
                    return dt.max(activeSubset,vs.target.getActiveAgents()).getDistance();
                case DIST_AVG:
                    return dt.average(activeSubset,vs.target.getActiveAgents());
                case AGENT_CLOSEST_TO_CAPTURE:
                    return 1.0 + vs.owner.agents.indexOf(dt.min(activeSubset,vs.target.getActiveAgents()).getFirst());
                case NUM_ACTIVE_AGENTS:
                    return (double)activeSubset.size();
                case NUM_TEAM_SAFE:
                    return (double)vs.owner.getNumberSafe();
                case NUM_ACTIVE_OPPONENTS:
                    return (double)vs.target.getActiveAgents().size();
                case NUM_OPPONENTS_CAPTURED: // NEEDS COOP FIX
                    return (double)(subset.size() - activeSubset.size());
                case AGENT_NUMBER_ADVANTAGE:
                    return (double)(activeSubset.size()-vs.target.getActiveAgents().size());
                case TIME_TOTAL:
                    return dt.getTime();
                case POSSIBLE_CAPTURES_NOT_MADE:
                    return (double) ( subset.size() > vs.target.getAgentNumber() ?
                        (vs.target.getAgentNumber() - subset.size() + activeSubset.size())
                        : activeSubset.size() );
                case OPPONENTS_UNCAPTURED: // NEEDS COOP FIX
                    return (double) (vs.target.getAgentNumber() - subset.size() + activeSubset.size()) ;
                case TIME_SINCE_CAP:
                case PERCENT_OPPONENTS_CAPTURED:
                    return (double)(subset.size()-activeSubset.size())/(vs.target.getAgentNumber());
                default:
                    return Double.NaN;
            }
        } catch (NullPointerException e){
            return Double.NaN;
        }
    }
    
    public Vector<Double> getValue(Vector<DistanceTable> xx) throws FunctionValueException {return null;}  
    
    /** Returns subset contribution given a simulation (runs the simulation to compute it). */
    public SplitContribution getCooperationMetric(Simulation sim) {
        return Cooperation.subsetContribution(sim, this, getSubset());
    }
    
    /** Returns subset of agents evaluating this particular metric. */
    public HashSet<Agent> getSubset() { 
        return vs.valueAgents.getSubset();
    }

    /** Resets the subset model for the given team. */
    public void resetTeam() {
        vs.initModels();
    }

    
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
    
    
    // BEAN PATTERNS
    
    @XmlAttribute
    public double getThreshold(){return vs.threshold.getValue();}
    public void setThreshold(double t){vs.threshold.setValue(t);}
    
    @XmlAttribute(name="type")
    public int getType(){return vs.type.getValue();}
    public void setType(int t){vs.type.setValue(t);}

    public String getOwnerName(){return vs.owner.getName();}
    
    String targetName="";
    
    @XmlAttribute(name="target")
    public String getTargetName(){return vs.target.getName();}
    public void setTargetName(String name){
        if(vs.teams!=null){
            for(Team t:vs.teams){
                if(t.getName().equals(name)){
                    vs.setTarget(t);
                    break;
                }
            }
        }
        targetName=name;
    }
    public void update(Vector<Team> teams,Team owner){
        vs.teams=teams;
        vs.owner=owner;
        vs.initModels();
        setTargetName(targetName);
    }
    
    public Team getOwner(){return vs.owner;}
    
    public Team getTarget(){return vs.target;}
    
    @XmlAttribute(name="cooperationOn")
    public boolean isCooperationTesting() { return vs.testsCooperation.getValue(); }
    public void setCooperationTesting(boolean ct){vs.testsCooperation.setValue(ct);}
        
    public HashSet<Agent> getComplement(){ return vs.valueAgents.getComplement(); }
            
    @Override
    public String toString(){ return vs.type.toString() + " (" + vs.owner.toString() + ")"; }
    
    // CONSTANTS
    
    public static final int DIST_MIN = 0;
    public static final int DIST_MAX = 1;
    public static final int DIST_AVG = 2;
    public static final int AGENT_CLOSEST_TO_CAPTURE = 3;
    public static final int NUM_ACTIVE_AGENTS = 4;
    public static final int NUM_TEAM_SAFE = 5;
    public static final int NUM_ACTIVE_OPPONENTS = 6;
    public static final int NUM_OPPONENTS_CAPTURED = 7;
    public static final int AGENT_NUMBER_ADVANTAGE = 8;
    public static final int POSSIBLE_CAPTURES_NOT_MADE = 9;
    public static final int OPPONENTS_UNCAPTURED = 10;
    public static final int TIME_TOTAL = 11;
    public static final int TIME_SINCE_CAP = 12;
    public static final int PERCENT_OPPONENTS_CAPTURED = 13;
    
    public static final String[] typeStrings = {
        "Min. distance to target",      // 0
        "Max. distance to target",      // 1  
        "Avg. distance to target",      // 2
        "Agent closest to capture",     // 3
        "# active agents",              // 4
        "# agents reached safety",      // 5
        "# active opponents",           // 6
        "# opponents captured",         // 7
        "Player # advantage",           // 8
        "# possible captures not made", // 9
        "# opponents uncaptured",       // 10
        "Simulation time",              // 11
        "Time since capture",            // 12
        "Percent opponents captured"    // 13
    };    
    
    public String explain() { return explain(vs.type.getValue()); }
    public String explain(int type) {
        switch(type){
            case DIST_MIN:
                return "Represents minimum distance between team "+vs.owner+" and opposing team "+vs.target;
            case DIST_MAX:
                return "Represents the maximum distancec between team "+vs.owner+" and opposing team "+vs.target;
            case DIST_AVG:
                return "Represents the average distancec between team "+vs.owner+" and opposing team "+vs.target;
            case NUM_ACTIVE_AGENTS:
                return "Represents the number of agents on the team "+vs.owner;
            case NUM_TEAM_SAFE:
                return "Represents the number of agents on the team "+vs.owner+" which have reached safety";
            case NUM_ACTIVE_OPPONENTS:
                return "Represents the number of agents on the opposing team "+vs.target;
            case NUM_OPPONENTS_CAPTURED:
                return "Represents the number of opposing agents on team "+vs.target+" which have been captured by team "+vs.owner;
            case AGENT_NUMBER_ADVANTAGE:
                return "Represents the number of agents on team "+vs.target+" minus the number on opposing team "+vs.target;
            case TIME_TOTAL:
                return "Represents the total time of the simulation";
            case TIME_SINCE_CAP:
                return "Represents the time since a member of team "+vs.owner+" last captured a member of team "+vs.target;
            case POSSIBLE_CAPTURES_NOT_MADE:
                return "Returns the number of potential captures which can be made by team "+vs.owner+" of opponents on team "+vs.target
                        + ", minus the number of captures which have been made";
            case OPPONENTS_UNCAPTURED:
                return "Returns the size of team "+vs.target+" minuts the number of captures made by team "+vs.owner;
            case PERCENT_OPPONENTS_CAPTURED:
                return "Returns the percentage of opponents captured";
        }
        return "Type not supported";
    }
    
    
    // SUB-CLASS ValuationSettings
    
    /** Wrapper for the static settings of the goal. */
    public class ValuationSettings extends Settings {
        
        /** List of potential teams. */
        private Vector<Team> teams;
        /** The team owning this goal. */
        protected Team owner; 
        /** Subset of team measuring the goal. */
        private SubsetModel<Agent> valueAgents;
        /** Model for potential targets. */
        private StringRangeModel opponentModel;
        /** The target of this goal. */
        protected Team target;
        /** Whether this valuation is analyzed for "cooperation" */
        private BooleanModel testsCooperation = new BooleanModel(false);
    
        /** 
         * Another parameter to use in specifying the goal. Usually the "target"
         * distance required to reach the goal.
         */
        private DoubleRangeModel threshold = new DoubleRangeModel(1,0,1000,.1);
        /** Parameter describing the type of valuation. */
        private StringRangeModel type = new StringRangeModel(typeStrings,0,0,13);
        
        public ValuationSettings(Vector<Team> teams, Team owner, Team target, int type, double threshold){
            setName("Valuation");
            this.teams = teams;
            this.owner = owner;
            this.target = target;
            this.threshold.setValue(threshold);
            this.type.setValue(type);
            initModels();
        }
        
        /** Reinitializes all models. */
        void initModels() {
            super.removeAllElements();
            opponentModel = new StringRangeModel(Simulation.getTeamStrings(teams), teams.indexOf(target), 0, teams.size());
            add(new SettingsProperty("Opponent", opponentModel, Settings.EDIT_COMBO));
            add(new SettingsProperty("Threshhold", this.threshold, Settings.EDIT_DOUBLE));
            add(new SettingsProperty("Type", this.type, Settings.EDIT_COMBO));
            add(new SettingsProperty("Cooperation", this.testsCooperation, Settings.EDIT_BOOLEAN));
            valueAgents = new SubsetModel<Agent> (owner.agents);
            addGroup("Subset", valueAgents, Settings.EDIT_BOOLEAN_GROUP, "Select agents used for valuation.");
        }
        
        /** Listens for changes to settings */
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            String ac="teamSetupChange";
            if(evt.getSource()==opponentModel){
                setTarget(teams.get(opponentModel.getValue()));
            }
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
