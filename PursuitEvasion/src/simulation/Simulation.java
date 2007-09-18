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

import Blaise.BPlot2D;
import Blaise.BPlotPath2D;
import Model.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import javax.swing.event.ChangeEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import javax.swing.event.EventListenerList;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import simulation.Team;
import simulation.Agent;
import javax.swing.JPanel;
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
    /** Has complete/precise knowledge of playing field. */
    Agent bird;
    /** Contains list of teams involved. */
    ArrayList<Team> teams;
    /** Table of distances (for speed of simulation) */
    DistanceTable dist;
    /** The time stamp when iterating */
    double time=0;
    
    
    // CONSTRUCTORS
    
    /** Standard constructor */
    public Simulation(){
        ss=new SimSettings();
        setGameType(SimulationFactory.SIMPLE_PE);
        bird=null;
        SimulationFactory.setSimulation(this,getGameType());
        dist=null;
    }
    /** Constructs given a type of game
     * @param gameType the type of game to simulate */
    public Simulation(int gameType){
        ss=new SimSettings();
        setGameType(gameType);
        bird=null;
        SimulationFactory.setSimulation(this,gameType);
        dist=null;
    }
    
    
    // METHODS: INITIALIZERS
    
    /** Intializes to a given list of teams. */
    public void initTeams(ArrayList<Team> newTeams){
        if(newTeams!=null){
            if(teams!=null){for(Team t:teams){t.removeActionListener(this);}}
            teams=newTeams;
            for(Team t:teams){t.addActionListener(this);}
        }
        //fireActionPerformed(new ActionEvent(this,ActionEvent.ACTION_PERFORMED,"teamsInitialized"));
    }
    
    // METHODS: DEPLOY SIMULATION
    
    /** Delete paths and get ready to start another simulation. */
    public void reset(){
        for(Team team:teams){team.reset();}
        dist=new DistanceTable(teams);
        time=0;
    }
    
    /** Resets the starting positions. Useful when positions are initialized randomly. */
    public void initStartingLocations(){
        for(Team team:teams){team.initStartingLocations();}
        dist=new DistanceTable(teams);
        time=0;
    }
    
    /** Runs default number of steps */
    public void run(){run(getNumSteps());}
    /** Tells the simulation to get going!
     * @param numSteps  how many time steps to run the simulation
     * @return          to be determined...
     */
    public int run(int numSteps){
        reset();
        for(int i=0;i<numSteps;i++){iterate();}
        fireActionPerformed(new ActionEvent(this,ActionEvent.ACTION_PERFORMED,"redraw"));
        return 0;
    }
    
    /** Runs a single iteration of the scenario */
    public void iterate(){
        dist.recalculate();
        for(Team t:teams){
            if(!t.getGoal().isTrivial()){
                t.checkGoal(dist,time);
                //t.gatherSensoryData(dist);
                //t.communicateSensoryData(dist);
                //t.fuseAgentPOV();
                t.tasking.assign(t,t.getGoal());
            }
        }
        for(Team t:teams){t.planPaths(time,getStepTime());}
        for(Team t:teams){t.move();}
        //bird.remember();
        time+=getStepTime();
    }
    
    // METHODS: RETURN RESULTS OF SIMULATION IN VARIOUS FORMATS
    
    /** Returns point models corresponding to initial conditions */
    public void placeInitialPointsOn(BPlot2D p){for(Team t:teams){t.placeInitialPointsOn(p);}}
    
    /** Recomputes animation settings for a plot window */
    public void setAnimationCycle(BPlot2D p){
        p.setAnimateCycle(getNumSteps()+10);
        if(getStepTime()>.4){p.setAnimateDelay(100);} else{p.setAnimateDelay((int)(250*getStepTime()));}
    }
    
    /** Returns all paths computed by the simulation
     * @return arraylist of bplotpath2d's suitable to add to a BPlot2D */
    public ArrayList<BPlotPath2D> getComputedPaths(){
        ArrayList<BPlotPath2D> result=new ArrayList<BPlotPath2D>();
        for(Team t:teams){result.addAll(t.getPlotPaths());}
        return result;
    }
    
    /** Returns all teams/agents as a JTree
     * @return a tree model with all agents involved in the simulation */
    public DefaultTreeModel getTreeModel(){
        DefaultMutableTreeNode top=new DefaultMutableTreeNode(this);
        for(Team t:teams){top.add(t.getTreeNode());}
        return new DefaultTreeModel(top);
    }
    
    
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
        if(e.getActionCommand()=="agentDisplayChange"){fireActionPerformed("redraw");
        }else if(e.getActionCommand()=="teamDisplayChange"){fireActionPerformed("redraw");
        }else if(e.getActionCommand()=="agentSetupChange"){run();
        }else if(e.getActionCommand()=="teamSetupChange"){run();
        }else if(e.getActionCommand()=="teamAgentsChange"){run();fireActionPerformed("reset");
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
    public String toString(){return ss.s;}
    
    public void setNumTeams(int newValue){ss.numTeams.setValue(newValue);}
    public void setGameType(int newValue){ss.gameType.setValue(newValue);}
    public void setPitchType(double newValue){ss.pitchSize.setValue(newValue);}
    public void setStepTime(double newValue){ss.stepTime.setValue(newValue);}
    public void setNumSteps(int newValue){ss.numSteps.setValue(newValue);}
    public void setMaxSteps(int newValue){ss.maxSteps.setValue(newValue);}
    public void setString(String newValue){ss.s=newValue;}
    
    public JPanel getPanel(){return ss.getPanel();}
    
    // SUBCLASSES
    
    private class SimSettings extends Settings{
        
        /** Number of teams */
        private IntegerRangeModel numTeams=new IntegerRangeModel(2,1,100);
        /** Type of game involved */
        private ComboBoxRangeModel gameType=SimulationFactory.comboBoxRangeModel();
        /** Pitch size */
        private DoubleRangeModel pitchSize=new DoubleRangeModel(60,0,50000,1);
        /** Time taken by a single step [in seconds] */
        private DoubleRangeModel stepTime=new DoubleRangeModel(.1,0,15,.01);
        /** Number of steps to run the simulation before quitting. */
        private IntegerRangeModel numSteps=new IntegerRangeModel(100,0,10000);
        /** If stop is based on reaching a goal, this is the max # of steps to allow. */
        private IntegerRangeModel maxSteps=new IntegerRangeModel(1000,0,10000000);
        /** Display string */
        private String s="PEG Simulation";
        
        SimSettings(){
            super();
            addProperty("Pitch Size",pitchSize,Settings.EDIT_DOUBLE);
            addProperty("Step Time",stepTime,Settings.EDIT_DOUBLE);
            addProperty("# of Steps",numSteps,Settings.EDIT_INTEGER);
            //addProperty("max Steps",maxSteps,Settings.EDIT_INTEGER);
            addProperty("Preset Game",gameType,Settings.EDIT_COMBO);
            //addProperty("# of Teams",numTeams,Settings.EDIT_INTEGER);
            initEventListening();
        }
        
        public void stateChanged(ChangeEvent e) {
            //System.out.println("simulation prop change: "+e.getPropertyName());
            if(e.getSource()==stepTime){      fireActionPerformed("animation");run();
            } else if(e.getSource()==numSteps){fireActionPerformed("animation");run();
            } else if(e.getSource()==pitchSize){System.out.println("Nonfunctional! Starting locs should reference this value!");initStartingLocations();run();
            } else if(e.getSource()==maxSteps){System.out.println("nonfunctional!");
            } else if(e.getSource()==numTeams){System.out.println("nonfunctional!");
            } else if(e.getSource()==gameType){SimulationFactory.setSimulation(Simulation.this,getGameType());run();fireActionPerformed("reset");
            }
        }
    }
}