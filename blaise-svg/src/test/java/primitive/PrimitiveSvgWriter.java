package primitive;

import com.googlecode.blaisemath.style.AttributeSet;
import com.googlecode.blaisemath.svg.render.SvgRenderException;
import com.googlecode.blaisemath.svg.xml.SvgElement;

/**
 * Maps Blaise graphics primitives to SVG components.
 * @author Elisha Peterson
 */
public interface PrimitiveSvgWriter<P, S extends SvgElement> {

    /**
     * Writes primitive as an SVG element.
     *
     * @param id
     * @param prim primitive
     * @param style primitive's style
     * @return element
     * @throws SvgRenderException if unable to write element
     */
    S write(String id, P prim, AttributeSet style) throws SvgRenderException;

}
