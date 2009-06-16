/*
 * Plot1D.java
 * Created on Feb 11, 2008
 */

package specto.euclidean1;

import specto.PlotPanel;
import specto.euclidean1.Axis1D;
import specto.euclidean1.Euclidean1;

/**
 * <p>
 * Plot1D is for display one-dimensional data, such as sequences of real numbers,
 * open and closed sets, etc.
 * </p>
 * @author Elisha Peterson
 */
public class Plot1D extends PlotPanel<Euclidean1> {
    /** Default constructor */
    public Plot1D(){
        super(new Euclidean1());
        addBase(new Axis1D());
    }
}
