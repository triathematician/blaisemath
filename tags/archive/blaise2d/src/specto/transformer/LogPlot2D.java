/*
 * LogPlot2D.java
 * Created on Mar 6, 2008
 */

package specto.transformer;

import specto.PlotPanel;
import specto.euclidean2.Euclidean2;

/**
 * <p>
 * LogPlot2D is a plot which displays logarithmic functions.
 * </p>
 * @author Elisha Peterson
 */
public class LogPlot2D extends PlotPanel<Euclidean2> {
    public LogPlot2D() {
        super(new LogVisometry());
        addBase(new specto.euclidean2.StandardGrid2D());
        addBase(new specto.euclidean2.Axes2D());
    }
}
