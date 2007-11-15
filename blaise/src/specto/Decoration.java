/*
 * Decoration.java
 * Created on Oct 19, 2007, 12:42:46 PM
 */

package specto;

/**
 * This interface is designed to control and display a "decoration" of another plottable,
 * e.g. a point or vector along a function plot.
 * @author ae3263
 */
public abstract class Decoration<P extends Plottable,V extends Visometry> extends Plottable<V> {
    
}
