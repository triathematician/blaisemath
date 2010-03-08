/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package scio.function.utils;

import org.apache.commons.math.analysis.MultivariateVectorialFunction;
import static java.lang.Math.*;

/**
 *
 * @author ae3263
 */
public enum DemoSurface3D implements MultivariateVectorialFunction {
    NONE(0, 0, 0, 0) {
        public double[] value(double[] in) {
            return new double[]{0, 0, 0};
        }
    },
    PLANE(0, 1, 0, 1) {
        public double[] value(double[] in) {
            return new double[]{ 1 - in[0] - in[1], in[0], in[1]};
        }
    },
    SPHERE(0, 2*PI, 0, PI) {
        public double[] value(double[] in) {
            double r1 = cos(in[1]);
            double r2 = sin(in[1]);
            return new double[]{ r2 * cos(in[0]), r2 * sin(in[0]), r1 };
        }
    },
    CYLINDER(0, 2*PI, -1, 1) {
        public double[] value(double[] in) {
            double r1 = 1;
            double r2 = in[1];
            return new double[]{ r1 * cos(in[0]), r1 * sin(in[0]), r2 };
        }
    },
    PARABOLOID(0, 2*PI, 0, 1) {
        public double[] value(double[] in) {
            double r1 = in[1];
            double r2 = in[1]*in[1];
            return new double[]{ r1 * cos(in[0]), r1 * sin(in[0]), r2 };
        }
    },
    CONE(0, 2*PI, -1, 1)  {
        public double[] value(double[] in) {
            double r1 = in[1];
            double r2 = in[1];
            return new double[]{ r1 * cos(in[0]), r1 * sin(in[0]), r2 };
        }
    },
    TORUS(0, 2*PI, 0, 2*PI) {
        public double[] value(double[] in) {
            double r1 = 2+Math.cos(in[1]);
            double r2 = Math.sin(in[1]);
            return new double[]{ r1 * cos(in[0]), r1 * sin(in[0]), r2 };
        }
    },
    TWISTED_TORUS(0, 2*PI, 0, 2*PI) {
        public double[] value(double[] in) {
            double r1 = 2+Math.cos(in[1]+in[0]);
            double r2 = Math.sin(in[1]+in[0]);
            return new double[]{ r1 * cos(in[0]), r1 * sin(in[0]), r2 };
        }
    },
    MOBIUS(0, PI, -.5, .5) {
        public double[] value(double[] in) {
            return new double[]{ (2+in[1]*cos(in[0])) * cos(2*in[0]), (2+in[1]*cos(in[0])) * sin(2*in[0]), in[1]*sin(in[0]) };
        }
    };

    public double u0, u1, v0, v1;

    private DemoSurface3D(double u0, double u1, double v0, double v1) {
        this.u0 = u0;
        this.u1 = u1;
        this.v0 = v0;
        this.v1 = v1;
    }
}
