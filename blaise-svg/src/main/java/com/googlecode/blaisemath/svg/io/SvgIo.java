package com.googlecode.blaisemath.svg.io;

/*
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


import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.googlecode.blaisemath.svg.SvgRoot;

import java.io.*;

/**
 * Utilities for reading/writing SVG.
 * @author Elisha Peterson
 */
class SvgIo {
    
//    static SvgRoot flexibleNamespaceParse(InputSource input) throws IOException {
//        try {
//            XMLFilter filter = new SvgNamespaceFilter();
//            SAXParserFactory factory = SAXParserFactory.newInstance();
//            factory.setNamespaceAware(true);
//            filter.setParent(factory.newSAXParser().getXMLReader());
//            UnmarshallerHandler umHandler = unmarshaller().getUnmarshallerHandler();
//            filter.setContentHandler(umHandler);
//            filter.parse(input);
//            return (SvgRoot) umHandler.getResult();
//        } catch (ParserConfigurationException | SAXException | JAXBException x) {
//            throw new IOException("Invalid svg or other error: "+input, x);
//        }
//    }
    
    static SvgRoot read(String input) throws IOException {
        return read(new StringReader(input));
    }
    
    static SvgRoot read(InputStream input) throws IOException {
        XmlMapper mapper = new XmlMapper();
        return mapper.readValue(input, SvgRoot.class);
    }
    
    static SvgRoot read(Reader reader) throws IOException {
        XmlMapper mapper = new XmlMapper();
        return mapper.readValue(reader, SvgRoot.class);
    }
    
    static String writeToString(SvgRoot root) throws IOException {
        XmlMapper mapper = new XmlMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        return mapper.writeValueAsString(root);
    }
    
    static void write(SvgRoot root, OutputStream output) throws IOException {
        XmlMapper mapper = new XmlMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.writeValue(output, root);
    }
    
    static void write(SvgRoot root, Writer writer) throws IOException {
        XmlMapper mapper = new XmlMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.writeValue(writer, root);
    }
    
}
