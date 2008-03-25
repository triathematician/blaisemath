/*
 * Team.java
 * Created on Aug 28, 2007, 10:26:33 AM
 */

// TODO Add team select box to goal settings panel
// TODO Move starting location initializers to outside utility class
// TODO Consolidate all "broadcast" methods

package simulation;

import scio.function.FunctionValueException;
import sequor.model.DoubleRangeModel;
import goal.Goal;
import behavior.*;
import utility.DistanceTable;
import goal.TaskGenerator;
import java.beans.PropertyChangeEvent;
import java.util.Vector;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.HashSet;
import java.util.Vector;
import javax.swing.JPanel;
import javax.swing.event.EventListenerList;
import sequor.Settings;
import sequor.model.ColorModel;
import sequor.model.StringRangeModel;
import sequor.model.IntegerRangeModel;
import sequor.model.ParametricModel;
import sequor.SettingsProperty;
import scio.coordinate.R2;
import utility.StartingPositionsFactory;

/**
 * @author Elisha Peterson<br><br>
 *
 * Handles routines dealing with an entire team of players.
 * Includes cooperative/control algorithms, broadcast methods for instructing team's agents, etc.
 */
public class Team extends Vector<Agent> implements TaskGenerator,ActionListener,PropertyChangeListener {
        
    // PROPERTIES
    
    /** The team's settings */
    public TeamSettings tes;
    /** The team's goals */
    private Vector<Goal> goals;
    /** The goals which involve "capturing" */
    private HashSet<Goal> captureGoals;
    /** Active agents */
    private HashSet<Agent> activeAgents;
    /** Agents which start the simulation as active. */
    private HashSet<Agent> startAgents;
    /** Agents used to measure a team's value function. */
    private HashSet<Agent> valueAgents;
    /** Active agents used to measure team's value function. */
    private HashSet<Agent> activeValueAgents;
    
    /** Whether to forward action events */
    private boolean editing=false;
    /** Result of the simulation if it should be recorded. Defaults to time at which goal is reached. */
    private double value;
    
    
    
    // CONSTRUCTORS
    public Team(){
        super();
        initLists();
        tes=new TeamSettings();
    }
    public Team(Team team){this(team.tes);}    
    Team(TeamSettings tes){
        super();
        value=Double.MAX_VALUE;
        initLists();
        this.tes=tes;
        initAgentNumber();
    }
    public Team(int size,int start,int behavior,Color color){
        super();
        editing=true;
        value=Double.MAX_VALUE;
        initLists();
        tes=new TeamSettings();
        setSize(size);
        setStart(start);
        setBehavior(behavior);
        setColor(color);
        clear();
        initAgentNumber();
        editing=false;
    }
    
    
    // METHODS: HELP FOR INITIALIZAITON
    
    /** Initializes all the vectors. */
    public void initLists(){
        goals=new Vector<Goal>();
        captureGoals=new HashSet<Goal>();
        activeAgents=new HashSet<Agent>();
        startAgents=new HashSet<Agent>();
        valueAgents=new HashSet<Agent>();
        activeValueAgents=new HashSet<Agent>();
    }
    
    /** Adds a goal. */        
    public void addGoal(double weight,Team target,int type,int tasking,double threshhold){
        Goal g=new Goal(weight,this,target,type,tasking,threshhold);
        goals.add(g);
        tes.addChild(g.gs,Settings.PROPERTY_INDEPENDENT);
        if(g.getType()==Goal.CAPTURE){captureGoals.add(g);}
    }
        
    /** Resets all agents to their initial positions; clears all paths */
    public void reset(){
        value=Double.MAX_VALUE;
        activeAgents.clear();
        activeAgents.addAll(startAgents);
        activeValueAgents.clear();
        activeValueAgents.addAll(valueAgents);
        for(Agent a:this){
            a.reset();
        }
        for(Goal g:goals){
            g.reset();
        }
    }
    
    /** Activate all. */
    public void initAllActive(){
        startAgents.clear();
        startAgents.addAll(this);
        activeAgents.clear();
        activeAgents.addAll(startAgents);
        valueAgents.clear();
        valueAgents.addAll(startAgents);
        activeValueAgents.clear();
        activeValueAgents.addAll(valueAgents);        
    }
    
    /** Changes the number of agents, resets starting locations. */
    public void initAgentNumber(){
        editing=true;
        while(size()>getSize()){
            Agent a=get(size()-1);
            a.removeActionListener(this);
            tes.removeChild(a.ags);
            remove(size()-1);
        }
        while(size()<getSize()){
            Agent a=new Agent(this);
            a.setString("Agent "+(size()+1));
            add(a);
            a.addActionListener(this);
            tes.addChild(a.ags,Settings.PROPERTY_INDEPENDENT);
        }
        initAllActive();
        editing=false;
    }
    
    /** Re-initializes agent starting locations. */
    public void initStartingLocations(double pitchSize){
        editing=true;
        switch(getStart()){
        case START_RANDOM: StartingPositionsFactory.startRandom(this,pitchSize); break;
        case START_LINE: StartingPositionsFactory.startLine(this,new R2(-pitchSize,pitchSize),new R2(pitchSize,-pitchSize)); break;
        case START_CIRCLE: StartingPositionsFactory.startCircle(this,new R2(),pitchSize);break;
        case START_ARC: StartingPositionsFactory.startArc(this,new R2(),pitchSize,Math.PI/3,5*Math.PI/3);break;
        default: StartingPositionsFactory.startZero(this); break;
        }
        editing=false;
    }
    
    
    // BEAN PATTERNS: GETTERS & SETTERS
    
    /** Returns center of mass of the team
     * @return center of mass */
    public R2 getCenterOfMass(){
        if (this.size()==0){return null;}
        R2 center=new R2(0,0);
        for(Agent agent:activeAgents){
            center.translate(agent.loc);
            //System.out.println("agent:"+agent.x+"+"+agent.y);
        }
        center.multiplyBy(1.0/this.size());
        //System.out.println("center:"+center.x+"+"+center.y);
        return center;
    }
    
   
//    /** Generates tree given list of goals and agents */
//    public DefaultMutableTreeNode getTreeNode(){
//        DefaultMutableTreeNode result=new DefaultMutableTreeNode(this);
//        for(Goal g:goals){result.add(new DefaultMutableTreeNode(g));}
//        for(Agent agent:this){result.add(new DefaultMutableTreeNode(agent));}
//        return result;
//    }
    
    /** Returns all goals which require capture checks.
     * @return      Vector of Goals representing those which involve capturing and removing player from the field.
     */
    public HashSet<Goal> getCaptureGoals(){return captureGoals;}
    
    /** Returns list of active agents.
     * @return      Vector of agents which are still active in the simualtion
     */
    public HashSet<Agent> getActiveAgents(){return activeAgents;}
    public HashSet<Agent> getStartAgents(){return startAgents;}
    public void setStartAgents(HashSet<Agent> agents){startAgents=agents;}
    public HashSet<Agent> getValueAgents(){return valueAgents;}
    public void setValueAgents(HashSet<Agent> agents){valueAgents=agents;}
    public HashSet<Agent> getActiveValueAgents(){return activeValueAgents;}
    
    
    
    // BROADCAST METHODS: PASS INSTRUCTIONS ONTO TEAM MEMBERS
    
    /** Instructs all agents to gather sensory data
     * @param d The global table of distances */
    public void gatherSensoryData(DistanceTable d){
        for(Agent a:activeAgents){
            a.gatherSensoryData(d);
        }
    }
    /** Instructs all agents to generate communication events for other players
     * @param d The global table of distances */
    public void communicateSensoryData(DistanceTable d){
        for(Agent a:activeAgents){
            a.generateSensoryEvents(this,d);
        }
    }
    /** Tells each agent to form their belief regarding the playing field. */
    public void fuseAgentPOV(){
        for(Agent a:activeAgents){
            a.fusePOV();
        }
    }
    /** Assigns tasks to the agents. */
    public void assignTasks(){
        for(Agent a:activeAgents){
            a.tasks.clear();
        }
        for(Goal g:goals){
            g.assignTasks();
        }
    }
    /** Generates directions for each team member based on their task and myBehavior.
     * @param time      the current time stamp
     * @param stepTime  the time between iterations */
    public void planPaths(double time,double stepTime){
        for(Agent a:activeAgents){
            a.planPath(time,stepTime);
        }
    }
    /** Moves all agents on the team using their assigned directions. */
    public void move(){for(Agent a:activeAgents){a.move();}}
    /** Deactivates a particular agent. */
    public void deactivate(Agent a){
        activeAgents.remove(a);
        activeValueAgents.remove(a);
        a.deactivate();
    }
    
    
    // BOOLEAN-GENERATING METHODS TESTING FOR WHETHER GOAL HAS BEEN REACHED
    
    /**
     * Checks to see if default value of goal has changed.
     * @param d     the global distance table
     */
    public void checkGoal(DistanceTable d,double time){  
        if(goals.isEmpty()){return;}
        double newValue=0;
        for(Goal g:goals){
            newValue+=g.getValue(d);
        }
        value=newValue;
    }
    
    
    // EVENT LISTENING
    
    /** Receives and re-broadcasts action events from agents on the team */
    public void actionPerformed(ActionEvent evt){
        if(!editing){
            fireActionPerformed(evt.getActionCommand());
        }
    }
    
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

    public void propertyChange(PropertyChangeEvent evt) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    
    // BEAN PATTERNS FOR INITIAL SETTINGS
    
    public int getSize(){return tes.size.getValue();}
    public int getStart(){return tes.start.getValue();}
    public Collection<Goal> getGoals(){return goals;}
    @Override
    public String toString(){return tes.toString();}
    
    @Override
    public void setSize(int newValue){tes.size.setValue(newValue);}
    public void setStart(int newValue){tes.start.setValue(newValue);}
    
    public double getSensorRange(){return tes.sensorRange.getValue();}
    public double getCommRange(){return tes.commRange.getValue();}
    public double getTopSpeed(){return tes.topSpeed.getValue();}
    public int getBehavior(){return tes.behavior.getValue();}
    public double getLeadFactor(){return tes.leadFactor.getValue();}
    public Color getColor(){return tes.color.getValue();}
    public R2 getPositionTime(double t){
        try {
            return new R2(tes.pm.getValue(t));
        } catch (FunctionValueException ex) {
            return R2.Origin;
        }
}
    public Double getValue(){return value;}
    
    public void setSensorRange(double newValue){tes.sensorRange.setValue(newValue);}
    public void setCommRange(double newValue){tes.commRange.setValue(newValue);}
    public void setTopSpeed(double newValue){tes.topSpeed.setValue(newValue);}
    public void setBehavior(int newValue){tes.behavior.setValue(newValue);}
    public void setLeadFactor(double newValue){tes.leadFactor.setValue(newValue);}
    public void setColor(Color newValue){tes.color.setValue(newValue);}
    public void setString(String newValue){tes.setName(newValue);}
    public void setFixedPath(String xt,String yt){tes.pm.setXString(xt);tes.pm.setYString(yt);}
    
    public JPanel getPanel(){return tes.getPanel();}   
        
    // BROADCAST METHODS: CHANGE SETTINGS OF AGENTS ON TEAM
    
    public void copySpeedtoTeam(){for(Agent a:this){a.setTopSpeed(getTopSpeed());}}
    public void copySensorRangetoTeam(){for(Agent a:this){a.setSensorRange(getSensorRange());}}
    public void copyCommRangetoTeam(){for(Agent a:this){a.setCommRange(getCommRange());}}
    public void copyBehaviortoTeam(){for(Agent a:this){a.setBehavior(getBehavior());}}
    public void copyColortoTeam(){for(Agent a:this){a.setColor(getColor());}}
    public void copyLeadFactortoTeam(){for(Agent a:this){a.setLeadFactor(getLeadFactor());}}
    public void copyPathtoTeam(){for(Agent a:this){a.setFixedPath(tes.pm.getStringX(),tes.pm.getStringY());}}
    
    // CONSTANTS FOR INITIAL SETTINGS
    
    public static final int START_ZERO=0;
    public static final int START_RANDOM=1;
    public static final int START_LINE=2;
    public static final int START_CIRCLE=3;
    public static final int START_ARC=4;
    public static final String[] START_STRINGS={"All at Zero","Random Positions","Along a Line","Around a Circle","Along a Circular Arc"};
    
    
    // SUBCLASSES
    
    /** Contains all the initial settings for the simulation. Everything else is used while the simulation is running. */
    private class TeamSettings extends Settings {
        
        /** Team size */
        private IntegerRangeModel size=new IntegerRangeModel(3,1,100);
        /** Starting positions to use */
        private StringRangeModel start=new StringRangeModel(START_STRINGS,START_RANDOM,0,4);
        
        /** Default sensor range [in ft]. */
        private DoubleRangeModel sensorRange=new DoubleRangeModel(20,0,5000);
        /** Default communications range [in ft]. */
        private DoubleRangeModel commRange=new DoubleRangeModel(50,0,5000);
        /** Default speed [in ft/s]. */
        private DoubleRangeModel topSpeed=new DoubleRangeModel(5,0,50,.05);
        /** Default behavioral setting */
        private StringRangeModel behavior=Behavior.getComboBoxModel();
        /** Lead factor if required for myBehavior */
        private DoubleRangeModel leadFactor=new DoubleRangeModel(0,0,2,.01);
        /** Position function if required for myBehavior */
        private ParametricModel pm=new ParametricModel();
        /** Default color. */
        private ColorModel color=new ColorModel(Color.BLUE);
        /** Returns the color */
        @Override
        public Color getColor(){return color.getValue();}
        
        public TeamSettings(){
            add(new SettingsProperty("# Agents",size,Settings.EDIT_INTEGER));
            add(new SettingsProperty("Starting Loc",start,Settings.EDIT_COMBO));
            add(new SettingsProperty("Speed",topSpeed,Settings.EDIT_DOUBLE));
            add(new SettingsProperty("Sensor Range",sensorRange,Settings.EDIT_DOUBLE));
            add(new SettingsProperty("Comm Range",commRange,Settings.EDIT_DOUBLE));
            add(new SettingsProperty("Behavior",behavior,Settings.EDIT_COMBO));
            add(new SettingsProperty("Lead Factor",leadFactor,Settings.NO_EDIT));
            add(new SettingsProperty("Position(t)",pm,Settings.NO_EDIT));
            add(new SettingsProperty("Color",color,Settings.EDIT_COLOR));
            initEventListening();
        }
        
        /**public JPanel getPanel(){
            JPanel result=new JPanel();
            for(Component c:super.getPanel().getComponents()){result.add(c);}
            SpringUtilities.makeCompactGrid(result,result.getComponentCount()/2,2,5,5,5,5);
            return result;
        }*/
        
        /** Listens for changes to settings */
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if(editing){return;}
            String ac=null;
            if(evt.getSource()==behavior){
                copyBehaviortoTeam();
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
                ac="teamSetupChange";
            } else if(evt.getSource()==size){       initAgentNumber();      ac="teamAgentsChange";
            } else if(evt.getSource()==start){                              ac="teamSetupChange";
            } else if(evt.getSource()==goals){                              ac="teamSetupChange";
            } else if(evt.getSource()==topSpeed){   copySpeedtoTeam();      ac="teamSetupChange";
            } else if(evt.getSource()==sensorRange){copySensorRangetoTeam();ac="teamSetupChange";
            } else if(evt.getSource()==commRange){  copyCommRangetoTeam();  ac="teamSetupChange";
            } else if(evt.getSource()==leadFactor){ copyLeadFactortoTeam(); ac="teamSetupChange";
            } else if(evt.getSource()==pm){         copyPathtoTeam();       ac="teamSetupChange";
            } else if(evt.getSource()==color){      copyColortoTeam();      ac="teamDisplayChange";}
            fireActionPerformed(ac);
        }
    }
}
