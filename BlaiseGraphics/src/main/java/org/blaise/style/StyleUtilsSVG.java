/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.blaise.style;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import java.awt.Color;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Convert styles to/from SVG.
 * @author Elisha
 */
public class StyleUtilsSVG {
    
    // utility class

    private StyleUtilsSVG() {
    }

    /** Convert style class to string. */    
    public static String convertStyleToSVG(Object style) throws IntrospectionException {
        BeanInfo info = Introspector.getBeanInfo(style.getClass());
        Map<String,String> props = Maps.newLinkedHashMap();
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (pd.getReadMethod() != null && pd.getWriteMethod() != null) {
                try {
                    props.put(convertKeyToSVG(pd.getName()), convertValueToSVG(pd.getReadMethod().invoke(style)));
                } catch (Exception ex) {
                    Logger.getLogger(StyleUtilsSVG.class.getName()).log(Level.SEVERE, "Failed to write property "+pd.getName(), ex);
                }
            }
        }
        return Joiner.on("; ").withKeyValueSeparator(":").join(props);
    }
    
    /** Convert property keys via camelcase, like "fontSize", to SVG-like keys like "font-size" */
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
    
    /** Convert values to SVG value strings */
    public static String convertValueToSVG(Object val) throws UnsupportedOperationException {
        if (val instanceof Color) {
            Color c = (Color) val;
            String col = String.format("%8h", c.getRGB());
            if (col.startsWith("ff")) {
                return "#"+col.substring(2);
            } else {
                return "#"+col;
            }
        } else if (val instanceof Number) {
            return ((Number)val).toString();
        } else if (val instanceof String) {
            return (String) val;
        } else {
            throw new UnsupportedOperationException("Unsupported value: " + val);
        }
    }

    
}
