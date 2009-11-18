/*
 * DerivedSpaceField.java
 * Created on Nov 6, 2009
 */

package org.bm.blaise.specto.space.diffeq;

import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.MultivariateVectorialFunction;
import scio.coordinate.P3D;
import scio.coordinate.sample.SampleCoordinateSetGenerator;
import scio.function.utils.SampleField3D;
import scio.function.utils.VectorFieldUtils;

/**
 * Offers user opportunity to switch between various "derived" types.
 * @author ae3263
 */
public class DerivedSpaceField extends SpaceVectorField {
    MultivariateVectorialFunction orig;
    MultivariateVectorialFunction der1;
    MultivariateVectorialFunction der2;

    DerivedType type = DerivedType.PARALLEL;

    public DerivedSpaceField(
            MultivariateVectorialFunction func,
            MultivariateVectorialFunction der1,
            MultivariateVectorialFunction der2,
            SampleCoordinateSetGenerator<P3D> ssg) {
        super(func, ssg);
        this.der1 = der1;
        this.der2 = der2;
        if (this.der2 == null) {
            this.der2 = new MultivariateVectorialFunction() {
                public double[] value(double[] point) throws FunctionEvaluationException, IllegalArgumentException {
                    return new double[]{0, 0, 1};
                }
            };
        }
        this.orig = func;
        this.func = type.derived(orig, der1, der2);
    }

    public DerivedType getType() {
        return type;
    }

    public void setType(DerivedType type) {
        this.type = type;
        this.func = type.derived(orig, der1, der2);
        fireStateChanged();
    }

    @Override
    public void setSample(SampleField3D sample) {
        super.setSample(sample);
        orig = sample;
    }

    public static enum DerivedType {
        ORIGINAL(){
            MultivariateVectorialFunction derived(
            MultivariateVectorialFunction func,
            MultivariateVectorialFunction der1,
            MultivariateVectorialFunction der2) {
                return func;
            }
        },
        NORMALIZED(){
            MultivariateVectorialFunction derived(
            MultivariateVectorialFunction func,
            MultivariateVectorialFunction der1,
            MultivariateVectorialFunction der2) {
                return VectorFieldUtils.normalizedField(func);
            }
        },
        PARALLEL(){
            MultivariateVectorialFunction derived(
            MultivariateVectorialFunction func,
            MultivariateVectorialFunction der1,
            MultivariateVectorialFunction der2) {
                return VectorFieldUtils.parallelField(func, der1);
            }
        },
        UNPARALLEL(){
            MultivariateVectorialFunction derived(
            MultivariateVectorialFunction func,
            MultivariateVectorialFunction der1,
            MultivariateVectorialFunction der2) {
                return VectorFieldUtils.unParallelField(func, der1);
            }
        },
        PERPENDICULAR(){
            MultivariateVectorialFunction derived(
            MultivariateVectorialFunction func,
            MultivariateVectorialFunction der1,
            MultivariateVectorialFunction der2) {
                return VectorFieldUtils.perpField(der1, der2);
            }
        },
        BIPERP(){
            MultivariateVectorialFunction derived(
            MultivariateVectorialFunction func,
            MultivariateVectorialFunction der1,
            MultivariateVectorialFunction der2) {
                return VectorFieldUtils.biPerpField(func, der1, der2);
            }
        },
        BIPARALLEL(){
            MultivariateVectorialFunction derived(
            MultivariateVectorialFunction func,
            MultivariateVectorialFunction der1,
            MultivariateVectorialFunction der2) {
                return VectorFieldUtils.parallelField(func, der1, der2);
            }
        };
        
        abstract MultivariateVectorialFunction derived(
            MultivariateVectorialFunction func,
            MultivariateVectorialFunction der1,
            MultivariateVectorialFunction der2);
    }
}
