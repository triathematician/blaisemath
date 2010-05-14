/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package visometry;

import primitive.PrimitiveEntry;
import primitive.style.PrimitiveStyle;

/**
 * Stores a local-coordinate primitive, and the corresponding window-system primitive.
 * This is the standard "drawn" object used by plottables.
 *
 * @author Elisha Peterson
 */
public class VPrimitiveEntry extends PrimitiveEntry {
    /** The local primitive. */
    public Object local;
    /** Whether the entry needs to be converted from local to window coordinates. */
    public boolean needsConversion = true;

    /** Construct with specified primitive and style. */
    public VPrimitiveEntry(Object localPrimitive, PrimitiveStyle style) {
        super(null, style);
        local = localPrimitive;
    }
}
