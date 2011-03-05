/**
 * LinePlotComponent.java
 * Created on Sep 19, 2009
 */

package later.visometry.line;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseWheelListener;
import org.bm.blaise.scio.coordinate.RealInterval;
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
        mouseListener = new LinePlotMouseHandler((LineVisometry) visometry, this);
        wheelListener = (MouseWheelListener) mouseListener;

        // set up the default domains for the plot
        LineVisometry lv = (LineVisometry) visometry;
        plottables.registerDomain("t", lv.getDomain(), Double.class);
        plottables.registerDomain("time", new RealInterval(0, 100), Double.class);

        plottables.add(axis = new LineAxis());
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
            plottables.remove(axis);
        if (newAxis != null)
            plottables.add(axis);
    }


}
