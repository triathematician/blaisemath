/**
 * CreatePathGesture.java
 * Created Oct 10, 2014
 */
package com.googlecode.blaisemath.gesture.swing;

import com.googlecode.blaisemath.gesture.GestureOrchestrator;
import com.google.common.collect.Lists;
import com.googlecode.blaisemath.graphics.core.Graphic;
import com.googlecode.blaisemath.graphics.swing.JGraphics;
import com.googlecode.blaisemath.util.TransformedCoordinateSpace;
import com.googlecode.blaisemath.style.Styles;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.List;

/*
 * #%L
 * BlaiseSketch
 * --
 * Copyright (C) 2015 Elisha Peterson
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

/**
 * Gesture for adding a path to the canvas.
 * 
 * @author Elisha
 */
public class CreatePathGesture extends JGraphicCreatorGesture {
    
    private List<Point2D> pathPoints;
    
    public CreatePathGesture(GestureOrchestrator orchestrator) {
        super(orchestrator, "Draw path", "Drag from the start of the path to the end of the path.");
    }

    @Override
    public void mousePressed(MouseEvent e) {
        Object src = e.getSource();
        pressPoint = src instanceof TransformedCoordinateSpace 
                ? ((TransformedCoordinateSpace)src).toGraphicCoordinate(e.getPoint())
                : e.getPoint();
        pathPoints = Lists.newArrayList();
        pathPoints.add(pressPoint);
        if (src instanceof Component) {
            ((Component) src).repaint();
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        Object src = e.getSource();
        locPoint = src instanceof TransformedCoordinateSpace 
                ? ((TransformedCoordinateSpace)src).toGraphicCoordinate(e.getPoint())
                : e.getPoint();
        pathPoints.add(locPoint);
        if (src instanceof Component) {
            ((Component) src).repaint();
        }
    }

    @Override
    protected Graphic<Graphics2D> createGraphic() {
        if (pathPoints != null && pathPoints.size() > 1) {
            GeneralPath gp = new GeneralPath();
            gp.moveTo(pathPoints.get(0).getX(), pathPoints.get(0).getY());
            for (int i = 1; i < pathPoints.size(); i++) {
                gp.lineTo(pathPoints.get(i).getX(), pathPoints.get(i).getY());
            }
            return JGraphics.path(gp, Styles.DEFAULT_PATH_STYLE.copy());
        } else {
            return null;
        }
    }
    
}
