/*
 * MultiGraphComponent.java
 * Created Nov 11, 2011
 */
package org.blaise.graph.dynamic.view;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import org.blaise.graph.Graph;
import org.blaise.graph.view.GraphComponent;
import org.blaise.graph.view.GraphManager;

/**
 * Displays multiple graphs in a common component. Implemented as a {@link JList}
 * with a custom {@link ListCellRenderer}.
 * 
 * @author elisha
 */
public class MultiGraphComponent extends JList implements PropertyChangeListener {

    /** Time graph manager */
    private TimeGraphManager manager;

    /** Constructs a longitudinal graph panel without an actual graph. */
    public MultiGraphComponent() {
        this(null);
    }

    /** Construct instance with specified graph manager */
    public MultiGraphComponent(TimeGraphManager m) {
        setCellRenderer(new GraphCellRenderer());
        setListData(new Object[]{});
        setManager(m);
        setLayoutOrientation(JList.HORIZONTAL_WRAP);
        super.setVisibleRowCount(10);
    }
    
    // <editor-fold defaultstate="collapsed" desc="Property Patterns">

    /** @return manager for the longitudinal graph */
    public TimeGraphManager getManager() { 
        return manager; 
    }
    
    /** Changes the manager for the longitudinal graph */
    public void setManager(TimeGraphManager m) {
        if (this.manager != m) {
            if (this.manager != null)
                this.manager.removePropertyChangeListener(this);
            this.manager = m;
            if (m != null) {
                m.addPropertyChangeListener(this);
                setListData(m.getTimeGraph().getTimes().toArray());
            } else {
                setListData(new Object[]{});
            }
        }
    }

    // </editor-fold>
    
    
    // <editor-fold defaultstate="collapsed" desc="Event Handling">
    public void propertyChange(PropertyChangeEvent evt) {
        repaint();
        // NOTHING HERE FOR THE MOMENT
    }

    // </editor-fold>
    
    
    private class GraphCellRenderer extends GraphComponent implements ListCellRenderer {
        public GraphCellRenderer() {
            setPreferredSize(new Dimension(200, 200));
        }
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Double time = (Double) value;
            Graph gr = MultiGraphComponent.this.manager.getTimeGraph().slice(time, false);
            GraphManager gm = getAdapter() == null ? null : getGraphManager();
            Map<Object, Point2D.Double> positionMap = MultiGraphComponent.this.manager.getLayoutAlgorithm().getPositionMap(time);
            if (gm == null && gr != null) {
                setGraphManager(gm = new GraphManager(gr));
                gm.requestPositionMap(positionMap);
            } else if (gm != null) {
                gm.setGraph(gr);
                gm.requestPositionMap(positionMap);
            }
            getVisometry().setWindowBounds(new Rectangle(0, 0, 200, 200));
            setBorder(BorderFactory.createTitledBorder("Time = " + time));
            return this;
        }
    }

}
