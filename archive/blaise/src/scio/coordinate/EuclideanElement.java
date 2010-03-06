/**
 * EuclideanElement.java
 * Created on May 30, 2008
 */

package scio.coordinate;

/**
 * Necessary methods for defining an element of Euclidean space (ordered n-vector of doubles)
 * @author elisha
 */
public interface EuclideanElement<C> extends InnerProductSpaceElement<C>, MetricSpaceElement<C>, VectorSpaceElement<C> {
    void addToElement(int position, double value);

    double getElement(int position);

    int getLength();

    void multiplyElement(int position, double value);

    void setElement(int position, double value);

}
