/*
 * MultilineTextRenderer.java
 * Created on Jan 2, 2013
 */

package com.googlecode.blaisemath.graphics.swing;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2009 - 2014 Elisha Peterson
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
import com.google.common.collect.Lists;
import com.googlecode.blaisemath.util.geom.LabeledPoint;
import com.googlecode.blaisemath.graphics.swing.TextRenderer;
import com.googlecode.blaisemath.style.AttributeSet;
import com.googlecode.blaisemath.style.Styles;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.util.Arrays;

/**
 * Draws text with line breaks, respecting the location of the line breaks.
 *
 * @todo calculate boundaries of multiline text
 * @todo use text alignment
 * 
 * @author petereb1
 */
public class MultilineTextRenderer extends TextRenderer {
    
    @Override
    public String toString() {
        return "MultilineTextRenderer";
    }

    @Override
    public void render(LabeledPoint text, AttributeSet style, Graphics2D canvas) {
        if (Strings.isNullOrEmpty(text.getText())) {
            return;
        }
        Font f = Styles.getFont(style);       
        canvas.setFont(f);
        canvas.setColor(style.getColor(Styles.FILL));
        
        double lineHeight = canvas.getFontMetrics().getHeight();
        String[] lns = text.getText().split("\n|\r\n");
        double y0 = text.getY();
        for (String s : Lists.reverse(Arrays.asList(lns))) {
            canvas.drawString(s, (float)text.getX(), (float) y0);
            y0 -= lineHeight;
        }
    }
    
}
