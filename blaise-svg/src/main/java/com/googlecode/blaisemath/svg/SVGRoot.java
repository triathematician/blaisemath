/**
 * SVGRoot.java
 * Created on Sep 26, 2014
 */
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

import com.googlecode.blaisemath.style.AttributeSet;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Root element for SVG object tree.
 * @author petereb1
 */
@XmlRootElement(name="svg")
public final class SVGRoot extends SVGGroup {
    
    private String viewBox = null;
    private int height = 100;
    private int width = 100;

    public SVGRoot() {
        setStyle(AttributeSet.of("font-family", "sans-serif"));
    }
    
    //<editor-fold defaultstate="collapsed" desc="PROPERTIES">

    @XmlAttribute
    public String getViewBox() {
        return viewBox;
    }

    public void setViewBox(String viewBox) {
        this.viewBox = viewBox;
    }

    public void setViewBox(Rectangle2D viewBox) {
        setViewBox(String.format("%d %d %d %d", (int) viewBox.getMinX(), (int) viewBox.getMinY(), 
                (int) viewBox.getWidth(), (int) viewBox.getHeight()));
    }

    @XmlTransient
    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @XmlTransient
    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }
    
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="STATIC UTILITIES">
    
    /**
     * Attempt to load an SVG root object from the given source.
     * @param input source
     * @return root object, if loaded properly
     * @throws java.io.IOException if input fails
     */
    public static SVGRoot load(InputStream input) throws IOException {
        try {
            JAXBContext jc = JAXBContext.newInstance(SVGRoot.class);
            return (SVGRoot) jc.createUnmarshaller().unmarshal(input);
        } catch (JAXBException ex) {
            throw new IOException("Could not load SVGRoot from input", ex);
        }
    }

    /**
     * Attempt to load an SVG root object from the given source.
     * @param reader source
     * @return root object, if loaded properly
     * @throws java.io.IOException if input fails
     */
    public static SVGRoot load(Reader reader) throws IOException {
        try {
            JAXBContext jc = JAXBContext.newInstance(SVGRoot.class);
            return (SVGRoot) jc.createUnmarshaller().unmarshal(reader);
        } catch (JAXBException ex) {
            throw new IOException("Could not load SVGRoot from input", ex);
        }
    }
    
    /**
     * Attempt to save an SVG root object to the given source.
     * @param root object to save
     * @param output where to save it
     * @throws java.io.IOException if save fails
     */
    public static void save(SVGRoot root, OutputStream output) throws IOException {
        try {
            JAXBContext jc = JAXBContext.newInstance(SVGRoot.class);
            Marshaller m = jc.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            m.marshal(root, output);
        } catch (JAXBException ex) {
            throw new IOException("Could not save SVGRoot to output", ex);
        }
    }
    
    /**
     * Attempt to save an SVG root object to the given source.
     * @param root object to save
     * @param writer where to save it
     * @throws java.io.IOException if save fails
     */
    public static void save(SVGRoot root, Writer writer) throws IOException {
        try {
            JAXBContext jc = JAXBContext.newInstance(SVGRoot.class);
            jc.createMarshaller().marshal(root, writer);
        } catch (JAXBException ex) {
            throw new IOException("Could not save SVGRoot to output", ex);
        }
    }
    
    //</editor-fold>
    
}
