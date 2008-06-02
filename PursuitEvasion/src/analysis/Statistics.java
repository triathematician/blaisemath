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
import specto.plottable.PlaneFunction2D;
import scio.function.BoundedFunction;

/**
 * Static library of methods to perform batch runs of simulations for a variety of initial conditions.
 * @author Elisha Peterson
 */
public class Statistics extends FiresChangeEvents {
    
    /** Basic simulation. */
    HashMap<Valuation,Vector<Double>> fullMetrics;
    /** Simulation evaluated by subset of players. */
    HashMap<Valuation,Vector<Double>> subsetMetrics;
    /** For simulation restricted to a few players. */
    HashMap<Valuation,Vector<Double>> partialMetrics;

    public Statistics() {}
    
//    /** Returns PlaneFunction2D corresponding to the above value function. */
//    public PlaneFunction2D getInitialPositionTestPlot(Simulation sim){
//        return new PlaneFunction2D(new InitialPositionTestFunction(sim));
//    }
    
    
    /** Resets the statistical data with a given collection of valuations, and a given size. */
    public void reset(Collection<Valuation> vals,int n){
        fullMetrics = new HashMap<Valuation,Vector<Double>>();
        subsetMetrics = new HashMap<Valuation,Vector<Double>>();
        partialMetrics = new HashMap<Valuation,Vector<Double>>();
        for(Valuation v:vals) {
            fullMetrics.put(v, new Vector<Double>(n));
            subsetMetrics.put(v, new Vector<Double>(n));
            partialMetrics.put(v, new Vector<Double>(n));
        }
    }
    
    /** This method takes the data stored in the DataLog class for a particular simulation and stores it in this class, to allow
     * for statistical data output.
     * @param dl The DataLog captured during the simulation
     */
    public void captureData(Valuation v,DataLog fullRunData, DataLog partialRunData){
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
    
    private static class AverageData { double avg; int nulls; }
    
    /** computes total, average, # nulls */
    private static AverageData computeAverages(Collection<Double> values, double max) {
        AverageData result = new AverageData();
        result.avg = 0.0;
        result.nulls = 0;
        for(Double d : values) {
            if (d == null || d.isInfinite() || d.isNaN()) {
                result.nulls++;
            } else {
                if(d > max) { System.out.println(d); }
                result.avg += d;
            }
        }
        result.avg /= (values.size() - result.nulls);
        return result;
    }
    
    /** Outputs results of a particular valuation. */
    public void outputValuation(Valuation val, JTextArea textArea){
        int numRuns = fullMetrics.get(val).size();
        AverageData full = computeAverages(fullMetrics.get(val), 100000);
        AverageData subset = computeAverages(subsetMetrics.get(val), 10000);
        AverageData partial = computeAverages(partialMetrics.get(val), 100000);
        textArea.append("Metric " + val.toString() + ":\n"
                 + "  FULL: average " + formatter.format(full.avg)
                 + " over " + numRuns + " runs (" + full.nulls + " null values)." + "\n"
                 + (val.isCooperationTesting() ? 
                      "  SUBSET: average " + formatter.format(subset.avg) + "\n"
                    + "  PARTIAL: average " + formatter.format(partial.avg)
                    + " over " + numRuns + " runs (" + partial.nulls + " null values)." + "\n"
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
        // delete previous content
        try {textArea.getDocument().remove(0, textArea.getDocument().getLength()-1);}catch(Exception e){}
        textArea.append("This data may be copy/pasted directly into Microsoft Excel.\n To do so, hit Ctrl+A, then Ctrl+C," +
                "\n and then paste into an (empty) Excel worksheet.\n ---- \n");
        int numTimes = 0;
        for(Valuation v: fullMetrics.keySet()) { numTimes = fullMetrics.get(v).size(); break; }
        for(Valuation v: fullMetrics.keySet()) {
            textArea.append(v.toString() + (v.isCooperationTesting() ? "\t(subset)\t(partial)\t" : "\t"));
        }
        textArea.append("\n");
        for(int n=0; n<numTimes; n++) {
            for(Valuation v: fullMetrics.keySet()) {
                textArea.append(formatter.format(fullMetrics.get(v).get(n)) + "\t"
                        + (v.isCooperationTesting() ?
                            formatter.format(subsetMetrics.get(v).get(n)) + "\t"
                            + formatter.format(partialMetrics.get(v).get(n)) + "\t"
                            : ""));
            }
            textArea.append("\n");
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