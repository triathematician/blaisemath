/**
 * Visuals3D.java
 * Created on Nov 15, 2008
 */

package analysis;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import simulation.Agent;
import simulation.Simulation;
import simulation.Team;
import specto.PlottableGroup;
import specto.euclidean2.PointSet2D;
import specto.euclidean3.Euclidean3;
import specto.euclidean3.PointSet3D;

/**
 * This class handles the primary 3D plot window for the simulation... this pretty much just shows
 * the time evolution of the player's movements.
 * 
 * @author Elisha Peterson
 */
public class MainVisuals3D extends PlottableGroup<Euclidean3> implements ActionListener {
        
    /** The underlying simulation. */
    Simulation sim;
    
    /** Default constructor (empty plot). */
    public MainVisuals3D(){}
    
    /** Constructs with given simulation. */
    public MainVisuals3D(Simulation sim) { setSim(sim); }

    
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
        PointSet3D captures = new PointSet3D(sim.log.capturePoints3D,Color.RED,PointSet2D.POINTS_ONLY);  
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
    class TeamVisuals extends PlottableGroup<Euclidean3> {        
        
        //DynamicTeamGraph dtg;
        
        TeamVisuals(Team t) {
            setTeam(t);
            //add(dtg);
            for(Agent a : t.agents){ add(new AgentVisuals(a)); }
        }
        
        public void setTeam(Team t) {
            if (t==null) { return; }
            setColorModel(t.getColorModel());
            setName(t.getName());
            //dtg = new DynamicTeamGraph(t,sim.log);
        }
    }
    
    
    /** Stores the visual elements for a particular agent. */
    class AgentVisuals extends PlottableGroup<Euclidean3> {
        
        PointSet3D path;
        //CirclePoint3D loc;
        
        AgentVisuals(Agent a) {
            setAgent(a);
            add(path);
            //add(loc);
        }
        
        public void setAgent(Agent a) {
            if (a==null){ return; }
            setColorModel(a.getColorModel());
            setName(a.getName());
//            if (loc==null) { loc = new CirclePoint2D(); }
//            loc.setColorModel(a.getColorModel());
//            loc.setModel(a.getPointModel());
//            loc.setLabel(a.getName());
//            loc.deleteRadii();
//            loc.addRadius(a.getCommRange());
//            loc.addRadius(a.getSensorRange());
            if (path==null) { path = new PointSet3D(); }
            path.setColorModel(a.getColorModel());
            path.setPath(sim.log.path3DOf(a));
        }
    }
}
