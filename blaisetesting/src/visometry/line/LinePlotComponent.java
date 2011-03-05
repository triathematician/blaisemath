/**
 * LinePlotComponent.java
 * Created on Sep 19, 2009
 */

package visometry.line;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseWheelListener;
import scio.coordinate.RealInterval;
import visometry.PlotComponent;

/**
 * <p>
 *   <code>LinePlotComponent</code> is a <code>PlotComponent</code> for a
 *   one-dimensional line.
 * </p>
 *
 * @author Elisha Peterson
 */
public class LinePlotComponent extends PlotComponent<Double> {

    LineAxis axis;

    public LinePlotComponent() {
        super(new LineVisometry(), new LineProcessor());
        defaultMouseListener = new LinePlotMouseHandler((LineVisometry) visometry, this);
        defaultMouseWheelListener = (MouseWheelListener) defaultMouseListener;

        // set up the default domains for the plot
        LineVisometry lv = (LineVisometry) visometry;
        pGroup.registerDomain("t", lv.getDomain(), Double.class);
        pGroup.registerDomain("time", new RealInterval(0, 100), Double.class);

        pGroup.add(axis = new LineAxis());
    }

    @Override
    protected void paintComponent(Graphics g) {
        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        super.paintComponent(g);
    }

    /** @return underlying axis */
    public LineAxis getAxis() { return axis; }
    /** Sets underlying axis */
    public void setAxis(LineAxis newAxis) {
        if (axis != null)
            pGroup.remove(axis);
        if (newAxis != null)
            pGroup.add(axis);
    }


}
