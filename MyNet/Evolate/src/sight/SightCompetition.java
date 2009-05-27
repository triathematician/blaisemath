/**
 * SightCompetition.java
 * Created on May 4, 2009
 */

package sight;

import java.beans.PropertyChangeEvent;
import sequor.Settings;
import sequor.SettingsProperty;
import sequor.model.*;

/**
 * This class defines settings for competitions, and is able to run all the different
 * kinds of sight game competitions.
 * 
 * @author Elisha Peterson
 */
public class SightCompetition {

    public CompSettings comp;
    public EvolSettings evol;
    public FixedSettings fixed;

    /** Initializes the competition. */
    public SightCompetition() {
        comp = new CompSettings();
        evol = new EvolSettings();
        fixed = new FixedSettings();
        comp.addChild(evol, Settings.PROPERTY_INDEPENDENT);
        comp.addChild(fixed, Settings.PROPERTY_INDEPENDENT);
    }

    /** Runs the competition, with current settings. */
    public void run() {
    }

    /** Contains settings relating to how the competition is being run. */
    public class CompSettings extends Settings {

        // PERTAINING TO SIMULATION

        /** Number of agents per team. */
        public IntegerRangeModel numAgents = new IntegerRangeModel(2,1,100);

        /** Number of targets. */
        public IntegerRangeModel numTargets = new IntegerRangeModel(1,1,100);

        // PERTAINING TO COMPETITION

        /** Number of rounds of evolutionary approach. */
        public IntegerRangeModel numRounds = new IntegerRangeModel(1,0,50);

        /** Number of steps per round (each generating a new simulation). */
        public IntegerRangeModel numSims = new IntegerRangeModel(1000,0,1000000);

        // PERTAINING TO WHAT KIND OF COMPETITION

        String[] types = { "Random", "Fixed", "Evolving" };

        /** Type of algorithm for the first team. */
        public StringRangeModel pool1 = new StringRangeModel(types);

        /** Type of algorithm for the second team. */
        public StringRangeModel pool2 = new StringRangeModel(types);



        /** Default contructor. */
        public CompSettings() {
            setName("Competition settings");
            add(new SettingsProperty("# Agents", numAgents, Settings.EDIT_INTEGER));
            add(new SettingsProperty("# Targets", numTargets, Settings.EDIT_INTEGER));
            addSeparator();
            add(new SettingsProperty("# Rounds Evol", numRounds, Settings.EDIT_INTEGER));
            add(new SettingsProperty("# Sims per Round", numSims, Settings.EDIT_INTEGER));
            addSeparator();
            add(new SettingsProperty("Algorithm 1", pool1, Settings.EDIT_COMBO));
            add(new SettingsProperty("Algorithm 2", pool2, Settings.EDIT_COMBO));
        }

//        /** Listens for changes to settings */
//        @Override
//        public void propertyChange(PropertyChangeEvent evt) {
//            String ac = null;
//            fireActionPerformed(ac);
//        }

    } // inner class SightCompetition.CompSettings


    /** Contains settings relating to the evolutionary teams. */
    public class EvolSettings extends Settings {

        // MAKE-UP OF EVOLUTIONARY TEAMS

        /** Number of teams selected per step for being at the top. */
        public IntegerRangeModel poolTop = new IntegerRangeModel(2, 0, 100);

        /** Number of teams selected per step for being at the top, with error introduced. */
        public IntegerRangeModel poolTopErr = new IntegerRangeModel(2, 0, 100);

        /** Number of teams selected per step, being a cross of two different fit teams. */
        public IntegerRangeModel poolCross = new IntegerRangeModel(2, 0, 100);

        /** Number of teams selected per step, based on fitness, with error introduced. */
        public IntegerRangeModel poolFitErr = new IntegerRangeModel(2, 0, 100);

        /** Number of teams selected per step, purely at random. */
        public IntegerRangeModel poolRandom = new IntegerRangeModel(2, 0, 100);

        /** Number of teams within an evolutionary pool. */
        public int poolSize;

        // MUTATIONS

        /** Probability of point mutations (error in an individual spec). */
        public DoubleRangeModel probMutation = new DoubleRangeModel(0.5, 0.0, 1.0, 0.01);

        /** Amount of error introduced by a point mutation. */
        public DoubleRangeModel mutationError = new DoubleRangeModel(0.01, 0.0, 1.0, 0.001);

        
        /** Default constructor */
        public EvolSettings() {
            setName("Evolutionary team settings");
            add(new SettingsProperty("# Top Teams", poolTop, Settings.EDIT_INTEGER));
            add(new SettingsProperty("# TopErr Teams", poolTopErr, Settings.EDIT_INTEGER));
            add(new SettingsProperty("# Cross Teams", poolCross, Settings.EDIT_INTEGER));
            add(new SettingsProperty("# FitErr Teams", poolFitErr, Settings.EDIT_INTEGER));
            add(new SettingsProperty("# Random Teams", poolRandom, Settings.EDIT_INTEGER));
            addSeparator();
            add(new SettingsProperty("Point mutation prob", probMutation, Settings.EDIT_DOUBLE));
            add(new SettingsProperty("Mutation error", mutationError, Settings.EDIT_DOUBLE));
        }

    } // inner class SightCompetition.EvolSettings


    /** Contains settings relating to teams with fixed specs. */
    public class FixedSettings extends Settings {

        /** Step size between specs. */
        public DoubleRangeModel specStep = new DoubleRangeModel(0.1, 0.001, 1, 0.001);

        /** Default constructor */
        public FixedSettings() {
            setName("Fixed Spec team settings");
            add(new SettingsProperty("Spec Step", specStep, Settings.EDIT_DOUBLE));
        }

    } // inner class SightCompetition.FixedSettings
    

}
