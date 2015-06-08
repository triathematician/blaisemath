/*
 * RectAdapter.java
 * Created on Aug 26, 2013
 */
package com.googlecode.blaisemath.util.xml;

/*
 * #%L
 * BlaiseCommon
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

import com.google.common.base.Splitter;
import java.awt.geom.Rectangle2D;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Serializes a rectangular shape to/from text.
 * @author Elisha Peerson
 */
public class RectAdapter extends XmlAdapter<String,Rectangle2D> {

    @Override
    public Rectangle2D unmarshal(String v) {
        if (v == null) {
            return null;
        }
        Matcher m = Pattern.compile("rectangle\\[(.*)\\]").matcher(v.toLowerCase().trim());
        if (m.find()) {
            String inner = m.group(1);
            Map<String,String> kv = Splitter.on(",").trimResults().withKeyValueSeparator("=").split(inner);
            try {
                Double x = Double.valueOf(kv.get("x"));
                Double y = Double.valueOf(kv.get("y"));
                Double w = Double.valueOf(kv.get("w"));
                Double h = Double.valueOf(kv.get("h"));
                return new Rectangle2D.Double(x,y,w,h);
            } catch (NumberFormatException x) {
                Logger.getLogger(RectAdapter.class.getName()).log(Level.FINEST,
                        "Not a double", x);
                return null;
            }
        } else {
            Logger.getLogger(RectAdapter.class.getName()).log(Level.FINEST,
                    "Not a valid rectangle", v);
            return null;
        }
    }

    @Override
    public String marshal(Rectangle2D v) {
        return v == null ? "null"
                : String.format("rectangle[x=%f,y=%f,w=%f,h=%f]", 
                        v.getX(), v.getY(), v.getWidth(), v.getHeight());
    }

}
