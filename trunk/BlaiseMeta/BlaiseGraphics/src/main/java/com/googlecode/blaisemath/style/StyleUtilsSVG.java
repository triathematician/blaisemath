/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.googlecode.blaisemath.style;

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


import com.google.common.base.Joiner;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import com.googlecode.blaisemath.util.ColorAdapter;
import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.awt.geom.RoundRectangle2D;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import javax.xml.bind.annotation.XmlAttribute;

/**
 * Convert styles to/from SVG.
 * @author Elisha
 */
public class StyleUtilsSVG {
    
    // utility class
    private StyleUtilsSVG() {
    }

    /**
     * Converts SVG-compatible string to style object.
     * @param svg the CSS style string
     * @return the style
     */
    public static AttributeSet convertSVGToStyle(@Nonnull String svg) {
        checkNotNull(svg);
        AttributeSet res = new AttributeSet();
        Map<String, String> vals = Splitter.on(";").trimResults().withKeyValueSeparator(":").split(svg);
        for (String key : vals.keySet()) {
            String sval = vals.get(key);
            Object val = convertValueFromSVG(sval);
            res.put(key, val);
        }
        return res;
    }
    
    /** 
     * Convert style class to an SVG-compatible string.
     * @param style the style object
     * @return string representation of the style, in SVG format
     */    
    public static String convertStyleToSVG(@Nonnull AttributeSet style) {
        checkNotNull(style);
        Map<String,String> props = Maps.newTreeMap();
        for (String s : style.getAttributes()) {
            Object styleValue = style.get(s);
            props.put(convertKeyToSVG(s), convertValueToSVG(styleValue));
        }
        return Joiner.on("; ").withKeyValueSeparator(":").join(props);
    }
    
    /** 
     * Convert property keys via CamelCase, like "fontSize", to SVG-like keys like "font-size".
     * @param key a property name
     * @return svg-style version of the key
     */
    public static String convertKeyToSVG(String key) {
        Pattern p = Pattern.compile("([a-z]+)([A-Z])");
        Matcher m = p.matcher(key);
        StringBuilder res = new StringBuilder();
        int last = 0;
        while (m.find()) {
            res.append(m.group(1)).append("-").append(m.group(2).toLowerCase());
            last = m.end();
        }
        res.append(key.substring(last));
        return res.toString();
    }
    
    /** 
     * Convert values to SVG value strings.
     * @param val a value object
     * @return string representation of the value
     * @throws UnsupportedOperationException if the value type is not convertible
     */
    public static String convertValueToSVG(Object val) {
        if (val instanceof Color) {
            return ColorAdapter.INST.marshal((Color) val);
        } else if (val instanceof Number) {
            return val.toString();
        } else if (val instanceof String) {
            return (String) val;
        } else {
            throw new UnsupportedOperationException("Unsupported value: " + val);
        }
    }

    /**
     * Convert values from SVG versions to java types
     * @param sval string representation of value
     * @return object for value
     */
    public static Object convertValueFromSVG(String sval) {
        if (sval.matches("#[0-9a-fA-f]{6}")
                || sval.matches("#[0-9a-fA-f]{8}")) {
            return ColorAdapter.INST.unmarshal(sval);
        }
        try {
            return Integer.valueOf(sval);
        } catch (Exception x) {}
        try {
            return Double.valueOf(sval);
        } catch (Exception x) {}
        return sval;
    }
    
    //<editor-fold defaultstate="collapsed" desc="INNER CLASSES">
    //
    // INNER CLASSES
    //
    
    public abstract static class ShapeSVG {
        @XmlAttribute
        private String id;
        @XmlAttribute
        private String style;
        
        public ShapeSVG() {}
        public ShapeSVG(String id, AttributeSet style) {
            this.id = id;
            this.style = style == null ? null : StyleUtilsSVG.convertStyleToSVG(style);
        }
        
        public abstract RectangularShape createShape();
        
        public String getId() {
            return id;
        }
        
        public AttributeSet createStyle() {
            return style == null ? null : StyleUtilsSVG.convertSVGToStyle(style);
        }
    }
    
    /** Serializable proxy for images */
    public static class ImageSVG extends ShapeSVG {
        @XmlAttribute
        private double x = 0;
        @XmlAttribute
        private double y = 0;
        @XmlAttribute
        private double width = 0;
        @XmlAttribute
        private double height = 0;
        @XmlAttribute(name="href", namespace="http://www.w3.org/1999/xlink")
        private String imageRef = null;
        
        public ImageSVG() {}
        public ImageSVG(String id, String imageRef, Rectangle2D rect) {
            super(id, AttributeSet.EMPTY);
            this.imageRef = imageRef;
            this.x = rect.getX();
            this.y = rect.getY();
            this.width = rect.getWidth();
            this.height = rect.getHeight();
        }

        public String getImageRef() {
            return imageRef;
        }
        
        public Rectangle2D createShape() {
            return new Rectangle2D.Double(x, y, width, height);
        }
    }
    
    /** Serializable proxy class for {@link RectangularShape}. */
    public static class RectSVG extends ShapeSVG {
        @XmlAttribute
        private double x = 0;
        @XmlAttribute
        private double y = 0;
        @XmlAttribute
        private double width = 0;
        @XmlAttribute
        private double height = 0;
        @XmlAttribute
        private double rx = 0;
        @XmlAttribute
        private double ry = 0;
        
        public RectSVG() {}
        public RectSVG(String id, RectangularShape v, AttributeSet style) {
            super(id, style);
            checkArgument(v instanceof Rectangle2D || v instanceof RoundRectangle2D);
            this.x = v.getX();
            this.y = v.getY();
            this.width = v.getWidth();
            this.height = v.getHeight();
            if (v instanceof RoundRectangle2D) {
                RoundRectangle2D rr = (RoundRectangle2D) v;
                this.rx = rr.getArcWidth();
                this.ry = rr.getArcHeight();
            }
        }
        
        @Override
        public RectangularShape createShape() {
            if (rx != 0 || ry != 0) {
                return new RoundRectangle2D.Double(x, y, width, height, rx, ry);
            } else {
                return new Rectangle2D.Double(x, y, width, height);
            }
        }
    }
    
    /** serializable proxy class for {@link Ellipse2D}. */
    public static class EllipseSVG extends ShapeSVG {
        @XmlAttribute
        private double cx = 0;
        @XmlAttribute
        private double cy = 0;
        @XmlAttribute
        private double rx = 0;
        @XmlAttribute
        private double ry = 0;
        
        public EllipseSVG() {}
        public EllipseSVG(String id, Ellipse2D ellipse, AttributeSet style) {
            super(id, style);
            this.cx = ellipse.getCenterX();
            this.cy = ellipse.getCenterY();
            this.rx = ellipse.getWidth()/2;
            this.ry = ellipse.getHeight()/2;
        }
        
        @Override
        public Ellipse2D createShape() {
            Ellipse2D.Double res = new Ellipse2D.Double();
            res.setFrameFromCenter(cx, cy, cx+rx, cy+ry);
            return res;
        }
    }
    
    //</editor-fold>

    
}
