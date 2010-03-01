/*
 * SpaceParametricSurface.java
 * Created Oct 2009
 */

package org.bm.blaise.specto.space.function;

import java.awt.geom.Point2D;
import org.bm.blaise.specto.space.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.event.ChangeEvent;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.MultivariateVectorialFunction;
import org.bm.blaise.specto.plottable.VRectangle;
import org.bm.blaise.specto.visometry.AbstractPlottable;
import org.bm.blaise.specto.visometry.VisometryGraphics;
import scio.coordinate.Point3D;
import scio.coordinate.MaxMinDomain;
import scio.coordinate.sample.RealIntervalSampler;
import util.ChangeEventHandler;

/**
 * <p>
 *  Constructs and displays a 3D parametric surface.
 * </p>
 * @author Elisha Peterson
 */
public class SpaceParametricSurface extends AbstractPlottable<Point3D> {

    /** The underlying function, 2 inputs, 2 outputs */
    MultivariateVectorialFunction func;
    /** Range of u-values for display purposes */
    RealIntervalSampler domainU;
    /** Range of v-valeus for display purposes */
    RealIntervalSampler domainV;
    /** Stores rectangle used to adjust the range. */
    VRectangle<Point2D.Double> domainPlottable;
    /** Style used to display the surface */
    GridStyle gridStyle = GridStyle.REGULAR;

    public SpaceParametricSurface(MultivariateVectorialFunction func, Point2D.Double min, Point2D.Double max) {
        setFunction(func);
        setDomainU(new RealIntervalSampler(min.x, max.x, 16));
        setDomainV(new RealIntervalSampler(min.y, max.y, 16));
        domainPlottable = new VRectangle<Point2D.Double>(min, max);
        domainPlottable.addChangeListener(this);
    }

    public SpaceParametricSurface(MultivariateVectorialFunction func, double u0, double u1, double v0, double v1) {
        this(func, new Point2D.Double(u0, v0), new Point2D.Double(u1, v1));
    }

    //
    // GETTERS & SETTERS
    //

    public MultivariateVectorialFunction getFunction() {
        return func;
    }

    public void setFunction(MultivariateVectorialFunction func) {
        if (func != null && this.func != func) {
            if (this.func instanceof ChangeEventHandler) {
                ((ChangeEventHandler)this.func).removeChangeListener(this);
            }
            this.func = func;
            if (func instanceof ChangeEventHandler) {
                ((ChangeEventHandler)func).addChangeListener(this);
            }
            needsComputation = true;
            fireStateChanged();
        }
    }

    public RealIntervalSampler getDomainU() {
        return domainU;
    }

    public void setDomainU(MaxMinDomain<Double> range) {
        if (range != null) {
            if (range instanceof RealIntervalSampler) {
                this.domainU = (RealIntervalSampler) range;
            } else {
                this.domainU.setMin(range.getMin());
                this.domainU.setMin(range.getMax());
                this.domainU.setMinInclusive(range.isMinInclusive());
                this.domainU.setMaxInclusive(range.isMaxInclusive());
            }
            needsComputation = true;
        }
    }

    public RealIntervalSampler getDomainV() {
        return domainV;
    }

    public void setDomainV(MaxMinDomain<Double> range) {
        if (range != null) {
            if (range instanceof RealIntervalSampler) {
                this.domainV = (RealIntervalSampler) range;
            } else {
                this.domainV.setMin(range.getMin());
                this.domainV.setMin(range.getMax());
                this.domainV.setMinInclusive(range.isMinInclusive());
                this.domainV.setMaxInclusive(range.isMaxInclusive());
            }
            needsComputation = true;
        }
    }

    public GridStyle getGridStyle() {
        return gridStyle;
    }

    public void setGridStyle(GridStyle gridStyle) {
        this.gridStyle = gridStyle;
        needsComputation = true;
        fireStateChanged();
    }

    public VRectangle<Point2D.Double> getDomainPlottable() {
        return domainPlottable;
    }

    //
    // EVENT HANDLING
    //

    @Override
    public void stateChanged(ChangeEvent e) {
        if (e.getSource() == domainPlottable) {
            domainU.setMin(domainPlottable.getPoint1().x);
            domainU.setMax(domainPlottable.getPoint2().x);
            domainV.setMin(domainPlottable.getPoint1().y);
            domainV.setMax(domainPlottable.getPoint2().y);
            needsComputation = true;
        }
        super.stateChanged(e);
    }

    //
    // DRAW METHODS
    //

    transient boolean needsComputation = true;
    transient List<Point3D[]> polys;

    @Override
    public void paintComponent(VisometryGraphics<Point3D> vg) {
        polys = getPolys(func);
        needsComputation = false;
        ((SpaceGraphics) vg).addToScene(polys);
    }
    
    List<Point3D[]> getPolys(MultivariateVectorialFunction func) {
        int nx = domainU.getNumSamples();
        int ny = domainV.getNumSamples();
        if (nx < 1 || ny < 1) {
            return Collections.EMPTY_LIST;
        }
        // here we compute the values
        double[] input = new double[2];
        Point3D[][] arr = new Point3D[nx][ny];
        try {
            int i = 0;
            for (double x : domainU.getSamples()) {
                int j = 0;
                for (double y : domainV.getSamples()) {
                    input[0] = x;
                    input[1] = y;
                    arr[i][j] = new Point3D(func.value(input));
                    j++;
                }
                i++;
            }
        } catch (FunctionEvaluationException e) {
            System.out.println("Unable to evaluate function in SpaceParamertricSurface");
        }
        if (gridStyle == GridStyle.REGULAR || gridStyle == GridStyle.NO_GRID) {
            // here we create and return the polygons
            List<Point3D[]> result = new ArrayList<Point3D[]>((nx-1) * (ny-1));
            for (int i = 0; i < nx-1; i++) {
                for (int j = 0; j < ny-1; j++) {
                    result.add(new Point3D[]{ arr[i][j], arr[i+1][j], arr[i+1][j+1], arr[i][j+1] });
                }
            }
            return result;
        } else {
            // here we create and return writeframe
            List<Point3D[]> result = new ArrayList<Point3D[]>((nx-1)*ny+nx*(ny-1));
            if (gridStyle != GridStyle.GRID_V) {
                for (int i = 0; i < nx-1; i++) {
                    for (int j = 0; j < ny; j++) {
                        result.add(new Point3D[]{ arr[i][j], arr[i+1][j] });
                    }
                }
            }
            if (gridStyle != GridStyle.GRID_U) {
                for (int i = 0; i < nx; i++) {
                    for (int j = 0; j < ny-1; j++) {
                        result.add(new Point3D[]{ arr[i][j], arr[i][j+1] });
                    }
                }
            }
            return result;
        }
    }

    @Override
    public String toString() {
        return "Parametric Surface [" + domainU + ", " + domainV + "]";
    }



    //
    // GRID STYLE
    //

    /** Controls display of grid lines and the surface. */
    public enum GridStyle {
        /** Display grid lines and surface. */
        REGULAR,
        /** Display surface only. */
        NO_GRID,
        /** Display grid lines only. */
        WIREFRAME,
        /** Display first-variable-constant lines and surface. */
        GRID_U,
        /** Display second-variable-constant lines and surface. */
        GRID_V,
        /** Display first-variable-constant lines only. */
        GRID_ONLY_U,
        /** Display second-variable-constant lines only. */
        GRID_ONLY_V;
    }

}
