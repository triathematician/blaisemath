/*
 * WeightedEdgeStyler.java
 * Created Sep 18, 2011
 */
package com.googlecode.blaisemath.graph.view;

/*
 * #%L
 * BlaiseGraphTheory
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

import com.google.common.base.Function;
import java.awt.Color;
import java.util.Map;
import com.googlecode.blaisemath.util.Edge;
import com.googlecode.blaisemath.style.PathStyleDecorated;
import com.googlecode.blaisemath.style.PathStyle;

/**
 * Provides a default node customizer suitable for a weighted-edge graph.
 *
 * @author elisha
 */
public class WeightedEdgeStyler implements Function<Edge, PathStyle> {

    /** Parent style */
    protected final PathStyle parent;
    /** Edge weights */
    protected Map<? extends Edge, Float> weights;
    /** The maximum edge weight */
    protected transient float maxWeight = 0f;

    /**
     * Construct the customizer
     *
     * @param parent the parent style
     * @param weights weightings for edges in graph
     */
    public <V> WeightedEdgeStyler(PathStyle parent, Map<Edge<V>, Float> weights) {
        this.parent = parent;
        this.weights = weights;
        for (Float wt : weights.values()) {
            maxWeight = Math.max(maxWeight, wt);
        }
    }

    public synchronized Map<? extends Edge, Float> getWeights() {
        return weights;
    }

    public synchronized void setWeights(Map<? extends Edge, Float> weights) {
        if (this.weights != weights) {
            this.weights = weights;
            for (Float wt : weights.values()) {
                maxWeight = Math.max(maxWeight, wt);
            }
        }
    }

    public synchronized PathStyle apply(Edge o) {
        Object wt = weights.get(o);
        if (wt instanceof Number) {
            float n = ((Number) wt).floatValue();
            maxWeight = Math.max(maxWeight, Math.abs(n));
            boolean positive = n >= 0;
            double relativeWeight = Math.abs(n) / maxWeight;
            Color c = positive ? positiveColor(parent.getStroke(), relativeWeight)
                    : negativeColor(parent.getStroke(), relativeWeight);
            return new PathStyleDecorated().baseStyle(parent).stroke(c).strokeWidth((float) (2 * relativeWeight));
        } else if (wt instanceof Color) {
            return new PathStyleDecorated().baseStyle(parent).stroke((Color) wt);
        } else {
            return parent;
        }
    }
    
    //<editor-fold defaultstate="collapsed" desc="STATIC UTILS">
    
    private static final float HUE_RANGE = 0.1f;

    /**
     * Create a "positive" weighted color derived on the base renderer's color
     *
     * @param weight relative weight as a percentage (between 0 and 1)
     */
    private static Color positiveColor(Color c, double weight) {
        weight = Math.min(1, Math.max(0, weight));
        int alpha = 100 + (int) (155 * weight);
        if (c == null) {
            return new Color(
                    25 - (int) (25 * weight),
                    205 + (int) (50 * weight),
                    100 - (int) (50 * weight),
                    alpha);
        } else {
            float[] hsb = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
            hsb[0] += HUE_RANGE * weight;
            hsb[1] *= (.5 + .5 * weight);
            return hsbColor(hsb, alpha);
        }
    }

    /**
     * Create a "negative" weighted color derived on the base renderer's color
     *
     * @param weight relative weight as a percentage (between 0 and 1)
     */
    private static Color negativeColor(Color c, double weight) {
        weight = Math.min(1, Math.max(0, weight));
        int alpha = 100 + (int) (155 * weight);
        if (c == null) {
            return new Color(
                    205 + (int) (50 * weight),
                    0,
                    100 - (int) (50 * weight),
                    alpha);
        } else {
            float[] hsb = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
            hsb[0] -= 2 * HUE_RANGE * weight;
            hsb[1] *= (.5 + .5 * weight);
            return hsbColor(hsb, alpha);
        }
    }

    private static Color hsbColor(float[] hsb, int alpha) {
        Color col = new Color(Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]));
        return new Color(col.getRed(), col.getGreen(), col.getBlue(), alpha);
    }
    
    //</editor-fold>
    
}
