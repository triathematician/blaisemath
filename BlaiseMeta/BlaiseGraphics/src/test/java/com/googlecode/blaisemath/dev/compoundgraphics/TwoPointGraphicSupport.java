/*
 * TwoPointGraphicSupport.java
 * Created Oct 1, 2011
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
import com.googlecode.blaisemath.graphics.BasicPointGraphic;
import com.googlecode.blaisemath.graphics.Graphic;
import com.googlecode.blaisemath.graphics.GraphicComposite;
import com.googlecode.blaisemath.style.PointStyleBasic;
import com.googlecode.blaisemath.style.PointStyle;

/**
 * Provides methods for managing a graphic that depends on two underlying points,
 * e.g. a segment, arrow, etc. While this class can be instantiated and used,
 * it is intended mostly to provide a convenient superclass.
 * 
 * @author elisha
 */
public class TwoPointGraphicSupport extends GraphicComposite {

    /** Point at start of arrow */
    protected final BasicPointGraphic start;
    /** Point at end of arrow */
    protected final BasicPointGraphic end;

    /**
     * Construct graphic with specified base points
     * @param start starting point
     * @param end ending point
     */
    public TwoPointGraphicSupport(Point2D start, Point2D end) {
        this.start = new BasicPointGraphic(start);
        this.end = new BasicPointGraphic(end);
        
        initGraphics();
        pointsUpdated();
    }

    /**
     * Adds the points to the composite graphic. If subclasses desire to change
     * the draw order, they should override this method.
     */
    protected void initGraphics() {
        addGraphic(this.start);
        addGraphic(this.end);
    }

    //
    // UPDATING
    //
    
    /**
     * Updates the points. This should be called whenever the points change.
     * The functionality here computes and adjusts the angles at the points,
     * so that the points are directed away from each other, and then calls 
     * {@link GraphicComposite#fireGraphicChanged()}.
     */
    protected void pointsUpdated() {
        double angle = Math.atan2(end.getPoint().getY() - start.getPoint().getY(), end.getPoint().getX() - start.getPoint().getX());
        start.setAngle(angle+Math.PI);
        end.setAngle(angle);
        fireGraphicChanged();
    }

    //
    // PROPERTIES
    //
    
    public double getStartX() {
        return start.getPoint().getX();
    }
    
    public double getStartY() {
        return start.getPoint().getY();
    }
    
    public double getEndX() {
        return end.getPoint().getX();
    }
    
    public double getEndY() {
        return end.getPoint().getY();
    }
    
    public Point2D getStartPoint() {
        return start.getPoint();
    }

    public PointStyle getStartPointStyle() {
        return start.getStyle();
    }

    public void setEndPoint(Point2D p) {
        end.setPoint(p);
        pointsUpdated();
    }

    public void setEndPointStyle(PointStyleBasic r) {
        end.setStyle(r);
    }

    public void setStartPoint(Point2D p) {
        start.setPoint(p);
        pointsUpdated();
    }

    public void setStartPointStyle(PointStyleBasic r) {
        start.setStyle(r);
    }

    public Point2D getEndPoint() {
        return end.getPoint();
    }

    public PointStyle getEndPointStyle() {
        return end.getStyle();
    }
    
    //
    // EVENT HANDLING
    //

    @Override
    public void graphicChanged(Graphic source) {
        if (source == start || source == end) {
            pointsUpdated();
        } else {
            super.graphicChanged(source);
        }
    }
}
