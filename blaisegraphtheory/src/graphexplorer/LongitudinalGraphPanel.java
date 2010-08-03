/*
 * LongitudinalGraphPanel.java
 * Created July 7, 2010
 */

package graphexplorer;

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
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
        implements ChangeListener, PropertyChangeListener {

    /** Central component of the panel */
    PlanePlotComponent plot;
    /** Active graph displayed in the panel */
    PlaneGraph visGraph;
    /** Controller for the panel */
    GraphController gc;
    /** Slider element */
    ListSlider slider;

    /** Constructs a longitudinal graph panel without an actual graph. */
    public LongitudinalGraphPanel(GraphController gc) {
        super(new java.awt.BorderLayout());
        add(plot = new PlanePlotComponent(), java.awt.BorderLayout.CENTER);
        add(slider = new ListSlider(), java.awt.BorderLayout.SOUTH);
        setPreferredSize(new Dimension(500, 500));
        this.gc = gc;
        gc.addPropertyChangeListener(this);
        LongitudinalGraph lGraph = gc.getLongitudinalGraph();
        if (lGraph != null) {
            plot.add(visGraph = new PlaneGraph(gc.getActiveGraph()));
            visGraph.setPositionMap(gc.getPositions());
            updateSliderTime();
            slider.addChangeListener(this);
        } else {
            slider.setEnabled(false);
        }
    }
    
    //
    // BEANS
    //
    
    /** @return currently displayed graph */
    public PlaneGraph getPlaneGraph() {
        return visGraph;
    }

    /**
     * @return number of slices
     */
    public int getNumSlices() {
        LongitudinalGraph lGraph = gc.getLongitudinalGraph();
        return visGraph == null ? 0 : lGraph.getTimes().size();
    }

    /** Sets index of active slice, if index is within bounds */
    public void setActiveSliceIndex(int index) {
        slider.setValue(index);
    }

    //
    // CHANGE HANDLING
    //

    /** Flag to keep slider from firing unnecessary changes */
    boolean updating = false;

    /** Updates slider time based on lg time */
    private void updateSliderTime() {
        if (!updating) {
            double time = gc.getTime();
            List times = gc.getLongitudinalGraph().getTimes();
            slider.setValues(times);
            for (int i = 0; i < times.size(); i++) {
                if (time == times.get(i)) {
                    slider.setValue(i);
                    break;
                }
            }
        }
    }

    /** Used to update slider time based on the controller time. */
    private void updateControllerTime() {
        updating = true;
        gc.setTime((Double) gc.getLongitudinalGraph().getTimes().get(slider.getValue()));
        visGraph.setGraph(gc.getActiveGraph());
        visGraph.setPositionMap(gc.getPositions());
        updating = false;
    }

    //
    // CHANGE HANDLING
    //

    public void stateChanged(ChangeEvent e) {
        if (e.getSource() == slider)
            gc.setTime((Double) gc.getLongitudinalGraph().getTimes().get(slider.getValue()));
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource() == gc) {
            if (gc.isLongitudinal()) {
                if (evt.getPropertyName().equals("time")) {
                    updateControllerTime();
                } else if (evt.getPropertyName().equals("active")) {
                    visGraph.setGraph(gc.getActiveGraph());
                    visGraph.setPositionMap(gc.getPositions());
                }
            }
        }
    }

}
