/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package projectionbuilder;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;
import org.bm.blaise.specto.plottable.VPoint;
import org.bm.blaise.specto.plottable.VPointSet;
import org.bm.blaise.specto.primitive.PointStyle;
import org.bm.blaise.specto.space.SpaceGraphics;
import org.bm.blaise.specto.space.SpacePlotComponent;
import org.bm.blaise.specto.visometry.VisometryGraphics;
import scio.coordinate.Point3D;

/**
 * Can be used to generate a set of points to display on a 3D plot. Depends
 * on selections in two other windowed projection systems. Listens for points
 * added to two other plots.
 * @author Elisha Peterson
 */
public class ProjectedPointGenerator extends VPointSet<Point3D>
        implements MouseListener {

    /** First component */
    SpacePlotComponent sp1;
    /** Second component */
    SpacePlotComponent sp2;
    /** Point on first plot clicked. */
    Point2D p1;
    /** Point on second plot clicked. */
    Point2D p2;
    /** Visible point on first plot. */
    VPoint<Point3D> p31;
    /** Visible point on first plot. */
    VPoint<Point3D> p32;

    public ProjectedPointGenerator() {
        super();
    }

    public ProjectedPointGenerator(SpacePlotComponent sp1, SpacePlotComponent sp2) {
        super();
        setPlotComponents(sp1, sp2);
    }

    /** Sets up two plot components. */
    public void setPlotComponents(SpacePlotComponent sp1, SpacePlotComponent sp2) {
        this.sp1 = sp1;
        this.sp2 = sp2;
        sp1.addMouseListener(this);
        sp2.addMouseListener(this);
        sp1.addPlottable(p31 = new VPoint<Point3D>(new Point3D()));
            p31.setVisible(false);
            p31.setLabelVisible(false);
            p31.setStyle(new PointStyle(PointStyle.PointShape.CROSS, 5));
        sp2.addPlottable(p32 = new VPoint<Point3D>(new Point3D()));
            p31.setVisible(false);
            p31.setLabelVisible(false);
            p32.setStyle(p31.getStyle());
    }

    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
            if (e.getSource() == sp1) {
                p1 = e.getPoint();
                p31.setPoint(sp1.getProjection().getCoordinateOf(p1));
                if (p31.isVisible())
                    p32.setVisible(false);
                p31.setVisible(true);
            } else if (e.getSource() == sp2) {
                p2 = e.getPoint();
                p32.setPoint(sp2.getProjection().getCoordinateOf(p2));
                if (p32.isVisible())
                    p31.setVisible(false);
                p32.setVisible(true);
            }
            if (p1 != null && p2 != null) {
                addValue(MidpointLines.midPointWithPoints(
                        sp1.getProjection().getCamera(),
                        p31.getPoint(),
                        sp2.getProjection().getCamera(),
                        p32.getPoint()
                        ));
                p1 = null;
                p2 = null;
            }
        }
    }

    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}

}
