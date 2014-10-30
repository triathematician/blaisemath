/**
 * VBasicPoint.java
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
import com.googlecode.blaisemath.util.Points;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * An entry for a draggable point at an arbitrary local coordinate.
 *
 * @param <C> local coordinate type
 * @author Elisha
 */
public class VBasicPoint<C,G> extends VGraphicSupport<C,G> {

    /** The point */
    protected C point;
    /** The windowGraphic entry */
    protected final PrimitiveGraphic<Point2D,G> windowGraphic = new PrimitiveGraphic<Point2D,G>();

    /**
     * Initialize point
     * @param initialPoint point's local coords
     */
    public VBasicPoint(final C initialPoint) {
        this.point = initialPoint;
        windowGraphic.addPropertyChangeListener(PrimitiveGraphic.PRIMITIVE_PROP,
            new PropertyChangeListener(){
                public void propertyChange(PropertyChangeEvent evt) {
                    setPoint(parent.getVisometry().toLocal(windowGraphic.getPrimitive()));
                    windowGraphic.setDefaultTooltip(
                            point instanceof Point2D ? Points.formatPoint((Point2D) point, 2)
                            : point + "");
                }
            }
        );
    }

    //
    // PROPERTIES
    //

    public final PrimitiveGraphic getWindowGraphic() {
        return windowGraphic;
    }

    public final C getPoint() {
        return point;
    }

    public final void setPoint(C point) {
        if (!Objects.equal(this.point, point)) {
            this.point = point;
            setUnconverted(true);
        }
    }

    public final AttributeSet getStyle() {
        return windowGraphic.getStyle();
    }

    public final void setStyle(AttributeSet sty) {
        windowGraphic.setStyle(sty);
    }

    //
    // CONVERSION
    //


    public void convert(Visometry<C> vis, VisometryProcessor<C> processor) {
        windowGraphic.setPrimitive(processor.convert(point, vis));
        windowGraphic.setDragEnabled(true);
        windowGraphic.setDefaultTooltip(
                point instanceof Point2D ? Points.formatPoint((Point2D) point, 2)
                : point + "");
        setUnconverted(false);
    }

}
