package com.googlecode.blaisemath.graph.app;

/*
 * #%L
 * BlaiseGraphTheory (v3)
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

import com.google.common.collect.Maps;
import com.googlecode.blaisemath.annotation.InvokedFromThread;
import com.googlecode.blaisemath.coordinate.CoordinateManager;
import com.googlecode.blaisemath.geom.Points;
import com.googlecode.blaisemath.graph.StaticGraphLayout;
import com.googlecode.blaisemath.graph.layout.GraphLayoutManager;
import com.googlecode.blaisemath.graphics.swing.JGraphicComponent;
import com.googlecode.blaisemath.util.swing.AnimationStep;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Map;

import static com.googlecode.blaisemath.graphics.swing.PanAndZoomHandler.getLocalBounds;
import static com.googlecode.blaisemath.graphics.swing.PanAndZoomHandler.setDesiredLocalBounds;

/**
 * Helps generate animations with coordinate manager and graph layout algorithm.
 * @author Elisha Peterson
 */
class AnimationUtils {

    /** Default number of steps to use in animating pan/zoom */
    private static final int ANIM_STEPS = 25;
    /** How long between animation steps */
    private static final int ANIM_DELAY_MILLIS = 5;

    /**
     * Animate change of node positions from current to new values.
     * @param <N> graph node type
     * @param <P> parameters type
     * @param gc graphic component, for coordinated zooming
     * @param glm layout manager
     * @param layout layout class
     * @param p layout parameters
     * @param margin margin for setting boundaries of graph component
     */
    public static <N,P> void animateCoordinateChange(GraphLayoutManager<N> glm, StaticGraphLayout<P> layout, P p,
                                                     @Nullable JGraphicComponent gc, double margin) {
        Map<N,Point2D.Double> newLocations = layout.layout(glm.getGraph(), glm.getNodeLocationCopy(), p);
        if (gc == null) {
            animateCoordinateChange(glm.getCoordinateManager(), newLocations);
        } else {
            animateAndZoomCoordinateChange(glm.getCoordinateManager(), newLocations, gc, margin);
        }
    }

    /**
     * Animate change of node positions from current to new values.
     * @param <S> type of source object
     * @param cm coordinate manager
     * @param locations new locations to animate to
     */
    private static <S> void animateCoordinateChange(final CoordinateManager<S, Point2D.Double> cm, final Map<S, Point2D.Double> locations) {
        final Map<S, Point2D.Double> oldLocations = cm.getLocationCopy(locations.keySet());
        AnimationStep.animate(0, ANIM_STEPS, ANIM_DELAY_MILLIS, new AnimationStep() {
            @Override
            @InvokedFromThread("AnimationStep")
            public void run(int idx, double pct) {
                Map<S, Point2D.Double> requestLocations = Maps.newHashMap();
                for (S s : locations.keySet()) {
                    Point2D.Double old = oldLocations.get(s);
                    Point2D.Double nue = locations.get(s);
                    requestLocations.put(s, old == null ? nue
                            : new Point2D.Double(old.x * (1 - pct) + nue.x * pct, old.y * (1 - pct) + nue.y * pct));
                }
                cm.setCoordinateMap(requestLocations);
            }
        });
    }

    /**
     * Animate change of node positions from current to new values, where the animation
     * is coordinated with setting the graphic component's bounds.
     * @param <S> type of source object
     * @param cm coordinate manager
     * @param locations new locations to animate to
     * @param gc graphic component, for coordinated zooming
     * @param margin margin for setting boundaries of graph component
     */
    private static <S> void animateAndZoomCoordinateChange(final CoordinateManager<S, Point2D.Double> cm, final Map<S, Point2D.Double> locations,
                                                           final JGraphicComponent gc, double margin) {
        if (locations.isEmpty()) {
            return;
        }

        Rectangle2D.Double oldBounds = getLocalBounds(gc);
        final double xMin = oldBounds.getMinX();
        final double yMin = oldBounds.getMinY();
        final double xMax = oldBounds.getMaxX();
        final double yMax = oldBounds.getMaxY();

        Rectangle2D.Double newBounds = Points.boundingBox(locations.values(), margin);
        assert newBounds != null;
        final double nxMin = newBounds.getMinX();
        final double nyMin = newBounds.getMinY();
        final double nxMax = newBounds.getMaxX();
        final double nyMax = newBounds.getMaxY();

        final Map<S, Point2D.Double> oldLocations = cm.getLocationCopy(locations.keySet());

        AnimationStep.animate(0, ANIM_STEPS, ANIM_DELAY_MILLIS, new AnimationStep(){
            @Override
            @InvokedFromThread("AnimationStep")
            public void run(int idx, double pct) {
                Map<S,Point2D.Double> requestLocations = Maps.newHashMap();
                for (S s : locations.keySet()) {
                    Point2D.Double old = oldLocations.get(s);
                    Point2D.Double nue = locations.get(s);
                    requestLocations.put(s, old == null ? nue
                        : new Point2D.Double(old.x*(1-pct)+nue.x*pct, old.y*(1-pct)+nue.y*pct));
                }
                cm.setCoordinateMap(requestLocations);

                double x1 = xMin + (nxMin - xMin) * pct;
                double y1 = yMin + (nyMin - yMin) * pct;
                double x2 = xMax + (nxMax - xMax) * pct;
                double y2 = yMax + (nyMax - yMax) * pct;
                setDesiredLocalBounds(gc, new Rectangle2D.Double(x1, y1, x2 - x1, y2 - y1));
            }
        });
    }

}
