/*
 * Settings.java
 * Created on Aug 28, 2007, 10:29:32 AM
 */

package pursuitevasion;

import Model.ComboBoxRangeModel;
import Model.DoubleRangeModel;
import Model.IntegerRangeModel;
import Model.Settings;
import agent.Team;
import agent.TeamSettings;
import task.Goal;
import behavior.Behavior;
import java.awt.Color;
import java.util.ArrayList;
import javax.swing.event.ChangeEvent;
import behavior.Tasking;
import java.beans.PropertyChangeSupport;

/**
 * Contains global settings for a pursuit/evasion scenario.
 * One of the most important methods is the getTeams() method, which generates
 * teams based on current settings and the type of game.
 * <br><br>
 * @author Elisha Peterson
 */
public class SimulationSettings extends Settings {
    
    // PROPERTIES
    
    /** Number of teams */
    private IntegerRangeModel numTeams=new IntegerRangeModel(2,1,100);
    /** Type of game involved */
    private ComboBoxRangeModel gameType=new ComboBoxRangeModel(GAME_STRINGS,SIMPLE_PE,0,4);
    /** Pitch size */
    private DoubleRangeModel pitchSize=new DoubleRangeModel(60,0,50000,1);
    
    /** Time taken by a single step [in seconds] */
    private DoubleRangeModel stepTime=new DoubleRangeModel(.1,0,15,.01);
    /** Number of steps to run the simulation before quitting. */
    private IntegerRangeModel numSteps=new IntegerRangeModel(100,0,10000);
    /** If stop is based on reaching a goal, this is the max # of steps to allow. */
    private IntegerRangeModel maxSteps=new IntegerRangeModel(1000,0,10000000);
    
    /** Display string */
    private String s="PEG Simulation";
    
    // CONSTANTS
    
    /** Specifies simple game with two teams, pursuers and evaders */
    public static final int SIMPLE_PE=0;
    /** Specifies simple game with three teams, pursuers, pursuers/evaders, and evaders */
    public static final int SIMPLE_PPE=1;
    /** Specifies game with two teams, pursuers, and evaders, with evaders seeking a goal */
    public static final int GOAL_PE=2;
    /** Lots of teams!! */
    public static final int LOTS_OF_FUN=3;
    /** Custom */
    public static final int CUSTOM=4;
    public static final String[] GAME_STRINGS={"Simple Pursuit/Evasion","Dog/Cat/Mouse game","Pursuit/Evasion with Goal","A !LOT! of Teams","Custom"};
    
    
    // CONSTRUCTORS & INTIALIZERS
    
    public SimulationSettings(){
        addProperty("# of Teams",numTeams,Settings.EDIT_INTEGER);
        addProperty("Pitch Size",pitchSize,Settings.EDIT_DOUBLE);
        addProperty("Step Time",stepTime,Settings.EDIT_DOUBLE);
        addProperty("# of Steps",numSteps,Settings.EDIT_INTEGER);
        addProperty("max Steps",maxSteps,Settings.EDIT_INTEGER);
        addProperty("Preset Game",gameType,Settings.EDIT_COMBO);
        initEventListening();
    }
    public SimulationSettings(int gameType){this();setGameType(gameType);}
    
    public void setSimTimes(double st,int ns,int ms){setStepTime(st);setNumSteps(ns);setMaxSteps(ms);}
    
    
    // GETTERS & SETTERS
    
    public int getNumTeams(){return numTeams.getValue();}
    public int getGameType(){return gameType.getValue();}
    public double getPitchSize(){return pitchSize.getValue();}
    public double getStepTime(){return stepTime.getValue();}
    public int getNumSteps(){return numSteps.getValue();}
    public int getMaxSteps(){return maxSteps.getValue();}
    public String toString(){return s;}
    
    public void setNumTeams(int newValue){numTeams.setValue(newValue);}
    public void setGameType(int newValue){gameType.setValue(newValue);}
    public void setPitchType(double newValue){pitchSize.setValue(newValue);}
    public void setStepTime(double newValue){stepTime.setValue(newValue);}
    public void setNumSteps(int newValue){numSteps.setValue(newValue);}
    public void setMaxSteps(int newValue){maxSteps.setValue(newValue);}
    public void setString(String newValue){s=newValue;}
    
   
    // METHODS TO GENERATE CLASSES WITH SIMULATION'S SETTINGS
    
    /** Generates a simulation class based on these settings.
     * @return simulation class with these settings */
    public Simulation getSimulation(){return new Simulation(this);}
    
    /** Generates default teams based on current settings *
     * @return list of teams specified by these settings */
    public ArrayList<Team> getTeams(){
        ArrayList<Team> teams=new ArrayList<Team>();
        switch(gameType.getValue()){
            // Initialize with capture radius 1, two teams
            // Initialize with capture radius 1, two teams
            // Initialize with capture radius 1, two teams
            // Initialize with capture radius 1, two teams
        case SIMPLE_PE:{
            setString("1-on-1");
            setNumTeams(2);
            Team dogs=new Team(new TeamSettings(3,TeamSettings.START_RANDOM,
                    new Goal(Goal.PURSUE,Goal.ALL,5.0),Tasking.CONTROL_CLOSEST,Behavior.PURSUIT_LEADING,
                    Color.RED,this));
            Team cats=new Team(new TeamSettings(2,TeamSettings.START_RANDOM,
                    new Goal(Goal.EVADE,Goal.ALL,5.0),Tasking.AUTO_CLOSEST,Behavior.FLEE,
                    Color.BLUE,this));
            dogs.ts.getSubSettings().setString("Dogs");dogs.setTarget(cats);teams.add(dogs);
            dogs.ts.getSubSettings().setString("Cats");cats.setTarget(dogs);teams.add(cats);
            break;
        }        case SIMPLE_PPE:{
            setString("1-on-1-on-1");
            setNumTeams(3);
            Team dogs=new Team(new TeamSettings(3,TeamSettings.START_RANDOM,
                    new Goal(Goal.PURSUE,Goal.ALL,5.0),Tasking.CONTROL_CLOSEST,Behavior.PURSUIT_LEADING,
                    Color.RED,this));
            Team cats=new Team(new TeamSettings(4,TeamSettings.START_RANDOM,
                    new Goal(Goal.PURSUE,Goal.ALL,5.0),Tasking.CONTROL_CLOSEST,Behavior.PURSUIT_LEADING,
                    Color.BLUE,this));
            Team mice=new Team(new TeamSettings(5,TeamSettings.START_RANDOM,
                    new Goal(Goal.EVADE,Goal.ALL,5.0),Tasking.AUTO_CLOSEST,Behavior.FLEE,
                    Color.GREEN,this));
            dogs.ts.getSubSettings().setString("Dogs");dogs.setTarget(cats);teams.add(dogs);
            cats.ts.getSubSettings().setString("Cats");cats.setTarget(mice);teams.add(cats);
            mice.ts.getSubSettings().setString("Mice");mice.setTarget(cats);teams.add(mice);
            break;
        }        // Initialize with capture radius 1, two teams plus a goal
        case GOAL_PE:{
            setString("1-on-1-Goal");
            setNumTeams(3);
            Team dogs=new Team(new TeamSettings(3,TeamSettings.START_RANDOM,
                    new Goal(Goal.PURSUE,Goal.ALL,5.0),Tasking.CONTROL_CLOSEST,Behavior.PURSUIT_LEADING,
                    Color.RED,this));
            Team cats=new Team(new TeamSettings(4,TeamSettings.START_RANDOM,
                    new Goal(Goal.PURSUE,Goal.ALL,5.0),Tasking.AUTO_CLOSEST,Behavior.SEEK,
                    Color.BLUE,this));
            Team milk=new Team(new TeamSettings(1,TeamSettings.START_RANDOM,
                    new Goal(Goal.NONE,Goal.ALL,5.0),Tasking.NO_TASKING,Behavior.STATIONARY,
                    Color.GREEN,this));
            dogs.ts.getSubSettings().setString("Dogs");dogs.setTarget(cats);teams.add(dogs);
            cats.ts.getSubSettings().setString("Cats");cats.setTarget(milk);teams.add(cats);
            milk.ts.getSubSettings().setString("Milk (Goal)");teams.add(milk);
            break;
        }
        // Initialize with many, many teams
        case LOTS_OF_FUN:{
            setString("Craziness!");
            setNumTeams(3);
            Team[] t=new Team[10];
            t[0]=new Team(new TeamSettings(2,TeamSettings.START_RANDOM,new Goal(Goal.NONE,Goal.ONE,5.0),Tasking.NO_TASKING,Behavior.STATIONARY,Color.BLACK,this));
            t[0].ts.getSubSettings().setString("Goal");
            t[1]=new Team(new TeamSettings(2,TeamSettings.START_RANDOM,new Goal(Goal.PURSUE,Goal.ALL,5.0),Tasking.CONTROL_CLOSEST,Behavior.PURSUIT_LEADING,Color.RED,this));
            t[2]=new Team(new TeamSettings(4,TeamSettings.START_RANDOM,new Goal(Goal.PURSUE,Goal.ALL,5.0),Tasking.CONTROL_CLOSEST,Behavior.PURSUIT_LEADING,Color.ORANGE,this));
            t[3]=new Team(new TeamSettings(3,TeamSettings.START_LINE,new Goal(Goal.PURSUE,Goal.ONE,5.0),Tasking.CONTROL_CLOSEST,Behavior.PURSUIT_LEADING,Color.YELLOW,this));
            t[4]=new Team(new TeamSettings(5,TeamSettings.START_ARC,new Goal(Goal.PURSUE,Goal.ALL,5.0),Tasking.CONTROL_CLOSEST,Behavior.PURSUIT_LEADING,Color.GREEN,this));
            t[5]=new Team(new TeamSettings(6,TeamSettings.START_RANDOM,new Goal(Goal.PURSUE,Goal.ALL,5.0),Tasking.CONTROL_CLOSEST,Behavior.PURSUIT_LEADING,Color.CYAN,this));
            t[6]=new Team(new TeamSettings(3,TeamSettings.START_CIRCLE,new Goal(Goal.PURSUE,Goal.ONE,5.0),Tasking.CONTROL_CLOSEST,Behavior.PURSUIT_LEADING,Color.BLUE,this));
            t[7]=new Team(new TeamSettings(2,TeamSettings.START_RANDOM,new Goal(Goal.PURSUE,Goal.ALL,5.0),Tasking.CONTROL_CLOSEST,Behavior.PURSUIT_LEADING,Color.MAGENTA,this));
            t[8]=new Team(new TeamSettings(4,TeamSettings.START_CIRCLE,new Goal(Goal.PURSUE,Goal.ONE,5.0),Tasking.AUTO_CLOSEST,Behavior.SEEK,Color.PINK,this));
            t[9]=new Team(new TeamSettings(2,TeamSettings.START_RANDOM,new Goal(Goal.PURSUE,Goal.ALL,5.0),Tasking.AUTO_CLOSEST,Behavior.SEEK,Color.GRAY,this));
            teams.add(t[0]);
            for(int i=1;i<9;i++){t[i].setTarget(t[i+1]);teams.add(t[i]);}
            t[9].setTarget(t[0]);teams.add(t[9]);
            break;
        }
        }
        return teams;
    }
    
// EVENT HANDLING

    public void removeAllPropertyChangeListeners(){pcs=new PropertyChangeSupport(this);}
            
} // class SimulationSettings
