/**
 * EvolutionaryGame.java
 * Created on May 14, 2009
 */

package game;

import java.util.TreeMap;
import java.util.Vector;
import mas.Agent;
import mas.Parameter;
import mas.ParameterSpace;
import mas.Simulation;
import mas.evol.Allele;
import mas.evol.DNA;
import mas.evol.EvolSim;
import mas.evol.GenePool;

/**
 * Represents an evolutionary game, that pits varying strategies against each other.
 * @author Elisha Peterson
 */
public class EvolutionaryGame {

    /** The underlying game, with an indexed set of plays and their outcomes. */
    final static SimpleGame.NPlayGame game = new SimpleGame.NPlayGame();

    final static int[] rules = { 1, 1, 1, 1, 5 };
    final static float mProb = 0.5f;
    public static final int nSteps = 100;

    public static void main(String[] args) {
        PlayerPool pool = new PlayerPool(game, rules, mProb);
        new EvolSim(nSteps, pool).run();
        pool.printLog(System.out);
        pool.printAgentHistory(System.out, true);
        System.out.println(pool.getPlayDistribution());
    }




    // inner classes

    /** Class representing the parameter space for an agent with this kind of strategy. */
    public static class GamePlay extends Allele<Integer> {
        /** Default to strategies between 0 and max, default value is 0. */
        public GamePlay(Integer nPlays) { super(0, nPlays-1, 0); }
        @Override
        public Allele copy() { return new GamePlay(max-1); }
        /** Simple mutations simply pick another strategy at random. */
        @Override
        public Allele mutation(double error) { return (GamePlay) getRandom(); }
        @Override
        public Parameter getRandom() { return new GamePlay((int)Math.floor((max+1)*Math.random())); }
    } // inner class GamePlay


    /** Class representing the gene pool of agents with various strategies. */
    public static class PlayerPool extends GenePool {

        SimpleGame.NPlayGame game;

        public PlayerPool(SimpleGame.NPlayGame game, int[] rules, float mProb) {
            super(rules, mProb, 0f);
            for (int i = 0; i < rules.length; i++) {
                for (int j = 0; j < rules[i]; j++) {
                    agents.add(new mas.Agent(new mas.evol.DNA("s", new GamePlay(game.getNumPlays()))));
                }
            }
            this.game = game;
        }

        @Override
        public Agent getAgentFrom(DNA dna) {
            return new mas.Agent(dna);
        }

        @Override
        public float getFitness(Agent agent, Simulation sim) {
            float totUtility = 0f;
            int strategy = (Integer) agent.getControlVars().get("s").value;
            for (Agent a : agents) {
                if (a.equals(agent)) { continue; }
                totUtility += (float) game.getPayoffs(strategy, (Integer) a.getControlVars().get("s").value).x;
            }
            return totUtility;
        }

        /** REturns distribution of players */
        public Vector<Integer> getPlayDistribution() {
            Vector<Integer> result = new Vector<Integer>();
            for (int i = 0; i < game.getNumPlays(); i++) {
                result.add(0);
            }
            for (Agent a : agents) {
                int play = (Integer) a.getControlVars().get("s").value;
                result.set(play, result.get(play)+1);
            }
            return result;
        }

        @Override
        public void evolve() {
            super.evolve();
            System.out.println(getPlayDistribution());
        }


    }
}
