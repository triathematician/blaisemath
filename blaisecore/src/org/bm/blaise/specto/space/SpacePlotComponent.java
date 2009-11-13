/*
 * SpacePlotComponent.java
 * Created on Oct 20, 2009
 */

package org.bm.blaise.specto.space;

import java.awt.Graphics;
import javax.swing.event.ChangeEvent;
import org.bm.blaise.specto.visometry.PlotComponent;
import org.bm.utils.Spacing;
import scio.coordinate.P3D;
import scio.coordinate.utils.SampleSetGenerator;

/**
 *
 * @author Elisha Peterson
 */
public class SpacePlotComponent extends PlotComponent<P3D> {

    /** View settings */
    boolean anaglyph;

    public SpacePlotComponent() {
        super(new SpaceVisometry());
        visometryGraphics = new SpaceGraphics(visometry);
        SpacePlotResizer resizer = new SpacePlotResizer((SpaceVisometry) visometry, this);
        defaultMouseListener = resizer;
        defaultMouseWheelListener = resizer;
    }

    public SampleSetGenerator<P3D> getSSG() {
        // TODO - hook into this generator in a better way!!
        return ((SpaceGraphics) visometryGraphics).getSampleSetGenerator(Spacing.REGULAR);
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