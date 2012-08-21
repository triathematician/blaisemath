/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bm.blaise.specto.space;

import org.bm.blaise.specto.visometry.Plottable;
import org.bm.blaise.specto.visometry.VisometryGraphics;
import scio.coordinate.Point3D;

/**
 * <p>
 *  This displays the plane of view for a <code>SpaceProjection</code>.
 * </p>
 * @author ae3263
 */
public class SpaceViewObjects extends Plottable<Point3D> {

    SpaceProjection sp;

    Point3D feature = new Point3D(1.06, 1.06, 1.06);

    // CONSTRUCTORS

    public SpaceViewObjects(SpaceProjection sp) {
        this.sp = sp;
    }



    // PAINT METHODS

    @Override
    public void draw(VisometryGraphics<Point3D> vg) {
        SpaceGraphics sg = (SpaceGraphics) vg;

        // vectors for the scene's focal point
//        sg.addToScene(new P3D[]{ sp.center });
//        sg.addToScene(new P3D[]{ sp.center.minus(sp.tDir.times(.3)), sp.center.plus(sp.tDir.times(.3)) });
//        sg.addToScene(new P3D[]{ sp.center.minus(sp.nDir.times(.3)), sp.center.plus(sp.nDir.times(.3)) });
//        sg.addToScene(new P3D[]{ sp.center.minus(sp.bDir.times(.3)), sp.center.plus(sp.bDir.times(.3)) });

        // camera
        sg.drawPoint( sp.camera );
        sg.drawSegment( sp.camera, sp.camera.plus(sp.tDir.times(.25)) );
        sg.drawSegment( sp.camera.minus(sp.nDir.times(.1)), sp.camera.plus(sp.nDir.times(.1)) );
        sg.drawSegment( sp.camera.minus(sp.bDir.times(.1)), sp.camera.plus(sp.bDir.times(.1)) );

        // screen
//        sg.addToScene(new P3D[]{ sp.screenCenter.minus(sp.tDir.times(.05)) });
//        sg.addToScene(new P3D[]{ sp.screenCenter.minus(sp.tDir.times(.05)), sp.screenCenter.minus(sp.tDir.times(.3)) });
        sg.drawSegment( sp.screenCenter.minus(sp.nDir.times(.1)), sp.screenCenter.plus(sp.nDir.times(.1)) );
        sg.drawSegment( sp.screenCenter.minus(sp.bDir.times(.1)), sp.screenCenter.plus(sp.bDir.times(.1)) );
        double wx = sp.winBounds.getWidth() / 2.0 / sp.dpi;
        double wy = sp.winBounds.getHeight() / 2.0 / sp.dpi;
//        sg.addToScene(new P3D[]{ sp.screenCenter.plus(sp.bDir.times(wx)).plus(sp.nDir.times(wy)), sp.screenCenter.minus(sp.bDir.times(wx)).plus(sp.nDir.times(wy))});
//        sg.addToScene(new P3D[]{ sp.screenCenter.minus(sp.bDir.times(wx)).plus(sp.nDir.times(wy)), sp.screenCenter.minus(sp.bDir.times(wx)).minus(sp.nDir.times(wy))});
//        sg.addToScene(new P3D[]{ sp.screenCenter.minus(sp.bDir.times(wx)).minus(sp.nDir.times(wy)), sp.screenCenter.plus(sp.bDir.times(wx)).minus(sp.nDir.times(wy))});
//        sg.addToScene(new P3D[]{ sp.screenCenter.plus(sp.bDir.times(wx)).minus(sp.nDir.times(wy)), sp.screenCenter.plus(sp.bDir.times(wx)).plus(sp.nDir.times(wy))});
        // polygon
        sg.drawShape(new Point3D[]{
            sp.screenCenter.plus(sp.bDir.times(wx)).plus(sp.nDir.times(wy)), sp.screenCenter.minus(sp.bDir.times(wx)).plus(sp.nDir.times(wy)),
            sp.screenCenter.minus(sp.bDir.times(wx)).minus(sp.nDir.times(wy)), sp.screenCenter.plus(sp.bDir.times(wx)).minus(sp.nDir.times(wy))});

        // feature
        sg.drawPoint( feature );
        sg.drawPoint( sp.getCoordinateOf(sp.getWindowPointOf(feature)) );
        sg.drawSegment( feature, sp.getCoordinateOf(sp.getWindowPointOf(feature)) );
        sg.drawSegment( sp.camera, sp.getCoordinateOf(sp.getWindowPointOf(feature)) );
        

        // draw boundaries of the viewing window (displays two rectangles, one for the full screen, and one just inside)
        //vg.setShapeStyle(shStyle);
//        double wx = sp.winBounds.getWidth() / 2.0 / sp.dpi;
//        double wy = sp.winBounds.getHeight() / 2.0 / sp.dpi;
//        sg.addToScene(new P3D[]{
//            sp.screenCenter.plus(sp.bDir.times(wx)).plus(sp.nDir.times(wy)),
//            sp.screenCenter.minus(sp.bDir.times(wx)).plus(sp.nDir.times(wy)),
//            sp.screenCenter.minus(sp.bDir.times(wx)).minus(sp.nDir.times(wy)),
//            sp.screenCenter.plus(sp.bDir.times(wx)).minus(sp.nDir.times(wy)) });
        //vg.drawClosedPath(corners);
//        wx *= .95;
//        wy *= .95;
//        sg.addToScene(new P3D[]{
//            sp.screenCenter.plus(sp.bDir.times(wx)).plus(sp.nDir.times(wy)),
//            sp.screenCenter.minus(sp.bDir.times(wx)).plus(sp.nDir.times(wy)),
//            sp.screenCenter.minus(sp.bDir.times(wx)).minus(sp.nDir.times(wy)),
//            sp.screenCenter.plus(sp.bDir.times(wx)).minus(sp.nDir.times(wy)) });
//        vg.drawClosedPath(corners);

        // screen direction vectors
//        vg.setArrowStyle(arrStyle);
//        arrStyle.setColor(SCR_COL);
//        sg.addToScene(new P3D[]{ sp.screenCenter, sp.screenCenter.plus(sp.nDir) });
//        sg.addToScene(new P3D[]{ sp.screenCenter, sp.screenCenter.plus(sp.bDir) });
//        vg.drawArrow(sp.screenCenter, sp.screenCenter.plus(sp.nDir));
//        vg.drawArrow(sp.screenCenter, sp.screenCenter.plus(sp.bDir));

        // spacial origin and i, j, k vectors
//        arrStyle.setColor(BSS_COL);
//        vg.drawArrow(P3D.ZERO, P3D.PLUS_I);
//        vg.drawArrow(P3D.ZERO, P3D.PLUS_J);
//        vg.drawArrow(P3D.ZERO, P3D.PLUS_K);

    }

}
