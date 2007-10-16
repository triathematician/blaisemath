/**
 * Agent.java
 * Created on Aug 28, 2007, 10:26:24 AM
 */

package simulation;

import Euclidean.*;
import Model.*;
import javax.swing.event.ChangeEvent;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import javax.swing.event.EventListenerList;
import behavior.Behavior;
import behavior.Task;
import javax.swing.JPanel;
import specto.dynamicplottable.PointSet2D;
import utility.DistanceTable;

/**
 * @author Elisha Peterson<br><br>
 *
 * Represents a single participant in the pursuit-evasion game. May be a pursuer, an evader, a stationary goal
 * or sensor, a universal knowledge control, etc.<br><br>
 *
 * Each is determined by several characteristic properties (Personality), its team (Team), its understanding
 * of the playing field (Pitch), and its memory (ArrayList of Pitch's).
 */
public class Agent extends PVector {
    
    // PROPERTIES
    
    /** Agent's settings */
    AgentSettings ags;
    
    /** Agent's current list of tasks (changes over time) */
    ArrayList<Task> tasks;
    /** Behavior corresponding to current task */
    Behavior behavior;
    
    /** Agent's initial position */
    PointRangeModel initialPosition;
    /** Agent's path */
    PPath path;
    /** Agent's view of the playing field */
    ArrayList<Agent> pov;
    /** Communications regarding the playing field */
    ArrayList<Agent> commpov;
    
    
    // CONSTRUCTORS
    
    /** Default constructor */
    public Agent(){
        super();
        ags=new AgentSettings();
        initBehavior();
        initialPosition=null;
        initMemory();
    }
    public Agent(PPoint p){this();setPoint(p);}
    /** Constructs with a player's team only
     * @param team  the agent's team */
    public Agent(Team team){
        ags=new AgentSettings();
        copySettingsFrom(team);
        initBehavior();
        initialPosition=null;
        initMemory();
    }
    
    
    // METHODS: INITIALIZATION HELPERS
    
    /** Sets behavior given default settings */
    public void initBehavior(){
        behavior=Behavior.getBehavior(getBehavior());
        tasks=new ArrayList<Task>();
    }
    /** Initializes memory... path,pov,commpov,memory */
    public void initMemory(){
        path=new PPath();
        pov=new ArrayList<Agent>();
        commpov=new ArrayList<Agent>();
        remember();
    }
    /** Resets positions, memory, and paths (but not behavior) */
    public void reset(){
        setPoint(initialPosition.getPoint());
        initBehavior();
        initMemory();
    }
    public void copySettingsFrom(Team team){
        setSensorRange(team.getSensorRange());
        setCommRange(team.getCommRange());
        setTopSpeed(team.getTopSpeed());
        setBehavior(team.getBehavior());
        setLeadFactor(team.getLeadFactor());
        setColor(team.getColor());
    }
    
    
    // BEAN PATTERNS: GETTERS & SETTERS
    
    public PPoint getPosition(){return (PPoint)this;}
    public void setPosition(PPoint newValue){setPoint(newValue);}
    // See just before settings subclass for the rest!
    
    
    // BEAN PATTERNS: RESULTS
    
    /** Returns the agent's path
     * @return the path plotted so far */
    public PPath getPath(){return path;}
    /** Returns plottable path for the agent
     * @return a path which can be plotted */
    public PointSet2D getPlotPath(){return new PointSet2D(path,getColor());}
    /** Returns the initial position model
     * @return model with the agent's color at the initial position */
    public PointRangeModel getPointModel(){if(initialPosition==null){initialPosition=new PointRangeModel(this);initialPosition.addChangeListener(ags);}return initialPosition;}
    
    
    // METHODS: TASKING
        
    /** Sets single task to seek/flee a given agent. Priority is automatically set to 1.
     * @param agent the target agent of the task
     * @param type  0 if trivial (ignored, 1 if pursue, 2 if evade */
    public void assignTask(PVector agent,int type){
        tasks.clear();
        if(type!=0){tasks.add(new Task(agent,type,1));}
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
    public boolean sees(Agent b){return pov.contains(b);}
    
    // TODO implement task fusion here!
    /**
     * Determines direction to proceed based on assigned behavior
     * @param time      the current time stamp
     * @param stepTime  the time between iterations
     */
    public void planPath(double time,double stepTime){
        if(tasks.isEmpty()){v=behavior.direction(this,null,time).multipliedBy(stepTime*getTopSpeed());           
        }else{v=behavior.direction(this,tasks.get(0).getTarget(),time).multipliedBy(stepTime*getTopSpeed());
        }
    }
    
    /** Logs current point and pitch in memory, and resets current understanding. */
    public void remember(){
        path.add(new PPoint(this));
        //memory.add(0,pov);
        //pov.clear();
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
    
    public double getSensorRange(){return ags.sensorRange.getValue();}
    public double getCommRange(){return ags.commRange.getValue();}
    public double getTopSpeed(){return ags.topSpeed.getValue();}
    public int getBehavior(){return ags.behavior.getValue();}
    public double getLeadFactor(){return ags.leadFactor.getValue();}
    public Color getColor(){return ags.color.getValue();}
    public String toString(){return ags.s;}
    public PPoint getPositionTime(double t){return ags.pm.getValue(t);}
    
    public void setSensorRange(double newValue){ags.sensorRange.setValue(newValue);}
    public void setCommRange(double newValue){ags.commRange.setValue(newValue);}
    public void setTopSpeed(double newValue){ags.topSpeed.setValue(newValue);}
    public void setBehavior(int newValue){
        ags.behavior.setValue(newValue);
        if(newValue==Behavior.FIXEDPATH){}
    }
    public void setLeadFactor(double newValue){ags.leadFactor.setValue(newValue);}
    public void setColor(Color newValue){ags.color.setValue(newValue);}
    public void setString(String newValue){ags.s=newValue;}
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
        private ComboBoxRangeModel behavior=Behavior.getComboBoxModel();
        /** Lead factor if required for behavior */
        private DoubleRangeModel leadFactor=new DoubleRangeModel(0,0,5,.01);
        /** Position function if required for behavior */
        private ParametricModel pm=new ParametricModel();
        /** Default color. */
        private ColorModel color=new ColorModel(Color.BLUE);
        /** Display string */
        private String s="Agent";
        
        AgentSettings(){
            addProperty("Speed",topSpeed,Settings.EDIT_DOUBLE);
            addProperty("Sensor Range",sensorRange,Settings.EDIT_DOUBLE);
            addProperty("Comm Range",commRange,Settings.EDIT_DOUBLE);
            addProperty("Behavior",behavior,Settings.EDIT_COMBO);
            addProperty("Lead Factor",leadFactor,Settings.NO_EDIT);
            addProperty("Position(t)",pm,Settings.NO_EDIT);
            addProperty("Color",color,Settings.EDIT_COLOR);
            initEventListening();
        }
        
        public void stateChanged(ChangeEvent e){
            if(e.getSource()==behavior){
                initBehavior();
                if(behavior.getValue()==Behavior.PURSUIT_LEADING){
                    setPropertyEditor("Lead Factor",Settings.EDIT_DOUBLE);
                    setPropertyEditor("Position(t)",Settings.NO_EDIT);
                }else if(behavior.getValue()==Behavior.FIXEDPATH){
                    setPropertyEditor("Position(t)",Settings.EDIT_PARAMETRIC);
                    setPropertyEditor("Lead Factor",Settings.NO_EDIT);
                }else{
                    setPropertyEditor("Position(t)",Settings.NO_EDIT);
                    setPropertyEditor("Lead Factor",Settings.NO_EDIT);
                }
            }
            if(e.getSource()==color){fireActionPerformed("agentDisplayChange");
            } else{fireActionPerformed("agentSetupChange");}
        }
    }    
}
