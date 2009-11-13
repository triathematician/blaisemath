/**
 * VParticleField.java
 * Created on Sep 24, 2009
 */

package org.bm.blaise.specto.plottable;

import org.bm.blaise.sequor.component.TimeClock;
import org.bm.blaise.specto.primitive.PathStyle;
import org.bm.blaise.specto.primitive.PointStyle;
import org.bm.blaise.specto.visometry.AbstractAnimatingPlottable;
import org.bm.blaise.specto.visometry.Visometry;
import org.bm.blaise.specto.visometry.VisometryGraphics;

/**
 * <p>
 *   <code>VParticleField</code> is a base class for visualizations that use a "particle field"
 *   to display something. These particles are randomly placed and animate according to specific
 *   abstract methods here.
 * </p>
 *
 * @author Elisha Peterson
 */
public abstract class VParticleField<V> extends AbstractAnimatingPlottable<V> {

    //
    //
    // ATTRIBUTES
    //
    //

    /** Display method */
    protected boolean displayAsPoints = true;

    /** Style of the particles, as points */
    protected PointStyle pStyle = new PointStyle();

    /** Style of the particles, as little paths */
    protected PathStyle style = new PathStyle();

    /** Number of particles to include */
    protected int numParticles = 50;

    /** Speed of particles, i.e. length between points */
    protected double speed = 1.0;

    /** Number of points in particle paths */
    protected int trailLength = 3;

    /** Number of paths to recycle per draw. */
    protected int nRecycle = 1;

    //
    //
    // BEAN PATTERNS
    //
    //

    public int getNumParticles() {
        return numParticles;
    }

    public void setNumParticles(int size) {
        this.numParticles = size;
        initPaths();
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public int getTrailLength() {
        return trailLength;
    }

    public void setTrailLength(int trailLength) {
        this.trailLength = trailLength;
        initPaths();
    }

    public int getNRecycle() {
        return nRecycle;
    }

    public void setNRecycle(int nRecycle) {
        this.nRecycle = nRecycle;
    }

    public boolean isDisplayAsPoints() {
        return displayAsPoints;
    }

    public void setDisplayAsPoints(boolean displayAsPoints) {
        this.displayAsPoints = displayAsPoints;
    }

    public PointStyle getPStyle() {
        return pStyle;
    }

    public void setPStyle(PointStyle pStyle) {
        this.pStyle = pStyle;
    }

    public PathStyle getStyle() {
        return style;
    }

    public void setStyle(PathStyle style) {
        this.style = style;
    }

    

    //
    //
    // PAINT METHODS
    //
    //

    transient protected V[][] paths;

    @Override
    public void paintComponent(VisometryGraphics<V> vg) {
        if (paths == null) {
            initPaths();
        }
        recomputeAtTime(null, vg, null);
        if (displayAsPoints) {
            vg.setPointStyle(pStyle);
            for (int i = 0; i < paths.length; i++) {
                vg.drawPoint(paths[i][paths[i].length-1]);
            }
        } else {
            vg.setPathStyle(style);
            vg.drawPaths(paths);
        }
    }

    transient int firstToRecycle = 0;
    transient int firstToRecycle2 = 0;
    transient int lastToRecycle = -1;
    transient int lastToRecycle2 = -1;


    public void recomputeAtTime(Visometry<V> vis, VisometryGraphics<V> canvas, TimeClock clock) {
        int nr = Math.min(nRecycle, paths.length);
        lastToRecycle = Math.min(firstToRecycle + nr, paths.length);
        if (lastToRecycle - firstToRecycle < nr) {
            firstToRecycle2 = 0;
            lastToRecycle2 = nr - (lastToRecycle - firstToRecycle);
        } else {
            firstToRecycle2 = -1;
            lastToRecycle2 = -1;
        }
        for (int i = 0; i < paths.length; i++) {
            if ( (i >= firstToRecycle && i < lastToRecycle) || (i >= firstToRecycle2 && i < lastToRecycle2) ) {
                paths[i] = getNewPath(trailLength);
            } else {
                advancePath(paths[i]);
            }
        }
        firstToRecycle = (firstToRecycle + nr) % paths.length;
    }

    //
    //
    // NEW ABSTRACT METHODS
    //
    //

    /**
     * Sets up the paths. Typically this involves initializing the "paths" variable
     * and adding the reqisite number of new paths. Here is a sample implementation:
     * <p>
     * <code>
     * paths = new V[numParticles][trailLength]; <br>
        for (int i = 0; i < paths.length; i++) {
            paths[i] = getNewPath(trailLength);
        } <br><br>
     * </code>
     * </p>
     * The only correction to make here is changing the <code>V</code> to be the desired class.
     */
    abstract public void initPaths();

    /**
     * This method returns one new path of specified length. The position and description of the
     * path is controlled by the implementation. Paths might be placed at random within
     * the screen view, or they might be limited to be along some fixed region (a curve or surface,
     * for example).
     *
     * @param length the length of the new path
     * @return an array of points of length <code>length</code>
     */
    abstract public V[] getNewPath(int length);

    /**
     * "Advances" the given path. Since the trail length is fixed, this should grow the path by
     * computing the next point of advance, and possibly remove the oldest point in the path.
     *
     * @param path the specific path to advance
     */
    abstract public void advancePath(V[] path);
}
