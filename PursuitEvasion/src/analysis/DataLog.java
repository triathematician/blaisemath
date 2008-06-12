/*
 * DataLog.java
 * Created on Nov 5, 2007, 10:26:59 AM
 */

package analysis;

import java.awt.event.ActionEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import scio.function.FunctionValueException;
import utility.*;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;
import javax.swing.JTextArea;
import metrics.Valuation;
import simulation.Agent;
import simulation.Simulation;
import simulation.Team;
import scio.coordinate.R2;
import sequor.FiresChangeEvents;
import sequor.VisualControl;
import sequor.control.FLabel;
import sequor.control.FLabelBox;
import specto.PlottableGroup;
import specto.euclidean2.CirclePoint2D;
import specto.euclidean2.InitialPointSet2D;
import specto.euclidean2.Point2D;
import specto.euclidean2.Plot2D;
import specto.euclidean2.PointSet2D;
import specto.euclidean2.Euclidean2;
import specto.style.PointStyle;

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
    HashMap<Valuation,Vector<R2>> partialTeamMetrics;
    Vector<SignificantEvent> significantEvents;
        
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
        InitialPointSet2D ip;
        for (final Agent a : t) {
            // add the dynamic point
            final CirclePoint2D cp = new CirclePoint2D(a.getPointModel(),a.getColorModel());
            cp.setLabel(a.toString());
            // add radii around the point
            cp.addRadius(a.getCommRange());
            cp.addRadius(a.getSensorRange());
            a.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e) {
                    if(e.getActionCommand().equals("agentSetupChange")){
                        cp.deleteRadii();
                        cp.addRadius(a.getCommRange());
                        cp.addRadius(a.getSensorRange());
                    }
                }
            });
            // add point and path to plot
            teamGroup.add(cp);
            teamGroup.add(new PointSet2D(agentPaths.get(a), a.getColorModel()));
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
        teamMetrics = new HashMap<Valuation,Vector<R2>>();
        partialTeamMetrics = new HashMap<Valuation,Vector<R2>>();
        significantEvents=new Vector<SignificantEvent>();        
        agentPaths=new HashMap<Agent,Vector<R2>>();
        for(Team t:s.getTeams()){
            for(Agent a:t){
                agentPaths.put(a,new Vector<R2>(s.getNumSteps()));
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
        
        if(mainDisplay!=null){
            // initializes the main display groups
            mainDisplayGroup=new HashMap<Team,PlottableGroup<Euclidean2>>(s.getNumTeams());
            for(Team t:s.getTeams()){            
                PlottableGroup<Euclidean2> teamGroup=new PlottableGroup<Euclidean2>();
                teamGroup.setColorModel(t.getColorModel());
                teamGroup.setName(t.toString());
                addAgentVisuals(t,teamGroup);
                teamGroup.add(new DynamicTeamGraph(t,this));
                mainDisplay.add(teamGroup);
                mainDisplayGroup.put(t, teamGroup);
            }
            // initializes capture groups
            captureGroup=new PlottableGroup<Euclidean2>();
            captureGroup.setName("Captures");
            captureGroup.setColor(Color.RED);
            mainDisplay.add(captureGroup);
        }
        
        if(valueDisplay!=null){
            // initializes the secondary display group
            valueDisplayGroup=new HashMap<Team,PlottableGroup<Euclidean2>>(s.getNumTeams());
            FLabelBox legend = new FLabelBox(0, 0, 0, 0);
            for(final Team t:s.getTeams()){
                PlottableGroup<Euclidean2> valueGroup=new PlottableGroup<Euclidean2>();  
                valueGroup.setName(t.toString());
                PointSet2D temp;
                for(final Valuation v:t.metrics){
                    temp = new PointSet2D(teamMetrics.get(v),t.getColorModel());
                    temp.style.setValue(PointSet2D.THIN);
                    valueGroup.add(temp);
                    temp = new PointSet2D(partialTeamMetrics.get(v),t.getColorModel());
                    temp.style.setValue(PointSet2D.DOTTED);
                    valueGroup.add(temp);
                    final FLabel label = new FLabel(v, t.getColorModel(), null);
                    v.addActionListener(new ActionListener(){
                        public void actionPerformed(ActionEvent e) {
                            label.setText(v.toString());
                        }
                    });
                    legend.add(label);
                }              
                if(t.victory != null) {
                    temp = new PointSet2D(teamMetrics.get(t.victory),t.getColorModel());
                    temp.style.setValue(PointSet2D.THIN);
                    valueGroup.add(temp);
                    temp = new PointSet2D(partialTeamMetrics.get(t.victory),t.getColorModel());
                    temp.style.setValue(PointSet2D.DOTTED);
                    valueGroup.add(temp);
                    final FLabel label = new FLabel(t.victory, t.getColorModel(), null);
                    t.victory.addActionListener(new ActionListener(){
                        public void actionPerformed(ActionEvent e) {
                            label.setText(t.victory.toString());
                        }
                    });
                    legend.add(label);
                }
                valueDisplayGroup.put(t, valueGroup);
                valueDisplay.add(valueGroup);
                HashSet<VisualControl> removeThese = new HashSet<VisualControl>();
                for(VisualControl vc: valueDisplay.getControls()){
                    if(vc instanceof FLabelBox){removeThese.add(vc);}
                }
                for(VisualControl vc:removeThese){valueDisplay.remove(vc);}
                valueDisplay.add(legend,3,6);
                legend.performLayout();
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
        // preRun the agent visuals; the team display groups do not change!
        if(mainPlot!=null){
            for(Team t:sim.getTeams()){            
                PlottableGroup<Euclidean2> teamGroup=mainDisplayGroup.get(t);
                if(teamGroup==null){
                    teamGroup=new PlottableGroup<Euclidean2>();
                }else{
                    teamGroup.clear();
                }
                teamGroup.setColorModel(t.getColorModel());
                addAgentVisuals(t,teamGroup);
                teamGroup.add(new DynamicTeamGraph(t,this));
            }
        }
    }
    
    /** Resets colors of all points and paths. */
    public void recolor(){
//        for(Team t:mainDisplayGroup.keySet()){
//            for(Plottable p:mainDisplayGroup.get(t).getElements()){                
//                p.setColor(t.getColor());
//            }
//            for(Plottable p:valueDisplayGroup.get(t).getElements()){
//                p.setColor(t.getColor());
//            }
//        }
    }
    
    /** Called when the simulation is run again with the same teams. */
    public void preRun(){
        for(Vector<R2> vv:agentPaths.values()){vv.clear();}
        for(Vector<R2> vv:teamMetrics.values()){vv.clear();}
        for(Vector<R2> vv:partialTeamMetrics.values()){vv.clear();}
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
                    teamMetrics.get(v).add(new R2(timeStep, v.getValue(dt, v.getOwner())));
                    partialTeamMetrics.get(v).add(new R2(timeStep, v.getValue(dt, v.getComplement())));
                } catch (FunctionValueException ex) {
                    Logger.getLogger(DataLog.class.getName()).log(Level.SEVERE, null, ex);
                }                
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
        Point2D adder=new Point2D(second.loc,captureGroup.getColor(),false);
        adder.style.setValue(PointStyle.RING);
        captureGroup.add(adder);
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
        codeWindow.append("CODE SNIPPET: cut/paste the lines below:\n\n");
        for(Team t : sim.getTeams()) {
            logWindow.append("   * "+t+": "+t.getStartingLocations()+"\n");
            codeWindow.append("R2[] "+t+"Positions = { ");
            for(Agent a : t) {
                codeWindow.append("new R2"+a.getInitialPosition()+" ");
                if(!t.lastElement().equals(a)) { codeWindow.append(", "); }
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
