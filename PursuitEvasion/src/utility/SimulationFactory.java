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
import metrics.Valuation;
import metrics.VictoryCondition;
import scio.coordinate.R2;
import sequor.model.StringRangeModel;
import sequor.style.VisualStyle;
import tasking.Tasking;

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
    /** Specifies game with two teams, pursuers, and evaders, with evaders seeking a goal */
    public static final int SAHARA_PE = 3;
    /** Specifies simple game with three teams, pursuers, pursuers/evaders, and evaders */
    public static final int SIMPLE_PPE = 4;
    /** Lots of teams!! */
    public static final int LOTS_OF_TEAMS = 5;
    /** Evaders go to endzone. Has sidelines and obstacles. */
    public static final int FOOTBALL_GAME = 6;
    /** For looking at lead factors. */
    public static final int LEAD_FACTOR = 7;
    /** For flocking algorithm. */
    public static final int FLOCKING = 8;
    
    /** Strings corresponding to each preset simulation. */
    public static final String[] GAME_STRINGS = {
        "Custom",
        "1 Team Goal Seeking (Follow the Light)",
        "2 Team (Cops & Robbers)",
        "2 Team (Sahara)",
        "3 Team (Antarctica)",
        "Lots of Teams (Swallowed)",
        "Football Game",    
        "Leading Algorithms (Jurassic)",
        "Flocking (The Birds)"};
    
    
    // STATIC FACTORY METHODS
    
    /** Returns model for use in selecting a particular simulation. */
    public static StringRangeModel comboBoxRangeModel() {
        return new StringRangeModel(GAME_STRINGS);
    }
    
    /** Lookup function returning the list of teams used by each preset simulation. */
    public static Vector<Team> getTeams(int simCode) {
        switch(simCode) {
            case CUSTOM: return null;
            case SIMPLE_PE: return lightSimulation();
            case TWOTEAM: return twoTeamSimulation();
            case SAHARA_PE: return twoPlusGoalSimulation();
            case SIMPLE_PPE: return threeTeamSimulation();
            case LOTS_OF_TEAMS: return bigSimulation();
            case FOOTBALL_GAME: return footballGameSimulation();
            case LEAD_FACTOR: return leadFactorSimulation();
            case FLOCKING: return flockSimulation();
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
        bugTeam.addCaptureCondition(teams, lightTeam, 1.0, CaptureCondition.AGENTSAFE);
        
        // Victory Conditions
        //                      ( TEAMS, THIS TEAM, OPPOSING TEAM, METRIC FOR VICTORY, THRESHOLD , WHAT HAPPENS IF LESS , WHAT HAPPENS IF MORE )
        bugTeam.setVictoryCondition(new VictoryCondition(teams, bugTeam, lightTeam,
                Valuation.DIST_MIN, 5.0, VictoryCondition.NEITHER, VictoryCondition.WON, false));

        // Goals (Taskings)
        //                      ( WEIGHT, TEAMS, OPPONENT, SEEK/FLEE/CAPTURE? , TASKING ALGORITHM , GOAL THRESHOLD )
        bugTeam.addTasking(1.0, teams, lightTeam, Tasking.SEEK, Tasking.AUTO_CLOSEST, 1.0);
        lightTeam.addTasking(1.0, teams, bugTeam, Tasking.FLEE, Tasking.AUTO_GRADIENT, 1.0);
        

        // Additional Metrics to Track
        bugTeam.addValuation(new Valuation(teams, bugTeam, lightTeam, Valuation.NUM_ACTIVE_OPPONENTS));
        lightTeam.addValuation(new Valuation(teams, lightTeam, bugTeam, Valuation.TIME_TOTAL));

        return teams;
    }
    
    /** Simulation representing an n-on-k scenario "cops and robbers". */
    public static Vector<Team> twoTeamSimulation() {        
        // Teams
        Vector<Team> teams = new Vector<Team>();
        Team copTeam = new Team(); teams.add(copTeam);
        Team robberTeam = new Team(); teams.add(robberTeam);
        //                      ( NAME , # , STARTING POS , BEHAVIOR ALGORITHM , COLOR )
        copTeam.initSettings("Cops", 5, Team.START_RANDOM, Behavior.LEADING, Color.BLUE);
        copTeam.setTopSpeed(6.0);
        robberTeam.initSettings("Robbers", 4, Team.START_RANDOM, Behavior.STRAIGHT, Color.ORANGE);        
        
        // Capture Conditions
        //                      ( TEAMS , OPPONENT , CAPTURE RANGE , WHAT HAPPENS UPON CAPTURE ) 
        copTeam.addCaptureCondition(teams, robberTeam, 5.0, CaptureCondition.REMOVEBOTH);
        
        // Victory Conditions
        //                      ( TEAMS, THIS TEAM, OPPOSING TEAM, METRIC FOR VICTORY, THRESHOLD , WHAT HAPPENS IF LESS , WHAT HAPPENS IF MORE )
        copTeam.setVictoryCondition(new VictoryCondition(teams, copTeam, robberTeam,
                Valuation.NUM_ACTIVE_OPPONENTS, 2, VictoryCondition.NEITHER, VictoryCondition.WON, false));
        
        // Goals (Taskings)
        //                      ( WEIGHT, TEAMS, OPPONENT, SEEK/FLEE/CAPTURE? , TASKING ALGORITHM , GOAL THRESHOLD )   
        robberTeam.addTasking(1.0, teams, copTeam, Tasking.FLEE, Tasking.AUTO_GRADIENT, 1.0);
        copTeam.addTasking(1.0, teams, robberTeam, Tasking.CAPTURE, Tasking.CONTROL_CLOSEST, 1.0);
        
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
        sealTeam.addTasking(1.0, teams, penguinTeam, Tasking.CAPTURE, Tasking.CONTROL_CLOSEST, 1.0);
        penguinTeam.addTasking(0.95, teams, sealTeam, Tasking.FLEE, Tasking.AUTO_GRADIENT, 1.0);
        penguinTeam.addTasking(1.0, teams, fishTeam, Tasking.CAPTURE, Tasking.CONTROL_CLOSEST, 1.0);
        fishTeam.addTasking(0.95, teams, penguinTeam, Tasking.FLEE, Tasking.AUTO_GRADIENT, 1.0);
        fishTeam.addTasking(1.0, teams, sealTeam, Tasking.CAPTURE, Tasking.CONTROL_CLOSEST, 1.0);
        sealTeam.addTasking(0.95, teams, fishTeam, Tasking.FLEE, Tasking.AUTO_GRADIENT, 1.0);
        
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
        wildebeastTeam.addCaptureCondition(teams, wateringHole, 1.0, CaptureCondition.AGENTSAFE);
        
        // Victory Conditions
        //                      ( TEAMS, THIS TEAM, OPPOSING TEAM, METRIC FOR VICTORY, THRESHOLD , WHAT HAPPENS IF LESS , WHAT HAPPENS IF MORE )
        wildebeastTeam.setVictoryCondition(new VictoryCondition(teams, wildebeastTeam, wateringHole,
                Valuation.NUM_TEAM_SAFE, 2.0, VictoryCondition.WON, VictoryCondition.NEITHER, false));
        lionTeam.setVictoryCondition(new VictoryCondition(teams, lionTeam, wildebeastTeam,
                Valuation.POSSIBLE_CAPTURES_NOT_MADE, 2.0, VictoryCondition.NEITHER, VictoryCondition.WON, false));
        
        // Goals (Taskings)
        //                      ( WEIGHT, TEAMS, OPPONENT, SEEK/FLEE/CAPTURE? , TASKING ALGORITHM , GOAL THRESHOLD )  
        lionTeam.addTasking(1.0, teams, wildebeastTeam, Tasking.CAPTURE, Tasking.AUTO_CLOSEST, 1.0);
        lionTeam.addTasking(.01, teams, wateringHole, Tasking.CAPTURE, Tasking.AUTO_CLOSEST, 2.0);
        wildebeastTeam.addTasking(0.5, teams, lionTeam, Tasking.FLEE, Tasking.AUTO_GRADIENT, 1.0);
        wildebeastTeam.addTasking(1.0, teams, wateringHole, Tasking.CAPTURE, Tasking.CONTROL_CLOSEST, 1.0);
        
        return teams;
    }
    
    /** Simulation for analyzing lead factors. */
    public static Vector<Team> leadFactorSimulation() {
        
        // Teams
        Vector<Team> teams = new Vector<Team>();
        Team mathematicians = new Team(); teams.add(mathematicians);
        mathematicians.initSettings("Mathematician", 1, Team.START_ZERO, Behavior.APPROACHPATH, Color.DARK_GRAY);
//        mathematicians.setFixedPath("20cos(t/4)", "20sin(t/2)");
        
        for (int i = 0; i < 1; i++) {
            Team raptor = new Team(); teams.add(raptor);
            raptor.initSettings("Velociraptors", 7, Team.START_SPECIFIC, Behavior.LEADING, Color.RED);
            raptor.setSensorRange(300.0);
            raptor.setTopSpeed(5.5);
            for (int j = 0; j < 7; j++) {
                if (j > 0) { raptor.agents.get(j).synchronizePointModelWith(raptor.agents.get(0)); }
                raptor.agents.get(j).setLeadFactor(j/5.0);
                raptor.agents.get(j).setColorValue(Color.getHSBColor(0.2f+j/7.0f, 1.0f, 1.0f));
            }
            raptor.addTasking(1.0, teams, mathematicians, Tasking.SEEK, Tasking.AUTO_CLOSEST, 1.0);
            raptor.addValuation(new Valuation(teams, raptor, mathematicians, Valuation.DIST_MIN));
            raptor.addValuation(new Valuation(teams, raptor, mathematicians, Valuation.AGENT_CLOSEST_TO_CAPTURE));
            raptor.addValuation(new Valuation(teams, raptor, mathematicians, Valuation.DIST_MAX));
        }
        
        return teams;
    }
    
    /** Exploration of flocking */
    public static Vector<Team> flockSimulation() {
        Vector<Team> teams = new Vector<Team>();
        Team flock = new Team(); teams.add(flock);
        Team predator = new Team(); teams.add(predator);
        Team obstacle = new Team(); teams.add(obstacle);
        flock.initSettings("Flock", 25, Team.START_RANDOM, Behavior.STRAIGHT, Color.BLUE);
        flock.setSensorRange(50.0);
        flock.setCommRange(0.0);
        flock.addTasking(.5, teams, flock, Tasking.FLEE, Tasking.AUTO_CLOSEST, 1);
        flock.addTasking(.4, teams, flock, Tasking.SEEK, Tasking.AUTO_COM, 1);
        flock.addTasking(.7, teams, flock, Tasking.SEEK, Tasking.AUTO_DIR_COM, 1);
        flock.addTasking(1, teams, predator, Tasking.FLEE, Tasking.AUTO_GRADIENT, 1);
        flock.addTasking(.5, teams, obstacle, Tasking.FLEE, Tasking.AUTO_CLOSEST, 1);
        
        predator.initSettings("Predator", 4, Team.START_RANDOM, Behavior.LEADING, Color.RED.darker());
        predator.setTopSpeed(6);
        predator.setSensorRange(55.0);
        predator.setCommRange(150.0);
        predator.addTasking(1, teams, flock, Tasking.CAPTURE, Tasking.AUTO_CLOSEST, 2.0);
        predator.addTasking(.7, teams, obstacle, Tasking.FLEE, Tasking.AUTO_CLOSEST, 1);
        
        obstacle.initSettings("Obstacle", 4, Team.START_RANDOM, Behavior.STATIONARY, Color.DARK_GRAY);
        
        predator.addCaptureCondition(teams, flock, 1.0, CaptureCondition.REMOVETARGET);
        
        predator.addValuation(new Valuation(teams, predator, flock, Valuation.NUM_OPPONENTS_CAPTURED));
        
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
            teams.get(i).addTasking(1.0, teams, teams.get(i + 1), Tasking.CAPTURE, Tasking.CONTROL_CLOSEST, 1.0);
            teams.get(i + 1).addTasking(0.5, teams, teams.get(i), Tasking.FLEE, Tasking.AUTO_GRADIENT, 1.0);
        }
        
        return teams;
    }
    
    /** Simulation including sidelines */
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
         defense.initSettings("Navy", 6, Team.START_DEFENSE, Behavior.PLUCKERLEAD, Color.blue);
        
         // Capture Conditions
        //                      ( TEAMS , OPPONENT , CAPTURE RANGE , WHAT HAPPENS UPON CAPTURE ) 
        offense.addCaptureCondition(teams, endzone, 1.0, CaptureCondition.AGENTSAFE);
        defense.addCaptureCondition(teams, offense, 1.0, CaptureCondition.REMOVEBOTH);
        
        
        // Victory Conditions
        //                      ( TEAMS, THIS TEAM, OPPOSING TEAM, METRIC FOR VICTORY, THRESHOLD , WHAT HAPPENS IF LESS , WHAT HAPPENS IF MORE )
        offense.setVictoryCondition(new VictoryCondition(teams, offense, endzone,
                Valuation.NUM_TEAM_SAFE, 2.0, VictoryCondition.WON, VictoryCondition.NEITHER, false));
        defense.setVictoryCondition(new VictoryCondition(teams, defense, offense,
                Valuation.OPPONENTS_UNCAPTURED, 1.0, VictoryCondition.NEITHER, VictoryCondition.WON, false));
        
        // Goals (Taskings)
        //                      ( WEIGHT, TEAMS, OPPONENT, SEEK/FLEE/CAPTURE? , TASKING ALGORITHM , GOAL THRESHOLD )  
        offense.addTasking(.3, teams, sideline, Tasking.FLEE, Tasking.AUTO_CLOSEST, 1.0);
        offense.addTasking(.7, teams, defense, Tasking.FLEE, Tasking.AUTO_CLOSEST, 1.0);
        offense.addTasking(1.0, teams, endzone, Tasking.CAPTURE, Tasking.CONTROL_CLOSEST, 1.0);
        defense.addTasking(1.0, teams, offense, Tasking.CAPTURE, Tasking.CONTROL_OPTIMAL, 1.0);
            
        return teams;
    }


}


