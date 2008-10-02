/**
 * VictoryCondition.java
 * Created on Apr 25, 2008
 */

package metrics;

import analysis.DataLog;
import java.util.Vector;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import scio.function.FunctionValueException;
import simulation.Team;
import utility.DistanceTable;

/**
 * This class is used to determine when a particular team has "won" or "lost" the simulation.
 * @author Elisha Peterson
 */
@XmlAccessorType(XmlAccessType.NONE)
public class VictoryCondition extends Valuation {
    
    // CONSTANTS
    
    public static final int NEITHER = 0;
    public static final int WON = 1;
    public static final int LOST = 2;   
    
    // SIMULATION PARAMETERS
    
    /** Stores the status according to above codes if the value is more than the threshold value (win/loss/neither) */
    int moreResult = NEITHER;
    /** Stores the status according to above codes if the value is less than the threshold value (win/loss/neither) */
    int lessResult = NEITHER;    
    /** Whether this victory condition forces the end of the game. */
    public boolean gameEnding = false;
    
    // DYNAMIC PARAMETERS
    
    /** Stores whether this value has been triggered from neither to "win" or "loss" */
    boolean triggered = false;    
    
    // CONSTRUCTORS

    public VictoryCondition(){
    }
    
    /** Main constructor */
    public VictoryCondition(Vector<Team> teams, Team owner, Team target, int type, double threshold,int moreResult,int lessResult){
        super(teams, owner, target, type, threshold);
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
    
    // BEAN PATTERNS

    @XmlAttribute(name="eventLessCode")
    public int getLessResult() { return lessResult; }
    public void setLessResult(int lessResult) { this.lessResult = lessResult; }

    @XmlAttribute(name="eventGreaterCode")
    public int getMoreResult() { return moreResult; }
    public void setMoreResult(int moreResult) { this.moreResult = moreResult; }

    @XmlAttribute
    public boolean isGameEnding() { return gameEnding; }
    public void setGameEnding(boolean endGame) { this.gameEnding = endGame; }    
    
}
