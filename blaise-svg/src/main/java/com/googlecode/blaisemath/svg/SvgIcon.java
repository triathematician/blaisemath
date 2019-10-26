package com.googlecode.blaisemath.svg;

/*-
 * #%L
 * blaise-svg
 * --
 * Copyright (C) 2014 - 2019 Elisha Peterson
 * --
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
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

import com.googlecode.blaisemath.svg.xml.SvgElement;
import com.googlecode.blaisemath.svg.xml.SvgIo;
import com.googlecode.blaisemath.svg.xml.SvgRoot;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * A component that renders SVG content within an {@link Icon}. Supports setting arbitrary width/height. The SVG content
 * will resize to fit.
 * @author Elisha Peterson
 */
public class SvgIcon implements Icon {

    private SvgElement element;
    private int iconWidth = 10;
    private int iconHeight = 10;

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
        res.setElement(SvgIo.read(svg));
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
            if (r.getWidth() != null) {
                iconWidth = r.getWidth();
            }
            if (r.getHeight() != null) {
                iconHeight = r.getHeight();
            }
        }
    }

    @Override
    public int getIconWidth() {
        return iconWidth;
    }

    public void setIconWidth(int iconWidth) {
        this.iconWidth = iconWidth;
    }

    @Override
    public int getIconHeight() {
        return iconHeight;
    }

    public void setIconHeight(int iconHeight) {
        this.iconHeight = iconHeight;
    }

    //endregion

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        if (element == null) {
            return;
        }
        Graphics2D canvas = (Graphics2D) g;
        SvgUtils.backgroundColor(element).ifPresent(bg -> {
            canvas.setColor(bg);
            canvas.fillRect(x, y, iconWidth, iconHeight);
        });

        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        SvgElementGraphic seg = SvgElementGraphic.create(element);
        seg.setRenderBackground(false);
        seg.setCanvasBounds(new Rectangle(x, y, iconWidth, iconHeight));
        seg.renderTo((Graphics2D) g);
    }

}
