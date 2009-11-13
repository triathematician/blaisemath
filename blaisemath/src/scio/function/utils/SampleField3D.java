/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package scio.function.utils;

import scio.function.MultivariateVectorialFunction;

/**
 *
 * @author ae3263
 */
public enum SampleField3D implements MultivariateVectorialFunction {
    OUT() {
        public double[] value(double[] in) {
            return new double[]{in[0], in[1], in[2]};
        }
    },
    IN() {
        public double[] value(double[] in) {
            return new double[]{-in[0], -in[1], -in[2]};
        }
    },
    WHIRLPOOL() {
        public double[] value(double[] in) {
            return new double[]{-in[1], in[0], -in[2]};
        }
    },
    LORENZ() {
        final double sigma = 10;
        final double rho = 4;
        final double beta = 7/3.;
        public double[] value(double[] in) {
            return new double[]{ sigma*(in[1]-in[0]), in[0]*(10*rho-in[2])-in[1], in[0]*in[1]-beta*in[2] };
        }
    };
}
