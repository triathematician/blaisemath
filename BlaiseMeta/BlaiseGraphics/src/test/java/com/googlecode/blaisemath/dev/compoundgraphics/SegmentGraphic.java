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

import java.awt.Color;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import com.googlecode.blaisemath.graphics.BasicShapeGraphic;
import com.googlecode.blaisemath.style.PointStyleBasic;
import com.googlecode.blaisemath.style.Markers;
import com.googlecode.blaisemath.style.ShapeStyle;
import com.googlecode.blaisemath.style.context.StyleHintSet;

/**
 * Displays a segment between two points.
 * By default displays an arrowhead at the end, and allows the endpoints of the arrow to be moved.
 *
 * @author Elisha
 */
public class SegmentGraphic extends TwoPointGraphicSupport {

    /** Entry with the line */
    protected BasicShapeGraphic lineEntry;

    /** Construct segment between specified points */
    public SegmentGraphic(Point2D start, Point2D end) {
        super(start, end);
    }

    @Override
    protected void initGraphics() {
        // ensure line is added first
        addGraphic(lineEntry = new BasicShapeGraphic(new GeneralPath()));        
        super.initGraphics();
        
        start.setStyle(new PointStyleBasic()
                .marker(Markers.CIRCLE)
                .stroke(null)
                .markerRadius(2)
                .fill(Color.black));
        start.setStyleHint(StyleHintSet.HIDDEN_HINT, true);
        
        end.setStyle(new PointStyleBasic()
                .marker(Markers.ARROWHEAD));
    }

    @Override
    protected void pointsUpdated() {
        double angle = Math.atan2(end.getPoint().getY() - start.getPoint().getY(), end.getPoint().getX() - start.getPoint().getX());
        start.setAngle(angle+Math.PI);
        end.setAngle(angle);
        lineEntry.setPrimitive(new Line2D.Double(start.getPoint(), end.getPoint()));
    }

    public ShapeStyle getLineStyle() { 
        return lineEntry.getStyle();
    }
    
    public void setLineStyle(ShapeStyle r) { 
        lineEntry.setStyle(r); 
    }
    
}
