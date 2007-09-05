/*
 * Simulation.java
 * 
 * Created on Aug 28, 2007, 10:29:14 AM
 * 
 * Author: Elisha Peterson
 * 
 * This class runs a single simulation with given settings.
 */

package pursuitevasion;

import agent.Agent;
import pursuitevasion.Pitch;
import agent.Team;
import java.util.ArrayList;
import utility.DistanceTable;

/**
 *
 * @author ae3263
 */
public class Simulation {
    

// PROPERTIES   
    
    /** Contains all settings used to run the simulation. */
    SimulationSettings ss;
    /** Has complete/precise knowledge of playing field. */
    Agent bird;
    /** The exact playing field. */
    Pitch pitch;    
    /** Contains list of teams involved. */
    ArrayList<Team> teams;
    /** Table of distances (for speed of simulation) */
    DistanceTable dist;
    /** The time stamp when iterating */
    double time;

    
// CONSTRUCTORS    
    
    /** Standard constructor */
    public Simulation(){this(SimulationSettings.SIMPLE_PE);}
    /** Constructs given a type of game 
     * @param gameType the type of game to simulate */
    public Simulation(int gameType){initialize(new SimulationSettings(gameType));}
    /** Constructs given a settings class 
     * @param ss a collection of simulation settings */
    public Simulation(SimulationSettings ss){initialize(ss);}

    
// METHODS: INITIALIZERS    
    
    /** Performs initial setup 
     * @param ss initial settings */
    public void initialize(SimulationSettings ss){
        this.ss=ss;
        pitch=this.ss.getPitch();
        teams=this.ss.getTeams();
        bird=new Agent(pitch);
        dist=new DistanceTable(teams);
        time=0;
    }
    
    
// METHODS: DEPLOY SIMULATION
    
    /** Tells the simulation to get going! 
     * @param numSteps  how many time steps to run the simulation   
     * @return          to be determined...
     */
    public int run(int numSteps){
        for(int i=0;i<numSteps;i++){iterate();}
        return 0;
    }
    
    /** Runs a single iteration of the scenario */
    public void iterate(){
        // Begin by collecting data, communicating it to team members, and forming beliefs about playing field.
        dist.recalculate();
        for(Team t:teams){t.gatherSensoryData(dist);}
        for(Team t:teams){t.communicateSensoryData(dist);}
        for(Team t:teams){t.fuseAgentPOV();}
        
        // Assign tasks to team members, plan corresponding paths, and move
        for(Team t:teams){t.tasking.assign(t,t.ts.getGoal());}
        for(Team t:teams){t.planPaths(time,ss.getStepTime());}
        for(Team t:teams){t.move();}
        
        // Store current positions and check for endstate
        bird.remember();
        time+=ss.getStepTime();
        for(Team t:teams){
            if(t.goalAchieved(dist)){
                System.out.println("Team "+t.toString()+" achieved its goal at time "+time+"!");
            }
        }
    }    
}