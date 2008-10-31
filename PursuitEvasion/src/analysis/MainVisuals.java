/**
 * MainVisuals.java
 * Created on Oct 30, 2008
 */

package analysis;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import simulation.Agent;
import simulation.Simulation;
import simulation.Team;
import specto.PlottableGroup;
import specto.euclidean2.CirclePoint2D;
import specto.euclidean2.Euclidean2;
import specto.euclidean2.PointSet2D;

/**
 * This class handles the primary 2D plot window for a simulation. It is written as a bean
 * pattern. Action handling tells the visualization to update if the team or simulations are changed.
 * Needs the SimulationLog to be initialized before this will work.
 * 
 * @author Elisha Peterson
 */
public class MainVisuals extends PlottableGroup<Euclidean2> implements ActionListener {
        
    /** The underlying simulation. */
    Simulation sim;
    
    /** Default constructor (empty plot). */
    public MainVisuals(){}
    
    /** Constructs with given simulation. */
    public MainVisuals(Simulation sim) { setSim(sim); }

    
    // BEAN PATTERNS
    
    /** Returns the simulation. */
    public Simulation getSim() { return sim; }
    
    /** Initializes to a new simulation. */
    public void setSim(Simulation sim) {
        if(sim == null) { return; }
        if(this.sim != null) { this.sim.removeActionListener(this); }
        this.sim = sim;
        sim.addActionListener(this);
        setName(sim.getName());
        clear();
        for (Team t : sim.getTeams()) { add(new TeamVisuals(t)); }
        PointSet2D captures = new PointSet2D(sim.log.capturePoints,Color.RED,PointSet2D.POINTS_ONLY);  
        add(captures);
    }    
    
    // EVENT HANDLING
    
    /** Handles action events from the simulation. */
    @Override
    public void actionPerformed(ActionEvent e) {
        String ac = e.getActionCommand();
        if (ac.equals("reset")) { setSim(sim); }
    }
    
    
    //
    //
    // SUB-CLASSES
    //
    //
    
    /** Stores the visual elements for a particular team. */
    class TeamVisuals extends PlottableGroup<Euclidean2> {        
        
        DynamicTeamGraph dtg;
        
        TeamVisuals(Team t) {
            setTeam(t);
            add(dtg);
            for(Agent a : t.agents){ add(new AgentVisuals(a)); }
        }
        
        public void setTeam(Team t) {
            if (t==null) { return; }
            setColorModel(t.getColorModel());
            setName(t.getName());
            dtg = new DynamicTeamGraph(t,sim.log);
        }
    }
    
    
    /** Stores the visual elements for a particular agent. */
    class AgentVisuals extends PlottableGroup<Euclidean2> {
        
        PointSet2D path;
        CirclePoint2D loc;
        
        AgentVisuals(Agent a) {
            setAgent(a);
            add(path);
            add(loc);
        }
        
        public void setAgent(Agent a) {
            if (a==null){ return; }
            setColorModel(a.getColorModel());
            setName(a.getName());
            if (loc==null) { loc = new CirclePoint2D(); }
            loc.setColorModel(a.getColorModel());
            loc.setModel(a.getPointModel());
            loc.setLabel(a.getName());
            loc.deleteRadii();
            loc.addRadius(a.getCommRange());
            loc.addRadius(a.getSensorRange());
            if (path==null) { path = new PointSet2D(); }
            path.setColorModel(a.getColorModel());
            path.setPath(sim.log.pathOf(a));
        }
    }
}
