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
import static java.util.Objects.requireNonNull;
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
        requireNonNull(v);
        return String.format("rectangle(%d,%d,%d,%d)", v.x, v.y, v.width, v.height);
    }

    @Override
    public Rectangle decode(String v) {
        requireNonNull(v);
        Matcher m = Pattern.compile("rectangle\\s*\\((.*),(.*),(.*),(.*)\\)").matcher(v.toLowerCase().trim());
        if (m.matches()) {
            try {
                Integer x = Integer.valueOf(m.group(1));
                Integer y = Integer.valueOf(m.group(2));
                Integer w = Integer.valueOf(m.group(3));
                Integer h = Integer.valueOf(m.group(4));
                return new Rectangle(x, y, w, h);
            } catch (NumberFormatException x) {
                LOG.log(Level.FINEST, "Not an integer", x);
                return null;
            }
        } else {
            LOG.log(Level.FINEST, "Not a valid rectangle", v);
            return null;
        }
    }
    
}
