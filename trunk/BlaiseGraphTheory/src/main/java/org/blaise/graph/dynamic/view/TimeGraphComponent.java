/*
 * TimeGraphComponent.java
 * Created July 7, 2010
 */

package org.blaise.graph.dynamic.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.blaise.graph.Graph;
import org.blaise.graph.dynamic.TimeGraph;
import org.blaise.graph.view.GraphComponent;
import org.blaise.graph.view.GraphManager;
import org.blaise.graph.view.PlaneGraphAdapter;
import org.jdesktop.layout.GroupLayout;

/**
 * This panel displays a longitudinal graph together with a slider bar that can
 * be used to transition across various time slices of the graph. This panel also
 * keeps track of the layout mechanism, which can then apply consistently across each
 * slice of the graph.
 *
 * @author Elisha Peterson
 */
public final class TimeGraphComponent extends JPanel
        implements PropertyChangeListener {

    /** Time graph manager */
    private TimeGraphManager manager;
    /** Time slider */
    private final TimeGraphSlider slider;
    /** Currently active graph component */
    private final GraphComponent plot;
    /** Flag telling component whether to update its own graph upon receiving a property time change */
    private boolean updateWithTime = true;
    
    /** Time label (overlays on plot) */
    private final JLabel timeLabel;


    // <editor-fold defaultstate="collapsed" desc="Constructors & Initializers">

    /** Constructs a longitudinal graph panel without an actual graph. */
    public TimeGraphComponent() {
        this(null, null);
    }

    /** Construct instance with specified graph manager */
    public TimeGraphComponent(TimeGraphManager m) {
        this(m, null);
    }

    /** Construct instance with specified graph manager */
    public TimeGraphComponent(TimeGraphManager m, GraphManager gm) {
        super(new java.awt.BorderLayout());
        plot = new GraphComponent();
        slider = new TimeGraphSlider();
        timeLabel = new JLabel("Slice t=??");
        initComponents();
        setManager(m, gm);
    }
    
    private void initComponents() {
        timeLabel.setFont(timeLabel.getFont().deriveFont(Font.ITALIC, 10f));
        timeLabel.setForeground(Color.DARK_GRAY);
        timeLabel.setOpaque(false);
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

        add(plot, BorderLayout.CENTER);
        add(slider, BorderLayout.SOUTH); // will later be replaced
        setPreferredSize(new Dimension(600, 600));
        validate();
    }
    // </editor-fold>


    // <editor-fold defaultstate="collapsed" desc="Property Patterns">

    /** Sets the underlying graph */
    public void setTimeGraph(TimeGraph g) {
        setManager(new TimeGraphManager(g));
    }
    
    /** @return plot on which graph is displayed */
    public GraphComponent getGraphComponent() { return plot; }

    /** @return graph adapter */
    public PlaneGraphAdapter getGraphAdapter() { return plot.getAdapter(); }

    /** @return manager for the longitudinal graph */
    public TimeGraphManager getManager() { return manager; }

    /** @return manager for graph */
    public GraphManager getGraphManager() { return plot.getGraphManager(); }
    
    /** Changes the manager for the longitudinal graph */
    public void setManager(TimeGraphManager m) { setManager(m, null); }
    
    /** Changes the manager for the longitudinal graph */
    private void setManager(TimeGraphManager m, GraphManager gm) {
        if (this.manager != m) {
            if (this.manager != null)
                this.manager.removePropertyChangeListener(this);
            slider.setManager(m);
            this.manager = m;
            if (m != null)
                m.addPropertyChangeListener(this);
            if (gm == null) {
                Graph g = m == null ? null : m.getSlice();
                plot.setGraphManager(g == null ? null : new GraphManager(g));
            } else {
                plot.setGraphManager(gm);
            }
        }
    }

    public boolean isUpdateWithTime() {
        return updateWithTime;
    }

    public void setUpdateWithTime(boolean updateWithTime) {
        this.updateWithTime = updateWithTime;
    }

    // </editor-fold>

    
    // <editor-fold defaultstate="collapsed" desc="Event Handling">
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource() == manager) {
            if (evt.getPropertyName().equals("timeData")) {
                Object[] arr = (Object[]) evt.getNewValue();
                Double time = (Double) arr[0];
                Map<Object,Point2D.Double> pos = (Map<Object, Point2D.Double>) arr[1];
                if (updateWithTime) {
                    Graph gr = manager.getSlice();
                    plot.getGraphManager().setGraph(gr);
                }
                if (pos != null)
                    plot.getGraphManager().requestPositionMap(pos);
                timeLabel.setText("Slice t="+time);
            } else if (evt.getPropertyName().equals("nodePositions")) {
                plot.getGraphManager().requestPositionMap((Map<Object, Point2D.Double>) evt.getNewValue());
            }
        }
    }

    // </editor-fold>


// <editor-fold defaultstate="collapsed" desc="Show/Hide Label">
    private static JLabel hideNote;

    public void hidePlot() {
        remove(plot);
        add(hideNote = new JLabel("Exporting..."));
        repaint();
    }

    public void showPlot() {
        remove(hideNote);
        for (Component c : getComponents())
            if (c == plot)
                return;
        add(plot, java.awt.BorderLayout.CENTER);
        repaint();
    }
// </editor-fold>

}
