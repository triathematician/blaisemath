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
 * Draws and stylizes a line with tick marks and labels.
 *
 * @author Elisha Peterson
 */
public class RuledLineStyle extends AbstractPrimitiveStyle<GraphicRuledLine<Point2D.Double>> {

    /** Pixel length of standard tick mark. */
    int tickSize = 7;
    /** Whether ticks extend above/to the right of line. */
    boolean ticksAbove = true;
    /** Whether ticks extend below/to the left of the line. */
    boolean ticksBelow = false;

    /** Style for the line and tick marks. */
    ArrowStyle lineStyle = new ArrowStyle( new Color(96, 96, 160), ArrowStyle.ArrowShape.REGULAR, tickSize );
    /** Style of the labels. */
    StringStyle labelStyle = new StringStyle( new Color(64, 64, 128), 14 );
    
    /** Whether ticks are shown on the plot. */
    private boolean ticksVisible = true;
    /** Whether tick labels are shown on the plot. */
    private boolean tickLabelsVisible = true;

    public Class getTargetType() {
        return GraphicRuledLine.class;
    }

    /** @return style for drawing the line */
    public ArrowStyle getLineStyle() { return lineStyle; }
    /** Sets style for drawing the line */
    public void setLineStyle(ArrowStyle style) { lineStyle = style; }

    public void draw(Graphics2D canvas, GraphicRuledLine<Point2D.Double> primitive) {
        // general computations
        Point2D.Double dt = new Point2D.Double(-primitive.start.y+primitive.end.y, -primitive.end.x+primitive.start.x);
        double magn = Math.sqrt(dt.x*dt.x+dt.y*dt.y);
        dt.x *= tickSize/magn; dt.y *= tickSize/magn;
        Point2D.Double relLabel = ticksAbove ? new Point2D.Double(2*dt.x, 2*dt.y) : new Point2D.Double(dt.x, dt.y);

        // main line
        lineStyle.draw(canvas, new Point2D.Double[]{primitive.start, primitive.end});

        // main label
        labelStyle.setAnchor(StringStyle.ANCHOR_CENTER);
        if (primitive.label != null)
            labelStyle.draw(canvas, new GraphicString<Point2D.Double>(
                    new Point2D.Double(.1*primitive.start.x + .9*primitive.end.x - relLabel.x, .1*primitive.start.y + .9*primitive.end.y - relLabel.y),
                    primitive.label ) );

        // tick marks
        if (ticksVisible && (ticksAbove || ticksBelow)) {
            Point2D.Double rel1 = ticksBelow ? new Point2D.Double(-dt.x, -dt.y) : new Point2D.Double();
            Point2D.Double rel2 = ticksAbove ? dt : new Point2D.Double();
            Point2D.Double ctr = new Point2D.Double();

            GeneralPath tickPath = new GeneralPath();
            for (int i = 0; i < primitive.ticks.length; i++) {
                double d = primitive.ticks[i];
                ctr.x = (1-d)*primitive.start.x + d*primitive.end.x;
                ctr.y = (1-d)*primitive.start.y + d*primitive.end.y;
                tickPath.moveTo( (float)(ctr.x + rel1.x), (float)(ctr.y + rel1.y) );
                tickPath.lineTo( (float)(ctr.x + rel2.x), (float)(ctr.y + rel2.y) );
                if (tickLabelsVisible && primitive.tickLabels != null && i < primitive.tickLabels.length && primitive.tickLabels[i] != null)
                    labelStyle.draw(canvas, new GraphicString<Point2D.Double>(new Point2D.Double(ctr.x + relLabel.x, ctr.y + relLabel.y), primitive.tickLabels[i]));
            }
            lineStyle.drawPath(canvas, tickPath);
        }
    }

    public boolean contained(GraphicRuledLine<Point2D.Double> primitive, Graphics2D canvas, Point point) {
        return false;
    }

}
