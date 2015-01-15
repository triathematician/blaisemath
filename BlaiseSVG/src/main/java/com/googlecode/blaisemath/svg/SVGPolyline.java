/**
 * Polyline.java
 * Created Sep 26, 2014
 */

package com.googlecode.blaisemath.svg;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2014 - 2015 Elisha Peterson
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
 *   SVG Polyline object.
 * </p>
 * @author elisha
 * TODO - implement functionality
 */
@XmlRootElement(name="polyline")
public final class SVGPolyline extends SVGElement {
    
    private static final PolylineConverter CONVERTER_INST = new PolylineConverter();

    public SVGPolyline() {
        super("polyline");
    }
    
    public static Converter<SVGPolyline, GeneralPath> shapeConverter() {
        return CONVERTER_INST;
    }
    
    private static final class PolylineConverter extends Converter<SVGPolyline, GeneralPath> {
        @Override
        protected GeneralPath doForward(SVGPolyline a) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        protected SVGPolyline doBackward(GeneralPath b) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

}
