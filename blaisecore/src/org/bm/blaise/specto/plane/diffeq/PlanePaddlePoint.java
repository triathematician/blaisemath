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
import org.bm.blaise.sequor.component.TimeClock;
import org.bm.blaise.specto.plottable.VPoint;
import org.bm.blaise.specto.primitive.BlaisePalette;
import org.bm.blaise.specto.primitive.PathStyle;
import org.bm.blaise.specto.visometry.AnimatingPlottable;
import org.bm.blaise.specto.visometry.Visometry;
import org.bm.blaise.specto.visometry.VisometryGraphics;

/**
 * <p>
 *    This class ...
 * </p>
 * @author Elisha Peterson
 */
public class PlanePaddlePoint extends VPoint<Point2D.Double> implements AnimatingPlottable<Point2D.Double> {

    /** Stores current orientation of paddle */
    transient double angle = 0;
    /** Stores length of paddle */
    double length = 0.15;
    /** Stores speed of paddle */
    double speed = 0.001;
    /** Whether paddle moves along the field, or is fixed to one position */
    boolean moving = false;
    /** Function underlying the point */
    MultivariateVectorialFunction vecFun;

    /** Style for the paddle arms */
    PathStyle armStyle;

    /** Constructs the paddle at the origin. */
    public PlanePaddlePoint(MultivariateVectorialFunction vecFun) {
        this(new Point2D.Double(), vecFun);
    }

    /** Constructs paddle at specified location, with specified vec field */
    public PlanePaddlePoint(Point2D.Double value, MultivariateVectorialFunction vecFun) {
        super(value);
        super.getStyle().setRadius(3);
//        super.getStyle().setFillColor(null);
//        super.getStyle().setShape(PointShape.OPEN_DOT);
        this.vecFun = vecFun;
        armStyle = new PathStyle(BlaisePalette.STANDARD.function(), 3f);
        setLabelVisible(false);
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

    @Override
    public void setPoint(Double value) {
        super.setPoint(value);
        computePoints();
    }

    public PathStyle getArmStyle() {
        return armStyle;
    }

    public void setArmStyle(PathStyle armStyle) {
        this.armStyle = armStyle;
    }

    //
    // DRAW AND COMPUTATION
    //

    transient Point2D.Double p1;
    transient Point2D.Double p2;
    transient Point2D.Double p3;
    transient Point2D.Double p4;

    void computePoints() {
        double ca = Math.cos(angle) * length;
        double sa = Math.sin(angle) * length;
        p1 = new Point2D.Double(value.x + ca, value.y + sa);
        p2 = new Point2D.Double(value.x - sa, value.y + ca);
        p3 = new Point2D.Double(value.x - ca, value.y - sa);
        p4 = new Point2D.Double(value.x + sa, value.y - ca);
    }

    public void recomputeAtTime(Visometry<Point2D.Double> vis, VisometryGraphics<Point2D.Double> canvas, TimeClock clock) {
        computePoints();
        try {
            double[] f1 = vecFun.value(new double[]{p1.x, p1.y});
            double[] f2 = vecFun.value(new double[]{p2.x, p2.y});
            double[] f3 = vecFun.value(new double[]{p3.x, p3.y});
            double[] f4 = vecFun.value(new double[]{p4.x, p4.y});
            double ca = Math.cos(angle);
            double sa = Math.sin(angle);
            // add dot product of angle with direction of paddle
            double dAngle = ( (f1[0] * -sa + f1[1] * ca)
                    + (f2[0] * -ca + f2[1] * -sa)
                    + (f3[0] * sa + f3[1] * -ca)
                    + (f4[0] * ca + f4[1] * sa)
                    ) / 4;
            angle += dAngle / length * speed;
            if (moving) {
                double[] fx = vecFun.value(new double[]{value.x, value.y});
                value.x += fx[0] * speed;
                value.y += fx[1] * speed;
            }
        } catch (FunctionEvaluationException ex) {
            Logger.getLogger(PlanePaddlePoint.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(PlanePaddlePoint.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void paintComponent(VisometryGraphics<Point2D.Double> vg) {
        if (p1 == null) {
            computePoints();
        }
        vg.setPathStyle(armStyle);
        vg.drawSegment(p1, p3);
        vg.drawSegment(p2, p4);
        super.paintComponent(vg);
    }

    @Override
    public String toString() {
        return "Paddle Point @ " + super.getValueString();
    }



}
