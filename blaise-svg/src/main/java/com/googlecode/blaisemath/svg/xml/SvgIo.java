package com.googlecode.blaisemath.svg.xml;

/*
 * #%L
 * blaise-svg
 * --
 * Copyright (C) 2014 - 2022 Elisha Peterson
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

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLFilter;

import javax.xml.bind.*;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;

/**
 * Utilities for reading/writing SVG.
 * @author Elisha Peterson
 */
public class SvgIo {

    private static JAXBContext CONTEXT;
    private static Unmarshaller UNMARSHALLER;
    private static Marshaller MARSHALLER;
    private static XMLFilter PARSE_FILTER;
    private static UnmarshallerHandler UNMARSHALLER_HANDLER;

    /**
     * Attempt to load an SVG root object from the given string.
     * @param input string
     * @return root object, if loaded properly
     * @throws java.io.IOException if input fails
     */
    public static SvgRoot read(String input) throws IOException {
        return flexibleNamespaceParse(new InputSource(new StringReader(input)));
    }

    /**
     * Attempt to load an SVG root object from the given source.
     * @param input source
     * @return root object, if loaded properly
     * @throws java.io.IOException if input fails
     */
    public static SvgRoot read(InputStream input) throws IOException {
        return flexibleNamespaceParse(new InputSource(input));
    }

    /**
     * Attempt to load an SVG root object from the given source.
     * @param reader source
     * @return root object, if loaded properly
     * @throws java.io.IOException if input fails
     */
    public static SvgRoot read(Reader reader) throws IOException {
        return flexibleNamespaceParse(new InputSource(reader));
    }

    /**
     * Attempt to write an SVG root object as a string.
     * @param root object to save
     * @return SVG string
     * @throws java.io.IOException if save fails
     */
    public static String writeToString(SvgRoot root) throws IOException {
        StringWriter sw = new StringWriter();
        write(root, sw);
        return sw.toString();
    }

    /**
     * Attempt to save an SVG element to the given source, wrapping in a root SVG if necessary.
     * @param el object to save
     * @return SVG string
     * @throws java.io.IOException if save fails
     */
    public static String writeToString(SvgElement el) throws IOException {
        if (el instanceof SvgRoot) {
            return writeToString((SvgRoot) el);
        } else {
            SvgRoot root = new SvgRoot();
            root.elements.add(el);
            return writeToString(root);
        }
    }

    /**
     * Attempt to save an SVG root object to the given source.
     * @param root object to save
     * @param output where to save it
     * @throws java.io.IOException if save fails
     */
    public static void write(SvgRoot root, OutputStream output) throws IOException {
        try {
            marshaller().marshal(root, output);
        } catch (JAXBException ex) {
            throw new IOException("Could not save SvgRoot to output", ex);
        }
    }

    /**
     * Attempt to save an SVG root object to the given source.
     * @param root object to save
     * @param writer where to save it
     * @throws java.io.IOException if save fails
     */
    public static void write(SvgRoot root, Writer writer) throws IOException {
        try {
            marshaller().marshal(root, writer);
        } catch (JAXBException ex) {
            throw new IOException("Could not save SvgRoot to output", ex);
        }
    }

    private static String writeToString(Object element) throws IOException {
        try {
            StringWriter sw = new StringWriter();
            marshaller().marshal(element, sw);
            return sw.toString();
        } catch (JAXBException ex) {
            throw new IOException("Could not save SvgRoot to output", ex);
        }
    }

    static String writeToCompactString(Object element) throws IOException {
        try {
            StringWriter sw = new StringWriter();
            Marshaller m = context().createMarshaller();
            m.marshal(element, sw);
            return sw.toString();
        } catch (JAXBException ex) {
            throw new IOException("Could not save SvgRoot to output", ex);
        }
    }

    //region HELPERS

    static SvgRoot flexibleNamespaceParse(InputSource input) throws IOException {
        if (PARSE_FILTER == null || UNMARSHALLER == null) {
            try {
                PARSE_FILTER = new SvgNamespaceFilter();
                SAXParserFactory factory = SAXParserFactory.newInstance();
                factory.setNamespaceAware(true);
                PARSE_FILTER.setParent(factory.newSAXParser().getXMLReader());
                UNMARSHALLER_HANDLER = unmarshaller().getUnmarshallerHandler();
                PARSE_FILTER.setContentHandler(UNMARSHALLER_HANDLER);
            } catch (ParserConfigurationException | SAXException | JAXBException x) {
                throw new IOException("Invalid svg or other error: " + input, x);
            }
        }
        try {
            PARSE_FILTER.parse(input);
            return (SvgRoot) UNMARSHALLER_HANDLER.getResult();
        } catch (SAXException | JAXBException x) {
            throw new IOException("Invalid svg or other error: "+input, x);
        }
    }

    static JAXBContext context() throws JAXBException {
        if (CONTEXT == null) {
            CONTEXT = JAXBContext.newInstance(SvgRoot.class);
        }
        return CONTEXT;
    }

    static Unmarshaller unmarshaller() throws JAXBException {
        if (UNMARSHALLER == null) {
            UNMARSHALLER = context().createUnmarshaller();
        }
        return UNMARSHALLER;
    }

    static Marshaller marshaller() throws JAXBException {
        if (MARSHALLER == null) {
            MARSHALLER = context().createMarshaller();
            MARSHALLER.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        }
        return MARSHALLER;
    }

    //endregion
}
