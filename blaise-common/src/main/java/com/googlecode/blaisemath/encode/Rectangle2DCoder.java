package com.googlecode.blaisemath.encode;

/*-
 * #%L
 * blaise-common
 * --
 * Copyright (C) 2014 - 2020 Elisha Peterson
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

import org.checkerframework.checker.nullness.qual.Nullable;

import java.awt.geom.Rectangle2D;
import static java.util.Objects.requireNonNull;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Adapter converting Rectangle2D to/from strings, of the form "rectangle2d(x,y,wid,ht)". Requires non-null values.
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
    public @Nullable Rectangle2D decode(String v) {
        requireNonNull(v);
        Matcher m = Pattern.compile("rectangle2d\\s*\\((.*),(.*),(.*),(.*)\\)").matcher(v.toLowerCase().trim());
        if (m.matches()) {
            try {
                double x = Double.parseDouble(m.group(1));
                double y = Double.parseDouble(m.group(2));
                double w = Double.parseDouble(m.group(3));
                double h = Double.parseDouble(m.group(4));
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
