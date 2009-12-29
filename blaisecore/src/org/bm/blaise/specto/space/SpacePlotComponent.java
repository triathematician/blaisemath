/*
 * SpacePlotComponent.java
 * Created on Oct 20, 2009
 */

package org.bm.blaise.specto.space;

import java.awt.Graphics;
import javax.swing.event.ChangeEvent;
import org.bm.blaise.specto.visometry.PlotComponent;
import scio.coordinate.Point3D;
import scio.coordinate.sample.SampleCoordinateSetGenerator;

/**
 *
 * @author Elisha Peterson
 */
public class SpacePlotComponent extends PlotComponent<Point3D> {

    /** View settings */
    boolean anaglyph;

    public SpacePlotComponent() {
        super(new SpaceVisometry());
        visometryGraphics = new SpaceGraphics(visometry);
        SpacePlotResizer resizer = new SpacePlotResizer((SpaceVisometry) visometry, this);
        defaultMouseListener = resizer;
        defaultMouseWheelListener = resizer;
    }

    public SampleCoordinateSetGenerator<Point3D> getSSG() {
        // TODO - hook into this generator in a better way!!
        return ((SpaceGraphics) visometryGraphics).getSampleSetGenerator();
    }

    public boolean isAnaglyph() {
        return anaglyph;
    }

    public void setAnaglyph(boolean anaglyph) {
        if (this.anaglyph != anaglyph) {
            if (anaglyph)
                visometryGraphics = new SpaceGraphicsAnaglyph(visometry);
            else
                visometryGraphics = new SpaceGraphics(visometry);
            this.anaglyph = anaglyph;
            repaint();
        }
    }

    public float getDefaultFillOpacity() {
        return ((SpaceGraphics) visometryGraphics).getOpacity();
    }

    public void setDefaultFillOpacity(float opacity) {
        ((SpaceGraphics) visometryGraphics).setOpacity(opacity);
    }

    public SpaceGraphics getVisometryGraphics() {
        return (SpaceGraphics) visometryGraphics;
    }

    public void setVisometryGraphics(SpaceGraphics visometryGraphics) {
        this.visometryGraphics = visometryGraphics;
    }



    @Override
    public void stateChanged(ChangeEvent e) {
        super.stateChanged(e);
    }

    @Override
    protected void paintComponent(Graphics g) {
        ((SpaceGraphics) visometryGraphics).clearScene();
        super.paintComponent(g);
        ((SpaceGraphics) visometryGraphics).drawScene();
    }

    public SpaceProjection getProjection() {
        return ((SpaceGraphics) visometryGraphics).proj;
    }

}