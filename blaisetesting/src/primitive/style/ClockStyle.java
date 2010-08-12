/**
 * ClockPointStyle.java
 * Created on Dec 17, 2009
 */

package primitive.style;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Calendar;
import java.util.GregorianCalendar;
import primitive.GraphicString;

/**
 * <p>
 *    This class displays a draggable clock on the screen.
 * </p>
 * @author Elisha Peterson
 */
public class ClockStyle extends AbstractPrimitiveStyle<Point2D.Double> {

    /** Contains the supported types of clock displays */
    public enum ClockModel { DEFAULT, ARCS };

    /** The general style of the clock. */
    ClockModel model = ClockModel.DEFAULT;
    /** The size of the clock. */
    int radius = 50;

    /** The style of frame. */
    ShapeStyle frameStyle = new ShapeStyle();
    /** The style of hands. */
    ArrowStyle handStyle = new ArrowStyle(Color.BLACK, 2, ArrowStyle.ArrowShape.DOT, ArrowStyle.ArrowShape.NONE, 5);
    /** The style of ticks. */
    PathStyleShape tickStyle = new PathStyleShape(Color.DARK_GRAY, 2);
    /** The style of date. */
    StringStyle dateStyle = new StringStyle(Anchor.West);

    @Override
    public String toString() {
        return "Clock";
    }

    public Class<? extends Point2D.Double> getTargetType() {
        return Point2D.Double.class;
    }

    /** @return the model of the clock */
    public ClockModel getModel() { return model; }
    /** Sets the model of the clock */
    public void setModel(ClockModel style) { this.model = style; }
    /** @return displayed radius of clock */
    public int getRadius() { return radius; }
    /** Sets radius of clock */
    public void setRadius(int radius) { this.radius = radius; }
    /** @return style used to display the outer frame */
    public ShapeStyle getFrameStyle() { return frameStyle; }
    /** Sets style used to display the outer frame */
    public void setFrameStyle(ShapeStyle frameStyle) { this.frameStyle = frameStyle; }
    /** @return arrow style used to display the clock's hands */
    public ArrowStyle getHandStyle() { return handStyle; }
    /** Sets style used to display the clock's hands */
    public void setHandStyle(ArrowStyle handStyle) { this.handStyle = handStyle; }
    /** @return path style used to display the hour ticks */
    public PathStyleShape getTickStyle() { return tickStyle; }
    /** Sets style used to display the hour ticks */
    public void setTickStyle(PathStyleShape tickStyle) { this.tickStyle = tickStyle; }
    /** @return style used to display the date */
    public StringStyle getDateStyle() { return dateStyle; }
    /** Sets style used to display the date */
    public void setDateStyle(StringStyle dateStyle) { this.dateStyle = dateStyle; }

    private static final String[] CAL_STRINGS = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};

    public void draw(Graphics2D canvas, Point2D.Double point) {
        Calendar calendar = new GregorianCalendar();
        paintFrame(canvas, model, frameStyle, point, radius);
        paintTicks(canvas, model, tickStyle, point, radius);
        paintDate(canvas, model, dateStyle, point, radius,
                Integer.toString(calendar.get(Calendar.DATE)),
                CAL_STRINGS[calendar.get(Calendar.MONTH)]);
        double sec = calendar.get(Calendar.SECOND) + calendar.get(Calendar.MILLISECOND)/1000;
        double min = calendar.get(Calendar.MINUTE) + sec/60;
        double hr = calendar.get(Calendar.HOUR) + min/60;
        paintHand(canvas, model, handStyle, point, .35*radius, Math.PI/2-hr*Math.PI/6.);
        paintHand(canvas, model, handStyle, point, .55*radius, Math.PI/2-min*Math.PI/30.);
        paintHand(canvas, model, handStyle, point, .75*radius, Math.PI/2-sec*Math.PI/30.);
    }

    public boolean contained(Point2D.Double primitive, Graphics2D canvas, Point point) {
        return primitive.distance(point) <= radius;
    }

    //
    // SPECIALIZED PAINT METHODS
    //
    
    static void paintHand(Graphics2D g, ClockModel m, ArrowStyle style, Point2D.Double p, double r, double angle) {
        switch(m) {
            case ARCS:
                style.drawPath(g, new Arc2D.Double(p.getX()-r, p.getY()-r, 2*r, 2*r, 90, -((450-angle*180/Math.PI)%360), Arc2D.OPEN));
            case DEFAULT:
            default:
                style.draw(g, new Point2D.Double[]{ p, new Point2D.Double(p.getX() + r*Math.cos(angle), p.getY() - r*Math.sin(angle)) } );
        }
    }
    
    static void paintFrame(Graphics2D g, ClockModel m, ShapeStyle style, Point2D.Double p, double r) {
        switch(m) {
            case ARCS:
            case DEFAULT:
            default:
                style.draw(g, new Ellipse2D.Double(p.getX()-r, p.getY()-r, 2*r, 2*r));
        }
    }
    
    static void paintTicks(Graphics2D g, ClockModel m, PathStyleShape style, Point2D.Double p, double r) {
        switch(m) {
            case ARCS:
            case DEFAULT:
            default:
                Line2D.Double line = new Line2D.Double(0,0,0,0);
                for (double angle = -Math.PI/2; angle < 3*Math.PI/2; angle += Math.PI/6) {
                    line.x1 = p.getX() + .85*r*Math.cos(angle);
                    line.y1 = p.getY() - .85*r*Math.sin(angle);
                    line.x2 = p.getX() + .95*r*Math.cos(angle);
                    line.y2 = p.getY() - .95*r*Math.sin(angle);
                    style.draw(g, line);
                }
        }
    }
    
    static void paintDate(Graphics2D g, ClockModel m, StringStyle style, Point2D.Double p, double r, String day, String month){
        switch(m) {
            case ARCS:
            case DEFAULT:
            default:
            GraphicString<Point2D.Double> gs = new GraphicString<Point2D.Double>(new Point2D.Double(p.getX() + 5, p.getY()), month);
            style.draw(g, gs);
        }
    }
}
