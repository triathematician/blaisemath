package com.googlecode.blaisemath.graph.layout;

/*
 * #%L
 * BlaiseGraphTheory
 * --
 * Copyright (C) 2009 - 2025 Elisha Peterson
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
import com.google.common.graph.Graph;
import com.googlecode.blaisemath.graph.StaticGraphLayout;
import com.googlecode.blaisemath.graph.layout.RandomBoxLayout.BoxLayoutParameters;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Map;
import java.util.Random;

/**
 * Position nodes at random locations in a box.
 *
 * @author Elisha Peterson
 */
public class RandomBoxLayout implements StaticGraphLayout<BoxLayoutParameters> {

    private static final RandomBoxLayout INST = new RandomBoxLayout();

    public static RandomBoxLayout getInstance() {
        return INST;
    }
    
    @Override
    public String toString() {
        return "Position nodes randomly in a rectangle";
    }

    @Override
    public BoxLayoutParameters createParameters() {
        return new BoxLayoutParameters();
    }
    
    @Override
    public <N> Map<N, Point2D.Double> layout(Graph<N> g, @Nullable Map<N, Point2D.Double> ic, BoxLayoutParameters parameters) {
        Random r = new Random();
        Map<N, Point2D.Double> result = Maps.newHashMap();
        double minX = parameters.getBounds().getMinX();
        double minY = parameters.getBounds().getMinY();
        double maxX = parameters.getBounds().getMaxX();
        double maxY = parameters.getBounds().getMaxY();
        for (N v : g.nodes()) {
            double x = r.nextDouble();
            double y = r.nextDouble();
            result.put(v, new Point2D.Double(x * minX + (1 - x) * maxX, y * minY + (1 - y) * maxY));
        }
        return result;
    }
    
    //region INNER CLASSES
    
    /** Parameters associated with circle layout */
    public static class BoxLayoutParameters {
        private Rectangle2D.Double bounds = new Rectangle2D.Double(-100, -100, 200, 200);

        public BoxLayoutParameters() {
        }

        public BoxLayoutParameters(Rectangle2D.Double bounds) {
            this.bounds = bounds;
        }
        
        public Rectangle2D.Double getBounds() {
            return bounds;
        }

        public void setBounds(Rectangle2D.Double bounds) {
            this.bounds = bounds;
        }
    }
    
    //endregion
    
}
