package com.googlecode.blaisemath.svg;

/*
 * #%L
 * BlaiseGraphics
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

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.google.common.base.Converter;
import static com.googlecode.blaisemath.svg.SvgPolyline.checkPointString;
import static com.googlecode.blaisemath.svg.SvgPolyline.toPath;
import static com.googlecode.blaisemath.svg.SvgPolyline.toPathString;
import java.awt.geom.GeneralPath;

/**
 * SVG Polygon object.
 *
 * @author Elisha Peterson
 */
@JacksonXmlRootElement(localName="polygon")
public final class SvgPolygon extends SvgElement {
    
    private static final PolygonConverter CONVERTER_INST = new PolygonConverter();
    
    private String ptStr = "";

    public SvgPolygon() {
        super("polygon");
    }

    public SvgPolygon(String pts) {
        super("polygon");
        this.ptStr = checkPointString(pts);
    }
    
    //region PROPERTIES


    @JacksonXmlProperty(isAttribute = true, localName="points")
    public String getPointStr() {
        return ptStr;
    }

    public void setPointStr(String pathStr) {
        this.ptStr = checkPointString(pathStr);
    }
    
    //endregion
    
    public static Converter<SvgPolygon, GeneralPath> shapeConverter() {
        return CONVERTER_INST;
    }
    
    private static final class PolygonConverter extends Converter<SvgPolygon, GeneralPath> {
        @Override
        protected GeneralPath doForward(SvgPolygon a) {
            GeneralPath gp = toPath(a.ptStr);
            gp.closePath();
            return gp;
        }

        @Override
        protected SvgPolygon doBackward(GeneralPath b) {
            String s = toPathString(b);
            return new SvgPolygon(s);
        }
    }

}
