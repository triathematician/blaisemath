/**
 * BasicTextGraphic.java
 * Created Jan 22, 2011
 */
package org.blaise.graphics;

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

import com.google.common.base.Strings;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.blaise.style.TextStyle;
import org.blaise.util.PointBean;

/**
 * Draws a string at a point.
 *
 * @author Elisha
 */
public class BasicTextGraphic extends GraphicSupport
        implements PointBean<Point2D> {

    /** Basepoint for the string */
    protected Point2D point;
    /** The object that will be drawn. */
    @Nullable protected String text;

    /** The associated style. */
    @Nullable protected TextStyle style = null;

    /** Keeps track of last draw boundary of string. */
    @Nullable private transient Rectangle2D lastBounds = null;

    //
    // CONSTRUCTORS
    //

    /** Construct with no style (will use the default) */
    public BasicTextGraphic(Point2D point, String s) {
        this(point, s, null);
    }

    /**
     * Construct with given primitive and style.
     * @param point location of string
     * @param s string to draw
     * @param style draws the string
     */
    public BasicTextGraphic(Point2D point, String s, TextStyle style) {
        this.point = point;
        this.text = s;
        this.style = style;
        PointBeanDragger dragger = new PointBeanDragger(this);
        addMouseListener(dragger);
        addMouseMotionListener(dragger);
    }

    @Override
    public String toString() {
        return "BasicStringGraphic:"+text;
    }


    //<editor-fold defaultstate="collapsed" desc="PROPERTIES">
    //
    // PROPERTIES
    //

    public Point2D getPoint() {
        return point;
    }

    public void setPoint(Point2D p) {
        if (!point.equals(p)) {
            point = new Point2D.Double(p.getX(), p.getY());
            fireGraphicChanged();
        }
    }

    /**
     * Return the string to be drawn.
     * @return the string
     */
    @Nullable 
    public String getString() {
        return text;
    }

    /**
     * Set the string to be drawn.
     * @param s the string
     */
    public void setString(@Nullable String s) {
        if (text == null ? s != null : !text.equals(s)) {
            text = s;
            fireGraphicChanged();
        }
    }

    /**
     * Return the style used to draw.
     * @return style
     */
    @Nullable 
    public TextStyle getStyle() {
        return style;
    }

    /**
     * Sets the style used to draw.
     * @param style the style
     */
    public void setStyle(@Nullable TextStyle style) {
        if (this.style != style) {
            this.style = style;
            fireGraphicChanged();
        }
    }

    //</editor-fold>


    //
    // GRAPHIC METHODS
    //

    public boolean contains(Point2D p) {
        return lastBounds != null && lastBounds.contains(p);
    }

    public boolean intersects(Rectangle2D box) {
        return lastBounds != null && lastBounds.intersects(box);
    }


    //
    // DRAW METHODS
    //

    /** Return the actual style used for drawing */
    @Nonnull 
    protected TextStyle drawStyle() {
        return style == null ? parent.getStyleContext().getStringStyle(this) : style;
    }

    public void draw(Graphics2D canvas) {
        if (Strings.isNullOrEmpty(text)) {
            return;
        }
        TextStyle sty = drawStyle();
        sty.draw(point, text, canvas, visibility);
        lastBounds = sty.bounds(point, text, canvas);
    }
}
