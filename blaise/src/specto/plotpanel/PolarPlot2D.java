/*
 * Plot2D.java
 * Created on Sep 14, 2007, 8:12:44 AM
 */

package specto.plotpanel;

import specto.PlotPanel;
import specto.gridplottable.Axes2D;
import specto.gridplottable.PolarGrid2D;
import specto.visometry.Euclidean2;

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
        addBase(new Axes2D());
    }
}
