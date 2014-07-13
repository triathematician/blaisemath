/*
 * PointStyleLabeled.java
 * Created Oct 5, 2011
 */
package com.googlecode.blaisemath.style;

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

import static com.google.common.base.Preconditions.checkNotNull;
import com.googlecode.blaisemath.coordinate.PointFormatters;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Point2D;
import javax.annotation.Nullable;

/**
 * Adds a label to a point style. The points' style and labels' style can
 * be manipulated separately. Label visibility may be turned on and off.
 *
 * @author elisha
 */
public class PointStyleLabeled implements PointStyle {

    /** Base style */
    @Nullable 
    protected PointStyle base = new PointStyleBasic();
    /** Label style */
    protected TextStyle labelStyle = new TextStyleBasic();
    /** Whether labels are visible */
    protected boolean showLabel = true;

    /**
     * Construct with default styles
     */
    public PointStyleLabeled() {
    }
    
    /**
     * Construct with base style.
     * @param baseStyle base style to use for point
     */
    public PointStyleLabeled(PointStyle baseStyle) {
        setBaseStyle(baseStyle);
    }

    @Override
    public String toString() {
        return String.format("PointStyleLabeled[baseStyle=%s, labelStyle=%s, showLabel=%s]", 
                base, labelStyle, showLabel);
    }
    
    
    //<editor-fold defaultstate="collapsed" desc="BUILDER PATTERNS">

    /** 
     * Sets base style & returns point to this object
     * @param base
     * @return 
     */
    public PointStyleLabeled baseStyle(PointStyle base) {
        setBaseStyle(base);
        return this;
    }

    /** 
     * Sets label style & returns pointer to object
     * @param labelStyle
     * @return 
     */
    public PointStyleLabeled labelStyle(TextStyle labelStyle) {
        setLabelStyle(labelStyle);
        return this;
    }

    /** 
     * Sets whether label is shown & returns pointer to object
     * @param showLabel
     * @return 
     */
    public PointStyleLabeled showLabel(boolean showLabel) {
        setShowLabel(showLabel);
        return this;
    }

    // </editor-fold>


    
    //<editor-fold defaultstate="collapsed" desc="PROPERTY PATTERNS">
    //
    // PROPERTY PATTERNS
    //

    public PointStyle getBaseStyle() {
        return base;
    }

    public final void setBaseStyle(PointStyle base) {
        this.base = checkNotNull(base);
    }

    public TextStyle getLabelStyle() {
        return labelStyle;
    }

    public void setLabelStyle(TextStyle labelStyle) {
        this.labelStyle = checkNotNull(labelStyle);
    }

    public boolean isShowLabel() {
        return showLabel;
    }

    public void setShowLabel(boolean showLabel) {
        this.showLabel = showLabel;
    }
    
    //</editor-fold>

    
    //
    // PointStyle METHODS
    //

    @Override
    public float getMarkerRadius() {
        return base.getMarkerRadius();
    }

    @Override
    public Shape markerShape(Point2D point) {
        return base.markerShape(point);
    }

    @Override
    public Shape markerShape(Point2D p, double angle) {
        return base.markerShape(p, angle);
    }

    /**
     * Draws specified point on the graphics canvas with visibility options
     * @param p the point to draw
     * @param label the point's label
     * @param canvas graphics element to draw on
     */
    public void draw(Point2D p, String label, Graphics2D canvas) {
        base.draw(p, canvas);
        if (showLabel) {
            ((TextStyleBasic)labelStyle).offset(base.getMarkerRadius());
            labelStyle.draw(p, label, canvas);
        }
    }

    /**
     * Draws specified point on the graphics canvas with visibility options
     * @param p the point to draw
     * @param angle orientation of the point
     * @param label the point's label
     * @param canvas graphics element to draw on
     * @param hints visibility & highlight settings
     */
    public void draw(Point2D p, double angle, String label, Graphics2D canvas) {
        base.draw(p, angle, canvas);
        if (showLabel) {
            labelStyle.draw(p, label, canvas);
        }
    }

    @Override
    public void draw(Point2D p, Graphics2D canvas) {
        draw(p, PointFormatters.formatPoint(p, 2), canvas);
    }

    @Override
    public void draw(Point2D p, double angle, Graphics2D canvas) {
        draw(p, angle, PointFormatters.formatPoint(p, 2), canvas);
    }
}
