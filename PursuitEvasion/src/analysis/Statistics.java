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
    
    HashMap<Valuation,Vector<Double>> fullResultMetrics;
    HashMap<Valuation,Vector<Double>> partialResultMetrics;
    HashMap<Valuation,Vector<Double>> partialRunMetrics;

    public Statistics() {}
    
//    /** Returns PlaneFunction2D corresponding to the above value function. */
//    public PlaneFunction2D getInitialPositionTestPlot(Simulation sim){
//        return new PlaneFunction2D(new InitialPositionTestFunction(sim));
//    }
    
    
    /** Resets the statistical data with a given collection of valuations, and a given size. */
    public void reset(Collection<Valuation> vals,int n){
        fullResultMetrics = new HashMap<Valuation,Vector<Double>>();
        partialResultMetrics = new HashMap<Valuation,Vector<Double>>();
        partialRunMetrics = new HashMap<Valuation,Vector<Double>>();
        for(Valuation v:vals) {
            fullResultMetrics.put(v, new Vector<Double>(n));
            partialResultMetrics.put(v, new Vector<Double>(n));
            partialRunMetrics.put(v, new Vector<Double>(n));
        }
    }
    
    /** This method takes the data stored in the DataLog class for a particular simulation and stores it in this class, to allow
     * for statistical data output.
     * @param dl The DataLog captured during the simulation
     */
    public void captureData(DataLog fullRunData, DataLog partialRunData){
        for(Valuation v:fullResultMetrics.keySet()) {
            fullResultMetrics.get(v).add(fullRunData.teamMetrics.get(v).lastElement().y);
            partialResultMetrics.get(v).add(fullRunData.partialTeamMetrics.get(v).lastElement().y);
            partialRunMetrics.get(v).add(partialRunData.teamMetrics.get(v).lastElement().y);
        }
    }
    
    // formatter
    final static NumberFormat formatter = DecimalFormat.getNumberInstance();
    
    /** Outputs results of a particular valuation. */
    public void outputValuation(Valuation val, JTextArea textArea){
        int numRuns = fullResultMetrics.get(val).size();
        double fullTotal = 0.0;
        int numFullNulls = 0;
        for(Double d : fullResultMetrics.get(val)) {
            if (d == null || d.isInfinite() || d.isNaN()) {
                numFullNulls++;
            } else {
                if(d > 10000) { System.out.println(d); }
                fullTotal += d;
            }
        }
        double partialTotal = 0.0;
        int numPartialNulls = 0;
        for(Double d : partialResultMetrics.get(val)) {
            if (d == null || d.isInfinite() || d.isNaN()) {
                numPartialNulls++;
            } else {
                if(d > 10000) { System.out.println(d); }
                partialTotal += d;
            }
        }
        textArea.append(val.toString() + " (full): average " + formatter.format(fullTotal / (numRuns - numFullNulls))
                 + " over " + numRuns + " runs (" + numFullNulls + " null values)." + "\n"
                 + (val.isCooperationTesting() ? 
                     val.toString() + " (partial): average " + formatter.format(partialTotal / (numRuns - numPartialNulls))
                    + " over " + numRuns + " runs (" + numPartialNulls + " null values)." + "\n"
                    : ""));
    }
    
    /** Outputs results to standard output. */
    public void output(JTextArea textArea){
        textArea.append("-----\n");
        for(Valuation val : fullResultMetrics.keySet()) {
            outputValuation (val, textArea);
        }
    }
    
    /** Outputs results of entire data run. */
    public void outputData(JTextArea textArea){
        int numTimes = 0;
        for(Valuation v: fullResultMetrics.keySet()) { numTimes = fullResultMetrics.get(v).size(); break; }
        for(Valuation v: fullResultMetrics.keySet()) {
            textArea.append(v.toString() + (v.isCooperationTesting() ? "\t\t" : "\t"));
        }
        textArea.append("\n");
        for(int n=0; n<numTimes; n++) {
            for(Valuation v: fullResultMetrics.keySet()) {
                textArea.append(formatter.format(fullResultMetrics.get(v).get(n)) + "\t"
                        + (v.isCooperationTesting() ? formatter.format(partialResultMetrics.get(v).get(n)) + "\t" : ""));
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