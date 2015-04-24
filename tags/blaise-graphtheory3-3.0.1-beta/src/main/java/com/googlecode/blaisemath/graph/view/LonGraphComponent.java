/*
 * LonGraphComponent.java
 * Created July 7, 2010
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

import com.google.common.collect.Maps;
import com.googlecode.blaisemath.graph.Graph;
import com.googlecode.blaisemath.graph.GraphLayoutManager;
import com.googlecode.blaisemath.graph.lon.LonGraph;
import com.googlecode.blaisemath.graph.mod.layout.SpringLayout;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.jdesktop.layout.GroupLayout;

/**
 * This panel displays a longitudinal graph together with a slider bar that can
 * be used to transition across various time slices of the graph. This panel also
 * keeps track of the layout mechanism, which can then apply consistently across each
 * slice of the graph.
 *
 * @author Elisha Peterson
 */
public final class LonGraphComponent extends JPanel {

    /** Time graph manager */
    private LonGraphManager manager;
    /** Time slider */
    private final LonGraphSlider slider;
    /** Currently active graph component */
    private final GraphComponent plot;

    /** Time label (overlays on plot) */
    private final JLabel timeLabel;

    /** Handles manager changes */
    private final PropertyChangeListener managerListener;
    

    /** Constructs a longitudinal graph panel without an actual graph. */
    public LonGraphComponent() {
        this(null, null);
    }

    /** 
     * Construct instance with specified graph manager
     * @param m graph manager
     */
    public LonGraphComponent(LonGraphManager m) {
        this(m, null);
    }

    /** 
     * Construct instance with specified graph manager
     * @param m graph manager
     * @param gm layout manager
     */
    public LonGraphComponent(LonGraphManager m, GraphLayoutManager gm) {
        super(new java.awt.BorderLayout());
        plot = new GraphComponent();
        slider = new LonGraphSlider();
        timeLabel = new JLabel("Slice t=??");
        initComponents();

        managerListener = new PropertyChangeListener(){
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals("time")) {
                    timeLabel.setText("Slice t="+evt.getNewValue());
                } else if (evt.getPropertyName().equals("nodePositions")) {
                    Graph gr = manager.getSlice();
                    plot.getLayoutManager().setGraph(gr);
                    Map<Object,Point2D.Double> locs = (Map<Object, Point2D.Double>) evt.getNewValue();
                    plot.getLayoutManager().requestLocations(locs);
                }
            }
        };
        
        setManager(m, gm);
    }

    private void initComponents() {
        //<editor-fold defaultstate="collapsed" desc="GUI initialization">
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
        // </editor-fold>
    }


    // <editor-fold defaultstate="collapsed" desc="Property Patterns">

    /**
     * Sets the underlying graph
     * @param g the graph
     */
    public void setLonGraph(LonGraph g) {
        setLonGraphManager(new LonGraphManager(g));
    }

    /**
     * @return plot on which graph is displayed
     */
    public GraphComponent getGraphComponent() {
        return plot;
    }

    /**
     * @return graph adapter
     */
    public VisualGraph getGraphAdapter() {
        return plot.getAdapter();
    }

    /**
     * @return manager for graph
     */
    public GraphLayoutManager getLayoutManager() {
        return plot.getLayoutManager();
    }

    /**
     * @return manager for the longitudinal graph
     */
    public LonGraphManager getLonGraphManager() {
        return manager;
    }

    /**
     * Changes the manager for the longitudinal graph
     * @param m the graph manager
     */
    public void setLonGraphManager(LonGraphManager m) {
        setManager(m, null);
    }

    /**
     * Changes the manager for the longitudinal graph
     * @param m the graph manager
     * @param gm the graph layout manager
     */
    private void setManager(LonGraphManager m, GraphLayoutManager gm) {
        if (this.manager != m) {
            if (this.manager != null) {
                this.manager.removePropertyChangeListener(managerListener);
            }
            slider.setManager(m);
            this.manager = m;
            if (m != null) {
                m.addPropertyChangeListener(managerListener);
            }
            if (gm == null) {
                Graph g = m == null ? null : m.getSlice();
                plot.setLayoutManager(g == null ? null : new GraphLayoutManager(g, new SpringLayout()));
            } else {
                plot.setLayoutManager(gm);
            }
        }
    }

    // </editor-fold>

}
