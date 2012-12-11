/**
 * DelegatingShapeGraphic.java
 * Created Aug 28, 2012
 */

package org.blaise.graphics;

import java.awt.Shape;
import org.blaise.style.ObjectStyler;
import org.blaise.style.ShapeStyle;

/**
 * <p>
 *  Delegates style and some other properties of a {@link Graphic} to an {@link ObjectStyler}.
 * </p>
 * @param <Src> type of source object
 * @author elisha
 */
public class DelegatingShapeGraphic<Src> extends AbstractShapeGraphic {

    /** Source object */
    protected Src source;
    /** Styler managing delgators */
    protected ObjectStyler<Src,? extends ShapeStyle> styler = new ObjectStyler<Src,ShapeStyle>();

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
    public DelegatingShapeGraphic(Src obj, Shape shape, boolean strokeOnly) {
        super(shape, strokeOnly);
        setSourceObject(obj);
    }

    /**
     * Initialize with source (source object) and graphic
     * @param obj the source object
     * @param shape the shape to use
     * @param strokeOnly whether object should be stroked only
     */
    public DelegatingShapeGraphic(Src obj, Shape shape, ObjectStyler<Src,? extends ShapeStyle> styler) {
        super(shape, false);
        setSourceObject(obj);
        setStyler(styler);
    }

    public Src getSourceObject() {
        return source;
    }

    public void setSourceObject(Src edge) {
        this.source = edge;
        setDefaultTooltip(styler.getTipDelegate() == null ? null : styler.getTipDelegate().of(edge));
        fireGraphicChanged();
    }

    public ObjectStyler<Src,? extends ShapeStyle> getStyler() {
        return styler;
    }

    public void setStyler(ObjectStyler<Src,? extends ShapeStyle> styler) {
        this.styler = styler;
        setDefaultTooltip(styler.getTipDelegate() == null ? null : styler.getTipDelegate().of(source));
        fireGraphicChanged();
    }

    @Override
    public ShapeStyle drawStyle() {
        ShapeStyle style = styler.getStyleDelegate() == null ? null : styler.getStyleDelegate().of(source);
        if (style != null) {
            return style;
        }
        return isStrokeOnly() ? parent.getStyleProvider().getPathStyle(this)
                : parent.getStyleProvider().getShapeStyle(this);
    }

}
