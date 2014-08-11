/**
 * SegmentGraphic.java
 * Created Jan 23, 2011
 */
package com.googlecode.blaisemath.dev.compoundgraphics;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2009 - 2014 Elisha Peterson
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

import com.googlecode.blaisemath.graphics.core.PrimitiveGraphic;
import com.googlecode.blaisemath.graphics.swing.JGraphics;
import java.awt.Color;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import com.googlecode.blaisemath.style.Markers;
import com.googlecode.blaisemath.style.AttributeSet;
import com.googlecode.blaisemath.style.StyleHints;
import com.googlecode.blaisemath.style.Styles;

/**
 * Displays a segment between two points.
 * By default displays an arrowhead at the end, and allows the endpoints of the arrow to be moved.
 *
 * @author Elisha
 */
public class SegmentGraphic extends TwoPointGraphicSupport {

    /** Entry with the line */
    protected PrimitiveGraphic segmentGraphic;

    /** Construct segment between specified points */
    public SegmentGraphic(Point2D start, Point2D end) {
        super(start, end);
    }

    @Override
    protected void initGraphics() {
        // ensure line is added first
        addGraphic(segmentGraphic = JGraphics.path(new GeneralPath()));        
        super.initGraphics();
        
        start.setStyle(AttributeSet.with(Styles.MARKER, Markers.CIRCLE)
                .and(Styles.MARKER_RADIUS, 2)
                .and(Styles.FILL, Color.black));
        start.setStyleHint(StyleHints.HIDDEN_FUNCTIONAL_HINT, true);
        
        end.setStyle(Styles.defaultPointStyle()
                .and(Styles.MARKER, Markers.ARROWHEAD));
    }

    @Override
    protected void pointsUpdated() {
        double angle = Math.atan2(
                end.getPrimitive().getY() - start.getPrimitive().getY(),
                end.getPrimitive().getX() - start.getPrimitive().getX());
        start.getPrimitive().setAngle(angle+Math.PI);
        end.getPrimitive().setAngle(angle);
        segmentGraphic.setPrimitive(new Line2D.Double(start.getPrimitive(), end.getPrimitive()));
    }

    public AttributeSet getLineStyle() { 
        return segmentGraphic.getStyle();
    }
    
    public void setLineStyle(AttributeSet s) { 
        segmentGraphic.setStyle(s); 
    }
    
}
