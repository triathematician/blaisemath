/**
 * Parameter.java
 * Created on Dec 11, 2008
 */

package mas;

import java.text.NumberFormat;

/**
 * <p>Represents a parameter for use within a simulation. All parameters can be adjusted or modified.</p>
 * 
 * @author Elisha Peterson
 */
public class Parameter<N extends Number> {
    public N min,max,value;

    /** Initializes with particular values. */
    public Parameter(Parameter pm) {
        this((N)pm.min, (N)pm.max, (N)pm.value);
    }

    /** Initializes with particular values. */
    public Parameter(N min, N max, N value) {
        this.min = min;
        this.max = max;
        this.value = value;
    }  
    
    /** Returns random instance within values. */
    public Parameter getRandom() {
        if (value instanceof Integer) {
            int ival = (Integer)min + (int)Math.floor(((Integer)max-(Integer)min+1)*Math.random());
            return new Parameter<Integer>((Integer)min,(Integer)max,ival);
        } else if (value instanceof Float) {
            float fval = (Float)min + (float)(((Float)max-(Float)min)*Math.random());
            return new Parameter<Float>((Float)min,(Float)max,fval);
        } else if (value instanceof Double) {
            double dval = (Double)min + (((Double)max-(Double)min)*Math.random());
            return new Parameter<Double>((Double)min,(Double)max,dval);
        }
        return null;
    }

    /** Returns copy. */
    public Parameter copy() {
        if (value instanceof Integer) {
            return new Parameter<Integer>((Integer)min,(Integer)max,(Integer)value);
        } else if (value instanceof Float) {
            return new Parameter<Float>((Float)min,(Float)max,(Float)max);
        } else if (value instanceof Double) {
            return new Parameter<Double>((Double)min,(Double)max,(Double)max);
        }
        return null;
    }
    
    /** Returns parameter within default window. Works some magic to generate
     * an interval of values around the number.
     * @param val an Integer, Float, or Double typed numeric
     * @return a parameter with automatically generated boundaries
     */
    public static Parameter getInstance(Number val) {
        if (val instanceof Integer) {
            int ival = (Integer) val;
            int min = Math.min(0,ival-10);
            int max = Math.max(0,ival+10);
            return new Parameter<Integer>(min,max,ival);
        } else if (val instanceof Float) {
            float fval = (Float) val;
            float min = Math.min(fval-10, (fval < 0) ? fval*10 : 0);
            float max = Math.max(fval+10, (fval > 0) ? fval*10 : 0);
            return new Parameter<Float>(min,max,fval);
        } else if (val instanceof Double) {
            double fval = (Double) val;
            double min = Math.min(fval-10, (fval < 0) ? fval*10 : 0);
            double max = Math.max(fval+10, (fval > 0) ? fval*10 : 0);
            return new Parameter<Double>(min,max,fval);
        }
        return null;
    }

    final NumberFormat nf = NumberFormat.getInstance();

    @Override
    public String toString(){ return nf.format(value); }
}