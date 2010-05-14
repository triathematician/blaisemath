/*
 * ScreenSampleDomainProvider.java
 * Created on Apr 7, 2010
 */

package coordinate;

import scio.coordinate.sample.SampleSet;

/**
 * <p>
 *   A domain that can generate sampling sets within the domain using a fixed 
 *   "pixel" spacing between samples.
 * </p>
 *
 * @author Elisha Peterson
 */
public interface ScreenSampleDomainProvider<C> {

    /** Use exactly the provided spacing. */
    final int HINT_REGULAR = 0;
    /** Prefers whole numbers and nice fractions. */
    final int HINT_PREFER_WHOLE_NUMBERS = 1;
    /** Prefer values at multiples of pi. */
    final int HINT_PREFER_PI = 2;

    /** 
     * Returns a sampler based upon the domain that samples elements at the approximate pixel spacing provided.
     * @param pixSpacing desired pixels between each sample (not guaranteed)
     * @param int a parameter describing the type of sampler desired (e.g. it may be exactly at the given pixel spacing,
     *      or approximately, preferring to peg more common values such as 1, 2, 5, 10, etc.)
     * @return a sampling class based upon the domain, w/ specified pixel spacing
     */
    public SampleSet<C> samplerWithPixelSpacing(float pixSpacing, int hint);
    
}
