/*
 * Settings.java
 * Created on Aug 28, 2007, 10:29:32 AM
 */

package pursuitevasion;

import Model.ComboBoxRangeModel;
import Model.DoubleRangeModel;
import Model.IntegerRangeModel;
import Model.Settings;
import agent.Agent;
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
    private ComboBoxRangeModel gameType=new ComboBoxRangeModel(GAME_STRINGS,SIMPLE_PE,0,6);
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
    public static final String[] GAME_STRINGS={"n-on-1","Two Teams","Dog/Cat/Mouse game","Pursuit/Evasion with Goal","A !LOT! of Teams","Lead Factor Analysis","Custom"};
    
    
    // CONSTRUCTORS & INTIALIZERS
    
    public SimulationSettings(){
        addProperty("Pitch Size",pitchSize,Settings.EDIT_DOUBLE);
        addProperty("Step Time",stepTime,Settings.EDIT_DOUBLE);
        addProperty("# of Steps",numSteps,Settings.EDIT_INTEGER);
        //addProperty("max Steps",maxSteps,Settings.EDIT_INTEGER);
        addProperty("Preset Game",gameType,Settings.EDIT_COMBO);
        addProperty("# of Teams",numTeams,Settings.EDIT_INTEGER);
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
        case SIMPLE_PE:{
            setString("Follow the Light");
            setNumTeams(2);
            Team dogs=new Team(new TeamSettings(4,TeamSettings.START_RANDOM,
                    new Goal(Goal.PURSUE,Goal.ALL,5.0),Tasking.AUTO_CLOSEST,Behavior.PURSUIT_LEADING,
                    Color.DARK_GRAY,this));
            Team cats=new Team(new TeamSettings(1,TeamSettings.START_RANDOM,
                    new Goal(Goal.EVADE,Goal.ALL,5.0),Tasking.AUTO_GRADIENT,Behavior.FLEE,
                    Color.GREEN,this));
            dogs.ts.getSubSettings().setString("Bugs");dogs.setTarget(cats);teams.add(dogs);
            cats.ts.getSubSettings().setString("Light");cats.setTarget(dogs);teams.add(cats);
            break;
        }
        case TWOTEAM:{
            setString("Cops & Robbers");
            setNumTeams(2);
            Team dogs=new Team(new TeamSettings(5,TeamSettings.START_RANDOM,
                    new Goal(Goal.PURSUE,Goal.ALL,5.0),Tasking.CONTROL_CLOSEST,Behavior.PURSUIT_LEADING,
                    Color.BLUE,this));
            Team cats=new Team(new TeamSettings(4,TeamSettings.START_RANDOM,
                    new Goal(Goal.EVADE,Goal.ALL,5.0),Tasking.AUTO_GRADIENT,Behavior.FLEE,
                    Color.ORANGE,this));
            dogs.ts.getSubSettings().setString("Cops");dogs.setTarget(cats);teams.add(dogs);
            cats.ts.getSubSettings().setString("Robbers");cats.setTarget(dogs);teams.add(cats);
            break;
        }        case SIMPLE_PPE:{
            setString("Antarctica");
            setNumTeams(3);
            Team dogs=new Team(new TeamSettings(3,TeamSettings.START_RANDOM,
                    new Goal(Goal.PURSUE,Goal.ALL,5.0),Tasking.CONTROL_CLOSEST,Behavior.PURSUIT_LEADING,
                    Color.BLUE,this));
            Team cats=new Team(new TeamSettings(4,TeamSettings.START_RANDOM,
                    new Goal(Goal.PURSUE,Goal.ALL,5.0),Tasking.CONTROL_CLOSEST,Behavior.PURSUIT_LEADING,
                    Color.BLACK,this));
            Team mice=new Team(new TeamSettings(5,TeamSettings.START_RANDOM,
                    new Goal(Goal.EVADE,Goal.ALL,5.0),Tasking.AUTO_GRADIENT,Behavior.FLEE,
                    Color.GREEN,this));
            dogs.ts.getSubSettings().setString("Seals");dogs.setTarget(cats);teams.add(dogs);
            cats.ts.getSubSettings().setString("Penguins");cats.setTarget(mice);teams.add(cats);
            mice.ts.getSubSettings().setString("Fish");mice.setTarget(cats);teams.add(mice);
            break;
        }        // Initialize with capture radius 1, two teams plus a goal
        case GOAL_PE:{
            setString("Wildlife");
            setNumTeams(3);
            Team dogs=new Team(new TeamSettings(3,TeamSettings.START_RANDOM,
                    new Goal(Goal.PURSUE,Goal.ALL,5.0),Tasking.CONTROL_CLOSEST,Behavior.PURSUIT_LEADING,
                    Color.ORANGE,this));
            Team cats=new Team(new TeamSettings(4,TeamSettings.START_RANDOM,
                    new Goal(Goal.PURSUE,Goal.ALL,5.0),Tasking.AUTO_CLOSEST,Behavior.SEEK,
                    Color.GRAY,this));
            Team milk=new Team(new TeamSettings(1,TeamSettings.START_RANDOM,
                    new Goal(Goal.NONE,Goal.ALL,5.0),Tasking.NO_TASKING,Behavior.STATIONARY,
                    Color.BLUE,this));
            dogs.ts.getSubSettings().setString("Lions");dogs.setTarget(cats);teams.add(dogs);
            cats.ts.getSubSettings().setString("Wildebeest");cats.setTarget(milk);teams.add(cats);
            milk.ts.getSubSettings().setString("Watering Hole");teams.add(milk);
            break;
        }
        // Initialize with many, many teams
        case LOTS_OF_FUN:{
            setString("Swallowed");
            setNumTeams(3);
            Team[] t=new Team[10];
            t[1]=new Team(new TeamSettings(2,TeamSettings.START_RANDOM,new Goal(Goal.PURSUE,Goal.ALL,5.0),Tasking.CONTROL_CLOSEST,Behavior.PURSUIT_LEADING,Color.RED,this));
            t[2]=new Team(new TeamSettings(4,TeamSettings.START_RANDOM,new Goal(Goal.PURSUE,Goal.ALL,5.0),Tasking.CONTROL_CLOSEST,Behavior.PURSUIT_LEADING,Color.ORANGE,this));
            t[3]=new Team(new TeamSettings(3,TeamSettings.START_LINE,new Goal(Goal.PURSUE,Goal.ONE,5.0),Tasking.CONTROL_CLOSEST,Behavior.PURSUIT_LEADING,Color.YELLOW,this));
            t[4]=new Team(new TeamSettings(5,TeamSettings.START_ARC,new Goal(Goal.PURSUE,Goal.ALL,5.0),Tasking.CONTROL_CLOSEST,Behavior.PURSUIT_LEADING,Color.GREEN,this));
            t[5]=new Team(new TeamSettings(6,TeamSettings.START_RANDOM,new Goal(Goal.PURSUE,Goal.ALL,5.0),Tasking.CONTROL_CLOSEST,Behavior.PURSUIT_LEADING,Color.CYAN,this));
            t[6]=new Team(new TeamSettings(3,TeamSettings.START_CIRCLE,new Goal(Goal.PURSUE,Goal.ONE,5.0),Tasking.CONTROL_CLOSEST,Behavior.PURSUIT_LEADING,Color.BLUE,this));
            t[7]=new Team(new TeamSettings(2,TeamSettings.START_RANDOM,new Goal(Goal.PURSUE,Goal.ALL,5.0),Tasking.CONTROL_CLOSEST,Behavior.PURSUIT_LEADING,Color.MAGENTA,this));
            t[8]=new Team(new TeamSettings(4,TeamSettings.START_CIRCLE,new Goal(Goal.PURSUE,Goal.ONE,5.0),Tasking.AUTO_CLOSEST,Behavior.SEEK,Color.PINK,this));
            t[9]=new Team(new TeamSettings(2,TeamSettings.START_RANDOM,new Goal(Goal.PURSUE,Goal.ALL,5.0),Tasking.AUTO_CLOSEST,Behavior.SEEK,Color.GRAY,this));
            t[0]=new Team(new TeamSettings(2,TeamSettings.START_RANDOM,new Goal(Goal.NONE,Goal.ONE,5.0),Tasking.NO_TASKING,Behavior.STATIONARY,Color.BLACK,this));
            for(int i=1;i<9;i++){t[i].setTarget(t[i+1]);teams.add(t[i]);}
            teams.add(t[9]);teams.add(t[0]);
            t[9].setTarget(t[0]);
            t[1].ts.getSubSettings().setString("Velociraptors");
            t[2].ts.getSubSettings().setString("Horses");
            t[3].ts.getSubSettings().setString("Cows");
            t[4].ts.getSubSettings().setString("Goats");
            t[5].ts.getSubSettings().setString("Dogs");
            t[6].ts.getSubSettings().setString("Cats");
            t[7].ts.getSubSettings().setString("Birds");
            t[8].ts.getSubSettings().setString("Spiders");
            t[9].ts.getSubSettings().setString("Flies");
            t[0].ts.getSubSettings().setString("Why");
            break;
        }
        case LEAD_FACTOR:{
            setString("Cut him off!");
            setNumTeams(2);
            Team dogs=new Team(new TeamSettings(11,TeamSettings.START_ZERO,
                    new Goal(Goal.PURSUE,Goal.ALL,5.0),Tasking.AUTO_CLOSEST,Behavior.PURSUIT_LEADING,
                    Color.DARK_GRAY,this));
            Team cats=new Team(new TeamSettings(1,TeamSettings.START_RANDOM,
                    new Goal(Goal.EVADE,Goal.ALL,5.0),Tasking.AUTO_CLOSEST,Behavior.FIXEDPATH,
                    Color.GREEN,this));
            cats.get(0).as.setFixedPath("20cos(t/4)","20sin(t/2)");
            for(int i=0;i<dogs.size();i++){
                dogs.get(i).as.setColor(new Color(100+15*i,25*i,25*i));
                dogs.get(i).as.setLeadFactor(i/10.0);
            }
            dogs.get(0).as.setColor(Color.DARK_GRAY);
            dogs.get(dogs.size()-1).as.setColor(new Color(100,100,250));
            dogs.ts.getSubSettings().setString("Cutters");dogs.setTarget(cats);teams.add(dogs);
            cats.ts.getSubSettings().setString("Him");cats.setTarget(dogs);teams.add(cats);
            break;            
        }
        }
        return teams;
    }
    
// EVENT HANDLING

    public void removeAllPropertyChangeListeners(){pcs=new PropertyChangeSupport(this);}
            
} // class SimulationSettings
