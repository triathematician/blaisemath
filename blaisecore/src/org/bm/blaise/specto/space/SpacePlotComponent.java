/*
 * SpacePlotComponent.java
 * Created on Oct 20, 2009
 */

package org.bm.blaise.specto.space;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
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
            addPlottable(SpaceAxes.instance("x", "y", "z"));
    }

    /** Initializes with axes labeled by specified strings. */
    public SpacePlotComponent(String title, String xLabel, String yLabel, String zLabel) {
        this();
        setTitle(title);
        addPlottable(SpaceAxes.instance(xLabel, yLabel, zLabel));
    }

    //
    // GETTERS & SETTERS
    //

    public SampleCoordinateSetGenerator<Point3D> getSSG() {
        // TODO - hook into this generator in a better way!!
        return ((SpaceGraphics) visometryGraphics).getSampleSetGenerator();
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

    public void setVisometryGraphics(SpaceGraphics visometryGraphics) {
        this.visometryGraphics = visometryGraphics;
    }

    
    // left = red, right = cyan
    BufferedImage leftImage; SampleModel leftR; DataBuffer leftBuffer;
    BufferedImage rightImage; SampleModel rightR; DataBuffer rightBuffer;

    void createCombinedImage() {
        int w = leftImage.getWidth();
        int h = leftImage.getHeight();
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                // overwrite the specified sample in the right image with a grayscale -> red version of the first
                rightR.setSample(x, y, 0,
                        (leftR.getSample(x, y, 0, leftBuffer)
                        + leftR.getSample(x, y, 1, leftBuffer)
                        + leftR.getSample(x, y, 2, leftBuffer) ) / 3,
                        rightBuffer);
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (anaglyph) {
            if (leftImage == null || rightImage == null) {
                leftImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
                rightImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
                leftR = leftImage.getRaster().getSampleModel();
                leftBuffer = leftImage.getRaster().getDataBuffer();
                rightR = rightImage.getRaster().getSampleModel();
                rightBuffer = rightImage.getRaster().getDataBuffer();
            }
            ((SpaceGraphics) visometryGraphics).clearScene();
            super.paintComponent(g);
            if (isOpaque()) {
                leftImage.getGraphics().setColor(getBackground());
                leftImage.getGraphics().fillRect(0, 0, getWidth(), getHeight());
                rightImage.getGraphics().setColor(getBackground());
                rightImage.getGraphics().fillRect(0, 0, getWidth(), getHeight());
            }
            SpaceProjection proj = ((SpaceGraphics) visometryGraphics).proj;
            proj.useLeftCamera();
            ((SpaceGraphics) visometryGraphics).drawScene((Graphics2D) leftImage.getGraphics());
            proj.useRightCamera();
            ((SpaceGraphics) visometryGraphics).drawScene((Graphics2D) rightImage.getGraphics());
            createCombinedImage();
            ((Graphics2D) g).drawImage(rightImage, 0, 0, null);
        } else {
            ((SpaceGraphics) visometryGraphics).proj.useCenterCamera();
            ((SpaceGraphics) visometryGraphics).clearScene();
            super.paintComponent(g);
            ((SpaceGraphics) visometryGraphics).drawScene((Graphics2D) g);
        }
    }

    public SpaceProjection getProjection() {
        return ((SpaceGraphics) visometryGraphics).proj;
    }

    @Override
    public void componentResized(ComponentEvent e) {
        leftImage = null;
        rightImage = null;
        super.componentResized(e);
    }



}