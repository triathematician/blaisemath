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

import com.ctc.wstx.api.WstxOutputProperties;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import com.googlecode.blaisemath.style.AttributeSetCoder;
import com.googlecode.blaisemath.svg.SvgElement;
import com.googlecode.blaisemath.svg.SvgRoot;
import com.sun.xml.internal.txw2.output.IndentingXMLStreamWriter;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.*;

/**
 * Utilities for reading/writing SVG.
 * @author Elisha Peterson
 */
public class SvgIo {

    /** Mapper instance */
    private static XmlMapper MAPPER = mapper();

    /** Utility method */
    private SvgIo() {
    }

    private static XmlMapper mapper() {
        if (MAPPER == null) {
            SimpleModule svgModule = new SimpleModule()
                    .addSerializer(new SvgElementSerializer())
                    .addDeserializer(SvgElement.class, new SvgElementDeserializer());
            MAPPER = (XmlMapper) new XmlMapper()
//                    .configure(ToXmlGenerator.Feature.WRITE_XML_DECLARATION, true)
                    .enable(SerializationFeature.INDENT_OUTPUT)
//                    .registerModule(svgModule)
                    ;
//            MAPPER.getFactory().getXMLOutputFactory().setProperty(WstxOutputProperties.P_USE_DOUBLE_QUOTES_IN_XML_DECL, true);
        }
        return MAPPER;
    }


    /**
     * Reads SVG content from input string.
     * @param input input SVG string
     * @return SVG root object
     * @throws IOException if there's a parse failure or other IO failure
     */
    public static SvgRoot read(String input) throws IOException {
        return read(new StringReader(input));
    }

    /**
     * Reads SVG content from input stream.
     * @param input input SVG
     * @return SVG root object
     * @throws IOException if there's a parse failure or other IO failure
     */
    public static SvgRoot read(InputStream input) throws IOException {
        return mapper().readValue(input, SvgRoot.class);
    }

    /**
     * Reads SVG content from input reader.
     * @param reader reader SVG
     * @return SVG root object
     * @throws IOException if there's a parse failure or other IO failure
     */
    public  static SvgRoot read(Reader reader) throws IOException {
        return mapper().readValue(reader, SvgRoot.class);
    }

    /**
     * Write SVG content to string.
     * @param root SVG content
     * @return string
     * @throws IOException if there's a write failure or other IO failure
     */
    public static String writeToString(SvgElement root) throws IOException {
        if (root instanceof SvgRoot) {
            try {
                XmlMapper mapper = mapper();
                StringWriter stringWriter = new StringWriter();
                XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newFactory();
                XMLStreamWriter sw = new IndentingXMLStreamWriter(xmlOutputFactory.createXMLStreamWriter(stringWriter));
                sw.writeStartDocument();
                sw.writeStartElement("", "svg", "http://www.w3.org/2000/svg");
                sw.writeNamespace("", "http://www.w3.org/2000/svg");
                sw.writeNamespace("xlink", "http://www.w3.org/1999/xlink");
                sw.writeAttribute("width", ((SvgRoot) root).getWidth()+"");
                sw.writeAttribute("height", ((SvgRoot) root).getHeight()+"");
                if (((SvgRoot) root).getViewBox() != null) {
                    sw.writeAttribute("viewBox", ((SvgRoot) root).getViewBox());
                }
                sw.writeAttribute("style", new AttributeSetCoder().encode(root.getStyle()));
                for (SvgElement e : ((SvgRoot) root).getElements()) {
                    mapper.writeValue(sw, e);
                }
                sw.writeEndElement();
                sw.writeEndDocument();
                return stringWriter.toString();
            } catch (XMLStreamException x) {
                throw new IOException(x);
            }
        } else {
            return mapper().writeValueAsString(root);
        }
    }

    /**
     * Write SVG content to output stream.
     * @param root SVG content
     * @param output where to write
     * @throws IOException if there's a write failure or other IO failure
     */
    public static void write(SvgElement root, OutputStream output) throws IOException {
        mapper().writeValue(output, root);
    }

    /**
     * Write SVG content to writer.
     * @param root SVG content
     * @param writer where to write
     * @throws IOException if there's a write failure or other IO failure
     */
    public static void write(SvgElement root, Writer writer) throws IOException {
        mapper().writeValue(writer, root);
    }
    
}
