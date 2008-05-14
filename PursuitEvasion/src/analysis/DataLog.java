/*
 * DataLog.java
 * Created on Nov 5, 2007, 10:26:59 AM
 */

package analysis;

import java.util.logging.Level;
import java.util.logging.Logger;
import scio.function.FunctionValueException;
import utility.*;
import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Vector;
import javax.swing.JTextArea;
import metrics.Goal;
import metrics.Valuation;
import simulation.Agent;
import simulation.Simulation;
import simulation.Team;
import scio.coordinate.R2;
import sequor.FiresChangeEvents;
import specto.Plottable;
import specto.PlottableGroup;
import specto.dynamicplottable.CirclePoint2D;
import specto.dynamicplottable.InitialPointSet2D;
import specto.dynamicplottable.Point2D;
import specto.plotpanel.Plot2D;
import specto.plottable.PointSet2D;
import specto.visometry.Euclidean2;

/**
 * Logs data from a simulation's run. Can be used to plot various results.
 * <p>
 * The log contains the following information:
 * <li>
 * <ul>Paths of all players throughout the simulation;
 * <ul>Value functions of all teams throughout the simulation;
 * <ul>Significant event for one or two agents = String + Agent + Agent + time
 * </li>
 * </p>
 * @author ae3263
 */
public class DataLog extends FiresChangeEvents {
    Simulation sim;
    Plot2D mainPlot;
    Plot2D valuePlot;
    HashMap<Agent,Vector<R2>> agentPaths;
    HashMap<Valuation,Vector<R2>> teamMetrics;
    Vector<SignificantEvent> significantEvents;
    
    /** This stores the value of primary interest in a particular simulation, such as the time to capture, whether capture has occurred, etc. */
    Double primaryOutput;
    
    /** Stores the paths of the agents */
    HashMap<Team,PlottableGroup<Euclidean2>> mainDisplayGroup;
    HashMap<Team,PlottableGroup<Euclidean2>> valueDisplayGroup;
    
    /** Stores positions of captures */
    PlottableGroup<Euclidean2> captureGroup;
    
    
    // CONSTRUCTORS
    
    /** Basic initializer */
    public DataLog(){}
    
    /** Initializes to the agents/teams of a particular simulation. */
    public DataLog(Simulation sim){
        initialize(sim,null,null);
        logAll(0,null);
    }
    

    // BEAN PATTERNS    

    public Double getPrimaryOutput() {return primaryOutput;}

    public void setPrimaryOutput(Double primaryOutput) {this.primaryOutput = primaryOutput;}    
    
    /** Returns position of agent at a particular time. */
    public R2 agentAt(Agent a,int step){
        return agentPaths.get(a).get(step);
    }
    
    /** Returns length of the log. */
    public int size(){
        return agentPaths.values().iterator().next().size();
    }
    
    
    // OTHER METHODS    
    
    /** Called by the initialization methods */
    private void addAgentVisuals(Team t, PlottableGroup<Euclidean2> teamGroup) {
        for (Agent a : t) {
            // add the dynamic point
            CirclePoint2D cp = new CirclePoint2D(a.getPointModel());
            cp.setLabel(a.toString());
            cp.setColor(a.getColor());
            cp.addRadius(a.getCommRange());
            cp.addRadius(a.getSensorRange());
            // add radius around the point
            teamGroup.add(cp);
            // add path attached to the point
            teamGroup.add(new InitialPointSet2D(cp, agentPaths.get(a), a.getColor()));
        }
    }    
    
    /** Called when the simulation is first initialized ONLY. */
    public void initialize(Simulation s,Plot2D mainDisplay,Plot2D valueDisplay){
        // tells simulation to refer to this class
        s.setDataLog(this);
        sim=s;
        mainPlot=mainDisplay;
        valuePlot=valueDisplay;
        
        // initializes vectors
        teamMetrics=new HashMap<Valuation,Vector<R2>>();
        significantEvents=new Vector<SignificantEvent>();        
        agentPaths=new HashMap<Agent,Vector<R2>>();
        for(Team t:s.getTeams()){
            for(Agent a:t){
                agentPaths.put(a,new Vector<R2>(s.getNumSteps()));
            }
            for(Valuation v:t.metrics){
                teamMetrics.put(v,new Vector<R2>(s.getNumSteps()));
            }
        }
        
        if(mainDisplay!=null){
            // initializes the main display groups
            mainDisplayGroup=new HashMap<Team,PlottableGroup<Euclidean2>>(s.getNumTeams());
            for(Team t:s.getTeams()){            
                PlottableGroup<Euclidean2> teamGroup=new PlottableGroup<Euclidean2>();
                addAgentVisuals(t,teamGroup);
                teamGroup.add(new DynamicTeamGraph(t,this));
                mainDisplay.add(teamGroup);
                mainDisplayGroup.put(t, teamGroup);
            }
            // initializes capture groups
            captureGroup=new PlottableGroup<Euclidean2>();
            mainDisplay.add(captureGroup);
        }
        
        if(valueDisplay!=null){
            // initializes the secondary display group
            valueDisplayGroup=new HashMap<Team,PlottableGroup<Euclidean2>>(s.getNumTeams());
            for(Team t:s.getTeams()){
                PlottableGroup<Euclidean2> valueGroup=new PlottableGroup<Euclidean2>();
                for(Valuation v:t.metrics){
                    valueGroup.add(new PointSet2D(teamMetrics.get(v),t.getColor()));
                }                
                valueDisplay.add(valueGroup);
                valueDisplayGroup.put(t, valueGroup);
            }
        }
    }
    
    /** Called when the number of <b>players</b> is changed in any way. If the number of teams is used, teh DataLog must be completely reinitialized. */
    public void initializeNumbersOnly(){
        // preRun agent path vectors, since the number of players may change
        agentPaths.clear();
        for(Team t:sim.getTeams()){
            for(Agent a:t){
                agentPaths.put(a,new Vector<R2>(sim.getNumSteps()));
            }
            for(Valuation v:t.metrics){
                if(teamMetrics.get(v)==null){
                    teamMetrics.put(v, new Vector<R2>(sim.getNumSteps()));
                }
            }
        }
        // preRun the agent visuals; the team display groups do not change!
        if(mainPlot!=null){
            for(Team t:sim.getTeams()){            
                PlottableGroup<Euclidean2> teamGroup=mainDisplayGroup.get(t);
                if(teamGroup==null){
                    teamGroup=new PlottableGroup<Euclidean2>();
                }else{
                    teamGroup.clear();
                }
                addAgentVisuals(t,teamGroup);
                teamGroup.add(new DynamicTeamGraph(t,this));
            }
        }
    }
    
    /** Resets colors of all points and paths. */
    public void recolor(){
        for(Team t:mainDisplayGroup.keySet()){
            for(Plottable p:mainDisplayGroup.get(t).getElements()){
                p.setColor(t.getColor());
            }
            for(Plottable p:valueDisplayGroup.get(t).getElements()){
                p.setColor(t.getColor());
            }
        }
    }
    
    /** Called when the simulation is run again with the same teams. */
    public void preRun(){
        for(Vector<R2> vv:agentPaths.values()){vv.clear();}
        for(Vector<R2> vv:teamMetrics.values()){vv.clear();}
        significantEvents.clear();
        captureGroup.clear();
        logAll(0,null);
    }
    
    /** Goes through all agents/teams in list and logs desired values. */
    public void logAll(int timeStep,DistanceTable dt){
        for(Agent a:agentPaths.keySet()){
            agentPaths.get(a).add(new R2(a.getPosition()));
        }
        if(dt!=null){
            for(Valuation v:teamMetrics.keySet()){
                try {
                    teamMetrics.get(v).add(new R2(timeStep, v.getValue(dt)));
                } catch (FunctionValueException ex) {
                    Logger.getLogger(DataLog.class.getName()).log(Level.SEVERE, null, ex);
                }                
            }
        }
    }
    
    /** Logs captures of a specified team over another team, within the specified distance.
     * Also removes the captured elements from activity.
     * @param dt            The DistanceTable containing the distances
     * @param g             The Goal representing the capturing
     * @param capDistance   The distance within which capture occurs
     */
    public void logCaptures(DistanceTable dt,Goal g,double time){
        AgentPair closest=dt.min(g.getOwner().getActiveAgents(),g.getTarget().getActiveAgents());
        while((closest!=null)&&(closest.getDistance()<g.getThreshhold())){
            logEvent(g.getOwner(),closest.getFirst(),g.getTarget(),closest.getSecond(),"Capture",time);
            Point2D adder=new Point2D(closest.getFirst().loc.plus(closest.getSecond().loc).multipliedBy(0.5),Color.RED,false);
            adder.style.setValue(Point2D.CIRCLE);
            captureGroup.add(adder);
            dt.removeAgents(closest);
            g.getOwner().deactivate(closest.getFirst());
            g.getTarget().deactivate(closest.getSecond());
            closest=dt.min(g.getOwner().getActiveAgents(),g.getTarget().getActiveAgents());
        }
    }
    
    /** Called after the simulation is completed. */
    public void postRun(){}

    /** Adds a significant event. */
    public void logEvent(Team owner, Agent first, Team target, Agent second, String string, double time) {
        significantEvents.add(new SignificantEvent(owner,first,target,second,string,time));
    }
    
    /** Outputs results to standard output. */
    public void output(JTextArea textArea){
        for(SignificantEvent se:significantEvents){
            textArea.append(se.toString()+"\n");
        }
    }
    
    
    // INNER CLASSES
    
    /** 
     * This subclass is used to store significant Agent or Agent/Agent events, such as the
     * capture of a particular player, communications, etc. It stores a string to describe
     * the event, the two agents, and the time at which it occurred.
     */
    class SignificantEvent{
        Team teamOriginator;
        Agent originator;
        Team teamReceiver;
        Agent receiver;
        String description;
        double time;

        public SignificantEvent(Team teamOriginator,Agent originator,Team teamReceiver,Agent receiver, String description, double time) {
            this.teamOriginator = teamOriginator;
            this.originator = originator;
            this.teamReceiver = teamReceiver;
            this.receiver = receiver;
            this.description = description;
            this.time = time;
        }
        
        @Override
        public String toString(){
            return description+" at time "+DecimalFormat.getNumberInstance().format(time)
                    +" ("+teamOriginator+":"+originator+" >> "+teamReceiver+":"+receiver+")";
        }
    } // CLASS DataLog.SignificantEvent //

    
    // UNSUPPORTED OPERATIONS
    
    @Override
    public FiresChangeEvents clone() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void copyValuesFrom(FiresChangeEvents parent) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setValue(String s) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public PropertyChangeEvent getChangeEvent(String s) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
