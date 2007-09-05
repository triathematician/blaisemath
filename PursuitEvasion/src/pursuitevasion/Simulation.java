/*
 * Simulation.java
 * 
 * Created on Aug 28, 2007, 10:29:14 AM
 * 
 * Author: Elisha Peterson
 * 
 * This class runs a single simulation with given settings.
 */

package pursuitevasion;

import Blaise.BPlotPath2D;
import Model.PointRangeModel;
import agent.Agent;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import javax.swing.event.ChangeEvent;
import pursuitevasion.Pitch;
import agent.Team;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import utility.DistanceTable;

/**
 *
 * @author ae3263
 */
public class Simulation implements ActionListener,ChangeListener,PropertyChangeListener {
    

// PROPERTIES   
    
    /** Contains all settings used to run the simulation. */
    public SimulationSettings ss;
    /** Has complete/precise knowledge of playing field. */
    Agent bird;
    /** The exact playing field. */
    public Pitch pitch;    
    /** Contains list of teams involved. */
    ArrayList<Team> teams;
    /** Table of distances (for speed of simulation) */
    DistanceTable dist;
    /** The time stamp when iterating */
    double time;

    
// CONSTRUCTORS    
    
    /** Standard constructor */
    public Simulation(){this(SimulationSettings.SIMPLE_PE);}
    /** Constructs given a type of game 
     * @param gameType the type of game to simulate */
    public Simulation(int gameType){initialize(new SimulationSettings(gameType));}
    /** Constructs given a settings class 
     * @param ss a collection of simulation settings */
    public Simulation(SimulationSettings ss){initialize(ss);}

    
// METHODS: INITIALIZERS    
    
    /** Performs initial setup 
     * @param ss initial settings */
    public void initialize(SimulationSettings ss){
        this.ss=ss;
        pitch=this.ss.getPitch();
        teams=this.ss.getTeams();
        bird=new Agent();
    }
    
    /** Resets the simulation. Do this whenever initial positions are changed! */
    public void reset(){
        for(Team team:teams){team.reset();}
        dist=new DistanceTable(teams);
        time=0;
    }
   
    
// METHODS: DEPLOY SIMULATION
    
    /** Tells the simulation to get going! 
     * @param numSteps  how many time steps to run the simulation   
     * @return          to be determined...
     */
    public int run(int numSteps){
        reset();
        for(int i=0;i<numSteps;i++){iterate();}
        fireActionPerformed(new ActionEvent(this,ActionEvent.ACTION_PERFORMED,"simulation"));
        return 0;
    }
    
    /** Runs a single iteration of the scenario */
    public void iterate(){
        // Begin by collecting data, communicating it to team members, and forming beliefs about playing field.
        dist.recalculate();
        for(Team t:teams){t.gatherSensoryData(dist);}
        for(Team t:teams){t.communicateSensoryData(dist);}
        for(Team t:teams){t.fuseAgentPOV();}
        
        // Assign tasks to team members, plan corresponding paths, and move
        for(Team t:teams){t.tasking.assign(t,t.ts.getGoal());}
        for(Team t:teams){t.planPaths(time,ss.getStepTime());}
        for(Team t:teams){t.move();}
        
        // Store current positions and check for endstate
        bird.remember();
        time+=ss.getStepTime();
        for(Team t:teams){
            if(t.goalAchieved(dist)){
                //System.out.println("Team "+t.toString()+" achieved its goal at time "+time+"!");
            }
        }
    }  
    
// METHODS: RETURN RESULTS OF SIMULATION IN VARIOUS FORMATS
    
    /** Returns point models corresponding to initial conditions
     * @return arraylist of pointrangemodel's suitable to add to a BPlot2D */
    public ArrayList<PointRangeModel> getInitialPoints(){
        ArrayList<PointRangeModel> result=new ArrayList<PointRangeModel>();
        for(Team t:teams){
            for(PointRangeModel prm:t.getPointModels()){
                prm.addChangeListener(this);
                result.add(prm);
            }
        }
        return result;
    }
    
    /** Returns all paths computed by the simulation
     * @return arraylist of bplotpath2d's suitable to add to a BPlot2D */
    public ArrayList<BPlotPath2D> getComputedPaths(){
        ArrayList<BPlotPath2D> result=new ArrayList<BPlotPath2D>();
        for(Team t:teams){result.addAll(t.getPlotPaths());}
        return result;
    }

    
// EVENT HANDLING    
    
    public void actionPerformed(ActionEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void stateChanged(ChangeEvent e){run(100);}

    public void propertyChange(PropertyChangeEvent evt) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
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