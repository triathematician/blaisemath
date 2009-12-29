/**
 * TwoCurveSurface.java
 * Created on Nov 19, 2009
 */

package org.bm.blaise.specto.space.function;

import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.MultivariateVectorialFunction;
import org.apache.commons.math.analysis.UnivariateVectorialFunction;

/**
 * <p>
 *    This class represents a parametric surface bounded between two curves.
 * </p>
 * @author Elisha Peterson
 */
public class TwoCurveSurface extends SpaceParametricSurface {

    public TwoCurveSurface(UnivariateVectorialFunction func1, UnivariateVectorialFunction func2, double u0, double u1) {
        super(spanFunc(func1, func2), u0, u1, 0, 1);
    }

    public static MultivariateVectorialFunction spanFunc(final UnivariateVectorialFunction func1, final UnivariateVectorialFunction func2) {
        return new MultivariateVectorialFunction() {
            public double[] value(double[] point) throws FunctionEvaluationException, IllegalArgumentException {
                double[] p1 = func1.value(point[0]);
                double[] p2 = func2.value(point[0]);
                return new double[]{
                    p1[0] + point[1] * (p2[0]-p1[0]),
                    p1[1] + point[1] * (p2[1]-p1[1]),
                    p1[2] + point[1] * (p2[2]-p1[2])
                };
            }
        };
    }
}
