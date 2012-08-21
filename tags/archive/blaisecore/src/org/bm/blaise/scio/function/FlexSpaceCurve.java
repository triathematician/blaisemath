/*
 * FlexSpaceCurve.java
 * Created on Jan 9, 2010
 */

package org.bm.blaise.scio.function;

import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.UnivariateVectorialFunction;
import scio.function.utils.DemoCurve3D;

/**
 * This class is a class that wraps an actual function together with demo functions,
 * allowing for automated generation of "demo" curves and the ability to swap between
 * various functions. This allows for classes that have beans using this kind of function
 * to be adjustable.
 * 
 * @author ae3263
 * @see scio.function.utils.DemoCurve3D
 */
public class FlexSpaceCurve extends FlexFunctionAbstract<UnivariateVectorialFunction> implements UnivariateVectorialFunction {

    DemoCurve3D demoFunc = DemoCurve3D.NONE;

    public FlexSpaceCurve() {
        super();
    }
    
    public FlexSpaceCurve(UnivariateVectorialFunction baseFunc) {
        super(baseFunc);
    }

    public DemoCurve3D getDemoFunc() {
        return demoFunc;
    }

    public void setDemoFunc(DemoCurve3D demoFunc) {
        if (this.demoFunc != demoFunc) {
            this.demoFunc = demoFunc;
            fireStateChanged();
        }
    }

    /** Returns true if the class is currently using the "demo" function. */
    protected boolean isDemo() {
        return baseFunc==null || demoFunc!=DemoCurve3D.NONE;
    }

    public double[] value(double x) throws FunctionEvaluationException {
        return isDemo() ? demoFunc.value(x) : baseFunc.value(x);
    }
}
