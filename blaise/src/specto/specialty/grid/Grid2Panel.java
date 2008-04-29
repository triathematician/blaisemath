/**
 * Grid2Panel.java
 * Created on Apr 8, 2008
 */

package specto.specialty.grid;

import specto.PlotPanel;

/**
 *
 * @author Elisha Peterson
 */
public class Grid2Panel extends PlotPanel<Grid2> {
    /** Default constructor */
    public Grid2Panel(){
        super(new Grid2());
        addBase(new Grid2Grid());
    }
}
