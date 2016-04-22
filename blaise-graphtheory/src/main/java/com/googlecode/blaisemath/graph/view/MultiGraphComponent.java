/*
 * MultiGraphComponent.java
 * Created Nov 11, 2011
 */
package com.googlecode.blaisemath.graph.view;

/*
 * #%L
 * BlaiseGraphTheory
 * --
 * Copyright (C) 2009 - 2016 Elisha Peterson
 * --
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
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

import com.googlecode.blaisemath.graph.Graph;
import com.googlecode.blaisemath.graph.GraphLayoutManager;
import com.googlecode.blaisemath.graph.modules.layout.SpringLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 * Displays multiple graphs in a common component. Implemented as a {@link JList}
 * with a custom {@link ListCellRenderer}.
 *
 * @author elisha
 */
public final class MultiGraphComponent extends JList {

    /** Time graph manager */
    private LongitudinalGraphManager manager;
    /** Handles updates from manager */
    private final PropertyChangeListener managerListener;
    
    /** Constructs a longitudinal graph panel without an actual graph. */
    public MultiGraphComponent() {
        this(null);
    }

    /** 
     * Construct instance with specified graph manager
     * @param m graph manager
     */
    public MultiGraphComponent(LongitudinalGraphManager m) {
        setCellRenderer(new GraphCellRenderer());
        setListData(new Object[]{});
        managerListener = new PropertyChangeListener(){
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                repaint();
            }
        };
        setManager(m);
        setLayoutOrientation(JList.HORIZONTAL_WRAP);
        super.setVisibleRowCount(10);
    }

    // <editor-fold defaultstate="collapsed" desc="Property Patterns">
    
    /**
     * @return manager for the longitudinal graph
     */
    public LongitudinalGraphManager getManager() {
        return manager;
    }

    /**
     * Changes the manager for the longitudinal graph
     * @param m the graph manager
     */
    public void setManager(LongitudinalGraphManager m) {
        if (this.manager != m) {
            if (this.manager != null) {
                this.manager.removePropertyChangeListener(managerListener);
            }
            this.manager = m;
            if (m != null) {
                m.addPropertyChangeListener(managerListener);
                setListData(m.getTimeGraph().getTimes().toArray());
            } else {
                setListData(new Object[]{});
            }
        }
    }

    // </editor-fold>


    private class GraphCellRenderer extends GraphComponent implements ListCellRenderer {
        GraphCellRenderer() {
            setPreferredSize(new Dimension(200, 200));
        }
        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Double time = (Double) value;
            Graph gr = MultiGraphComponent.this.manager.getTimeGraph().slice(time, false);
            GraphLayoutManager gm = getAdapter() == null ? null : getLayoutManager();
            Map<Object, Point2D.Double> positionMap = MultiGraphComponent.this.manager.getLayoutAlgorithm().getPositionsCopy(time);
            if (gm == null && gr != null) {
                setLayoutManager(gm = new GraphLayoutManager(gr, new SpringLayout()));
                gm.requestLocations(positionMap);
            } else if (gm != null) {
                gm.setGraph(gr);
                gm.requestLocations(positionMap);
            }
            setBorder(BorderFactory.createTitledBorder("Time = " + time));
            return this;
        }
    }

}
