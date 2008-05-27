/**
 * Grid2Panel.java
 * Created on Apr 8, 2008
 */

package specto.specialty.grid;

import java.awt.Color;
import java.awt.Graphics;
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

    @Override
    public void paintComponent(Graphics gb) {
        super.paintComponent(gb);
    }
}
