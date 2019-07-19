package com.googlecode.blaisemath.graphics.swing;

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

import com.googlecode.blaisemath.geom.Markers;
import com.googlecode.blaisemath.graphics.core.PrimitiveGraphic;
import com.googlecode.blaisemath.graphics.swing.ArrowPathRenderer.ArrowLocation;
import com.googlecode.blaisemath.style.AttributeSet;
import com.googlecode.blaisemath.style.StyleHints;
import com.googlecode.blaisemath.style.Styles;
import java.awt.Color;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

/**
 * Displays a line segment between two points, with the possibility of adding
 * arrows on one or both ends.
 *
 * @author Elisha Peterson
 */
public class SegmentGraphic extends TwoPointGraphic {

    /** Entry with the line */
    protected PrimitiveGraphic lineGraphic;
    /** Where arrows are displayed */
    protected ArrowLocation arrowLoc;

    /** 
     * Construct segment between specified points
     * @param start start of segment
     * @param end end of segment
     * @param loc where to position arrows, relative to start and end
     */
    public SegmentGraphic(Point2D start, Point2D end, ArrowLocation loc) {
        super(start, end);
        setArrowLocation(loc);
    }

    @Override
    protected void initGraphics() {
        // ensure line is added first
        lineGraphic = JGraphics.path(new GeneralPath());
        addGraphic(lineGraphic);        
        super.initGraphics();
        
        start.setStyle(Styles.marker(Markers.CIRCLE, Color.black, 2));
        start.setStyleHint(StyleHints.HIDDEN_FUNCTIONAL_HINT, true);
        
        end.setStyle(Styles.DEFAULT_POINT_STYLE.copy().and(Styles.MARKER, Markers.ARROWHEAD));
    }

    @Override
    protected void pointsUpdated() {
        super.pointsUpdated();
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
