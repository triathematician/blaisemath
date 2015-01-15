/**
 * PlanePlotComponent.java
 * Created on Jul 30, 2009
 */

package com.googlecode.blaisemath.visometry.plane;

/*
 * #%L
 * BlaiseVisometry
 * --
 * Copyright (C) 2009 - 2015 Elisha Peterson
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
import com.googlecode.blaisemath.visometry.VGraphicComponent;

/**
 * <p>
 *   <code>PlanePlotComponent</code> is a <code>VGraphicComponent</code> for a
 *   two-dimensional Euclidean plane.
 * </p>
 *
 * @author Elisha Peterson
 */
public class PlanePlotComponent extends VGraphicComponent<Point2D.Double> {

    /** Construct */
    public PlanePlotComponent() {
        super(new PlaneVisometry());

        PlaneVisometry pv = (PlaneVisometry) getVisometry();

        PlanePlotMouseHandler mouseListener = new PlanePlotMouseHandler(pv);
        addMouseListener(mouseListener);
        addMouseMotionListener(mouseListener);
        addMouseWheelListener(mouseListener);
        overlays.add(mouseListener);

        setPreferredSize(new java.awt.Dimension(400, 400));
    }

    //
    // DELEGATE METHODS
    //

    /**
     * Set desired range of values to display.
     * Recomputes transformation after setting.
     * @param min1 first coordinate min
     * @param min2 second coordinate min
     * @param max1 first coordinate max
     * @param max2 second coordinate max
     */
    public void setDesiredRange(double min1, double min2, double max1, double max2) {
        ((PlaneVisometry) getVisometry()).setDesiredRange(min1, min2, max1, max2);
    }

    /**
     * Sets aspect ratio of plot
     * Recomputes transformation after setting
     * @param ratio new aspect ratio
     */
    public void setAspectRatio(double ratio) {
        ((PlaneVisometry) getVisometry()).setAspectRatio(ratio);
    }
}
