/**
 * TextRenderer.java
 * Created Jul 31, 2014
 */
package com.googlecode.blaisemath.graphics.swing;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2009 - 2016 Elisha Peterson
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
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.googlecode.blaisemath.style.Anchor;
import static com.googlecode.blaisemath.style.Anchor.*;
import com.googlecode.blaisemath.style.AttributeSet;
import com.googlecode.blaisemath.style.Renderer;
import com.googlecode.blaisemath.style.Styles;
import static com.googlecode.blaisemath.style.Styles.*;
import com.googlecode.blaisemath.util.AnchoredText;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.font.FontRenderContext;
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
    
    private static final Logger LOG = Logger.getLogger(TextRenderer.class.getName());
    private static final TextRenderer INST = new TextRenderer();
    
    private static final Table<String, String, Anchor> ANCHOR_BASELINE_LOOKUP = HashBasedTable.create();
    static {
        ANCHOR_BASELINE_LOOKUP.put(TEXT_ANCHOR_START, ALIGN_BASELINE_BASELINE, SOUTHWEST);
        ANCHOR_BASELINE_LOOKUP.put(TEXT_ANCHOR_START, ALIGN_BASELINE_MIDDLE, WEST);
        ANCHOR_BASELINE_LOOKUP.put(TEXT_ANCHOR_START, ALIGN_BASELINE_HANGING, NORTHWEST);
        ANCHOR_BASELINE_LOOKUP.put(TEXT_ANCHOR_MIDDLE, ALIGN_BASELINE_BASELINE, SOUTH);
        ANCHOR_BASELINE_LOOKUP.put(TEXT_ANCHOR_MIDDLE, ALIGN_BASELINE_MIDDLE, CENTER);
        ANCHOR_BASELINE_LOOKUP.put(TEXT_ANCHOR_MIDDLE, ALIGN_BASELINE_HANGING, NORTH);
        ANCHOR_BASELINE_LOOKUP.put(TEXT_ANCHOR_END, ALIGN_BASELINE_BASELINE, SOUTHEAST);
        ANCHOR_BASELINE_LOOKUP.put(TEXT_ANCHOR_END, ALIGN_BASELINE_MIDDLE, EAST);
        ANCHOR_BASELINE_LOOKUP.put(TEXT_ANCHOR_END, ALIGN_BASELINE_HANGING, NORTHEAST);
    }
    
    public static Renderer<AnchoredText, Graphics2D> getInstance() {
        return INST;
    }
    
    @Override
    public void render(AnchoredText primitive, AttributeSet style, Graphics2D canvas) {
        String text = primitive.getText();
        if (Strings.isNullOrEmpty(text)) {
            return;
        }

        canvas.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        canvas.setColor(style.getColor(Styles.FILL, Color.black));
        canvas.setFont(Styles.getFont(style));
        Rectangle2D bounds = boundingBox(primitive, style, canvas);
        canvas.drawString(text, (float) bounds.getX(), (float) (bounds.getMaxY()));
    }

    @Override
    public boolean contains(AnchoredText primitive, AttributeSet style, Point2D point) {
        Rectangle2D bounds = boundingBox(primitive, style);
        return bounds != null && bounds.contains(point);
    }

    @Override
    public boolean intersects(AnchoredText primitive, AttributeSet style, Rectangle2D rect) {
        Rectangle2D bounds = boundingBox(primitive, style);
        return bounds != null && bounds.intersects(rect);
    }
    
    protected static Anchor anchorFromStyle(AttributeSet style) {
        Object anchor = style.get(Styles.TEXT_ANCHOR);
        Object baseline = style.get(Styles.ALIGN_BASELINE);
        if (!(anchor == null || anchor instanceof String || anchor instanceof Anchor)) {
            LOG.log(Level.WARNING, "Invalid text anchor: {0}", anchor);
        }
        if (!(baseline == null || baseline instanceof String)) {
            LOG.log(Level.WARNING, "Invalid baseline: {0}", anchor);
        }
        if (anchor == null) {
            anchor = TEXT_ANCHOR_START;
        }
        if (baseline == null) {
            baseline = ALIGN_BASELINE_BASELINE;
        }
        
        if (anchor instanceof Anchor) {
            return (Anchor) anchor;
        } else if (ANCHOR_BASELINE_LOOKUP.contains(anchor, baseline)) {
            return ANCHOR_BASELINE_LOOKUP.get(anchor, baseline);
        } else {
            LOG.log(Level.WARNING, "Invalid anchor/baseline: {0}/{1}", new Object[]{anchor, baseline});
            return Anchor.SOUTHWEST;
        }
    }
    
    protected static Anchor anchorFromAttributes(String anchor, String baseline) {
        return ANCHOR_BASELINE_LOOKUP.contains(anchor, baseline) ? ANCHOR_BASELINE_LOOKUP.get(anchor, baseline)
                : Anchor.SOUTHWEST;
    }

    @Override
    public Rectangle2D boundingBox(AnchoredText primitive, AttributeSet style) {
        return boundingBox(primitive, style, null);
    }
    
    public Rectangle2D boundingBox(AnchoredText primitive, AttributeSet style, Graphics2D canvas) {
        if (Strings.isNullOrEmpty(primitive.getText())) {
            return null;
        }
        
        Font font = Styles.getFont(style);
        FontRenderContext frc = canvas == null ? new FontRenderContext(font.getTransform(), true, false)
                : canvas.getFontRenderContext();
        double width = font.getStringBounds(primitive.getText(), frc).getWidth();
        double height = font.getSize()*72/Toolkit.getDefaultToolkit().getScreenResolution();
        
        Anchor textAnchor = anchorFromStyle(style);
        Point2D offset = style.getPoint(Styles.OFFSET, new Point());
        assert offset != null;
        Point2D shift = textAnchor.getRectOffset(width, height);
        return new Rectangle2D.Double(
                primitive.getX() + offset.getX() + shift.getX(), 
                primitive.getY() + offset.getY() + shift.getY()-height, 
                width, height);
    }
    
}
