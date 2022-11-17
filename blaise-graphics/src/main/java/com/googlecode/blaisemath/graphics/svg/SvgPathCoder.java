package com.googlecode.blaisemath.graphics.svg;

/*-
 * #%L
 * blaise-graphics
 * --
 * Copyright (C) 2009 - 2022 Elisha Peterson
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

import com.google.common.base.Strings;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.google.common.primitives.Floats;
import com.googlecode.blaisemath.encode.StringDecoder;
import com.googlecode.blaisemath.encode.StringEncoder;

import java.awt.*;
import java.awt.geom.*;
import java.text.DecimalFormat;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Converts Java paths to/from SVG path strings.
 * @author Elisha Peterson
 */
public class SvgPathCoder implements StringEncoder<Path2D>, StringDecoder<Path2D> {

    private static final Logger LOG = Logger.getLogger(SvgPathCoder.class.getName());
    private static final DecimalFormat DF = new DecimalFormat("#.######");
    private static final DecimalFormat DF_LARGE = new DecimalFormat("#.######E0");

    // TODO - is this reasonable, given that Shape objects are not immutable?
    private static final LoadingCache<Shape, String> PATH_CACHE = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .build(new CacheLoader<>() {
                @Override
                public String load(Shape shape) {
                    return encodeIterator(shape.getPathIterator(null));
                }
            });

    @Override
    public String encode(Path2D obj) {
        return PATH_CACHE.getUnchecked(obj);
    }

    public String encodeShapePath(Shape obj) {
        return PATH_CACHE.getUnchecked(obj);
    }

    /**
     * Encode path iterator to an SVG path string.
     * @param pi iterator
     * @return path string
     */
    public static String encodeIterator(PathIterator pi) {
        float[] cur = new float[6];
        int curSegmentType;
        StringBuilder pathString = new StringBuilder();
        while (!pi.isDone()) {
            curSegmentType = pi.currentSegment(cur);
            switch (curSegmentType) {
                case PathIterator.SEG_MOVETO:
                    pathString.append("M ").append(numStr6(" ", cur[0], cur[1])).append(" ");
                    break;
                case PathIterator.SEG_LINETO:
                    pathString.append("L ").append(numStr6(" ", cur[0], cur[1])).append(" ");
                    break;
                case PathIterator.SEG_QUADTO:
                    pathString.append("Q ").append(numStr6(" ", cur[0], cur[1], cur[2], cur[3])).append(" ");
                    break;
                case PathIterator.SEG_CUBICTO:
                    pathString.append("C ").append(numStr6(" ", cur[0], cur[1], cur[2], cur[3], cur[4], cur[5])).append(" ");
                    break;
                case PathIterator.SEG_CLOSE:
                    pathString.append("Z");
                    break;
                default:
                    break;
            }
            pi.next();
        }
        return pathString.toString().trim();
    }

    @Override
    public Path2D decode(String svg) {
        if (Strings.isNullOrEmpty(svg)) {
            return new Path2D.Float();
        }
        String[] tokens = tokenizeSvgPath(svg);
        
        float[] start = {0f, 0f};
        float[] loc = {0f, 0f};
        float[] nextAnchor = {0f, 0f};

        Path2D.Float gp = new Path2D.Float();
        int pos = 0;
        while (pos < tokens.length) {
            SvgPathOperator op = operatorOf(tokens[pos].toLowerCase()); 
            float[] coordinates = nextFloats(tokens, pos+1);
            boolean relative = Character.isLowerCase(tokens[pos].charAt(0));
            op.apply(gp, coordinates, start, loc, nextAnchor, relative);
            pos += coordinates.length+1;
        }
        return gp;
    }
    
    //region STATIC UTILITIES

    /** Prints a sequence of numbers with the specified joiner and precision */
    static String numStr6(String join, double... vals) {
        if (vals.length == 0) {
            return "";
        }
        StringBuilder res = new StringBuilder();
        res.append(numStr6(vals[0]));
        for (int i = 1; i < vals.length; i++) {
            res.append(join).append(numStr6(vals[i]));
        }
        return res.toString();
    }

    /** Prints numbers w/ up to 6 digits of precision, removing trailing zeros */
    static String numStr6(double val) {
        return Math.abs(val) < 1E7 ? DF.format(val) : DF_LARGE.format(val);
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
        List<Float> values = Lists.newArrayList();
        for (int i = start; i < tokens.length; i++) {
            try {
                values.add(Float.valueOf(tokens[i]));
            } catch (NumberFormatException ex) {
                LOG.log(Level.FINEST, "Not a float: " + tokens[i], ex);
                break;
            }
        }
        return Floats.toArray(values);
    }
    
    /** See http://stackoverflow.com/questions/1805101/svg-elliptical-arcs-with-java. */
    private static void arcTo(Path2D.Float path, float rx, float ry, float theta, boolean largeArcFlag, boolean sweepFlag, float x, float y) {
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
    
    //endregion
    
    //region INNER CLASSES

    /** Helps decode based on operator type. */
    private enum SvgPathOperator {
        MOVE('m') {
            @Override
            void apply(Path2D.Float gp, float[] coords, float[] start, float[] loc, float[] nextAnchor, boolean relative) {
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
            void apply(Path2D.Float gp, float[] coords, float[] start, float[] loc, float[] nextAnchor, boolean relative) {
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
            void apply(Path2D.Float gp, float[] coords, float[] start, float[] loc, float[] nextAnchor, boolean relative) {
                for (float coord : coords) {
                    loc[0] = relative ? loc[0] + coord : coord;
                    gp.lineTo(loc[0], loc[1]);
                }
                nextAnchor[0] = loc[0];
                nextAnchor[1] = loc[1];
            }
        },
        VLINE('v') {
            @Override
            void apply(Path2D.Float gp, float[] coords, float[] start, float[] loc, float[] nextAnchor, boolean relative) {
                for (float coord : coords) {
                    loc[1] = relative ? loc[1] + coord : coord;
                    gp.lineTo(loc[0], loc[1]);
                }
                nextAnchor[0] = loc[0];
                nextAnchor[1] = loc[1];
            }
        },
        CUBIC_CURVE('c') {
            @Override
            void apply(Path2D.Float gp, float[] coords, float[] start, float[] loc, float[] nextAnchor, boolean relative) {
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
            void apply(Path2D.Float gp, float[] coords, float[] start, float[] loc, float[] nextAnchor, boolean relative) {
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
            void apply(Path2D.Float gp, float[] coords, float[] start, float[] loc, float[] nextAnchor, boolean relative) {
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
            void apply(Path2D.Float gp, float[] coords, float[] start, float[] loc, float[] nextAnchor, boolean relative) {
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
            void apply(Path2D.Float gp, float[] coords, float[] start, float[] loc, float[] nextAnchor, boolean relative) {
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
            void apply(Path2D.Float gp, float[] coords, float[] start, float[] loc, float[] nextAnchor, boolean relative) {
                gp.closePath();
                loc[0] = start[0];
                loc[1] = start[1];
                nextAnchor[0] = start[0];
                nextAnchor[1] = start[1];
            }
        };
        
        private char cmd;

        SvgPathOperator(char cmd) {
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
        abstract void apply(Path2D.Float gp, float[] coords, float[] start, float[] loc, float[] nextAnchor, boolean relative);
    }
    
    //endregion

}
