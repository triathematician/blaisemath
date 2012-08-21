/*
 * NiceRangeGenerator.java
 * Created on Mar 22, 2008
 */
package org.bm.utils;

/**
 * NiceRangeGenerator is originally designed to generate nice grid spacing for plots,
 * so that when the plot is resized/moved, the grid lines are "stuck" on appropriate
 * values depending on the size of the window, NOT the position of the window. This
 * allows for the grid to be "smoothly" dragged around the screen.
 * 
 * Other uses for the methods herein include tick marks for sliders and graphs, etc.
 * 
 * @author Elisha Peterson
 */
public interface NiceRangeGenerator {

    /**
     * Generates a list of values within the interval [min, max] (and possibly slightly outside).
     * The step between the values should be close to <code>idealStep</code>, but adjusted so the
     * values are "nice" in some way. Here "nice" is determined by the subclass.
     *
     * @param min minimum of interval
     * @param max maximum of interval
     * @param idealStep the ideal step between values
     * 
     * @return list of values between min and max with increment close to the ideal step.
     */
    public double[] niceRange(double min, double max, double idealStep);


    //
    // CONCRETE INSTANCES
    //

    /** <code>NiceRangeGenerator</code> instance that prefers steps of 1, 2, 2.5, and 5. */
    public static final NiceRangeGenerator STANDARD = new StandardRange();

    /** <code>NiceRangeGenerator</code> instance that prefers steps of multiples of <b>PI</b>. */
    public static final NiceRangeGenerator PI = new StandardRange() {
        @Override
        public double getStep(double min, double max, double idealStep) {
            return super.getStep(min, max, idealStep) * Math.PI / 2;
        }
    };


    //
    // STANDARD CLASS
    //

    /** <code>NiceRangeGenerator</code> that prefers steps of 1, 2, 2.5, and 5. */
    class StandardRange implements NiceRangeGenerator {
        protected double getStep(double min, double max, double idealStep) {
            // make sure values are positive, nonzero, etc.
            if (min > max) {
                double temp = min;
                min = max;
                max = temp;
            }
            if (idealStep == 0) {
                return 0;
            }
            idealStep = Math.abs(idealStep);
            // first, put the range in between 1 and 10
            double idealDigit = idealStep;
            while (idealDigit > 10) {
                idealDigit /= 10;
            }
            while (idealDigit < 1) {
                idealDigit *= 10;
            }
            // now set the factor as that times 1,2,2.5, or 5
            double factor = idealStep / idealDigit;
            factor *=
                    (idealDigit < 1.8) ? 1 : (idealDigit < 2.2) ? 2 : (idealDigit < 3.5) ? 2.5 : (idealDigit < 7.5) ? 5 : 10;
            return factor;
        }

        public double[] niceRange(double min, double max, double idealStep) {
            if (idealStep == 0) {
                return new double[]{min, max};
            }
            double factor = getStep(min, max, idealStep);
            if (factor == 0) {
                return new double[]{};
            }
            int size = (int) ( (max - min) / factor ) + 2;
            double[] result = new double[size];
            result[0] = factor * Math.floor(min / factor); // allow slightly smaller values
            for (int i = 1; i < size; i++) {
                result[i] = result[0] + factor * i;
            }
            return result;
        }
    };
}
