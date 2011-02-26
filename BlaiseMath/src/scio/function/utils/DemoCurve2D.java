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
public enum DemoCurve2D implements UnivariateVectorialFunction {
    NONE(0,0) {
        public double[] value(double x) {
            return null;
        }
    },
    SEGMENT(-1,2) {
        public double[] value(double x) {
            return new double[]{x-1, .5*x};
        }
    },
    CIRCLE(0, 2*PI) {
        public double[] value(double x) {
            return new double[]{cos(x), sin(x)};
        }
    },
    ARCHIMEDEAN_SPIRAL(0,2) {
        public double[] value(double x) {
            return new double[]{ x*cos(2*PI*x), x*sin(2*PI*x) };
        }
    },
    DOUBLE_BUBBLE(0, 2*PI) {
        public double[] value(double x) {
            double cx = cos(x);
            double r = 1.5 + 2.5 * cx * cx;
            return new double[] { r * cx, r * sin(x) };
        }
    };

    public double t0, t1;

    DemoCurve2D(double t0, double t1) {
        this.t0 = t0;
        this.t1 = t1;
    }
}
