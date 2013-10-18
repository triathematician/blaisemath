/*
 * TextPathStyle.java
 * Created Dec 2010
 */
package org.blaise.style;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.util.Set;

/**
 * This style paints the label text instead of the stroke.
 *
 * @author petereb1
 */
public class TextPathStyle implements ShapeStyle {

    private final BasicStringStyle textStyle;
    private final String text;
    private final boolean stretch;
    
    public TextPathStyle(BasicStringStyle style, String label) {
        this(style, label, false);
    }

    public TextPathStyle(BasicStringStyle style, String label, boolean stretch) {
        this.textStyle = style;
        this.text = label;
        this.stretch = stretch;
    }

    public void draw(Shape primitive, Graphics2D canvas, Set<VisibilityHint> hints) {
//        canvas.setColor(new Color(255,128,128,192));
//        canvas.setStroke(new BasicStroke());
//        canvas.draw(primitive);
        canvas.setFont(textStyle.getFont());
        canvas.setStroke(new TextStroke(text, textStyle.getFont(), stretch, false));
        canvas.setColor(textStyle.getColor());
        canvas.draw(primitive);
        canvas.setStroke(new BasicStroke());
    }

}
