/**
 * ClockPointStyle.java
 * Created on Dec 17, 2009
 */

package org.bm.blaise.specto.primitive;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Calendar;
import java.util.GregorianCalendar;
import org.bm.blaise.specto.primitive.ArrowStyle.ArrowShape;

/**
 * <p>
 *    This class ...
 * </p>
 * @author Elisha Peterson
 */
public class ClockStyle implements PrimitiveStyle<Point2D> {

    /** The general style of the clock. */
    ClockModel model = ClockModel.DEFAULT;

    /** The style of frame. */
    ShapeStyle frameStyle = new ShapeStyle();
    /** The style of ticks. */
    PathStyle tickStyle = new PathStyle(Color.DARK_GRAY, 2);
    /** The style of hands. */
    ArrowStyle handStyle = new ArrowStyle(new PathStyle(Color.BLACK, 2), ArrowShape.NONE, ArrowShape.DOT, 5);
    /** The style of date. */
    StringStyle dateStyle = new StringStyle();

    /** The size of the clock. */
    int radius = 50;


    //
    // GETTERS & SETTERS
    //

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public StringStyle getDateStyle() {
        return dateStyle;
    }

    public void setDateStyle(StringStyle dateStyle) {
        this.dateStyle = dateStyle;
    }

    public ShapeStyle getFrameStyle() {
        return frameStyle;
    }

    public void setFrameStyle(ShapeStyle frameStyle) {
        this.frameStyle = frameStyle;
    }

    public ArrowStyle getHandStyle() {
        return handStyle;
    }

    public void setHandStyle(ArrowStyle handStyle) {
        this.handStyle = handStyle;
    }

    public PathStyle getTickStyle() {
        return tickStyle;
    }

    public void setTickStyle(PathStyle tickStyle) {
        this.tickStyle = tickStyle;
    }

    public ClockModel getModel() {
        return model;
    }

    public void setModel(ClockModel style) {
        this.model = style;
    }

    //
    // PAINT METHODS
    //

    private static final String[] CAL_STRINGS = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};

    public void draw(Point2D point, Graphics2D canvas) {
        Calendar calendar = new GregorianCalendar();
        model.paintFrame(canvas, frameStyle, point, radius);
        model.paintTicks(canvas, tickStyle, point, radius);
        model.paintDate(canvas, dateStyle, point, radius,
                Integer.toString(calendar.get(Calendar.DATE)),
                CAL_STRINGS[calendar.get(Calendar.MONTH)]);
        double sec = calendar.get(Calendar.SECOND) + calendar.get(Calendar.MILLISECOND)/1000;
        double min = calendar.get(Calendar.MINUTE) + sec/60;
        double hr = calendar.get(Calendar.HOUR) + min/60;
        model.paintHand(canvas, handStyle, point, .35*radius, Math.PI/2-hr*Math.PI/6.);
        model.paintHand(canvas, handStyle, point, .55*radius, Math.PI/2-min*Math.PI/30.);
        model.paintHand(canvas, handStyle, point, .75*radius, Math.PI/2-sec*Math.PI/30.);
    }

    public void draw(Point2D[] primitives, Graphics2D canvas) {
        for (Point2D p : primitives) {
            draw(p, canvas);
        }
    }

    @Override
    public String toString() {
        return "Clock";
    }

    public enum ClockModel {
        DEFAULT {
            public void paintHand(Graphics2D g, ArrowStyle style, Point2D p, double r, double angle) {
                style.draw(new GraphicArrow( p, new Point2D.Double(p.getX() + r*Math.cos(angle), p.getY() - r*Math.sin(angle)) ), g);
            }
        },
        ARCS {
            public void paintHand(Graphics2D g, ArrowStyle style, Point2D p, double r, double angle) {
                style.pathStyle.draw(new Arc2D.Double(p.getX()-r, p.getY()-r, 2*r, 2*r, 90, -((450-angle*180/Math.PI)%360), Arc2D.OPEN), g);
            }
        };

        /** Paints outside "frame" at given window location and pixel radius. */
        public void paintFrame(Graphics2D g, ShapeStyle style, Point2D p, double r){
            style.draw(new Ellipse2D.Double(p.getX()-r, p.getY()-r, 2*r, 2*r), g);
        }
        /** Paints ticks at given window location and pixel radius. */
        public void paintTicks(Graphics2D g, PathStyle style, Point2D p, double r){
            Line2D.Double line = new Line2D.Double(0,0,0,0);
            for (double angle = -Math.PI/2; angle < 3*Math.PI/2; angle += Math.PI/6) {
                line.x1 = p.getX() + .85*r*Math.cos(angle);
                line.y1 = p.getY() - .85*r*Math.sin(angle);
                line.x2 = p.getX() + .95*r*Math.cos(angle);
                line.y2 = p.getY() - .95*r*Math.sin(angle);
                style.draw(line, g);
            }
        }
        /** Paints "date" at given window location and pixel radius. */
        public void paintDate(Graphics2D g, StringStyle style, Point2D p, double r, String day, String month){
            GraphicString gs = new GraphicString(p.getX() + 2, p.getY() - 3, month);
            style.draw(gs, g);
        }

        /** Paints "hand" at given window location and pixel radius. */
        abstract public void paintHand(Graphics2D g, ArrowStyle style, Point2D p, double r, double angle);
    }
}
