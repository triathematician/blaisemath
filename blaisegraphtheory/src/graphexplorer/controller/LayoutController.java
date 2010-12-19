/*
 * LayoutController.java
 * Created Nov 18, 2010
 */

package graphexplorer.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.LinkedHashMap;
import java.util.Map;
import org.bm.blaise.scio.graph.layout.IterativeGraphLayout;
import org.bm.blaise.scio.graph.layout.StaticGraphLayout;
import stormtimer.BetterTimer;

/**
 * <p>
 * Responsible for handling the layout mechanism for a graph, 
 * and notifying interested listeners when the values change.
 * </p>
 * <p>
 * Built to handle iterative layout algorithms for graphs and
 * the supporting animations.
 * </p>
 * <p>
 * Operates principally off the primary view graph,
 * but also maintains positions for nodes that are not
 * currently visible, so that if they come back into view
 * their positions are saved.
 * </p>
 * 
 * @author elisha
 */
public class LayoutController extends AbstractGraphController {

    //
    // PROPERTY CHANGE NAMES
    //

    /** Changes to layout algorithm */
    public static final String $LAYOUT_ALGORITHM = "layout algorithm";
    /** Changes to animating status */
    public static final String $ANIMATING = "animating";
    /** Changes to node positions */
    public static final String $POSITIONS = "positions";
    
    //
    // PROPERTIES
    //

    /** Stores the animating layout (may be null) */
    private IterativeGraphLayout layout = null;
    
    /** Timer handling animating layouts */
    BetterTimer layoutTimer;
    /** Whether animation is currently running */
    boolean animating = false;
    
    /** Stores the set of nodes & their locations (all nodes for longitudinal graphs) */
    private Map<Object, Point2D.Double> positions = null;

    //
    // CONSTRUCTOR
    //

    /** Constructs a new instance. */
    public LayoutController(GraphController gc) {
        gc.addViewGraphFollower(this);
        addPropertyChangeListener(gc);
        setBaseGraph(gc.getViewGraph());
    }

    //
    // LAYOUT COMPTUATIONS
    //

    @Override
    protected void updateGraph() {
        // TODO - background task layout mechanism here
    }

    /** Applies specified layout to the active graph */
    public void applyLayout(StaticGraphLayout layout, double... parameters) {
        setPositions(layout.layout(baseGraph, parameters));
    }

    /**
     * Steps layout animation (if not running).
     */
    public void stepLayout() {
        if (!animating && layout != null) {
            layout.iterate(baseGraph);
            positions.putAll(layout.getPositions());
            pcs.firePropertyChange($POSITIONS, null, positions);
        }
    }

    //
    // PROPERTY PATTERNS
    //

    /** @return active layout (may be null) */
    public IterativeGraphLayout getLayoutAlgorithm() {
        return layout;
    }

    /**
     * Sets the currently active layout, but does not start it up
     * @param layout the new layout
     */
    public void setLayoutAlgorithm(IterativeGraphLayout layout) {
        if (this.layout != layout) {
            if (layoutTimer != null)
                layoutTimer.stop();
            Object oldLayout = this.layout;
            this.layout = layout;
            if (layout != null)
                layout.reset(currentPositions());
            pcs.firePropertyChange($LAYOUT_ALGORITHM, oldLayout, layout);
        }
    }

    /** @return all nodes with positions */
    public Map<Object, Point2D.Double> getPositions() {
        return positions;
    }
    
    /**
     * Sets the locations of some or all nodes in the graph.
     * @param positions a map specifying positions of nodes
     */
    public void setPositions(Map<Object, Point2D.Double> positions) {
        if (!positions.keySet().containsAll(positions.keySet()))
            throw new IllegalArgumentException("Position map contains invalid keys!");
        if (animating)
            layout.requestPositions(positions);
        else {
            positions.putAll(positions);
            pcs.firePropertyChange($POSITIONS, null, positions);
        }
    }

    /** @return position of specified node */
    Point2D.Double positionOf(Object node) {
        return positions == null ? null : positions.get(node);
    }

    /** @return true if iterative layout algorithm is currently running */
    public boolean isAnimating() {
        return animating;
    }

    /** Sets layout algorithm to running or not running */
    public void setAnimating(boolean newAnimating) {
        if (animating == newAnimating || (newAnimating && layout == null))
            return;
        boolean oldValue = animating;

        animating = newAnimating;

        if (animating) {
            if (layoutTimer != null) {
                layoutTimer.cancel();
                layoutTimer = null;
            }
            layoutTimer = new BetterTimer(1);
            layoutTimer.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e) {
                    layout.iterate(baseGraph);
                    positions.putAll(layout.getPositions());
                    pcs.firePropertyChange($POSITIONS, null, positions);
                }
            });
            layoutTimer.start();
        } else {
            layoutTimer.cancel();
        }
        pcs.firePropertyChange($ANIMATING, oldValue, animating);
    }

    //
    // private utilities
    //

    /** @return list of current node locations, in same order as that stored in the graph */
    private Map<Object,Point2D.Double> currentPositions() {
        LinkedHashMap<Object,Point2D.Double> result = new LinkedHashMap<Object,Point2D.Double>();
        for (Object o : baseGraph.nodes())
            result.put(o, positions.get(o));
        return result;
    }
}
