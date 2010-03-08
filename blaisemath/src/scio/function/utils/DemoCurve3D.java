/**
 * SampleCurve2D.java
 * Created on Nov 23, 2009
 */

package scio.function.utils;

import org.apache.commons.math.analysis.UnivariateVectorialFunction;
import static java.lang.Math.*;

/**
 * <p>
 *    This <code>enum</code> contains several sample 3d curve functions.
 * </p>
 * @author Elisha Peterson
 */
public enum DemoCurve3D implements UnivariateVectorialFunction {
    NONE(0,0) {
        public double[] value(double x) {
            return new double[]{0, 0, 0};
        }
    },
    SEGMENT(-1,2) {
        public double[] value(double x) {
            return new double[]{x-1, .5*x, x+1};
        }
    },
    CIRCLE(0, 2*PI) {
        public double[] value(double x) {
            return new double[]{cos(x), sin(x), 0};
        }
    },
    SPIRAL(0, 2) {
        public double[] value(double x) {
            return new double[]{cos(2*PI*x), sin(2*PI*x), x};
        }
    },
    FLATTER_SPIRAL(0, 2) {
        public double[] value(double x) {
            return new double[]{cos(4*PI*x), sin(4*PI*x), x};
        }
    },
    TREFOIL_TOROIDAL_SPIRAL(0, 2*PI) {
        public double[] value(double x) {
            return new double[]{(2+cos(3*x))*cos(2*x), (2+cos(3*x))*sin(2*x), sin(3*x)};
        }
    },
    CINQUEFOIL_TOROIDAL_SPIRAL(0, 2*PI) {
        public double[] value(double x) {
            return new double[]{(2+cos(5*x))*cos(2*x), (2+cos(5*x))*sin(2*x), sin(5*x)};
        }
    };

    public double t0,t1;

    DemoCurve3D(double t0, double t1) {
        this.t0 = t0;
        this.t1 = t1;
    }
}
