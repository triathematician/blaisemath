package com.googlecode.blaisemath.graph.app;

/*
 * #%L
 * BlaiseGraphTheory (v3)
 * --
 * Copyright (C) 2009 - 2024 Elisha Peterson
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

import com.google.common.graph.Graph;
import com.googlecode.blaisemath.graph.GraphNodeMetric;
import com.googlecode.blaisemath.graph.view.GraphComponent;
import com.googlecode.blaisemath.graphics.impl.DelegatingNodeLinkGraphic;
import com.googlecode.blaisemath.graphics.swing.PanAndZoomHandler;
import com.googlecode.blaisemath.style.AttributeSet;
import com.googlecode.blaisemath.style.Styles;
import org.jdesktop.application.Action;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * Graph component for {@link GraphApp}.
 * 
 * @author Elisha Peterson
 */
@SuppressWarnings({"EmptyMethod", "unused", "UnstableApiUsage"})
public class GraphAppCanvas extends GraphComponent {

    private final MetricScaler scaler = new MetricScaler();

    public GraphAppCanvas() {
        PanAndZoomHandler.install(this);

        DelegatingNodeLinkGraphic graph = adapter.getViewGraph();
        graph.setDragEnabled(true);
        graph.setPointSelectionEnabled(true);
        
        graph.getNodeStyler().setLabelDelegate(Object::toString);
        graph.getNodeStyler().setLabelStyleDelegate(input -> {
            Double rad = scaler.apply(input).getDouble(Styles.MARKER_RADIUS, 10.0);
            Point2D offset = new Point2D.Double(rad, rad);
            return AttributeSet.withParent(Styles.DEFAULT_TEXT_STYLE)
                    .and(Styles.FILL, new Color(128, 128, 64, 128))
                    .and(Styles.FONT_SIZE, 8f)
                    .and(Styles.OFFSET, offset);
        });
        graph.getNodeStyler().setStyleDelegate(scaler);
    }
    
    //region PROPERTIES

    @Override
    public void setGraph(Graph graph) {
        super.setGraph(graph);
        scaler.setGraph(graph);
    }

    public GraphNodeMetric getMetric() {
        return scaler.getMetric();
    }

    public void setMetric(GraphNodeMetric metric) {
        scaler.setMetric(metric);
    }
    
    //endregion
    
    //region ACTIONS
    
    @Action
    public void startLayout() {
        getLayoutManager().setLayoutTaskActive(true);
    }
    
    @Action
    public void stopLayout() {
        getLayoutManager().setLayoutTaskActive(false);
    }
    
    @Action
    @Override
    public void zoomToAll() {
        super.zoomToAll();
    }
    
    @Action
    @Override
    public void zoomToSelected() {
        super.zoomToSelected();
    }
    
    @Action
    public void zoom100() {
        super.resetTransform();
    }
    
    @Action
    @Override
    public void zoomIn() {
        super.zoomIn();
    }
    
    @Action
    @Override
    public void zoomOut() {
        super.zoomOut();
    }
    
    //endregion
    
}
