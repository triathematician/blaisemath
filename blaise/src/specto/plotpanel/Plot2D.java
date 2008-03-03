/*
 * Plot2D.java
 * Created on Sep 14, 2007, 8:12:44 AM
 */

package specto.plotpanel;

import java.util.Collection;
import specto.PlotPanel;
import specto.Plottable;
import specto.gridplottable.Grid2D;
import specto.visometry.Euclidean2;

/**
 * The primary 2D plot window which should be used in applications. Will have support
 * for drawing grid, axes, and labels, plus animation and event handling.
 * @author Elisha Peterson
 */
public class Plot2D extends PlotPanel<Euclidean2> {
    /** Default constructor */
    public Plot2D(){
        super(new Euclidean2());
        add(new Grid2D());
    }

    @Override
    public void removeAll() {
        super.removeAll();
        add(new Grid2D());
    }

    @Override
    public <T extends Plottable<Euclidean2>> void removeAll(Collection<T> cpv) {
        super.removeAll(cpv);
        add(new Grid2D());
    }
}
