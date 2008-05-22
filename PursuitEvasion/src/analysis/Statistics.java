/*
 * Statistics.java
 * Created on Aug 28, 2007, 10:40:52 AM
 */

package analysis;

import metrics.Valuation;
import simulation.*;
import java.beans.PropertyChangeEvent;
import java.text.DecimalFormat;
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
    
    HashMap<Valuation,Vector<Double>> resultMetrics;

    public Statistics() {}
    
    /** Returns PlaneFunction2D corresponding to the above value function. */
    public PlaneFunction2D getInitialPositionTestPlot(Simulation sim){
        return new PlaneFunction2D(new InitialPositionTestFunction(sim));
    }
    
    
    /** Resets the statistical data with a given collection of valuations, and a given size. */
    public void reset(Collection<Valuation> vals,int n){
        resultMetrics = new HashMap<Valuation,Vector<Double>>();
        for(Valuation v:vals) {
            resultMetrics.put(v, new Vector<Double>(n));
        }
    }
    
    /** This method takes the data stored in the DataLog class for a particular simulation and stores it in this class, to allow
     * for statistical data output.
     * @param dl The DataLog captured during the simulation
     */
    public void captureData(DataLog dl){
        for(Valuation v:resultMetrics.keySet()) {
            resultMetrics.get(v).add(dl.teamMetrics.get(v).lastElement().y);
        }
    }
    
    /** Outputs results of a particular valuation. */
    public void outputValuation(Valuation val, JTextArea textArea){
        int numRuns = resultMetrics.get(val).size();
        double total = 0.0;
        int numNulls = 0;
        for(Double d : resultMetrics.get(val)) {
            if (d == null || d.isInfinite() || d.isNaN()) {
                numNulls++;
            } else {
                if(d > 10000) { System.out.println(d); }
                total += d;
            }
        }
        total /= (numRuns - numNulls);
        textArea.append(val.toString() + ": average " + DecimalFormat.getNumberInstance().format(total)
                 + " over " + numRuns + " runs (" + numNulls + " null values)." + "\n");
    }
    
    /** Outputs results to standard output. */
    public void output(JTextArea textArea){
        textArea.append("-----\n");
        for(Valuation val : resultMetrics.keySet()) {
            outputValuation (val, textArea);
        }
    }
    
    /** Outputs results of entire data run. */
    public void outputData(JTextArea textArea){
        int numTimes = 0;
        for(Valuation v: resultMetrics.keySet()) { numTimes = resultMetrics.get(v).size(); break; }
        for(Valuation v: resultMetrics.keySet()) {
            textArea.append(v.toString() + "\t");
        }
        textArea.append("\n");
        for(int n=0; n<numTimes; n++) {
            for(Valuation v: resultMetrics.keySet()) {
                textArea.append(resultMetrics.get(v).get(n) + "\t");
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