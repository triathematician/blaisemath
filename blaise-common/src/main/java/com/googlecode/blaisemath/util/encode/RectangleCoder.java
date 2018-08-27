package com.googlecode.blaisemath.util.encode;

/*-
 * #%L
 * blaise-common
 * --
 * Copyright (C) 2014 - 2018 Elisha Peterson
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
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Adapter converting Rectangle2D to/from strings.
 *
 * @author Elisha Peterson
 */
public final class RectangleCoder implements StringEncoder<Rectangle>, StringDecoder<Rectangle> {

    private static final Logger LOG = Logger.getLogger(RectangleCoder.class.getName());

    @Override
    public String encode(Rectangle v) {
        return v == null ? "null" : String.format("rectangle[x=%f,y=%f,w=%f,h=%f]",
                v.getX(), v.getY(), v.getWidth(), v.getHeight());
    }

    @Override
    public Rectangle decode(String v) {
        if (v == null) {
            return null;
        }
        Matcher m = Pattern.compile("rectangle\\[(.*)\\]").matcher(v.toLowerCase().trim());
        if (m.find()) {
            String inner = m.group(1);
            Map<String,String> kv = Splitter.on(",").trimResults().withKeyValueSeparator("=").split(inner);
            try {
                Integer x = Integer.valueOf(kv.get("x"));
                Integer y = Integer.valueOf(kv.get("y"));
                Integer w = Integer.valueOf(kv.get("w"));
                Integer h = Integer.valueOf(kv.get("h"));
                return new Rectangle(x,y,w,h);
            } catch (NumberFormatException x) {
                LOG.log(Level.FINEST, "Not a double", x);
                return null;
            }
        } else {
            LOG.log(Level.FINEST, "Not a valid rectangle", v);
            return null;
        }
    }
    
}
