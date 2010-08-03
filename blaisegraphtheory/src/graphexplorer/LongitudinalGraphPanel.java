/*
 * LongitudinalGraphPanel.java
 * Created July 7, 2010
 */

package graphexplorer;

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.bm.blaise.scio.graph.Graph;
import org.bm.blaise.scio.graph.LongitudinalGraph;
import org.bm.blaise.specto.plane.graph.PlaneGraph;
import util.ListSlider;
import visometry.plane.PlanePlotComponent;

/**
 * This panel displays a longitudinal graph together with a slider bar that can
 * be used to transition across various time slices of the graph. This panel also
 * keeps track of the layout mechanism, which can then apply consistently across each
 * slice of the graph.
 *
 * @author Elisha Peterson
 */
public class LongitudinalGraphPanel extends JPanel
        implements ChangeListener {

    /** Central component of the panel */
    PlanePlotComponent plot;
    /** Active graph displayed in the panel */
    PlaneGraph visGraph;
    /** Longitudinal graph underlying the panel */
    LongitudinalGraph lGraph;
    /** Slider element */
    ListSlider slider;

    /** Constructs a longitudinal graph panel without an actual graph. */
    public LongitudinalGraphPanel(LongitudinalGraph lGraph) {
        super(new java.awt.BorderLayout());
        add(plot = new PlanePlotComponent(), java.awt.BorderLayout.CENTER);
        add(slider = new ListSlider(), java.awt.BorderLayout.SOUTH);
        setPreferredSize(new Dimension(500, 500));
        setLongitudinalGraph(lGraph);
        slider.addChangeListener(this);
    }

    //
    // BEANS
    //

    /**
     * Returns the panel's longitudinal graph
     * @return longitudinal graph corresponding to this panel
     */
    public LongitudinalGraph getLongitudinalGraph() {
        return lGraph;
    }

    /** 
     * Sets current active graph, possibly null
     * @param lGraph a longitudinal graph or null
     */
    public void setLongitudinalGraph(LongitudinalGraph lGraph) {
        this.lGraph = lGraph;
        if (lGraph == null && visGraph != null) {
            plot.remove(visGraph);
            visGraph = null;
        } else if (lGraph != null) {
            Graph slice0 = lGraph.slice(lGraph.getMinimumTime(), true);
            if (visGraph != null)
                plot.remove(visGraph);
            plot.add(visGraph = new PlaneGraph(slice0));
            slider.setValues(lGraph.getTimes());
        }
    }

    /** @return active time */
    public double getCurrentTime() {
        return (Double) lGraph.getTimes().get(slider.getValue());
    }
    
    /** 
     * Sets current time to value closest to specified one.
     * Maintains current point locations.
     */
    public void setCurrentTime(double time) {
        Point2D.Double[] pts = visGraph.getPoint();
        visGraph.setGraph(lGraph.slice(time, false));
        visGraph.setPoint(pts);
    }

    /** @return index of active slice */
    public int getActiveSliceIndex() {
        return slider.getValue();
    }

    /** Sets index of active slice, if index is within bounds */
    public void setActiveSliceIndex(int index) {
        if (index >= 0 && index < lGraph.getTimes().size())
            setCurrentTime((Double) lGraph.getTimes().get(index));
    }

    //
    // ESSENTIAL GETTERS
    //

    /**
     * @return number of slices
     */
    public int getNumSlices() {
        return visGraph == null ? 0 : lGraph.getTimes().size();
    }

    /**
     * Returns the currently displayed graph slice on the panel
     * @return slice of longitudinal graph currently displayed
     */
    public Graph getActiveSlice() {
        return visGraph == null ? null : visGraph.getGraph();
    }

    /**
     * Returns the currently displayed plane graph
     * @return plane graph
     */
    public PlaneGraph getPlaneGraph() {
        return visGraph;
    }

    //
    // CHANGE HANDLING
    //

    public void stateChanged(ChangeEvent e) {
        if (e.getSource() == slider)
            setCurrentTime(slider.getValue());
    }    

}
