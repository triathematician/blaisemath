/*
 * SimulationFactory.java
 * Created on Sep 18, 2007, 10:00:49 AM
 */
package utility;

import simulation.Team;
import behavior.Behavior;
import java.awt.Color;
import java.util.Vector;
import metrics.CaptureCondition;
import metrics.Goal;
import metrics.Valuation;
import metrics.VictoryCondition;
import scio.coordinate.R2;
import sequor.model.StringRangeModel;
import sequor.style.VisualStyle;
import tasking.TaskGenerator;

/**
 * This class provides preset simulations to load into a program.
 * <br><br>
 * @author Elisha Peterson
 */
public class SimulationFactory {
    
    // CONSTANTS
    
    /** Custom game */
    public static final int CUSTOM = 0;
    /** n-on-1 game */
    public static final int SIMPLE_PE = 1;
    /** Specifies simple game with two teams, pursuers and evaders */
    public static final int TWOTEAM = 2;
    /** Specifies simple game with three teams, pursuers, pursuers/evaders, and evaders */
    public static final int SIMPLE_PPE = 3;
    /** Specifies game with two teams, pursuers, and evaders, with evaders seeking a goal */
    public static final int SAHARA_PE = 4;
    /** Lots of teams!! */
    public static final int LOTS_OF_FUN = 5;
    /** For looking at lead factors. */
    public static final int LEAD_FACTOR = 6;
    /** Custom starting locations */
    public static final int CUSTOM_START_SAHARA = 7;
    /** Evaders go to endzone. Has sidelines and obstacles. */
    public static final int FOOTBALL_GAME = 8;
    
    /** Strings corresponding to each preset simulation. */
    public static final String[] GAME_STRINGS = {
        "Custom", "Follow the Light", "Cops & Robbers", "Antarctica",
        "Sahara", "Swallowed", "Jurassic",
        "Custom Start Sahara","Football Game"};
    
    
    // STATIC FACTORY METHODS
    
    /** Returns model for use in selecting a particular simulation. */
    public static StringRangeModel comboBoxRangeModel() {
        return new StringRangeModel(GAME_STRINGS);
    }
    
    /** Lookup function returning the list of teams used by each preset simulation. */
    public static Vector<Team> getTeams(int simCode) {
        switch(simCode) {
            case SIMPLE_PE: return lightSimulation();
            case TWOTEAM: return twoTeamSimulation();
            case SIMPLE_PPE: return threeTeamSimulation();
            case SAHARA_PE: return twoPlusGoalSimulation();
            case LOTS_OF_FUN: return bigSimulation();
            case LEAD_FACTOR: return leadFactorSimulation();
            case FOOTBALL_GAME: return footballGameSimulation();
            case CUSTOM_START_SAHARA:
                Vector<Team> teams = twoPlusGoalSimulation();
                R2[] LionsPositions = { new R2(40, 40) , new R2(0, -10) , new R2(-40, -50) };
                teams.get(0).setStartingLocations(LionsPositions);
                R2[] WildebeastPositions = { new R2(0, 30) , new R2(50, 10) , new R2(30, 30) , new R2(10, 0) };
                teams.get(1).setStartingLocations(WildebeastPositions);
                R2[] WaterPositions = { new R2(-20, 10) };
                teams.get(2).setStartingLocations(WaterPositions);
                return teams;
            default: return null;
        }
    }
    
    
    // METHODS FOR INITIALIZING SIMULATION
    
    /** Simulation representing an n-on-1 scenario. */
    public static Vector<Team> lightSimulation() {        
        // Teams
        Vector<Team> teams = new Vector<Team>();
        Team bugTeam = new Team(); teams.add(bugTeam);
        Team lightTeam = new Team(); teams.add(lightTeam);
        //                      ( NAME , # , STARTING POS , BEHAVIOR ALGORITHM , COLOR )
        bugTeam.initSettings("Bugs", 4, Team.START_RANDOM, Behavior.LEADING, Color.DARK_GRAY);
        lightTeam.initSettings("Light", 1, Team.START_RANDOM, Behavior.STRAIGHT, VisualStyle.DARK_GREEN);        
        
        // Set specific starting locations
        R2[] positions = { new R2(-30,30), new R2(-20,-20), new R2(50,10), new R2(10,10) };
        bugTeam.setStartingLocations(positions);
        
        // Capture Conditions
        //                      ( TEAMS , OPPONENT , CAPTURE RANGE , WHAT HAPPENS UPON CAPTURE )     
        bugTeam.addCaptureCondition(teams, lightTeam, 1.0, CaptureCondition.REMOVEAGENT);
        
        // Victory Conditions
        //                      ( TEAMS, THIS TEAM, OPPOSING TEAM, METRIC FOR VICTORY, THRESHOLD , WHAT HAPPENS IF LESS , WHAT HAPPENS IF MORE )
        bugTeam.setVictoryCondition(new VictoryCondition(teams, bugTeam, lightTeam,
                Valuation.DIST_MIN, 5.0, VictoryCondition.NEITHER, VictoryCondition.WON));

        // Goals (Taskings)
        //                      ( WEIGHT, TEAMS, OPPONENT, SEEK/FLEE/CAPTURE? , TASKING ALGORITHM , GOAL THRESHOLD )
        bugTeam.addAutoGoal(1.0, teams, lightTeam, Goal.SEEK, TaskGenerator.AUTO_CLOSEST, 1.0);
        lightTeam.addAutoGoal(1.0, teams, bugTeam, Goal.FLEE, TaskGenerator.AUTO_GRADIENT, 1.0);
        

        // Additional Metrics to Track
        bugTeam.addValuation(new Valuation(teams, bugTeam, lightTeam, Valuation.NUM_ACTIVE_OPPONENTS));
        lightTeam.addValuation(new Valuation(teams, lightTeam, bugTeam, Valuation.TIME_TOTAL));

        return teams;
    }
    
    /** Simulation representing an n-on-k scenario. */
    public static Vector<Team> twoTeamSimulation() {        
        // Teams
        Vector<Team> teams = new Vector<Team>();
        Team copTeam = new Team(); teams.add(copTeam);
        Team robberTeam = new Team(); teams.add(robberTeam);
        //                      ( NAME , # , STARTING POS , BEHAVIOR ALGORITHM , COLOR )
        copTeam.initSettings("Cops", 5, Team.START_RANDOM, Behavior.LEADING, Color.BLUE);
        robberTeam.initSettings("Robbers", 4, Team.START_RANDOM, Behavior.STRAIGHT, Color.ORANGE);        
        
        // Capture Conditions
        //                      ( TEAMS , OPPONENT , CAPTURE RANGE , WHAT HAPPENS UPON CAPTURE ) 
        copTeam.addCaptureCondition(teams, robberTeam, 5.0, CaptureCondition.REMOVETARGET);
        
        // Victory Conditions
        //                      ( TEAMS, THIS TEAM, OPPOSING TEAM, METRIC FOR VICTORY, THRESHOLD , WHAT HAPPENS IF LESS , WHAT HAPPENS IF MORE )
        copTeam.setVictoryCondition(new VictoryCondition(teams, copTeam, robberTeam,
                Valuation.NUM_OPPONENTS_CAPTURED, 2.0, VictoryCondition.WON, VictoryCondition.NEITHER));        
        
        // Goals (Taskings)
        //                      ( WEIGHT, TEAMS, OPPONENT, SEEK/FLEE/CAPTURE? , TASKING ALGORITHM , GOAL THRESHOLD )   
        robberTeam.addAutoGoal(1.0, teams, copTeam, Goal.FLEE, TaskGenerator.AUTO_GRADIENT, 1.0);
        copTeam.addAutoGoal(1.0, teams, robberTeam, Goal.CAPTURE, TaskGenerator.CONTROL_CLOSEST, 1.0);
        
        // Additional Metrics to Track
        copTeam.addValuation(new Valuation(teams, copTeam, robberTeam, Valuation.DIST_AVG));
        
        return teams;
    }
    
    /** Simulation representing an m-on-n-on-k scenario. */
    public static Vector<Team> threeTeamSimulation() {        
        // Teams
        Vector<Team> teams = new Vector<Team>();
        Team sealTeam = new Team(); teams.add(sealTeam);
        Team penguinTeam = new Team(); teams.add(penguinTeam);
        Team fishTeam = new Team(); teams.add(fishTeam);
        //                      ( NAME , # , STARTING POS , BEHAVIOR ALGORITHM , COLOR )
        sealTeam.initSettings("Seals", 3, Team.START_RANDOM, Behavior.LEADING, Color.BLUE);
        penguinTeam.initSettings("Penguins", 4, Team.START_RANDOM, Behavior.LEADING, Color.BLACK);
        fishTeam.initSettings("Fish", 5, Team.START_RANDOM, Behavior.STRAIGHT, Color.GREEN);
        
        // Goals (Taskings)
        //                      ( WEIGHT, TEAMS, OPPONENT, SEEK/FLEE/CAPTURE? , TASKING ALGORITHM , GOAL THRESHOLD )  
        sealTeam.addAutoGoal(1.0, teams, penguinTeam, Goal.CAPTURE, TaskGenerator.CONTROL_CLOSEST, 1.0);
        penguinTeam.addAutoGoal(0.5, teams, sealTeam, Goal.FLEE, TaskGenerator.AUTO_GRADIENT, 1.0);
        penguinTeam.addAutoGoal(1.0, teams, fishTeam, Goal.CAPTURE, TaskGenerator.CONTROL_CLOSEST, 1.0);
        fishTeam.addAutoGoal(0.5, teams, penguinTeam, Goal.FLEE, TaskGenerator.AUTO_GRADIENT, 1.0);
        //fishTeam.addAutoGoal(1.0, teams, sealTeam, Goal.CAPTURE, TaskGenerator.CONTROL_CLOSEST, 1.0);
        sealTeam.addAutoGoal(0.5, teams, fishTeam, Goal.FLEE, TaskGenerator.AUTO_GRADIENT, 1.0);
        
        return teams;
    }
    
    /** Simulation representing an n-on-k scenario, with evaders seeking a "safehouse". */
    public static Vector<Team> twoPlusGoalSimulation() {
        
        // Teams
        Vector<Team> teams = new Vector<Team>();
        Team lionTeam = new Team(); teams.add(lionTeam);
        Team wildebeastTeam = new Team(); teams.add(wildebeastTeam);
        Team wateringHole = new Team(); teams.add(wateringHole);
        //                      ( NAME , # , STARTING POS , BEHAVIOR ALGORITHM , COLOR )
        lionTeam.initSettings("Lions", 4, Team.START_RANDOM, Behavior.QUADRANTSEARCHSMALL, Color.ORANGE);
        lionTeam.setTopSpeed(6.5);
        wildebeastTeam.initSettings("Wildebeast", 4, Team.START_RANDOM, Behavior.STRAIGHT, Color.GRAY);
        wateringHole.initSettings("Water", 1, Team.START_RANDOM, Behavior.STATIONARY, Color.BLUE);
                
        // Capture Conditions
        //                      ( TEAMS , OPPONENT , CAPTURE RANGE , WHAT HAPPENS UPON CAPTURE ) 
        lionTeam.addCaptureCondition(teams, wildebeastTeam, 1.0, CaptureCondition.REMOVEBOTH);
        wildebeastTeam.addCaptureCondition(teams, wateringHole, 1.0, CaptureCondition.REMOVEAGENT);
        
        // Victory Conditions
        //                      ( TEAMS, THIS TEAM, OPPOSING TEAM, METRIC FOR VICTORY, THRESHOLD , WHAT HAPPENS IF LESS , WHAT HAPPENS IF MORE )
        wildebeastTeam.setVictoryCondition(new VictoryCondition(teams, wildebeastTeam, wateringHole,
                Valuation.NUM_TEAM_SAFE, 0.0, VictoryCondition.WON, VictoryCondition.NEITHER));
        lionTeam.setVictoryCondition(new VictoryCondition(teams, lionTeam, wildebeastTeam,
                Valuation.POSSIBLE_CAPTURES_NOT_MADE, 0.0, VictoryCondition.WON, VictoryCondition.NEITHER));
        
        // Goals (Taskings)
        //                      ( WEIGHT, TEAMS, OPPONENT, SEEK/FLEE/CAPTURE? , TASKING ALGORITHM , GOAL THRESHOLD )  
        lionTeam.addAutoGoal(1.0, teams, wildebeastTeam, Goal.CAPTURE, TaskGenerator.AUTO_CLOSEST, 1.0);
        lionTeam.addAutoGoal(.01, teams, wateringHole, Goal.CAPTURE, TaskGenerator.AUTO_CLOSEST, 2.0);
        wildebeastTeam.addAutoGoal(0.5, teams, lionTeam, Goal.FLEE, TaskGenerator.AUTO_GRADIENT, 1.0);
        wildebeastTeam.addAutoGoal(1.0, teams, wateringHole, Goal.CAPTURE, TaskGenerator.CONTROL_CLOSEST, 1.0);
        
        return teams;
    }
    
    /** Simulation for analyzing lead factors. */
    public static Vector<Team> leadFactorSimulation() {
        
        // Teams
        Vector<Team> teams = new Vector<Team>();
        Team mathematicians = new Team(); teams.add(mathematicians);
        mathematicians.initSettings("Mathematician", 1, Team.START_ZERO, Behavior.APPROACHPATH, Color.GREEN);
//        mathematicians.setFixedPath("20cos(t/4)", "20sin(t/2)");
        
        for (int i = 0; i < 1; i++) {
            Team raptor = new Team(); teams.add(raptor);
            raptor.initSettings("Velociraptors", 11, Team.START_SPECIFIC, Behavior.LEADING, Color.DARK_GRAY);
            raptor.setSensorRange(100.0);
            raptor.setTopSpeed(5.5);
            for (int j = 0; j < 11; j++) {
                if (j > 0) { raptor.agents.get(j).synchronizePointModelWith(raptor.agents.get(0)); }
                raptor.agents.get(j).setLeadFactor(j/10.0);
                raptor.agents.get(j).setColorValue(Color.getHSBColor(0.2f+j/12.0f, 1.0f, 1.0f));
            }
            raptor.addAutoGoal(1.0, teams, mathematicians, Goal.SEEK, TaskGenerator.AUTO_CLOSEST, 1.0);
            raptor.addValuation(new Valuation(teams, raptor, mathematicians, Valuation.DIST_MIN));
            raptor.addValuation(new Valuation(teams, raptor, mathematicians, Valuation.AGENT_CLOSEST_TO_CAPTURE));
            raptor.addValuation(new Valuation(teams, raptor, mathematicians, Valuation.DIST_MAX));
        }
        
        return teams;
    }
    
    /** Simulation representing many, many teams all chasing and being chased. Pretty much chaos, but fun chaos! */
    public static Vector<Team> bigSimulation() {
        
        // Teams
        Vector<Team> teams = new Vector<Team>(10);
        for(int i=0;i<10;i++){teams.add(new Team());}
        //                      ( NAME , # , STARTING POS , BEHAVIOR ALGORITHM , COLOR )
        teams.get(0).initSettings("Old Lady", 1, Team.START_RANDOM, Behavior.LEADING, Color.RED);
        teams.get(1).initSettings("Horses", 4, Team.START_RANDOM, Behavior.LEADING, Color.ORANGE);
        teams.get(2).initSettings("Cows", 3, Team.START_LINE, Behavior.LEADING, Color.YELLOW);
        teams.get(3).initSettings("Goats", 5, Team.START_ARC, Behavior.LEADING, Color.GREEN);
        teams.get(4).initSettings("Dogs", 6, Team.START_RANDOM, Behavior.LEADING, Color.CYAN);
        teams.get(5).initSettings("Cats", 3, Team.START_CIRCLE, Behavior.LEADING, Color.BLUE);
        teams.get(6).initSettings("Birds", 2, Team.START_RANDOM, Behavior.LEADING, Color.MAGENTA);
        teams.get(7).initSettings("Spiders", 4, Team.START_CIRCLE, Behavior.STRAIGHT, Color.PINK);
        teams.get(8).initSettings("Flies", 2, Team.START_RANDOM, Behavior.STRAIGHT, Color.GRAY);
        teams.get(9).initSettings("Why", 2, Team.START_RANDOM, Behavior.STATIONARY, Color.BLACK);
        
        // Goals (Taskings)
        //                      ( WEIGHT, TEAMS, OPPONENT, SEEK/FLEE/CAPTURE? , TASKING ALGORITHM , GOAL THRESHOLD )  
        for (int i = 0; i < 9; i++) {
            teams.get(i).addAutoGoal(1.0, teams, teams.get(i + 1), Goal.CAPTURE, TaskGenerator.CONTROL_CLOSEST, 1.0);
            teams.get(i + 1).addAutoGoal(0.5, teams, teams.get(i), Goal.FLEE, TaskGenerator.AUTO_GRADIENT, 1.0);
        }
        
        return teams;
    }
     public static Vector<Team> footballGameSimulation(){
         
         //teams, offense is evading
         Vector<Team> teams = new Vector<Team>();
        Team sideline = new Team(); teams.add(sideline);
        Team endzone = new Team(); teams.add(endzone);
        Team offense = new Team(); teams.add(offense);
        Team defense = new Team(); teams.add(defense);
          //                             ( NAME , # , STARTING POS , BEHAVIOR ALGORITHM , COLOR )    
         sideline.initSettings("Sidelines", 20, Team.START_SIDELINE, Behavior.STATIONARY, Color.RED);
         endzone.initSettings("Endzone", 9, Team.START_ENDZONE, Behavior.STATIONARY, Color.GREEN);
         offense.initSettings("Army", 6, Team.START_OFFENSE, Behavior.STRAIGHT, Color.BLACK);
         defense.initSettings("Navy", 6, Team.START_DEFENSE, Behavior.LEADING, Color.blue);
        
         // Capture Conditions
        //                      ( TEAMS , OPPONENT , CAPTURE RANGE , WHAT HAPPENS UPON CAPTURE ) 
        offense.addCaptureCondition(teams, endzone, 1.0, CaptureCondition.REMOVEAGENT);
        defense.addCaptureCondition(teams, offense, 1.0, CaptureCondition.REMOVEBOTH);
        
        
        // Victory Conditions
        //                      ( TEAMS, THIS TEAM, OPPOSING TEAM, METRIC FOR VICTORY, THRESHOLD , WHAT HAPPENS IF LESS , WHAT HAPPENS IF MORE )
        offense.setVictoryCondition(new VictoryCondition(teams, offense, endzone,
                Valuation.POSSIBLE_CAPTURES_NOT_MADE, 0.0, VictoryCondition.WON, VictoryCondition.NEITHER));
        defense.setVictoryCondition(new VictoryCondition(teams, defense, offense,
                Valuation.POSSIBLE_CAPTURES_NOT_MADE, 0.0, VictoryCondition.WON, VictoryCondition.NEITHER));
        
        // Goals (Taskings)
        //                      ( WEIGHT, TEAMS, OPPONENT, SEEK/FLEE/CAPTURE? , TASKING ALGORITHM , GOAL THRESHOLD )  
        offense.addAutoGoal(.3, teams, sideline, Goal.FLEE, TaskGenerator.AUTO_CLOSEST, 1.0);
        offense.addAutoGoal(.7, teams, defense, Goal.FLEE, TaskGenerator.AUTO_CLOSEST, 1.0);
        offense.addAutoGoal(1.0, teams, endzone, Goal.CAPTURE, TaskGenerator.CONTROL_CLOSEST, 1.0);
        defense.addAutoGoal(1.0, teams, offense, Goal.CAPTURE, TaskGenerator.CONTROL_OPTIMAL, 1.0);
            
        return teams;
    }


}


