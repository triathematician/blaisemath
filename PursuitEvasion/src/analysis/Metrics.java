/**
 * Metrics.java
 * Created on Mar 7, 2008
 */

package analysis;

import java.util.HashSet;
import simulation.Agent;
import simulation.Simulation;
import simulation.Team;

/**
 * The purpose of this class is to compute metrics based on particular initial configurations of players and parameters.
 * This may require running a simulation multiple times with a subset of the team.
 * 
 * @author Elisha Peterson
 */
public class Metrics {
    
    /** Obtains value of simulation with a restricted set of players; value is that obtained by a particular subset as well. */
    public static double runSubsetSimulation(Simulation s,Team t,HashSet<Agent> activeAgents,HashSet<Agent> valueAgents){
        t.setStartAgents(activeAgents);
        t.setValueAgents(valueAgents);
        s.setBatchProcessing(true);
        s.run();
        s.setBatchProcessing(false);
        return t.getValue();
    }
    
    /** Measures contributions of a particular subset of players, divided into altruistic and selfish contributions. */
    public static SplitContribution subsetContribution(Simulation s,Team t,HashSet<Agent> subset){
        HashSet<Agent> team=new HashSet<Agent>();
        team.addAll(t);
        HashSet<Agent> complement=new HashSet<Agent>();
        for(Agent a:t){
            if(!subset.contains(a)){complement.add(a);}
        }
        // run with all players, value measured by all
        double trial1=runSubsetSimulation(s,t,team,team);
        // run with all players, value measured only by some
        double trial2=runSubsetSimulation(s,t,team,complement);
        // run with some players, value measured only by some
        double trial3=runSubsetSimulation(s,t,complement,complement);
        return new SplitContribution(trial1-trial2,trial2-trial3);
    }
            
    public static class SplitContribution {    
        public double selfish;
        public double altruistic;

        public SplitContribution(double selfish, double altruistic) {
            this.selfish = selfish;
            this.altruistic = altruistic;
        }
        
        public double getTotal(){return selfish+altruistic;}
        
        @Override
        public String toString(){
            return "Selfish: "+selfish+", Altrustic: "+altruistic+", Total: "+getTotal();
        }
    }
}
