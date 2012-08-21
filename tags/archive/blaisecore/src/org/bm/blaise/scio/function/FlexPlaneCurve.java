/*
 * FlexPlaneCurve.java
 * Created on Jan 9, 2010
 */

package org.bm.blaise.scio.function;

import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.UnivariateVectorialFunction;
import scio.function.utils.DemoCurve2D;

/**
 * This class is a class that wraps an actual function together with demo functions,
 * allowing for automated generation of "demo" curves and the ability to swap between
 * various functions. This allows for classes that have beans using this kind of function
 * to be adjustable.
 * 
 * @author ae3263
 * @see scio.function.utils.DemoCurve2D
 */
public class FlexPlaneCurve extends FlexFunctionAbstract<UnivariateVectorialFunction> implements UnivariateVectorialFunction {

    DemoCurve2D demoFunc = DemoCurve2D.NONE;

    public FlexPlaneCurve() {
        super();
    }
    
    public FlexPlaneCurve(UnivariateVectorialFunction baseFunc) {
        super(baseFunc);
    }

    public DemoCurve2D getDemoFunc() {
        return demoFunc;
    }

    public void setDemoFunc(DemoCurve2D demoFunc) {
        if (this.demoFunc != demoFunc) {
            this.demoFunc = demoFunc;
            fireStateChanged();
        }
    }

    /** Returns true if the class is currently using the "demo" function. */
    protected boolean isDemo() {
        return baseFunc==null || demoFunc!=DemoCurve2D.NONE;
    }

    public double[] value(double x) throws FunctionEvaluationException {
        return isDemo() ? demoFunc.value(x) : baseFunc.value(x);
    }
}
