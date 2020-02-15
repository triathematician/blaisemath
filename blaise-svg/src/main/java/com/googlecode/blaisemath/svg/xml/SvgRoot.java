package com.googlecode.blaisemath.svg.xml;

/*
 * #%L
 * BlaiseSVG
 * --
 * Copyright (C) 2014 - 2020 Elisha Peterson
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
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.googlecode.blaisemath.svg.internal.SvgUtils.parseLength;
import static java.util.stream.Collectors.toList;

/**
 * Root element for SVG object tree. Stores width/height parameters as well as a viewbox to define the coordinate system
 * for SVG content.
 * @author Elisha Peterson
 */
@XmlRootElement(name="svg", namespace="http://www.w3.org/2000/svg")
public final class SvgRoot extends SvgGroup {

    private static final Logger LOG = Logger.getLogger(SvgRoot.class.getName());
    
    private @Nullable Rectangle2D viewBox = null;
    private int height = 150;
    private int width = 300;
    private String preserveAspectRatio = null;

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
            List<Double> values = Splitter.onPattern("\\s+").splitToList(viewBox).stream()
                    .map(s -> s.contains(".") ? Double.valueOf(s) : Integer.valueOf(s))
                    .collect(toList());
            this.viewBox = new Rectangle2D.Double(values.get(0), values.get(1), values.get(2), values.get(3));
        } catch (NumberFormatException | IndexOutOfBoundsException x) {
            LOG.log(Level.WARNING, "Invalid view box: " + viewBox, x);
        }
    }

    @XmlTransient
    public @NonNull Rectangle2D getViewBoxAsRectangle() {
        return viewBox != null ? viewBox : new Rectangle2D.Double(0, 0, width, height);
    }

    public void setViewBoxAsRectangle(@Nullable Rectangle2D viewBox) {
        this.viewBox = viewBox;
    }

    @XmlTransient
    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @XmlAttribute(name = "height")
    private String getHeightString() {
        return height+"";
    }
    
    private void setHeightString(String ht) {
        setHeight(parseLength(ht).map(Double::intValue).orElse(150));
    }

    @XmlTransient
    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    @XmlAttribute(name = "width")
    private String getWidthString() {
        return width+"";
    }
    
    private void setWidthString(String ht) {
        setWidth(parseLength(ht).map(Double::intValue).orElse(300));
    }

    public @Nullable String getPreserveAspectRatio() {
        return preserveAspectRatio;
    }

    public void setPreserveAspectRatio(@Nullable String preserveAspectRatio) {
        this.preserveAspectRatio = preserveAspectRatio;
    }

    //endregion

}
