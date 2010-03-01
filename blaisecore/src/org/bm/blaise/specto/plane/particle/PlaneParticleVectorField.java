/**
 * PlaneParticleVectorField.java
 * Created on Sep 24, 2009
 */
package org.bm.blaise.specto.plane.particle;

import org.bm.blaise.specto.plane.*;
import java.awt.geom.Point2D;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.MultivariateVectorialFunction;
import org.bm.blaise.specto.visometry.Visometry;
import org.bm.blaise.specto.visometry.VisometryChangeListener;
import org.bm.blaise.specto.visometry.VisometryGraphics;
import org.bm.blaise.specto.plottable.VParticleField;
import scio.random.RandomCoordinateGenerator;
import util.ChangeEventHandler;

/**
 * <p>
 *   <code>PlaneParticleVectorField</code> uses an underlying vector field to push around little
 *   particles representing the "flow" of the field.
 *   The field is specified by an underlying
 *   function with 2 inputs and 2 outputs (a <code>MultivariateVectorialFunction</code>).
 * </p>
 *
 * @author Elisha Peterson
 */
public class PlaneParticleVectorField extends VParticleField<Point2D.Double> implements VisometryChangeListener {

    //
    //
    // PROPERTIES
    //
    //
    /** Underlying function */
    MultivariateVectorialFunction func;
    
    /** Used to generator random points. */
    RandomCoordinateGenerator<Point2D.Double> rpg;

    //
    //
    // CONSTRUCTORS
    //
    //
    /**
     * Construct the vector field.
     * @param func underlying function that determines the vectors
     */
    public PlaneParticleVectorField(MultivariateVectorialFunction func, RandomCoordinateGenerator<Point2D.Double> rpg) {
        setFunc(func);
        this.rpg = rpg;
        this.numParticles = 500;
    }


    //
    //
    // BEAN PATTERNS
    //
    //
    /** @return function describing the field */
    public MultivariateVectorialFunction getFunc() {
        return func;
    }

    /** Sets the function for the field.
     * @param func the function
     */
    public void setFunc(MultivariateVectorialFunction func) {
        if (func != null && this.func != func) {
            if (this.func instanceof ChangeEventHandler) {
                ((ChangeEventHandler)this.func).removeChangeListener(this);
            }
            this.func = func;
            if (func instanceof ChangeEventHandler) {
                ((ChangeEventHandler)func).addChangeListener(this);
            }
            fireStateChanged();
        }
    }

    //
    //
    // PAINT METHODS
    //
    //

    transient double winScaleFactor;

    public void visometryChanged(Visometry vis, VisometryGraphics canvas) {
        PlaneVisometry pv = (PlaneVisometry) vis;
        rpg = pv;
        winScaleFactor = 0.5*(Math.abs(pv.getScaleX()) + Math.abs(pv.getScaleY()));
    }

    @Override
    public void initPaths() {
        paths = new Point2D.Double[numParticles][trailLength];
        for (int i = 0; i < paths.length; i++) {
            paths[i] = getNewPath(trailLength);
        }
    }

    @Override
    public Point2D.Double[] getNewPath(int length) {
        Point2D.Double[] result = new Point2D.Double[length];
        Point2D.Double random = rpg.randomValue();
        for (int i = 0; i < length; i++) {
            result[i] = random;
        }
        return result;
    }

    @Override
    public void advancePath(Point2D.Double[] path) {
        double tScale = 0.000001 * winScaleFactor * speed;
        double[] input = new double[2];
        double[] result = new double[2];
        Point2D.Double last;
        for (int j = 0; j < path.length - 1; j++) {
            path[j] = path[j + 1];
        }
        if (path.length != 0) {
            last = path[path.length - 1];
            input[0] = last.x;
            input[1] = last.y;
            try {
                result = func.value(input);
            } catch (FunctionEvaluationException ex) {
                result = input;
            } catch (IllegalArgumentException ex) {
                result = input;
            }
            path[path.length - 1] = new Point2D.Double(input[0] + result[0] * tScale, input[1] + result[1] * tScale);
        }
    }

    @Override
    public String toString() {
        return "Particle Visualization of DE Solution";
    }
}
