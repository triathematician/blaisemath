/**
 * TextRenderer.java
 * Created Jul 31, 2014
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
import com.googlecode.blaisemath.style.Anchor;
import static com.googlecode.blaisemath.style.Anchor.CENTER;
import static com.googlecode.blaisemath.style.Anchor.EAST;
import static com.googlecode.blaisemath.style.Anchor.NORTH;
import static com.googlecode.blaisemath.style.Anchor.NORTHEAST;
import static com.googlecode.blaisemath.style.Anchor.NORTHWEST;
import static com.googlecode.blaisemath.style.Anchor.SOUTH;
import static com.googlecode.blaisemath.style.Anchor.SOUTHEAST;
import static com.googlecode.blaisemath.style.Anchor.WEST;
import com.googlecode.blaisemath.style.AttributeSet;
import com.googlecode.blaisemath.style.Renderer;
import com.googlecode.blaisemath.style.Styles;
import com.googlecode.blaisemath.util.AnchoredText;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Renders a string of text on a canvas.
 * 
 * @author Elisha
 */
public class TextRenderer implements Renderer<AnchoredText, Graphics2D> {

    private static final TextRenderer INST = new TextRenderer();
    
    public static Renderer<AnchoredText, Graphics2D> getInstance() {
        return INST;
    }
    
    public void render(AnchoredText primitive, AttributeSet style, Graphics2D canvas) {
        String text = primitive.getText();
        if (Strings.isNullOrEmpty(text)) {
            return;
        }
        
        canvas.setColor(style.getColor(Styles.FILL, Color.black));
        canvas.setFont(Styles.getFont(style));
        Rectangle2D bounds = boundingBox(primitive, style);
        canvas.drawString(text, (float) bounds.getX(), (float) (bounds.getMaxY()));
    }

    public boolean contains(AnchoredText primitive, AttributeSet style, Point2D point) {
        Rectangle2D bounds = boundingBox(primitive, style);
        return bounds != null && bounds.contains(point);
    }

    public boolean intersects(AnchoredText primitive, AttributeSet style, Rectangle2D rect) {
        Rectangle2D bounds = boundingBox(primitive, style);
        return bounds != null && bounds.intersects(rect);
    }

    public Rectangle2D boundingBox(AnchoredText primitive, AttributeSet style) {
        if (Strings.isNullOrEmpty(primitive.getText())) {
            return null;
        }
        
        Object anchor = style.get(Styles.TEXT_ANCHOR);
        if (!(anchor == null || anchor instanceof String || anchor instanceof Anchor)) {
            Logger.getLogger(TextRenderer.class.getName()).log(Level.WARNING,
                    "Invalid text anchor: {0}", anchor);
        }
        
        Font font = Styles.getFont(style);
        FontRenderContext frc = new FontRenderContext(font.getTransform(), true, true);
        TextLayout tl = new TextLayout(primitive.getText(), font, frc);
        double width = font.getStringBounds(primitive.getText(), frc).getWidth();
        double height = tl.getBounds().getHeight();
        
        Anchor textAnchor = anchor == null ? Anchor.SOUTHWEST 
                : anchor instanceof Anchor ? (Anchor) anchor
                : anchor instanceof String ? Anchor.valueOf((String) anchor)
                : null;
        Point2D offset = style.getPoint(Styles.OFFSET, new Point());
        if (textAnchor == Anchor.SOUTHWEST) {
            return new Rectangle2D.Double(
                    primitive.getX() + offset.getX(), 
                    primitive.getY() + offset.getY()-height, 
                    width, height);
        }

        Point2D.Double shift = anchorShift(textAnchor, width, height);

        return new Rectangle2D.Double(
                primitive.getX() + offset.getX() + shift.x, 
                primitive.getY() + offset.getY() + shift.y-height, 
                width, height);
    }
    

    /** 
     * Generates a shift coordinate based on a given width and height, relative to the
     * provided anchor location.
     * @param textAnchor
     * @param width
     * @param height
     * @return 
     */
    public static Point2D.Double anchorShift(Anchor textAnchor, double width, double height) {
        Point2D.Double shift = new Point2D.Double();
        switch (textAnchor) {
            case NORTHEAST:
            case EAST:
            case SOUTHEAST:
                shift.x = -width;
                break;
            case NORTH:
            case CENTER:
            case SOUTH:
                shift.x = -width / 2;
                break;
            default:
                // all other cases don't need shift
                break;
        }

        switch (textAnchor) {
            case NORTHWEST:
            case NORTH:
            case NORTHEAST:
                shift.y = height;
                break;
            case WEST:
            case CENTER:
            case EAST:
                shift.y = height / 2;
                break;
            default:
                // all other cases don't need shift
                break;
        }
        
        return shift;
    }
    
}
