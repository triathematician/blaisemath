/*
 * RealDomainUtils.java
 * Created Apr 14, 2010
 */

package org.blaise.math.line;

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

import org.blaise.math.coordinate.DomainHint;
import org.blaise.math.line.RealIntervalNiceSampler;
import org.blaise.math.line.RealIntervalStepSampler;
import org.blaise.math.line.RealInterval;

/**
 * Utilities for working with domains.
 * 
 * @author Elisha Peterson
 */
public class RealDomainUtils {

    /**
     * @param domain a real interval domain
     * @param scale approximate number of pixels per sample
     * @param hint hint used to generate the domain (values in <code>ScreenSampleDomainProvider</code>)
     *
     * @return a step sampler built on another domain, with a given scale and a given hint.
     * @see <code>ScreenSampleDomainProvider</code>
     */
    public static RealIntervalStepSampler stepSamplingDomain(RealInterval domain, double scale, DomainHint hint) {
        switch (hint) {
            case PREFER_PI:
                return new RealIntervalNiceSampler(domain, scale, true);
            case REGULAR:
            case PREFER_INTS:
            default:
                return new RealIntervalNiceSampler(domain, scale);
        }
    }
}
