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
    /** Custom */
    public static final int CUSTOM = 6;
    public static final String[] GAME_STRINGS = {"Follow the Light", "Cops & Robbers", "Antarctica", "Sahara", "Swallowed", "Jurassic", "Custom"};
    // STATIC FACTORY METHODS
    public static StringRangeModel comboBoxRangeModel() {
        return new StringRangeModel(GAME_STRINGS, SIMPLE_PE, 0, 6);
    }
    
    public static void setSimulation(Simulation sim, int simCode) {
        String name;
        Vector<Team> teams;
        int primary;
        switch (simCode) {
            case SIMPLE_PE:
                name = "Follow the Light";
                teams = lightSimulation();
                primary = 1;
                break;
            case TWOTEAM:                
                name = "Cops & Robbers";
                teams = twoTeamSimulation();
                primary = 1;
                break;
            case SIMPLE_PPE:                
                name = "Antarctica";
                teams = threeTeamSimulation();
                primary = 1;
                break;
            case SAHARA_PE:                
                name = "Sahara";
                teams = twoPlusGoalSimulation();
                primary = 0;
                break;
            case LOTS_OF_FUN:                
                name = "Swallowed";
                teams = bigSimulation();
                primary = 0;
                break;
            case LEAD_FACTOR:                
                name = "Jurassic";
                teams = leadFactorSimulation();
                primary = 0;
                break;
            default:
                name = "custom operation not supported!";
                teams = null;
                primary = 0;
                break;
        }
        sim.mainInitialize(name, teams.size(), teams, primary);
    }

    /** Simulation representing an n-on-1 scenario. */
    public static Vector<Team> lightSimulation() {
        Vector<Team> teams = new Vector<Team>();
        //                      NAME        #    STARTING POS        BEHAVIOR ALGORITHM          COLOR
        Team bugTeam = new Team("Bugs", 4, Team.START_RANDOM, Behavior.LEADING, Color.DARK_GRAY);
        Team lightTeam = new Team("Light", 1, Team.START_RANDOM, Behavior.STRAIGHT, VisualStyle.DARK_GREEN);        
        teams.add(bugTeam);
        teams.add(lightTeam);
        bugTeam.setVictoryCondition(new VictoryCondition(teams, bugTeam, lightTeam,
                Valuation.DIST_MIN, 5.0, VictoryCondition.NEITHER, VictoryCondition.WON));        
        bugTeam.addCaptureCondition(teams, lightTeam, 1.0, CaptureCondition.REMOVEAGENT);
        
        bugTeam.addValuation(new Valuation(teams, bugTeam, lightTeam, Valuation.NUM_OPPONENT));
        lightTeam.addValuation(new Valuation(teams, lightTeam, bugTeam, Valuation.TIME_TOTAL));
        
        bugTeam.addAutoGoal(1.0, teams, lightTeam, Goal.SEEK, TaskGenerator.AUTO_CLOSEST, 1.0);
        lightTeam.addAutoGoal(1.0, teams, bugTeam, Goal.FLEE, TaskGenerator.AUTO_GRADIENT, 1.0);
        
        return teams;
    }
    
    public static Vector<Team> twoTeamSimulation() {
        Vector<Team> teams = new Vector<Team>();
        //                                      #    STARTING POS        BEHAVIOR ALGORITHM          COLOR
        Team copTeam = new Team("Cops", 5, Team.START_RANDOM, Behavior.LEADING, Color.BLUE);
        Team robberTeam = new Team("Robbers", 4, Team.START_RANDOM, Behavior.STRAIGHT, Color.ORANGE);        
        teams.add(copTeam);
        teams.add(robberTeam);
        
        copTeam.setVictoryCondition(new VictoryCondition(teams, copTeam, robberTeam,
                Valuation.NUM_CAP, 2.0, VictoryCondition.WON, VictoryCondition.NEITHER));
        copTeam.addCaptureCondition(teams, robberTeam, 5.0, CaptureCondition.REMOVETARGET);
        
        copTeam.addValuation(new Valuation(teams, copTeam, robberTeam, Valuation.DIST_AVG));
        
        robberTeam.addAutoGoal(1.0, teams, copTeam, Goal.FLEE, TaskGenerator.AUTO_GRADIENT, 1.0);
        copTeam.addAutoGoal(1.0, teams, robberTeam, Goal.CAPTURE, TaskGenerator.CONTROL_CLOSEST, 1.0);
        
        return teams;
    }
    
    public static Vector<Team> threeTeamSimulation() {
        Vector<Team> teams = new Vector<Team>();
        //                      #    STARTING POS        BEHAVIOR ALGORITHM          COLOR
        Team dogTeam = new Team(3, Team.START_RANDOM, Behavior.LEADING, Color.BLUE);
        Team catTeam = new Team(4, Team.START_RANDOM, Behavior.LEADING, Color.BLACK);
        Team mouseTeam = new Team(5, Team.START_RANDOM, Behavior.STRAIGHT, Color.GREEN);
        dogTeam.addAutoGoal(1.0, teams, catTeam, Goal.CAPTURE, TaskGenerator.CONTROL_CLOSEST, 1.0);
        catTeam.addAutoGoal(0.5, teams, dogTeam, Goal.FLEE, TaskGenerator.AUTO_GRADIENT, 1.0);
        catTeam.addAutoGoal(1.0, teams, mouseTeam, Goal.CAPTURE, TaskGenerator.CONTROL_CLOSEST, 1.0);
        mouseTeam.addAutoGoal(0.5, teams, catTeam, Goal.FLEE, TaskGenerator.AUTO_GRADIENT, 1.0);
        mouseTeam.addAutoGoal(1.0, teams, dogTeam, Goal.CAPTURE, TaskGenerator.CONTROL_CLOSEST, 1.0);
        dogTeam.addAutoGoal(0.5, teams, mouseTeam, Goal.FLEE, TaskGenerator.AUTO_GRADIENT, 1.0);
        dogTeam.setString("Seals");
        catTeam.setString("Penguins");
        mouseTeam.setString("Fish");
        teams.add(dogTeam);
        teams.add(catTeam);
        teams.add(mouseTeam);
        return teams;
    }
    
    public static Vector<Team> twoPlusGoalSimulation() {
        Vector<Team> teams = new Vector<Team>();
        //                      #    STARTING POS        BEHAVIOR ALGORITHM          COLOR
        Team lionTeam = new Team(3, Team.START_RANDOM, Behavior.LEADING, Color.ORANGE);
        lionTeam.setTopSpeed(5.5);
        Team wildebeastTeam = new Team(4, Team.START_RANDOM, Behavior.STRAIGHT, Color.GRAY);
        Team wateringHole = new Team(1, Team.START_RANDOM, Behavior.STATIONARY, Color.BLUE);
        lionTeam.addAutoGoal(1.0, teams, wildebeastTeam, Goal.CAPTURE, TaskGenerator.AUTO_CLOSEST, 1.0);
        lionTeam.addAutoGoal(.01, teams, wateringHole, Goal.CAPTURE, TaskGenerator.CONTROL_CLOSEST, 7.0);
        wildebeastTeam.addAutoGoal(0.5, teams, lionTeam, Goal.FLEE, TaskGenerator.AUTO_GRADIENT, 1.0);
        wildebeastTeam.addAutoGoal(1.0, teams, wateringHole, Goal.CAPTURE, TaskGenerator.CONTROL_CLOSEST, 1.0);
        lionTeam.setString("Lions");        
        wildebeastTeam.setString("Wildebeast");
        wateringHole.setString("Watering Hole");
        teams.add(lionTeam);
        teams.add(wildebeastTeam);
        teams.add(wateringHole);
        lionTeam.addCaptureCondition(teams, wildebeastTeam, 1.0, CaptureCondition.REMOVEBOTH);
        wildebeastTeam.addCaptureCondition(teams, wateringHole, 1.0, CaptureCondition.REMOVEAGENT);
        wildebeastTeam.setVictoryCondition(new VictoryCondition(teams, wildebeastTeam, wateringHole,
                Valuation.NUM_TEAM, 2.0, VictoryCondition.WON, VictoryCondition.NEITHER));
        lionTeam.addCaptureCondition(teams, wildebeastTeam, 1.0, CaptureCondition.REMOVEBOTH);
        lionTeam.setVictoryCondition(new VictoryCondition(teams, lionTeam, wildebeastTeam,
                Valuation.NUM_TEAM, 2.0, VictoryCondition.WON, VictoryCondition.NEITHER));
                return teams;
    }
    
    public static Vector<Team> leadFactorSimulation() {
        Vector<Team> teams = new Vector<Team>();

        //                 #    STARTING POS        BEHAVIOR ALGORITHM          COLOR
        Team dogs = new Team(11, Team.START_ZERO, Behavior.LEADING, Color.DARK_GRAY);
        Team cats = new Team(1, Team.START_RANDOM, Behavior.APPROACHPATH, Color.GREEN);
        cats.setFixedPath("20cos(t/4)", "20sin(t/2)");
        for (int i = 0; i < dogs.size(); i++) {
            dogs.get(i).setColor(new Color(100 + 15 * i, 25 * i, 25 * i));
            dogs.get(i).setLeadFactor(i / 10.0);
        }
        dogs.get(0).setColor(Color.DARK_GRAY);
        dogs.get(dogs.size() - 1).setColor(new Color(100, 100, 250));
        dogs.addAutoGoal(1.0, teams, cats, Goal.SEEK, TaskGenerator.AUTO_CLOSEST, 1.0);
        dogs.setString("Velociraptors");
        cats.setString("Mathematicians");
        teams.add(dogs);
        teams.add(cats);
        return teams;
    }
    
    public static Vector<Team> bigSimulation() {
        Vector<Team> teams = new Vector<Team>();
        //                  #     STARTING POS        BEHAVIOR ALGORITHM          COLOR
        teams.add(new Team(1, Team.START_RANDOM, Behavior.LEADING, Color.RED));
        teams.add(new Team(4, Team.START_RANDOM, Behavior.LEADING, Color.ORANGE));
        teams.add(new Team(3, Team.START_LINE, Behavior.LEADING, Color.YELLOW));
        teams.add(new Team(5, Team.START_ARC, Behavior.LEADING, Color.GREEN));
        teams.add(new Team(6, Team.START_RANDOM, Behavior.LEADING, Color.CYAN));
        teams.add(new Team(3, Team.START_CIRCLE, Behavior.LEADING, Color.BLUE));
        teams.add(new Team(2, Team.START_RANDOM, Behavior.LEADING, Color.MAGENTA));
        teams.add(new Team(4, Team.START_CIRCLE, Behavior.STRAIGHT, Color.PINK));
        teams.add(new Team(2, Team.START_RANDOM, Behavior.STRAIGHT, Color.GRAY));
        teams.add(new Team(2, Team.START_RANDOM, Behavior.STATIONARY, Color.BLACK));
        teams.get(0).setString("Old Lady");
        teams.get(1).setString("Horses");
        teams.get(2).setString("Cows");
        teams.get(3).setString("Goats");
        teams.get(4).setString("Dogs");
        teams.get(5).setString("Cats");
        teams.get(6).setString("Birds");
        teams.get(7).setString("Spiders");
        teams.get(8).setString("Flies");
        teams.get(9).setString("Why");
        for (int i = 0; i < 9; i++) {
            teams.get(i).addAutoGoal(1.0, teams, teams.get(i + 1), Goal.CAPTURE, TaskGenerator.CONTROL_CLOSEST, 1.0);
            teams.get(i + 1).addAutoGoal(0.5, teams, teams.get(i), Goal.FLEE, TaskGenerator.AUTO_GRADIENT, 1.0);
        }
        return teams;
    }
}
