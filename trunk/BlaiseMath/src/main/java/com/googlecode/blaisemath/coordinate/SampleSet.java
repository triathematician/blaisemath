/*
 * SampleCoordinateSetGenerator.java
 * Created on Nov 4, 2009
 */

package com.googlecode.blaisemath.coordinate;

/*
 * #%L
 * BlaiseMath
 * --
 * Copyright (C) 2009 - 2014 Elisha Peterson
 * --
 * Licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
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

import java.util.List;

/**
 * <p>
 *  Returns a collection of points of specified type. This should be used, e.g. to provide
 *  several points in a grid, or a list of sample values for a Riemann sum.
 * </p>
 *
 * @param <C> the coordinate type of the output
 * 
 * @author Elisha Peterson
 */
public interface SampleSet<C> {

    /**
     * Return a list of sampled points. Any options as to what this looks
     * like should be controlled by implementing classes.
     *
     * @return an array of sampled points of the given coordinate type
     */
    public List<C> getSamples();

    /**
     * Returns difference between adjacent samples, if relevant to the generator.
     * E.g. in a grid of sampled points, would be the difference between the x
     * and y values.
     *
     * @return object describing the difference between successive samples
     */
    public C getSampleDiff();
}
