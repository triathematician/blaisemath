/**
 * SvgPolygon.java
 * Created Sep 26, 2014
 */

package com.googlecode.blaisemath.svg;

/*
 * #%L
 * BlaiseGraphics
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

import com.google.common.base.Converter;
import com.googlecode.blaisemath.graphics.AnchoredText;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * <p>
 *   Svg text object.
 * </p>
 * @author elisha
 */
@XmlRootElement(name="text")
public final class SvgText extends SvgElement {
    
    private static final TextConverter CONVERTER_INST = new TextConverter();
    
    private double x;
    private double y;
    private String content;

    public SvgText() {
        this(0, 0, "");
    }

    public SvgText(double x, double y, String content) {
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

    @Override
    public String getValue() {
        return content;
    }

    @Override
    public void setValue(String value) {
        this.content = value;
    }
    
    //</editor-fold>
    
    public static Converter<SvgText, AnchoredText> textConverter() {
        return CONVERTER_INST;
    }
    
    
    private static class TextConverter extends Converter<SvgText, AnchoredText> {
        @Override
        protected SvgText doBackward(AnchoredText r) {
            return new SvgText(r.getX(), r.getY(), r.getText());
        }

        @Override
        protected AnchoredText doForward(SvgText r) {
            return new AnchoredText(r.x, r.y, r.content);
        }
    }
    
}