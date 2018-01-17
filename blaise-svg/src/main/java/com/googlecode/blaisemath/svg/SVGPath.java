/**
 * SVGPath.java
 * Created Dec 9, 2012
 */

package com.googlecode.blaisemath.svg;

/*
 * #%L
 * BlaiseGraphics
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

import com.google.common.base.Converter;
import static com.google.common.base.Preconditions.checkArgument;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.primitives.Floats;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * <p>
 *   SVG path object.
 * </p>
 * @author elisha
 */
@XmlRootElement(name="path")
public final class SVGPath extends SVGElement {

    private static final Logger LOG = Logger.getLogger(SVGPath.class.getName());

    private static final PathConverter CONVERTER_INST = new PathConverter();
    
    private String pathStr;
    
    public SVGPath() {
        super("path");
    }
    
    public SVGPath(String pathStr) {
        super("path");
        this.pathStr = checkSvgPathStr(pathStr);
    }
        
    /**
     * Create an {@code SVGPath} from a {@code PathIterator} object.
     * @param pi path iterator
     * @return svg path
     */
    public static SVGPath create(PathIterator pi) {
        float[] cur = new float[6];
        int curSegmentType = -1;
        StringBuilder pathString = new StringBuilder();
        while (!pi.isDone()) {
            curSegmentType = pi.currentSegment(cur);
            switch (curSegmentType) {
                case PathIterator.SEG_MOVETO:
                    pathString.append("M ").append(numStr(" ", 6, cur[0], cur[1])).append(" ");
                    break;
                case PathIterator.SEG_LINETO:
                    pathString.append("L ").append(numStr(" ", 6, cur[0], cur[1])).append(" ");
                    break;
                case PathIterator.SEG_QUADTO:
                    pathString.append("Q ").append(numStr(" ", 6, cur[0], cur[1], cur[2], cur[3])).append(" ");
                    break;
                case PathIterator.SEG_CUBICTO:
                    pathString.append("C ").append(numStr(" ", 6, cur[0], cur[1], cur[2], cur[3], cur[4], cur[5])).append(" ");
                    break;
                case PathIterator.SEG_CLOSE:
                    pathString.append("Z");
                    break;
                default:
                    break;
            }
            pi.next();
        }
        return new SVGPath(pathString.toString().trim());
    }
    
    //<editor-fold defaultstate="collapsed" desc="PROPERTY PATTERNS">
    //
    // PROPERTY PATTERNS
    //
    
    @XmlAttribute(name="d")
    public String getPathStr() {
        return pathStr;
    }

    public void setPathStr(String pathStr) {
        this.pathStr = checkSvgPathStr(pathStr);
    }
    
    //</editor-fold>
    
    public static Converter<SVGPath, Path2D> shapeConverter() {
        return CONVERTER_INST;
    }
    
    private static final class PathConverter extends Converter<SVGPath, Path2D> {
        @Override
        protected Path2D doForward(SVGPath path) {
            return toPath(path.pathStr);
        }

        @Override
        protected SVGPath doBackward(Path2D b) {
            return SVGPath.create(b.getPathIterator(null));
        }
    }
    
    //<editor-fold defaultstate="collapsed" desc="STATIC UTILITIES">
    
    /** Prints numbers w/ up to n digits of precision, removing trailing zeros */
    static String numStr(int prec, double val) {
        String res = String.format("%."+prec+"f", val);
        return res.indexOf('.') < 0 ? res : res.replaceAll("0*$", "").replaceAll("\\.$", "");
    }
    
    /** Prints a sequence of numbers with the specified joiner and precision */
    static String numStr(String join, int prec, double... vals) {
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

    /** Checks that the given string is a valid SVG path string. */
    static String checkSvgPathStr(String svg) {
        toPath(svg);
        return svg;
    }
    
    /** Converts SVG path string to a Java path */
    static GeneralPath toPath(String svg) {
        if (Strings.isNullOrEmpty(svg)) {
            return new GeneralPath();
        }
        String[] tokens = tokenizeSvgPath(svg);
        
        float[] start = {0f, 0f};
        float[] loc = {0f, 0f};
        float[] nextAnchor = {0f, 0f};

        GeneralPath gp = new GeneralPath();
        int pos = 0;
        while (pos < tokens.length) {
            SvgPathOperator op = operatorOf(tokens[pos].toLowerCase()); 
            float[] coords = nextFloats(tokens, pos+1);
            boolean relative = Character.isLowerCase(tokens[pos].charAt(0));
            op.apply(gp, coords, start, loc, nextAnchor, relative);
            pos += coords.length+1;
        }
        return gp;
    }
    
    /** Tokenize the path string, first making sure we have spaces in front of all numbers */
    private static String[] tokenizeSvgPath(String path) {
        return path.replaceAll("[A-Za-z]", " $0 ").trim()
                .replaceAll("[\\-]", " -")
                .split("[\\s,]+");
    }

    /** Get operator for given token */
    private static SvgPathOperator operatorOf(String token) {          
        checkArgument(token.length() == 1, "Invalid operator: "+token);
        for (SvgPathOperator op : SvgPathOperator.values()) {
            if (op.cmd == token.charAt(0)) {
                return op;
            }
        }
        checkArgument(false, "Invalid operator: "+token);
        return null;
    }
    
    /** 
     * Convert strings to floats, starting at given index, stopping at first non-float value
     * @param tokens array of strings
     * @param start starting index
     * @return coordinates extracted from string
     */
    private static float[] nextFloats(String[] tokens, int start) {
        List<Float> vals = Lists.newArrayList();
        for (int i = start; i < tokens.length; i++) {
            try {
                vals.add(Float.valueOf(tokens[i]));
            } catch (NumberFormatException ex) {
                LOG.log(Level.FINEST, "Not a float: " + tokens[i], ex);
                break;
            }
        }
        return Floats.toArray(vals);
    }
    
    /** See http://stackoverflow.com/questions/1805101/svg-elliptical-arcs-with-java. */
    private static void arcTo(GeneralPath path, float rx, float ry, float theta, boolean largeArcFlag, boolean sweepFlag, float x, float y) {
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
        
        AffineTransform rotation = AffineTransform.getRotateInstance(theta, cx, cy);
        path.append(rotation.createTransformedShape(arc), true);
    }

    private enum SvgPathOperator {
        MOVE('m') {
            @Override
            void apply(GeneralPath gp, float[] coords, float[] start, float[] loc, float[] nextAnchor, boolean relative) {
                for (int i = 0; i < coords.length-1; i+=2) {
                    loc[0] = relative ? loc[0]+coords[i] : coords[i];
                    loc[1] = relative ? loc[1]+coords[i+1] : coords[i+1];
                    if (i == 0) {
                        gp.moveTo(loc[0], loc[1]);
                    } else {
                        gp.lineTo(loc[0], loc[1]);
                    }
                }
                nextAnchor[0] = loc[0];
                nextAnchor[1] = loc[1];
                start[0] = loc[0];
                start[1] = loc[1];
            }
        },
        LINE('l') {
            @Override
            void apply(GeneralPath gp, float[] coords, float[] start, float[] loc, float[] nextAnchor, boolean relative) {
                for (int i = 0; i < coords.length-1; i+=2) {
                    loc[0] = relative ? loc[0]+coords[i] : coords[i];
                    loc[1] = relative ? loc[1]+coords[i+1] : coords[i+1];
                    gp.lineTo(loc[0], loc[1]);
                }
                nextAnchor[0] = loc[0];
                nextAnchor[1] = loc[1];
            }
        },
        HLINE('h') {
            @Override
            void apply(GeneralPath gp, float[] coords, float[] start, float[] loc, float[] nextAnchor, boolean relative) {
                for (int i = 0; i < coords.length; i++) {
                    loc[0] = relative ? loc[0]+coords[i] : coords[i];
                    gp.lineTo(loc[0], loc[1]);
                }
                nextAnchor[0] = loc[0];
                nextAnchor[1] = loc[1];
            }
        },
        VLINE('v') {
            @Override
            void apply(GeneralPath gp, float[] coords, float[] start, float[] loc, float[] nextAnchor, boolean relative) {
                for (int i = 0; i < coords.length; i++) {
                    loc[1] = relative ? loc[1]+coords[i] : coords[i];
                    gp.lineTo(loc[0], loc[1]);
                }
                nextAnchor[0] = loc[0];
                nextAnchor[1] = loc[1];
            }
        },
        CUBIC_CURVE('c') {
            @Override
            void apply(GeneralPath gp, float[] coords, float[] start, float[] loc, float[] nextAnchor, boolean relative) {
                for (int i = 0; i < coords.length-5; i+=6) {
                    float x1 = relative ? loc[0]+coords[i] : coords[i];
                    float y1 = relative ? loc[1]+coords[i+1] : coords[i+1];
                    float x2 = relative ? loc[0]+coords[i+2] : coords[i+2];
                    float y2 = relative ? loc[1]+coords[i+3] : coords[i+3];
                    loc[0] = relative ? loc[0]+coords[i+4] : coords[i+4];
                    loc[1] = relative ? loc[1]+coords[i+5] : coords[i+5];
                    gp.curveTo(x1, y1, x2, y2, loc[0], loc[1]);
                    nextAnchor[0] = 2*loc[0]-x2;
                    nextAnchor[1] = 2*loc[1]-y2;
                }
            }
        },
        SMOOTH_CURVE('s') {
            @Override
            void apply(GeneralPath gp, float[] coords, float[] start, float[] loc, float[] nextAnchor, boolean relative) {
                for (int i = 0; i < coords.length-3; i+=4) {
                    float x1 = nextAnchor[0];
                    float y1 = nextAnchor[1];
                    float x2 = relative ? loc[0]+coords[i] : coords[i];
                    float y2 = relative ? loc[1]+coords[i+1] : coords[i+1];
                    loc[0] = relative ? loc[0]+coords[i+2] : coords[i+2];
                    loc[1] = relative ? loc[1]+coords[i+3] : coords[i+3];
                    gp.curveTo(x1, y1, x2, y2, loc[0], loc[1]);
                    nextAnchor[0] = 2*loc[0]-x2;
                    nextAnchor[1] = 2*loc[1]-y2;
                }
            }
        },
        QUAD_CURVE('q') {
            @Override
            void apply(GeneralPath gp, float[] coords, float[] start, float[] loc, float[] nextAnchor, boolean relative) {
                for (int i = 0; i < coords.length-3; i+=4) {
                    float x1 = relative ? loc[0]+coords[i] : coords[i];
                    float y1 = relative ? loc[1]+coords[i+1] : coords[i+1];
                    loc[0] = relative ? loc[0]+coords[i+2] : coords[i+2];
                    loc[1] = relative ? loc[1]+coords[i+3] : coords[i+3];
                    gp.quadTo(x1, y1, loc[0], loc[1]);
                    nextAnchor[0] = 2*loc[0]-x1;
                    nextAnchor[1] = 2*loc[1]-y1;
                }
            }
        },
        SMOOTH_QUAD_CURVE('t') {
            @Override
            void apply(GeneralPath gp, float[] coords, float[] start, float[] loc, float[] nextAnchor, boolean relative) {
                for (int i = 0; i < coords.length-1; i+=2) {
                    float x1 = nextAnchor[0];
                    float y1 = nextAnchor[1];
                    loc[0] = relative ? loc[0]+coords[i] : coords[i];
                    loc[1] = relative ? loc[1]+coords[i+1] : coords[i+1];
                    gp.quadTo(x1, y1, loc[0], loc[1]);
                    nextAnchor[0] = 2*loc[0]-x1;
                    nextAnchor[1] = 2*loc[1]-y1;
                }
            }
        },
        ARC('a') {
            @Override
            void apply(GeneralPath gp, float[] coords, float[] start, float[] loc, float[] nextAnchor, boolean relative) {
                for (int i = 0; i < coords.length-6; i+=7) {
                    float rx = coords[i];
                    float ry = coords[i+1];
                    float xAxisRotation = coords[i+2];
                    boolean largeArcFlag = coords[i+3] == 1f;
                    boolean sweepFlag = coords[i+4] == 1f;
                    loc[0] = relative ? loc[0]+coords[i+5] : coords[i+5];
                    loc[1] = relative ? loc[1]+coords[i+6] : coords[i+6];
                    arcTo(gp, rx, ry, xAxisRotation, largeArcFlag, sweepFlag, loc[0], loc[1]);
                    nextAnchor[0] = loc[0];
                    nextAnchor[1] = loc[1];
                }
            }
        },
        CLOSE_PATH('z') {
            @Override
            void apply(GeneralPath gp, float[] coords, float[] start, float[] loc, float[] nextAnchor, boolean relative) {
                gp.closePath();
                loc[0] = start[0];
                loc[1] = start[1];
                nextAnchor[0] = start[0];
                nextAnchor[1] = start[1];
            }
        };
        
        private char cmd;

        private SvgPathOperator(char cmd) {
            this.cmd = cmd;
        }

        /**
         * Apply the SVG command, adding the results onto the path.
         * @param gp path to add results onto
         * @param coords coordinates associated with the current command
         * @param start starting location for current subpath (modified by move commands)
         * @param loc current location (modified by most commands)
         * @param nextAnchor next anchor location (modified for curve commands)
         * @param relative whether coordinates are relative (true) or absolute (false)
         */
        abstract void apply(GeneralPath gp, float[] coords, float[] start, float[] loc, float[] nextAnchor, boolean relative);
    }
    
}
