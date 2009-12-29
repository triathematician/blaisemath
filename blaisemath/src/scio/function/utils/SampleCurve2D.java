/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * SampleCurve2D.java
 * Created on Nov 23, 2009
 */

package scio.function.utils;

import org.apache.commons.math.analysis.UnivariateVectorialFunction;
import static java.lang.Math.*;

/**
 * <p>
 *    This class ...
 * </p>
 * @author Elisha Peterson
 */
public enum SampleCurve2D implements UnivariateVectorialFunction {
    SEGMENT() {
        public double[] value(double x) {
            return new double[]{x-1, .5*x};
        }
    },
    CIRCLE() {
        public double[] value(double x) {
            return new double[]{cos(x), sin(x)};
        }
    },
    DOUBLE_BUBBLE() {
        public double[] value(double x) {
            double cx = cos(x);
            double r = 1.5 + 2.5 * cx * cx;
            return new double[] { r * cx, r * sin(x) };
        }
    };
}
