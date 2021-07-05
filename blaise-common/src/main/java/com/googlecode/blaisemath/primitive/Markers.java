package com.googlecode.blaisemath.primitive;

/*-
 * #%L
 * blaise-common
 * --
 * Copyright (C) 2014 - 2021 Elisha Peterson
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


import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collections;
import java.util.List;
import java.util.ServiceLoader;

/**
 * Provides several custom shapes that can be used to draw points.
 *
 * @author Elisha Peterson
 */
public final class Markers {

    /** Caches markers loaded from resources file. */
    private static final List<Marker> MARKER_CACHE = Lists.newArrayList();
    /** Singleton for empty path. */
    private static final GeneralPath EMPTY_PATH = new GeneralPath();

    //region STATIC INSTANCES

    public static final BlankMarker BLANK = new BlankMarker();
    public static final CircleMarker CIRCLE = new CircleMarker();
    public static final SquareMarker SQUARE = new SquareMarker();
    public static final DiamondMarker DIAMOND = new DiamondMarker();
    public static final TriangleMarker TRIANGLE = new TriangleMarker();
    public static final StarMarker5 STAR = new StarMarker5();
    public static final StarMarker7 STAR7 = new StarMarker7();
    public static final StarMarker11 STAR11 = new StarMarker11();
    public static final PlusMarker PLUS = new PlusMarker();
    public static final CrossMarker CROSS = new CrossMarker();
    public static final TargetMarker TARGET = new TargetMarker();
    public static final ArrowMarker ARROW = new ArrowMarker();
    public static final GapArrowMarker GAP_ARROW = new GapArrowMarker();
    public static final ThickArrowMarker THICK_ARROW = new ThickArrowMarker();
    public static final ChevronMarker CHEVRON_MARKER = new ChevronMarker();
    public static final TriangleMarkerForward TRIANGLE_ARROW = new TriangleMarkerForward();
    public static final ArrowheadMarker ARROWHEAD = new ArrowheadMarker();
    public static final TeardropMarker TEARDROP = new TeardropMarker();
    public static final HappyFaceMarker HAPPYFACE = new HappyFaceMarker();
    public static final HouseMarker HOUSE = new HouseMarker();

    //endregion

    /**
     * Utility class
     */
    private Markers() {
    }

    /**
     * Retrieve list of available shapes.
     * @return list of marker constants
     */
    public static List<Marker> getAvailableMarkers() {
        if (MARKER_CACHE.isEmpty()) {
            ServiceLoader<Marker> loader = ServiceLoader.load(Marker.class);
            Iterables.addAll(MARKER_CACHE, loader);
        }
        return Collections.unmodifiableList(MARKER_CACHE);
    }

    /**
     * Blank marker.
     */
    public static class BlankMarker implements Marker {
        @Override
        public Shape create(Point2D p, double angle, float radius) {
            return EMPTY_PATH;
        }
    }

    /**
     * Circle marker.
     */
    public static class CircleMarker implements Marker {
        @Override
        public Shape create(Point2D p, double angle, float radius) {
            return new Ellipse2D.Double(p.getX() - radius, p.getY() - radius, 2 * radius, 2 * radius);
        }
    }

    /**
     * Square marker.
     */
    public static class SquareMarker implements Marker {
        @Override
        public Shape create(Point2D p, double angle, float radius) {
            return new Rectangle2D.Double(
                    p.getX() - radius / Math.sqrt(2),
                    p.getY() - radius / Math.sqrt(2),
                    2 * radius / Math.sqrt(2),
                    2 * radius / Math.sqrt(2));
        }
    }

    /**
     * Diamond marker.
     */
    public static class DiamondMarker implements Marker {
        @Override
        public Shape create(Point2D p, double angle, float radius) {
            double x = p.getX();
            double y = p.getY();
            GeneralPath path = new GeneralPath();
            path.moveTo((float) x, (float) (y - radius));
            path.lineTo((float) (x - radius), (float) y);
            path.lineTo((float) x, (float) (y + radius));
            path.lineTo((float) (x + radius), (float) y);
            path.closePath();
            return path;
        }
    }

    /**
     * Triangle marker, pointing up.
     */
    public static class TriangleMarker implements Marker {
        @Override
        public Shape create(Point2D p, double angle, float radius) {
            double x = p.getX();
            double y = p.getY();
            GeneralPath path = new GeneralPath();
            path.moveTo((float) x, (float) (y - radius));
            path.lineTo((float) (x + radius * Math.cos(Math.PI * 1.16667)),
                    (float) (y - radius * Math.sin(Math.PI * 1.16667)));
            path.lineTo((float) (x + radius * Math.cos(Math.PI * 1.83333)),
                    (float) (y - radius * Math.sin(Math.PI * 1.83333)));
            path.closePath();
            return path;
        }
    }

    /**
     * Five point star marker.
     */
    public static class StarMarker5 implements Marker {
        @Override
        public Shape create(Point2D p, double angle, float radius) {
            double x = p.getX();
            double y = p.getY();
            GeneralPath path = new GeneralPath();
            path.moveTo((float) x, (float) (y - radius));
            for (int i = 0; i < 5; i++) {
                double theta = Math.PI / 2 + 2 * Math.PI * i / 5;
                path.lineTo((float) (x + radius * Math.cos(theta)),
                        (float) (y - radius * Math.sin(theta)));
                theta += Math.PI / 5;
                path.lineTo((float) (x + radius / Math.sqrt(8) * Math.cos(theta)),
                        (float) (y - radius / Math.sqrt(8) * Math.sin(theta)));
            }
            path.closePath();
            return path;
        }
    }

    /**
     * Seven point star marker.
     */
    public static class StarMarker7 implements Marker {
        @Override
        public Shape create(Point2D p, double angle, float radius) {
            double x = p.getX();
            double y = p.getY();
            GeneralPath path = new GeneralPath();
            path.moveTo((float) x, (float) (y - radius));
            for (int i = 0; i < 7; i++) {
                double theta = Math.PI / 2 + 2 * Math.PI * i / 7;
                path.lineTo((float) (x + radius * Math.cos(theta)),
                        (float) (y - radius * Math.sin(theta)));
                theta += Math.PI / 7;
                path.lineTo((float) (x + radius / 2 * Math.cos(theta)),
                        (float) (y - radius / 2 * Math.sin(theta)));
            }
            path.closePath();
            return path;
        }
    }

    /**
     * Eleven point star marker.
     */
    public static class StarMarker11 implements Marker {
        @Override
        public Shape create(Point2D p, double angle, float radius) {
            double x = p.getX();
            double y = p.getY();
            GeneralPath path = new GeneralPath();
            path.moveTo((float) x, (float) (y - radius));
            for (int i = 0; i < 11; i++) {
                double theta = Math.PI / 2 + 2 * Math.PI * i / 11;
                path.lineTo((float) (x + radius * Math.cos(theta)),
                        (float) (y - radius * Math.sin(theta)));
                theta += Math.PI / 11;
                path.lineTo((float) (x + radius / 1.5 * Math.cos(theta)),
                        (float) (y - radius / 1.5 * Math.sin(theta)));
            }
            path.closePath();
            return path;
        }
    }

    /**
     * Plus marker.
     */
    public static class PlusMarker implements Marker {
        @Override
        public Shape create(Point2D p, double angle, float radius) {
            double x = p.getX();
            double y = p.getY();
            GeneralPath path = new GeneralPath();
            path.moveTo((float) x, (float) (y - radius));
            path.lineTo((float) x, (float) (y + radius));
            path.moveTo((float) (x - radius), (float) y);
            path.lineTo((float) (x + radius), (float) y);
            return new Area(new BasicStroke(radius/3).createStrokedShape(path));
        }
    }

    /**
     * Cross marker.
     */
    public static class CrossMarker implements Marker {
        @Override
        public Shape create(Point2D p, double angle, float radius) {
            double x = p.getX();
            double y = p.getY();
            GeneralPath path = new GeneralPath();
            double r2 = 0.7 * radius;
            path.moveTo((float) (x - r2), (float) (y - r2));
            path.lineTo((float) (x + r2), (float) (y + r2));
            path.moveTo((float) (x - r2), (float) (y + r2));
            path.lineTo((float) (x + r2), (float) (y - r2));
            return new Area(new BasicStroke(radius/3).createStrokedShape(path));
        }
    }

    /**
     * Target marker (with circle and crosshairs).
     */
    public static class TargetMarker implements Marker {
        @Override
        public Shape create(Point2D p, double angle, float radius) {
            double x = p.getX();
            double y = p.getY();
            GeneralPath path = new GeneralPath();
            path.moveTo((float) x, (float) (y - radius));
            path.lineTo((float) x, (float) (y + radius));
            path.moveTo((float) (x - radius), (float) y);
            path.lineTo((float) (x + radius), (float) y);
            path.append(new Ellipse2D.Double(x - .6 * radius, y - .6 * radius, 1.2 * radius, 1.2 * radius), false);
            return new Area(new BasicStroke(radius/6).createStrokedShape(path));
        }
    }

    /**
     * Arrow marker, pointing forward.
     */
    public static class GapArrowMarker implements Marker {
        @Override
        public Shape create(Point2D p, double angle, float radius) {
            double x = p.getX();
            double y = p.getY();
            GeneralPath path = new GeneralPath();
            path.moveTo((float) (x + .5 * radius), (float) (y - .5 * radius));
            path.lineTo((float) (x + radius), (float) y);
            path.lineTo((float) (x + .5*radius), (float) (y + .5*radius));
            path.moveTo((float) (x + .4*radius), (float) y);
            path.lineTo((float) (x - radius), (float) y);
            Shape wideShape = new Area(new BasicStroke(radius/4).createStrokedShape(path));
            return AffineTransform.getRotateInstance(angle, x, y).createTransformedShape(wideShape);
        }
    }

    /**
     * Arrow marker, pointing forward.
     */
    public static class ArrowMarker implements Marker {
        @Override
        public Shape create(Point2D p, double angle, float radius) {
            double x = p.getX();
            double y = p.getY();
            GeneralPath path = new GeneralPath();
            path.moveTo((float) (x + .5 * radius), (float) (y - .5 * radius));
            path.lineTo((float) (x + radius), (float) y);
            path.lineTo((float) (x + .5*radius), (float) (y + .5*radius));
            path.moveTo((float) (x + .8*radius), (float) y);
            path.lineTo((float) (x - radius), (float) y);
            Shape wideShape = new Area(new BasicStroke(radius/4).createStrokedShape(path));
            return AffineTransform.getRotateInstance(angle, x, y).createTransformedShape(wideShape);
        }
    }

    /**
     * Thicker arrow marker, pointing forward.
     */
    public static class ThickArrowMarker implements Marker {
        @Override
        public Shape create(Point2D p, double angle, float radius) {
            double x = p.getX();
            double y = p.getY();
            GeneralPath path = new GeneralPath();
            path.moveTo((float) (x + .5 * radius), (float) (y - .5 * radius));
            path.lineTo((float) (x + radius), (float) y);
            path.lineTo((float) (x + .5*radius), (float) (y + .5*radius));
            path.moveTo((float) (x + .6*radius), (float) y);
            path.lineTo((float) (x - radius), (float) y);
            Shape wideShape = new Area(new BasicStroke(radius/2).createStrokedShape(path));
            return AffineTransform.getRotateInstance(angle, x, y).createTransformedShape(wideShape);
        }
    }

    /**
     * Chevron marker, pointing forward.
     */
    public static class ChevronMarker implements Marker {

        @Override
        public Shape create(Point2D p, double angle, float radius) {
            double x = p.getX(), y = p.getY();
            GeneralPath path = new GeneralPath();
            path.moveTo((float) (x + .3 * radius), (float) (y - .5 * radius));
            path.lineTo((float) (x + .8 * radius), (float) y);
            path.lineTo((float) (x + .3 * radius), (float) (y + .5 * radius));
            path.moveTo((float) (x - .7 * radius), (float) (y - .5 * radius));
            path.lineTo((float) (x - .2 * radius), (float) y);
            path.lineTo((float) (x - .7 * radius), (float) (y + .5 * radius));
            Shape wideShape = new Area(new BasicStroke(radius/4).createStrokedShape(path));
            return AffineTransform.getRotateInstance(angle, x, y).createTransformedShape(wideShape);
        }
    }

    /**
     * Triangle marker, pointing forward.
     */
    public static class TriangleMarkerForward implements Marker {

        @Override
        public Shape create(Point2D p, double angle, float radius) {
            double x = p.getX(), y = p.getY();
            GeneralPath path = new GeneralPath();
            path.moveTo((float) (x + radius), (float) y);
            path.lineTo((float) (x + radius * Math.cos(Math.PI * 0.6667)),
                    (float) (y - radius * Math.sin(Math.PI * 0.6667)));
            path.lineTo((float) (x + radius * Math.cos(Math.PI * 1.3333)),
                    (float) (y - radius * Math.sin(Math.PI * 1.3333)));
            path.closePath();
            return AffineTransform.getRotateInstance(angle, x, y).createTransformedShape(path);
        }
    }

    /**
     * Arrowhead marker, pointing forward.
     */
    public static class ArrowheadMarker implements Marker {

        @Override
        public Shape create(Point2D p, double angle, float radius) {
            double x = p.getX(), y = p.getY();
            GeneralPath gp10 = new GeneralPath();
            gp10.moveTo((float) (x + radius), (float) y);
            gp10.lineTo((float) (x - radius), (float) (y + radius));
            gp10.lineTo((float) (x - .5 * radius), (float) y);
            gp10.lineTo((float) (x - radius), (float) (y - radius));
            gp10.closePath();
            return AffineTransform.getRotateInstance(angle, x, y).createTransformedShape(gp10);
        }
    }

    /**
     * Teardrop marker, pointing forward.
     */
    public static class TeardropMarker implements Marker {

        @Override
        public Shape create(Point2D p, double angle, float radius) {
            double x = p.getX(), y = p.getY();
            GeneralPath gp11 = new GeneralPath();
            gp11.moveTo(-.25f, -.5f);
            gp11.curveTo(-1f, -.5f, -1f, .5f, -.25f, .5f);
            gp11.curveTo(.5f, .5f, .5f, 0, 1f, 0);
            gp11.curveTo(.5f, 0, .5f, -.5f, -.2f, -.5f);
            gp11.closePath();
            gp11.transform(new AffineTransform(radius, 0, 0, radius, x, y));
            return AffineTransform.getRotateInstance(angle, x, y).createTransformedShape(gp11);
        }
    }

    /**
     * Happy face marker.
     */
    public static class HappyFaceMarker implements Marker {

        @Override
        public Shape create(Point2D p, double angle, float radius) {
            double x = p.getX(), y = p.getY();
            Area a = new Area(new Ellipse2D.Double(x - radius, y - radius, 2 * radius, 2 * radius));
            a.subtract(new Area(new Ellipse2D.Double(x - radius / 3 - radius / 6, y - radius / 2, radius / 3, radius / 3)));
            a.subtract(new Area(new Ellipse2D.Double(x + radius / 3 - radius / 6, y - radius / 2, radius / 3, radius / 3)));
            a.subtract(new Area(new Arc2D.Double(x - radius / 2, y - radius / 2, radius, radius, 200, 140, Arc2D.CHORD)));
            return a;
        }
    }

    /**
     * House-shaped marker.
     */
    public static class HouseMarker implements Marker {

        @Override
        public Shape create(Point2D p, double angle, float radius) {
            double x = p.getX(), y = p.getY();
            GeneralPath gp13 = new GeneralPath();
            gp13.moveTo(-.9f, -.9f);
            gp13.lineTo(.9f, -.9f);
            gp13.lineTo(.9f, .4f);
            gp13.lineTo(1f, .4f);
            gp13.lineTo(.75f, .625f);
            gp13.lineTo(.75f, 1f);
            gp13.lineTo(.5f, 1f);
            gp13.lineTo(.5f, .75f);
            gp13.lineTo(0f, 1f);
            gp13.lineTo(-1f, .4f);
            gp13.lineTo(-.9f, .4f);
            gp13.lineTo(-.9f, -.9f);
            gp13.closePath();
            gp13.transform(new AffineTransform(radius, 0, 0, -radius, x, y));
            return gp13;
        }
    }

}
