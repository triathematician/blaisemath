/*
 * DataLog.java
 * Created on Nov 5, 2007, 10:26:59 AM
 */

package utility;

import goal.Goal;
import java.util.HashMap;
import java.util.Vector;
import simulation.Agent;
import simulation.Simulation;
import simulation.Team;
import scio.coordinate.R2;
import specto.plottable.PointSet2D;

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
public class DataLog {
    HashMap<Agent,Vector<R2>> agentPaths;
    HashMap<Team,Vector<R2>> teamValues;
    Vector<SignificantEvent> significantEvents;
    
    /** Initializes to the agents/teams of a particular simulation. */
    public DataLog(Simulation sim){
        agentPaths=new HashMap<Agent,Vector<R2>>();
        teamValues=new HashMap<Team,Vector<R2>>();
        significantEvents=new Vector<SignificantEvent>();
        for(Team t:sim.getTeams()){
            initialize(t);
        }
        logAll(0);
    }
    
    /** Called when the simulation is first initialized. */
    public void initialize(Team t){
        for(Agent a:t){
            agentPaths.put(a,new Vector<R2>());
        }
        teamValues.put(t,new Vector<R2>());
    }
    
    /** Called when the simulation is run again with the same teams. */
    public void reset(){
        for(Vector<R2> vv:agentPaths.values()){vv.clear();}
        for(Vector<R2> vv:teamValues.values()){vv.clear();}
        significantEvents.clear();
        logAll(0);
    }
    
    /** Returns paths associated with all agents. */
    public Vector<PointSet2D> getPlotPaths(){
        Vector<PointSet2D> result=new Vector<PointSet2D>();
        for(Agent a:agentPaths.keySet()){
            result.add(new PointSet2D(agentPaths.get(a),a.getColor()));
        }
        return result;
    }
    
    /** Returns value plots associated with teams. */
    public Vector<PointSet2D> getValuePlots(){
        Vector<PointSet2D> result=new Vector<PointSet2D>();
        for(Team t:teamValues.keySet()){
            result.add(new PointSet2D(teamValues.get(t),t.getColor()));
        }
        return result;
    }
    
    /** Returns position of agent at a particular time. */
    public R2 agentAt(Agent a,int step){
        return agentPaths.get(a).get(step);
    }
    
    /** Returns length of the log. */
    public int size(){
        return agentPaths.values().iterator().next().size();
    }
    
    /** Goes through all agents/teams in list and logs desired values. */
    public void logAll(int timeStep){
        for(Agent a:agentPaths.keySet()){
            agentPaths.get(a).add(new R2(a.getPosition()));
        }
        for(Team t:teamValues.keySet()){
            teamValues.get(t).add(new R2(timeStep,t.getValue()));
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
        while((closest!=null)&&(closest.distance<g.getThreshhold())){
            significantEvents.add(new SignificantEvent(closest.first,closest.second,"capture",time));
            dt.removeAgents(closest);
            g.getOwner().deactivate(closest.first);
            g.getTarget().deactivate(closest.second);
            closest=dt.min(g.getOwner().getActiveAgents(),g.getTarget().getActiveAgents());
        }
    }
    
    /** Outputs results to standard output. */
    public void output(){
        for(SignificantEvent se:significantEvents){
            System.out.println(se.toString());
        }
    }
    
    
    /** 
     * This subclass is used to store significant Agent or Agent/Agent events, such as the
     * capture of a particular player, communications, etc. It stores a string to describe
     * the event, the two agents, and the time at which it occurred.
     */
    class SignificantEvent{
        Agent originator;
        Agent receiver;
        String description;
        double time;

        public SignificantEvent(Agent originator, Agent receiver, String description, double time) {
            this.originator = originator;
            this.receiver = receiver;
            this.description = description;
            this.time = time;
        }
        
        @Override
        public String toString(){
            return description+" at time "+time+" (agents "+originator+" and "+receiver+")";
        }
    } // CLASS SignificantEvent //
}
