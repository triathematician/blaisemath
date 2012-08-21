/*
 * FlexSpaceSurface.java
 * Created on Jan 9, 2010
 */

package org.bm.blaise.scio.function;

import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.MultivariateVectorialFunction;
import scio.function.utils.DemoSurface3D;

/**
 * This class is a class that wraps an actual function together with demo functions,
 * allowing for automated generation of "demo" curves and the ability to swap between
 * various functions. This allows for classes that have beans using this kind of function
 * to be adjustable.
 * 
 * @author ae3263
 * @see scio.function.utils.DemoSurface3D
 */
public class FlexSpaceSurface extends FlexFunctionAbstract<MultivariateVectorialFunction> implements MultivariateVectorialFunction {

    DemoSurface3D demoFunc = DemoSurface3D.NONE;

    public FlexSpaceSurface() {
        super();
    }
    
    public FlexSpaceSurface(MultivariateVectorialFunction baseFunc) {
        super(baseFunc);
    }

    public DemoSurface3D getDemoFunc() {
        return demoFunc;
    }

    public void setDemoFunc(DemoSurface3D demoFunc) {
        if (this.demoFunc != demoFunc) {
            this.demoFunc = demoFunc;
            fireStateChanged();
        }
    }

    /** Returns true if the class is currently using the "demo" function. */
    protected boolean isDemo() {
        return baseFunc==null || demoFunc!=DemoSurface3D.NONE;
    }

    public double[] value(double[] x) throws FunctionEvaluationException {
        return isDemo() ? demoFunc.value(x) : baseFunc.value(x);
    }
}
