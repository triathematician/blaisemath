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

import Blaise.BPlot2D;
import Blaise.BPlotPath2D;
import Model.PointRangeModel;
import Model.Settings;
import agent.Agent;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import javax.swing.event.ChangeEvent;
import agent.Team;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import javax.swing.JTree;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import utility.DistanceTable;

/**
 *
 * @author ae3263
 */
public class Simulation implements ActionListener,PropertyChangeListener {
    

// PROPERTIES   
    
    /** Contains all settings used to run the simulation. */
    public SimulationSettings ss;
    /** Has complete/precise knowledge of playing field. */
    Agent bird;
    /** Contains list of teams involved. */
    ArrayList<Team> teams;
    /** Table of distances (for speed of simulation) */
    DistanceTable dist;
    /** The time stamp when iterating */
    double time;

    
// CONSTRUCTORS    
    
    /** Standard constructor */
    public Simulation(){this(SimulationSettings.GOAL_PE);}
    /** Constructs given a type of game 
     * @param gameType the type of game to simulate */
    public Simulation(int gameType){this(new SimulationSettings(gameType));}
    /** Constructs given a settings class 
     * @param ss a collection of simulation settings */
    public Simulation(SimulationSettings ss){setSettings(ss);}

    
// METHODS: INITIALIZERS   
    
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
   
    
// GETTERS & SETTERS
    
    public SimulationSettings getSettings(){return ss;}
    /** Resets the settings of the simulation. Note that this completely resets the simulation!
     * This is very important, since ALL changes to settings in the simulation should be told to
     * pass through the SimulationSettings class!!
     * @param newValue the new settings to use */
    public void setSettings(SimulationSettings newValue){
        if(newValue==null){return;}
        // remove previous event listeners so classes recycle... i'm not sure this is necessary!
        if(ss==null){ss=newValue;ss.addPropertyChangeListener(this);}
        else{
            for(Team t:teams){t.removeActionListener(this);}
            if(newValue!=ss){ss.removeAllPropertyChangeListeners();ss=newValue;ss.addPropertyChangeListener(this);}
        }
        teams=ss.getTeams();for(Team t:teams){t.addActionListener(this);}
        bird=new Agent();
        fireActionPerformed(new ActionEvent(this,ActionEvent.ACTION_PERFORMED,"reset"));
        run();
    }
    
    
// METHODS: DEPLOY SIMULATION
    
    /** Runs default number of steps */
    public void run(){run(ss.getNumSteps());}
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
                fireActionPerformed("Team "+t.ts.toString()+" achieved its goal at time "+time+"!");
            }
        }
    }  
    
// METHODS: RETURN RESULTS OF SIMULATION IN VARIOUS FORMATS
    
    /** Returns point models corresponding to initial conditions */
    public void placeInitialPointsOn(BPlot2D p){for(Team t:teams){t.placeInitialPointsOn(p);}}
    
    /** Recomputes animation settings for a plot window */
    public void setAnimationCycle(BPlot2D p){
        p.setAnimateCycle(ss.getNumSteps()+10);
        if(ss.getStepTime()>.4){p.setAnimateDelay(100);}
        else{p.setAnimateDelay((int)(250*ss.getStepTime()));}
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
        DefaultMutableTreeNode top=new DefaultMutableTreeNode(ss);
        for(Team t:teams){top.add(t.getTreeNode());}
        return new DefaultTreeModel(top);
    }

    
// EVENT HANDLING    
    
    /** Almost all changes pass through this method. This is a very important method! */
    public void propertyChange(PropertyChangeEvent e) {
        //System.out.println("simulation prop change: "+e.getPropertyName());
        if(e.getPropertyName()=="# of Steps"){fireActionPerformed(new ActionEvent(this,ActionEvent.ACTION_PERFORMED,"animation"));run();}
        else if(e.getPropertyName()=="Step Time"){fireActionPerformed(new ActionEvent(this,ActionEvent.ACTION_PERFORMED,"animation"));run();}
        else if(e.getPropertyName()=="Pitch Size"){System.out.println("Starting locs should reference this value!");initStartingLocations();run();}
        else if(e.getPropertyName()=="Preset Game"){setSettings(ss);}
        else if(e.getPropertyName()=="max Steps"){System.out.println("nonfunctional!");}
        else if(e.getPropertyName()=="# of Teams"){System.out.println("nonfunctional!");}
    }
    
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
        //System.out.println("simulation action performed: "+e.getActionCommand());
        if(e.getActionCommand()=="rerun"){run();}
        else if(e.getActionCommand()=="redraw"){fireActionPerformed(e);}
        else if(e.getActionCommand()=="reset"){fireActionPerformed(e);}
    }
}