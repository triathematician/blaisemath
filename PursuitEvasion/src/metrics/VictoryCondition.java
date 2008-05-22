/**
 * VictoryCondition.java
 * Created on Apr 25, 2008
 */

package metrics;

import analysis.DataLog;
import scio.function.FunctionValueException;
import simulation.Team;
import utility.DistanceTable;

/**
 * This class is used to determine when a particular team has "won" or "lost" the simulation.
 * @author Elisha Peterson
 */
public class VictoryCondition extends Valuation {
    
    // CONSTANTS
    
    public static final int NEITHER = 0;
    public static final int WON = 1;
    public static final int LOST = 2;   
    
    /** Stores the status if the value is more than the threshold value (win/loss/neither) */
    int moreResult;
    /** Stores the status if the value is less than the threshold value (win/loss/neither) */
    int lessResult;
    
    /** Whether this victory condition forces the end of the game. */
    public boolean endGame = false;
    
    /** Stores whether this value has been triggered from neither to "win" or "loss" */
    boolean triggered = false;
    
    
    // CONSTRUCTORS
    
    /** Main constructor */
    public VictoryCondition(Team owner, Team target, int type, double threshold,int moreResult,int lessResult){
        super(owner, target, type, threshold);
        vs.setName("Victory Condition");
        this.moreResult = moreResult;
        this.lessResult = lessResult;
        reset();
    } 
    
    // INITIALIZERS
    
    public void reset() {
        triggered = false;
    }
    
    
    // DETERMINES WHETHER VICTORY HAS OCCURRED
    
    /** Checks to see if victory condition has been met; outputs info to log if victory has been achieved.
     * @param dt the table of distances
     * @param log the data log for the simulation
     * @param time the current time for the simulation
     * @return status integer representing win, loss, or neither
     */
    public int check(DistanceTable dt,DataLog log,double time){
        try {
            //System.out.println("value: "+getValue(dt)+", thresh: "+getThreshold()+", more: "+moreResult+", less: "+lessResult);
            int result = getValue(dt) >= getThreshold() ? moreResult : lessResult;
            if(triggered) { return result; }
            switch (result) {
                case NEITHER:
                    break;
                case WON:
                    triggered = true;
                    log.logEvent(null, null, null, null, "Victory", time);
                    break;
                case LOST:
                    triggered = false;
                    log.logEvent(null, null, null, null, "Defeat", time);
                    break;
            }
            return result;
        } catch (FunctionValueException ex) {
            return NEITHER;
        }
    }
}
