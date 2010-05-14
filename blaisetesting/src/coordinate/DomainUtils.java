/*
 * DomainUtils.java
 * Created Apr 14, 2010
 */

package coordinate;

import scio.coordinate.RealInterval;

/**
 * Utilities for working with domains.
 * 
 * @author Elisha Peterson
 */
public class DomainUtils {

    /**
     * @param domain a real interval domain
     * @param scale approximate number of pixels per sample
     * @param hint hint used to generate the domain (values in <code>ScreenSampleDomainProvider</code>)
     *
     * @return a step sampler built on another domain, with a given scale and a given hint.
     * @see <code>ScreenSampleDomainProvider</code>
     */
    public static RealIntervalStepSampler stepSamplingDomain(RealInterval domain, double scale, int hint) {
        return hint == ScreenSampleDomainProvider.HINT_REGULAR ? new RealIntervalStepSampler(domain, scale)
                : hint == ScreenSampleDomainProvider.HINT_PREFER_WHOLE_NUMBERS ? new RealIntervalNiceSampler(domain, scale)
                : hint == ScreenSampleDomainProvider.HINT_PREFER_PI ? new RealIntervalNiceSampler(domain, scale, true)
                : null;
    }
}
