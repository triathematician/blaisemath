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
