package com.googlecode.blaisemath.svg.render;

import com.googlecode.blaisemath.primitive.Anchor;
import com.googlecode.blaisemath.primitive.AnchoredText;
import com.googlecode.blaisemath.style.AttributeSet;
import com.googlecode.blaisemath.style.Styles;
import com.googlecode.blaisemath.svg.xml.SvgText;

/**
 * Write SVG text representation.
 * @author Elisha Peterson
 */
public class SvgTextRenderer extends SvgRenderer<AnchoredText> {

    @Override
    public void render(AnchoredText r, AttributeSet style, SvgTreeBuilder canvas) {
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
        res.id = StyleWriter.id(style);
        res.style = StyleWriter.toString(style);
        canvas.add(res);
    }

}
