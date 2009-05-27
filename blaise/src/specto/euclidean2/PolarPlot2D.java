/*
 * Plot2D.java
 * Created on Sep 14, 2007, 8:12:44 AM
 */

package specto.euclidean2;

import specto.PlotPanel;
import specto.euclidean2.Axes2D;
import specto.euclidean2.PolarGrid2D;
import specto.euclidean2.Euclidean2;

/**
 * The primary 2D plot window which should be used in applications. Will have support
 * for drawing grid, axes, and labels, plus animation and event handling.
 * @author Elisha Peterson
 */
public class PolarPlot2D extends PlotPanel<Euclidean2> {
    /** Default constructor */
    public PolarPlot2D(){
        super(new Euclidean2());
        addBase(new PolarGrid2D());
        addBase(new PolarAxes2D());
    }
}
