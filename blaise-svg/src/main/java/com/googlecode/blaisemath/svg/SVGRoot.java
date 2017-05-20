package com.googlecode.blaisemath.svg;

/*
 * #%L
 * BlaiseSVG
 * --
 * Copyright (C) 2014 - 2017 Elisha Peterson
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
import com.googlecode.blaisemath.style.AttributeSet;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.stream.Collectors.toList;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Root element for SVG object tree.
 * @author petereb1
 */
@XmlRootElement(name="svg")
public final class SVGRoot extends SVGGroup {

    private static final Logger LOG = Logger.getLogger(SVGRoot.class.getName());
    
    private Rectangle2D viewBox = null;
    private int height = 100;
    private int width = 100;

    public SVGRoot() {
        setStyle(AttributeSet.of("font-family", "sans-serif"));
    }
    
    //<editor-fold defaultstate="collapsed" desc="PROPERTIES">

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
    public Rectangle2D getViewBoxAsRectangle() {
        return viewBox;
    }

    public void setViewBoxAsRectangle(Rectangle2D viewBox) {
        this.viewBox = viewBox;
    }

    @XmlAttribute
    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @XmlAttribute
    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }
    
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="STATIC UTILITIES">
    
    /**
     * Attempt to load an SVG root object from the given string.
     * @param input string
     * @return root object, if loaded properly
     * @throws java.io.IOException if input fails
     */
    public static SVGRoot load(String input) throws IOException {
        return SvgIo.read(input);
    }
    
    /**
     * Attempt to load an SVG root object from the given source.
     * @param input source
     * @return root object, if loaded properly
     * @throws java.io.IOException if input fails
     */
    public static SVGRoot load(InputStream input) throws IOException {
        return SvgIo.read(input);
    }

    /**
     * Attempt to load an SVG root object from the given source.
     * @param reader source
     * @return root object, if loaded properly
     * @throws java.io.IOException if input fails
     */
    public static SVGRoot load(Reader reader) throws IOException {
        return SvgIo.read(reader);
    }
    
    /**
     * Attempt to save an SVG root object to the given source.
     * @param root object to save
     * @return SVG string
     * @throws java.io.IOException if save fails
     */
    public static String saveToString(SVGRoot root) throws IOException {
        return SvgIo.writeToString(root);
    }
    
    /**
     * Attempt to save an SVG root object to the given source.
     * @param root object to save
     * @param output where to save it
     * @throws java.io.IOException if save fails
     */
    public static void save(SVGRoot root, OutputStream output) throws IOException {
        SvgIo.write(root, output);
    }
    
    /**
     * Attempt to save an SVG root object to the given source.
     * @param root object to save
     * @param writer where to save it
     * @throws java.io.IOException if save fails
     */
    public static void save(SVGRoot root, Writer writer) throws IOException {
        SvgIo.write(root, writer);
    }
    
    //</editor-fold>
    
}
