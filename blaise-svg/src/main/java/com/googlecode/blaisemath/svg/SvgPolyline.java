package com.googlecode.blaisemath.svg;

/*
 * #%L
 * BlaiseGraphics
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

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.google.common.base.Converter;

import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * SVG Polyline object.
 *
 * @author Elisha Peterson
 */
@JacksonXmlRootElement(localName="polyline")
public final class SvgPolyline extends SvgElement {
    
    private static final PolylineConverter CONVERTER_INST = new PolylineConverter();
    
    private String ptStr;

    public SvgPolyline() {
        super("polyline");
    }

    public SvgPolyline(String pts) {
        super("polyline");
        this.ptStr = checkPointString(pts);
    }
    
    //region PROPERTIES

    @JacksonXmlProperty(isAttribute = true, localName="points")
    public String getPointStr() {
        return ptStr;
    }

    public void setPointStr(String ptStr) {
        this.ptStr = checkPointString(ptStr);
    }
    
    //endregion
    
    public static Converter<SvgPolyline, GeneralPath> shapeConverter() {
        return CONVERTER_INST;
    }
    
    private static final class PolylineConverter extends Converter<SvgPolyline, GeneralPath> {
        @Override
        protected GeneralPath doForward(SvgPolyline a) {
            return toPath(a.ptStr);
        }

        @Override
        protected SvgPolyline doBackward(GeneralPath b) {
            return new SvgPolyline(toPathString(b));
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
                Double.valueOf(p2[0]);
                Double.valueOf(p2[1]);
            } catch (NumberFormatException x) {
                throw new IllegalArgumentException("Invalid point string: "+s, x);
            }
        }
        return s;
    }
    
    /** Converts point string to path */
    static GeneralPath toPath(String pointStr) {
        checkPointString(pointStr);
        String ps = pointStr.replaceAll("\\s*,\\s*", ",");
        GeneralPath path = new GeneralPath();
        for (String p : ps.split("\\s+")) {
            String[] sp2 = p.split(",");
            if (path.getCurrentPoint() == null) {
                path.moveTo(Double.valueOf(sp2[0]), Double.valueOf(sp2[1]));
            } else {
                path.lineTo(Double.valueOf(sp2[0]), Double.valueOf(sp2[1]));
            }
        }
        return path;
    }
    
    /** Converts path to point string */
    static String toPathString(GeneralPath gp) {
        PathIterator pi = gp.getPathIterator(null);
        float[] cur = new float[6];
        int curSegmentType = -1;
        String s0 = null;
        StringBuilder pathString = new StringBuilder();
        while (!pi.isDone()) {
            curSegmentType = pi.currentSegment(cur);
            if (curSegmentType == PathIterator.SEG_LINETO || curSegmentType == PathIterator.SEG_MOVETO) {
                String s = " "+ SvgPath.numStr(",", 6, cur[0], cur[1]);
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
    
    //endregion

}
