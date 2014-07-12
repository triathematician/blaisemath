/*
 * MultiGraphComponent.java
 * Created Nov 11, 2011
 */
package com.googlecode.blaisemath.graph.view;

/*
 * #%L
 * BlaiseGraphTheory
 * --
 * Copyright (C) 2009 - 2014 Elisha Peterson
 * --
 * Licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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
import com.googlecode.blaisemath.graph.Graph;
import com.googlecode.blaisemath.graph.view.GraphComponent;
import com.googlecode.blaisemath.graph.layout.GraphLayoutManager;

/**
 * Displays multiple graphs in a common component. Implemented as a {@link JList}
 * with a custom {@link ListCellRenderer}.
 *
 * @author elisha
 */
public class MultiGraphComponent extends JList implements PropertyChangeListener {

    /** Time graph manager */
    private LongitudinalGraphManager manager;

    /** Constructs a longitudinal graph panel without an actual graph. */
    public MultiGraphComponent() {
        this(null);
    }

    /** Construct instance with specified graph manager */
    public MultiGraphComponent(LongitudinalGraphManager m) {
        setCellRenderer(new GraphCellRenderer());
        setListData(new Object[]{});
        setManager(m);
        setLayoutOrientation(JList.HORIZONTAL_WRAP);
        super.setVisibleRowCount(10);
    }

    // <editor-fold defaultstate="collapsed" desc="Property Patterns">

    /** @return manager for the longitudinal graph */
    public LongitudinalGraphManager getManager() {
        return manager;
    }

    /** Changes the manager for the longitudinal graph */
    public void setManager(LongitudinalGraphManager m) {
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
            GraphLayoutManager gm = getAdapter() == null ? null : getGraphManager();
            Map<Object, Point2D.Double> positionMap = MultiGraphComponent.this.manager.getLayoutAlgorithm().getPositionMap(time);
            if (gm == null && gr != null) {
                setGraphManager(gm = new GraphLayoutManager(gr));
                gm.requestLocations(positionMap);
            } else if (gm != null) {
                gm.setGraph(gr);
                gm.requestLocations(positionMap);
            }
//            getVisometry().setWindowBounds(new Rectangle(0, 0, 200, 200));
            setBorder(BorderFactory.createTitledBorder("Time = " + time));
            return this;
        }
    }

}
