/*
 * DomainHint.java
 * Created Jun 3, 2010
 */

package coordinate;

/**
 * Provides a few hint constants used by domain providers to generate values.
 *
 * @author Elisha Peterson
 */
public enum DomainHint {
    /** Tells domain provider to use regular algorithm for determining values */
    REGULAR,
    /** Tells domain provider to prefer integers */
    PREFER_INTS,
    /** Tells domain provider to prefer multiples of pi */
    PREFER_PI;
}
