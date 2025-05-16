package com.googlecode.blaisemath.svg.render;

/*-
 * #%L
 * blaise-svg
 * --
 * Copyright (C) 2014 - 2025 Elisha Peterson
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

import com.google.common.base.Strings;
import com.googlecode.blaisemath.graphics.swing.render.WrappedTextRenderer;
import com.googlecode.blaisemath.primitive.StyledText;
import com.googlecode.blaisemath.style.AttributeSet;
import com.googlecode.blaisemath.svg.xml.SvgGroup;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * Write SVG text representation for text wrapped inside a rectangle.
 * @author Elisha Peterson
 */
public class SvgWrappedTextRenderer extends SvgRenderer<String> {

    private static final Rectangle2D bounds = new Rectangle2D.Double();

    @Override
    public void render(String text, AttributeSet style, SvgTreeBuilder canvas) {
        Graphics2D testCanvas = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB).createGraphics();
        WrappedTextRenderer rend = new WrappedTextRenderer();
        rend.setMinWidthFactor(0);
        rend.setMaxReduceFontSize(0);
        Iterable<StyledText> lines = rend.computeLines(text, style, bounds, WrappedTextRenderer.defaultInsets(), testCanvas);

        SvgGroup grp = canvas.beginGroup();
        grp.id = StyleWriter.id(style);
        grp.style = StyleWriter.toString(style);
        SvgTextRenderer renderer = new SvgTextRenderer();
        for (StyledText st : lines) {
            if (st.getText() != null && !Strings.isNullOrEmpty(st.getText().getText())) {
                renderer.render(st.getText(), st.getStyle(), canvas);
            }
        }
        canvas.endGroup();
    }

}
