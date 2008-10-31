/*
 * Statistics.java
 * Created on Aug 28, 2007, 10:40:52 AM
 */

package analysis;

import metrics.Valuation;
import simulation.*;
import java.beans.PropertyChangeEvent;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Vector;
import javax.swing.JTextArea;
import scio.coordinate.R2;
import scio.function.FunctionValueException;
import sequor.FiresChangeEvents;
import scio.function.BoundedFunction;

/**
 * Static library of methods to perform batch runs of simulations for a variety of initial conditions.
 * @author Elisha Peterson
 */
public class Statistics extends FiresChangeEvents {
    
    /** Stores metrics given the standard simulation. */
    HashMap<Valuation,Vector<Double>> fullMetrics;
    /** Stores metrics as perceived by chosen subset of players. */
    HashMap<Valuation,Vector<Double>> subsetMetrics;
    /** Stores metrics of the game when a subset of players participates. */
    HashMap<Valuation,Vector<Double>> partialMetrics;
    
    /** Stores starting values of teams for each run. */
    HashMap<Team,Vector<Vector<R2>>> startingLoc;
    
    public Statistics() {}
    
//    /** Returns PlaneFunction2D corresponding to the above value function. */
//    public PlaneFunction2D getInitialPositionTestPlot(Simulation sim){
//        return new PlaneFunction2D(new InitialPositionTestFunction(sim));
//    }
    
    
    /** Resets the statistical data with a given collection of valuations, and a given size. */
    public void reset(Collection<Valuation> vals,Collection<Team> teams,int n){
        fullMetrics = new HashMap<Valuation,Vector<Double>>();
        subsetMetrics = new HashMap<Valuation,Vector<Double>>();
        partialMetrics = new HashMap<Valuation,Vector<Double>>();
        startingLoc = new HashMap<Team,Vector<Vector<R2>>>();
        for(Valuation v:vals) {
            fullMetrics.put(v, new Vector<Double>(n));
            subsetMetrics.put(v, new Vector<Double>(n));
            partialMetrics.put(v, new Vector<Double>(n));
        }
        for(Team t:teams) {
            startingLoc.put(t, new Vector<Vector<R2>>(n));
        }
    }

    /** This method stores the starting positions for each team for a particular simulation.
     * @param teams the collection of teams in the simulation
     */
    public void captureLocs(Collection<Team> teams){
        for(Team t:teams){
            R2[] sl=t.getStartingLocations();
            Vector<R2> vr=new Vector<R2>();
            for (int i = 0; i < sl.length; i++) {vr.add(sl[i]);}
            startingLoc.get(t).add(vr);
        }
    }
    
    /** This method takes the data stored in the SimulationLog class for a particular simulation and stores it in this class, to allow
     * for statistical data output.
     * @param v the particular valuation to store data for
     * @param fullRunData the SimulationLog captured during the simulation with all players
     * @param partialRunData the datalog captured during the simulation with only part of the players
     */
    public void captureData(Valuation v,SimulationLog fullRunData, SimulationLog partialRunData){
        if (fullRunData != null) {
            fullMetrics.get(v).add(fullRunData.teamMetrics.get(v).lastElement().y);
            subsetMetrics.get(v).add(fullRunData.partialTeamMetrics.get(v).lastElement().y);
        }
        if (partialRunData != null) {
            partialMetrics.get(v).add(partialRunData.teamMetrics.get(v).lastElement().y);
        }
    }
    
    // formatter
    final static NumberFormat formatter = DecimalFormat.getNumberInstance();
    
    private static class AverageData { 
        double avg; double std; int runs; int nulls; 
        @Override
        public String toString() {
            return "avg/std/var (runs/nulls): "
                    + formatter.format(avg) + "/" + formatter.format(std) + "/" + formatter.format(std*std)
                    + " (" + runs + "/" + nulls + ")";
        }
        
        public final static String[] PROPERTIES = {"avg", "std", "var", "runs", "nulls"};
        
        public double getProperty(int n) {
            switch(n){
                case 0: return avg;
                case 1: return std;
                case 2: return std*std;
                case 3: return runs;
                case 4: return nulls;
            }
            return -1;
        }
    }
    
    /** computes total, average, # nulls */
    private static AverageData computeAverages(Collection<Double> values, double max) {
        AverageData result = new AverageData();
        result.avg = 0.0;
        result.std = 0.0;
        result.runs = values.size();
        result.nulls = 0;
        for(Double d : values) {
            if (d == null || d.isInfinite() || d.isNaN()) {
                result.nulls++;
            } else {
                if(d > max) { System.out.println(d); }
                result.avg += d;
                result.std += d*d;
            }
        }
        result.avg /= (result.runs - result.nulls);
        result.std = Math.sqrt(result.std / (result.runs - result.nulls) - result.avg*result.avg);
        return result;
    }
    
    /** Outputs results of a particular valuation. */
    public void outputValuation(Valuation val, JTextArea textArea){
        int numRuns = fullMetrics.get(val).size();
        AverageData full = computeAverages(fullMetrics.get(val), 100000);
        AverageData subset = computeAverages(subsetMetrics.get(val), 10000);
        AverageData partial = computeAverages(partialMetrics.get(val), 100000);
        textArea.append("Metric " + val + ":\n"
                 + "  FULL: " + full + "\n"
                 + (val.isCooperationTesting() ? 
                      "  SUBSET: " + subset + "\n"
                    + "  PARTIAL: " + partial + "\n"
                    + "  SELFISH COOPERATION: average " + formatter.format(subset.avg - full.avg) + "\n"
                    + "  ALTRUISTIC COOPERATION: average " + formatter.format(partial.avg - subset.avg) + "\n"
                    + "  TOTAL COOPERATION: average " + formatter.format(partial.avg - full.avg) + "\n"
                    : ""));
    }
    
    /** Outputs results to standard output. */
    public void output(JTextArea textArea){
        textArea.append("-----\n");
        for(Valuation val : fullMetrics.keySet()) {
            outputValuation (val, textArea);
        }
    }
    
    /** Outputs results of entire data run. */
    public void outputData(JTextArea textArea){        
        HashMap<Valuation,AverageData> fullData = new HashMap<Valuation,AverageData>();
        HashMap<Valuation,AverageData> subsetData = new HashMap<Valuation,AverageData>();
        HashMap<Valuation,AverageData> partialData = new HashMap<Valuation,AverageData>();
        for(Valuation val : fullMetrics.keySet()) { fullData.put(val, computeAverages(fullMetrics.get(val), 100000)); }
        for(Valuation val : subsetMetrics.keySet()) { subsetData.put(val, computeAverages(subsetMetrics.get(val), 100000)); }
        for(Valuation val : partialMetrics.keySet()) { partialData.put(val, computeAverages(partialMetrics.get(val), 100000)); }
        
        // delete previous content
        try {textArea.getDocument().remove(0, textArea.getDocument().getLength()-1);}catch(Exception e){}
        // helpful comments
        textArea.append("This data may be copy/pasted directly into Microsoft Excel.\n To do so, hit Ctrl+A, then Ctrl+C," +
                "\n and then paste into an (empty) Excel worksheet.\n ---- \n");
        // print the header row
        textArea.append("\t");
        for(Valuation v: fullMetrics.keySet()) {
            textArea.append(v.toString() + (v.isCooperationTesting() ? "\t(subset)\t(partial)\t" : "\t"));
        }
        for(Team t: startingLoc.keySet()){
            textArea.append(t.toString()+"\t");
        }
        textArea.append("\n");
        // print the data
        int numIterationsPerformed = 0;
        for(Valuation v: fullMetrics.keySet()) { numIterationsPerformed = fullMetrics.get(v).size(); break; }
        for(int n=0; n<numIterationsPerformed; n++) {
            textArea.append((n+1)+"\t");
            for(Valuation v: fullMetrics.keySet()) {
                textArea.append(formatter.format(fullMetrics.get(v).get(n)) + "\t"
                        + (v.isCooperationTesting() ?
                            formatter.format(subsetMetrics.get(v).get(n)) + "\t" + formatter.format(partialMetrics.get(v).get(n)) + "\t"
                            : ""));
            }
            for(Team t: startingLoc.keySet()){
                textArea.append(startingLoc.get(t).get(n)+"\t");
            }
            textArea.append("\n");
        }
        // summary information
        for(int i=0;i<=4;i++) {
            textArea.append("\n"+AverageData.PROPERTIES[i]);
            for(Valuation v:fullMetrics.keySet()) {
                textArea.append("\t" + formatter.format(fullData.get(v).getProperty(i))
                        + (v.isCooperationTesting() ?
                              "\t" + formatter.format(subsetData.get(v).getProperty(i))
                            + "\t" + formatter.format(partialData.get(v).getProperty(i))
                            : ""));
            }
        }
        textArea.append("\n");
    }

    
    // UNSUPPORTED METHODS
    
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
    
    
    
    /** This function computes the output value of a simulation given an input of initial starting location of a particular agent. */
    public class InitialPositionTestFunction implements BoundedFunction<R2,Double>{
        Simulation sim;
        public InitialPositionTestFunction(Simulation sim){this.sim=sim;}        
        public Double getValue(R2 x) throws FunctionValueException {
            return 0.0;
            //sim.getPrimaryAgent().getPointModel().setTo(x);
            //return sim.getPrimaryValue();
        }
        public Vector<Double> getValue(Vector<R2> x) throws FunctionValueException {
            //Agent changes=sim.getPrimaryAgent();
            Vector<Double> result=new Vector<Double>();
            sim.setBatchProcessing(true);
            for(R2 point:x){
                //changes.getPointModel().setTo(point);
                //result.add(sim.getPrimaryValue()>0.5?50.0:0.0); // threshhold
            }
            sim.setBatchProcessing(false);
            return result;
        }
        public Double minValue() {return 0.0;}
        public Double maxValue() {return 50.0;}
    } // INNER CLASS Statistics.InitialPositionTestFunction
}