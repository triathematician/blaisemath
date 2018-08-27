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
import com.googlecode.blaisemath.util.NumberSplitter;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.List;
import static java.util.Objects.requireNonNull;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Adapter converting Point to/from strings.
 *
 * @author Elisha Peterson
 */
public final class PointCoder implements StringEncoder<Point>, StringDecoder<Point> {

    private static final Logger LOG = Logger.getLogger(PointCoder.class.getName());

    @Override
    public String encode(Point v) {
        requireNonNull(v);
        return String.format("point[%d,%d]", v.x, v.y);
    }

    @Override
    public Point decode(String v) {
        requireNonNull(v);
        Matcher m = Pattern.compile("point\\[(.*)\\]").matcher(v.toLowerCase().trim());
        if (m.find()) {
            String inner = m.group(1);
            Iterable<String> kv = Splitter.on(",").trimResults().split(inner);
            try {
                int x = Integer.valueOf(Iterables.get(kv, 0));
                int y = Integer.valueOf(Iterables.get(kv, 1));
                return new Point(x,y);
            } catch (NumberFormatException x) {
                LOG.log(Level.FINEST, "Not an integer", x);
                return null;
            }
        } else {
            LOG.log(Level.FINEST, "Not a valid point", v);
            return null;
        }
    }
    
//    /**
//     * Encode point as string, in the form (a, b)
//     * @param p point to encode
//     * @return string
//     */
//    public static String encode(Point2D p) {
//        requireNonNull(p);
//        if (p instanceof Point) {
//            return String.format("(%d,%d)", ((Point) p).x, ((Point) p).y);
//        } else {
//            return String.format("(%f,%f)", p.getX(), p.getY());
//        }
//    }
//    
//    /**
//     * Decodes point from string, assuming the notation used by {@link #encode}.
//     * @param v string
//     * @return decoded string
//     * @throws NullPointerException if v is null
//     * @throws IllegalArgumentException if v is an invalid string
//     */
//    public static Point2D decode(String v) {
//        String str = v.trim();
//        if (str.startsWith("(") && str.endsWith(")")) {
//            str = str.substring(1, str.length() - 1).trim();
//        }
//        NumberSplitter splitter = NumberSplitter.onPattern("\\s*[,\\s]\\s*");
//        List<Integer> attempt = splitter.trySplitToIntegers(str, null);
//        if (attempt != null && attempt.size() == 2) {
//            return new Point(attempt.get(0), attempt.get(1));
//        }
//        List<Double> attempt2 = splitter.trySplitToDoubles(str, null);
//        if (attempt2 != null && attempt2.size() == 2) {
//            return new Point2D.Double(attempt2.get(0), attempt2.get(1));
//        }
//        throw new IllegalArgumentException("Invalid point: "+v);
//    }
    
}
