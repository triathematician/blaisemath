package com.googlecode.blaisemath.graphics.core;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2009 - 2021 Elisha Peterson
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

import com.google.common.base.Preconditions;
import com.googlecode.blaisemath.style.AttributeSet;
import com.googlecode.blaisemath.style.Renderer;
import com.googlecode.blaisemath.util.geom.Points;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.function.Function;

/**
 * A collection of points that are treated as a single graphic.
 * Customization is provided for tooltips and for dragging individual points,
 * but to customize any other attribute of graphics for individual points,
 * use {@link DelegatingPointSetGraphic} instead.
 *
 * @author Elisha Peterson
 */
public class BasicPointSetGraphic<G> extends PrimitiveArrayGraphic<Point2D,G> {

    public static final String P_POINT = "point";
    
    /** Optional delegate for tooltips */
    protected @Nullable Function<Point2D, String> pointTipper = null;

    /**
     * Construct with no point (defaults to origin)
     */
    public BasicPointSetGraphic() {
        this(new Point2D[0], null, null);
    }

    /**
     * Construct with no style (will use the default)
     * @param p initial point
     */
    public BasicPointSetGraphic(Point2D[] p) {
        this(p, null, null);
    }

    /**
     * Construct with given primitive and style.
     * @param p initial point
     * @param style the style
     * @param rend renders the points
     */
    public BasicPointSetGraphic(Point2D[] p, AttributeSet style, Renderer<Point2D,G> rend) {
        super(p, style, rend);
        IndexedPointMover dragger = new IndexedPointMover();
        addMouseListener(dragger);
        addMouseMotionListener(dragger);
    }

    @Override
    public String toString() {
        return "Point Set";
    }

    //region PROPERTIES

    @Override
    public void setPrimitive(Point2D[] p) {
        if (!Arrays.equals(primitive, p)) {
            Object old = this.primitive;
            this.primitive = p.clone();
            fireGraphicChanged();
            pcs.firePropertyChange(P_POINT, old, this.primitive);
        }
    }
    
    public Point2D getPoint(int i) {
        return primitive[i];
    }
    
    public void setPoint(int i, Point2D pt) {
        if (primitive[i] != pt) {
            Point2D old = primitive[i];
            primitive[i] = pt;
            fireGraphicChanged();
            pcs.fireIndexedPropertyChange(P_POINT, i, old, primitive[i]);
        }
    }

    public int getPointCount() {
        return primitive.length;
    }

    public Function<Point2D, @Nullable String> getPointTipDelegate() {
        return pointTipper;
    }

    public void setPointTipDelegate(Function<Point2D, @Nullable String> pointTipper) {
        this.pointTipper = pointTipper;
    }

    //endregion

    @Override
    public String getTooltip(Point2D p, G canvas) {
        int i = indexOf(p, canvas);
        return i == -1 ? null : getPointTooltip(primitive[i]);
    }

    /**
     * Overridable method that generates the default tooltip on a point
     * @param pt the point
     * @return formatted location of the point
     */
    public String getPointTooltip(Point2D pt) {
        Preconditions.checkNotNull(pt);
        return pointTipper == null
                ? Points.format(pt, 1)
                : pointTipper.apply(pt);
    }
    
    //region INNER CLASSES

    /** Handles dragging of individual points */
    public class IndexedPointMover extends GMouseDragHandler {

        /** Index of point being dragged */
        private int indexStart;
        /** Location at start of drag */
        private Point2D beanStart;

        @Override
        public void mouseDragInitiated(GMouseEvent e, Point2D start) {
            // TODO - get canvas reference from somewhere??
            indexStart = indexOf(start, null);
            if (indexStart != -1) {
                beanStart = getPrimitive(indexStart);
            }
        }

        @Override
        public void mouseDragInProgress(GMouseEvent e, Point2D start) {
            if (indexStart != -1) {
                Point2D dragPos = e.getGraphicLocation();
                Point2D nueLoc = new Point2D.Double(beanStart.getX() + dragPos.getX() - start.getX(),
                        beanStart.getY() + dragPos.getY() - start.getY());
                setPrimitive(indexStart, nueLoc);
            }
        }

        @Override
        public void mouseDragCompleted(GMouseEvent e, Point2D start) {
            beanStart = null;
            indexStart = -1;
        }

    }
    
    //endregion
    
}
