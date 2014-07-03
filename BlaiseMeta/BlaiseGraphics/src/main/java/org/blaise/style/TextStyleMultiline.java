/*
 * TextStyleMultiline.java
 * Created on Jan 2, 2013
 */

package org.blaise.style;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2009 - 2014 Elisha Peterson
 * --
 * Licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
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


import com.google.common.collect.Lists;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.Arrays;

/**
 * Draws a string within clipping boundaries defined by a graphics canvas.
 * For this to work properly, clip must be set on the canvas prior to drawing.
 *
 * @author cornidc1
 */
public class TextStyleMultiline extends TextStyleBasic {
    
    @Override
    public String toString() {
        return String.format("TextStyleMultiline[fill=%s, font=%s, fontSize=%.1f, offset=(%.1f,%.1f), textAnchor=%s]", 
                fill, font, fontSize, offset.getX(), offset.getY(), textAnchor);
    }

    @Override
    public void draw(Point2D point, String string, Graphics2D canvas) {
        if (string == null || string.length() == 0) {
            return;
        }
        Font f = getFont() == null ? canvas.getFont().deriveFont(getFontSize()) : getFont();
        canvas.setFont(f);
        FontMetrics fm = canvas.getFontMetrics();
        canvas.setColor(getFill());
        
        String[] lns = string.split("\n|\r\n");
        double y0 = point.getY();
        for (String s : Lists.reverse(Arrays.asList(lns))) {
            canvas.drawString(s, (float)point.getX(), (float) y0);
            y0 -= fm.getHeight();
        }
    }

}
