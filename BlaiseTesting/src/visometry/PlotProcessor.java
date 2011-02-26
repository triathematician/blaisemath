/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package visometry;

import primitive.PrimitiveEntry;

/**
 * Prepares a collection of <code>PrimitiveEntry</code>'s (i.e. object/style pairings) for rendering.
 * The main responsibility here is to convert the objects into window-coordinate primitives that
 * can then be drawn by the renderer.
 *
 * In certain cases, this class might also change the order of the primitives.
 *
 * Since this class is generally responsible for the translation process, it should also maintain pointers
 * between the "local" primitive and the "window" primitive object.
 *
 * After preparation, a renderer will use the data stored here to draw everything.
 *
 * @param <C> type of the local coordinate
 *
 * @see VPrimitiveEntry
 *
 * @author Elisha Peterson
 */
public abstract class PlotProcessor<C> {

    /** 
     * Object iterating over the primitives... possibly the same as that
     * passed into <code>prepare</code>, possibly different
     * (e.g. for a 3d plot require appropriate sorting)
     */
    Iterable<? extends PrimitiveEntry> primitives;

    /** 
     * Prepares using specified visometry.
     * @param prims the table of primitives and associated styles
     * @param vis the visometry used for the conversion process
     */
    void prepare(Iterable<VPrimitiveEntry> prims, Visometry<C> vis) {
        // use same order of the primitives
        primitives = prims;
        for (VPrimitiveEntry vp : prims)
            if (vp.needsConversion || vp.primitive == null)
                convert(vp, vis);
    }

    /**
     * Converts local primitives to window primitives in the specified visometry.
     * @param prim the primitive object to convert
     * @param vis the visometry used for conversion
     */
    abstract protected void convert(VPrimitiveEntry prim, Visometry<C> vis);
}
