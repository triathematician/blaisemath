package com.googlecode.blaisemath.graphics.impl;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2009 - 2019 Elisha Peterson
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

import com.googlecode.blaisemath.graphics.PrimitiveGraphic;
import com.googlecode.blaisemath.graphics.Renderer;
import com.googlecode.blaisemath.primitive.ArrowLocation;
import com.googlecode.blaisemath.primitive.Markers;
import com.googlecode.blaisemath.style.AttributeSet;
import com.googlecode.blaisemath.style.StyleHints;
import com.googlecode.blaisemath.style.Styles;
import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

/**
 * Displays a line segment between two points, with the possibility of adding
 * arrows on one or both ends.
 *
 * @param <G> graphics canvas type
 * 
 * @author Elisha Peterson
 */
public class SegmentGraphic<G> extends TwoPointGraphic<G> {

    /** Entry with the line */
    protected PrimitiveGraphic<Shape, G> lineGraphic = new PrimitiveGraphic<>(null, Styles.DEFAULT_PATH_STYLE.copy(), null);
    /** Where arrows are displayed */
    protected ArrowLocation arrowLoc;

    /** 
     * Construct segment between specified points
     * @param ps start of segment
     * @param pe end of segment
     * @param loc where to position arrows, relative to start and end
     * @param renderer renderer for points
     * @param pathRenderer renderer for paths
     */
    public SegmentGraphic(Point2D ps, Point2D pe, ArrowLocation loc, Renderer<Point2D, G> renderer, Renderer<Shape, G> pathRenderer) {
        super(ps, pe, renderer);
        setArrowLocation(loc);
        
        start.setStyle(Styles.marker(Markers.CIRCLE, Color.black, 2));
        start.setStyleHint(StyleHints.HIDDEN_FUNCTIONAL_HINT, true);
        end.setStyle(Styles.DEFAULT_POINT_STYLE.copy().and(Styles.MARKER, Markers.ARROWHEAD));
        lineGraphic.setRenderer(pathRenderer);
        addGraphic(lineGraphic);        
    }

    @Override
    protected void pointsUpdated() {
        super.pointsUpdated();
        if (lineGraphic == null) {
            lineGraphic = new PrimitiveGraphic<>(null, Styles.DEFAULT_PATH_STYLE.copy(), null);
        }
        lineGraphic.setPrimitive(new Line2D.Double(start.getPrimitive(), end.getPrimitive()));
    }

    public AttributeSet getLineStyle() { 
        return lineGraphic.getStyle();
    }
    
    public void setLineStyle(AttributeSet s) { 
        lineGraphic.setStyle(s); 
    }

    public ArrowLocation getArrowLocation() {
        return arrowLoc;
    }
    
    private static void setArrow(PrimitiveGraphic gr, boolean val) {
        gr.getStyle().put(Styles.MARKER, val ? Markers.ARROWHEAD : null);
        gr.setStyleHint(StyleHints.HIDDEN_FUNCTIONAL_HINT, !val);
    }
    
    public void setArrowLocation(ArrowLocation arrowLoc) {
        this.arrowLoc = arrowLoc;
        setArrow(start, arrowLoc == ArrowLocation.BOTH || arrowLoc == ArrowLocation.START);
        setArrow(end, arrowLoc == ArrowLocation.BOTH || arrowLoc == ArrowLocation.END);
    }
    
}
