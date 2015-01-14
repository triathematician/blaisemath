/*
 * NiceRangeGenerator.java
 * Created on Mar 22, 2008
 */
package com.googlecode.blaisemath.coordinate;

/*
 * #%L
 * BlaiseMath
 * --
 * Copyright (C) 2009 - 2015 Elisha Peterson
 * --
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.AbstractList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
    public List<Double> niceRange(double min, double max, double idealStep);


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
            if (idealStep == 0)
                return 0;
            idealStep = Math.abs(idealStep);
            // first, put the range in between 1 and 10
            double idealDigit = idealStep;
            while (idealDigit > 10)
                idealDigit /= 10;
            while (idealDigit < 1)
                idealDigit *= 10;
            // now set the factor as that times 1,2,2.5, or 5
            double factor = idealStep / idealDigit;
            factor *= (idealDigit < 1.8) ? 1 : (idealDigit < 2.2) ? 2 : (idealDigit < 3.5) ? 2.5 : (idealDigit < 7.5) ? 5 : 10;
            return factor;
        }

        public List<Double> niceRange(double min, double max, double idealStep) {
            if (idealStep == 0)
                return Arrays.asList((Double) min, (Double) max);
            final double factor = getStep(min, max, idealStep);
            if (factor == 0)
                return Collections.emptyList();

            final double start = factor * Math.floor(min / factor);
            final int n = 1 + (int) ( (max - start) / factor);

            return new AbstractList<Double>(){
                @Override
                public Double get(int index) {
                    return start + factor * index;
                }
                @Override
                public int size() {
                    return n;
                }
            };
        }
    };
}
