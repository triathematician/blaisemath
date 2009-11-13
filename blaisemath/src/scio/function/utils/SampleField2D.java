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
public enum SampleField2D implements MultivariateVectorialFunction {
    OUT() {
        public double[] value(double[] in) {
            return new double[]{in[0], in[1]};
        }
    },
    IN() {
        public double[] value(double[] in) {
            return new double[]{-in[0], -in[1]};
        }
    },
    AROUND() {
        public double[] value(double[] in) {
            return new double[]{-in[1], in[0]};
        }
    },
    LINEAR() {
        public double[] value(double[] in) {
            return new double[]{in[0]+in[1], in[0]-in[1]};
        }
    },
    CONSERVATIVE1() {
        public double[] value(double[] in) {
            return new double[]{in[1]*Math.cos(in[0]*in[1]), in[0]*Math.cos(in[0]*in[1])};
        }
    },
    LOGISTIC1() {
        public double[] value(double[] in) {
            return new double[]{1, in[0]*(1-in[0])};
        }
    },
    LOGISTIC2() {
        public double[] value(double[] in) {
            return new double[]{in[1]*(1-in[1]), in[0]*(1-in[0])};
        }
    };
}
