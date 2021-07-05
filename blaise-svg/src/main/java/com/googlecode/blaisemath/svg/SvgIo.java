package com.googlecode.blaisemath.svg;

/*
 * #%L
 * blaise-svg
 * --
 * Copyright (C) 2014 - 2021 Elisha Peterson
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


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.UnmarshallerHandler;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLFilter;

/**
 * Utilities for reading/writing Svg.
 * @author elisha
 */
class SvgIo {
    
    private static JAXBContext CONTEXT;
    
    static JAXBContext context() throws JAXBException {
        if (CONTEXT == null) {
            CONTEXT = JAXBContext.newInstance(SvgRoot.class);
        }
        return CONTEXT;
    }
    
    static Unmarshaller unmarshaller() throws JAXBException {
        return context().createUnmarshaller();
    }
    
    static SvgRoot flexibleNamespaceParse(InputSource input) throws IOException {
        try {
            XMLFilter filter = new SvgNamespaceFilter();
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setNamespaceAware(true);
            filter.setParent(factory.newSAXParser().getXMLReader());
            UnmarshallerHandler umHandler = unmarshaller().getUnmarshallerHandler();
            filter.setContentHandler(umHandler);
            filter.parse(input);
            return (SvgRoot) umHandler.getResult();
        } catch (ParserConfigurationException | SAXException | JAXBException x) {
            throw new IOException("Invalid svg or other error: "+input, x);
        }
    }
    
    static Marshaller marshaller() throws JAXBException {
        Marshaller m = context().createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        return m;
    }
    
    static SvgRoot read(String input) throws IOException {
        return read(new StringReader(input));
    }
    
    static SvgRoot read(InputStream input) throws IOException {
        return flexibleNamespaceParse(new InputSource(input));
    }
    
    static SvgRoot read(Reader reader) throws IOException {
        return flexibleNamespaceParse(new InputSource(reader));
    }
    
    static String writeToString(SvgRoot root) throws IOException {
        StringWriter sw = new StringWriter();
        write(root, sw);
        return sw.toString();
    }
    
    static void write(SvgRoot root, OutputStream output) throws IOException {
        try {
            marshaller().marshal(root, output);
        } catch (JAXBException ex) {
            throw new IOException("Could not save SvgRoot to output", ex);
        }
    }
    
    static void write(SvgRoot root, Writer writer) throws IOException {
        try {
            marshaller().marshal(root, writer);
        } catch (JAXBException ex) {
            throw new IOException("Could not save SvgRoot to output", ex);
        }
    }
    
}
