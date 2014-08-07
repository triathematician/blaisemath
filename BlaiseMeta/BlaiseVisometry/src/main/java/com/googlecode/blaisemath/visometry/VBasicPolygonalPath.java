/**
 * VBasicPolygonalPath.java
 * Created Jan 29, 2011
 */
package com.googlecode.blaisemath.visometry;

/*
 * #%L
 * BlaiseVisometry
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

import com.google.common.base.Objects;
import com.googlecode.blaisemath.graphics.core.PrimitiveGraphic;
import com.googlecode.blaisemath.style.AttributeSet;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.util.Arrays;

/**
 * An entry for a polygonal path, drawn using a stroke renderer.
 *
 * @author Elisha
 */
public class VBasicPolygonalPath<C,G> extends VGraphicSupport<C,G> {

    /** Contains the local points */
    private C[] pathCoordinates;
    /** The drawn path */
    private final PrimitiveGraphic<Shape,G> window = new PrimitiveGraphic<Shape,G>();

    /** Construct with specified bean to handle dragging (may be null) */
    public VBasicPolygonalPath(C[] local) {
        this.pathCoordinates = local.clone();
    }
    
    //<editor-fold defaultstate="collapsed" desc="PROPERTY PATTERNS">
    //
    // PROPERTY PATTERNS
    //

    public PrimitiveGraphic<Shape,G> getWindowGraphic() {
        return window;
    }

    public synchronized C getPoint(int i) {
        return pathCoordinates[i];
    }

    public synchronized void setPoint(int i, C point) {
        if (!(Objects.equal(point, pathCoordinates[i]))) {
            this.pathCoordinates[i] = point;
            setUnconverted(true);
        }
    }

    public synchronized C[] getPoint() {
        return pathCoordinates.clone();
    }

    public synchronized void setPoint(C[] point) {
        this.pathCoordinates = point.clone();
        setUnconverted(true);
    }

    public AttributeSet getPathStyle() {
        return (AttributeSet) window.getStyle();
    }

    public void setPathStyle(AttributeSet rend) {
        window.setStyle(rend);
    }
    
    //</editor-fold>
    

    public void convert(Visometry<C> vis, VisometryProcessor<C> processor) {
        GeneralPath gp = processor.convertToPath(Arrays.asList(pathCoordinates), vis);
        window.setPrimitive(gp);
        window.setDefaultTooltip("Polygonal path");
        setUnconverted(false);
    }
}
