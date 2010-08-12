/*
 * LongitudinalGraphPanel.java
 * Created July 7, 2010
 */

package graphexplorer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.bm.blaise.scio.graph.LongitudinalGraph;
import org.bm.blaise.specto.plane.graph.PlaneGraph;
import org.jdesktop.layout.GroupLayout;
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
    /** time label */
    JLabel timeLabel;

    /** Constructs a longitudinal graph panel without an actual graph. */
    public LongitudinalGraphPanel(GraphController gc) {
        super(new java.awt.BorderLayout());

        setPreferredSize(new Dimension(600, 600));
        add(plot = new PlanePlotComponent(), java.awt.BorderLayout.CENTER);
        add(slider = new ListSlider(), java.awt.BorderLayout.SOUTH);
        timeLabel = new JLabel("Slice t=??");
        timeLabel.setFont(timeLabel.getFont().deriveFont(Font.ITALIC, 10f));
        timeLabel.setForeground(Color.DARK_GRAY);
        timeLabel.setOpaque(false);

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
        initPlotLayout();
    }

    /**
     * Sets up the layout of the main panel; specifically, adds the time label to
     * the bottom right of the screen
     */
    void initPlotLayout() {
        GroupLayout layout = new GroupLayout(plot);
        plot.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.LEADING)
                .add(GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap(213, Short.MAX_VALUE)
                .add(timeLabel)
                .addContainerGap()) );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.LEADING)
                .add(GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap(206, Short.MAX_VALUE)
                .add(timeLabel)
                .addContainerGap())
        );        
    }

    private static JLabel hideNote;

    void hidePlot() {
        remove(plot);
        add(hideNote = new JLabel("Exporting..."));
        repaint();
    }

    void showPlot() {
        remove(hideNote);
        for (Component c : getComponents())
            if (c == plot)
                return;
        add(plot, java.awt.BorderLayout.CENTER);
        repaint();
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
            timeLabel.setText("Slice t="+time);
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
        double time = (Double) gc.getLongitudinalGraph().getTimes().get(slider.getValue());
        gc.setTime(time);
        timeLabel.setText("Slice t="+time);
        visGraph.setGraph(gc.getActiveGraph());
        visGraph.setPositionMap(gc.getPositions());
        visGraph.highlightNodes(gc.getNodeSubset());
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
                    visGraph.highlightNodes(gc.getNodeSubset());
                }
            }
        }
    }

}
