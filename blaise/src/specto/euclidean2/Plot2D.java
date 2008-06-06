/*
 * Plot2D.java
 * Created on Sep 14, 2007, 8:12:44 AM
 */

package specto.euclidean2;

import specto.PlotPanel;

/**
 * The primary 2D plot window which should be used in applications. Will have support
 * for drawing grid, axes, and labels, plus animation and event handling.
 * @author Elisha Peterson
 */
public class Plot2D extends PlotPanel<Euclidean2> {
    /** Default constructor */
    public Plot2D(){
        super(new Euclidean2());
        addBase(new StandardGrid2D());
        addBase(new Axes2D());
    }
}
