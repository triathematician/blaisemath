/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bm.blaise.scio.function.utils;

import org.apache.commons.math.analysis.MultivariateVectorialFunction;


/**
 *
 * @author ae3263
 */
public enum DemoField3D implements MultivariateVectorialFunction {
    NONE() {
        public double[] value(double[] in) {
            return new double[]{0, 0, 0};
        }
    },
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
    WHIRLPOOL2() {
        public double[] value(double[] in) {
            return new double[]{-in[1]/Math.sqrt(in[0]*in[0]+in[1]*in[1]), in[0]/Math.sqrt(in[0]*in[0]+in[1]*in[1]), -in[2]};
        }
    },
    LORENZ() {
        final double sigma = 10;
        final double rho = 28;
        final double beta = 8/3.;
        final double scaleDown = 10;
        public double[] value(double[] in) {
            in[0] *= 20;
            in[1] *= 20;
            in[2] = 20*(in[2]+1);
            return new double[]{ sigma*(in[1]-in[0]) / scaleDown, (in[0]*(rho-in[2])-in[1]) / scaleDown, (in[0]*in[1]-beta*in[2]) / scaleDown };
        }
    };
}
