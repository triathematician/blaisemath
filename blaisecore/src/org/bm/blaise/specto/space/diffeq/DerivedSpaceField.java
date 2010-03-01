/*
 * DerivedSpaceField.java
 * Created on Nov 6, 2009
 */

package org.bm.blaise.specto.space.diffeq;

import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.MultivariateVectorialFunction;
import scio.coordinate.Point3D;
import scio.coordinate.sample.SampleCoordinateSetGenerator;
import scio.function.utils.MultivariableUtils;
import scio.function.utils.VectorFieldUtils;
import util.ChangeEventHandler;

/**
 * <p>
 *   Offers user opportunity to switch between various "derived" types. The user may specify a base field
 *   and possibly a few others that determine how this is used.
 * </p>
 * @author Elisha Peterson
 * @see scio.function.utils.VectorFieldUtils
 */
public class DerivedSpaceField extends SpaceVectorField {
    MultivariateVectorialFunction orig;
    MultivariateVectorialFunction der1;
    MultivariateVectorialFunction der2;

    DerivedType type = DerivedType.PARALLEL;

    public DerivedSpaceField(
            MultivariateVectorialFunction orig,
            MultivariateVectorialFunction der1,
            MultivariateVectorialFunction der2,
            SampleCoordinateSetGenerator<Point3D> ssg) {
        super(null, ssg);
        this.der1 = der1;
        this.der2 = der2;
        if (this.der2 == null) {
            this.der2 = new MultivariateVectorialFunction() {
                public double[] value(double[] point) throws FunctionEvaluationException, IllegalArgumentException {
                    return new double[]{0, 0, 1};
                }
            };
        }
        setFunction(orig);
    }

    public DerivedSpaceField(
            DerivedType type,
            MultivariateVectorialFunction orig,
            MultivariateVectorialFunction der1,
            MultivariateVectorialFunction der2,
            SampleCoordinateSetGenerator<Point3D> ssg) {
        super(null, ssg);
        this.type = type;
        this.der1 = der1;
        this.der2 = der2;
        if (this.der2 == null) {
            this.der2 = new MultivariateVectorialFunction() {
                public double[] value(double[] point) throws FunctionEvaluationException, IllegalArgumentException {
                    return new double[]{0, 0, 1};
                }
            };
        }
        setFunction(orig);
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
    public MultivariateVectorialFunction getFunction() {
        return orig;
    }

    @Override
    public void setFunction(MultivariateVectorialFunction orig) {
        if (orig != null && this.orig != orig) {
            if (this.orig instanceof ChangeEventHandler) {
                ((ChangeEventHandler)this.orig).removeChangeListener(this);
            }
            this.orig = orig;
            func = type.derived(orig, der1, der2);
            if (orig instanceof ChangeEventHandler) {
                ((ChangeEventHandler)orig).addChangeListener(this);
            }
            fireStateChanged();
        }
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
        },
        CURL(){
            MultivariateVectorialFunction derived(
            MultivariateVectorialFunction func,
            MultivariateVectorialFunction der1,
            MultivariateVectorialFunction der2) {
                return MultivariableUtils.approximateCurlOf(func, 1e-6);
            }
        };
        
        abstract MultivariateVectorialFunction derived(
            MultivariateVectorialFunction func,
            MultivariateVectorialFunction der1,
            MultivariateVectorialFunction der2);
    }
}
