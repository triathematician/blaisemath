/**
 * SVGPath.java
 * Created Dec 9, 2012
 */

package com.googlecode.blaisemath.svg;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2009 - 2014 Elisha Peterson
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

import com.google.common.base.Converter;
import static com.google.common.base.Preconditions.checkArgument;
import com.google.common.collect.Lists;
import java.awt.geom.Arc2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.annotation.XmlElement;

/**
 * <p>
 *   SVG path object.
 * </p>
 * @author elisha
 * @todo implement functionality
 */
public final class SVGPath extends SVGElement {

    private static final PathConverter CONVERTER_INST = new PathConverter();
    
    private String pathStr;
    
    public SVGPath() {
        super("path");
    }
    
    public SVGPath(String pathStr) {
        super("path");
        this.pathStr = pathStr;
    }
    
    //<editor-fold defaultstate="collapsed" desc="PROPERTY PATTERNS">
    //
    // PROPERTY PATTERNS
    //
    
    @XmlElement(name="???")
    public String getPathStr() {
        return pathStr;
    }

    public void setPathStr(String pathStr) {
        this.pathStr = pathStr;
    }
    
    //</editor-fold>
    
    public static Converter<SVGPath, GeneralPath> shapeConverter() {
        return CONVERTER_INST;
    }
    
    private static final class PathConverter extends Converter<SVGPath, GeneralPath> {
        @Override
        protected GeneralPath doForward(SVGPath path) {
            String svg = path.pathStr;
            GeneralPath gp = new GeneralPath();
            // ensure spaces follow every letter
            svg = svg.replaceAll("[A-Za-z]", " $0 ").trim();
            String[] spl = svg.split("[\\s,]+");
            float[] curveRes = null;
            float[] quadCurveRes = null;
            for (int pos = 0; pos < spl.length; pos++) {
                String s = spl[pos].trim();
                if (s.length() != 1) {
                    throw new IllegalArgumentException("invalid comand: " + s + "   svg:\n"+svg);
                }
                boolean rel = Character.isLowerCase(s.charAt(0));
                float[] cur = gp.getCurrentPoint() == null ? null
                        : new float[] { (float) gp.getCurrentPoint().getX(), (float) gp.getCurrentPoint().getY() };
                float[] add = rel ? cur : new float[] { 0, 0 };
                float[] coords = values(spl, pos+1);
                pos += coords.length;
                int ch = s.toLowerCase().charAt(0);
                switch (ch) {
                    case 'm':
                        move(gp, coords, add);
                        curveRes = null;
                        quadCurveRes = null;
                        break;
                    case 'l':
                    case 'h':
                    case 'v':
                        line(gp, coords, ch, cur, add);
                        curveRes = null;
                        quadCurveRes = null;
                        break;
                    case 'c':
                    case 's':
                        curveRes = curve(gp, coords, ch, add, cur, curveRes);
                        quadCurveRes = null;
                        break;
                    case 'q':
                    case 't':
                        quadCurveRes = quadCurve(gp, coords, ch, add, cur, quadCurveRes);
                        curveRes = null;
                        break;
                    case 'a':
                        arc(gp, coords, add);
                        curveRes = null;
                        quadCurveRes = null;
                        break;
                    case 'z':
                        gp.closePath();
                        break;
                    default:
                        throw new IllegalArgumentException(svg);

                }
            }
            return gp;
        }

        @Override
        protected SVGPath doBackward(GeneralPath b) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
    
    //<editor-fold defaultstate="collapsed" desc="STATIC UTILITIES">
    
    /** Move through list of coordinates */
    private static void move(GeneralPath gp, float[] coords, float[] add) {
        for (int i = 0; i < coords.length-1; i++) {
            gp.moveTo(coords[i]+add[0], coords[i+1]+add[1]);
        }
    }
    
    /** 
     * Draw successive lines through list of coordinates 
     */
    private static void line(GeneralPath gp, float[] coords, int ch, float[] cur, float[] add) {
        switch(ch) {
            case 'l':
                for (int i = 0; i < coords.length-1; i++) {
                    gp.lineTo(coords[i]+add[0], coords[i+1]+add[1]);
                }
                break;
            case 'h':
                for (int i = 0; i < coords.length; i++) {
                    gp.lineTo(coords[i]+add[0], cur[1]);
                }
                break;
            case 'v':
                for (int i = 0; i < coords.length; i++) {
                    gp.lineTo(cur[0], coords[i]+add[1]);
                }
                break;
            default:
                throw new IllegalArgumentException();
        }
    }
    
    /**
     * Draw curve using specified coordinates
     * @param gp path to add to
     * @param coords list of coordinates
     * @param ch character indicating type of draw
     * @param add relative position to add to coordinates
     * @param cur coordinates of current point
     * @param last coordinates of last control point
     * @return last control point used in this segment of draw
     */
    private static float[] quadCurve(GeneralPath gp, float[] coords, int ch, float[] add, float[] cur, float[] last) {
        switch(ch) {
            case 'q':
                for (int i = 0; i < coords.length-3; i++) {
                    gp.quadTo(coords[i]+add[0], coords[i+1]+add[1], coords[i+2]+add[0], coords[i+3]+add[1]);
                    last = new float[] { coords[i]+add[0], coords[i+1]+add[1] };
                }
                break;
            case 't':
                for (int i = 0; i < coords.length-1; i++) {
                    float[] ctrl = last == null ? new float[]{cur[0], cur[1]} 
                            : new float[] {2*cur[0]-last[0], 2*cur[1]-last[1]};
                    gp.quadTo(ctrl[0], ctrl[1], coords[i]+add[0], coords[i+1]+add[1]);
                    last = ctrl;
                }
                break;
            default:
                throw new IllegalArgumentException();
        }
        return last;
    }
    
    private static float[] curve(GeneralPath gp, float[] coords, int ch, float[] add, float[] cur, float[] last) {    
        switch(ch) {
            case 'c':
                for (int i = 0; i < coords.length-5; i++) {
                    gp.curveTo(coords[i]+add[0], coords[i+1]+add[1], coords[i+2]+add[0], coords[i+3]+add[1], coords[i+4]+add[0], coords[i+5]+add[1]);
                    last = new float[] { coords[i+2]+add[0], coords[i+3]+add[1] };
                }
                break;
            case 's':
                checkArgument(last != null);
                for (int i = 0; i < coords.length-3; i++) {
                    float[] ctrl = last == null ? new float[]{cur[0], cur[1]} 
                            : new float[] {2*cur[0]-last[0], 2*cur[1]-last[1]};
                    gp.curveTo(ctrl[0], ctrl[1], coords[i]+add[0], coords[i+1]+add[1], coords[i+2]+add[0], coords[i+3]+add[1]);
                    last = new float[] { coords[i]+add[0], coords[i+1]+add[1] };
                }
                break;
            default:
                throw new IllegalArgumentException();
        }
        return last;
    }
    
    private static void arc(GeneralPath gp, float[] coords, float[] add) {
        for (int i = 0; i < coords.length-6; i+=6) {
            arcTo(gp, coords[i], coords[i+1], coords[i+2], coords[i+3]==1f, coords[i+4]==1f, coords[i+5]+add[0], coords[i+6]+add[1]);
        }
    }
    
    /**
     * See http://stackoverflow.com/questions/1805101/svg-elliptical-arcs-with-java.
     * @param path
     * @param rx
     * @param ry
     * @param theta
     * @param largeArcFlag
     * @param sweepFlag
     * @param x
     * @param y 
     */
    private static final void arcTo(GeneralPath path, float rx, float ry, float theta, boolean largeArcFlag, boolean sweepFlag, float x, float y) {
        // Ensure radii are valid
        if (rx == 0 || ry == 0) {
                path.lineTo(x, y);
                return;
        }
        // Get the current (x, y) coordinates of the path
        Point2D p2d = path.getCurrentPoint();
        float x0 = (float) p2d.getX();
        float y0 = (float) p2d.getY();
        // Compute the half distance between the current and the final point
        float dx2 = (x0 - x) / 2.0f;
        float dy2 = (y0 - y) / 2.0f;
        // Convert theta from degrees to radians
        theta = (float) Math.toRadians(theta % 360f);

        //
        // Step 1 : Compute (x1, y1)
        //
        float x1 = (float) (Math.cos(theta) * (double) dx2 + Math.sin(theta)
                        * (double) dy2);
        float y1 = (float) (-Math.sin(theta) * (double) dx2 + Math.cos(theta)
                        * (double) dy2);
        // Ensure radii are large enough
        rx = Math.abs(rx);
        ry = Math.abs(ry);
        float prx = rx * rx;
        float pry = ry * ry;
        float px1 = x1 * x1;
        float py1 = y1 * y1;
        double d = px1 / prx + py1 / pry;
        if (d > 1) {
            rx = Math.abs((float) (Math.sqrt(d) * (double) rx));
            ry = Math.abs((float) (Math.sqrt(d) * (double) ry));
            prx = rx * rx;
            pry = ry * ry;
        }

        //
        // Step 2 : Compute (cx1, cy1)
        //
        double sign = (largeArcFlag == sweepFlag) ? -1d : 1d;
        float coef = (float) (sign * Math .sqrt(((prx * pry) - (prx * py1) - (pry * px1))
                                        / ((prx * py1) + (pry * px1))));
        float cx1 = coef * ((rx * y1) / ry);
        float cy1 = coef * -((ry * x1) / rx);

        //
        // Step 3 : Compute (cx, cy) from (cx1, cy1)
        //
        float sx2 = (x0 + x) / 2.0f;
        float sy2 = (y0 + y) / 2.0f;
        float cx = sx2 + (float) (Math.cos(theta) * (double) cx1 - Math.sin(theta) * (double) cy1);
        float cy = sy2 + (float) (Math.sin(theta) * (double) cx1 + Math.cos(theta) * (double) cy1);

        //
        // Step 4 : Compute the angleStart (theta1) and the angleExtent (dtheta)
        //
        float ux = (x1 - cx1) / rx;
        float uy = (y1 - cy1) / ry;
        float vx = (-x1 - cx1) / rx;
        float vy = (-y1 - cy1) / ry;
        float p, n;
        // Compute the angle start
        n = (float) Math.sqrt((ux * ux) + (uy * uy));
        p = ux; // (1 * ux) + (0 * uy)
        sign = (uy < 0) ? -1d : 1d;
        float angleStart = (float) Math.toDegrees(sign * Math.acos(p / n));
        // Compute the angle extent
        n = (float) Math.sqrt((ux * ux + uy * uy) * (vx * vx + vy * vy));
        p = ux * vx + uy * vy;
        sign = (ux * vy - uy * vx < 0) ? -1d : 1d;
        float angleExtent = (float) Math.toDegrees(sign * Math.acos(p / n));
        if (!sweepFlag && angleExtent > 0) {
            angleExtent -= 360f;
        } else if (sweepFlag && angleExtent < 0) {
            angleExtent += 360f;
        }
        angleExtent %= 360f;
        angleStart %= 360f;

        Arc2D.Float arc = new Arc2D.Float();
        arc.x = cx - rx;
        arc.y = cy - ry;
        arc.width = rx * 2.0f;
        arc.height = ry * 2.0f;
        arc.start = -angleStart;
        arc.extent = -angleExtent;
        path.append(arc, true);
    }
    

    /** 
     * Convert strings to floats, starting at given index. 
     * @param str array of strings
     * @param start starting index
     * @return coordinates extracted from string
     */
    private static float[] values(String[] str, int start) {
        List<Float> vals = Lists.newArrayList();
        int idx = start;
        while (idx < str.length) {
            try {
                vals.add(Float.valueOf(str[idx]));
                idx++;
            } catch (NumberFormatException ex) {
                Logger.getLogger(SVGPath.class.getName()).log(Level.FINEST,
                        "Not a float: "+str[idx], ex);
                break;
            }
        }
        float[] res = new float[vals.size()];
        for (int i = 0; i < res.length; i++) {
            res[i] = vals.get(i);
        }
        return res;
    }
    
    //</editor-fold>

}
