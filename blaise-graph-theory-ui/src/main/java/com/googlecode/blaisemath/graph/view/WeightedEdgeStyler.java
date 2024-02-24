package com.googlecode.blaisemath.graph.view;

/*
 * #%L
 * BlaiseGraphTheory
 * --
 * Copyright (C) 2009 - 2024 Elisha Peterson
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
import com.google.common.collect.Ordering;
import com.google.common.graph.EndpointPair;
import com.googlecode.blaisemath.style.AttributeSet;
import com.googlecode.blaisemath.style.Styles;

import java.awt.*;
import java.util.Map;

/**
 * Provides an edge styler for changing the appearance of edges in a weighted graph. Provides unique styles for positive
 * and negative weights.
 * 
 * @param <E> edge type
 * @author Elisha Peterson
 */
public class WeightedEdgeStyler<E extends EndpointPair> implements Function<E, AttributeSet> {
    
    private static final float HUE_RANGE = 0.1f;

    /** Parent style */
    protected final AttributeSet parent;
    /** Edge weights */
    protected Map<E, Double> weights;
    /** The maximum edge weight */
    protected double maxWeight;

    /**
     * Construct the customizer
     * @param parent the parent style
     * @param weights weightings for edges in graph
     */
    public WeightedEdgeStyler(AttributeSet parent, Map<E, Double> weights) {
        this.parent = parent;
        this.weights = weights;
        this.maxWeight = weights.isEmpty() ? 1.0 : Ordering.natural().max(weights.values());
    }

    public Map<E, Double> getWeights() {
        return weights;
    }

    public void setWeights(Map<E, Double> weights) {
        if (this.weights != weights) {
            this.weights = weights;
            this.maxWeight = weights.isEmpty() ? 1.0 : Ordering.natural().max(weights.values());
        }
    }

    @Override
    public AttributeSet apply(E o) {
        Double wt = weights.get(o);
        maxWeight = Math.max(maxWeight, Math.abs(wt));
        boolean positive = wt >= 0;
        double relativeWeight = Math.abs(wt) / maxWeight;
        Color stroke = parent.getColor(Styles.STROKE);
        Color c = positive ? positiveColor(stroke, relativeWeight)
                : negativeColor(stroke, relativeWeight);
        return AttributeSet.withParent(parent)
                .and(Styles.STROKE, c)
                .and(Styles.STROKE_WIDTH, (float) (2 * relativeWeight));
    }
    
    //region UTILS

    private static Color positiveColor(Color c, double weight) {
        double wt = Math.min(1, Math.max(0, weight));
        int alpha = 100 + (int) (155 * wt);
        if (c == null) {
            return new Color(
                    25 - (int) (25 * wt),
                    205 + (int) (50 * wt),
                    100 - (int) (50 * wt),
                    alpha);
        } else {
            float[] hsb = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
            hsb[0] += HUE_RANGE * wt;
            hsb[1] *= (.5 + .5 * wt);
            return hsbColor(hsb, alpha);
        }
    }
    
    private static Color negativeColor(Color c, double weight) {
        double wt = Math.min(1, Math.max(0, weight));
        int alpha = 100 + (int) (155 * wt);
        if (c == null) {
            return new Color(
                    205 + (int) (50 * wt),
                    0,
                    100 - (int) (50 * wt),
                    alpha);
        } else {
            float[] hsb = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
            hsb[0] -= 2 * HUE_RANGE * wt;
            hsb[1] *= (.5 + .5 * wt);
            return hsbColor(hsb, alpha);
        }
    }

    private static Color hsbColor(float[] hsb, int alpha) {
        Color col = new Color(Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]));
        return new Color(col.getRed(), col.getGreen(), col.getBlue(), alpha);
    }
    
    //endregion
    
}
