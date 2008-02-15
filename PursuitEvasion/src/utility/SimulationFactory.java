/*
 * SimulationFactory.java
 * Created on Sep 18, 2007, 10:00:49 AM
 */

package utility;

import simulation.Team;
import behavior.Behavior;
import tasking.Tasking;
import java.awt.Color;
import java.util.Vector;
import simulation.Simulation;
import goal.Goal;
import sequor.model.ComboBoxRangeModel;

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
        case SIMPLE_PE:     sim.setString("Follow the Light");  sim.setNumTeams(2);     sim.initTeams(lightSimulation());           sim.setPrimary(1);  break;
        case TWOTEAM:       sim.setString("Cops & Robbers");    sim.setNumTeams(2);     sim.initTeams(twoTeamSimulation());         sim.setPrimary(1);  break;
        case SIMPLE_PPE:    sim.setString("Antarctica");        sim.setNumTeams(3);     sim.initTeams(threeTeamSimulation());       sim.setPrimary(1);  break;
        case GOAL_PE:       sim.setString("Sahara");            sim.setNumTeams(3);     sim.initTeams(twoPlusGoalSimulation());     sim.setPrimary(0);  break;
        case LOTS_OF_FUN:   sim.setString("Swallowed");         sim.setNumTeams(10);    sim.initTeams(bigSimulation());             sim.setPrimary(0);  break;
        case LEAD_FACTOR:   sim.setString("Jurassic");          sim.setNumTeams(2);     sim.initTeams(leadFactorSimulation());      sim.setPrimary(0);  break;
        case CUSTOM:        System.out.println("custom operation not supported!");;
        }
    }
    
    public static Vector<Team> lightSimulation(){
        Vector<Team> teams=new Vector<Team>();
        //                      #    STARTING POS        BEHAVIOR ALGORITHM          COLOR
        Team bugTeam=new Team(  4,   Team.START_RANDOM,  Behavior.PURSUIT_LEADING,   Color.DARK_GRAY);
        Team lightTeam=new Team(1,   Team.START_RANDOM,  Behavior.FLEE,              Color.GREEN);
        bugTeam.addGoal(  1.0,lightTeam,Goal.SEEK,  Tasking.AUTO_CLOSEST,1.0);
        lightTeam.addGoal(1.0,bugTeam,  Goal.FLEE,  Tasking.AUTO_GRADIENT, 1.0);
        bugTeam.setString("Bugs");         
        lightTeam.setString("Light");        
        teams.add(bugTeam);
        teams.add(lightTeam);
        return teams;
    }
    
    public static Vector<Team> twoTeamSimulation(){
        Vector<Team> teams=new Vector<Team>();        
        //                          #    STARTING POS        BEHAVIOR ALGORITHM          COLOR
        Team copTeam=new Team(      5,   Team.START_RANDOM,  Behavior.PURSUIT_LEADING,   Color.BLUE);
        Team robberTeam=new Team(   4,   Team.START_RANDOM,  Behavior.FLEE,              Color.ORANGE);
        robberTeam.addGoal(1.0, copTeam,   Goal.CAPTURE,  Tasking.AUTO_GRADIENT,1.0);
        copTeam.addGoal(   1.0, robberTeam,Goal.FLEE,  Tasking.CONTROL_CLOSEST, 1.0);
        copTeam.setString("Cops");
        robberTeam.setString("Robbers");
        teams.add(copTeam);
        teams.add(robberTeam);
        return teams;
    }
    
    public static Vector<Team> threeTeamSimulation(){
        Vector<Team> teams=new Vector<Team>();
        //                      #    STARTING POS        BEHAVIOR ALGORITHM          COLOR
        Team dogTeam=new Team(  3,   Team.START_RANDOM,  Behavior.PURSUIT_LEADING,   Color.BLUE);
        Team catTeam=new Team(  4,   Team.START_RANDOM,  Behavior.PURSUIT_LEADING,   Color.BLACK);
        Team mouseTeam=new Team(5,   Team.START_RANDOM,  Behavior.FLEE,              Color.GREEN);
        dogTeam.addGoal(  1.0,catTeam,  Goal.CAPTURE,Tasking.CONTROL_CLOSEST,1.0);
        catTeam.addGoal(  0.5,dogTeam,  Goal.FLEE,Tasking.AUTO_GRADIENT,  1.0);
        catTeam.addGoal(  1.0,mouseTeam,Goal.CAPTURE,Tasking.CONTROL_CLOSEST,1.0);
        mouseTeam.addGoal(1.0,catTeam,  Goal.FLEE,Tasking.AUTO_GRADIENT,  1.0);
        dogTeam.setString("Seals");
        catTeam.setString("Penguins");
        mouseTeam.setString("Fish");
        teams.add(dogTeam);
        teams.add(catTeam);
        teams.add(mouseTeam);
        return teams;
    }
    
    public static Vector<Team> twoPlusGoalSimulation(){
        Vector<Team> teams=new Vector<Team>();        
        //                      #    STARTING POS        BEHAVIOR ALGORITHM          COLOR
        Team dogTeam=new Team(  3,   Team.START_RANDOM,  Behavior.PURSUIT_LEADING,   Color.ORANGE);
        Team catTeam=new Team(  4,   Team.START_RANDOM,  Behavior.SEEK,              Color.GRAY);
        Team milk=new Team(     1,   Team.START_RANDOM,  Behavior.STATIONARY,        Color.BLUE);
        dogTeam.addGoal(  1.0,catTeam,  Goal.CAPTURE,Tasking.CONTROL_CLOSEST,1.0);
        catTeam.addGoal(  0.5,dogTeam,  Goal.FLEE, Tasking.AUTO_GRADIENT, 1.0);
        catTeam.addGoal(  1.0,milk,     Goal.CAPTURE,Tasking.AUTO_CLOSEST,   1.0);
        dogTeam.setString("Lions");
        catTeam.setString("Wildebeest");
        milk.setString("Watering Hole");
        teams.add(dogTeam);
        teams.add(catTeam);
        teams.add(milk);
        return teams;
    }
    
    public static Vector<Team> leadFactorSimulation(){
        Vector<Team> teams=new Vector<Team>();
        
        //                 #    STARTING POS        BEHAVIOR ALGORITHM          COLOR
        Team dogs=new Team(11,  Team.START_ZERO,    Behavior.PURSUIT_LEADING,   Color.DARK_GRAY);
        Team cats=new Team(1,   Team.START_RANDOM,  Behavior.FIXEDPATH,         Color.GREEN);
        cats.setFixedPath("20cos(t/4)","20sin(t/2)");
        for(int i=0;i<dogs.size();i++){
            dogs.get(i).setColor(new Color(100+15*i,25*i,25*i));
            dogs.get(i).setLeadFactor(i/10.0);
        }
        dogs.get(0).setColor(Color.DARK_GRAY);
        dogs.get(dogs.size()-1).setColor(new Color(100,100,250));
        dogs.addGoal(1.0, cats, Goal.SEEK,Tasking.AUTO_CLOSEST, 1.0);
        dogs.setString("Velociraptors");
        cats.setString("Mathematicians");
        teams.add(dogs);
        teams.add(cats);
        return teams;
    }
    
    public static Vector<Team> bigSimulation(){
        Vector<Team> teams=new Vector<Team>();
        //                  #     STARTING POS        BEHAVIOR ALGORITHM          COLOR
        teams.add(new Team( 1,    Team.START_RANDOM,  Behavior.PURSUIT_LEADING,   Color.RED));
        teams.add(new Team( 4,    Team.START_RANDOM,  Behavior.PURSUIT_LEADING,   Color.ORANGE));
        teams.add(new Team( 3,    Team.START_LINE,    Behavior.PURSUIT_LEADING,   Color.YELLOW));
        teams.add(new Team( 5,    Team.START_ARC,     Behavior.PURSUIT_LEADING,   Color.GREEN));
        teams.add(new Team( 6,    Team.START_RANDOM,  Behavior.PURSUIT_LEADING,   Color.CYAN));
        teams.add(new Team( 3,    Team.START_CIRCLE,  Behavior.PURSUIT_LEADING,   Color.BLUE));
        teams.add(new Team( 2,    Team.START_RANDOM,  Behavior.PURSUIT_LEADING,   Color.MAGENTA));
        teams.add(new Team( 4,    Team.START_CIRCLE,  Behavior.SEEK,              Color.PINK));
        teams.add(new Team( 2,    Team.START_RANDOM,  Behavior.SEEK,              Color.GRAY));
        teams.add(new Team( 2,    Team.START_RANDOM,  Behavior.STATIONARY,        Color.BLACK));
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
        for(int i=0;i<9;i++){
            teams.get(i).addGoal(1.0,teams.get(i+1),Goal.CAPTURE,Tasking.CONTROL_CLOSEST,1.0);
            teams.get(i+1).addGoal(0.5,teams.get(i),Goal.FLEE,Tasking.AUTO_GRADIENT, 1.0);
        }
        return teams;
    }
}
