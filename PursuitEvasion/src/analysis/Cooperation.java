/**
 * Cooperation.java
 * Created on Mar 7, 2008
 */

package analysis;

import java.text.NumberFormat;
import java.util.Collection;
import java.util.HashSet;
import metrics.Valuation;
import scio.function.FunctionValueException;
import simulation.Agent;
import simulation.Simulation;
import simulation.Team;

/**
 * The purpose of this class is to compute metrics based on particular initial configurations of players and parameters.
 * This may require running a simulation multiple times with a subset of the owner.
 * 
 * @author Elisha Peterson
 */
public class Cooperation {
    
    /** Measures contributions of a particular subset of players, divided into altruistic and selfish contributions.
     * @param sim the simulation used to compute the metrics
     * @param val the valuation used to evaluate the current status of the simulation
     * @param subset the subset of agents to analyze
     * @return
     */
    public static SplitContribution subsetContribution(Simulation sim, Valuation val, HashSet<Agent> subset){
        HashSet<Agent> team=new HashSet<Agent>();
        Team owner = val.getOwner();
        team.addAll(owner);
        HashSet<Agent> complement=val.getComplement();
        try {
            double trial1, trial2, trial3;
            sim.setBatchProcessing(true);
            // subset participates
            owner.setStartAgents(complement);
            sim.run();
            trial3 = val.getValue(sim, complement);    
            // entire team participates
            owner.setStartAgents(team);
            sim.run();
            trial1 = val.getValue(sim, team);
            trial2 = val.getValue(sim, complement);  
            // return values
            sim.setBatchProcessing(false);
            return new SplitContribution(owner, subset, val, trial2-trial1, trial3-trial2);
            //return new SplitContribution(owner, subset, val, trial1-trial2, trial2-trial3);
        } catch (FunctionValueException e) {
            return new SplitContribution();            
        }
    }
    
    
    // INNER CLASSES
    
    /** Stores selfish+altruistic values of current simulation. */            
    public static class SplitContribution {    
        Team owner;
        HashSet<Agent> subset;
        Valuation val;
        
        public Double selfish;
        public Double altruistic;

        private SplitContribution() { }

        public SplitContribution(Team t, Collection<Agent> subset, Valuation val, double selfish, double altruistic) {
            this.owner = t;
            this.selfish = selfish;
            this.altruistic = altruistic;
            this.subset = new HashSet<Agent> (subset);
            this.val = val;
        }
        
        public double getTotal(){return selfish+altruistic;}
        
        @Override
        public String toString(){
            return val + " cooperation " + subset.toString() + ": "
                    + NumberFormat.getNumberInstance().format(selfish) + " selfish + "
                    + NumberFormat.getNumberInstance().format(altruistic) + " altruistic = "
                    + NumberFormat.getNumberInstance().format(getTotal()) + " total.\n";
        }
    }
}
