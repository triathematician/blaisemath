/*
 * SpacePlotComponent.java
 * Created on Oct 20, 2009
 */

package org.bm.blaise.specto.space;

import java.awt.Graphics;
import java.awt.Graphics2D;
import org.bm.blaise.specto.visometry.PlotComponent;
import org.bm.utils.SpaceGridSampleSet;
import scio.coordinate.Point3D;
import scio.coordinate.sample.SampleGenerator;

/**
 *
 * @author Elisha Peterson
 */
public class SpacePlotComponent extends PlotComponent<Point3D> {

    /** View settings */
    boolean anaglyph;

    //
    // CONSTRUCTORS
    //

    public SpacePlotComponent() {
        super(new SpaceVisometry());
        visometryGraphics = new SpaceGraphics(visometry);
        SpacePlotResizer resizer = new SpacePlotResizer((SpaceVisometry) visometry, this);
        defaultMouseListener = resizer;
        defaultMouseWheelListener = resizer;
    }

    /** Initializes with a title and (if desired) axes labeled by x,y,z. */
    public SpacePlotComponent(String title, boolean showAxes) {
        this();
        setTitle(title);
        if (showAxes)
            addPlottable(new SpaceAxes("x", "y", "z"));
    }

    /** Initializes with axes labeled by specified strings. */
    public SpacePlotComponent(String title, String xLabel, String yLabel, String zLabel) {
        this();
        setTitle(title);
        addPlottable(new SpaceAxes(xLabel, yLabel, zLabel));
    }

    //
    // GETTERS & SETTERS
    //

    transient SpaceGridSampleSet pgss;
    
    public SampleGenerator<Point3D> getSSG() {
        SpaceVisometry pv = (SpaceVisometry) this.visometry;
        if (pgss == null)
            pgss = new SpaceGridSampleSet(pv.minPoint, pv.maxPoint);
        else {
            pgss.setMin(pv.minPoint);
            pgss.setMax(pv.maxPoint);
        }
        // TODO - figure out how to make this work better (i.e. should it depend on screen size??)
        return pgss;
    }

    public SpaceProjection getProjection() {
        return ((SpaceGraphics) visometryGraphics).proj;
    }

    public boolean isAnaglyph() {
        return anaglyph;
    }

    public void setAnaglyph(boolean anaglyph) {
        if (this.anaglyph != anaglyph) {
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

    public void setVisometryGraphics(SpaceGraphics gr) {
        if (gr instanceof SpaceGraphics && this.visometryGraphics != gr)
            this.visometryGraphics = gr;
    }

    //
    // DRAW METHODS
    //

    @Override
    protected void paintComponent(Graphics g) {
        SpaceGraphics sg = (SpaceGraphics) visometryGraphics;
        sg.clearScene();
        plottables.draw(sg); // this constructs the 3d plottables, and adds them to the graphics scene
        sg.drawScene((Graphics2D) g, anaglyph);
    }

}