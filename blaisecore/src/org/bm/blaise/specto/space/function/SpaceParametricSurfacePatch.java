/*
 * SpaceParametricSurfacePatch.java
 * Created Oct 2009
 */

package org.bm.blaise.specto.space.function;

import java.awt.geom.Point2D;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.math.FunctionEvaluationException;
import org.bm.blaise.specto.space.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.event.ChangeEvent;
import org.apache.commons.math.analysis.MultivariateVectorialFunction;
import org.bm.blaise.specto.plottable.VRectangle;
import org.bm.blaise.specto.visometry.AbstractPlottable;
import org.bm.blaise.specto.visometry.VisometryGraphics;
import scio.coordinate.Point3D;

/**
 * <p>
 *  Constructs and displays a single patch on a 3D parametric surface.
 * </p>
 * @author Elisha Peterson
 */
public class SpaceParametricSurfacePatch extends AbstractPlottable<Point3D> {

    /** The underlying function, 2 inputs, 2 outputs */
    MultivariateVectorialFunction func;
    /** Starting values of u and v */
    Point2D.Double domainPoint;
    /** Difference values of u and v */
    Point2D.Double dPoint;
    /** Stores rectangle used to adjust the domain. */
    VRectangle<Point2D.Double> domainPlottable;

    public SpaceParametricSurfacePatch(MultivariateVectorialFunction func, Point2D.Double point, Point2D.Double dPoint) {
        setFunction(func);
        this.domainPoint = point;
        this.dPoint = dPoint;
        domainPlottable = new VRectangle<Point2D.Double>(domainPoint, new Point2D.Double(point.x + dPoint.x, point.y + dPoint.y));
        domainPlottable.addChangeListener(this);
    }

    public SpaceParametricSurfacePatch(MultivariateVectorialFunction func, double u0, double v0, double du, double dv) {
        this(func, new Point2D.Double(u0, v0), new Point2D.Double(du, dv));
    }

    public MultivariateVectorialFunction getFunction() {
        return func;
    }

    public void setFunction(MultivariateVectorialFunction func) {
        if (func != null && this.func != func) {
            this.func = func;
        }
    }

    public Point2D.Double getDiffPoint() {
        return dPoint;
    }

    public void setDiffPoint(Point2D.Double dPoint) {
        this.dPoint = dPoint;
    }

    public Point2D.Double getDomainPoint() {
        return domainPoint;
    }

    public void setDomainPoint(Point2D.Double domainPoint) {
        this.domainPoint = domainPoint;
    }

    public VRectangle<Point2D.Double> getDomainPlottable() {
        return domainPlottable;
    }


    //
    //
    // DRAW METHODS
    //
    //

    @Override
    public void stateChanged(ChangeEvent e) {
        if (e.getSource() == domainPlottable) {
            domainPoint.setLocation(domainPlottable.getPoint1());
            dPoint.setLocation(
                    domainPlottable.getPoint2().x-domainPlottable.getPoint1().x,
                    domainPlottable.getPoint2().y-domainPlottable.getPoint1().y );
        }
        super.stateChanged(e);
    }

    transient List<Point3D[]> polys;

    @Override
    public void paintComponent(VisometryGraphics<Point3D> vg) {
        try {
            polys = new ArrayList<Point3D[]>(1);
            Point3D[] patch = new Point3D[4];
            double[] input = new double[]{domainPoint.x, domainPoint.y};
            patch[0] = new Point3D(func.value(input));
            input[0] += dPoint.x;
            patch[1] = new Point3D(func.value(input));
            input[1] += dPoint.y;
            patch[2] = new Point3D(func.value(input));
            input[0] -= dPoint.x;
            patch[3] = new Point3D(func.value(input));
            polys.add(patch);
            ((SpaceGraphics) vg).addToScene(polys);
        } catch (FunctionEvaluationException ex) {
            Logger.getLogger(SpaceParametricSurfacePatch.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(SpaceParametricSurfacePatch.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
