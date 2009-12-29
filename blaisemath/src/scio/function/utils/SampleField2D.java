/*
 * SampleField2D.java
 * Created Nov 2009
 */

package scio.function.utils;

import org.apache.commons.math.analysis.DifferentiableMultivariateVectorialFunction;
import org.apache.commons.math.analysis.MultivariateMatrixFunction;
import static java.lang.Math.*;

/**
 * Contains sample 2d vector fields.
 * 
 * @author ae3263
 */
public enum SampleField2D implements DifferentiableMultivariateVectorialFunction {
    OUT() {
        public double[] value(double[] in) {
            return new double[]{in[0], in[1]};
        }
        public MultivariateMatrixFunction jacobian() {
            return new MultivariateMatrixFunction() {
                public double[][] value(double[] point) {
                    return new double[][] { { 1, 0}, {0, 1} };
                }
            };
        }
    },
    IN() {
        public double[] value(double[] in) {
            return new double[]{-in[0], -in[1]};
        }
        public MultivariateMatrixFunction jacobian() {
            return new MultivariateMatrixFunction() {
                public double[][] value(double[] point) {
                    return new double[][] { { -1, 0}, {0, -1} };
                }
            };
        }
    },
    AROUND() {
        public double[] value(double[] in) {
            return new double[]{-in[1], in[0]};
        }
        public MultivariateMatrixFunction jacobian() {
            return new MultivariateMatrixFunction() {
                public double[][] value(double[] point) {
                    return new double[][] { { 0, -1}, {1, 0} };
                }
            };
        }
    },
    LINEAR() {
        public double[] value(double[] in) {
            return new double[]{in[0]+in[1], in[0]-in[1]};
        }
        public MultivariateMatrixFunction jacobian() {
            return new MultivariateMatrixFunction() {
                public double[][] value(double[] point) {
                    return new double[][] { { 1, 1}, {1, -1} };
                }
            };
        }
    },
    CONSERVATIVE1() {
        public double[] value(double[] in) {
            return new double[]{in[1]*cos(in[0]*in[1]), in[0]*cos(in[0]*in[1])};
        }
        public MultivariateMatrixFunction jacobian() {
            return new MultivariateMatrixFunction() {
                public double[][] value(double[] in) {
                    return new double[][] {
                        { -in[1]*in[1]*sin(in[0]*in[1]), cos(in[0]*in[1]) -in[1]*in[0]*sin(in[0]*in[1]) },
                        { cos(in[0]*in[1]) - in[1]*in[0]*sin(in[0]*in[1]), -in[0]*in[0]*sin(in[0]*in[1])  } };
                }
            };
        }
    },
    LOGISTIC1() {
        public double[] value(double[] in) {
            return new double[]{1, in[0]*(1-in[0])};
        }
        public MultivariateMatrixFunction jacobian() {
            return new MultivariateMatrixFunction() {
                public double[][] value(double[] in) {
                    return new double[][] { { 0, 0}, {1 - 2 * in[0], 0} };
                }
            };
        }
    },
    LOGISTIC2() {
        public double[] value(double[] in) {
            return new double[]{in[1]*(1-in[1]), in[0]*(1-in[0])};
        }
        public MultivariateMatrixFunction jacobian() {
            return new MultivariateMatrixFunction() {
                public double[][] value(double[] in) {
                    return new double[][] { { 0, 1-2*in[1]}, {1-2*in[0], 0} };
                }
            };
        }
    },
    WACKY() {
        public double[] value(double[] in) {
            return new double[]{cos(in[0]+in[1])*3 + in[1], sin(in[1]+in[0])*3 - in[0]};
        }
        public MultivariateMatrixFunction jacobian() {
            return new MultivariateMatrixFunction() {
                public double[][] value(double[] in) {
                    return new double[][] { 
                        { -3*sin(in[0]+in[1]), -3*sin(in[0]+in[1]) + 1 },
                        { -1 + 3*cos(in[0]+in[1]), 3*cos(in[0]+in[1])} };
                }
            };
        }
    };
}
