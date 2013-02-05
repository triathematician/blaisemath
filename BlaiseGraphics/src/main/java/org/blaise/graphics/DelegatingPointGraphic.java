/**
 * DelegatingPointGraphic.java
 * Created Aug 21, 2012
 */
package org.blaise.graphics;

import com.google.common.base.Function;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;
import org.blaise.style.ObjectStyler;
import org.blaise.style.PointStyle;
import org.blaise.style.StringStyle;

/**
 * Uses an {@link ObjectStyler} and a source object to draw a labeled point on a canvas.
 *
 * @author Elisha
 */
public class DelegatingPointGraphic<Src> extends AbstractPointGraphic {

    /** Source object */
    protected Src src;
    /** Manages delegators */
    protected ObjectStyler<Src, PointStyle> styler = new ObjectStyler<Src, PointStyle>();

    public DelegatingPointGraphic() {
        this(null, new Point2D.Double());
    }

    public DelegatingPointGraphic(Src src, Point2D pt) {
        super(pt);
        setSourceObject(src);
    }

    public Src getSourceObject() {
        return src;
    }

    public void setSourceObject(Src src) {
        this.src = src;
        setDefaultTooltip(styler.getTipDelegate().apply(src));
        fireGraphicChanged();
    }

    public ObjectStyler<Src, PointStyle> getStyler() {
        return styler;
    }

    public void setStyler(ObjectStyler<Src, PointStyle> styler) {
        this.styler = styler;
        fireGraphicChanged();
    }

    @Override
    public String getTooltip(Point p) {
        if (tipEnabled) {
            String txt = styler.getTipDelegate().apply(src);
            return txt == null ? tipText : txt;
        } else {
            return null;
        }
    }

    @Override
    public PointStyle drawStyle() {
        PointStyle style = null;
        if (styler != null && styler.getStyleDelegate() != null) {
            style = styler.getStyleDelegate().apply(src);
        }
        if (style == null) {
            style = parent.getStyleContext().getPointStyle(this);
        }
        return style;
    }

    public void draw(Graphics2D canvas) {
        PointStyle ps = drawStyle();
        ps.draw(point, canvas, visibility);

        if (styler.getLabelDelegate() != null) {
            String label = styler.getLabelDelegate().apply(src);
            if (label != null && label.length() > 0) {
                Function<? super Src,StringStyle> lStyler = styler.getLabelStyleDelegate();
                if (lStyler != null) {
                    lStyler.apply(src).draw(point, label, canvas, visibility);
                }
            }
        }
    }


}
