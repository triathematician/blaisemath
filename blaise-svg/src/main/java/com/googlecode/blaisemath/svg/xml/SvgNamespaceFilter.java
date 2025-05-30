package com.googlecode.blaisemath.svg.xml;

/*
 * #%L
 * blaise-svg
 * --
 * Copyright (C) 2014 - 2025 Elisha Peterson
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


import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLFilterImpl;

/**
 * Filters SVG elements without a namespace.
 * @author elisha
 */
class SvgNamespaceFilter extends XMLFilterImpl {

    private static final String NAMESPACE = "http://www.w3.org/2000/svg";

    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        super.endElement(uri.equals("") ? NAMESPACE : uri, localName, qName);
    }

    @Override
    public void startElement(String uri, String localName, String qName,
                             Attributes atts) throws SAXException {
        super.startElement(uri.equals("") ? NAMESPACE : uri, localName, qName, atts);
    }

}
