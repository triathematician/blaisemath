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

import org.checkerframework.checker.nullness.qual.Nullable;

import java.awt.geom.Point2D;
import static java.util.Objects.requireNonNull;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Adapter converting Point2D to/from strings of the form "(1.1,2)". Requires non-null values.
 *
 * @author Elisha Peterson
 */
public final class Point2DCoder implements StringEncoder<Point2D>, StringDecoder<Point2D> {

    private static final Logger LOG = Logger.getLogger(Point2DCoder.class.getName());

    @Override
    public String encode(Point2D v) {
        requireNonNull(v);
        return String.format("(%f,%f)", v.getX(), v.getY());
    }

    @Override
    public @Nullable Point2D decode(String v) {
        requireNonNull(v);
        Matcher m = Pattern.compile("\\((.*),(.*)\\)").matcher(v.toLowerCase().trim());
        if (m.matches()) {
            try {
                double x = Double.parseDouble(m.group(1).trim());
                double y = Double.parseDouble(m.group(2).trim());
                return new Point2D.Double(x,y);
            } catch (NumberFormatException x) {
                LOG.log(Level.FINEST, "Not a double", x);
                return null;
            }
        } else {
            LOG.log(Level.FINEST, "Not a valid point", v);
            return null;
        }
    }
    
}
