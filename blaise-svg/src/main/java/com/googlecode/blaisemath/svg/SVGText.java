/**
 * SVGPolygon.java
 * Created Sep 26, 2014
 */

package com.googlecode.blaisemath.svg;

/*
 * #%L
 * BlaiseGraphics
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

import com.google.common.base.Converter;
import com.googlecode.blaisemath.util.AnchoredText;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * <p>
 *   SVG text object.
 * </p>
 * @author elisha
 */
@XmlRootElement(name="text")
public final class SVGText extends SVGElement {
    
    private static final TextConverter CONVERTER_INST = new TextConverter();
    
    private double x;
    private double y;
    private String content;

    public SVGText() {
        this(0, 0, "");
    }

    public SVGText(double x, double y, String content) {
        super("polygon");
        this.x = x;
        this.y = y;
        this.content = content;
    }
    
    //<editor-fold defaultstate="collapsed" desc="PROPERTY PATTERNS">
    //
    // PROPERTY PATTERNS
    //
    
    @XmlAttribute
    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    @XmlAttribute
    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    @XmlElement(name="tspan")
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public void setValue(String value) {
        setContent(value);
    }

    @Override
    public String getValue() {
        return getContent();
    }
    
    //</editor-fold>
    
    public static Converter<SVGText, AnchoredText> textConverter() {
        return CONVERTER_INST;
    }
    
    
    private static class TextConverter extends Converter<SVGText, AnchoredText> {
        @Override
        protected SVGText doBackward(AnchoredText r) {
            return new SVGText(r.getX(), r.getY(), r.getText());
        }

        @Override
        protected AnchoredText doForward(SVGText r) {
            return new AnchoredText(r.x, r.y, r.content);
        }
    }
    
}
