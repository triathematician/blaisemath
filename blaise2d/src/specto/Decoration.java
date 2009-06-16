/*
 * Decoration.java
 * Created on Oct 19, 2007, 12:42:46 PM
 */

package specto;

/**
 * <p>
 * This interface is designed to control and display a "decoration" of another plottable,
 * e.g. a point or vector along the graph of a function. This is primarily an organizational convenience,
 * although the interface provides a static method for returning a decoration with a given parent. Giving
 * a plottable this interface will help it to be automatically generated given the parent function.
 * </p>
 * @author Elisha Peterson
 */
public interface Decoration<V extends Visometry,P extends Plottable<V>>{    
    public void setParent(P parent);
    public P getParent();
}
