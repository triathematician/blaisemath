/*
 * WrappingStringStyle.java
 * Created on Jan 2, 2013
 */

package org.blaise.style;

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
    public void draw(Point2D point, String string, Graphics2D canvas, VisibilityHintSet visibility) {
        if (string == null || string.length() == 0) {
            return;
        }
        Font f = getFont() == null ? canvas.getFont().deriveFont(getFontSize()) : getFont();
        canvas.setFont(f);
        FontMetrics fm = canvas.getFontMetrics();
        canvas.setColor(ColorUtils.applyHints(getFill(), visibility));
        
        String lns[] = string.split("\n|\r\n");
        double y0 = point.getY();
        for (String s : Lists.reverse(Arrays.asList(lns))) {
            canvas.drawString(s, (float)point.getX(), (float) y0);
            y0 -= fm.getHeight();
        }
    }

}
