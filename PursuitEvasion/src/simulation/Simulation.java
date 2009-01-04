/*
 * Simulation.java
 *
 * Created on Aug 28, 2007, 10:29:14 AM
 *
 * Author: Elisha Peterson
 *
 * This class runs a single simulation with given settings.
 */

package simulation;

import analysis.SimulationLog;
import analysis.Statistics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;
import javax.swing.JMenu;
import javax.swing.event.ChangeEvent;
import javax.swing.event.EventListenerList;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import metrics.CaptureCondition;
import metrics.Valuation;
import metrics.VictoryCondition;
import sequor.Settings;
import sequor.SettingsProperty;
import sequor.model.DoubleRangeModel;
import sequor.model.IntegerRangeModel;
import sequor.model.StringRangeModel;
import utility.DistanceTable;
import utility.SimulationFactory;


/**
 *
 * @author ae3263
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class Simulation implements ActionListener {
    
    //
    // CONTROL VARIABLES (define the simulation)
    //
    
    /** Contains all settings used to run the simulation. */
    @XmlTransient
    public SimSettings ss;
    
    /** Contains list of teams involved (mixture of control and state variables). */
    @XmlElement
    Vector<Team> teams;
    
    
    //
    // STATE VARIABLES (change during simulation)
    //
    
    /** Table of distances (for speed of simulation) */
    public DistanceTable dist;   
    
    /** The data collected in a simulation. */ 
    public SimulationLog log;
    
    
    //
    // BASIC PROPERTIES (unrelated to simulation)
    //    
    
    /** Whether a batch of several runs is currently processing. */
    boolean batchProcessing;
    
    
    //
    // CONSTRUCTORS
    //
    
    /** Standard constructor */
    public Simulation(){
        initControlVariables();
        initProperties();
        initStateVariables();
    }
    
    /** Constructs given a type of game
     * @param gameType the type of game to simulate */
    public Simulation(int gameType){
        initControlVariables();
        initProperties();
        setGameType(gameType);
        initStateVariables();
    }
    
    
    //
    // METHODS: INITIALIZERS
    //
    
    /** Initializes control variables. */
    public void initControlVariables() {
        ss = new SimSettings();
        teams = new Vector<Team>(getNumTeams());
    }
    
    /** Initializes state variables. Also resets any state variables, so can
     * before a simulation is run. */
    public void initStateVariables() {
        for (Team t : teams) { t.initStateVariables(); }
        dist = new DistanceTable(teams);
        if(log==null) { log = new SimulationLog(this); }
        log.preRun();
    }
    
    /** Initializes properties. */
    public void initProperties() {
        batchProcessing = false;
    }
    
    /** Resets the starting positions. Useful when positions are initialized randomly. */
    public void initStartingLocations(){
        for(Team team:teams){team.initStartingLocations(getPitchSize());}
    }
    
    
    //   
    // BEAN PATTERNS (control variables)
    //
    
    @XmlAttribute
    public String getName(){return ss.getName();}
    public void setName(String newValue){ss.setName(newValue);}
    
    // @XmlAttribute not an attribute since game type when loaded will always be custom
    public int getGameType(){return ss.gameType.getValue();}
    public void setGameType(int newValue){ss.gameType.setValue(newValue);}
    
    // @XmlAttribute not necessary to make this an attribute
    public int getNumTeams(){return ss.numTeams.getValue();}
    public void setNumTeams(int newValue){ss.numTeams.setValue(newValue);}
    
    @XmlAttribute
    public double getStepTime(){return ss.stepTime.getValue();}
    public void setStepTime(double newValue){ss.stepTime.setValue(newValue);}
    
    @XmlAttribute
    public int getNumSteps(){return ss.numSteps.getValue();}
    public void setNumSteps(int newValue){ss.numSteps.setValue(newValue);}
    
    @XmlAttribute
    public int getMaxSteps(){return ss.maxSteps.getValue();}
    public void setMaxSteps(int newValue){ss.maxSteps.setValue(newValue);}
    
    @XmlAttribute
    public double getPitchSize(){return ss.pitchSize.getValue();}
    public void setPitchSize(double newValue){ss.pitchSize.setValue(newValue);}
        
    /** Returns list of teams used in the simulation. */
    public Vector<Team> getTeams(){return teams;}
    public void setTeams(Vector<Team> newTeams){ if(newTeams!=null){ teams = newTeams; } }
    
    
    //   
    // BEAN PATTERNS (others)
    //
            
    @Override
    public String toString(){return ss.toString();}
    
    public void setBatchProcessing(boolean b) {batchProcessing=b;}
    
    /** Returns string list of teams. */
    public static String[] getTeamStrings(Vector<Team> teams){
        String[] result = new String[teams.size()];
        for (int i = 0; i < teams.size(); i++) {
            result[i] = teams.get(i).toString();
        }
        return result;
    }

    /** Returns custom menu for this simulation. */
    public JMenu getMenu(String s){JMenu jm=ss.getMenu();jm.setText(s);return jm;}

    /** Returns data model for selecting preset game types, suitable for use with a combobox. */
    public StringRangeModel getGameTypeModel(){return ss.gameType;}
    
    
    //
    // ACCESSOR METHODS FOR TEAM VECTOR
    //    
    
    /** Initializes listening for the current list of teams. Should
     * be called once, after all the teams are loaded in.
     */
    public void update() {
        batchProcessing = true;
        ss.removeAllChildren();
        for(Team t:teams){            
            t.addActionListener(this);
            ss.addChild(t.tes,Settings.PROPERTY_INDEPENDENT);
            t.initStartingLocations(getPitchSize());
            t.update(teams);
        }
        setNumTeams(teams.size());
        if(log==null){ log = new SimulationLog(); }
        log.initialize(Simulation.this);
        batchProcessing = false;
    }
    
    
    //
    // METHODS: DEPLOY SIMULATION
    //
    
    /** Runs several times and computes average result. */
    public void runSeveral(int numTimes){
        
        // Initialize for data collection   
        Statistics stats=new Statistics();
        HashSet<Valuation> vals = new HashSet<Valuation> ();
        for(Team team:teams){
            if(team.victory != null) { vals.add(team.victory); }
            vals.addAll(team.metrics);
        }     
        stats.reset(vals, teams, numTimes);
        
        // Compile list of partial simulations which must be run
        HashMap<Valuation, HashSet<Agent>> coopVals = new HashMap<Valuation, HashSet<Agent>> ();
        for (Valuation v : vals) {
            if(v.isCooperationTesting()) {
                coopVals.put(v, v.getComplement());
            }
        }
        
        // Run the simulations
        batchProcessing=true;        
        for(int i=0;i<numTimes;i++){
            for(Team team:teams){
                team.initStartingLocations(getPitchSize());
                team.initStateVariables();
            }
            stats.captureLocs(teams);
            dist=new DistanceTable(teams);
            run();
            // capture full data
            for (Valuation v : vals) { stats.captureData(v, log, null); }
            // capture partial data
            for (Valuation v : coopVals.keySet()) {
                v.getOwner().setStartAgents(coopVals.get(v));
                run();
                stats.captureData(v, null, log);
                v.getOwner().setStartAgents(v.getOwner().agents);
            }
        }
        batchProcessing=false;
        fireActionPerformed(new ActionEvent(stats,0,"log"));
        actionPerformed(new ActionEvent(this,0,"redraw"));
    }
    
    /** Runs default number of steps */
    public void run(){run(getNumSteps());}
    
    /** Tells the simulation to get going!
     * @param numSteps  how many time steps to run the simulation
     */
    public void run(int numSteps){
        boolean quit = false;
        initStateVariables();
        double time=0;
        for(int i=0;i<numSteps;i++){
            time = i*getStepTime();
            quit = iterate(time);
            log.logAll(i,dist);
            if(quit){break;}
        }
        //log.setPrimaryOutput(primary.getRValue());
        actionPerformed(new ActionEvent(log,0,"log"));
        if(!batchProcessing){actionPerformed(new ActionEvent(this,0,"redraw"));}
    }
    
    /** Runs a single iteration of the scenario */
    public boolean iterate(double time){
        
        // Step 1. Recalculate the table of distances
        dist.recalculate(time);
        
        // Step 2. Check for captures and remove associated playerd
        for(Team t:teams){
            for(CaptureCondition cc:t.capture){
                cc.check(dist, log, time);
            }
        }

        // Step 3. Check for victory
        for(Team t:teams){
            VictoryCondition vc=t.victory;
            if (vc!=null) {
                int result=vc.check(dist, log, time);
                if(result!=VictoryCondition.NEITHER && vc.gameEnding){
                    log.logEvent(null,null,null,null,"Simulation ended",time);
                    return true;
                }
            }
        }
//        for(Team t:teams){
//           t.checkGoal(dist,time);
//        }
        
        // Step 4. Collect/communicate data
        for(Team t:teams){
//            if(!t.getGoals().isEmpty()){
                t.gatherSensoryData(dist);
                t.communicateSensoryData(dist);
                t.fuseAgentPOV();
//            }
        }
        
        // Step 5. Perform tasking
        for(Team t:teams){
//            if(!t.getGoals().isEmpty()){
                t.assignTasks(dist);
//            }
        }
        
        // Step 6. Fuse tasks
        for(Team t:teams){t.planPaths(time,getStepTime());}
        
        // Step 7. Move agents
        for(Team t:teams){t.move(getStepTime());}
        
        return false;
    }
        
    
    //
    // EVENT HANDLING
    //
    
    // Remaining code deals with action listening
    protected ActionEvent actionEvent=null;
    protected EventListenerList listenerList=new EventListenerList();
    public void addActionListener(ActionListener l){listenerList.add(ActionListener.class, l);}
    public void removeActionListener(ActionListener l){listenerList.remove(ActionListener.class, l);}
    protected void fireActionPerformed(String s){fireActionPerformed(new ActionEvent(this,ActionEvent.ACTION_PERFORMED,s));}
    protected void fireActionPerformed(ActionEvent e){
        //System.out.println(e.getActionCommand());
        actionEvent=e;
        Object[] listeners=listenerList.getListenerList();
        for(int i=listeners.length-2;i>=0;i-=2){
            if(listeners[i]==ActionListener.class){
                if(actionEvent==null){actionEvent=e;}
                ((ActionListener)listeners[i+1]).actionPerformed(actionEvent);
            }
        }
    }
    
    public void actionPerformed(ActionEvent e) {
        // if several runs are being performed, do not bother to display the changes being made
        if(batchProcessing){return;}
        String es=e.getActionCommand();
        // otherwise, go ahead and redraw/recolor/etc. as appropriate
        if(es==null){
            fireActionPerformed(e);
        }else if(es.equals("simulationRun")||es.equals("agentDisplayChange")||es.equals("teamDisplayChange")){ // routine run of the simulation
            fireActionPerformed("redraw");
        // change in parameters for a particular player or team
        }else if(es.equals("agentSetupChange")||es.equals("teamSetupChange")){ 
            run();
        // change in number of agents; must preRun the simulation to reload the settings tree
        }else if(e.getActionCommand().equals("teamAgentsChange")){ 
            log.initializeNumbersOnly();
            fireActionPerformed("reset");
            run();
        }else {
            fireActionPerformed(e);
        }
    }
    
    


    //
    //
    // SUBCLASSES
    //
    //
    
    /** Stores the control variables for the simulation. Ex: determining how long to run the simulation,
     * the area of interest in the simulation, and so on.
     */
    public class SimSettings extends Settings {
        
        // BASIC PROPERTIES
        
        /** Number of teams */
        private IntegerRangeModel numTeams=new IntegerRangeModel(2,1,100);
        /** Type of game involved */
        private StringRangeModel gameType=SimulationFactory.comboBoxRangeModel();
        /** Pitch size */
        private DoubleRangeModel pitchSize=new DoubleRangeModel(60,0,50000,1);
        /** Time taken by a single step [in seconds] */
        private DoubleRangeModel stepTime=new DoubleRangeModel(.1,0,15,.01);
        /** Number of steps to run the simulation before quitting. */
        private IntegerRangeModel numSteps=new IntegerRangeModel(250,0,10000);
        /** If stop is based on reaching a goal, this is the max # of steps to allow. */
        private IntegerRangeModel maxSteps=new IntegerRangeModel(1000,0,10000000);
        
        // CONSTRUCTORS
        
        /** Default constructor */
        public SimSettings(){
            super();
            initProperties();
            setName("Custom");
        }
        
        /** Initialize the properties. */
        public void initProperties(){
            add(new SettingsProperty("Pitch Size",pitchSize,Settings.EDIT_DOUBLE,"Change the boundaries of the random positions (nonfunctional)"));
            add(new SettingsProperty("Step Time",stepTime,Settings.EDIT_DOUBLE,"Change the time taken for each iteration of the algorithm"));
            add(new SettingsProperty("# of Steps",numSteps,Settings.EDIT_INTEGER,"Change the number of steps in the simulation"));
            // we want the game type to also fire changes, but not show up in the simulation window as an editable property
            gameType.addChangeListener(this);
        }
        
        @Override
        public void stateChanged(ChangeEvent e) {
            if(e.getSource()==stepTime || e.getSource()==numSteps){      
                fireActionPerformed("animation");
            } else if(e.getSource()==pitchSize){
                initStartingLocations();
            } else if(e.getSource()==gameType){
                setName(SimulationFactory.GAME_STRINGS[getGameType()]);
                setTeams(SimulationFactory.getTeams(getGameType()));
                update();
                fireActionPerformed("reset");
            } else {
                return;
            }
            run();
        }
    }
}