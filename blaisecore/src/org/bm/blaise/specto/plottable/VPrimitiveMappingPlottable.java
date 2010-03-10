/*
 * VPrimitiveMappingPlottable.java
 * Created on Nov 4, 2009
 */

package org.bm.blaise.specto.plottable;

import scio.coordinate.sample.SampleCoordinateSetGenerator;
import org.bm.blaise.specto.primitive.PrimitiveStyle;
import org.bm.blaise.specto.visometry.*;

/**
 * <p>
 *   This plottable uses a <code>SampleSetGenerator</code> and a <code>PrimitiveStyle</code>
 *   to plot a grid of primitives. It is intended to display repeated graphical elements
 *   that differ slightly from one point to the next, for example vector fields.
 * </p>
 *
 * @param <C> the type of coordinate (a primitive is generated at each coordinate)
 * @param <P> the type of primitive generated
 * @param <CV> the type of coordinate for the visometry
 *
 * @author Elisha Peterson
 */
public abstract class VPrimitiveMappingPlottable<C, P, CV> extends AbstractPlottable<CV>
        implements PrimitiveMapper<C, P, CV>, VisometryChangeListener {

    //
    // PROPERTIES
    //

    /** The styling class */
    protected PrimitiveStyle<P> style;

    /** The coordinate grid generator. */
    protected SampleCoordinateSetGenerator<C> ssg;

    //
    // CONSTRUCTORS
    //

    /**
     * Constructor requires providing a style class and a sample generator.
     *
     * @param style the style for the primitive <code>P</code>
     * @param ssg the sample generator for the coordinate <code>C</code>
     */
    public VPrimitiveMappingPlottable(PrimitiveStyle<P> style, SampleCoordinateSetGenerator<C> ssg) {
        this.style = style;
        this.ssg = ssg;
    }

    //
    // GET/SET PATTERNS
    //

    public SampleCoordinateSetGenerator<C> getSampleSetGenerator() {
        return ssg;
    }

    public void setSampleSetGenerator(SampleCoordinateSetGenerator<C> ssg) {
        this.ssg = ssg;
    }
    
    //
    // DRAW METHODS
    //

    /** Stores graphic primitive objects displayed. */
    transient protected P[] primitives;

    /** 
     * Scales the size of graphics primitives appropriately. Extended classes
     * should use this method to adjust the size of the primitives, if necessary.
     */
    public void scalePrimitives(Visometry<CV> vis) {}

    public void visometryChanged(Visometry vis, VisometryGraphics canvas) {
        primitives = primitivesAt(ssg.getSamples(), canvas);
        scalePrimitives(vis);
    }

    @Override
    public void paintComponent(VisometryGraphics<CV> vg) {
        vg.drawWinPrimitives(primitives, style);
    }

}
