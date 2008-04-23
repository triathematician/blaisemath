/*
 * DataLog.java
 * Created on Nov 5, 2007, 10:26:59 AM
 */

package peg1d;

import java.awt.Color;
import specto.dynamicplottable.Point2D;
import java.text.DecimalFormat;
import java.util.Vector;
import javax.swing.JTextArea;
import scio.coordinate.R2;
import specto.PlottableGroup;
import specto.dynamicplottable.InitialPointSet2D;
import specto.plotpanel.Plot2D;
import specto.visometry.Euclidean2;

/**
 * Logs data from a simulation's run.
 * @author Elisha Peterson
 */
public class DataLog {
    Simulation sim;
    Plot2D mainPlot;
    Vector<SignificantEvent> significantEvents;
    Boolean pursuersWin = null;
    Double time = null;
    
    /** Stores the paths of the agents */
    PlottableGroup<Euclidean2> pursuerDisplayGroup;
    PlottableGroup<Euclidean2> evaderDisplayGroup;
    PlottableGroup<Euclidean2> goalGroup;
    
    /** Stores positions of captures */
    PlottableGroup<Euclidean2> captureGroup;
    
    
    // CONSTRUCTORS
    
    /** Basic initializer */
    public DataLog(){}
    
    /** Initializes to the agents/teams of a particular simulation. */
    public DataLog(Simulation sim){
        initialize(sim,null);
        significantEvents=new Vector<SignificantEvent>();
    }
    
    
    // OTHER METHODS    
    
    /** Called when the simulation is first initialized ONLY. */
    public void initialize(Simulation s,Plot2D mainDisplay){
        if(mainDisplay==null || s==null){return;}
        // tells simulation to refer to this class
        //s.setDataLog(this);
        mainPlot=mainDisplay;
        sim=s;
        pursuerDisplayGroup=new PlottableGroup<Euclidean2>();
        evaderDisplayGroup=new PlottableGroup<Euclidean2>();
        for(InitialPointSet2D p:sim.pursuers){pursuerDisplayGroup.add(p);}
        for(InitialPointSet2D p:sim.evaders){evaderDisplayGroup.add(p);}
        recolor();
        goalGroup=new PlottableGroup<Euclidean2>();
        goalGroup.add(sim.getGoalLine());
        captureGroup=new PlottableGroup<Euclidean2>();
        mainPlot.add(captureGroup);
        mainPlot.add(pursuerDisplayGroup);
        mainPlot.add(evaderDisplayGroup);
        mainPlot.add(goalGroup);
    }
    
    /** Called when the number of <b>players</b> is changed in any way. If the number of teams is used, teh DataLog must be completely reinitialized. */
    public void initializeNumbersOnly(){
        pursuerDisplayGroup.clear();
        evaderDisplayGroup.clear();
        for(InitialPointSet2D p:sim.pursuers){pursuerDisplayGroup.add(p);}
        for(InitialPointSet2D p:sim.evaders){evaderDisplayGroup.add(p);}
        recolor();
    }
    
    /** Resets colors of all points and paths. */
    public void recolor(){
        pursuerDisplayGroup.setColor(sim.getPColor());
        evaderDisplayGroup.setColor(sim.getEColor());
    }
    
    /** Called when the simulation is run again with the same teams. */
    public void preRun(){
        significantEvents.clear();
        pursuersWin = null;
        if(captureGroup!=null){captureGroup.clear();}
    }
    
    /** Logs captures of a specified team over another team, within the specified distance.
     */
    public void logCapture(int pursuer,int evader,double pos,double time){
        significantEvents.add(new SignificantEvent(pursuer,evader,pos,time,"Capture"));            
        Point2D where=new Point2D(new R2(pos,time),Color.CYAN,false);
        where.style.setValue(Point2D.CONCENTRIC);
        if(captureGroup!=null){captureGroup.add(where);}
    }
    
    /** Called after the simulation is completed. */
    public void postRun(){
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
    static class SignificantEvent{
        Integer pursuer;
        Integer evader;
        Double position;
        Double time;
        String description;

        public SignificantEvent(int pursuer, int evader, double position, double time, String description) {
            this.pursuer = pursuer;
            this.evader = evader;
            this.position = position;
            this.time = time;
            this.description = description;
        }
        SignificantEvent(double time, String description) {
            this.time = time;
            this.description = description;
        }
        
        @Override
        public String toString(){
            if(pursuer==null){
                return description+" at time "+DecimalFormat.getNumberInstance().format(time);
            }
            return description+" at time "+DecimalFormat.getNumberInstance().format(time)
                    +" and position "+DecimalFormat.getNumberInstance().format(position)
                    +" (Pursuer "+pursuer+" >> Evader "+evader+")";
        }
    } // CLASS DataLog.SignificantEvent //
}
