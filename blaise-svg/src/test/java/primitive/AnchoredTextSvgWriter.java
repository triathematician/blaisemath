package primitive;

import com.googlecode.blaisemath.primitive.Anchor;
import com.googlecode.blaisemath.primitive.AnchoredText;
import com.googlecode.blaisemath.style.AttributeSet;
import com.googlecode.blaisemath.style.Styles;
import com.googlecode.blaisemath.svg.render.StyleWriter;
import com.googlecode.blaisemath.svg.render.SvgRenderException;
import com.googlecode.blaisemath.svg.xml.SvgText;

/**
 * Converts an anchored text primitive to an SVG text primitive. Assumes text is being written on a single line using
 * {@link com.googlecode.blaisemath.graphics.swing.render.TextRenderer}.
 * @author Elisha Peterson
 */
public class AnchoredTextSvgWriter implements PrimitiveSvgWriter<AnchoredText, SvgText> {

    @Override
    public SvgText write(String id, AnchoredText r, AttributeSet style) throws SvgRenderException {
        SvgText res = SvgText.create((float) r.x, (float) r.y, r.getText());
        Object ta = style.get(Styles.TEXT_ANCHOR);
        AttributeSet copy = style.copy();
        if (ta instanceof Anchor) {
            copy.put(Styles.TEXT_ANCHOR, Styles.toTextAnchor((Anchor) ta));
            copy.put(Styles.ALIGN_BASELINE, Styles.toAlignBaseline((Anchor) ta));
        } else if (ta instanceof String && Styles.isAnchorName(ta)) {
            copy.put(Styles.TEXT_ANCHOR, Styles.toTextAnchor((String) ta));
            copy.put(Styles.ALIGN_BASELINE, Styles.toAlignBaseline((String) ta));
        }
        res.id = id;
        res.style = StyleWriter.toString(style);
        return res;
    }

}
