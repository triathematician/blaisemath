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

import java.awt.geom.Rectangle2D;
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
public final class Rectangle2DCoder implements StringEncoder<Rectangle2D>, StringDecoder<Rectangle2D> {

    private static final Logger LOG = Logger.getLogger(Rectangle2DCoder.class.getName());

    @Override
    public String encode(Rectangle2D v) {
        requireNonNull(v);
        return String.format("rectangle2d(%f,%f,%f,%f)", v.getX(), v.getY(), v.getWidth(), v.getHeight());
    }

    @Override
    public Rectangle2D decode(String v) {
        requireNonNull(v);
        Matcher m = Pattern.compile("rectangle2d\\s*\\((.*),(.*),(.*),(.*)\\)").matcher(v.toLowerCase().trim());
        if (m.matches()) {
            try {
                Double x = Double.valueOf(m.group(1));
                Double y = Double.valueOf(m.group(2));
                Double w = Double.valueOf(m.group(3));
                Double h = Double.valueOf(m.group(4));
                return new Rectangle2D.Double(x, y, w, h);
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
