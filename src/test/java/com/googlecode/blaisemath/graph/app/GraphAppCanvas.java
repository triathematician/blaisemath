/**
 * GraphAppCanvas.java
 * Created Jan 2016
 */
package com.googlecode.blaisemath.graph.app;

/*
 * #%L
 * BlaiseGraphTheory (v3)
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


import com.googlecode.blaisemath.graph.view.GraphComponent;
import com.googlecode.blaisemath.graphics.swing.PanAndZoomHandler;
import org.jdesktop.application.Action;

/**
 * Graph component for {@link GraphApp}.
 * 
 * @author elisha
 */
public class GraphAppCanvas extends GraphComponent {

    public GraphAppCanvas() {
        PanAndZoomHandler.install(this);
        adapter.getViewGraph().setDragEnabled(true);
        adapter.getViewGraph().setPointSelectionEnabled(true);
    }
    
    
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
    
}
