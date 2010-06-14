/*
 * SpacePlotComponent.java
 * Created on Oct 20, 2009
 */

package visometry.space;

import java.awt.event.MouseWheelListener;
import scio.coordinate.Point3D;
import scio.coordinate.RealInterval;
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
        defaultMouseListener = new SpacePlotMouseHandler(sv, this);
        defaultMouseWheelListener = (MouseWheelListener) defaultMouseListener;

        // set up the default domains for the plot
        pGroup.registerDomain("x", sv.getDomain1(), Double.class);
        pGroup.registerDomain("y", sv.getDomain2(), Double.class);
        pGroup.registerDomain("z", sv.getDomain3(), Double.class);
//        pGroup.registerDomain("xy", sv.getXYDomain(), Point2D.Double.class);
//        pGroup.registerDomain("xz", sv.getXZDomain(), Point2D.Double.class);
//        pGroup.registerDomain("yz", sv.getYZDomain(), Point2D.Double.class);
//        pGroup.registerDomain("xyz", sv.getXYZDomain(), Point3D.class);
        pGroup.registerDomain("time", new RealInterval(0, 100), Double.class);
    }

}
