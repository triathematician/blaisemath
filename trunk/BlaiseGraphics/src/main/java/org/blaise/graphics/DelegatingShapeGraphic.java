/**
 * DelegatingShapeGraphic.java
 * Created Aug 28, 2012
 */

package org.blaise.graphics;

import static com.google.common.base.Preconditions.checkNotNull;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.JPopupMenu;
import org.blaise.style.ObjectStyler;
import org.blaise.style.ShapeStyle;

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
        return isStrokeOnly() ? parent.getStyleContext().getPathStyle(this)
                : parent.getStyleContext().getShapeStyle(this);
    }

}
