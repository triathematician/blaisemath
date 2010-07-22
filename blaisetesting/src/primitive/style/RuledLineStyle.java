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
import primitive.style.ArrowStyle.ArrowShape;

/**
 * Draws and stylizes a line with tick marks and labels.
 *
 * @author Elisha Peterson
 */
public class RuledLineStyle extends AbstractPrimitiveStyle<GraphicRuledLine<Point2D.Double>> {

    /**
     * A wrapper that contains an inner ruled line style, but with opposite orientation for ticks.
     */
    public static class Opposite extends AbstractPrimitiveStyle<GraphicRuledLine<Point2D.Double>> {
        private RuledLineStyle parent;
        public Opposite(RuledLineStyle parent) { this.parent = parent; }
        public Class<? extends GraphicRuledLine<Point2D.Double>> getTargetType() { return parent.getTargetType(); }
        public void draw(Graphics2D canvas, GraphicRuledLine<Point2D.Double> primitive) {
            boolean mlo = parent.mainLabelOpposite;
            boolean la = parent.labelsAbove;
            boolean ta = parent.ticksAbove;
            boolean tb = parent.ticksBelow;
            parent.labelsAbove = !la;
            if (ta && !tb || !ta && tb) {
                parent.ticksAbove = !ta;
                parent.ticksBelow = !tb;
            }
            parent.mainLabelOpposite = false;
            parent.draw(canvas, primitive);
            parent.labelsAbove = la;
            if (ta && !tb || !ta && tb) {
                parent.ticksAbove = ta;
                parent.ticksBelow = tb;
            }
            parent.mainLabelOpposite = mlo;
        }
        public boolean contained(GraphicRuledLine<Point2D.Double> primitive, Graphics2D canvas, Point point) { return parent.contained(primitive, canvas, point); }
    }

    /** Pixel length of standard tick mark. */
    int tickSize = 7;
    /** Whether ticks extend above/to the right of line. */
    boolean ticksAbove = true;
    /** Whether ticks extend below/to the left of the line. */
    boolean ticksBelow = false;
    /** Whether labels are drawn above/to the right or below/to the left. */
    boolean labelsAbove = true;
    /** Whether main label is on same side as other labels */
    boolean mainLabelOpposite = true;

    /** Style for the line and tick marks. */
    ArrowStyle lineStyle = new ArrowStyle( new Color(96, 96, 160), ArrowStyle.ArrowShape.REGULAR, tickSize );
    /** Style of the labels. */
    StringStyle labelStyle = new StringStyle( new Color(64, 64, 128), 14 );
    
    /** Whether ticks are shown on the plot. */
    private boolean ticksVisible = true;
    /** Whether tick labels are shown on the plot. */
    private boolean tickLabelsVisible = true;

    /** Whether arrow is shown at the end of the line */
    private boolean drawArrow = true;

    public Class getTargetType() {
        return GraphicRuledLine.class;
    }

    /** @return style for drawing the line */
    public ArrowStyle getLineStyle() { return lineStyle; }
    /** Sets style for drawing the line */
    public void setLineStyle(ArrowStyle style) { lineStyle = style; }
    /** @return arrow drawing status */
    public boolean isDrawArrow() { return drawArrow; }
    /** Sets draw arrow status */
    public void setDrawArrow(boolean val) { drawArrow = val; }

    public void draw(Graphics2D canvas, GraphicRuledLine<Point2D.Double> primitive) {

        // draw the main line
        ArrowShape hs = lineStyle.getHeadShape();
        if (!drawArrow) lineStyle.setHeadShape(ArrowShape.NONE);
        lineStyle.draw(canvas, new Point2D.Double[]{primitive.start, primitive.end});
        lineStyle.setHeadShape(hs);

        // compute the default direction for tick marks along the line
        Point2D.Double dt = new Point2D.Double(primitive.end.y-primitive.start.y, primitive.start.x-primitive.end.x);
        double magn = Math.sqrt(dt.x*dt.x+dt.y*dt.y);
        dt.x *= tickSize/magn; dt.y *= tickSize/magn;
        boolean vert = dt.y >= dt.x;
        if (vert) { dt.x = -dt.x; dt.y = -dt.y; }

        // setup for tick mark labels
        Point2D.Double labelOffset = labelsAbove ? new Point2D.Double(1.5*dt.x, 1.5*dt.y) : new Point2D.Double(-1.5*dt.x, -1.5*dt.y);
        StringStyle.Anchor labelAnchor = labelsAbove && vert ? StringStyle.Anchor.W
                : vert ? StringStyle.Anchor.E
                : labelsAbove && !vert ? StringStyle.Anchor.S
                : StringStyle.Anchor.N;
        Point2D.Double mainLabelOffset = mainLabelOpposite ? new Point2D.Double(-.5*labelOffset.x, -.5*labelOffset.y) : new Point2D.Double(.5*labelOffset.x, .5*labelOffset.y);
        StringStyle.Anchor mainLabelAnchor = mainLabelOpposite ?
            (labelsAbove && vert ? StringStyle.Anchor.E
                : vert ? StringStyle.Anchor.W
                : labelsAbove && !vert ? StringStyle.Anchor.N
                : StringStyle.Anchor.S)
                : labelAnchor;

        // draw main label
        labelStyle.setAnchor(mainLabelAnchor);
        if (primitive.label != null)
            labelStyle.draw(canvas, new GraphicString<Point2D.Double>(
                    new Point2D.Double(.1*primitive.start.x + .9*primitive.end.x + mainLabelOffset.x, .1*primitive.start.y + .9*primitive.end.y + mainLabelOffset.y),
                    primitive.label ) );

        // draw ticks and their labels
        labelStyle.setAnchor(labelAnchor);
        if (ticksVisible && (ticksAbove || ticksBelow)) {
            Point2D.Double rel1 = ticksBelow ? new Point2D.Double(-dt.x, -dt.y) : new Point2D.Double();
            Point2D.Double rel2 = ticksAbove ? dt : new Point2D.Double();
            Point2D.Double ctr = new Point2D.Double();

            GeneralPath tickPath = new GeneralPath();
            for (int i = 0; i < primitive.ticks.length; i++) {
                double d = primitive.ticks[i];
                if (d < 0.02 || d > 0.98) continue; // don't show ticks close to the ends
                ctr.x = (1-d)*primitive.start.x + d*primitive.end.x;
                ctr.y = (1-d)*primitive.start.y + d*primitive.end.y;
                tickPath.moveTo( (float)(ctr.x + rel1.x), (float)(ctr.y + rel1.y) );
                tickPath.lineTo( (float)(ctr.x + rel2.x), (float)(ctr.y + rel2.y) );
                if (tickLabelsVisible && primitive.tickLabels != null
                        && i < primitive.tickLabels.length && primitive.tickLabels[i] != null) {
                    labelStyle.draw(canvas, new GraphicString<Point2D.Double>(
                            new Point2D.Double(ctr.x + labelOffset.x, ctr.y + labelOffset.y),
                            primitive.tickLabels[i]));
                }
            }
            lineStyle.drawPath(canvas, tickPath);
        }
    }

    public boolean contained(GraphicRuledLine<Point2D.Double> primitive, Graphics2D canvas, Point point) {
        return false;
    }

}
