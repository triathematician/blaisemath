package com.googlecode.blaisemath.svg.reader;

/*-
 * #%L
 * blaise-svg
 * --
 * Copyright (C) 2014 - 2019 Elisha Peterson
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

import com.googlecode.blaisemath.svg.xml.SvgPolyline;

import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Converts an SVG polyline to its Java shape equivalent.
 * @author Elisha Peterson
 */
public class SvgPolylineReader extends SvgShapeReader<SvgPolyline, Path2D.Float> {

    @Override
    protected Path2D.Float createPrimitive(SvgPolyline r) throws SvgReadException {
        if (r == null) {
            throw new SvgReadException("Null SVG element");
        }
        try {
            String pointStr = r.points;
            String ps = pointStr.replaceAll("\\s*,\\s*", ",");
            Path2D.Float path = new Path2D.Float();
            for (String p : ps.split("\\s+")) {
                String[] sp2 = p.split(",");
                if (path.getCurrentPoint() == null) {
                    path.moveTo(Float.valueOf(sp2[0]), Float.valueOf(sp2[1]));
                } else {
                    path.lineTo(Float.valueOf(sp2[0]), Float.valueOf(sp2[1]));
                }
            }
            return path;
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException x) {
            throw new SvgReadException("Invalid point string: " + r.points, x);
        }
    }

    //region POINT STRING UTILITIES

    /** Ensures validity of a string of points */
    static String checkPointString(String s) {
        checkNotNull(s);
        String ps = s.replaceAll("\\s*,\\s*", ",");
        String[] pp = ps.split("\\s+");
        for (String p : pp) {
            String[] p2 = p.split(",");
            checkArgument(p2.length == 2, "Invalid point string: "+s);
            try {
                Float.valueOf(p2[0]);
                Float.valueOf(p2[1]);
            } catch (NumberFormatException x) {
                throw new IllegalArgumentException("Invalid point string: "+s, x);
            }
        }
        return s;
    }

    /** Converts path to point string */
    static String toPathString(Path2D gp) {
        PathIterator pi = gp.getPathIterator(null);
        float[] cur = new float[6];
        int curSegmentType = -1;
        String s0 = null;
        StringBuilder pathString = new StringBuilder();
        while (!pi.isDone()) {
            curSegmentType = pi.currentSegment(cur);
            if (curSegmentType == PathIterator.SEG_LINETO || curSegmentType == PathIterator.SEG_MOVETO) {
                String s = " "+ numStr(",", 6, cur[0], cur[1]);
                pathString.append(s);
                if (s0 == null) {
                    s0 = s;
                }
            } else if (curSegmentType == PathIterator.SEG_CLOSE) {
                // ignore - this is the difference between polyline and polygon
            } else {
                throw new IllegalArgumentException("Path cannot be converted to polyline.");
            }
            pi.next();
        }
        return pathString.toString().trim();
    }

    /** Prints numbers w/ up to n digits of precision, removing trailing zeros */
    static String numStr(int prec, float val) {
        String res = String.format("%."+prec+"f", val);
        return res.indexOf('.') < 0 ? res : res.replaceAll("0*$", "").replaceAll("\\.$", "");
    }

    /** Prints a sequence of numbers with the specified joiner and precision */
    static String numStr(String join, int prec, float... vals) {
        if (vals.length == 0) {
            return "";
        }
        StringBuilder res = new StringBuilder();
        res.append(numStr(prec, vals[0]));
        for (int i = 1; i < vals.length; i++) {
            res.append(join).append(numStr(prec, vals[i]));
        }
        return res.toString();
    }

    //endregion

}
