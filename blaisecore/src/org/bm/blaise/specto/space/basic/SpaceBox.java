/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bm.blaise.specto.space.basic;

import org.bm.blaise.specto.space.*;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import org.bm.blaise.specto.primitive.ShapeStyle;
import org.bm.blaise.specto.visometry.AbstractPlottable;
import org.bm.blaise.specto.visometry.VisometryGraphics;
import scio.coordinate.Point3D;

/**
 *
 * @author ae3263
 */
public class SpaceBox extends AbstractPlottable<Point3D> {

//    ShapeStyle boxStyle = new ShapeStyle(ShapeStyle.DEFAULT_STROKE, Color.BLACK, Color.LIGHT_GRAY, 1.0f);

    @Override
    public void paintComponent(VisometryGraphics<Point3D> vg) {
        SpaceGraphics sg = (SpaceGraphics) vg;
//        vg.setShapeStyle(boxStyle);
        sg.addToScene(getBox(vg, new Point3D(.15, .15, .15), .3, .3, .3));
        sg.addToScene(getBox(vg, new Point3D(.55, .15, .15), .3, .3, .3));
        sg.addToScene(getBox(vg, new Point3D(.15, .55, .15), .3, .3, .3));
        sg.addToScene(getBox(vg, new Point3D(.15, .15, .55), .3, .3, .3));
        sg.addToScene(getBox(vg, new Point3D(.55, .55, .15), .3, .3, .3));
        sg.addToScene(getBox(vg, new Point3D(.55, .15, .55), .3, .3, .3));
        sg.addToScene(getBox(vg, new Point3D(.15, .55, .55), .3, .3, .3));
        sg.addToScene(getBox(vg, new Point3D(.75, .75, .75), .3, .3, .3));
        sg.addToScene(new Point3D[]{new Point3D(1.06, 1.06, 1.06)});
//        ((SpaceGraphics) vg).drawPolygons(boxes);
    }

    public List<Point3D[]> getBox(VisometryGraphics<Point3D> vg, Point3D base, double l, double w, double h) {
        List<Point3D[]> result = new ArrayList<Point3D[]>();

        Point3D cl = base.plus(l, 0, 0);
        Point3D cw = base.plus(0, w, 0);
        Point3D ch = base.plus(0, 0, h);
        Point3D clw = base.plus(l, w, 0);
        Point3D clh = base.plus(l, 0, h);
        Point3D cwh = base.plus(0, w, h);
        Point3D clwh = base.plus(l, w, h);

        result.add(new Point3D[]{base, cl, clw, cw});
        result.add(new Point3D[]{base, cl, clh, ch});
        result.add(new Point3D[]{base, cw, cwh, ch});
        result.add(new Point3D[]{cl, clw, clwh, clh});
        result.add(new Point3D[]{cw, clw, clwh, cwh});
        result.add(new Point3D[]{ch, clh, clwh, cwh});

        return result;
    }

}
