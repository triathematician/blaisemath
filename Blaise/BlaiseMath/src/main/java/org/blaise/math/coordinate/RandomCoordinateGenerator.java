/*
 * RandomPointGenerator.java
 * Created on Nov 4, 2009
 */

package org.blaise.math.coordinate;

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

/**
 * <p>
 *  Generates random points in specified coordinate system.
 * </p>
 *
 * @param <C> the coordinate type of the plottable
 * 
 * @author Elisha Peterson
 */
public interface RandomCoordinateGenerator<C> {

    /**
     * Return a random point.
     * @return a randomly chosen point
     */
    public C randomValue();

}
