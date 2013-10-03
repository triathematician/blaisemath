/*
 * LabeledShapeGraphic.java
 * Created on Oct 15, 2012
 */

package org.blaise.graphics;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.RectangularShape;
import java.util.Set;
import javax.swing.JPopupMenu;
import org.blaise.style.BasicStringStyle;
import org.blaise.style.ObjectStyler;
import org.blaise.style.ShapeStyle;
import org.blaise.style.VisibilityHint;
import org.blaise.style.WrappingStringStyle;

/**
 * Customizable graphic that represents a labeled item.
 * Uses an {@link ObjectStyler} to customize appearance.
 *
 * @param <E> type of item
 *
 * @author petereb1
 */
public class LabeledShapeGraphic<E> extends DelegatingShapeGraphic<E> {

    /** Draws label */
    private final BasicStringGraphic labelGraphic = new BasicStringGraphic(new Point2D.Double(), "");
    /** Special style for label */
    private WrappingStringStyle labelStyle;
    /** Whether variable inset on "highlight" visibility tag is active */
    private boolean variableInset = true;

    /**
     * Initialize with source object.
     * @param src source object
     * @param rect bounding rectangle for graphic
     * @param styler styler for source object
     */
    public LabeledShapeGraphic(E src, RectangularShape rect, ObjectStyler<E,ShapeStyle> styler) {
        setStyler(styler);
        setSourceObject(src);
        setPrimitive(rect);
    }

    //<editor-fold defaultstate="collapsed" desc="PROPERTY PATTERNS">
    //
    // PROPERTY PATTERNS
    //
    

    @Override
    public void setSourceObject(E src) {
        super.setSourceObject(src);
        if (labelGraphic != null) {
            labelGraphic.setString(src == null ? "" : getStyler() == null || getStyler().getLabelDelegate() == null ? ""+src
                    : getStyler().getLabelDelegate().apply(src));
            BasicStringStyle ss = (BasicStringStyle) (getStyler() == null || getStyler().getLabelStyleDelegate() == null ? null
                                        : getStyler().getLabelStyleDelegate().apply(src));
            labelStyle = (WrappingStringStyle) (ss == null ? null
                    : new WrappingStringStyle()
                        .anchor(ss.getAnchor())
                        .color(ss.getColor())
                        .font(ss.getFont())
                        .fontSize(ss.getFontSize())
                        .offset(ss.getOffset()));
            labelGraphic.setStyle(labelStyle);
        }
    }
    
    public boolean isVariableInset() {
        return variableInset;
    }
    
    public void setVariableInset(boolean variableInset) {
        if (this.variableInset != variableInset) {
            this.variableInset = variableInset;
            fireGraphicChanged();
        }
    }

    //</editor-fold>
    
    @Override
    public void initialize(JPopupMenu menu, Point point, Object focus, Set<Graphic> selection) {
        super.initialize(menu, point, getSourceObject(), selection);
    }

    @Override
    public ShapeStyle drawStyle() {
        final ShapeStyle parent = super.drawStyle();
        return new ShapeStyle() {
            public void draw(Shape primitive, Graphics2D canvas, Set<VisibilityHint> hints) {
                boolean highlight = hints.contains(VisibilityHint.Highlight);
                if (primitive instanceof RectangularShape) {
                    RectangularShape rs = (RectangularShape) primitive;
                    int inset = !variableInset?0:highlight?1:3;
                    RectangularShape rs2 = (RectangularShape) rs.clone();
                    rs2.setFrameFromCenter(rs.getCenterX(), rs.getCenterY(), rs.getMaxX()-inset, rs.getMaxY()-inset);
                    primitive = rs2;
                }
                parent.draw(primitive, canvas, hints);
                // must set the clip for the label style to draw properly
                if (labelStyle != null) {
                    labelStyle.setClip(primitive instanceof RectangularShape ? (RectangularShape) primitive : primitive.getBounds2D());
                    labelGraphic.setPoint(new Point2D.Double(primitive.getBounds2D().getMinX()+2, primitive.getBounds2D().getMinY()+2));
                    labelGraphic.draw(canvas);
                }
            }
        };
    }

}
