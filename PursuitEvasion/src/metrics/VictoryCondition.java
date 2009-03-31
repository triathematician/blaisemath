/**
 * VictoryCondition.java
 * Created on Apr 25, 2008
 */

package metrics;

import analysis.SimulationLog;
import java.util.Vector;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import sequor.Settings;
import sequor.SettingsProperty;
import sequor.model.BooleanModel;
import sequor.model.StringRangeModel;
import sequor.model.SubsetModel;
import simulation.Agent;
import simulation.Simulation;
import simulation.Team;
import utility.DistanceTable;

/**
 * This class is used to determine when a particular team has "won" or "lost" the simulation.
 * @author Elisha Peterson
 */
@XmlAccessorType(XmlAccessType.NONE)
public class VictoryCondition extends Valuation {
        
    // DYNAMIC PARAMETERS
    
    /** Stores whether this value has been triggered from neither to "win" or "loss" */
    boolean triggered = false;    
    
    // CONSTRUCTORS

    public VictoryCondition(){ this(new Vector<Team>(),new Team(),new Team(),DIST_MIN,0.0,NEITHER,NEITHER,false); }
    
    /** Main constructor */
    public VictoryCondition(Vector<Team> teams, Team owner, Team target, int type, double threshold,int moreResult,int lessResult,boolean gameEnding){
        vs = new VictorySettings(teams, owner, target, type, threshold, moreResult, lessResult, gameEnding);
        vs.setName("Victory Condition");
        addActionListener(owner);
        initStateVariables();
    }

    /** Initializes the settings class. */
    protected void initSettings(Vector<Team> teams, Team owner, Team target, int type, double threshold,int moreResult,int lessResult,boolean gameEnding){
    }
    
    // INITIALIZERS
    
    public void initStateVariables() {
        triggered = false;
    }
    
    
    // DETERMINES WHETHER VICTORY HAS OCCURRED
    
    /** Checks to see if victory condition has been met; outputs info to log if victory has been achieved.
     * @param dt the table of distances
     * @param log the data log for the simulation
     * @param time the current time for the simulation
     * @return status integer representing win, loss, or neither
     */
    public int check(DistanceTable dt,CaptureMap cap,SimulationLog log,double time){
        //System.out.println("value: "+getValue(dt)+", thresh: "+getThreshold()+", more: "+moreResult+", less: "+lessResult);
        int result = getValue(dt,cap) >= getThreshold() ? getMoreResult() : getLessResult();
        if(triggered) { return result; }
        switch (result) {
            case NEITHER:
                break;
            case WON:
                triggered = true;
                log.logEvent(vs.owner, null, vs.target, null, "Victory", time);
                break;
            case LOST:
                triggered = false;
                log.logEvent(vs.owner, null, vs.target, null, "Defeat", time);
                break;
        }
        return result;
    }
    
    // BEAN PATTERNS

    @XmlAttribute(name="eventLessCode")
    public int getLessResult() { return ((VictorySettings)vs).lessModel.getIValue(); }
    public void setLessResult(int lessResult) { ((VictorySettings)vs).lessModel.setValue(lessResult); }

    @XmlAttribute(name="eventGreaterCode")
    public int getMoreResult() { return ((VictorySettings)vs).moreModel.getIValue(); }
    public void setMoreResult(int moreResult) { ((VictorySettings)vs).moreModel.setValue(moreResult); }

    @XmlAttribute
    public boolean isGameEnding() { return ((VictorySettings)vs).endModel.getValue(); }
    public void setGameEnding(boolean gameEnding) { ((VictorySettings)vs).endModel.setValue(gameEnding); }


    // CONSTANTS

    public static final int NEITHER = 0;
    public static final int WON = 1;
    public static final int LOST = 2;

    String[] winloss = {"No change", "Win", "Loss"};

    /** Generalizes valuation settings class. */
    public class VictorySettings extends Valuation.ValuationSettings {
        /** Stores the status according to above codes if the value is more than the threshold value (win/loss/neither) */
        private StringRangeModel moreModel = new StringRangeModel(winloss,0,0,2);
        /** Stores the status according to above codes if the value is less than the threshold value (win/loss/neither) */
        private StringRangeModel lessModel = new StringRangeModel(winloss,0,0,2);
        /** Whether this victory condition forces the end of the game. */
        private BooleanModel endModel = new BooleanModel(false);

        private VictorySettings(Vector<Team> teams, Team owner, Team target, int type, double threshold, int moreResult, int lessResult, boolean gameEnding) {
            super(teams, owner, target, type, threshold);
            this.moreModel.setValue(moreResult);
            this.lessModel.setValue(lessResult);
            this.endModel.setValue(gameEnding);
        }
        
        /** Reinitializes all models. */
        @Override void initModels() {
            super.removeAllElements();
            opponentModel = new StringRangeModel(Simulation.getTeamStrings(teams), teams.indexOf(target), 0, teams.size());
            add(new SettingsProperty("Opponent", opponentModel, Settings.EDIT_COMBO));
            add(new SettingsProperty("Type", type, Settings.EDIT_COMBO));
            add(new SettingsProperty("Threshhold", threshold, Settings.EDIT_DOUBLE));
            add(new SettingsProperty("Condition if >=", moreModel, Settings.EDIT_COMBO));
            add(new SettingsProperty("Condition if <", lessModel, Settings.EDIT_COMBO));
            add(new SettingsProperty("Game ending ?", endModel, Settings.EDIT_BOOLEAN));
            add(new SettingsProperty("Cooperation", this.testsCooperation, Settings.EDIT_BOOLEAN));
            valueAgents = new SubsetModel<Agent> (owner.agents);
            addGroup("Subset", valueAgents, Settings.EDIT_BOOLEAN_GROUP, "Select agents used for valuation.");
        }
    }
}
