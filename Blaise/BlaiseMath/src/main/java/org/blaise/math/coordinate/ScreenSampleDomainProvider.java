/*
 * ScreenSampleDomainProvider.java
 * Created on Apr 7, 2010
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
 *   A domain that can generate sampling sets within the domain using a fixed 
 *   "pixel" spacing between samples.
 * </p>
 *
 * @author Elisha Peterson
 */
public interface ScreenSampleDomainProvider<C> {

    /** 
     * Returns a sampler based upon the domain that samples elements at the approximate pixel spacing provided.
     * @param pixSpacing desired pixels between each sample (not guaranteed)
     * @param hint a parameter describing the type of sampler desired (e.g. it may be exactly at the given pixel spacing,
     *      or approximately, preferring to peg more common values such as 1, 2, 5, 10, etc.)
     * @return a sampling class based upon the domain, w/ specified pixel spacing
     */
    public SampleSet<C> samplerWithPixelSpacing(float pixSpacing, DomainHint hint);
    
}
