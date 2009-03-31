/*
 * Tasking.java
 * Created on Nov 6, 2007, 2:59:04 PM
 */

package tasking;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.util.Vector;
import javax.swing.JPanel;
import javax.swing.event.EventListenerList;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import sequor.Settings;
import sequor.SettingsProperty;
import sequor.model.DoubleRangeModel;
import sequor.model.StringRangeModel;
import simulation.Agent;
import simulation.Simulation;
import simulation.Team;

/**
 * Interface used by Agent,Team,etc saved for consistency in generating tasks.
 * <br><br>
 * @author ae3263
 */
@XmlAccessorType(XmlAccessType.NONE)
public class Tasking {

    // CONSTANTS

    public static final int SEEK = 0;
    public static final int CAPTURE = 1;
    public static final int FLEE = 2;
    public static final String[] TYPE_STRINGS = {"Seek", "Capture", "Flee"};

    public static final int NO_TASKING=0;
    public static final int AUTO_CLOSEST=1;
    public static final int AUTO_TWO_LINE=2;
    public static final int AUTO_FARTHEST=3;
    public static final int AUTO_COM=4;
    public static final int AUTO_DIR_COM=5;
    public static final int AUTO_GRADIENT=6;
    public static final int CONTROL_CLOSEST=7;
    public static final int CONTROL_OPTIMAL=8;
    public static final String[] TASKING_STRINGS=
    {"None","Autonomous Closest","Autonomous Two Closest","Autonomous Farthest",
     "Autonomous Center-of-Mass","Autonomous Center-of-Directions",
     "Autonomous Gradient","Control Closest", "Control Optimal"};

    // CONTROL VARIABLES

    /** Static settings. */
    public TaskSettings ts;

    // STATE VARIABLES

    /** TaskGenerator which actually generates the goals, based on the setting here. */
    public TaskGenerator tasker;
    /** Whether or not the goal has been achieved. */
    private boolean achieved = false;
    /** The agent which achieved the goal. */
    private Agent which = null;

    // CONSTRUCTORS

    /** Default constructor. */
    public Tasking(){
        ts = new TaskSettings();
        tasker = ts.getTasking();
    }

    /**
     * Default initializer requires several parameters.
     * @param weight    a constant factor (between 0 and 1) associated with the goal
     * @param owner     the team whose goal this is
     * @param target    the target of this goal
     * @param type      whether this is a "SEEK" goal or an "EVADE" goal
     * @param tasking   the type of tasking associated with this goal
     * @param thresh    specifies at what point the goal has been reached... sometimes a more generic parameter
     */
    public Tasking(double weight, Vector<Team> teams, Team owner, Team target, int type, int algorithm, double thresh) {
        ts = new TaskSettings(weight, teams, owner, target, type, algorithm, thresh);
        tasker = ts.getTasking();
        addActionListener(owner);
    }

    /** Constructs with a settings class.
     * @param s a settings class, hopefully of type "GoalSettings"
     */
    public Tasking(Settings s) {
        if(s instanceof TaskSettings){
            ts = (TaskSettings) s;
            tasker = ts.getTasking();
            addActionListener(ts.owner);
        }
    }

    // STATIC FACTORY METHODS

    /** Return class with desired tasking
     * @param tasking the tasking code
     * @return subclass of tasking with desired algorithm */
    public static TaskGenerator getTasking(Team team, int type, int algorithm){
        switch(algorithm){
            case AUTO_CLOSEST:
                return new AutoClosest(team, type);
            case AUTO_TWO_LINE:
                return new AutoTwoClosest(team, type);
            case AUTO_FARTHEST:
                return new AutoFarthest(team, type);
            case AUTO_COM:
                return new AutoCOM(team, type);
            case AUTO_DIR_COM:
                return new AutoDirectionCOM(team, type);
            case AUTO_GRADIENT:
                return new AutoGradient(team, type);
            case CONTROL_CLOSEST:
                return new ControlClosest(team, type);
            case CONTROL_OPTIMAL:
                return new ControlOptimal(team, type);
        }
        return null;
    }


    /**
     * Resets the goal after each simulation is run.
     */
    public void reset() {
        achieved = false;
        which = null;
    }

    /**
     * Assigns tasks to team based upon the tasking algorithm.
     */
//    public void assignTasks(DistanceTable table) {
//        if (tasker != null) {
//            tasker.generate(ts.owner.agents, table, getWeight());
//        }
//    }

    /**
     * String representation of the goal.
     */
    @Override
    public String toString() {
        return ts.toString();
    }

    // IMPLEMENT BASIC FUNCTIONALITY OF THE GOAL
    /** Tells eacch agent in the specified team to generate tasks with the instructions given
     * in the "tasker"
     * @param team The collection of agents which will generate the tasks
     * @param table The table off distances
     * @param priority The priority weighting to apply to the tasks
     */
//    public void generate(Collection<Agent> team, DistanceTable table, double priority) {
//        tasker.generate(team, table, priority * getWeight());
//    }

    // BEAN PATTERNS

    @XmlAttribute
    public int getTaskType() { return ts.type.getValue(); }
    public void setTaskType(int newValue) { ts.type.setValue(newValue); }

    @XmlAttribute
    public int getAlgorithm() { return ts.algorithm.getValue(); }
    public void setAlgorithm(int newValue) { ts.algorithm.setValue(newValue); }

    @XmlAttribute
    public double getWeight() { return ts.weight.getValue(); }
    public void setWeight(double newValue) { ts.weight.setValue(newValue); }

    @XmlAttribute
    public double getThreshhold() { return ts.threshhold.getValue(); }
    public void setThreshhold(double newValue) { ts.threshhold.setValue(newValue); }

    public String getOwnerName(){return ts.owner.getName();}

    String targetName="";

    @XmlAttribute(name="target")
    public String getTargetName(){return ts.target.getName();}
    public void setTargetName(String name){targetName=name;}
    public void update(Vector<Team> teams,Team owner){
        ts.teams=teams;
        ts.owner=owner;
        ts.initModels();
        for(Team t:ts.teams){
            if(t.getName().equals(targetName)){
                ts.setTarget(t);
                return;
            }
        }
        if(targetName.equals("")){ ts.setTarget(owner); targetName=owner.getName(); }
    }


    public Team getOwner() { return ts.owner; }
    public void setOwner(Team newValue) { ts.setOwner(newValue); }

    public Team getTarget() { return ts.target; }
    public void setTarget(Team newValue) { ts.setTarget(newValue); }

    public boolean isAchieved() { return achieved; }

    public Agent getAgent() { return which; }

    public JPanel getPanel() {
        return ts.getPanel();
    }


    // EVENT HANDLING

    boolean editing;
    // Remaining code deals with action listening
    protected EventListenerList listenerList = new EventListenerList();

    public void addActionListener(ActionListener l) {
        listenerList.add(ActionListener.class, l);
    }

    public void removeActionListener(ActionListener l) {
        listenerList.remove(ActionListener.class, l);
    }

    protected void fireActionPerformed(String s) {
        if (editing) {
            return;
        }
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ActionListener.class) {
                ((ActionListener) listeners[i + 1]).actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, s));
            }
        }
    }


    // SUB-CLASS GoalSettings

    /** Wrapper for the static settings of the task. */
    public class TaskSettings extends Settings {

        /** List of potential teams. */
        private Vector<Team> teams;
        /** Model for potential targets. */
        private StringRangeModel opponentModel;
        /** The team owning this task. */
        protected Team owner;
        /** The target of this task. */
        private Team target;
        /** The team's tasking algorithm default */
        private StringRangeModel algorithm=new StringRangeModel(Tasking.TASKING_STRINGS);
        /** Specifies whether task is seek, capture, or flee. */
        private StringRangeModel type = new StringRangeModel(TYPE_STRINGS, SEEK, 0, 2);
        /** Specifies the weighting/priority of the task. */
        private DoubleRangeModel weight = new DoubleRangeModel(1, 0, 1, .01);
        /**
         * Another parameter to use in specifying the goal. Usually the "target"
         * distance required to reach the goal.
         */
        private DoubleRangeModel threshhold = new DoubleRangeModel(1, 0, 1000, .1);
        /** Specifies the function describing whether the goal has been achieved. */
        //private Function<DistanceTable, Double> value;

        private TaskSettings() {
            this(1.0, new Vector<Team>(), new Team(), new Team(), SEEK, Tasking.AUTO_CLOSEST, 0.0);
        }

        public TaskSettings(double weight, Vector<Team> teams, Team owner, Team target, int type, int tasking, double threshhold) {
            setName("Tasking");
            this.teams = teams;
            this.weight.setValue(weight);
            this.owner = owner;
            this.target = target;
            targetName = target.getName();
            this.type.setValue(type);
            this.algorithm.setValue(tasking);
            this.threshhold.setValue(threshhold);
            initModels();
        }

        private void initModels() {
            super.removeAllElements();
            opponentModel = new StringRangeModel(Simulation.getTeamStrings(teams), teams.indexOf(target), 0, teams.size());
            add(new SettingsProperty("Opponent", opponentModel, Settings.EDIT_COMBO));
            add(new SettingsProperty("Tasking", this.algorithm, Settings.EDIT_COMBO));
            add(new SettingsProperty("Task Type", this.type, Settings.EDIT_COMBO));
            add(new SettingsProperty("Task Weight", this.weight, Settings.EDIT_DOUBLE));
            //add(new SettingsProperty("Task Cutoff", this.threshhold, Settings.EDIT_DOUBLE));
        }

        /** Listens for changes to settings */
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            String ac = null;
            if (evt.getSource() == weight) {
                ac = "teamSetupChange";
            } else if (evt.getSource() == type) {
                if (tasker != null) { tasker.type = type.getIValue(); }
                ac = "teamSetupChange";
            } else if (evt.getSource() == threshhold) {
                ac = "teamSetupChange";
            } else if (evt.getSource() == algorithm) {
                tasker = getTasking();
                ac = "teamSetupChange";
            } else if (evt.getSource() == opponentModel) {
                setTarget(teams.get(opponentModel.getValue()));
                targetName = target.getName();
                ac = "teamSetupChange";
            }
            fireActionPerformed(ac);
        }

        private TaskGenerator getTasking() {
            return Tasking.getTasking(target, type.getValue(), algorithm.getValue());
        }

        public void setOwner(Team newValue) {
            if (newValue != null && !newValue.equals(owner)) {
                owner = newValue;
                pcs.firePropertyChange("taskOwner", null, newValue);
            }
        }

        public void setTarget(Team newValue) {
            if (newValue != null && !newValue.equals(target)) {
                target = newValue;
                tasker.setTarget(target);
                opponentModel.setValue(target.toString());
                pcs.firePropertyChange("taskTarget", null, newValue);
            }
        }
    } // SUB-CLASS GoalSettings
}
