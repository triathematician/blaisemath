/**
 * SVGPolygon.java
 * Created Sep 26, 2014
 */

package com.googlecode.blaisemath.svg;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2009 - 2014 Elisha Peterson
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

import com.google.common.base.Converter;
import java.awt.geom.GeneralPath;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * <p>
 *   SVG Polygon object.
 * </p>
 * @author elisha
 * @todo implement functionality
 */
@XmlRootElement(name="polygon")
public final class SVGPolygon extends SVGElement {
    
    private static final PolygonConverter CONVERTER_INST = new PolygonConverter();

    public SVGPolygon() {
        super("polygon");
    }
    
    public static Converter<SVGPolygon, GeneralPath> shapeConverter() {
        return CONVERTER_INST;
    }
    
    private static final class PolygonConverter extends Converter<SVGPolygon, GeneralPath> {
        @Override
        protected GeneralPath doForward(SVGPolygon a) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        protected SVGPolygon doBackward(GeneralPath b) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }

}
