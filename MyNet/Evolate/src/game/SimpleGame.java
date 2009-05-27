/**
 * SimpleGame.java
 * Created on May 14, 2009
 */

package game;

import java.util.TreeSet;
import scio.coordinate.R2;

/**
 * Represents a simple 2-player game with a fixed set of strategies and a payoff for each
 * player, associated with that set of strategies.
 *
 * @author Elisha Peterson
 */
public interface SimpleGame<O> {

    /** Returns the space of possible plays as a set. */
    public TreeSet<O> getPlaySpace();

    /** Returns the two-payoff set given the strategies specified. */
    public R2 getPayoffs(O strategy1, O strategy2);

    
    // INNER CLASSES

    /** Represents a basic game with a choice between several strategies. */
    public class NPlayGame implements SimpleGame<Integer> {

        Double[][] payoffs;

        public static Double[][] PAYOFF_PRISONER = { { 2.0, 0.0 } , { 3.0, 1.0 } };
        public static Double[][] PAYOFF_HILO = { { 2.0 , 0.0 } , { 0.0, 1.0 } };

        /** Initializes to the prisoner's dilemam game. */
        public NPlayGame() {
            this(PAYOFF_PRISONER);
        }

        /** Initializes to the given payoff matrix; the size of the matrix determines the number of possible strategies. */
        public NPlayGame(Double[][] payoffs) {
            this.payoffs = payoffs;
        }

        /** Returns the set of playable strategies (as integers). */
        public TreeSet<Integer> getPlaySpace() {
            TreeSet<Integer> result = new TreeSet<Integer>();
            for (int i = 0; i < payoffs.length; i++) {
                result.add(i);
            }
            return result;
        }

        /** Returns the payoff associated with the two given strategies. */
        public R2 getPayoffs(Integer strategy1, Integer strategy2) throws ArrayIndexOutOfBoundsException {
            return new R2(payoffs[strategy1][strategy2], payoffs[strategy2][strategy1]);
        }

        /** Returns number of potential plays. */
        public int getNumPlays() {
            return payoffs.length;
        }
    } // inner class NPlayGame
}
