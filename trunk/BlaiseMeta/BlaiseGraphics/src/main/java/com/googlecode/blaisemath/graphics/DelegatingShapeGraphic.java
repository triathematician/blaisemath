/**
 * DelegatingShapeGraphic.java
 * Created Aug 28, 2012
 */

package com.googlecode.blaisemath.graphics;

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


import static com.google.common.base.Preconditions.checkNotNull;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.JPopupMenu;
import com.googlecode.blaisemath.style.ObjectStyler;
import com.googlecode.blaisemath.style.ShapeStyle;

/**
 * Delegates style and some other properties of a {@link Graphic} to an {@link ObjectStyler}.
 * 
 * @param <S> type of source object
 * 
 * @author elisha
 */
public class DelegatingShapeGraphic<S> extends AbstractShapeGraphic {

    /** Source object */
    @Nullable 
    protected S source;
    /** Styler managing delgators */
    @Nonnull 
    protected ObjectStyler<S,? extends ShapeStyle> styler = ObjectStyler.create();

    /** Initialize without arguments */
    public DelegatingShapeGraphic() {
        this(null, null, false);
    }

    /**
     * Initialize with source (source object) and graphic
     * @param obj the source object
     * @param shape the shape to use
     * @param strokeOnly whether object should be stroked only
     */
    public DelegatingShapeGraphic(S obj, Shape shape, boolean strokeOnly) {
        super(shape, strokeOnly);
        setSourceObject(obj);
    }

    /**
     * Initialize with source (source object) and graphic
     * @param obj the source object
     * @param shape the shape to use
     * @param styler object styler
     */
    public DelegatingShapeGraphic(S obj, Shape shape, ObjectStyler<S,? extends ShapeStyle> styler) {
        super(shape, false);
        setSourceObject(obj);
        setStyler(styler);
    }
    
    
    /**
     * Hook method for updating the shape attributes after the source graphic or style has changed.
     * This version of the method updates the tooltip and notifies listeners that the
     * graphic has changed.
     */
    protected void sourceGraphicUpdated() {
        setDefaultTooltip(styler.getTipDelegate() == null ? null : styler.getTipDelegate().apply(source));
        fireGraphicChanged();
    }
    
    /**
     * Update the context menu initializer to use the source object for the focus, rather than the graphic.
     * @param menu
     * @param src
     * @param point
     * @param focus
     * @param selection 
     */
    @Override
    public void initContextMenu(JPopupMenu menu, Graphic src, Point2D point, Object focus, Set selection) {
        super.initContextMenu(menu, src, point, getSourceObject(), selection);
    }
    
    
    //<editor-fold defaultstate="collapsed" desc="PROPERTY PATTERNS">
    //
    // PROPERTY PATTERNS
    //

    /**
     * Get the source object being represented by this shape.
     * @return source object
     */
    public final S getSourceObject() {
        return source;
    }

    /**
     * Set the source object being represented by this shape.
     * This also updates the tooltip, and notifies listeners that
     * the graphic has changed.
     * @param src the new source object
     */
    public final void setSourceObject(S src) {
        if (this.source != src) {
            this.source = src;
            sourceGraphicUpdated();
        }
    }

    /**
     * Get the style object used for the graphic's style.
     * @return style object
     */
    public final ObjectStyler<S,? extends ShapeStyle> getStyler() {
        return styler;
    }

    /**
     * Set the style object used for the graphic's style.
     * @param styler a new style object
     */
    public final void setStyler(ObjectStyler<S,? extends ShapeStyle> styler) {
        if (this.styler != checkNotNull(styler)) {
            this.styler = styler;
            sourceGraphicUpdated();
        }
    }
    
    //</editor-fold>

    
    @Override
    @Nonnull
    protected ShapeStyle drawStyle() {
        ShapeStyle style = styler.getStyleDelegate() == null ? null 
                : styler.getStyleDelegate().apply(source);
        if (style != null) {
            return style;
        }
        return isStrokeOnly() 
                ? parent.getStyleContext().getPathStyle(this, styleHints)
                : parent.getStyleContext().getShapeStyle(this, styleHints);
    }

}
