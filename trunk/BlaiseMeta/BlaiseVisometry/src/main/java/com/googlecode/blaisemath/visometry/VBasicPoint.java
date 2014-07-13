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
import java.awt.geom.Point2D;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import com.googlecode.blaisemath.graphics.BasicPointGraphic;
import com.googlecode.blaisemath.style.PointStyle;
import com.googlecode.blaisemath.coordinate.PointFormatters;

/**
 * An entry for a draggable point at an arbitrary local coordinate.
 *
 * @param <C> local coordinate type
 * @author Elisha
 */
public class VBasicPoint<C> extends VGraphicSupport<C> {

    /** The point */
    protected C point;
    /** The windowGraphic entry */
    protected final BasicPointGraphic windowGraphic = new BasicPointGraphic();

    /**
     * Initialize point
     * @param initialPoint point's local coords
     */
    public VBasicPoint(final C initialPoint) {
        this.point = initialPoint;
        windowGraphic.addChangeListener(new ChangeListener(){
            public synchronized void stateChanged(ChangeEvent e) {
                setPoint(parent.getVisometry().toLocal(windowGraphic.getPoint()));
                windowGraphic.setDefaultTooltip(
                        point instanceof Point2D ? PointFormatters.formatPoint((Point2D) point, 2)
                        : point + "");
            }
        });
    }

    //
    // PROPERTIES
    //

    public final synchronized BasicPointGraphic getWindowGraphic() {
        return windowGraphic;
    }

    public final synchronized C getPoint() {
        return point;
    }

    public final synchronized void setPoint(C point) {
        if (!Objects.equal(this.point, point)) {
            this.point = point;
            setUnconverted(true);
        }
    }

    public final PointStyle getStyle() {
        return windowGraphic.getStyle();
    }

    public final void setStyle(PointStyle rend) {
        windowGraphic.setStyle(rend);
    }

    //
    // CONVERSION
    //


    public synchronized void convert(Visometry<C> vis, VisometryProcessor<C> processor) {
        windowGraphic.setPoint(processor.convert(point, vis));
        windowGraphic.setDefaultTooltip(
                point instanceof Point2D ? PointFormatters.formatPoint((Point2D) point, 2)
                : point + "");
        setUnconverted(false);
    }

}
