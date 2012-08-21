/*
 * DefaultWeightedEdgeStyle.java
 * Created Sep 18, 2011
 */
package org.bm.blaise.specto.plane.graph;

import java.awt.Color;
import org.bm.blaise.style.DecoratorPathStyle;
import org.bm.blaise.scio.graph.WeightedGraph;
import org.bm.blaise.style.PathStyle;
import org.bm.util.Delegator;

/**
 * Provides a default node customizer suitable for a weighted-edge graph.
 * 
 * @author elisha
 */
public class DefaultWeightedEdgeStyle implements Delegator<Object[], PathStyle> {

    /** Parent style */
    protected final PathStyle parent;
    /** The weighted graph */
    protected final WeightedGraph wg;
    
    /** The maximum edge weight */
    protected transient float maxWeight = 0f;
    
    /**
     * Construct the customizer
     * @param parent the parent style
     * @param adapter the adapter managing the graph's display
     * @param wg the weighted graph; weights should be either numbers or colors
     */
    public DefaultWeightedEdgeStyle(PathStyle parent, WeightedGraph wg) {
        this.parent = parent;
        this.wg = wg;
        for (Object o1 : wg.nodes())
            for (Object o2 : wg.nodes()) {
                Object wt = wg.getWeight(o1, o2);
                if (wt instanceof Number)
                    maxWeight = Math.max(maxWeight, Math.abs(((Number)wt).floatValue()));
            }
    }

    public PathStyle of(Object[] o) {
        Object wt = wg.getWeight(o[0], o[1]);
        if (wt instanceof Number) {
            float n = ((Number)wt).floatValue();
            maxWeight = Math.max(maxWeight, Math.abs(n));
            boolean positive = n >= 0;
            double relativeWeight = Math.abs(n)/maxWeight;
            Color c = positive ? positiveColor(parent.getColor(), relativeWeight) 
                    : negativeColor(parent.getColor(), relativeWeight);
            return new DecoratorPathStyle(parent, (float) (2*relativeWeight), c);
        } else if (wt instanceof Color) {
            return new DecoratorPathStyle(parent, (Color) wt);
        } else
            return parent;
    }
    private static final float HUE_RANGE = 0.1f;
    
    /** 
     * Create a "positive" weighted color derived on the base renderer's color 
     * @param weight relative weight as a percentage (between 0 and 1)
     */
    private static Color positiveColor(Color c, double weight) {
        weight = Math.min(1, Math.max(0, weight));
        int alpha = 100+(int)(155*weight);
        if (c == null)
            return new Color(
                25-(int)(25*weight),
                205+(int)(50*weight),
                100-(int)(50*weight),
                alpha);
        else {
            float[] hsb = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
            hsb[0] += HUE_RANGE * weight;
            hsb[1] *= (.5+.5*weight);
            return hsbColor(hsb, alpha);
        }
    }

    /** 
     * Create a "negative" weighted color derived on the base renderer's color
     * @param weight relative weight as a percentage (between 0 and 1)
     */
    private static Color negativeColor(Color c, double weight) {
        weight = Math.min(1, Math.max(0, weight));
        int alpha = 100+(int)(155*weight);
        if (c == null)
            return new Color(
                205+(int)(50*weight),
                0,
                100-(int)(50*weight),
                alpha);
        else {
            float[] hsb = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
            hsb[0] -= 2 * HUE_RANGE * weight;
            hsb[1] *= (.5+.5*weight);
            return hsbColor(hsb, alpha);
        }
    }
    
    private static Color hsbColor(float[] hsb, int alpha) {
        Color col = new Color(Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]));
        return new Color(col.getRed(), col.getGreen(), col.getBlue(), alpha);
    }
    
}
