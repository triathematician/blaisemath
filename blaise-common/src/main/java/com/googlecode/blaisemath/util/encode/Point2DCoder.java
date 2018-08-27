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
import com.google.common.collect.Iterables;
import java.awt.geom.Point2D;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Adapter converting Point2D to/from strings.
 *
 * @author Elisha Peterson
 */
public final class Point2DCoder implements StringEncoder<Point2D>, StringDecoder<Point2D> {

    private static final Logger LOG = Logger.getLogger(Point2DCoder.class.getName());

    @Override
    public String encode(Point2D v) {
        return v == null ? "null"
                : String.format("point[%f,%f]", v.getX(), v.getY());
    }

    @Override
    public Point2D decode(String v) {
        if (v == null) {
            return null;
        }
        Matcher m = Pattern.compile("point\\[(.*)\\]").matcher(v.toLowerCase().trim());
        if (m.find()) {
            String inner = m.group(1);
            Iterable<String> kv = Splitter.on(",").trimResults().split(inner);
            try {
                Double x = Double.valueOf(Iterables.get(kv, 0));
                Double y = Double.valueOf(Iterables.get(kv, 1));
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
