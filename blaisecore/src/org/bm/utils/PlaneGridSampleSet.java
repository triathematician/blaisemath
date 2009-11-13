/*
 * PlaneSampleSet.java
 * Created on Nov 4, 2009
 */

package org.bm.utils;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.AbstractList;
import java.util.List;
import scio.coordinate.utils.SampleSetGenerator;

/**
 * <p>
 *     This class generates a grid of evenly-spaced sample points in space.
 * </p>
 * @author Elisha Peterson
 */
public class PlaneGridSampleSet implements SampleSetGenerator<Point2D.Double> {

    /** Boundary of sampled region. */
    Rectangle2D.Double bounds;
    /** Border around the sampling region. */
    double sampleBorder = 0;
    /**
     * Determines whether the grid is sampled at "nice" points, i.e. corresponding
     * to integers, half-integers, etc.
     */
    boolean sampleNice = false;
    /** Sample numbers. */
    int nx = 10;
    int ny = 10;

    public PlaneGridSampleSet(Rectangle2D.Double bounds) {
        this.bounds = bounds;
    }

    //
    //
    // BEANS
    //
    //

    public Rectangle2D.Double getBounds() {
        return bounds;
    }

    public void setBounds(Rectangle2D.Double bounds) {
        this.bounds = bounds;
    }

    public int getNX() {
        return nx;
    }

    public void setNX(int nx) {
        this.nx = nx;
    }

    public int getNY() {
        return ny;
    }

    public void setNY(int ny) {
        this.ny = ny;
    }

    public boolean isSampleNice() {
        return sampleNice;
    }

    public void setSampleNice(boolean sampleNice) {
        this.sampleNice = sampleNice;
    }
    

    //
    //
    // SAMPLE ALGORITHMS
    //
    //

    /** Sample step. */
    transient Point2D.Double diff = new Point2D.Double();

    /** 
     * Computes samples using the current boundaries.
     *
     * @param nXSamples number of samples in the x direction
     * @param nYSamples number of samples in the y direction
     * @return an <code>AbstractList</code> containing the grid of sampled points
     */
    List<Point2D.Double> computeSamples(int nXSamples, int nYSamples) {
        nx = nXSamples;
        ny = nYSamples;
        diff.x = bounds.width / (nx - 1);
        diff.y = bounds.height / (ny - 1);
        return new AbstractList<Point2D.Double>() {
            int n = nx * ny;
            @Override
            public Point2D.Double get(int index) {
                return new Point2D.Double(
                        bounds.x + diff.x * (index % nx),
                        bounds.y + diff.y * ( (int) (index / nx) )
                        );
            }
            @Override
            public int size() {
                return n;
            }
        };
    }

    /**
     * Computes samples using the current boundaries; and puts the sample points
     * at "nice" coordinates. Tries to ensure the spacing is about what is suggested
     * by the parameters to the command, although this is not guaranteed, as placement
     * of the poitns takes priority.
     *
     * @param approxDX approximate spacing in the x direction
     * @param approxDY approximate spacing in the y direction
     * @return an <code>AbstractList</code> containing the grid of sampled points
     */
    public List<Point2D.Double> computeNiceSamples(double approxDX, double approxDY) {
        final double[] xRange = NiceRangeGenerator.STANDARD.niceRange(bounds.x, bounds.x + bounds.width, approxDX);
        final double[] yRange = NiceRangeGenerator.STANDARD.niceRange(bounds.y, bounds.y + bounds.height, approxDY);
        nx = xRange.length;
        ny = yRange.length;
        diff.x = xRange[1] - xRange[0];
        diff.y = yRange[1] - yRange[0];
        return new AbstractList<Point2D.Double>() {
            int n = nx * ny;
            @Override
            public Point2D.Double get(int index) {
                return new Point2D.Double(xRange[index % nx], yRange[index / nx]);
            }
            @Override
            public int size() {
                return n;
            }
        };
    }

    public List<Point2D.Double> getSamples() {
        if (sampleNice) {
            return computeNiceSamples(bounds.width / nx, bounds.height / ny);
        } else {
            return computeSamples(nx, ny);
        }
    }

    public Point2D.Double getSampleDiff() {
        return diff;
    }

    @Override
    public String toString() {
        return "Grid Sample";
    }
}
