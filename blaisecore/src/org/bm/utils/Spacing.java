/**
 * Spacing.java
 * Created Oct 2009
 */
package org.bm.utils;

/**
 * <p>
 *    This class is used to store shortcuts for various spacing of objects on the screen,
 *    for example vector fields, or points sampled along a curve, etc. This is primarily
 *    when the number of values does not matter too much, so one only needs a few general
 *    options for spacing.
 * </p>
 * <p>
 *    Each constant is attached to a number of different values with varying interpretations:
 *    <ul>
 *      <li> <b>pixSpace</b> is the number of pixels separating each sample
 *      <li> <b>dimSamples</b> is the number of samples for each dimension
 *      <li> <b>totSamples</b> is the total number of samples
 *    </ul>
 *    It is up to any classes that uses this to determine exactly how to make use of these options.
 * </p>
 */
@Deprecated
public enum Spacing {
    SUPER_FINE("Super fine", 25, 20, 300),
    FINE("Fine", 40, 16, 200),
    REGULAR("Regular", 80, 10, 100),
    COARSE("Coarse", 120, 6, 50);
    
    public String name;

    /** Represents what <b>should</b> be the spacing (in pixels) between objects. */
    public int pixSpace;

    /** Represents what <b>should</b> be the number of object samples per dimension. */
    public int dimSamples;

    /** Represents what <b>should</b> be the <u>TOTAL</u> number of object samples. */
    public int totSamples;

    Spacing(String name, int pixSpace, int dimSamples, int totSamples) {
        this.name = name;
        this.pixSpace = pixSpace;
        this.dimSamples = dimSamples;
        this.totSamples = totSamples;
    }

    @Override
    public String toString() {
        return name;
    }
}
