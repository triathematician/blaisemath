/**
 * Agent.java
 * Created on Aug 28, 2007, 10:26:24 AM
 */

package simulation;

import behavior.ApproachPath;
import java.util.logging.Level;
import java.util.logging.Logger;
import scio.function.FunctionValueException;
import sequor.model.DoubleRangeModel;
import sequor.model.PointRangeModel;
import behavior.Behavior;
import goal.Task;
import utility.DistanceTable;
import scio.coordinate.R2;
import scio.coordinate.V2;
import javax.swing.event.ChangeEvent;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import java.util.Collection;
import javax.swing.event.EventListenerList;
import javax.swing.JPanel;
import behavior.TaskFusion;
import goal.Goal;
import goal.TaskGenerator;
import sequor.Settings;
import sequor.model.ColorModel;
import sequor.model.StringRangeModel;
import sequor.model.ParametricModel;
import sequor.SettingsProperty;

/**
 * Represents a single participant in the pursuit-evasion game. May be a pursuer, an evader, a stationary goal
 * or sensor, a universal knowledge control, etc.<br><br>
 *
 * Each is determined by several characteristic properties (Personality), its team (Team), its understanding
 * of the playing field (Pitch), and its memory (Vector of Pitch's).<br><br>
 * 
 * @author Elisha Peterson
 */
public class Agent implements TaskGenerator {
    
    // PROPERTIES
    
    /** Location */
    public V2 loc;
    
    /** Agent's settings */
    AgentSettings ags;
    
    /** Agent's current list of tasks (changes over time) */
    Vector<Task> tasks;
    /** Behavior corresponding to current task */
    Behavior myBehavior;
    
    /** Agent's initial position */
    PointRangeModel initialPosition;
    /** Agent's view of the playing field */
    Vector<Agent> pov;
    /** Communications regarding the playing field */
    Vector<Agent> commpov;
    
    /** Whether the agent is still in play. */
    boolean active=true;
    
    
    // CONSTRUCTORS
    
    /** Default constructor */
    public Agent(){
        super();
        loc=new V2();
        ags=new AgentSettings();
        initialize();
    }
    public Agent(R2 p){
        this();
        setPosition(p);
    }
    /** Constructs with a player's team only
     * @param team  the agent's team */
    public Agent(Team team){
        this();
        copySettingsFrom(team);
        initialize();
    }
    
    
    // METHODS: INITIALIZATION HELPERS

    /** First initialization of the agent. */
    public void initialize(){
        myBehavior=Behavior.getBehavior(getBehaviorType());
        tasks=new Vector<Task>();
        initialPosition=null;
        pov=new Vector<Agent>();
        commpov=new Vector<Agent>();
    }
    
    /** Resets before another run of the simulation. */
    public void reset(){
        setPosition(initialPosition.getX(),initialPosition.getY());
        tasks.clear();
        pov.clear();
        commpov.clear();
    }
    
    /** Moves the agent. */
    public void move(){setPosition(loc.getEnd());}
    
    public void copySettingsFrom(Team team){
        setSensorRange(team.getSensorRange());
        setCommRange(team.getCommRange());
        setTopSpeed(team.getTopSpeed());
        setBehavior(team.getBehavior());
        setLeadFactor(team.getLeadFactor());
        setColor(team.getColor());
    }
    
    
    // BEAN PATTERNS: GETTERS & SETTERS
    
    public Behavior getBehavior(){return myBehavior;}
    public R2 getPosition(){return loc.getStart();}
    private void setPosition(double newX,double newY){loc.x=newX;loc.y=newY;}
    private void setPosition(R2 newValue){loc.x=newValue.x;loc.y=newValue.y;}
    // See just before settings subclass for the rest!
    
    
    // BEAN PATTERNS: RESULTS
    
    /** Returns the initial position model
     * @return model with the agent's color at the initial position */
    public PointRangeModel getPointModel(){
        if(initialPosition==null){
            initialPosition=new PointRangeModel(loc.x,loc.y);
            initialPosition.addChangeListener(ags);
        }
        return initialPosition;
    }    
    
    // METHODS: TASKING
        
    /** Sets single task to seek/flee a given agent.
     * @param agent     the target agent of the task
     * @param g         the goal underlying the task
     * @param weight    the weighting of the task
     */
    public void assignTask(TaskGenerator tg,V2 agent,Goal g,double weight){
        if(agent!=null){
            tasks.add(new Task(tg,g,agent,weight));
        }
    }
    
    
    // METHODS DEALING WITH UNDERSTANDING OF PLAYING FIELD (POV)
    
    /** Gathers sensory data based on distance table.
     * @param dist the global table of distances */
    public void gatherSensoryData(DistanceTable dist){
        pov.clear();
        pov.addAll(dist.getAgentsInRadius(this,getSensorRange()));
    }
    /** Generates communications events based on sensory data that should be passed on to team members.
     * These events are sent to agents within communications range, who then adjust their understanding
     * of the playing field based on these comms. A sensory event is simply a collection of agents that
     * a given player sees. For the moment, it is just the single player's sensory events, and not those
     * stored in memory.
     * @param team the agent's team
     * @param dist the global table of distances */
    public void generateSensoryEvents(Team team,DistanceTable dist){
        for(Agent a:dist.getAgentsInRadius(this,team,getCommRange())){
            a.acceptSensoryEvent(pov);
        }
    }
    /** Accept a communication based on sensory data
     * @param agents list of agent positions communicated */
    public void acceptSensoryEvent(Collection<Agent> agents){commpov.addAll(agents);}
    
    /** Forms belief about the playing field by fusing own understanding
     * of playing field with that suggested by others. */
    public void fusePOV(){
        pov.addAll(commpov);
        commpov.clear();
    }
    
    /** Whether or not this agent can "see" another agent. */
    public boolean sees(Agent b){
        return pov.contains(b);
    }
    
    /**
     * Determines direction to proceed based on assigned myBehavior
     * @param time      the current time stamp
     * @param stepTime  the time between iterations
     */
    public void planPath(double time,double stepTime){
        if(myBehavior instanceof ApproachPath){
            loc.v=myBehavior.direction(this,null,time).multipliedBy(stepTime*getTopSpeed());
        }else{
            loc.v=TaskFusion.getVector(this,tasks,time).multipliedBy(stepTime*getTopSpeed());
        }
        if(java.lang.Double.isNaN(loc.v.x)){System.out.println("nan in path planning "+loc.v.toString()+" and pos x="+loc.x+" y="+loc.y);}
    }
    
    
    // EVENT HANDLING
    
    /** Indicates the initial position has changed. */
    public void stateChanged(ChangeEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    // Remaining code deals with action listening
    protected EventListenerList listenerList = new EventListenerList();
    public void addActionListener(ActionListener l){listenerList.add(ActionListener.class, l);}
    public void removeActionListener(ActionListener l){listenerList.remove(ActionListener.class, l);}
    protected void fireActionPerformed(String s){
        Object[] listeners=listenerList.getListenerList();
        for(int i=listeners.length-2;i>=0;i-=2){
            if(listeners[i]==ActionListener.class){
                ((ActionListener)listeners[i+1]).actionPerformed(new ActionEvent(this,ActionEvent.ACTION_PERFORMED,s));
            }
        }
    }
    
    
    // BEAN PATTERNS: INITIAL SETTINGS
    
    public R2 getInitialPosition(){return new R2(initialPosition.getX(),initialPosition.getY());}
    public double getSensorRange(){return ags.sensorRange.getValue();}
    public double getCommRange(){return ags.commRange.getValue();}
    public double getTopSpeed(){return ags.topSpeed.getValue();}
    public int getBehaviorType(){return ags.behavior.getValue();}
    public double getLeadFactor(){return ags.leadFactor.getValue();}
    public Color getColor(){return ags.color.getValue();}
    @Override
    public String toString(){return ags.toString();}
    public R2 getPositionTime(double t){
        try {
            return new R2(ags.pm.getValue(t));
        } catch (FunctionValueException ex) {
            return R2.Origin;
        }
    }
    public boolean isActive(){return active;}
    
    public void deactivate(){active=false;}
    public void setSensorRange(double newValue){ags.sensorRange.setValue(newValue);}
    public void setCommRange(double newValue){ags.commRange.setValue(newValue);}
    public void setTopSpeed(double newValue){ags.topSpeed.setValue(newValue);}
    public void setBehavior(int newValue){
        ags.behavior.setValue(newValue);
        if(newValue==Behavior.APPROACHPATH){}
    }
    public void setLeadFactor(double newValue){ags.leadFactor.setValue(newValue);}
    public void setColor(Color newValue){ags.color.setValue(newValue);}
    public void setString(String newValue){ags.setName(newValue);}
    public void setFixedPath(String xt,String yt){ags.pm.setXString(xt);ags.pm.setYString(yt);}
    
    public JPanel getPanel(){return ags.getPanel();}
    
    // SUBCLASSES
    
    /** Contains all the initial settings for the simulation. Everything else is used while the simulation is running. */
    private class AgentSettings extends Settings {
        
        /** Default sensor range [in ft]. */
        private DoubleRangeModel sensorRange=new DoubleRangeModel(20,0,5000,1.0);
        /** Default communications range [in ft]. */
        private DoubleRangeModel commRange=new DoubleRangeModel(50,0,5000,1.0);
        /** Default speed [in ft/s]. */
        private DoubleRangeModel topSpeed=new DoubleRangeModel(5,0,50,.05);
        /** Default behavioral setting */
        private StringRangeModel behavior=Behavior.getComboBoxModel();
        /** Lead factor if required for myBehavior */
        private DoubleRangeModel leadFactor=new DoubleRangeModel(0,0,2,.01);
        /** Position function if required for myBehavior */
        private ParametricModel pm=new ParametricModel("10cos(t)","10sin(t)");
        /** Default color. */
        private ColorModel color=new ColorModel(Color.BLUE);
        /** Returns the color */
        public Color getColor(){return color.getValue();}
        
        AgentSettings(){
            setName("Agent");
            add(new SettingsProperty("Speed",topSpeed,Settings.EDIT_DOUBLE));
            add(new SettingsProperty("Sensor Range",sensorRange,Settings.EDIT_DOUBLE));
            add(new SettingsProperty("Comm Range",commRange,Settings.EDIT_DOUBLE));
            add(new SettingsProperty("Behavior",behavior,Settings.EDIT_COMBO));
            add(new SettingsProperty("Lead Factor",leadFactor,Settings.NO_EDIT));
            add(new SettingsProperty("Position(t)",pm,Settings.NO_EDIT));
            add(new SettingsProperty("Color",color,Settings.EDIT_COLOR));
            initEventListening();
        }
        
        public void stateChanged(ChangeEvent e){
            if(e.getSource()==behavior){
                myBehavior=Behavior.getBehavior(behavior.getValue());
                if(behavior.getValue()==Behavior.LEADING){
                    setPropertyEditor("Lead Factor",Settings.EDIT_DOUBLE_SLIDER);
                    setPropertyEditor("Position(t)",Settings.NO_EDIT);
                }else if(behavior.getValue()==Behavior.APPROACHPATH){
                    setPropertyEditor("Position(t)",Settings.EDIT_PARAMETRIC);
                    setPropertyEditor("Lead Factor",Settings.NO_EDIT);
                }else{
                    setPropertyEditor("Position(t)",Settings.NO_EDIT);
                    setPropertyEditor("Lead Factor",Settings.NO_EDIT);
                }
                fireActionPerformed("agentBehaviorChange");
            }
            if(e.getSource()==color){
                fireActionPerformed("agentDisplayChange");
            }else{
                fireActionPerformed("agentSetupChange");
            }
        }
    }    
}
