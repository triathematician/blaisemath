/*
 * Team.java
 * Created on Aug 28, 2007, 10:26:33 AM
 */// TODO Add team select box to goal settings panel
// TODO Move starting location initializers to outside utility class
// TODO Consolidate all "broadcast" methods
package simulation;

import sequor.event.DoubleRangeModel;
import metrics.*;
import behavior.*;
import utility.DistanceTable;
import java.beans.PropertyChangeEvent;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;
import javax.swing.JPanel;
import javax.swing.event.EventListenerList;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import sequor.Settings;
import sequor.model.ColorModel;
import sequor.model.StringRangeModel;
import sequor.model.IntegerRangeModel;
import sequor.model.ParametricModel;
import scio.coordinate.R2;
import scio.function.FunctionValueException;
import sequor.SettingsProperty;
import tasking.TaskGenerator;
import utility.StartingPositionsFactory;

/**
 * @author Elisha Peterson<br><br>
 *
 * Handles routines dealing with an entire team of players.
 * Includes cooperative/control algorithms, broadcast methods for instructing team's agents, etc.
 */
@XmlAccessorType(XmlAccessType.NONE)
public class Team implements ActionListener {
    
    //
    // CONTROL VARIABLES (define the simulation)
    //
        
    /** Global team settings */
    public TeamSettings tes;    
    /** Agent settings */
    public Settings ags;    
    /** Metrics settings group */
    public Settings mets;    
    /** Tasking settings group */
    public Settings tass;

    /** Stores initial positions (if specific) */
    R2[] positions;
        
    /** The agents on the team. */
    public Vector<Agent> agents;
    /** Control agent. */
    private Agent control;
    
    /** Capture conditions of the team. */
    @XmlElement(name="capture")
    public Vector<CaptureCondition> capture;    
    /** Value metrics of the team. */
    @XmlElement(name="metric")
    public Vector<Valuation> metrics;    
    /** Victory condition of the team. */
    @XmlElement(name="victory")
    public VictoryCondition victory;
    
    
    //
    // STATE VARIABLES (change during simulation)
    //
    
    /** Active agents */
    private HashSet<Agent> activeAgents;
    
    /** Agents which start the simulation as active. */
    private HashSet<Agent> startAgents;
    
    /** Whether to forward action events */
    private boolean editing = false;
    
    /** Result of the simulation if it should be recorded. Defaults to time at which goal is reached. */
    private double value = Double.MAX_VALUE;
   
    /** Stores number of agents which have been captured by opposing teams.
    * The value of this team itself represents the number which make it to "safety".
    */
    private HashMap<Team,Integer> captures;
    
    //
    // CONSTRUCTORS
    //
    
    /** Default constructor. */    
    public Team() {
        initControlVariables();
        initStateVariables();
        initAgentNumber();
    }

    /** Initializes based on another team's settings. */    
    public Team(Team t2) {
        initControlVariables();
        tes = t2.tes;
        initStateVariables();
        initAgentNumber();
    }

    /** Set up certain settings. */
    public void initSettings(String name, int size, int startCode, int behaviorCode, Color color) {
        if(name!=null){setName(name);}
        setAgentNumber(size);
        setStartCode(startCode);
        setBehavior(behaviorCode);
        setColorValue(color);   
    }
    
    //
    // METHODS: INITIALIZERS
    //
    
    /** Initializes settings. */
    public void initControlVariables() {
        tes = new TeamSettings();
        ags = new Settings("Agents");
        tes.addChild(ags, Settings.PROPERTY_INDEPENDENT);
        tass = new Settings("Taskings");
        tes.addChild(tass, Settings.PROPERTY_INDEPENDENT);
        mets = new Settings("Metrics");
        tes.addChild(mets, Settings.PROPERTY_INDEPENDENT);
              
        agents = new Vector<Agent>();
        control = new Agent();
        capture = new Vector<CaptureCondition>();
        metrics = new Vector<Valuation>();
    }
    
    /** Initializes all the vectors. */
    public void initStateVariables() {
        if(startAgents==null){ startAgents = new HashSet<Agent>(); }
        if(activeAgents==null){ activeAgents = new HashSet<Agent>(); }
        activeAgents.clear();
        activeAgents.addAll(startAgents);
        if(captures==null){ captures = new HashMap<Team,Integer>(); } else { captures.clear(); }
        for (Agent a : agents) { a.initStateVariables(); }
        if (victory != null) { victory.initStateVariables(); }
    }

    
    //
    // METHODS: ADDING ELEMENTS TO THE TEAM
    //
    
    /** Adds a goal. */
    public void addAutoGoal(double weight, Vector<Team> teams, Team target, int type, int tasking, double threshhold) {
        Goal g = new Goal(weight, teams, this, target, type, tasking, threshhold);
        addControlTask(g);
    }

    /** Adds a capture condition. */
    public void addCaptureCondition(Vector<Team> teams, Team target, double captureDistance, int removal) {
        addCaptureCondition(new CaptureCondition(teams, this, target, captureDistance, removal));
    }
    /** Adds a capture condition. */
    public void addCaptureCondition(CaptureCondition cc){
        capture.add(cc);
        tes.addChild(cc.vs, Settings.PROPERTY_INDEPENDENT);
    }

    /** Adds a valuation metric. */
    public void addValuation(Valuation val) {
        metrics.add(val);
        mets.addChild(val.vs, Settings.PROPERTY_INDEPENDENT);
    }

    /** Adds a "control agent". */
    public void addControlTask(TaskGenerator tag) {
        control.addTaskGenerator(tag);
        if(tag instanceof Goal){
            tass.addChild(((Goal)tag).gs, Settings.PROPERTY_INDEPENDENT);
        }
    }

    /** Activate all. */
    public void activateAllAgents() {
        startAgents.clear();
        startAgents.addAll(agents);
        activeAgents.clear();
        activeAgents.addAll(agents);     
    }

    /** Adds an agent to the team. */
    public synchronized boolean add(Agent a) {
        a.addActionListener(this);
        ags.addChild(a.ags, Settings.PROPERTY_INDEPENDENT);
        boolean result = agents.add(a);
        setAgentNumber(agents.size());
        return result;
    }
    
    /** Adds a group of agents to the team. */
    public synchronized void addAll(Collection<Agent> newAgents) {
        for (Agent a:newAgents){agents.add(a);}
    }
    
    /** Removes an agent from the team. */
    public synchronized boolean remove(Agent a) {
        a.removeActionListener(this);
        ags.removeChild(a.ags);
        tes.removeChild(a.ags);
        return agents.remove(a);
    }

    /** Changes the number of agents, resets starting locations. */
    public void initAgentNumber() {
        editing = true;
        int targetSize = getAgentNumber();
        while (agents.size() > targetSize) {
            remove(agents.lastElement());
        }
        while (agents.size() < targetSize) {
            add(new Agent(this,"Agent "+(agents.size()+1)));
        }
        if (victory != null) {
            victory.resetTeam();
        }
        for (CaptureCondition cc : capture) {
            cc.resetTeam();
        }
        for (Valuation v : metrics) {
            v.resetTeam();
        }
        activateAllAgents();
        editing = false;
    }

    /** Re-initializes agent starting locations. */
    public void initStartingLocations(double pitchSize) {
        editing = true;
        switch (getStartCode()) {
            case START_ZERO:
                StartingPositionsFactory.startZero(agents);
                break;
            case START_RANDOM:
                StartingPositionsFactory.startRandom(agents, pitchSize);
                break;
            case START_LINE:
                StartingPositionsFactory.startLine(agents, new R2(-pitchSize, pitchSize), new R2(pitchSize, -pitchSize));
                break;
            case START_CIRCLE:
                StartingPositionsFactory.startCircle(agents, new R2(), pitchSize);
                break;
            case START_ARC:
                StartingPositionsFactory.startArc(agents, new R2(), pitchSize, Math.PI / 3, 5 * Math.PI / 3);
                break;
            case START_SPECIFIC:
                StartingPositionsFactory.startSpecific(agents, positions);
                break;
            case START_SIDELINE:
                StartingPositionsFactory.startSideline(agents);
                break;
           
            case START_ENDZONE:
                StartingPositionsFactory.startEndzone(agents);
                break;
            case START_OFFENSE:
                StartingPositionsFactory.startOffense(agents);
                break;
            case START_DEFENSE:
                StartingPositionsFactory.startDefense(agents);
                break;
                
            default:
                StartingPositionsFactory.startCustom(agents);
                break;
        }
        editing = false;
    }
    
    
    // BEAN PATTERNS: GETTERS & SETTERS
    
    /** Returns center of mass of the team
     * @return center of mass */
    public R2 getCenterOfMass() {
        if (agents.size() == 0) {
            return null;
        }
        R2 center = new R2(0, 0);
        for (Agent agent : activeAgents) {
            center.translate(agent.loc);
        //System.out.println("agent:"+agent.x+"+"+agent.y);
        }
        center.multiplyBy(1.0 /agents.size());
        //System.out.println("center:"+center.x+"+"+center.y);
        return center;
    }
    
    /** Returns current set of active agents. */
    public HashSet<Agent> getActiveAgents() {
        return activeAgents;
    }

    /** Returns set of agents which start the simulation. */
    public Collection<Agent> getStartAgents() {
        return startAgents;
    }

    /** Sets agents which are used in the simulation to a smaller subset. */
    public void setStartAgents(Collection<Agent> agents) {
        if (startAgents == null) {
            startAgents = new HashSet<Agent>();
        }
        startAgents.clear();
        startAgents.addAll(agents);
    }
    
    /** Returns vector of agent starting positions. */
    public R2[] getStartingLocations() {
        R2[] result = new R2[agents.size()];
        for (int i = 0; i < result.length; i++) {
            result[i]=agents.get(i).getInitialPosition();
        }
        return result;
    }
    
    /** Sets initial locations to a particular vector of points. */
    public void setStartingLocations(R2[] positions) {
        this.positions = positions;
        setStartCode(START_SPECIFIC);
    }
    
    
    // LOG EVENTS
    
    /** Logs a capture by a particular opposing team. */
    public void addOneCapturedBy(Team opponent) {
        if(captures.containsKey(opponent)) {
            captures.put(opponent, captures.get(opponent)+1);
        } else {
            captures.put(opponent,1);
        }
    }
    
    /** Logs that a particular member of the team has reached "safety". */
    public void addOneReachedSafety() {
        // code is same as above, so just recycle it
        addOneCapturedBy(this);
    }
    
    /** Returns number which have been captured by an opposing team. */
    public int getNumberCapturedBy(Team opponent) {
        return captures.containsKey(opponent) ? captures.get(opponent) : 0;
    }
    
    /** Returns number of team which have reached safety. */
    public int getNumberSafe() {
        return getNumberCapturedBy(this);
    }
    
//    public HashSet<Agent> getValueAgents(){return valueAgents;}
//    public void setValueAgents(HashSet<Agent> agents){valueAgents=agents;}
//    public HashSet<Agent> getActiveValueAgents(){return activeValueAgents;}
    // BROADCAST METHODS: PASS INSTRUCTIONS ONTO TEAM MEMBERS
    /** Instructs all agents to gather sensory data
     * @param d The global table of distances */
    public void gatherSensoryData(DistanceTable d) {
        for (Agent a : activeAgents) {
            a.gatherSensoryData(d);
        }
    }

    /** Instructs all agents to generate communication events for other players
     * @param d The global table of distances */
    public void communicateSensoryData(DistanceTable d) {
        for (Agent a : activeAgents) {
            a.generateSensoryEvents(this, d);
        }
    }

    /** Tells each agent to form their belief regarding the playing field. */
    public void fuseAgentPOV() {
        for (Agent a : activeAgents) {
            a.fusePOV();
        }
    }

    /** Assigns tasks to the agents. */
    public void assignTasks(DistanceTable table) {
        for (Agent a : activeAgents) {
            a.tasks.clear();
        }
        control.generateTasks(this, table, 1.0);
        for (Agent a : activeAgents) {
            a.generateTasks(this, table, 1.0);
        }
    }

    /** Generates directions for each team member based on their task and myBehavior.
     * @param time      the current time stamp
     * @param stepTime  the time between iterations */
    public void planPaths(double time, double stepTime) {
        for (Agent a : activeAgents) {
            a.planPath(time, stepTime);
        }
    }

    /** Moves all agents on the team using their assigned directions. */
    public void move(double stepTime) {
        for (Agent a : activeAgents) {
            a.move(stepTime);
        }
    }

    /** Deactivates a particular agent. */
    public void deactivate(Agent a) {
        activeAgents.remove(a);
//        activeValueAgents.remove(a);
        a.deactivate();
    }
    
    // EVENT LISTENING
    /** Receives and re-broadcasts action events from agents on the team */
    public void actionPerformed(ActionEvent evt) {
        if (!editing) {
            fireActionPerformed(evt.getActionCommand());
        }
    }    // Remaining code deals with action listening
    protected EventListenerList listenerList = new EventListenerList();

    public void addActionListener(ActionListener l) {
        listenerList.remove(ActionListener.class, l);
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
    
    // BEAN PATTERNS FOR INITIAL SETTINGS
    
    @XmlAttribute // not necessary to make this an attribute... comes in as the number of agents in the team
    public int getAgentNumber() { return tes.size.getValue(); }
    public void setAgentNumber(int newValue) { tes.size.setValue(newValue); }
        
    @XmlAttribute
    public String getName(){ return toString(); }
    public void setName(String newValue) { tes.setName(newValue); }

    // @XmlAttribute Should not be saved as saving stores the agent positions.
    public int getStartCode() { return tes.start.getValue(); }
    public void setStartCode(int newValue) { tes.start.setValue(newValue); }
    
    @XmlAttribute
    public double getSensorRange() { return tes.sensorRange.getValue(); }
    public void setSensorRange(double newValue) { tes.sensorRange.setValue(newValue); }

    @XmlAttribute
    public double getCommRange() { return tes.commRange.getValue(); }
    public void setCommRange(double newValue) { tes.commRange.setValue(newValue); }

    @XmlAttribute
    public double getTopSpeed() { return tes.topSpeed.getValue(); }
    public void setTopSpeed(double newValue) { tes.topSpeed.setValue(newValue); }

    @XmlAttribute(name="behaviorCode")
    public int getBehavior() { return tes.behavior.getValue(); }
    public void setBehavior(int newValue) { tes.behavior.setValue(newValue); }

    @XmlAttribute
    public double getLeadFactor() { return tes.leadFactor.getValue(); }
    public void setLeadFactor(double newValue) { tes.leadFactor.setValue(newValue); }

    
//    @XmlElement(name="victory")
//    public VictoryCondition getVictoryCondition(){return victory;}
    public void setVictoryCondition(VictoryCondition vic) {
        this.victory = vic;
        tes.addChild(vic.vs, Settings.PROPERTY_INDEPENDENT);
    }
    
    @XmlElement(name="tasking")
    public Vector<Goal> getTaskings(){
        Vector<Goal> result = new Vector<Goal>();
        for (TaskGenerator tg:control.getTaskGenerators()){
            if(tg instanceof Goal){result.add((Goal) tg);}
        }
        return result;
    }
    public void setTaskings(Vector<Goal> goals){
        if(goals!=null){
            control.taskGenerators.clear();
            for(Goal g:goals){addControlTask(g);}
        }
    }

    /** Returns a specific agent. */
    public Agent getAgent(int i){return agents.get(i);}
    
    @XmlElementWrapper(name="agents")
    @XmlElement(name="agent")
    public Vector<Agent> getAllAgents() { return agents; }
    public void setAllAgents(Vector<Agent> newAgents) {  
        if(newAgents==null){return;}
        if(newAgents==agents){
           //System.out.println("here");            
        } else {
//            System.out.println("old positions for team "+getName()+":");
//            for(Agent a:agents){System.out.println(" "+a.getInitialPosition());}
//            System.out.println("new positions:");
//            for(Agent a:newAgents){System.out.println(" "+a.getInitialPosition());}
//            editing = true;
//            for(Agent a:agents){a.removeActionListener(this);tes.removeChild(a.ags);}            
//            agents.clear();
//            for(Agent a:newAgents){add(a);}
//            if(victory!=null){victory.resetTeam();}
//            for (CaptureCondition cc:capture){cc.resetTeam();}
//            for (Valuation v:metrics){v.resetTeam();}
//            activateAllAgents();
//            editing=false;
//            System.out.println("newer positions:");
//            for(Agent a:agents){System.out.println(" "+a.getInitialPosition());}
        }
    }
    
    /** Reloads the settings tree, and updates the team names for subsidiary settings. */
    public void update(Vector<Team> teams){
        tes.getChildren().clear();
        tass.getChildren().clear();
        ags.getChildren().clear();
        mets.getChildren().clear();
        tes.addChild(ags, Settings.PROPERTY_INDEPENDENT);
        tes.addChild(tass, Settings.PROPERTY_INDEPENDENT);
        tes.addChild(mets, Settings.PROPERTY_INDEPENDENT);
        for(CaptureCondition v:capture){
            v.update(teams,this);
            tes.addChild(v.vs, Settings.PROPERTY_INDEPENDENT);
        }
        for(Valuation v:metrics){
            v.update(teams,this);
            tes.addChild(v.vs, Settings.PROPERTY_INDEPENDENT);
        }
        for(TaskGenerator g:control.getTaskGenerators()){
            ((Goal)g).update(teams,this);
            tass.addChild(((Goal)g).gs, Settings.PROPERTY_INDEPENDENT);
        }
        if(victory!=null){
            victory.update(teams,this);
            tes.addChild(victory.vs, Settings.PROPERTY_INDEPENDENT);
        }        
        for(Agent a:agents){
            a.addActionListener(this);
            ags.addChild(a.ags, Settings.PROPERTY_INDEPENDENT);
        }
        startAgents.clear();
        startAgents.addAll(agents);
    }

    
    @XmlAttribute
    public String getColor(){return tes.color.getHexString();}
    public void setColor(String s){tes.color.setHexString(s);}
    
    //@XmlElement(name="color")
    public ColorModel getColorModel() { return tes.color; }        
    public void setColorModel(ColorModel cm) { tes.color.copyValuesFrom(cm); }    
    
    public void setColorValue(Color newValue) { tes.color.setValue(newValue); }

    public int getNumActive() { return getActiveAgents().size(); }
    public Double getValue() { return value; }

    //    public Collection<Goal> getGoals(){return goals;}
    
    @Override
    public String toString() { return tes.toString(); }

    public R2 getPositionTime(double t) {
        try {
            return new R2(tes.pm.getValue(t));
        } catch (FunctionValueException ex) {
            return R2.ORIGIN;
        }
    }

    public void setFixedPath(String xt, String yt) {
        tes.pm.setXString(xt);
        tes.pm.setYString(yt);
    }

    public JPanel getPanel() {
        return tes.getPanel();
    }
    
    // BROADCAST METHODS: CHANGE SETTINGS OF AGENTS ON TEAM
    
    public void copySpeedtoTeam() { for (Agent a : agents) { a.setTopSpeed(getTopSpeed()); } }
    public void copySensorRangetoTeam() { for (Agent a : agents) { a.setSensorRange(getSensorRange()); } }
    public void copyCommRangetoTeam() { for (Agent a : agents) { a.setCommRange(getCommRange()); } }
    public void copyBehaviortoTeam() { for (Agent a : agents) { a.setBehaviorCode(getBehavior()); } }
    public void copyColortoTeam() { for (Agent a : agents) { a.setColorValue(tes.getColor()); } }
    public void copyLeadFactortoTeam() { for (Agent a : agents) { a.setLeadFactor(getLeadFactor()); } }
    public void copyPathtoTeam() { for (Agent a : agents) { a.setFixedPath(tes.pm.getXString(), tes.pm.getYString()); } }
    
    // CONSTANTS FOR INITIAL SETTINGS
    
    public static final int START_CUSTOM = 0;
    public static final int START_ZERO = 1;
    public static final int START_RANDOM = 2;
    public static final int START_LINE = 3;
    public static final int START_CIRCLE = 4;
    public static final int START_ARC = 5;
    public static final int START_SPECIFIC = 6;
    public static final int START_SIDELINE=7;
    public static final int START_ENDZONE = 8;
    public static final int START_OFFENSE = 9;
    public static final int START_DEFENSE = 10;
    
    /** String with labels for initial conditions. */
    public static final String[] START_STRINGS = {"Custom", "All at Zero", "Random Positions", "Along a Line", "Around a Circle", "Along a Circular Arc", "Specific Locations", "Sideline Locations", "Endzone Locations", "Offense Locations", "Defense Locations"};

    
    //    
    // SUBCLASSES
    //
    
    /** Contains all the initial settings for the simulation. Everything else is used while the simulation is running. */
    private class TeamSettings extends Settings {

        /** Team size */
        private IntegerRangeModel size = new IntegerRangeModel(3, 1, 100);
        /** Starting positions to use */
        private StringRangeModel start = new StringRangeModel(START_STRINGS, START_CUSTOM);
        /** Default sensor range [in ft]. */
        private DoubleRangeModel sensorRange = new DoubleRangeModel(20, 0, 5000, .5);
        /** Default communications range [in ft]. */
        private DoubleRangeModel commRange = new DoubleRangeModel(50, 0, 5000, .5);
        /** Default speed [in ft/s]. */
        private DoubleRangeModel topSpeed = new DoubleRangeModel(5, 0, 50, .05);
        /** Default behavioral setting */
        private StringRangeModel behavior = Behavior.getComboBoxModel();
        /** Lead factor if required for myBehavior */
        private DoubleRangeModel leadFactor = new DoubleRangeModel(0, 0, 2, .01);
        /** Position function if required for myBehavior */
        private ParametricModel pm = new ParametricModel();
        /** Default color. */
        private ColorModel color = new ColorModel(Color.GREEN);

        /** Returns the color */
        @Override
        public Color getColor() {
            return color.getValue();
        }

        public TeamSettings() {
            add(new SettingsProperty("# Agents", size, Settings.EDIT_INTEGER));
            add(new SettingsProperty("Starting Loc", start, Settings.EDIT_COMBO));
            add(new SettingsProperty("Speed", topSpeed, Settings.EDIT_DOUBLE));
            add(new SettingsProperty("Sensor Range", sensorRange, Settings.EDIT_DOUBLE));
            add(new SettingsProperty("Comm Range", commRange, Settings.EDIT_DOUBLE));
            add(new SettingsProperty("Behavior", behavior, Settings.EDIT_COMBO));
            add(new SettingsProperty("Lead Factor", leadFactor, Settings.EDIT_DOUBLE_SLIDER));
            add(new SettingsProperty("Position(t)", pm, Settings.NO_EDIT));
            add(new SettingsProperty("Color", color, Settings.EDIT_COLOR));
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
            if (editing) {
                return;
            }
            String ac = null;
            if (evt.getSource() == behavior) {
                copyBehaviortoTeam();
                if (behavior.getValue() == Behavior.APPROACHPATH) {
                    setPropertyEditor("Position(t)", Settings.EDIT_PARAMETRIC);
                } else {
                    setPropertyEditor("Position(t)", Settings.NO_EDIT);
                }
                ac = "teamSetupChange";
            } else if (evt.getSource() == size) {
                initAgentNumber();
                ac = "teamAgentsChange";
            } else if (evt.getSource() == start) {
                ac = "teamAgentsChange";
//            } else if(evt.getSource()==goals){                              ac="teamSetupChange";
            } else if (evt.getSource() == topSpeed) {
                copySpeedtoTeam();
                ac = "teamSetupChange";
            } else if (evt.getSource() == sensorRange) {
                copySensorRangetoTeam();
                ac = "teamSetupChange";
            } else if (evt.getSource() == commRange) {
                copyCommRangetoTeam();
                ac = "teamSetupChange";
            } else if (evt.getSource() == leadFactor) {
                copyLeadFactortoTeam();
                ac = "teamSetupChange";
            } else if (evt.getSource() == pm) {
                copyPathtoTeam();
                ac = "teamSetupChange";
            } else if (evt.getSource() == color) {
                copyColortoTeam();
                ac = "teamDisplayChange";
            }
            fireActionPerformed(ac);
        }
    }
}
