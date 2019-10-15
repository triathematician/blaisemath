package com.googlecode.blaisemath.svg;

import com.googlecode.blaisemath.graphics.swing.JGraphicComponent;
import com.googlecode.blaisemath.svg.reader.StyleReader;
import com.googlecode.blaisemath.svg.xml.SvgElement;
import com.googlecode.blaisemath.svg.xml.SvgRoot;
import com.googlecode.blaisemath.util.Colors;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.Optional;

/**
 * A component that displays SVG content.
 * @author Elisha Peterson
 */
public class SvgIcon implements Icon {

    private SvgElement element;
    private Integer width = null;
    private Integer height = null;

    /**
     * Construct icon from SVG element.
     * @param el base element
     * @return icon
     */
    public static SvgIcon create(SvgElement el) {
        SvgIcon res = new SvgIcon();
        res.setElement(el);
        return res;
    }

    /**
     * Construct icon from SVG string.
     * @param svg SVG string
     * @return icon
     * @throws IOException if there's a problem parsing the SVG string
     */
    public static SvgIcon create(String svg) throws IOException {
        SvgIcon res = new SvgIcon();
        res.setElement(SvgRoot.load(svg));
        return res;
    }

    //region PROPERTIES

    public SvgElement getElement() {
        return element;
    }

    public void setElement(SvgElement element) {
        this.element = element;
        if (element instanceof SvgRoot) {
            SvgRoot r = (SvgRoot) element;
            width = r.getWidth();
            height = r.getHeight();
        }
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    //endregion

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        if (element == null) {
            return;
        }
        SvgUtils.backgroundColor(element).ifPresent(bg -> {
            g.setColor(bg);
            g.fillRect(x, y, getIconWidth(), getIconHeight());
        });

        JGraphicComponent jg = new JGraphicComponent();
        jg.addGraphic(SvgGraphic.create(element));
        if (element instanceof SvgRoot && ((SvgRoot) element).getViewBoxAsRectangle() != null) {
            Rectangle2D r = ((SvgRoot) element).getViewBoxAsRectangle();
            g.translate((int) -r.getX(), (int) -r.getY());
            jg.renderTo((Graphics2D) g);
            g.translate((int) r.getX(), (int) r.getY());
        }
    }

    @Override
    public int getIconWidth() {
        if (width != null) {
            return width;
        } else if (element instanceof SvgRoot) {
            SvgRoot r = (SvgRoot) element;
            Rectangle2D vb = r.getViewBoxAsRectangle();
            if (vb != null) {
                return (int) vb.getWidth();
            }
        }
        return 0;
    }

    @Override
    public int getIconHeight() {
        if (height != null) {
            return height;
        } else if (element instanceof SvgRoot) {
            SvgRoot r = (SvgRoot) element;
            Rectangle2D vb = r.getViewBoxAsRectangle();
            if (vb != null) {
                return (int) vb.getHeight();
            }
        }
        return 0;
    }

}
