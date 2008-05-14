/**
 * VictoryCondition.java
 * Created on Apr 25, 2008
 */

package metrics;

import analysis.DataLog;
import scio.function.FunctionValueException;
import utility.DistanceTable;

/**
 * This class is used to determine when a particular team has "won" or "lost" the simulation.
 * @author Elisha Peterson
 */
public abstract class VictoryCondition {
    
    /** Whether this victory condition forces the end of the game. */
    public boolean endGame=false;
    
    // CONSTANTS
    
    public static final int NEITHER=0;
    public static final int WON=1;
    public static final int LOST=2;    
    
    /** Checks to see if victory condition has been met; outputs info to log if victory has been achieved.
     * @param dt the table of distances
     * @param log the data log for the simulation
     * @param time the current time for the simulation
     * @return status integer representing win, loss, or neither
     */
    public int check(DistanceTable dt,DataLog log,double time){
        int result=check(dt);
        switch(result){
            case NEITHER:
                break;
            case WON:
                log.logEvent(null,null,null,null,"Victory",time);
                break;
            case LOST:
                log.logEvent(null,null,null,null,"Defeat",time);
        }
        return result;
    }
    
    /** Abstract method to determine victory. */
    public abstract int check(DistanceTable dt);
    
    
    // INNER CLASSES
    
    /** Basic victory condition, based simply on whether the valuation is greater than
     * or less than the threshold.
     */
    public static class Basic extends VictoryCondition {
        int moreResult;
        int lessResult;
        Valuation goal;
        
        public Basic(Valuation goal,int moreResult,int lessResult){
            this.goal = goal;
            this.moreResult = moreResult;
            this.lessResult = lessResult;
        }

        @Override
        public int check(DistanceTable dt) {
            try {
                return goal.getValue(dt) > 0 ? moreResult : lessResult;
            } catch (FunctionValueException ex) {
                return NEITHER;
            }
        }
    } // INNER CLASS VictoryCondition.Basic
}
