/*
 * LonGraphListComponent.java
 * Created Nov 11, 2011
 */
package com.googlecode.blaisemath.graph.view;

/*
 * #%L
 * BlaiseGraphTheory
 * --
 * Copyright (C) 2009 - 2015 Elisha Peterson
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
import com.googlecode.blaisemath.graphics.swing.PanAndZoomHandler;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
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
public final class LonGraphListComponent extends JList {
    
    private static final Dimension CELL_DIMENSION = new Dimension(150, 150);
    private static final int MARGIN = 5;
    private static final Rectangle CELL_RECT = new Rectangle(MARGIN, MARGIN, CELL_DIMENSION.width-2*MARGIN, CELL_DIMENSION.height-2*MARGIN);

    /** Time graph manager */
    private LonGraphManager manager;
    /** Handles updates from manager */
    private final PropertyChangeListener managerListener;
    
    /** Constructs a longitudinal graph panel without an actual graph. */
    public LonGraphListComponent() {
        this(null);
    }

    /** 
     * Construct instance with specified graph manager
     * @param m graph manager
     */
    public LonGraphListComponent(LonGraphManager m) {
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
        setVisibleRowCount(-1);
    }

    // <editor-fold defaultstate="collapsed" desc="Property Patterns">
    
    /**
     * @return manager for the longitudinal graph
     */
    public LonGraphManager getManager() {
        return manager;
    }

    /**
     * Changes the manager for the longitudinal graph
     * @param m the graph manager
     */
    public void setManager(LonGraphManager m) {
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
            setPreferredSize(CELL_DIMENSION);
        }
        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Double time = (Double) value;
            Graph gr = LonGraphListComponent.this.manager.getTimeGraph().slice(time, false);
            GraphLayoutManager gm = getLayoutManager();
            Map<Object, Point2D.Double> nodeLocs = LonGraphListComponent.this.manager.getLayoutAlgorithm().getPositionsCopy(time);
            gm.setGraph(gr);
            gm.requestLocations(nodeLocs);
            Rectangle2D bds = getGraphicRoot().boundingBox();
            PanAndZoomHandler.setDesiredLocalBounds(this, CELL_RECT, bds);
            setBorder(BorderFactory.createTitledBorder("Time = " + time));
            return this;
        }
    }

}
