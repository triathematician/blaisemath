/*
 * Pitch.java
 * Created on Aug 28, 2007, 10:26:39 AM
 */

package pursuitevasion;

import agent.Agent;
import java.util.ArrayList;

/**
 * @author Elisha Peterson
 * 
 * This file will contain a single player's view of the entire playing field, as a collection
 * of agents, obstacles, and locations. At the moment, it's just a collection of agents.
 */
public class Pitch extends ArrayList<Agent>{

// PROPERTIES
    
    /** All the settings */
    public PitchSettings ps;
    
// CONSTRUCTORS    
    
    /** Default Constructor */
    public Pitch(){super();ps=new PitchSettings();}    
    /** Constructs with given settings
     * @param ps a collection of pitch settings */
    public Pitch(PitchSettings ps){this.ps=ps;}
}
