/*
 * Statistics.java
 * Created on Aug 28, 2007, 10:40:52 AM
 */

package simulation;

import java.beans.PropertyChangeEvent;
import java.text.DecimalFormat;
import java.util.Vector;
import javax.swing.JTextArea;
import scio.coordinate.R2;
import scio.function.Function;
import scio.function.FunctionValueException;
import sequor.model.FiresChangeEvents;
import specto.plottable.PlaneFunction2D;
import specto.visometry.Euclidean2;
import utility.DataLog;

/**
 * Static library of methods to perform batch runs of simulations for a variety of initial conditions.
 * @author Elisha Peterson
 */
public class Statistics extends FiresChangeEvents {
    
    Vector<Double> primaryOutputs;

    public Statistics() {reset();}
    public Statistics(int n) {reset(n);}
    
    /** This function computes the output value of a simulation given an input of initial starting location of a particular agent. */
    public class InitialPositionTestFunction implements Function<R2,Double>{
        Simulation sim;
        public InitialPositionTestFunction(Simulation sim){this.sim=sim;}        
        public Double getValue(R2 x) throws FunctionValueException {
            Agent changes=sim.getPrimaryAgent();
            changes.getPointModel().setTo(x);
            return sim.getPrimaryValue();
        }
        public Vector<Double> getValue(Vector<R2> x) throws FunctionValueException {
            Agent changes=sim.getPrimaryAgent();
            Vector<Double> result=new Vector<Double>();
            sim.batchProcessing=true;
            for(R2 point:x){
                changes.getPointModel().setTo(point);
                result.add(sim.getPrimaryValue()>0.5?50.0:0.0);
            }
            sim.batchProcessing=false;
            return result;
        }
        public Double minValue() {return 0.0;}
        public Double maxValue() {return 50.0;}
        
    }
    
    /** Returns PlaneFunction2D corresponding to the above value function. */
    public PlaneFunction2D getInitialPositionTestPlot(Euclidean2 vis,Simulation sim){
        InitialPositionTestFunction iptf=new InitialPositionTestFunction(sim);
        return new PlaneFunction2D(vis,iptf);
    }
    
    
    /** Resets the statistical data. */
    public void reset(){reset(-1);}
    public void reset(int n){
        if(primaryOutputs==null){
            if(n!=-1){primaryOutputs=new Vector<Double>(n);
            }else{primaryOutputs=new Vector<Double>();}
        }else{
            primaryOutputs.clear();
            if(n!=-1){primaryOutputs.ensureCapacity(n);}
        }
    }
    
    /** This method takes the data stored in the DataLog class for a particular simulation and stores it in this class, to allow
     * for statistical data output.
     * @param dl The DataLog captured during the simulation
     */
    public void captureData(DataLog dl){
        primaryOutputs.add(dl.getPrimaryOutput());
    }
    
    /** Outputs results to standard output. */
    public void output(JTextArea textArea){
        double total=0.0;
        int numNulls=0;
        for(Double d:primaryOutputs){
            if(d!=null){total+=d;
            }else{numNulls++;}
        }
        total=total/(primaryOutputs.size()-numNulls);
        textArea.append("Result of "+primaryOutputs.size()+" run is an average of "
                +DecimalFormat.getNumberInstance().format(total)
                +" and "+numNulls+" times with no catch.\n");
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
}