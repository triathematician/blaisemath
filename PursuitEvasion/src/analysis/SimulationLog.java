/*
 * SimulationLog.java
 * Created on Nov 5, 2007, 10:26:59 AM
 */

package analysis;

import scio.coordinate.R3;
import utility.*;
import java.beans.PropertyChangeEvent;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Vector;
import javax.swing.JTextArea;
import metrics.CaptureMap;
import metrics.Valuation;
import simulation.Agent;
import simulation.Simulation;
import simulation.Team;
import scio.coordinate.R2;
import sequor.FiresChangeEvents;

/**
 * Logs data from a simulation's run. Can be used to plot various results.
 * <p>
 * The log contains the following information:
 * <ul>
 * <li>Paths of all players throughout the simulation;</li>
 * <li>Value functions of all teams throughout the simulation;</li>
 * <li>Significant event for one or two agents = String + Agent + Agent + time</li>
 * </ul>
 * </p>
 * @author ae3263
 */
public class SimulationLog extends FiresChangeEvents {
    
    Simulation sim;
    
    HashMap<Agent,Vector<R2>> agentPaths;
    HashMap<Agent,Vector<R3>> agentPaths3D;
    HashMap<Valuation,Vector<R2>> teamMetrics;
    HashMap<Valuation,Vector<R2>> partialTeamMetrics;
    Vector<SignificantEvent> significantEvents;
    Vector<R2> capturePoints;
    Vector<R3> capturePoints3D;
        
    
    // CONSTRUCTORS
    
    /** Basic initializer */
    public SimulationLog(){        
    }
    
    /** Initializes to the agents/teams of a particular simulation. */
    public SimulationLog(Simulation sim){
        initialize(sim);
        logAll(0, null, null);
    }
    

    // BEAN PATTERNS    
    
    /** Returns position of agent at a particular time. */
    public R2 agentAt(Agent a,int step){
        return agentPaths.get(a).get(step);
    }
    
    /** Returns length of the log. */
    public int size(){
        return agentPaths.values().iterator().next().size();
    }
    
    /** Returns path of a particular agent. */
    public Vector<R2> pathOf(Agent a){
        return agentPaths.get(a);
    }
    
    /** Returns path of a particular agent with time coordinate. */
    Vector<R3> path3DOf(Agent a) {
        return agentPaths3D.get(a);
    }
    
    // OTHER METHODS    
        
    /** Called when the simulation is first initialized ONLY. */
    public void initialize(Simulation s){
        // tells simulation to refer to this class
        s.log = this;
        sim = s;
        
        // initializes vectors
        teamMetrics = new HashMap<Valuation,Vector<R2>>();
        partialTeamMetrics = new HashMap<Valuation,Vector<R2>>();
        significantEvents=new Vector<SignificantEvent>();        
        agentPaths=new HashMap<Agent,Vector<R2>>();
        agentPaths3D=new HashMap<Agent,Vector<R3>>();
        capturePoints=new Vector<R2>();
        capturePoints3D=new Vector<R3>();
        for(Team t:s.getTeams()){
            for(Agent a:t.agents){
                agentPaths.put(a,new Vector<R2>(s.getNumSteps()));
                agentPaths3D.put(a,new Vector<R3>(s.getNumSteps()));
            }
            for(Valuation v:t.metrics){
                teamMetrics.put(v,new Vector<R2>(s.getNumSteps()));
                partialTeamMetrics.put(v,new Vector<R2>(s.getNumSteps()));
            }
            if(t.victory != null){
                teamMetrics.put(t.victory,new Vector<R2>(s.getNumSteps()));
                partialTeamMetrics.put(t.victory,new Vector<R2>(s.getNumSteps()));
            }
        }
    }
    
    /** Called when the number of <b>players</b> is changed in any way. If the number of teams is used, teh SimulationLog must be completely reinitialized. */
    public void initializeNumbersOnly(){
        // preRun agent path vectors, since the number of players may change
        agentPaths.clear();
        agentPaths3D.clear();
        for(Team t:sim.getTeams()){
            for(Agent a:t.agents){
                agentPaths.put(a,new Vector<R2>(sim.getNumSteps()));
                agentPaths3D.put(a,new Vector<R3>(sim.getNumSteps()));
            }
            for(Valuation v:t.metrics){
                if(teamMetrics.get(v)==null){
                    teamMetrics.put(v, new Vector<R2>(sim.getNumSteps()));
                }
                if(partialTeamMetrics.get(v)==null){
                    partialTeamMetrics.put(v, new Vector<R2>(sim.getNumSteps()));
                }                
            }
            if(t.victory != null && teamMetrics.get(t.victory) == null){
                teamMetrics.put(t.victory, new Vector<R2>(sim.getNumSteps()));
            }
            if(t.victory != null && partialTeamMetrics.get(t.victory) == null){
                partialTeamMetrics.put(t.victory, new Vector<R2>(sim.getNumSteps()));
            }
        }
    }
    
    /** Called when the simulation is run again with the same teams. */
    public void preRun(){
        for(Vector<R2> vv:agentPaths.values()){vv.clear();}
        for(Vector<R3> vv:agentPaths3D.values()){vv.clear();}
        for(Vector<R2> vv:teamMetrics.values()){vv.clear();}
        for(Vector<R2> vv:partialTeamMetrics.values()){vv.clear();}
        significantEvents.clear();
        capturePoints.clear();
        capturePoints3D.clear();
        logAll(0, null, null);
    }
    
    /** Goes through all agents/teams in list and logs desired values. */
    public void logAll(int timeStep, DistanceTable dt, CaptureMap cap){
        for(Agent a:agentPaths.keySet()){
            agentPaths.get(a).add(new R2(a.loc.x,a.loc.y));
            if(timeStep==0 || a.isActive()) {
                agentPaths3D.get(a).add(new R3(a.loc.x,a.loc.y,timeStep*sim.getStepTime()));
            }
        }
        if(dt!=null){
            for(Valuation v:teamMetrics.keySet()){
                teamMetrics.get(v).add(new R2(timeStep, v.getValue(dt, cap, v.getOwner().agents)));
                partialTeamMetrics.get(v).add(new R2(timeStep, v.getValue(dt, cap, v.getComplement())));
            }
        }
    }
    
    /** Logs captures of a specified team over another team, within the specified distanceTo.
     * Also removes the captured elements from activity.
     * @param dt            The DistanceTable containing the distances
     * @param g             The Goal representing the capturing
     * @param capDistance   The distanceTo within which capture occurs
     */
    public void logCaptureEvent(Team owner, Agent first, Team target, Agent second, String string, DistanceTable dt, double time) {
        logEvent(owner, first, target, second, string, time);
        capturePoints.add(new R2(second.loc));
        capturePoints3D.add(new R3(second.loc.x,second.loc.y,time));
    }
    
    /** Called after the simulation is completed. */
    public void postRun(){}

    /** Adds a significant event. */
    public void logEvent(Team owner, Agent first, Team target, Agent second, String string, double time) {
        significantEvents.add(new SignificantEvent(owner,first,target,second,string,time));
    }
    
    
    // PRINTING RESULTS TO WINDOW
    
    /** Outputs results to standard output. */
    public void output(JTextArea textArea){
        textArea.append(": Significant Events\n");
        for(SignificantEvent se:significantEvents){
            textArea.append("   * " + se.toString() + "\n");
        }
    }

    /** Outputs starting locations of teams. */
    public void printStartingLocations(JTextArea logWindow, JTextArea codeWindow) {
        logWindow.append(": Starting Locations\n");
        codeWindow.append("// CODE SNIPPET: cut/paste the lines below:\n\n");
        for(Team t : sim.getTeams()) {
            Vector<R2> vr=new Vector<R2>();
            for (int i = 0; i < t.getStartingLocations().length; i++) {vr.add(t.getStartingLocations()[i]);}
            logWindow.append("   * "+t+": "+vr+"\n");
            codeWindow.append("R2[] "+t+"Positions = { ");
            for(Agent a : t.agents) {
                codeWindow.append("new R2"+a.getInitialPosition()+" ");
                if(!t.agents.lastElement().equals(a)) { codeWindow.append(", "); }
            }
            codeWindow.append("};\n");
            codeWindow.append("teams.get("+sim.getTeams().indexOf(t)+").setStartingLocations("+t+"Positions);\n");
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
            String result = description+" at time "+DecimalFormat.getNumberInstance().format(time);
            return teamOriginator == null ? result : 
                    result+" ("+teamOriginator+(originator!=null?":"+originator:"")+" >> "+teamReceiver+(receiver!=null?":"+receiver:"")+")";
        }
    } // CLASS SimulationLog.SignificantEvent //

    
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
