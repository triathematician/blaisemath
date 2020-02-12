package primitive;

/*-
 * #%L
 * blaise-svg
 * --
 * Copyright (C) 2014 - 2020 Elisha Peterson
 * --
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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
