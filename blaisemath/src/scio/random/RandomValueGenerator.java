/**
 * RandomValueGenerator.java
 * Created on Nov 25, 2008
 */
package scio.random;

/**
 * <p>
 *  This interface specifies a class which generates random numbers of some coordinate type.
 * </p>
 * @author Elisha Peterson
 */
public interface RandomValueGenerator<C> {

    /** 
     * Generate a random number of the appropriate type.
     * @return a random value
     */
    public C nextValue();
}
