/**
 * PlaneParticleVectorField.java
 * Created on Sep 24, 2009
 */
package org.bm.blaise.specto.space.particle;

import java.util.Arrays;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.MultivariateVectorialFunction;
import org.bm.blaise.specto.visometry.Visometry;
import org.bm.blaise.specto.visometry.VisometryChangeListener;
import org.bm.blaise.specto.visometry.VisometryGraphics;
import org.bm.blaise.specto.plottable.VParticleField;
import org.bm.blaise.specto.space.SpaceVisometry;
import scio.coordinate.Point3D;
import scio.random.RandomCoordinateGenerator;
import util.ChangeEventHandler;

/**
 * <p>
 *   <code>SpaceParticleVectorField</code> uses an underlying vector field to push around little
 *   particles representing the "flow" of the field.
 *   The field is specified by an underlying
 *   function with 3 inputs and 3 outputs (a <code>MultivariateVectorialFunction</code>). The points
 *   are generated automatically based on the visometry.
 * </p>
 *
 * @author Elisha Peterson
 */
public class SpaceParticleVectorField extends VParticleField<Point3D> implements VisometryChangeListener {

    //
    //
    // PROPERTIES
    //
    //
    /** Underlying function */
    MultivariateVectorialFunction func;
    
    /** Used to generator random points. */
    RandomCoordinateGenerator<Point3D> rpg;

    //
    //
    // CONSTRUCTORS
    //
    //
    /**
     * Construct the vector field.
     * @param func underlying function that determines the vectors
     */
    public SpaceParticleVectorField(MultivariateVectorialFunction func) {
        setFunction(func);
        this.numParticles = 400;
    }


    //
    // BEAN PATTERNS
    //

    /** @return function describing the field */
    public MultivariateVectorialFunction getFunction() {
        return func;
    }

    /** Sets the function for the field.
     * @param func the function
     */
    public void setFunction(MultivariateVectorialFunction func) {
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
    // PAINT METHODS
    //

    public void visometryChanged(Visometry vis, VisometryGraphics canvas) {
        SpaceVisometry sv = (SpaceVisometry) vis;
        rpg = sv;
    }

    @Override
    public void initPaths() {
        paths = new Point3D[numParticles][trailLength];
        for (int i = 0; i < paths.length; i++) {
            paths[i] = getNewPath(trailLength);
        }
    }

    @Override
    public Point3D[] getNewPath(int length) {
        Point3D[] result = new Point3D[length];
        Point3D random = rpg.randomValue();
        for (int i = 0; i < length; i++) {
            result[i] = random;
        }
        return result;
    }

    @Override
    public void advancePath(Point3D[] path) {
        double tScale = .001 * speed;
        double[] input = new double[3];
        double[] result = new double[3];
        Point3D last;
        for (int j = 0; j < path.length - 1; j++) {
            path[j] = path[j + 1];
        }
        if (path.length != 0) {
            last = path[path.length - 1];
            input[0] = last.x;
            input[1] = last.y;
            input[2] = last.z;
            try {
                result = func.value(input);
            } catch (FunctionEvaluationException ex) {
                result = input;
            } catch (IllegalArgumentException ex) {
                result = input;
            }
            path[path.length - 1] = new Point3D(input[0] + result[0] * tScale, input[1] + result[1] * tScale, input[2] + result[2] * tScale);
        }
    }
    
    @Override
    public String toString() {
        return "Particle Visualization of DE Solution";
    }
}
