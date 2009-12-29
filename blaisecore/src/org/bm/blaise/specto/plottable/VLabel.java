/**
 * VPoint.java
 * Created on Jul 30, 2009
 */

package org.bm.blaise.specto.plottable;

import java.awt.Point;
import java.awt.geom.Point2D.Double;
import org.bm.blaise.specto.primitive.StringStyle;
import org.bm.blaise.specto.visometry.VisometryGraphics;

/**
 * <p>
 *   <code>VPoint</code> is a label based at a particular point in the visometry.
 * </p>
 *
 * @author Elisha Peterson
 */
public class VLabel<C> extends VInvisiblePoint<C> {

    /** String to display. */
    String text = "TEST";

    /** Offset in window pixels from point. */
    Point offset = new Point();

    /** Style for the coordinate label. */
    StringStyle labelStyle = new StringStyle();

    //
    //
    // CONSTRUCTORS
    //
    //

    /** Cosntructs label at specified basepoint with specified text. */
    public VLabel(C value, String string) {
        super(value);
        this.text = string;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
    
    public StringStyle getLabelStyle() {
        return labelStyle;
    }

    public void setLabelStyle(StringStyle labelStyle) {
        this.labelStyle = labelStyle;
    }

    public Point getOffset() {
        return offset;
    }

    public void setOffset(Point offset) {
        this.offset = offset;
    }

    @Override
    public String toString() {
        return "VLabel \""+getText()+"\" ["+value.toString()+"]";
    }

    
    //
    //
    // PAINTING
    //
    //

    @Override
    public void paintComponent(VisometryGraphics<C> vg) {
        vg.setStringStyle(labelStyle);
        vg.drawString(text, value, offset.x, offset.y);
    }

}
