/**
 * PlanePaddlePoint.java
 * Created on Nov 23, 2009
 */

package org.bm.blaise.specto.plane.diffeq;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.MultivariateVectorialFunction;
import org.bm.blaise.sequor.timer.TimeClock;
import org.bm.blaise.specto.plottable.VPoint;
import org.bm.blaise.specto.plottable.VPointSet;
import org.bm.blaise.specto.primitive.BlaisePalette;
import org.bm.blaise.specto.primitive.PathStyle;
import org.bm.blaise.specto.visometry.AnimatingPlottable;
import org.bm.blaise.specto.visometry.Visometry;
import org.bm.blaise.specto.visometry.VisometryGraphics;

/**
 * <p>
 *    This class illustrates the curl of a vector field using points that work like
 *    little paddle wheels, spinning according to the flow of the vector field.
 * </p>
 * @author Elisha Peterson
 */
public class PlanePaddlePoint extends VPointSet<Point2D.Double> implements AnimatingPlottable<Point2D.Double> {

    /** Stores length of paddle */
    double length = 0.25;
    /** Stores speed of paddle */
    double speed = 0.001;
    /** Whether paddle moves along the field, or is fixed to one position */
    boolean moving = false;
    /** Function underlying the point */
    MultivariateVectorialFunction field;

    /** Style for the paddle arms */
    PathStyle armStyle;

    /** Constructs paddle at specified location(s), with specified vec field */
    public PlanePaddlePoint(MultivariateVectorialFunction field, Point2D.Double... values) {
        super(values);
        super.getPointStyle().setRadius(3);
        armStyle = new PathStyle(BlaisePalette.STANDARD.func1(), 3f);
        setLabelsVisible(false);
        this.field = field;
        computePoints();
    }

    //
    //
    // BEAN PATTERNS
    //
    //

    public boolean isAnimationOn() {
        return true;
    }

    public void setAnimationOn(boolean newValue) {
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public double getSpeed() {
        return speed*100;
    }

    public void setSpeed(double speed) {
        this.speed = speed/100;
    }

    public boolean isMoving() {
        return moving;
    }

    public void setMoving(boolean moving) {
        this.moving = moving;
    }

    public PathStyle getArmStyle() {
        return armStyle;
    }

    public void setArmStyle(PathStyle armStyle) {
        this.armStyle = armStyle;
    }

    @Override
    protected void fireStateChanged() {
        computePoints();
        super.fireStateChanged();
    }

    //
    // DRAW AND COMPUTATION
    //



    /** Stores current orientation of paddle */
    transient double angles[];

    transient Point2D.Double[] p1;
    transient Point2D.Double[] p2;
    transient Point2D.Double[] p3;
    transient Point2D.Double[] p4;

    void computePoints() {
        if (angles == null || angles.length != values.length) {
            angles = new double[values.length];
            p1 = new Point2D.Double[values.length];
            p2 = new Point2D.Double[values.length];
            p3 = new Point2D.Double[values.length];
            p4 = new Point2D.Double[values.length];
        }
        for (int i = 0; i < values.length; i++) {
            double ca = Math.cos(angles[i]) * length;
            double sa = Math.sin(angles[i]) * length;
            p1[i] = new Point2D.Double(values[i].x + ca, values[i].y + sa);
            p2[i] = new Point2D.Double(values[i].x - sa, values[i].y + ca);
            p3[i] = new Point2D.Double(values[i].x - ca, values[i].y - sa);
            p4[i] = new Point2D.Double(values[i].x + sa, values[i].y - ca);
        }
    }

    public void recomputeAtTime(Visometry<Point2D.Double> vis, VisometryGraphics<Point2D.Double> canvas, TimeClock clock) {
        computePoints();
        try {
            for (int i = 0; i < values.length; i++) {
                double[] f1 = field.value(new double[]{p1[i].x, p1[i].y});
                double[] f2 = field.value(new double[]{p2[i].x, p2[i].y});
                double[] f3 = field.value(new double[]{p3[i].x, p3[i].y});
                double[] f4 = field.value(new double[]{p4[i].x, p4[i].y});
                double ca = Math.cos(angles[i]);
                double sa = Math.sin(angles[i]);
                // add dot product of angle with direction of paddle
                double dAngle = ( (f1[0] * -sa + f1[1] * ca)
                        + (f2[0] * -ca + f2[1] * -sa)
                        + (f3[0] * sa + f3[1] * -ca)
                        + (f4[0] * ca + f4[1] * sa)
                        ) / 4;
                angles[i] += dAngle / length * speed;
                if (moving) {
                    double[] fx = field.value(new double[]{values[i].x, values[i].y});
                    values[i].x += fx[0] * speed;
                    values[i].y += fx[1] * speed;
                }
            }
        } catch (FunctionEvaluationException ex) {
            Logger.getLogger(PlanePaddlePoint.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(PlanePaddlePoint.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void paintComponent(VisometryGraphics<Point2D.Double> vg) {
        if (angles == null || angles.length != values.length)
            computePoints();
        for (int i = 0; i < values.length; i++) {
            vg.drawSegment(p1[i], p3[i], armStyle);
            vg.drawSegment(p2[i], p4[i], armStyle);
        }
        super.paintComponent(vg);
    }

    @Override
    public String toString() {
        return "Paddle Points @ " + super.toString();
    }
}
