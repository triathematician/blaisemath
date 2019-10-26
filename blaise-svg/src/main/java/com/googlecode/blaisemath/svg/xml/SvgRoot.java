package com.googlecode.blaisemath.svg.xml;

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

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import org.checkerframework.checker.nullness.qual.Nullable;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.googlecode.blaisemath.svg.SvgUtils.parseLength;
import static java.util.stream.Collectors.toList;

/**
 * Root element for SVG object tree.
 * @author Elisha Peterson
 */
@XmlRootElement(name="svg", namespace="http://www.w3.org/2000/svg")
public final class SvgRoot extends SvgGroup {

    private static final Logger LOG = Logger.getLogger(SvgRoot.class.getName());
    
    private @Nullable Rectangle2D viewBox = null;
    private Integer height = null;
    private Integer width = null;

    public SvgRoot() {
        style = "font-family:sans-serif";
    }

    /**
     * Create group with initial list of elements
     * @param elements elements to add
     * @return group
     */
    public static SvgRoot create(SvgElement... elements) {
        SvgRoot res = new SvgRoot();
        res.elements.addAll(Arrays.asList(elements));
        return res;
    }
    
    //region PROPERTIES

    @XmlAttribute
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

    @XmlTransient
    public @Nullable Rectangle2D getViewBoxAsRectangle() {
        return viewBox;
    }

    public void setViewBoxAsRectangle(@Nullable Rectangle2D viewBox) {
        this.viewBox = viewBox;
    }

    @XmlTransient
    public @Nullable Integer getHeight() {
        return height;
    }

    public void setHeight(@Nullable Integer height) {
        this.height = height;
    }

    @XmlAttribute(name = "height")
    private String getHeightString() {
        return height == null ? null : height.toString();
    }
    
    private void setHeightString(String ht) {
        setHeight(parseLength(ht).map(Double::intValue).orElse(null));
    }

    @XmlTransient
    public @Nullable Integer getWidth() {
        return width;
    }

    public void setWidth(@Nullable Integer width) {
        this.width = width;
    }

    @XmlAttribute(name = "width")
    private String getWidthString() {
        return width == null ? null : width.toString();
    }
    
    private void setWidthString(String ht) {
        setWidth(parseLength(ht).map(Double::intValue).orElse(null));
    }
    
    //endregion

}
