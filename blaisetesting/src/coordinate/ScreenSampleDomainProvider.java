/*
 * ScreenSampleDomainProvider.java
 * Created on Apr 7, 2010
 */

package coordinate;

import org.bm.blaise.scio.coordinate.sample.SampleSet;

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
