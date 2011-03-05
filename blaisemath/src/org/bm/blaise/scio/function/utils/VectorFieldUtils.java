/*
 * VectorFieldUtils.java
 * Created on Nov 6, 2009
 */

package org.bm.blaise.scio.function.utils;

import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.MultivariateVectorialFunction;

/**
 * Contains utility classes for deriving new vector fields based on old ones, e.g.
 * parallelism, perpendicularism, etc.
 *
 * @author Elisha Peterson
 */
public class VectorFieldUtils {

    /** Returns filed that is normalized version of that supplied. */
    public static MultivariateVectorialFunction normalizedField(MultivariateVectorialFunction vf) { 
        return new Direction(vf); }

    /** Returns field that is the part of the supplied field parallel to a second field. */
    public static MultivariateVectorialFunction parallelField(MultivariateVectorialFunction vf, MultivariateVectorialFunction par) { 
        return new Parallel(vf, par); }

    /** Returns field that is the part of the supplied field parallel to a second field. */
    public static MultivariateVectorialFunction unParallelField(MultivariateVectorialFunction vf, MultivariateVectorialFunction par) {
        return new NonParallel(vf, par); }

    /** Returns field that is the part of the supplied field parallel to two other fields. */
    public static MultivariateVectorialFunction parallelField(MultivariateVectorialFunction vf, MultivariateVectorialFunction par1, MultivariateVectorialFunction par2) { 
        return new NonParallel(vf, new Perp(par1, par2)); }

    /** Returns part of field that is perpendicular to two other fields. */
    public static MultivariateVectorialFunction biPerpField(MultivariateVectorialFunction vf, MultivariateVectorialFunction vf1, MultivariateVectorialFunction vf2) {
        return new Parallel(vf, new Perp(vf1, vf2)); }

    /** Returns field that is perpendicular to two other fields. */
    public static MultivariateVectorialFunction perpField(MultivariateVectorialFunction vf1, MultivariateVectorialFunction vf2) {
        return new Perp(vf1, vf2); }



    /** Normalized version of another field. */
    static class Direction implements MultivariateVectorialFunction {
        MultivariateVectorialFunction vf;
        /** Construct this derived field
         * @param vf the original vector field */
        public Direction(MultivariateVectorialFunction vf) { this.vf = vf; }
        /** Value returned is the part of the first field in the direction of the second field. */
        public double[] value(double[] point) throws FunctionEvaluationException, IllegalArgumentException {
            int n = point.length;
            double[] result = vf.value(point);
            double length = 0;
            for (int i = 0; i < n; i++) {
                length += result[i] * result[i];
            }
            length = Math.sqrt(length);
            for (int i = 0; i < n; i++) {
                result[i] /= length;
            }
            return result;
        }
    }
    
    /** Restricts field to part parallel to another field. */
    static class Parallel implements MultivariateVectorialFunction {
        MultivariateVectorialFunction vf;
        MultivariateVectorialFunction par;
        /** Construct this derived field
         * @param vf the original vector field
         * @param par a field that the resulting field will be parallel to */
        public Parallel(MultivariateVectorialFunction vf, MultivariateVectorialFunction par) {
            this.vf = vf;
            this.par = par;
        }
        /** Value returned is the part of the first field in the direction of the second field. */
        public double[] value(double[] point) throws FunctionEvaluationException, IllegalArgumentException {
            int n = point.length;
            double[] r1 = vf.value(point);
            double[] r2 = par.value(point);
            double dot1 = 0;
            double dot2 = 0;
            for (int i = 0; i < n; i++) {
                dot1 += r1[i] * r2[i];
                dot2 += r2[i] * r2[i];
            }
            dot1 /= dot2;
            double[] result = new double[n];
            for (int i = 0; i < result.length; i++) {
                result[i] = r2[i] * dot1;
            }
            return result;
        }
    }

    /** Restricts field to part that is NOT parallel to another field. */
    static class NonParallel implements MultivariateVectorialFunction {
        MultivariateVectorialFunction func1;
        MultivariateVectorialFunction nPar;
        /** Construct this derived field
         * @param vf the original vector field
         * @param nPar a field that the resulting field... */
        public NonParallel(MultivariateVectorialFunction vf, MultivariateVectorialFunction nPar) {
            this.func1 = vf;
            this.nPar = nPar;
        }
        /** Value returned is the part of the first field in the direction of the second field. */
        public double[] value(double[] point) throws FunctionEvaluationException, IllegalArgumentException {
            int n = point.length;
            double[] r1 = func1.value(point);
            double[] r2 = nPar.value(point);
            double dot1 = 0;
            double dot2 = 0;
            for (int i = 0; i < n; i++) {
                dot1 += r1[i] * r2[i];
                dot2 += r2[i] * r2[i];
            }
            dot1 /= dot2;
            double[] result = new double[n];
            for (int i = 0; i < result.length; i++) {
                result[i] = r1[i] - r2[i] * dot1;
            }
            return result;
        }
    }

    /** Restricts field to part that is NOT parallel to another field. */
    static class Perp implements MultivariateVectorialFunction {
        MultivariateVectorialFunction perp1;
        MultivariateVectorialFunction perp2;
        /** Construct this derived field
         * @param perp1 first field perpendicular to
         * @param perp2 second field perpendicular to */
        public Perp(MultivariateVectorialFunction perp1, MultivariateVectorialFunction perp2) {
            this.perp1 = perp1;
            this.perp2 = perp2;
        }
        /** Value returned is the part of the first field in the direction of the second field. */
        public double[] value(double[] point) throws FunctionEvaluationException, IllegalArgumentException {
            int n = point.length;
            if (! (n==2 || n==3)) {
                throw new IllegalArgumentException("PerpVectorField intended for use in dimension 2 or 3 only!");
            }
            double[] r1 = perp1.value(point);
            double[] r2 = perp2.value(point);

            double[] result = null;
            if (r1.length == 2) {
                result = new double[]{0, 0, r1[0] * r2[1] - r1[1] * r2[0]};
            } else if (r1.length == 3) {
                result = new double[]{
                    r1[1] * r2[2] - r1[2] * r2[1],
                    r1[2] * r2[0] - r1[0] * r2[2],
                    r1[0] * r2[1] - r1[1] * r2[0]};
            }
            //System.out.println("val1 = "+Arrays.toString(r1)+", val2 = "+Arrays.toString(r2)+", vec = " + Arrays.toString(result));
            return result;
        }
    }
    
}
