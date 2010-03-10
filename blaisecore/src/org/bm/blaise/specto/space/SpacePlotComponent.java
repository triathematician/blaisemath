/*
 * SpacePlotComponent.java
 * Created on Oct 20, 2009
 */

package org.bm.blaise.specto.space;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.SampleModel;
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
    int width, height;
    BufferedImage leftImage; SampleModel leftModel; DataBufferByte leftBuffer;
    BufferedImage rightImage; SampleModel rightModel; DataBufferInt rightBuffer;
    BufferedImage viewImage;

    int chunk; // how many rows are copied at a time
    int[] leftSamples; // stores samples from left (grayscale)
    int[] rightSamplesGreen; // stores samples from right (green)
    int[] rightSamplesBlue; // stores samples from right (blue)
    int[] viewInts; // stores data used for the viewing image

    void initAnaglyph() {
        leftImage = null; leftModel = null; leftBuffer = null; leftSamples = null;
        rightImage = null; rightModel = null; rightBuffer = null; rightSamplesGreen = null; rightSamplesBlue = null;
        viewImage = null; viewInts = null;

        width = getWidth();
        height = getHeight();
        chunk = 10000 / width;
        if (chunk == 0)
            chunk = 1;
        
        leftImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        leftModel = leftImage.getRaster().getSampleModel();
        leftBuffer = (DataBufferByte) leftImage.getRaster().getDataBuffer();
        leftSamples = new int[chunk*width];

        rightImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        rightModel = rightImage.getRaster().getSampleModel();
        rightBuffer = (DataBufferInt) rightImage.getRaster().getDataBuffer();
        rightSamplesGreen = new int[chunk*width];
        rightSamplesBlue = new int[chunk*width];

        viewImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        viewInts = ((DataBufferInt) viewImage.getRaster().getDataBuffer()).getData();
    }

    void combineImages() {
        int row = 0;
        while (row < height) {
            int nRows = chunk;
            if (row + nRows > height)
                nRows = height - row;
            int nSamples = nRows * width;
            int i0 = row * width;
            leftModel.getSamples(0, row, width, nRows, 0, leftSamples, leftBuffer);
            rightModel.getSamples(0, row, width, nRows, 1, rightSamplesGreen, rightBuffer);
            rightModel.getSamples(0, row, width, nRows, 2, rightSamplesBlue, rightBuffer);
            for (int i = 0; i < nSamples; i++)
                viewInts[i + i0] =
                        (leftSamples[i] << 16)
                        + (rightSamplesGreen[i] << 8)
                        + (rightSamplesBlue[i]);
            row += chunk;
        }
    }


    @Override
    protected void paintComponent(Graphics g) {
        SpaceGraphics sg = (SpaceGraphics) visometryGraphics;
        if (anaglyph) {
            // first construct the scene using the paint methods
            sg.clearScene();
            super.paintComponent(g);

            if (leftImage == null)
                initAnaglyph();
            Graphics2D leftGr = leftImage.createGraphics();
            Graphics2D rightGr = rightImage.createGraphics();
            if (isOpaque()) {
                leftGr.setColor(getBackground());
                leftGr.fillRect(0, 0, getWidth(), getHeight());
                rightGr.setColor(getBackground());
                rightGr.fillRect(0, 0, getWidth(), getHeight());
            }
            sg.proj.useLeftCamera();
            sg.drawScene(leftGr);
            sg.proj.useRightCamera();
            sg.drawScene(rightGr);
            combineImages();
            ((Graphics2D) g).drawImage(viewImage, 0, 0, null);
        } else {
            sg.proj.useCenterCamera();
            sg.clearScene();
            super.paintComponent(g);
            sg.drawScene((Graphics2D) g);
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