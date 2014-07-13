/**
 * BasicPointSetGraphic.java
 * Created Jan 22, 2011
 */
package com.googlecode.blaisemath.graphics;

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

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.googlecode.blaisemath.style.PointStyle;
import com.googlecode.blaisemath.coordinate.IndexedPointBean;
import com.googlecode.blaisemath.coordinate.PointFormatters;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Arrays;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A collection of points that are treated as a single graphic.
 * Customization is provided for tooltips and for dragging individual points,
 * but to customize any other attribute of graphics for individual points,
 * use {@link CustomPointSetGraphic} instead.
 * @see PointStyle
 * @see CustomPointSetGraphic
 *
 * @author Elisha Peterson
 */
public class BasicPointSetGraphic extends GraphicSupport implements IndexedPointBean<Point2D> {

    public static final String POINT_PROP = "point";
    
    /** The points that will be drawn. */
    protected Point2D[] points = new Point2D[0];
    /** The associated style (may be null). */
    @Nullable 
    protected PointStyle style = null;
    /** Optional delegate for tooltips */
    @Nullable 
    protected Function<Point2D, String> pointTipper = null;
    
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    //
    // CONSTRUCTORS
    //

    /**
     * Construct with no point (defaults to origin)
     */
    public BasicPointSetGraphic() {
        this(new Point2D[0], null);
    }

    /**
     * Construct with no style (will use the default)
     * @param p initial point
     */
    public BasicPointSetGraphic(Point2D[] p) {
        this(p, null);
    }

    /**
     * Construct with given primitive and style.
     * @param p initial point
     * @param style the style
     */
    public BasicPointSetGraphic(Point2D[] p, PointStyle style) {
        this.points = p.clone();
        this.style = style;
        IndexedPointBeanDragger dragger = new IndexedPointBeanDragger(this);
        addMouseListener(dragger);
        addMouseMotionListener(dragger);
    }

    @Override
    public String toString() {
        return "Point Set";
    }


    //<editor-fold defaultstate="collapsed" desc="PROPERTIES">
    //
    // PROPERTIES
    //

    public Point2D[] getPoint() { 
        return points; 
    }
    
    public void setPoint(Point2D[] p) {
        if (!Arrays.equals(points, p)) {
            Object old = points;
            points = p.clone();
            fireGraphicChanged();
            pcs.firePropertyChange(POINT_PROP, old, points);
        }
    }
    
    public Point2D getPoint(int i) {
        return points[i];
    }
    
    public void setPoint(int i, Point2D pt) {
        if (points[i] != pt) {
            Point2D old = points[i];
            points[i] = pt;
            fireGraphicChanged();
            pcs.fireIndexedPropertyChange(POINT_PROP, i, old, points[i]);
        }
    }

    public int getPointCount() {
        return points.length;
    }

    public int indexOf(Point2D nearby, Point2D start2) {
        PointStyle rend = drawStyle();
        synchronized(points) {
            for (int i = points.length-1; i >= 0; i--) {
                if (rend.markerShape(points[i]).contains(nearby)) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * Return the style for the point
     * @return style, or null if there is none
     */
    public PointStyle getStyle() {
        return style;
    }

    /**
     * Set the style for the point
     * @param style the style; may be null
     */
    public void setStyle(PointStyle style) {
        if (this.style != style) {
            this.style = style;
            fireGraphicChanged();
        }
    }

    public Function<Point2D, String> getPointTipDelegate() {
        return pointTipper;
    }

    public void setPointTipDelegate(Function<Point2D, String> pointTipper) {
        this.pointTipper = pointTipper;
    }

    //</editor-fold>


    //
    // GRAPHIC METHODS
    //

    public boolean contains(Point2D p) {
        return indexOf(p, p) != -1;
    }

    public boolean intersects(Rectangle2D box) {
        PointStyle drawer = drawStyle();
        synchronized (points) {
            for (Point2D p : points) {
                if (drawer.markerShape(p).intersects(box)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String getTooltip(Point2D p) {
        int i = indexOf(p, p);
        return i == -1 ? null : getPointTooltip(points[i]);
    }

    /**
     * Overridable method that generates the default tooltip on a point
     * @param pt the point
     * @return formatted location of the point
     */
    public String getPointTooltip(Point2D pt) {
        Preconditions.checkNotNull(pt);
        return pointTipper == null
                ? PointFormatters.formatPoint(pt, 1)
                : pointTipper.apply(pt);
    }


    //
    // DRAW METHODS
    //

    /** 
     * Return the actual style used for drawing.
     * @return style for drawing
     */
    @Nonnull 
    protected PointStyle drawStyle() {
        return parent.getPointStyle(style, this, styleHints);
    }

    @Override
    public void draw(Graphics2D canvas) {
        PointStyle drawer = drawStyle();
        synchronized (points) {
            for (Point2D p : points) {
                drawer.draw(p, canvas);
            }
        }
    }

    
    //<editor-fold defaultstate="collapsed" desc="PropertyChangeSupport METHODS">
    
    public final void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }
    
    public final void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }
    
    public final void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(propertyName, listener);
    }
    
    public final void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(propertyName, listener);
    }
    //</editor-fold>

    
}
