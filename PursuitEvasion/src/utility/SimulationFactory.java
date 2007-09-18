/*
 * SimulationFactory.java
 * Created on Sep 18, 2007, 10:00:49 AM
 */

package utility;

import Model.ComboBoxRangeModel;
import simulation.Team;
import behavior.Behavior;
import behavior.Tasking;
import java.awt.Color;
import java.util.ArrayList;
import simulation.Simulation;
import behavior.Goal;

/**
 * This class provides preset simulations to load into a program.
 * <br><br>
 * @author Elisha Peterson
 */
public class SimulationFactory {
    
    // CONSTANTS
    
    /** n-on-1 game */
    public static final int SIMPLE_PE=0;
    /** Specifies simple game with two teams, pursuers and evaders */
    public static final int TWOTEAM=1;
    /** Specifies simple game with three teams, pursuers, pursuers/evaders, and evaders */
    public static final int SIMPLE_PPE=2;
    /** Specifies game with two teams, pursuers, and evaders, with evaders seeking a goal */
    public static final int GOAL_PE=3;
    /** Lots of teams!! */
    public static final int LOTS_OF_FUN=4;
    /** For looking at lead factors. */
    public static final int LEAD_FACTOR=5;
    /** Custom */
    public static final int CUSTOM=6;
    public static final String[] GAME_STRINGS={"Follow the Light","Cops & Robbers","Antarctica","Sahara","Swallowed","Jurassic","Custom"};


    // STATIC FACTORY METHODS
    
    public static ComboBoxRangeModel comboBoxRangeModel(){return new ComboBoxRangeModel(GAME_STRINGS,SIMPLE_PE,0,6);}
    
    public static void setSimulation(Simulation sim,int simCode){
        switch(simCode){
        case SIMPLE_PE:     sim.setString("Follow the Light");  sim.setNumTeams(2);     sim.initTeams(lightSimulation());           break;
        case TWOTEAM:       sim.setString("Cops & Robbers");    sim.setNumTeams(2);     sim.initTeams(twoTeamSimulation());         break;
        case SIMPLE_PPE:    sim.setString("Antarctica");        sim.setNumTeams(3);     sim.initTeams(threeTeamSimulation());       break;
        case GOAL_PE:       sim.setString("Sahara");          sim.setNumTeams(3);     sim.initTeams(twoPlusGoalSimulation());     break;
        case LOTS_OF_FUN:   sim.setString("Swallowed");         sim.setNumTeams(10);    sim.initTeams(bigSimulation());             break;
        case LEAD_FACTOR:   sim.setString("Jurassic");      sim.setNumTeams(2);     sim.initTeams(leadFactorSimulation());      break;
        case CUSTOM:        System.out.println("custom operation not supported!");;
        }
    }
    
    public static ArrayList<Team> lightSimulation(){
        ArrayList<Team> teams=new ArrayList<Team>();
        //                 #    STARTING POS        GOAL TYPE       ONE/ALL     CAP.DIST.   TASK ALGORITHM          BEHAVIOR ALGORITHM          COLOR
        Team dogs=new Team(4,   Team.START_RANDOM,  Goal.PURSUE,    Goal.ALL,   5.0,        Tasking.AUTO_CLOSEST,   Behavior.PURSUIT_LEADING,   Color.DARK_GRAY);
        Team cats=new Team(1,   Team.START_RANDOM,  Goal.EVADE,     Goal.ALL,   5.0,        Tasking.AUTO_GRADIENT,  Behavior.FLEE,              Color.GREEN);
        dogs.setString("Bugs");         dogs.setTarget(cats);   teams.add(dogs);
        cats.setString("Light");        cats.setTarget(dogs);   teams.add(cats);
        return teams;
    }
    
    public static ArrayList<Team> twoTeamSimulation(){
        ArrayList<Team> teams=new ArrayList<Team>();        
        //                 #    STARTING POS        GOAL TYPE       ONE/ALL     CAP.DIST.   TASK ALGORITHM          BEHAVIOR ALGORITHM          COLOR
        Team dogs=new Team(5,   Team.START_RANDOM,  Goal.PURSUE,    Goal.ALL,   5.0,        Tasking.CONTROL_CLOSEST,Behavior.PURSUIT_LEADING,   Color.BLUE);
        Team cats=new Team(4,   Team.START_RANDOM,  Goal.EVADE,     Goal.ALL,   5.0,        Tasking.AUTO_GRADIENT,  Behavior.FLEE,              Color.ORANGE);
        dogs.setString("Cops");         dogs.setTarget(cats);   teams.add(dogs);
        cats.setString("Robbers");      cats.setTarget(dogs);   teams.add(cats);
        return teams;
    }
    
    public static ArrayList<Team> threeTeamSimulation(){
        ArrayList<Team> teams=new ArrayList<Team>();
        
        //                 #    STARTING POS        GOAL TYPE       ONE/ALL     CAP.DIST.   TASK ALGORITHM          BEHAVIOR ALGORITHM          COLOR
        Team dogs=new Team(3,   Team.START_RANDOM,  Goal.PURSUE,    Goal.ALL,   5.0,        Tasking.CONTROL_CLOSEST,Behavior.PURSUIT_LEADING,   Color.BLUE);
        Team cats=new Team(4,   Team.START_RANDOM,  Goal.PURSUE,    Goal.ALL,   5.0,        Tasking.CONTROL_CLOSEST,Behavior.PURSUIT_LEADING,   Color.BLACK);
        Team mice=new Team(5,   Team.START_RANDOM,  Goal.EVADE,     Goal.ALL,   5.0,        Tasking.AUTO_GRADIENT,  Behavior.FLEE,              Color.GREEN);
        dogs.setString("Seals");        dogs.setTarget(cats);   teams.add(dogs);
        cats.setString("Penguins");     cats.setTarget(mice);   teams.add(cats);
        mice.setString("Fish");         mice.setTarget(cats);   teams.add(mice);
        return teams;
    }
    
    public static ArrayList<Team> twoPlusGoalSimulation(){
        ArrayList<Team> teams=new ArrayList<Team>();
        
        //                 #    STARTING POS        GOAL TYPE       ONE/ALL     CAP.DIST.   TASK ALGORITHM          BEHAVIOR ALGORITHM          COLOR
        Team dogs=new Team(3,   Team.START_RANDOM,  Goal.PURSUE,    Goal.ALL,   5.0,        Tasking.CONTROL_CLOSEST,Behavior.PURSUIT_LEADING,   Color.ORANGE);
        Team cats=new Team(4,   Team.START_RANDOM,  Goal.PURSUE,    Goal.ALL,   5.0,        Tasking.AUTO_CLOSEST,   Behavior.SEEK,              Color.GRAY);
        Team milk=new Team(1,   Team.START_RANDOM,  Goal.NONE,      Goal.ALL,   5.0,        Tasking.NO_TASKING,     Behavior.STATIONARY,        Color.BLUE);
        dogs.setString("Lions");        dogs.setTarget(cats);   teams.add(dogs);
        cats.setString("Wildebeest");   cats.setTarget(milk);   teams.add(cats);
        milk.setString("Watering Hole");                        teams.add(milk);
        return teams;
    }
    
    public static ArrayList<Team> leadFactorSimulation(){
        ArrayList<Team> teams=new ArrayList<Team>();
        
        //                 #    STARTING POS        GOAL TYPE       ONE/ALL     CAP.DIST.   TASK ALGORITHM          BEHAVIOR ALGORITHM          COLOR
        Team dogs=new Team(11,  Team.START_ZERO,    Goal.PURSUE,    Goal.ALL,   5.0,        Tasking.AUTO_CLOSEST,   Behavior.PURSUIT_LEADING,   Color.DARK_GRAY);
        Team cats=new Team(1,   Team.START_RANDOM,  Goal.EVADE,     Goal.ALL,   5.0,        Tasking.AUTO_CLOSEST,   Behavior.FIXEDPATH,         Color.GREEN);
        cats.get(0).setFixedPath("20cos(t/4)","20sin(t/2)");
        for(int i=0;i<dogs.size();i++){
            dogs.get(i).setColor(new Color(100+15*i,25*i,25*i));
            dogs.get(i).setLeadFactor(i/10.0);
        }
        dogs.get(0).setColor(Color.DARK_GRAY);
        dogs.get(dogs.size()-1).setColor(new Color(100,100,250));
        dogs.setString("Velociraptors");dogs.setTarget(cats);   teams.add(dogs);
        cats.setString("Mathematicians");cats.setTarget(dogs);   teams.add(cats);
        return teams;
    }
    
    public static ArrayList<Team> bigSimulation(){
        ArrayList<Team> teams=new ArrayList<Team>();
        //            #     STARTING POS        GOAL TYPE       ONE/ALL     CAP.DIST.   TASK ALGORITHM          BEHAVIOR ALGORITHM          COLOR
        teams.add(new Team(1,    Team.START_RANDOM,  Goal.PURSUE,    Goal.ALL,   5.0,        Tasking.CONTROL_CLOSEST,Behavior.PURSUIT_LEADING,   Color.RED));
        teams.add(new Team(4,    Team.START_RANDOM,  Goal.PURSUE,    Goal.ALL,   5.0,        Tasking.CONTROL_CLOSEST,Behavior.PURSUIT_LEADING,   Color.ORANGE));
        teams.add(new Team(3,    Team.START_LINE,    Goal.PURSUE,    Goal.ONE,   5.0,        Tasking.CONTROL_CLOSEST,Behavior.PURSUIT_LEADING,   Color.YELLOW));
        teams.add(new Team(5,    Team.START_ARC,     Goal.PURSUE,    Goal.ALL,   5.0,        Tasking.CONTROL_CLOSEST,Behavior.PURSUIT_LEADING,   Color.GREEN));
        teams.add(new Team(6,    Team.START_RANDOM,  Goal.PURSUE,    Goal.ALL,   5.0,        Tasking.CONTROL_CLOSEST,Behavior.PURSUIT_LEADING,   Color.CYAN));
        teams.add(new Team(3,    Team.START_CIRCLE,  Goal.PURSUE,    Goal.ONE,   5.0,        Tasking.CONTROL_CLOSEST,Behavior.PURSUIT_LEADING,   Color.BLUE));
        teams.add(new Team(2,    Team.START_RANDOM,  Goal.PURSUE,    Goal.ALL,   5.0,        Tasking.CONTROL_CLOSEST,Behavior.PURSUIT_LEADING,   Color.MAGENTA));
        teams.add(new Team(4,    Team.START_CIRCLE,  Goal.PURSUE,    Goal.ONE,   5.0,        Tasking.AUTO_CLOSEST,   Behavior.SEEK,              Color.PINK));
        teams.add(new Team(2,    Team.START_RANDOM,  Goal.PURSUE,    Goal.ALL,   5.0,        Tasking.AUTO_CLOSEST,   Behavior.SEEK,              Color.GRAY));
        teams.add(new Team(2,    Team.START_RANDOM,  Goal.NONE,      Goal.ONE,   5.0,        Tasking.NO_TASKING,     Behavior.STATIONARY,        Color.BLACK));
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
        for(int i=0;i<9;i++){teams.get(i).setTarget(teams.get(i+1));}
        return teams;
    }
}
