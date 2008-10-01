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

import analysis.DataLog;
import analysis.Statistics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.EventListenerList;
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
public class Simulation implements ActionListener,PropertyChangeListener {
    
    
    // PROPERTIES
    
    /** Contains all settings used to run the simulation. */
    public SimSettings ss;
    /** Contains list of teams involved. */
    Vector<Team> teams;
    /** Table of distances (for speed of simulation) */
    DistanceTable dist;
    
    /** The data collected in a simulation. */
    DataLog log;
    /** Whether a batch of several runs is currently processing. */
    boolean batchProcessing;
    
    
    // CONSTRUCTORS
    
    /** Standard constructor */
    public Simulation(){this(SimulationFactory.SAHARA_PE);}
    /** Constructs given a type of game
     * @param gameType the type of game to simulate */
    public Simulation(int gameType){
        ss=new SimSettings(gameType);
        
        SimulationFactory.setSimulation(this,gameType);
        dist=null;
        batchProcessing=false;
    }
    
    
    // METHODS: INITIALIZERS
    
    /** PRimary Initializer */
    public void mainInitialize(String name,Vector<Team> teams){
        setString(name);
        initTeams(teams);
    }
    
    /** Intializes to a given list of teams. */
    public void initTeams(Vector<Team> newTeams){
        if(newTeams!=null){
            setNumTeams(newTeams.size());
            if(teams!=null){for(Team t:teams){t.removeActionListener(this);}}
            teams=newTeams;
            ss.getChildren().clear();
            for(Team t:teams){
                t.addActionListener(this);
                t.initStartingLocations(getPitchSize());
                ss.addChild(t.tes,Settings.PROPERTY_INDEPENDENT);
            }
        }        
    }
    
    
    // BEAN PATTERNS
    
    /** Returns current distance table. */
    public DistanceTable getDistanceTable() { return this.dist; }    
    /** Returns current data log. */
    public DataLog getLog() { return this.log; }    
    /** Returns list of teams used in the simulation. */
    public Vector<Team> getTeams(){return teams;}
    
    /** Returns string list of teams. */
    public static String[] getTeamStrings(Vector<Team> teams){
        String[] result = new String[teams.size()];
        for (int i = 0; i < teams.size(); i++) {
            result[i] = teams.get(i).toString();
        }
        return result;
    }
    
    
    // METHODS: DEPLOY SIMULATION
    
    /** Delete paths and get ready to start another simulation. */
    public void reset(){
        for(Team team:teams){team.reset();}
        dist=new DistanceTable(teams);
    }
    
    /** Resets the starting positions. Useful when positions are initialized randomly. */
    public void initStartingLocations(){
        for(Team team:teams){team.initStartingLocations(getPitchSize());}
        dist=new DistanceTable(teams);
    }
    
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
                team.reset();
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
                v.getOwner().setStartAgents(v.getOwner());
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
        reset();
        log.preRun();
        double time=0;
        for(int i=0;i<numSteps;i++){
            time=i*getStepTime();
            quit = iterate(time);
            log.logAll(i,dist);
            if(quit){break;}
        }
        //log.setPrimaryOutput(primary.getValue());
        actionPerformed(new ActionEvent(log,0,"log"));
        actionPerformed(new ActionEvent(this,0,"redraw"));
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
                if(result!=VictoryCondition.NEITHER && vc.endGame){
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
        for(Team t:teams){t.move();}
        
        return false;
    }
    
    // METHODS FOR RUNNING STATS ON THE SIMULATION
    
    public void setBatchProcessing(boolean b) {batchProcessing=b;}
    
    // EVENT HANDLING
    
    // Remaining code deals with action listening
    protected ActionEvent actionEvent=null;
    protected EventListenerList listenerList=new EventListenerList();
    public void addActionListener(ActionListener l){listenerList.add(ActionListener.class, l);}
    public void removeActionListener(ActionListener l){listenerList.remove(ActionListener.class, l);}
    protected void fireActionPerformed(String s){fireActionPerformed(new ActionEvent(this,ActionEvent.ACTION_PERFORMED,s));}
    protected void fireActionPerformed(ActionEvent e){
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
        }else if(es.equals("simulationRun")){ // routine run of the simulation
            fireActionPerformed("redraw");
        }
        // cosmetic change only
        else if(es.equals("agentDisplayChange")||es.equals("teamDisplayChange")){ 
            fireActionPerformed("recolor");
        // change in parameters for a particular player or team
        }else if(es.equals("agentSetupChange")||es.equals("teamSetupChange")){ 
            run();
        // change in number of agents; must preRun the simulation to reload the settings tree
        }else if(e.getActionCommand().equals("teamAgentsChange")){ 
            log.initializeNumbersOnly();
            fireActionPerformed("reset");
            run();
            fireActionPerformed("redraw");
        }else {fireActionPerformed(e);
        }
    }
    
    public void propertyChange(PropertyChangeEvent evt) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    
    // BEAN PATTERNS: GETTERS & SETTERS
    
    public int getNumTeams(){return ss.numTeams.getValue();}
    public int getGameType(){return ss.gameType.getValue();}
    public double getPitchSize(){return ss.pitchSize.getValue();}
    public double getStepTime(){return ss.stepTime.getValue();}
    public int getNumSteps(){return ss.numSteps.getValue();}
    public int getMaxSteps(){return ss.maxSteps.getValue();}
    @Override
    public String toString(){return ss.toString();}
    
    public void setNumTeams(int newValue){ss.numTeams.setValue(newValue);}
    public void setGameType(int newValue){ss.gameType.setValue(newValue);}
    public void setPitchType(double newValue){ss.pitchSize.setValue(newValue);}
    public void setStepTime(double newValue){ss.stepTime.setValue(newValue);}
    public void setNumSteps(int newValue){ss.numSteps.setValue(newValue);}
    public void setMaxSteps(int newValue){ss.maxSteps.setValue(newValue);}
    public void setString(String newValue){ss.setName(newValue);}
    public void setDataLog(DataLog dl){this.log=dl;}
    
    public JPanel getPanel(){return ss.getPanel();}
    public JMenu getMenu(String s){JMenu jm=ss.getMenu();jm.setText(s);return jm;}
    public StringRangeModel getGameTypeModel(){return ss.gameType;}
    
    // SUBCLASSES
    
    private class SimSettings extends Settings{
        
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
        
        SimSettings(){
            super();
            add(new SettingsProperty("Pitch Size",pitchSize,Settings.EDIT_DOUBLE,"Change the boundaries of the random positions (nonfunctional)"));
            add(new SettingsProperty("Step Time",stepTime,Settings.EDIT_DOUBLE,"Change the time taken for each iteration of the algorithm"));
            add(new SettingsProperty("# of Steps",numSteps,Settings.EDIT_INTEGER,"Change the number of steps in the simulation"));
            gameType.addChangeListener(this);
            //addProperty("max Steps",maxSteps,Settings.EDIT_INTEGER);
            //addProperty("# of Teams",numTeams,Settings.EDIT_INTEGER);
        }

        private SimSettings(int gameType) {
         super();
            add(new SettingsProperty("Pitch Size",pitchSize,Settings.EDIT_DOUBLE,"Change the boundaries of the random positions (nonfunctional)"));
            add(new SettingsProperty("Step Time",stepTime,Settings.EDIT_DOUBLE,"Change the time taken for each iteration of the algorithm"));
            add(new SettingsProperty("# of Steps",numSteps,Settings.EDIT_INTEGER,"Change the number of steps in the simulation"));
            this.gameType.setValue(gameType);
            this.gameType.addChangeListener(this);
            //addProperty("max Steps",maxSteps,Settings.EDIT_INTEGER);
            //addProperty("# of Teams",numTeams,Settings.EDIT_INTEGER);
        }
        
        @Override
        public void stateChanged(ChangeEvent e) {
            //System.out.println("simulation prop change: "+e.getPropertyName());
            if(e.getSource()==stepTime){      fireActionPerformed("animation");run();
            } else if(e.getSource()==numSteps){fireActionPerformed("animation");run();
            } else if(e.getSource()==pitchSize){initStartingLocations();run();
            } else if(e.getSource()==maxSteps){System.out.println("nonfunctional!");
            } else if(e.getSource()==numTeams){System.out.println("nonfunctional!");
            } else if(e.getSource()==gameType){
                SimulationFactory.setSimulation(Simulation.this,getGameType());
                fireActionPerformed("reset");
                dist=null;
                run();                
            }
        }
    }
}