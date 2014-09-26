/*
 * LabeledPointGraphic.java
 * Created Jan 22, 2011
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

import java.awt.geom.Point2D;
import com.googlecode.blaisemath.graphics.core.Graphic;
import com.googlecode.blaisemath.graphics.core.GraphicComposite;
import com.googlecode.blaisemath.graphics.core.PrimitiveGraphic;
import com.googlecode.blaisemath.graphics.swing.JGraphics;
import com.googlecode.blaisemath.style.AttributeSet;
import com.googlecode.blaisemath.util.coordinate.CoordinateBean;
import com.googlecode.blaisemath.util.geom.AnchoredText;
import java.awt.Graphics2D;

/**
 * Displays a point together with a label.
 *
 * @author Elisha
 */
public class LabeledPointGraphic extends GraphicComposite<Graphics2D>
        implements CoordinateBean<Point2D> {

    /** Stores the point */
    private final PrimitiveGraphic<Point2D,Graphics2D> point;
    /** Stores the string */
    private final PrimitiveGraphic<AnchoredText,Graphics2D> label;

    //
    // CONSTRUCTORS
    //

    /** Construct labeled point with given primitive and default style */
    public LabeledPointGraphic(Point2D p, String s) { 
        point = JGraphics.point(p);
        label = JGraphics.text(new AnchoredText(p, s));
        label.setMouseEnabled(false);
        addGraphic(point);
        addGraphic(label);
    }

    
    //
    // PROPERTIES
    //

    public Point2D getPoint() { 
        return point.getPrimitive();
    }

    public void setPoint(Point2D p) {
        point.setPrimitive(p);
    }

    public AttributeSet getPointStyle() {
        return point.getStyle();
    }

    public void setPointStyle(AttributeSet style) {
        point.setStyle(style);
    }

    public String getLabel() {
        return label.getPrimitive().getText();
    }

    public void setLabel(String s) {
        label.setPrimitive(new AnchoredText(getPoint(), s));
        fireGraphicChanged();
    }

    public AttributeSet getLabelStyle() {
        return label.getStyle();
    }

    public void setLabelStyle(AttributeSet style) {
        label.setStyle(style);
    }

    //
    // EVENT HANDLING
    //
    
    public boolean isDragEnabled() {
        return point.isDragEnabled();
    }

    public void setDragEnabled(boolean val) {
        point.setDragEnabled(val);
    }

    @Override
    public void graphicChanged(Graphic source) {
        if (source == point) {
            label.setPrimitive(new AnchoredText(getPoint(), label.getPrimitive().getText()));
        }
        super.graphicChanged(source);
    }

    
    
    @Override
    public boolean contains(Point2D p) {
        // override to prevent dragging and tips on the string
        return this.point.contains(p);
   }    
    
    
}
