package com.googlecode.blaisemath.graphics.impl;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2009 - 2022 Elisha Peterson
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

import com.googlecode.blaisemath.graphics.Graphic;
import com.googlecode.blaisemath.graphics.GraphicComposite;
import com.googlecode.blaisemath.graphics.PrimitiveGraphic;
import com.googlecode.blaisemath.style.Styles;
import com.googlecode.blaisemath.coordinate.OrientedPoint2D;
import com.googlecode.blaisemath.graphics.Renderer;
import java.awt.geom.Point2D;

/**
 * Provides methods for managing a graphic that depends on two underlying points,
 * e.g. a segment, arrow, etc. While this class can be instantiated and used,
 * it is intended mostly to provide a convenient superclass.
 * 
 * @param <G> graphics canvas type
 * 
 * @author Elisha Peterson
 */
public class TwoPointGraphic<G> extends GraphicComposite<G> {

    /** Point at start of arrow */
    protected final PrimitiveGraphic<Point2D, G> start;
    /** Point at end of arrow */
    protected final PrimitiveGraphic<Point2D, G> end;

    /**
     * Construct graphic with specified base points
     * @param start starting point
     * @param end ending point
     * @param renderer renderer for points
     */
    public TwoPointGraphic(Point2D start, Point2D end, Renderer<Point2D, G> renderer) {
        style = Styles.DEFAULT_POINT_STYLE.copy();
        this.start = new PrimitiveGraphic<>(new OrientedPoint2D(start), style, renderer);
        this.end = new PrimitiveGraphic<>(new OrientedPoint2D(end), style, renderer);
        
        addGraphic(this.start);
        addGraphic(this.end);
        pointsUpdated();
    }

    public PrimitiveGraphic<Point2D, G> getStartGraphic() {
        return start;
    }

    public PrimitiveGraphic<Point2D, G> getEndGraphic() {
        return end;
    }

    /**
     * Updates the points. This should be called whenever the points change.
     * The functionality here computes and adjusts the angles at the points,
     * so that the points are directed away from each other, and then calls 
     * {@link GraphicComposite#fireGraphicChanged()}.
     */
    protected void pointsUpdated() {
        if (!(start.getPrimitive() instanceof OrientedPoint2D)) {
            start.setPrimitive(new OrientedPoint2D(start.getPrimitive()));
        }
        if (!(end.getPrimitive() instanceof OrientedPoint2D)) {
            end.setPrimitive(new OrientedPoint2D(end.getPrimitive()));
        }

        ((OrientedPoint2D) start.getPrimitive()).awayFrom(end.getPrimitive());
        ((OrientedPoint2D) end.getPrimitive()).awayFrom(start.getPrimitive());
        fireGraphicChanged();
    }

    //region EVENTS

    public boolean isDragEnabled() {
        return start.isDragEnabled() && end.isDragEnabled();
    }
    
    public void setDragEnabled(boolean val) {
        start.setDragEnabled(val);
        end.setDragEnabled(val);
    }

    @Override
    public void graphicChanged(Graphic source) {
        if (source == start || source == end) {
            pointsUpdated();
        } else {
            super.graphicChanged(source);
        }
    }

    //endregion
}
