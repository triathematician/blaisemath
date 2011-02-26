/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bm.blaise.specto.plottable;

import java.util.ArrayList;
import java.util.List;
import org.bm.blaise.specto.primitive.PrimitiveStyle;
import org.bm.blaise.specto.visometry.Plottable;
import org.bm.blaise.specto.visometry.Visometry;
import org.bm.blaise.specto.visometry.VisometryChangeListener;
import org.bm.blaise.specto.visometry.VisometryGraphics;
import scio.coordinate.sample.SampleGenerator;

/**
 * @param <V> the type of value associated with each primitive; together with a point,
 *   this should determine the object being represented
 * @param <P> the type of the primitive object being displayed
 * @param <C> the type of the coordinate being displayed
 *
 * @author ae3263
 */
abstract public class SamplerPlottable<V, P, C> extends Plottable<C>
        implements VisometryChangeListener {

    /** Coordinate generator */
    SampleGenerator<C> sg;
    
    /** Style used to draw the primitives. */
    protected PrimitiveStyle style;

    public SamplerPlottable(SampleGenerator<C> sg) {
        this.sg = sg;
    }

    //
    // VALUE PATTERNS
    //

    public SampleGenerator<C> getSampleGenerator() {
        return sg;
    }

    public void setSampleGenerator(SampleGenerator<C> sg) {
        if (this.sg != sg) {
            this.sg = sg;
            needsComputation = true;
            fireStateChanged();
        }
    }

    //
    // DRAW METHODS
    //

    /** Contains the sample points. */
    transient List<C> samples;

    /** Contains the value objects. */
    transient List<V> values;
    /** The scaling factor used to scale the arrows up or down */
    transient double scaleFactor;
    
    /** Contains the primitive objects to display. */
    transient P[] primitives;

    /** Flag saying whether vector positions need to be recomputed. */
    transient protected boolean needsComputation = true;
    /** Flag saying whether graphic arrows need to be recomputed. */
    transient protected boolean needsPrimitiveComputation = true;

    /** This generates the "value" objects used to construct the primitives. */
    void constructValues() {
        // construct the values
        samples = sg.getSamples();
        values = new ArrayList<V>(samples.size());
        for (C pt : samples)
            values.add(getValue(pt));
    }

    /** 
     * 
     * @param sample location of a sample
     * @return an object corresponding to the specified sample
     */
    abstract protected V getValue(C sample);

    /** computes the "scale" of the value objects */
    protected double getScaleFactor(List<V> values, C diff){
        return 1.0;
    }

    /** 
     * Constructs the graphic arrows to be actually displayed. 
     */
    abstract protected P[] getPrimitives(List<C> samples, List<V> values, double scaleFactor, VisometryGraphics<C> vg);

    public void visometryChanged(Visometry vis, VisometryGraphics canvas) {
        if (needsComputation)
            constructValues();
        scaleFactor = getScaleFactor(values, sg.getSampleDiff());
        primitives = getPrimitives(samples, values, scaleFactor, canvas);
        needsPrimitiveComputation = false;
    }

    @Override
    public void draw(VisometryGraphics<C> vg) {
        if (needsComputation || needsPrimitiveComputation)
            visometryChanged(null, vg);
        style.draw(vg.getScreenGraphics(), primitives, selected);
    }
    

}
