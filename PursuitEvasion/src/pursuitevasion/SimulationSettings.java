/*
 * Settings.java
 * Created on Aug 28, 2007, 10:29:32 AM
 */

package pursuitevasion;

import Model.DoubleRangeModel;
import Model.IntegerRangeModel;
import Model.SpinnerDoubleEditor;
import Model.SpinnerIntegerEditor;
import agent.Team;
import pursuitevasion.PitchSettings;
import agent.TeamSettings;
import task.Goal;
import behavior.Behavior;
import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import behavior.Tasking;

/**
 * @author Elisha Peterson
 * <br><br>
 * Contains global settings for a pursuit/evasion scenario.
 * One of the most important methods is the getTeams() method, which generates
 * teams based on current settings and the type of game.
 */
public class SimulationSettings implements ChangeListener,PropertyChangeListener {      

// PROPERTIES    
    
    /** Number of teams */
    private IntegerRangeModel numTeams=new IntegerRangeModel(2,1,100);
    /** Type of game involved */
    private IntegerRangeModel gameType=new IntegerRangeModel(0,0,3);
    /** Pitch settings */
    private PitchSettings pitchSettings;
    
    /** Time taken by a single step [in seconds] */
    private DoubleRangeModel stepTime=new DoubleRangeModel(.5,0,15);
    /** Number of steps to run the simulation before quitting. */
    private IntegerRangeModel numSteps=new IntegerRangeModel(50,0,10000);
    /** If stop is based on reaching a goal, this is the max # of steps to allow. */
    private IntegerRangeModel maxSteps=new IntegerRangeModel(1000,0,10000000);
    

// CONSTANTS    
    
    /** Specifies simple game with two teams, pursuers and evaders */
    public static final int SIMPLE_PE=0;
    /** Specifies simple game with three teams, pursuers, pursuers/evaders, and evaders */
    public static final int SIMPLE_PPE=1;
    /** Specifies game with two teams, pursuers, and evaders, with evaders seeking a goal */
    public static final int GOAL_PE=2;
    /** Lots of teams!! */
    public static final int LOTS_OF_FUN=3;
    
    /** Specifies non-stationary & stationary */
    public static final boolean STATIONARY=true;
    public static final boolean MOVING=false;
    
    
// CONSTRUCTORS
    
    /** Default constructor */
    SimulationSettings(){
        super();
        pcs=new PropertyChangeSupport(this);
        pitchSettings=new PitchSettings(this);
        numTeams.addChangeListener(this);
        gameType.addChangeListener(this);
        stepTime.addChangeListener(this);
        numSteps.addChangeListener(this);
        maxSteps.addChangeListener(this);        
    }
    /** Sets up the specified game 
     * @param gameType an integer specifying the type of game */
    SimulationSettings(int gameType){
        this();
        setGameType(gameType);
    }
     
    
// GETTERS & SETTERS     

    public int getNumTeams(){return numTeams.getValue();}
    public int getGameType(){return gameType.getValue();}
    public double getStepTime(){return stepTime.getValue();}
    public int getNumSteps(){return numSteps.getValue();}
    public int getMaxSteps(){return maxSteps.getValue();}
    
    public void setNumTeams(int newValue){numTeams.setValue(newValue);}
    public void setGameType(int newValue){gameType.setValue(newValue);}
    public void setStepTime(double newValue){stepTime.setValue(newValue);}
    public void setNumSteps(int newValue){numSteps.setValue(newValue);}
    public void setMaxSteps(int newValue){maxSteps.setValue(newValue);}
    
    public void setSimTimes(double st,int ns,int ms){setStepTime(st);setNumSteps(ns);setMaxSteps(ms);}

    
// EVENT HANDLING ROUTINES
    
    /** Utility class for handling bean property changes. */
    protected PropertyChangeSupport pcs;
    /**Add a property change listener for a specific property.
     * @param l the listener */
    public void addPropertyChangeListener(PropertyChangeListener l){pcs.addPropertyChangeListener(l);}
    /**Remove a property change listener for a specific property.
     * @param l the listener */
    public void removePropertyChangeListener(PropertyChangeListener l){pcs.removePropertyChangeListener(l);}    
    /** Handles change events by firing change events */
    public void stateChanged(ChangeEvent e) {
        if(e.getSource()==numTeams){pcs.firePropertyChange("simNumTeams",null,numTeams.getValue());}
        if(e.getSource()==gameType){pcs.firePropertyChange("simGameType",null,gameType.getValue());}
        if(e.getSource()==stepTime){pcs.firePropertyChange("simStepTime",null,stepTime.getValue());}
        if(e.getSource()==numSteps){pcs.firePropertyChange("simNumSteps",null,numSteps.getValue());}
        if(e.getSource()==maxSteps){pcs.firePropertyChange("simMaxSteps",null,maxSteps.getValue());}
    }
    /** Passes on property changes from more specialized settings */
    public void propertyChange(PropertyChangeEvent evt){pcs.firePropertyChange(evt);}


// GUI ELEMENT GENERATING ROUTINES
// The routines below generate models for use with gui elements... these models also include step sizes for spinners */
    
    public SpinnerIntegerEditor getNumTeamsSpinnerModel(){return new SpinnerIntegerEditor(numTeams,1);}
    public SpinnerIntegerEditor getGameTypeSpinnerModel(){return new SpinnerIntegerEditor(gameType,1);}
    public SpinnerDoubleEditor getStepTimeSpinnerModel(){return new SpinnerDoubleEditor(stepTime,.05);}
    public SpinnerIntegerEditor getNumStepsSpinnerModel(){return new SpinnerIntegerEditor(numSteps,1);}
    public SpinnerIntegerEditor getMaxStepsSpinnerModel(){return new SpinnerIntegerEditor(maxSteps,1);}


// METHODS TO GENERATE CLASSES WITH SIMULATION'S SETTINGS
    
    /** Generates a simulation class based on these settings. 
     * @return simulation class with these settings */
    public Simulation getSimulation(){return new Simulation(this);}
    
    /** Generates a pitch based on pitchSettings
     * @return the pitch specified by these settings */
    public Pitch getPitch(){return pitchSettings.getPitch();}
    
    /** Generates default teams based on current settings *
     * @return list of teams specified by these settings */
    public ArrayList<Team> getTeams(){
        ArrayList<Team> teams=new ArrayList<Team>();
        switch(gameType.getValue()){
        // Initialize with capture radius 1, two teams
                // Initialize with capture radius 1, two teams
        case SIMPLE_PE:{
            setNumTeams(2);
            Team dogs=new Team(new TeamSettings(3,TeamSettings.START_RANDOM,
                    new Goal(Goal.CAPTURE_ALL,5.0),Tasking.CONTROL_CLOSEST,Behavior.PURSUIT_LEADING,
                    MOVING,Color.RED,this));
            Team cats=new Team(new TeamSettings(2,TeamSettings.START_RANDOM,new Goal(Goal.ALL_ESCAPE,5.0),Tasking.AUTO_CLOSEST,Behavior.FLEE,MOVING,
                    Color.BLUE,this));
            dogs.setTarget(cats);teams.add(dogs);
            cats.setTarget(dogs);teams.add(cats);
            break;
        }        // Initialize with capture radius 1, three teams
        case SIMPLE_PPE:{
            setNumTeams(3);
            Team dogs=new Team(new TeamSettings(3,TeamSettings.START_RANDOM,
                    new Goal(Goal.CAPTURE_ALL,5.0),Tasking.CONTROL_CLOSEST,Behavior.PURSUIT_LEADING,
                    MOVING,Color.RED,this));
            Team cats=new Team(new TeamSettings(4,TeamSettings.START_RANDOM,
                    new Goal(Goal.CAPTURE_ALL,5.0),Tasking.CONTROL_CLOSEST,Behavior.PURSUIT_LEADING,
                    MOVING,Color.BLUE,this));
            Team mice=new Team(new TeamSettings(5,TeamSettings.START_RANDOM,new Goal(Goal.ALL_ESCAPE,5.0),Tasking.AUTO_CLOSEST,Behavior.FLEE,MOVING,
                    Color.GREEN,this));
            dogs.ts.getGoal().setTarget(cats);teams.add(dogs);
            cats.ts.getGoal().setTarget(mice);teams.add(cats);
            mice.ts.getGoal().setTarget(cats);teams.add(mice);
            break;
        }        // Initialize with capture radius 1, two teams plus a goal
        case GOAL_PE:{
            setNumTeams(3);
            Team dogs=new Team(new TeamSettings(3,TeamSettings.START_RANDOM,
                    new Goal(Goal.CAPTURE_ALL,5.0),Tasking.CONTROL_CLOSEST,Behavior.PURSUIT_LEADING,
                    MOVING,Color.RED,this));
            Team cats=new Team(new TeamSettings(4,TeamSettings.START_RANDOM,
                    new Goal(Goal.CAPTURE_ALL,5.0),Tasking.AUTO_CLOSEST,Behavior.SEEK,
                    MOVING,Color.BLUE,this));
            Team milk=new Team(new TeamSettings(1,TeamSettings.START_RANDOM,
                    new Goal(Goal.TRIVIAL,5.0),Tasking.NO_TASKING,Behavior.FLEE,
                    STATIONARY,Color.GREEN,this));
            dogs.setTarget(cats);teams.add(dogs);
            cats.setTarget(milk);teams.add(cats);
            teams.add(milk);
            break;
        }
        // Initialize with many, many teams
        case LOTS_OF_FUN:{
            setNumTeams(3);
            Team[] t=new Team[10];
            t[0]=new Team(new TeamSettings(2,TeamSettings.START_RANDOM,new Goal(Goal.TRIVIAL,5.0),Tasking.NO_TASKING,Behavior.FLEE,STATIONARY,Color.BLACK,this));
            t[1]=new Team(new TeamSettings(2,TeamSettings.START_RANDOM,new Goal(Goal.CAPTURE_ALL,5.0),Tasking.CONTROL_CLOSEST,Behavior.PURSUIT_LEADING,MOVING,Color.RED,this));
            t[2]=new Team(new TeamSettings(4,TeamSettings.START_RANDOM,new Goal(Goal.CAPTURE_ALL,5.0),Tasking.CONTROL_CLOSEST,Behavior.PURSUIT_LEADING,MOVING,Color.ORANGE,this));
            t[3]=new Team(new TeamSettings(3,TeamSettings.START_LINE,new Goal(Goal.CAPTURE_ONE,5.0),Tasking.CONTROL_CLOSEST,Behavior.PURSUIT_LEADING,MOVING,Color.YELLOW,this));
            t[4]=new Team(new TeamSettings(5,TeamSettings.START_ARC,new Goal(Goal.CAPTURE_ALL,5.0),Tasking.CONTROL_CLOSEST,Behavior.PURSUIT_LEADING,MOVING,Color.GREEN,this));
            t[5]=new Team(new TeamSettings(6,TeamSettings.START_RANDOM,new Goal(Goal.CAPTURE_ALL,5.0),Tasking.CONTROL_CLOSEST,Behavior.PURSUIT_LEADING,MOVING,Color.CYAN,this));
            t[6]=new Team(new TeamSettings(3,TeamSettings.START_CIRCLE,new Goal(Goal.CAPTURE_ONE,5.0),Tasking.CONTROL_CLOSEST,Behavior.PURSUIT_LEADING,MOVING,Color.BLUE,this));
            t[7]=new Team(new TeamSettings(2,TeamSettings.START_RANDOM,new Goal(Goal.CAPTURE_ALL,5.0),Tasking.CONTROL_CLOSEST,Behavior.PURSUIT_LEADING,MOVING,Color.MAGENTA,this));
            t[8]=new Team(new TeamSettings(4,TeamSettings.START_CIRCLE,new Goal(Goal.CAPTURE_ONE,5.0),Tasking.AUTO_CLOSEST,Behavior.SEEK,MOVING,Color.PINK,this));
            t[9]=new Team(new TeamSettings(2,TeamSettings.START_RANDOM,new Goal(Goal.CAPTURE_ALL,5.0),Tasking.AUTO_CLOSEST,Behavior.SEEK,MOVING,Color.GRAY,this));
            teams.add(t[0]);
            for(int i=1;i<9;i++){t[i].setTarget(t[i+1]);teams.add(t[i]);}
            t[9].setTarget(t[0]);teams.add(t[9]);
            break;
        }
        }
        return teams;
    }
} // class SimulationSettings
