/**
 * PlotZ2D.java
 * Created on Apr 8, 2008
 */

package specto.integer2;

import specto.PlotPanel;

/**
 *
 * @author Elisha Peterson
 */
public class PlotZ2D extends PlotPanel<Integer2> {
    /** Default constructor */
    public PlotZ2D(){
        super(new Integer2());
        addBase(new GridZ2D());
    }
}
