package com.googlecode.blaisemath.svg.reader;

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

import com.googlecode.blaisemath.graphics.Graphic;
import com.googlecode.blaisemath.graphics.swing.JGraphics;
import com.googlecode.blaisemath.primitive.AnchoredText;
import com.googlecode.blaisemath.style.AttributeSet;
import com.googlecode.blaisemath.svg.xml.SvgText;

import java.awt.*;

/**
 * Converts an SVG text element to its Java shape equivalent.
 * @author Elisha Peterson
 */
public final class SvgTextReader extends SvgReader<SvgText, AnchoredText> {

    @Override
    protected AnchoredText createPrimitive(SvgText r) {
        if (r == null) {
            throw new SvgReadException("Null SVG element");
        }
        return new AnchoredText(r.x, r.y, r.value);
    }

    @Override
    protected Graphic<Graphics2D> createGraphic(AnchoredText prim, AttributeSet style) {
        Graphic<Graphics2D> res = JGraphics.text(prim, style);
        res.setMouseDisabled(true);
        return res;
    }

}
