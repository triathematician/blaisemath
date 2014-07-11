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

import com.google.common.base.Function;
import com.google.common.base.Objects;
import java.awt.geom.Point2D;
import java.beans.IndexedPropertyChangeEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import com.googlecode.blaisemath.graphics.BasicPointSetGraphic;
import com.googlecode.blaisemath.style.PointStyle;
import com.googlecode.blaisemath.util.PointFormatters;

/**
 * An entry for a draggable point at an arbitrary local coordinate.
 *
 * @param <C> local coordinate type
 * @author Elisha
 */
public class VBasicPointSet<C> extends VGraphicSupport<C> {

    /** The points */
    protected C[] point;
    /** The window entry */
    protected final BasicPointSetGraphic window = new BasicPointSetGraphic();
    
    /** Provides tooltips for each point */
    private final VisTipDelegate<C> tipper = new VisTipDelegate<C>();
    
    /** Represents a point in the array whose value needs to be updated */
    protected int newPointIndex = -1;
    /** Represents the updated value that a point should have */
    protected Point2D newPointValue = null;

    /** 
     * Construct the point set
     * @param initialPoint initial set of points
     */
    public VBasicPointSet(C[] initialPoint) {
        this.point = initialPoint.clone();
        
        window.setPointTipDelegate(tipper);
        
        // listen for changes to the window graphic, e.g. user moved a point
        window.addPropertyChangeListener(new PropertyChangeListener(){
            public void propertyChange(PropertyChangeEvent evt) {
                handleWindowPropertyChange(evt);
            }
        });
    }
    
    private synchronized void handleWindowPropertyChange(PropertyChangeEvent evt) {
        if (evt instanceof IndexedPropertyChangeEvent) {
            IndexedPropertyChangeEvent ipce = (IndexedPropertyChangeEvent) evt;
            newPointValue = (Point2D) ipce.getNewValue();
            newPointIndex = ipce.getIndex();
            setUnconverted(true);
        }
    }

    //
    // PROPERTIES
    //

    public BasicPointSetGraphic getWindowGraphic() {
        return window;
    }

    public PointStyle getStyle() {
        return window.getStyle();
    }

    public void setStyle(PointStyle rend) {
        window.setStyle(rend);
    }

    //
    // DraggablePointBean PROPERTIES
    //

    public synchronized C getPoint(int i) {
        return point[i];
    }

    public synchronized void setPoint(int i, C point) {
        if (!(Objects.equal(this.point[i], point))) {
            this.point[i] = point;
            setUnconverted(true);
        }
    }

    public synchronized C[] getPoint() {
        return point.clone();
    }

    public synchronized void setPoint(C[] point) {
        this.point = point.clone();
        setUnconverted(true);
    }

    //
    // CONVERSION
    //

    public synchronized void convert(final Visometry<C> vis, VisometryProcessor<C> processor) {
        if (newPointValue != null) {
            C loc = vis.toLocal(newPointValue);
            if (newPointIndex >= 0 && newPointIndex < point.length) {
                point[newPointIndex] = loc;
            }
        } else {
            Point2D[] p = processor.convertToArray(point, vis);
            window.setPoint(p);
        }
        newPointValue = null;
        newPointIndex = -1;
        tipper.vis = vis;
        setUnconverted(false);
    }

    
    //<editor-fold defaultstate="collapsed" desc="INNER CLASSES">
    
    /** Converts a point to a tip string */
    public static class VisTipDelegate<C> implements Function<Point2D,String> {
        Visometry<C> vis = null;
        
        public String apply(Point2D src) {
            C local = vis.toLocal(src);
            return local instanceof Point2D 
                    ? PointFormatters.formatPoint((Point2D) local, 2)
                    : local + "";
        }
    }
    
    //</editor-fold>

}
