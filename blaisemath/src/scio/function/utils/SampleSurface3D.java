/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package scio.function.utils;

import scio.function.MultivariateVectorialFunction;
import static java.lang.Math.*;

/**
 *
 * @author ae3263
 */
public enum SampleSurface3D implements MultivariateVectorialFunction {
    SPHERE(0, 2*PI, 0, PI) {
        public double[] value(double[] in) {
            double r1 = cos(in[0]);
            double r2 = sin(in[0]);
            return new double[]{ r1 * cos(in[1]), r1 * sin(in[1]), r2 };
        }
    },
    CYLINDER(-1, 1, 0, 2*PI) {
        public double[] value(double[] in) {
            double r1 = 1;
            double r2 = in[0];
            return new double[]{ r1 * cos(in[1]), r1 * sin(in[1]), r2 };
        }
    },
    PARABOLOID(0, 1, 0, 2*PI) {
        public double[] value(double[] in) {
            double r1 = in[0];
            double r2 = in[0]*in[0];
            return new double[]{ r1 * cos(in[1]), r1 * sin(in[1]), r2 };
        }
    },
    CONE(-1, 1, 0, 2*PI) {
        public double[] value(double[] in) {
            double r1 = in[0];
            double r2 = in[0];
            return new double[]{ r1 * cos(in[1]), r1 * sin(in[1]), r2 };
        }
    },
    TORUS(0, 2*PI, 0, 2*PI) {
        public double[] value(double[] in) {
            double r1 = 2+Math.cos(in[0]);
            double r2 = Math.sin(in[0]);
            return new double[]{ r1 * cos(in[1]), r1 * sin(in[1]), r2 };
        }
    },
    MOBIUS(-.5, .5, 0, PI) {
        public double[] value(double[] in) {
            return new double[]{ (2+in[0]*cos(in[1])) * cos(2*in[1]), (2+in[0]*cos(in[1])) * sin(2*in[1]), in[0]*sin(in[1]) };
        }
    };

    public double u0, u1, v0, v1;

    private SampleSurface3D(double u0, double u1, double v0, double v1) {
        this.u0 = u0;
        this.u1 = u1;
        this.v0 = v0;
        this.v1 = v1;
    }
}
