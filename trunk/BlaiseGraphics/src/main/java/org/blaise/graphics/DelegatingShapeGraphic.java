/**
 * DelegatingShapeGraphic.java
 * Created Aug 28, 2012
 */

package org.blaise.graphics;

import static com.google.common.base.Preconditions.*;
import java.awt.Shape;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
    @Nullable protected S source;
    /** Styler managing delgators */
    @Nonnull protected ObjectStyler<S,? extends ShapeStyle> styler = ObjectStyler.create();

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
    
    
    //<editor-fold defaultstate="collapsed" desc="PROPERTY PATTERNS">
    //
    // PROPERTY PATTERNS
    //

    public S getSourceObject() {
        return source;
    }

    public void setSourceObject(S edge) {
        if (this.source != edge) {
            this.source = edge;
            setDefaultTooltip(styler.getTipDelegate() == null ? null : styler.getTipDelegate().apply(edge));
            fireGraphicChanged();
        }
    }

    public ObjectStyler<S,? extends ShapeStyle> getStyler() {
        return styler;
    }

    public final void setStyler(ObjectStyler<S,? extends ShapeStyle> styler) {
        if (this.styler != checkNotNull(styler)) {
            this.styler = styler;
            setDefaultTooltip(styler.getTipDelegate() == null ? null : styler.getTipDelegate().apply(source));
            fireGraphicChanged();
        }
    }
    
    //</editor-fold>

    
    @Override
    @Nonnull
    protected ShapeStyle drawStyle() {
        ShapeStyle style = styler.getStyleDelegate() == null ? null : styler.getStyleDelegate().apply(source);
        if (style != null) {
            return style;
        }
        return isStrokeOnly() ? parent.getStyleContext().getPathStyle(this)
                : parent.getStyleContext().getShapeStyle(this);
    }

}
