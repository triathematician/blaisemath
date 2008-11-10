/**
 * MetricVisuals.java
 * Created on Oct 30, 2008
 */

package analysis;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.HashSet;
import metrics.Valuation;
import sequor.VisualControl;
import sequor.control.FLabel;
import sequor.control.FLabelBox;
import simulation.Simulation;
import simulation.Team;
import specto.Plottable;
import specto.PlottableGroup;
import specto.euclidean2.Euclidean2;
import specto.euclidean2.PointSet2D;

/**
 * Stores visual elements that can be added to v "metrics plot" displaying the value
 * of various metrics over time.
 * 
 * @author Elisha Peterson
 */
public class MetricVisuals extends PlottableGroup<Euclidean2> implements ActionListener {  
    
    /** The underlying simulation. */
    Simulation sim;   
        
    /** The legend box. */
    FLabelBox legend;
    
    
    // CONSTRUCTORS
    
    /** Default constructor (empty plot). */
    public MetricVisuals(){
        legend = new FLabelBox(0, 0, 0, 0);
    }
    
    /** Constructs with given simulation. */
    public MetricVisuals(Simulation sim) {
        this();
        setSim(sim);
    }

    
    // BEAN PATTERNS
    
    /** Returns the simulation. */
    public Simulation getSim() { return sim; }
    
    /** Initializes to v new simulation. */
    public void setSim(Simulation sim) {
        if(sim == null) { return; }
        if(this.sim != null) { this.sim.removeActionListener(this); }
        this.sim = sim;
        setName(sim.getName());
        sim.addActionListener(this);
        setTeams(sim.getTeams());
    }
    
    /** Initializes to given set of teams. */
    public void setTeams(Collection<Team> teams){
        if(teams == null){ return; }
        for (Plottable p : plottables) {
            if (p instanceof TeamValueVisuals) { 
                ((TeamValueVisuals)p).t.removeActionListener((TeamValueVisuals)p);
            }
        }
        clear();
        legend.clear();
        for (Team t : sim.getTeams()) { add(new TeamValueVisuals(t)); }
        legend.performLayout();
    }
    
    /** Returns legend box. */
    public VisualControl getLegend() { return legend; }
    
    
    // EVENT HANDLING
    
    /** Handles action events from the simulation. */
    @Override
    public void actionPerformed(ActionEvent e) {
        String ac = e.getActionCommand();
        if(e.getSource().equals(sim)){
            if (ac.equals("reset")) { setSim(sim); }
        }
    }
    
    
    //
    //
    // SUB-CLASSES
    //
    //
    
    /** Stores the visual elements for v particular team. */
    class TeamValueVisuals extends PlottableGroup<Euclidean2> implements ActionListener {
        
        Team t;
        HashSet<FLabel> tLabels;
        
        TeamValueVisuals(Team t) {
            tLabels=new HashSet<FLabel>();
            setTeam(t);
        }
        
        public void setTeam(final Team t) {
            if (t==null) { return; }
            if (this.t!=null){ this.t.removeActionListener(this); }   
            this.t = t;         
            t.addActionListener(this);
            setColorModel(t.getColorModel());
            setName(t.getName());
            clear();
            for(FLabel fl:tLabels){legend.remove(fl);}
            tLabels.clear();
            // add metrics
            for(final Valuation v:t.metrics) {
                add(new PointSet2D(sim.log.teamMetrics.get(v),t.getColorModel(),PointSet2D.THIN));
                add(new PointSet2D(sim.log.partialTeamMetrics.get(v),t.getColorModel(),PointSet2D.DOTTED));
                final FLabel label = new FLabel(v, t.getColorModel(), null);
                v.addActionListener(new ActionListener(){
                    public void actionPerformed(ActionEvent e) { label.setText(v.toString()); }
                });
                legend.add(label);
                tLabels.add(label);
            }
            // add victory metric
            if(t.victory != null) {
                add(new PointSet2D(sim.log.teamMetrics.get(t.victory),t.getColorModel(),PointSet2D.THIN));
                add(new PointSet2D(sim.log.partialTeamMetrics.get(t.victory),t.getColorModel(),PointSet2D.DOTTED));
                final FLabel label = new FLabel(t.victory, t.getColorModel(), null);
                t.victory.addActionListener(new ActionListener(){
                    public void actionPerformed(ActionEvent e) { label.setText(t.victory.toString()); }
                });
                legend.add(label);
                tLabels.add(label);
            }
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if(e.getSource() instanceof Team && e.getActionCommand().equals("teamSetupChange")){
                setTeam((Team)e.getSource());
            }
        }
    }
}
