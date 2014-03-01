/*
 * PointStyleBasic.java
 * Created Jan 22, 2011
 */

package org.blaise.style;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2009 - 2014 Elisha Peterson
 * --
 * Licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
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
import java.awt.Color;
import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Draws a point on the graphics canvas, using a {@link Marker} object and a {@link ShapeStyle}.
 * See also the <a href="http://www.w3.org/TR/SVG/painting.html#Markers">related SVG documentation</a> on markers.
 * 
 * @author Elisha Peterson
 */
public class PointStyleBasic extends PointStyleSupport {

    /** Style of the point */
    protected Marker marker = new Markers.CircleShape();
    /** Radius of the displayed point */
    protected float markerRadius = 4;
    /** Delegate style used to draw the point */
    protected ShapeStyleBasic shapeStyle = new ShapeStyleBasic().fill(Color.lightGray).stroke(Color.darkGray);


    /** Construct instance with default settings */
    public PointStyleBasic() { 
    }

    @Override
    public String toString() {
        return String.format("BasicPointStyle[marker=%s, markerRadius=%.1f, fill=%s, stroke=%s, strokeWidth=%.1f]", 
                marker, markerRadius, shapeStyle.fill, shapeStyle.stroke, shapeStyle.strokeWidth);
    }
    
    //<editor-fold defaultstate="collapsed" desc="BUILDER PATTERNS">

    /** Sets shape & returns pointer to object */
    public PointStyleBasic marker(Marker s) {
        setMarker(s);
        return this;
    }

    /** Sets radius & returns pointer to object */
    public PointStyleBasic markerRadius(float radius) {
        setMarkerRadius(radius);
        return this;
    }

    /** Sets fill color & returns pointer to object */
    public PointStyleBasic fill(Color c) {
        setFill(c);
        return this;
    }

    /** Sets stroke color & returns pointer to object */
    public PointStyleBasic stroke(Color c) {
        setStroke(c);
        return this;
    }

    /** Sets strokeWidth & returns pointer to object */
    public PointStyleBasic strokeWidth(float thick) {
        setStrokeWidth(thick);
        return this;
    }

    // </editor-fold>

    
    //<editor-fold defaultstate="collapsed" desc="PROPERTY PATTERNS">
    //
    // PROPERTY PATTERNS
    //

    public Marker getMarker() {
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = checkNotNull(marker);
    }

    public float getMarkerRadius() {
        return markerRadius;
    }

    public final void setMarkerRadius(float r) {
        markerRadius = Math.max(r, 1);
    }

    @Nullable 
    public Color getFill() {
        return shapeStyle.getFill();
    }
    
    public void setFill(@Nullable Color fill) {
        shapeStyle.setFill(fill);
    }

    @Nullable 
    public Color getStroke() {
        return shapeStyle.getStroke();
    }

    public void setStroke(@Nullable Color stroke) {
        shapeStyle.setStroke(stroke);
    }

    public float getStrokeWidth() {
        return shapeStyle.getStrokeWidth();
    }

    public void setStrokeWidth(float thickness) {
        shapeStyle.setStrokeWidth(thickness);
    }
    
    protected ShapeStyleBasic getShapeStyle() {
        return shapeStyle;
    }
    
    protected void setShapeStyle(ShapeStyleBasic r) {
        this.shapeStyle = checkNotNull(r);
    }
    
    // </editor-fold>

}
