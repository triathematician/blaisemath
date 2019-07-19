package com.googlecode.blaisemath.svg;

/*
 * #%L
 * BlaiseSVG
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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.googlecode.blaisemath.style.AttributeSet;
import com.googlecode.blaisemath.svg.io.SvgIo;

import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.googlecode.blaisemath.svg.SvgUtils.parseLength;
import static java.util.stream.Collectors.toList;

/**
 * Root element for SVG object tree.
 * @author Elisha Peterson
 */
public final class SvgRoot extends SvgGroup {

    private static final Logger LOG = Logger.getLogger(SvgRoot.class.getName());
    
    private Rectangle2D viewBox = null;
    private double height = 100;
    private double width = 100;

    public SvgRoot() {
        setStyle(AttributeSet.of("font-family", "sans-serif"));
    }

    //region PROPERTIES

    public String getViewBox() {
        return viewBox == null ? null : String.format("%d %d %d %d", (int) viewBox.getMinX(), (int) viewBox.getMinY(),
                (int) viewBox.getWidth(), (int) viewBox.getHeight());
    }

    public void setViewBox(String viewBox) {
        if (Strings.isNullOrEmpty(viewBox)) {
            return;
        }
        try {
            List<Double> vals = Splitter.onPattern("\\s+").splitToList(viewBox).stream()
                    .map(s -> s.contains(".") ? Double.valueOf(s) : Integer.valueOf(s))
                    .collect(toList());
            this.viewBox = new Rectangle2D.Double(vals.get(0), vals.get(1), vals.get(2), vals.get(3));
        } catch (NumberFormatException | IndexOutOfBoundsException x) {
            LOG.log(Level.WARNING, "Invalid view box: " + viewBox, x);
        }
    }

    public Rectangle2D getViewBoxAsRectangle() {
        return viewBox;
    }

    public void setViewBoxAsRectangle(Rectangle2D viewBox) {
        this.viewBox = viewBox;
    }

    @JsonIgnore
    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    @JacksonXmlProperty(isAttribute = true, localName = "height")
    private String getHeightString() {
        return height+"";
    }

    private void setHeightString(String ht) {
        setHeight(parseLength(ht));
    }

    @JsonIgnore
    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    @JacksonXmlProperty(isAttribute = true, localName = "width")
    private String getWidthString() {
        return width+"";
    }

    private void setWidthString(String wid) {
        setWidth(parseLength(wid));
    }

    //endregion
    
}
