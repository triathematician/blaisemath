/*
 * LabeledShapeGraphic.java
 * Created on Oct 15, 2012
 */

package org.blaise.graphics;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.RectangularShape;
import org.blaise.style.ObjectStyler;
import org.blaise.style.ShapeStyle;
import org.blaise.style.TextStyleBasic;
import org.blaise.style.TextStyleMultiline;
import org.blaise.style.VisibilityHint;
import org.blaise.style.VisibilityHintSet;

/**
 * Customizable graphic that represents a labeled item.
 * Uses an {@link ObjectStyler} to customize appearance.
 *
 * @param <E> type of item
 *
 * @author cornidc1
 */
public class TextShapeGraphic<E> extends DelegatingShapeGraphic<E> {

    /** Draws label */
    private final BasicTextGraphic labelGraphic = new BasicTextGraphic(new Point2D.Double(), "");
    /** Special style for Text */
    private TextStyleMultiline labelStyle;

    /**
     * Initialize with source object.
     * @param src source object
     * @param rect bounding rectangle for graphic
     * @param styler styler for source object
     */
    public TextShapeGraphic(E src, RectangularShape rect, ObjectStyler<E,ShapeStyle> styler) {
        super(src, rect, styler);
    }

    @Override
    protected void sourceGraphicUpdated() {
        if (labelGraphic != null) {
            labelGraphic.setString(source == null ? "" 
                    : getStyler() == null || getStyler().getLabelDelegate() == null ? ""+source
                    : getStyler().getLabelDelegate().apply(source));
            TextStyleBasic ss = (TextStyleBasic) (getStyler() == null || getStyler().getLabelStyleDelegate() == null ? null
                                        : getStyler().getLabelStyleDelegate().apply(source));
            labelStyle = (TextStyleMultiline) (ss == null ? null
                    : new TextStyleMultiline()
                        .textAnchor(ss.getTextAnchor())
                        .fill(ss.getFill())
                        .font(ss.getFont())
                        .fontSize(ss.getFontSize())
                        .offset(ss.getOffset()));
            labelGraphic.setStyle(labelStyle);
        }
    }

    @Override
    public ShapeStyle drawStyle() {
        final ShapeStyle parentStyle = super.drawStyle();
        return new ShapeStyle() {
            public void draw(Shape primitive, Graphics2D canvas, VisibilityHintSet hints) {
                boolean highlight = hints.contains(VisibilityHint.HIGHLIGHT);
                if (primitive instanceof RectangularShape) {
                    RectangularShape rs = (RectangularShape) primitive;
                    int inset = highlight?1:3;
                    RectangularShape rs2 = (RectangularShape) rs.clone();
                    rs2.setFrameFromCenter(rs.getCenterX(), rs.getCenterY(), rs.getMaxX()-inset, rs.getMaxY()-inset);
                    primitive = rs2;
                }
                parentStyle.draw(primitive, canvas, hints);
                if (labelStyle != null) {
                    labelGraphic.setPoint(new Point2D.Double(primitive.getBounds2D().getMinX()+2, primitive.getBounds2D().getMaxY()-2));
                    labelGraphic.draw(canvas);
                }
            }
        };
    }
}
