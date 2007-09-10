/**
 * Agent.java
 * Created on Aug 28, 2007, 10:26:24 AM
 */

package agent;

import Blaise.BClickablePoint;
import Blaise.BPlotPath2D;
import Euclidean.PPath;
import Euclidean.PPoint;
import java.beans.PropertyChangeEvent;
import pursuitevasion.Pitch;
import Euclidean.PVector;
import Model.PointRangeModel;
import behavior.Behavior;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import javax.swing.event.EventListenerList;
import task.Task;
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
public class Agent extends PVector implements PropertyChangeListener {
    
    
// PROPERTIES
    
    /** Properties of the agent... this doesn't change during a simulation. */
    AgentSettings as;
    /** Pointer to player's team */
    Team team;
    
    /** Agent's current list of tasks (changes over time) */
    ArrayList<Task> tasks;
    /** Behavior corresponding to current task */
    Behavior behavior;
    
    /** Agent's initial position */
    PointRangeModel initialPosition;
    /** Agent's path */
    PPath path;
    /** Agent's view of the playing field */
    Pitch pov;
    /** Communications regarding the playing field */
    Pitch commpov;
    /** Agent's memory of the playing field */
    ArrayList<Pitch> memory;

    
// CONSTRUCTORS
    
    /** Default constructor */
    public Agent(){this(new AgentSettings());}
    /** Constructs with agent settings class
     * @param as the agent settings to use */
    public Agent(AgentSettings as){
        this.as=as;
        this.as.addPropertyChangeListener(this);
        team=toTeam();
        initBehavior();
        initialPosition=new PointRangeModel(this);
        initMemory();
    }
    public Agent(PPoint p){this();x=p.x;y=p.y;}
    /** Constructs with a player's team only
     * @param team  the agent's team */
    public Agent(Team team){
        this.team=team;
        as=new AgentSettings(team.ts);
        as.addPropertyChangeListener(this);
        initBehavior();
        initialPosition=new PointRangeModel(this);
        initMemory();
    }

    
// METHODS: INITIALIZATION HELPERS
    
    /** Sets behavior given default settings */
    public void initBehavior(){
        behavior=Behavior.getBehavior(as.getBehavior());
        tasks=new ArrayList<Task>();
    }
    /** Initializes memory... path,pov,commpov,memory */
    public void initMemory(){
        path=new PPath();
        pov=new Pitch();
        commpov=new Pitch();
        memory=new ArrayList<Pitch>();
        remember();
    }
    /** Resets positions, memory, and paths (but not behavior) */
    public void reset(){
        this.setPoint(initialPosition.getPoint());
        initMemory();
    }

// BEAN PATTERNS: GETTERS & SETTERS

    /** Returns the agent's path
     * @return the path plotted so far */
    public PPath getPath(){return path;}    
    /** Returns plottable path for the agent
     * @return a path which can be plotted */
    public BPlotPath2D getPlotPath(){return new BPlotPath2D(path,as.getColor());}
    /** Returns the initial position model 
     * @return model with the agent's color at the initial position */
    public PointRangeModel getPointModel(){return initialPosition;}
    
// TASK ADJUSTMENT METHODS
    
    /** Removes all tasks from list */
    public void clearTasks(){tasks.clear();}
    
    /** Sets single task to seek/flee a given agent. Priority is automatically set to 1.
     * @param agent the target agent of the task
     * @param type  0 if trivial (ignored, 1 if pursue, 2 if evade */
    public void assignTask(Agent agent,int type){
        clearTasks();
        if(type!=0){tasks.add(new Task(agent,type,1));}
    }
    

// CONVERSION METHODS
    
    /** Generates team with this single agent
     * @return team with this agent only. */
    public Team toTeam(){
        Team team=new Team();
        team.add(this);
        return team;
    }
    
    
// METHODS DEALING WITH UNDERSTANDING OF PLAYING FIELD (POV)
    
    /** Gathers sensory data based on distance table. 
     * @param dist the global table of distances */
    public void gatherSensoryData(DistanceTable dist){
        pov=new Pitch();
        // TODO implement error in data
        pov.addAll(dist.getAgentsInRadius(this,as.getSensorRange()));
    }
    /** Generates communications events based on sensory data that should be passed on to team members. 
     * These events are sent to agents within communications range, who then adjust their understanding
     * of the playing field based on these comms. A sensory event is simply a collection of agents that
     * a given player sees. For the moment, it is just the single player's sensory events, and not those
     * stored in memory.
     * @param dist the global table of distances */
    public void generateSensoryEvents(DistanceTable dist){
        for(Agent a:dist.getAgentsInRadius(this,team,as.getCommRange())){
            a.acceptSensoryEvent(pov);
        }
    }
    /** Accept a communication based on sensory data 
     * @param agents list of agent positions communicated */
    public void acceptSensoryEvent(Collection<Agent> agents){
        if(commpov==null){commpov=new Pitch();}
        commpov.addAll(agents);
    }
    
    /** Forms belief about the playing field by fusing own understanding
     * of playing field with that suggested by others. */
    public void fusePOV(){
        // TODO improve the functionality here!! use more advanced techniques!!
        pov.addAll(commpov);
        commpov.clear();
    }
    
    // TODO implement task fusion here!
    /**
     * Determines direction to proceed based on assigned behavior
     * @param time      the current time stamp
     * @param stepTime  the time between iterations
     */
    public void planPath(double time,double stepTime){
        if(tasks.isEmpty()){return;}
        this.v=behavior.direction(this,tasks.get(0).getTarget(),time).multipliedBy(stepTime*as.getTopSpeed());
    }
    
    /** Logs current point and pitch in memory, and resets current understanding. */
    public void remember(){
        path.add(new PPoint(x,y));
        memory.add(0,pov);
        pov.clear();
    }

    
// EVENT LISTENING    
    
    /** Listens for changes to settings */
    public void propertyChange(PropertyChangeEvent evt){
        //System.out.println("agent prop change: "+evt.getPropertyName());
        if(evt.getPropertyName()=="Speed"){fireActionPerformed(new ActionEvent(this,ActionEvent.ACTION_PERFORMED,"rerun"));}
        else if(evt.getPropertyName()=="Sensor Range"){fireActionPerformed(new ActionEvent(this,ActionEvent.ACTION_PERFORMED,"rerun"));}
        else if(evt.getPropertyName()=="Comm Range"){fireActionPerformed(new ActionEvent(this,ActionEvent.ACTION_PERFORMED,"rerun"));}
        else if(evt.getPropertyName()=="Behavior"){
            initBehavior();
            fireActionPerformed(new ActionEvent(this,ActionEvent.ACTION_PERFORMED,"rerun"));
        }
        else if(evt.getPropertyName()=="Color"){fireActionPerformed(new ActionEvent(this,ActionEvent.ACTION_PERFORMED,"redraw"));}
    }
    
    // Remaining code deals with action listening
    protected ActionEvent actionEvent = null;
    protected EventListenerList listenerList = new EventListenerList();
    public void addActionListener(ActionListener l){listenerList.add(ActionListener.class, l);}
    public void removeActionListener(ActionListener l){listenerList.remove(ActionListener.class, l);}
    protected void fireActionPerformed(ActionEvent e){
        Object[] listeners=listenerList.getListenerList();
        for(int i=listeners.length-2;i>=0;i-=2){
            if(listeners[i]==ActionListener.class){
                if(actionEvent==null){actionEvent=e;}
                ((ActionListener)listeners[i+1]).actionPerformed(actionEvent);
            }
        }
    }
}
