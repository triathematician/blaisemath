/*
 * SpacePlotComponent.java
 * Created on Oct 20, 2009
 */

package later.visometry.space;

import java.awt.event.MouseWheelListener;
import org.bm.blaise.scio.coordinate.Point3D;
import org.bm.blaise.scio.coordinate.RealInterval;
import visometry.PlotComponent;

/**
 * Standard class for displaying 3d plots.
 * 
 * @author Elisha Peterson
 */
public class SpacePlotComponent extends PlotComponent<Point3D> {

    public SpacePlotComponent() {
        super(new SpaceVisometry(), new SpaceProcessor());
        SpaceVisometry sv = (SpaceVisometry) visometry;
        mouseListener = new SpacePlotMouseHandler(sv, this);
        wheelListener = (MouseWheelListener) mouseListener;

        // set up the default domains for the plot
        plottables.registerDomain("x", sv.getDomain1(), Double.class);
        plottables.registerDomain("y", sv.getDomain2(), Double.class);
        plottables.registerDomain("z", sv.getDomain3(), Double.class);
//        pGroup.registerDomain("xy", sv.getXYDomain(), Point2D.Double.class);
//        pGroup.registerDomain("xz", sv.getXZDomain(), Point2D.Double.class);
//        pGroup.registerDomain("yz", sv.getYZDomain(), Point2D.Double.class);
//        pGroup.registerDomain("xyz", sv.getXYZDomain(), Point3D.class);
        plottables.registerDomain("time", new RealInterval(0, 100), Double.class);
    }

}
