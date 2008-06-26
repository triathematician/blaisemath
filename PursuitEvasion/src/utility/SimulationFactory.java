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
import simulation.Simulation;
import tasking.TaskGenerator;

/**
 * This class provides preset simulations to load into a program.
 * <br><br>
 * @author Elisha Peterson
 */
public class SimulationFactory {
    
    // CONSTANTS
    
    /** n-on-1 game */
    public static final int SIMPLE_PE = 0;
    /** Specifies simple game with two teams, pursuers and evaders */
    public static final int TWOTEAM = 1;
    /** Specifies simple game with three teams, pursuers, pursuers/evaders, and evaders */
    public static final int SIMPLE_PPE = 2;
    /** Specifies game with two teams, pursuers, and evaders, with evaders seeking a goal */
    public static final int SAHARA_PE = 3;
    /** Lots of teams!! */
    public static final int LOTS_OF_FUN = 4;
    /** For looking at lead factors. */
    public static final int LEAD_FACTOR = 5;
    /** Custom starting locations */
    public static final int CUSTOM_START_SAHARA = 6;
    /** Custom */
    public static final int FOOTBALL_GAME =7;
    /** Evaders go to endzone. Has sidelines and obstacles. */
    public static final int CUSTOM = 7;
    
    /** Strings corresponding to each preset simulation. */
    public static final String[] GAME_STRINGS = {
        "Follow the Light", "Cops & Robbers", "Antarctica",
        "Sahara", "Swallowed", "Jurassic",
        "Custom Start Sahara","Football Game", "Custom"};
    
    
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
    
    /** Initializes the specified simulation to the settings specified by the given code. */
    public static void setSimulation(Simulation sim, int simCode) {
        sim.mainInitialize(GAME_STRINGS[simCode], getTeams(simCode));
    }
    
    /** Simulation representing an n-on-1 scenario. */
    public static Vector<Team> lightSimulation() {        
        // Teams
        Vector<Team> teams = new Vector<Team>();
        //                      ( NAME , # , STARTING POS , BEHAVIOR ALGORITHM , COLOR )
        Team bugTeam = new Team("Bugs", 4, Team.START_RANDOM, Behavior.LEADING, Color.DARK_GRAY);
        Team lightTeam = new Team("Light", 1, Team.START_RANDOM, Behavior.STRAIGHT, VisualStyle.DARK_GREEN);        
        teams.add(bugTeam);
        teams.add(lightTeam);
        
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
        bugTeam.addValuation(new Valuation(teams, bugTeam, lightTeam, Valuation.NUM_OPPONENT));
        lightTeam.addValuation(new Valuation(teams, lightTeam, bugTeam, Valuation.TIME_TOTAL));

        return teams;
    }
    
    /** Simulation representing an n-on-k scenario. */
    public static Vector<Team> twoTeamSimulation() {        
        // Teams
        Vector<Team> teams = new Vector<Team>();
        //                      ( NAME , # , STARTING POS , BEHAVIOR ALGORITHM , COLOR )
        Team copTeam = new Team("Cops", 5, Team.START_RANDOM, Behavior.LEADING, Color.BLUE);
        Team robberTeam = new Team("Robbers", 4, Team.START_RANDOM, Behavior.STRAIGHT, Color.ORANGE);        
        teams.add(copTeam);
        teams.add(robberTeam);
        
        // Capture Conditions
        //                      ( TEAMS , OPPONENT , CAPTURE RANGE , WHAT HAPPENS UPON CAPTURE ) 
        copTeam.addCaptureCondition(teams, robberTeam, 5.0, CaptureCondition.REMOVETARGET);
        
        // Victory Conditions
        //                      ( TEAMS, THIS TEAM, OPPOSING TEAM, METRIC FOR VICTORY, THRESHOLD , WHAT HAPPENS IF LESS , WHAT HAPPENS IF MORE )
        copTeam.setVictoryCondition(new VictoryCondition(teams, copTeam, robberTeam,
                Valuation.NUM_CAP, 2.0, VictoryCondition.WON, VictoryCondition.NEITHER));        
        
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
        //                      ( NAME , # , STARTING POS , BEHAVIOR ALGORITHM , COLOR )
        Team sealTeam = new Team("Seals", 3, Team.START_RANDOM, Behavior.LEADING, Color.BLUE);
        Team penguinTeam = new Team("Penguins", 4, Team.START_RANDOM, Behavior.LEADING, Color.BLACK);
        Team fishTeam = new Team("Fish", 5, Team.START_RANDOM, Behavior.STRAIGHT, Color.GREEN);
        teams.add(sealTeam);
        teams.add(penguinTeam);
        teams.add(fishTeam);
        
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
        //                      ( NAME , # , STARTING POS , BEHAVIOR ALGORITHM , COLOR )
        Team lionTeam = new Team("Lions", 4, Team.START_RANDOM, Behavior.QUADRANTSEARCHSMALL, Color.ORANGE);
        lionTeam.setTopSpeed(6.5);
        Team wildebeastTeam = new Team("Wildebeast", 4, Team.START_RANDOM, Behavior.STRAIGHT, Color.GRAY);
        Team wateringHole = new Team("Water", 1, Team.START_RANDOM, Behavior.STATIONARY, Color.BLUE);
        teams.add(lionTeam);
        teams.add(wildebeastTeam);
        teams.add(wateringHole);
                
        // Capture Conditions
        //                      ( TEAMS , OPPONENT , CAPTURE RANGE , WHAT HAPPENS UPON CAPTURE ) 
        lionTeam.addCaptureCondition(teams, wildebeastTeam, 1.0, CaptureCondition.REMOVEBOTH);
        wildebeastTeam.addCaptureCondition(teams, wateringHole, 1.0, CaptureCondition.REMOVEAGENT);
        
        // Victory Conditions
        //                      ( TEAMS, THIS TEAM, OPPOSING TEAM, METRIC FOR VICTORY, THRESHOLD , WHAT HAPPENS IF LESS , WHAT HAPPENS IF MORE )
        wildebeastTeam.setVictoryCondition(new VictoryCondition(teams, wildebeastTeam, wateringHole,
                Valuation.EVERY_CAPTURE, 0.0, VictoryCondition.WON, VictoryCondition.NEITHER));
        lionTeam.setVictoryCondition(new VictoryCondition(teams, lionTeam, wildebeastTeam,
                Valuation.EVERY_CAPTURE, 0.0, VictoryCondition.WON, VictoryCondition.NEITHER));
        
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
        //                      ( NAME , # , STARTING POS , BEHAVIOR ALGORITHM , COLOR )
        Team raptors = new Team("Velociraptors", 11, Team.START_ZERO, Behavior.LEADING, Color.DARK_GRAY);
        Team mathematicians = new Team("Mathematician", 1, Team.START_RANDOM, Behavior.APPROACHPATH, Color.GREEN);
        teams.add(raptors);
        teams.add(mathematicians);
        
        // Special Settings
        mathematicians.setFixedPath("20cos(t/4)", "20sin(t/2)");
        for (int i = 0; i < raptors.size(); i++) {
            raptors.get(i).setColor(new Color(100 + 15 * i, 25 * i, 25 * i));
            raptors.get(i).setLeadFactor(i / 10.0);
        }
        raptors.get(0).setColor(Color.DARK_GRAY);
        raptors.get(raptors.size() - 1).setColor(new Color(100, 100, 250));
        
        // Goals (Taskings)
        //                      ( WEIGHT, TEAMS, OPPONENT, SEEK/FLEE/CAPTURE? , TASKING ALGORITHM , GOAL THRESHOLD )  
        raptors.addAutoGoal(1.0, teams, mathematicians, Goal.SEEK, TaskGenerator.AUTO_CLOSEST, 1.0);
        
        return teams;
    }
    
    /** Simulation representing many, many teams all chasing and being chased. Pretty much chaos, but fun chaos! */
    public static Vector<Team> bigSimulation() {
        
        // Teams
        Vector<Team> teams = new Vector<Team>();
        //                      ( NAME , # , STARTING POS , BEHAVIOR ALGORITHM , COLOR )
        teams.add(new Team("Old Lady", 1, Team.START_RANDOM, Behavior.LEADING, Color.RED));
        teams.add(new Team("Horses", 4, Team.START_RANDOM, Behavior.LEADING, Color.ORANGE));
        teams.add(new Team("Cows", 3, Team.START_LINE, Behavior.LEADING, Color.YELLOW));
        teams.add(new Team("Goats", 5, Team.START_ARC, Behavior.LEADING, Color.GREEN));
        teams.add(new Team("Dogs", 6, Team.START_RANDOM, Behavior.LEADING, Color.CYAN));
        teams.add(new Team("Cats", 3, Team.START_CIRCLE, Behavior.LEADING, Color.BLUE));
        teams.add(new Team("Birds", 2, Team.START_RANDOM, Behavior.LEADING, Color.MAGENTA));
        teams.add(new Team("Spiders", 4, Team.START_CIRCLE, Behavior.STRAIGHT, Color.PINK));
        teams.add(new Team("Flies", 2, Team.START_RANDOM, Behavior.STRAIGHT, Color.GRAY));
        teams.add(new Team("Why", 2, Team.START_RANDOM, Behavior.STATIONARY, Color.BLACK));
        
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
          //                             ( NAME , # , STARTING POS , BEHAVIOR ALGORITHM , COLOR )    
         Team sideline = new Team("Sidelines", 20, Team.START_SIDELINE, Behavior.STATIONARY, Color.RED);
         Team endzone = new Team("Endzone", 9, Team.START_ENDZONE, Behavior.STATIONARY, Color.GREEN);
         Team offense = new Team("Army", 6, Team.START_OFFENSE, Behavior.STRAIGHT, Color.BLACK);
         Team defense = new Team("Navy", 6, Team.START_DEFENSE, Behavior.LEADING, Color.blue);
         teams.add(sideline);
         teams.add(endzone);
         teams.add(offense);
         teams.add(defense);
        
         // Capture Conditions
        //                      ( TEAMS , OPPONENT , CAPTURE RANGE , WHAT HAPPENS UPON CAPTURE ) 
        offense.addCaptureCondition(teams, endzone, 1.0, CaptureCondition.REMOVEAGENT);
        defense.addCaptureCondition(teams, offense, 1.0, CaptureCondition.REMOVEBOTH);
        
        
        // Victory Conditions
        //                      ( TEAMS, THIS TEAM, OPPOSING TEAM, METRIC FOR VICTORY, THRESHOLD , WHAT HAPPENS IF LESS , WHAT HAPPENS IF MORE )
        offense.setVictoryCondition(new VictoryCondition(teams, offense, endzone,
                Valuation.EVERY_CAPTURE, 0.0, VictoryCondition.WON, VictoryCondition.NEITHER));
        defense.setVictoryCondition(new VictoryCondition(teams, defense, offense,
                Valuation.EVERY_CAPTURE, 0.0, VictoryCondition.WON, VictoryCondition.NEITHER));
        
        // Goals (Taskings)
        //                      ( WEIGHT, TEAMS, OPPONENT, SEEK/FLEE/CAPTURE? , TASKING ALGORITHM , GOAL THRESHOLD )  
        offense.addAutoGoal(.3, teams, sideline, Goal.FLEE, TaskGenerator.AUTO_CLOSEST, 1.0);
        offense.addAutoGoal(.7, teams, defense, Goal.FLEE, TaskGenerator.AUTO_CLOSEST, 1.0);
        offense.addAutoGoal(1.0, teams, endzone, Goal.CAPTURE, TaskGenerator.CONTROL_CLOSEST, 1.0);
        defense.addAutoGoal(1.0, teams, offense, Goal.CAPTURE, TaskGenerator.CONTROL_OPTIMAL, 1.0);
            
        return teams;
    }


}


