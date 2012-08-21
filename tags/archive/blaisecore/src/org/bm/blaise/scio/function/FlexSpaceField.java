/*
 * FlexSpaceSurface.java
 * Created on Jan 9, 2010
 */

package org.bm.blaise.scio.function;

import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.MultivariateVectorialFunction;
import scio.function.utils.DemoField3D;
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
public class FlexSpaceField extends FlexFunctionAbstract<MultivariateVectorialFunction> implements MultivariateVectorialFunction {

    DemoField3D demoFunc = DemoField3D.NONE;

    public FlexSpaceField() {
        super();
    }
    
    public FlexSpaceField(MultivariateVectorialFunction baseFunc) {
        super(baseFunc);
    }

    public DemoField3D getDemoFunc() {
        return demoFunc;
    }

    public void setDemoFunc(DemoField3D demoFunc) {
        if (this.demoFunc != demoFunc) {
            this.demoFunc = demoFunc;
            fireStateChanged();
        }
    }

    /** Returns true if the class is currently using the "demo" function. */
    protected boolean isDemo() {
        return baseFunc==null || demoFunc!=DemoField3D.NONE;
    }

    public double[] value(double[] x) throws FunctionEvaluationException {
        return isDemo() ? demoFunc.value(x) : baseFunc.value(x);
    }
}
