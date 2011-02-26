/*
 * RuledLineStyle.java
 * Created Apr 13, 2010
 */

package primitive.style;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import primitive.GraphicRuledLine;
import primitive.GraphicString;

/**
 * Draws and stylizes a series of tick marks and labels oriented along a line.
 * Does not draw the underlying line... only the ticks and labels.
 *
 * @author Elisha Peterson
 */
public class RuledLineStyle extends AbstractPrimitiveStyle<GraphicRuledLine<Point2D.Double>> {

    /** Pixel length of tick mark (to the left of the line). */
    int ruleLeft = 7;
    /** Pixel length of tick mark (to the right of the line). */
    int ruleRight = -7;
    /** Label position, as a percentage of the tick position (0 is right, 1 is leftmost) */
    double labelPos = 0;

    /** Reverses the order of left & right, in effect drawing everything on the opposite side of the line from where it would be otherwise. */
    boolean mirrored = false;

    /** Style for the tick marks. */
    LineStyle ruleStyle = new LineStyle( new Color(96, 96, 160) );
    /** Style of the labels. */
    StringStyle labelStyle = new StringStyle( new Color(64, 64, 128), 14 );

    /** Whether ticks are shown on the plot. */
    private boolean rulesVisible = true;
    /** Whether tick labels are shown on the plot. */
    private boolean labelsVisible = true;

    public Class getTargetType() {
        return GraphicRuledLine.class;
    }

    /** @return style for drawing the rules */
    public LineStyle getRuleStyle() { return ruleStyle; }
    /** Sets style for drawing the rules */
    public void setRuleStyle(LineStyle style) { ruleStyle = style; }

    /** @return style for drawing labels */
    public StringStyle getLabelStyle() { return labelStyle; }
    /** Sets style for drawing labels */
    public void setLabelStyle(StringStyle style) { labelStyle = style; }

    /** @return tick extension to the left (in pixels) */
    public int getRuleLeft() { return ruleLeft; }
    /** Set tick extension to the left (in pixels) */
    public void setRuleLeft(int size) { ruleLeft = size; }

    /** @return tick extension to the right (in pixels) */
    public int getRuleRight() { return ruleRight; }
    /** Set tick extension to the right (in pixels) */
    public void setRuleRight(int size) { ruleRight = size; }

    /** @return visibility of rules */
    public boolean isRulesVisible() { return rulesVisible; }
    /** Sets visibility of rules */
    public void setRulesVisible(boolean val) { rulesVisible = val; }

    /** @return visibility of labels */
    public boolean isLabelsVisible() { return labelsVisible; }
    /** Sets visibility of labels */
    public void setLabelsVisible(boolean val) { labelsVisible = val; }

    /** @return label position (as a percentage) */
    public double getLabelPosition() { return labelPos; }
    /** Sets label position (as a percentage) */
    public void setLabelPosition(double d) { labelPos = d; }

    /** @return mirroring status */
    public boolean isMirrored() { return mirrored; }
    /** Sets mirroring status */
    public void setMirrored(boolean val) { mirrored = val; }

    //
    // DRAW METHODS
    //

    public void draw(Graphics2D canvas, GraphicRuledLine<Point2D.Double> primitive) {

        // compute the default direction for tick marks along the line (this is the direction of "left")
        Point2D.Double dl = new Point2D.Double(primitive.end.y-primitive.start.y, primitive.start.x-primitive.end.x);
        double magn = Math.sqrt(dl.x*dl.x+dl.y*dl.y);
        dl.x /= magn; dl.y /= magn;
        boolean vert = dl.y >= dl.x;
        if (vert) { dl.x = -dl.x; dl.y = -dl.y; }

        // draw ticks and their labels
        if (rulesVisible && ruleLeft != ruleRight) {
            Point2D.Double relL = new Point2D.Double(ruleLeft * dl.x, ruleLeft * dl.y);
            Point2D.Double relR = new Point2D.Double(ruleRight * dl.x, ruleRight * dl.y);
            double ruleLabel = (1-labelPos)*ruleRight + labelPos*ruleLeft;
            Point2D.Double relLab = new Point2D.Double(ruleLabel * dl.x, ruleLabel * dl.y);
            Point2D.Double ctr = new Point2D.Double();

            GeneralPath rulePath = new GeneralPath();
            for (int i = 0; i < primitive.ticks.length; i++) {
                double d = primitive.ticks[i];
                if (d < 0.02 || d > 0.98) continue; // don't show ticks close to the ends
                ctr.x = (1-d)*primitive.start.x + d*primitive.end.x;
                ctr.y = (1-d)*primitive.start.y + d*primitive.end.y;
                rulePath.moveTo( (float)(ctr.x + relR.x), (float)(ctr.y + relR.y) );
                rulePath.lineTo( (float)(ctr.x + relL.x), (float)(ctr.y + relL.y) );
                if (labelsVisible && primitive.tickLabels != null
                        && i < primitive.tickLabels.length && primitive.tickLabels[i] != null) {
                    labelStyle.draw(canvas, new GraphicString<Point2D.Double>(
                            new Point2D.Double(ctr.x + relLab.x, ctr.y + relLab.y),
                            primitive.tickLabels[i]));
                }
            }
            ruleStyle.drawPath(canvas, rulePath);
        }
    }

    public boolean contained(GraphicRuledLine<Point2D.Double> primitive, Graphics2D canvas, Point point) {
        return false;
    }

}
