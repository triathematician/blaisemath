/**
 * Agent.java
 * Created on Aug 28, 2007, 10:26:24 AM
 */

package simulation;

import behavior.ApproachPath;
import sequor.model.DoubleRangeModel;
import sequor.model.PointRangeModel;
import behavior.Behavior;
import tasking.Task;
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
import behavior.TaskFusion;
import behavior.TrustMap;
import javax.swing.JPanel;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import metrics.Goal;
import scio.function.FunctionValueException;
import tasking.TaskGenerator;
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
@XmlAccessorType(XmlAccessType.NONE)
public class Agent {
    
    // STATIC PROPERTIES
        
    /** Agent's settings */
    AgentSettings ags;    
    /** Agents trusted by this agent */
    TrustMap trusted;
    /** Task generators associated with this agent */
    Vector<TaskGenerator> taskGenerators;
    
    // CHANGING PROPERTIES
    
    /** Location */
    public V2 loc;    
    /** Agent's current list of tasks (changes over time) */
    Vector<Task> tasks;    
    /** Behavior corresponding to current task */
    Behavior myBehavior;
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

    /** Constructs with a given team and specified name. */
    Agent(Team team, String name) {
        this(team);
        setName(name);
    }

    
    // PROPERTY INTERFACE METHODS
    
    public void addTaskGenerator(TaskGenerator tag){taskGenerators.add(tag);}
    public Vector<TaskGenerator> getTaskGenerators(){return taskGenerators;}    
    
    
    // METHODS: INITIALIZATION HELPERS

    /** First initialization of the agent. */
    public void initialize(){
        myBehavior=Behavior.getBehavior(getBehaviorCode());
        tasks=new Vector<Task>();
        pov=new Vector<Agent>();
        commpov=new Vector<Agent>();
        taskGenerators=new Vector<TaskGenerator>();
    }
    
    /** Resets before another run of the simulation. */
    public void initStateVariables(){
        setPosition(getX(),getY());
        tasks.clear();
        pov.clear();
        commpov.clear();
        myBehavior.reset();
    }
    
    /** Moves the agent. */
    public void move(){setPosition(loc.getEnd());}
    
    public void copySettingsFrom(Team team){
        setSensorRange(team.getSensorRange());
        setCommRange(team.getCommRange());
        setTopSpeed(team.getTopSpeed());
        setBehaviorCode(team.getBehavior());
        setLeadFactor(team.getLeadFactor());
        setColorValue(team.getColorModel().getValue());
    }
    
    
    
    // BEAN PATTERNS: RESULTS
    
    /** Returns the initial position model
     * @return model with the agent's color at the initial position */
    public PointRangeModel getPointModel(){
        return new PointRangeModel(ags.startX,ags.startY);
    }    
    
    // METHODS: TASKING
        
//    /** Sets single task to seek/flee a given agent.
//     * @param agent     the target agent of the task
//     * @param g         the goal underlying the task
//     * @param weight    the weighting of the task
//     */
//    public void assignTask(TaskGenerator tg,V2 agent,double weight){
//        if(agent!=null){
//            tasks.add(new Task(tg,this,agent,weight));
//        }
//    }
    
    public void assign(Task t){
        tasks.add(t);
        //System.out.println(this+" assigned "+t);
    }

    public void generateTasks(Team team,DistanceTable table, double priority) {
        for(TaskGenerator tag:taskGenerators){
            tag.generate(team.getActiveAgents(), table, priority);
        }
    }
    
    
    // METHODS DEALING WITH UNDERSTANDING OF PLAYING FIELD (POV)
    
    /** Gathers sensory data based on distanceTo table.
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
        for(Agent a:dist.getAgentsInRadius(this, team.getActiveAgents(), getCommRange())){
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
    
    // BEAN PATTERNS: GETTERS & SETTERS
        
    @XmlAttribute
    public String getName(){return ags.getName();}
    public void setName(String newValue){ags.setName(newValue);}
    
    /** Returns initial starting location of the agent. */
    //@XmlElement(name="start")
    //@XmlJavaTypeAdapter(MyR2Adapter.class)
    public R2 getInitialPosition(){return new R2(getX(),getY());}
    public void setInitialPosition(R2 point){getPointModel().setTo(point);}
    public void setInitialPosition(double x,double y){getPointModel().setTo(x,y);}
    
    @XmlAttribute
    public double getX(){return ags.startX.getDValue();}
    public void setX(double x){ags.startX.setValue(x);}
    
    @XmlAttribute
    public double getY(){return ags.startY.getDValue();}
    public void setY(double y){ags.startY.setValue(y);}
    
    @XmlAttribute
    public double getSensorRange(){return ags.sensorRange.getValue();}
    public void setSensorRange(double newValue){ags.sensorRange.setValue(newValue);}
    
    @XmlAttribute
    public double getCommRange(){return ags.commRange.getValue();}
    public void setCommRange(double newValue){ags.commRange.setValue(newValue);}
    
    @XmlAttribute
    public double getTopSpeed(){return ags.topSpeed.getValue();}
    public void setTopSpeed(double newValue){ags.topSpeed.setValue(newValue);}

    @XmlAttribute
    public double getLeadFactor(){return ags.leadFactor.getValue();}
    public void setLeadFactor(double newValue){ags.leadFactor.setValue(newValue);}

    @XmlAttribute
    public String getColor(){return ags.color.getHexString();}
    public void setColor(String s){ags.color.setHexString(s);}
    
    //@XmlElement(name="color")
    public ColorModel getColorModel(){return ags.color;}
    public void setColorModel(ColorModel cm) { ags.color.copyValuesFrom(cm); }  
    
    public void setColorValue(Color newValue){ags.color.setValue(newValue);}
    
    @XmlAttribute(name="behaviorCode")
    public int getBehaviorCode(){return ags.behavior.getValue();}
    public void setBehaviorCode(int newValue){
        ags.behavior.setValue(newValue);
        if(newValue==Behavior.APPROACHPATH){}
    }
    
    public Behavior getBehavior(){return myBehavior;}
    
    /** Returns current position. */
    public R2 getPosition(){return loc.getStart();}
    private void setPosition(R2 newValue){loc.x=newValue.x;loc.y=newValue.y;}
    private void setPosition(double newX,double newY){loc.x=newX;loc.y=newY;}   
        
    @Override
    public String toString(){return getName();}
    
    public R2 getPositionTime(double t){
        try {
            return new R2(ags.pm.getValue(t));
        } catch (FunctionValueException ex) {
            return R2.ORIGIN;
        }
    }
    
    public boolean isActive(){return active;}    
    public void deactivate(){active=false;}
    
    public void setFixedPath(String xt,String yt){ags.pm.setXString(xt);ags.pm.setYString(yt);}
    
    public JPanel getPanel(){return ags.getPanel();}
    
    // SUBCLASSES
    
    /** Contains all the initial settings for the simulation. Everything else is used while the simulation is running. */
    private class AgentSettings extends Settings {
        
        /** Starting Position. */
        private DoubleRangeModel startX=new DoubleRangeModel(0.0,-500.0,500.0,1.0);
        /** Starting Position. */
        private DoubleRangeModel startY=new DoubleRangeModel(0.0,-500.0,500.0,1.0);
        
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
            add(new SettingsProperty("Lead Factor",leadFactor,Settings.EDIT_DOUBLE_SLIDER));
            add(new SettingsProperty("Position(t)",pm,Settings.NO_EDIT));
            add(new SettingsProperty("Color",color,Settings.EDIT_COLOR));
            add(new SettingsProperty("x",startX,Settings.EDIT_DOUBLE));
            add(new SettingsProperty("y",startY,Settings.EDIT_DOUBLE));
        }
        
        @Override
        public void stateChanged(ChangeEvent e){
            if(e.getSource()==behavior){
                myBehavior=Behavior.getBehavior(behavior.getValue());
                if(behavior.getValue()==Behavior.APPROACHPATH){
                    setPropertyEditor("Position(t)",Settings.EDIT_PARAMETRIC);
                }else{
                    setPropertyEditor("Position(t)",Settings.NO_EDIT);
                }
                fireActionPerformed("agentBehaviorChange");
            }
            if(e.getSource()==color){
                fireActionPerformed("agentDisplayChange");
            // initial positions of players has changed
            }else{
                fireActionPerformed("agentSetupChange");
            }
        }
    }    
}