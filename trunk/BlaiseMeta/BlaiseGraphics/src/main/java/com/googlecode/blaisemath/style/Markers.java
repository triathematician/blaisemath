/*
 * ShapeLibrary.java
 * Created Jan 8, 2011
 */
package com.googlecode.blaisemath.style;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2009 - 2014 Elisha Peterson
 * --
 * Licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
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

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.List;

/**
 * Provides several custom shapes that can be used to draw points.
 *
 * TODO - configure these shapes in a resource file, or load them dynamically
 *
 * @author Elisha Peterson
 */
public final class Markers {

    public static final NoShape NONE = new NoShape();
    public static final CircleShape CIRCLE = new CircleShape();
    public static final SquareShape SQUARE = new SquareShape();
    public static final DiamondShape DIAMOND = new DiamondShape();
    public static final TriangleShape TRIANGLE = new TriangleShape();
    public static final StarShape STAR = new StarShape();
    public static final Star7Shape STAR7 = new Star7Shape();
    public static final Star11Shape STAR11 = new Star11Shape();
    public static final PlusShape PLUS = new PlusShape();
    public static final CrossShape CROSS = new CrossShape();
    public static final CrosshairsShape CROSSHAIRS = new CrosshairsShape();
    public static final HappyFaceShape HAPPYFACE = new HappyFaceShape();
    public static final HouseShape HOUSE = new HouseShape();
    public static final SimpleArrowShape ARROW = new SimpleArrowShape();
    public static final TrianglePointerShape TRIANGLE_ARROW = new TrianglePointerShape();
    public static final TriangleFlagShape ARROWHEAD = new TriangleFlagShape();
    public static final TeardropShape TEARDROP = new TeardropShape();
    public static final CarShape CAR = new CarShape();

    static final GeneralPath EMPTY_PATH = new GeneralPath();

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
        return Arrays.asList(
                NONE, CIRCLE, SQUARE, DIAMOND, TRIANGLE,
                STAR, STAR7, STAR11,
                PLUS, CROSS, CROSSHAIRS,
                HAPPYFACE, HOUSE,
                ARROW, TRIANGLE_ARROW, ARROWHEAD, TEARDROP,
                CAR
        );
    }

    /**
     * Blank shape, draws nothing
     */
    public static class NoShape implements Marker {

        @Override
        public Shape create(Point2D p, double angle, float radius) {
            return EMPTY_PATH;
        }
    }

    /**
     * Circle centered at point
     */
    public static class CircleShape implements Marker {

        @Override
        public Shape create(Point2D p, double angle, float radius) {
            return new Ellipse2D.Double(p.getX() - radius, p.getY() - radius, 2 * radius, 2 * radius);
        }
    }

    /**
     * Square
     */
    public static class SquareShape implements Marker {

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
     * Diamond
     */
    public static class DiamondShape implements Marker {

        @Override
        public Shape create(Point2D p, double angle, float radius) {
            double x = p.getX(), y = p.getY();
            GeneralPath gp = new GeneralPath();
            gp.moveTo((float) x, (float) (y - radius));
            gp.lineTo((float) (x - radius), (float) y);
            gp.lineTo((float) x, (float) (y + radius));
            gp.lineTo((float) (x + radius), (float) y);
            gp.closePath();
            return gp;
        }
    }

    /**
     * Triangle, with peak pointed up
     */
    public static class TriangleShape implements Marker {

        @Override
        public Shape create(Point2D p, double angle, float radius) {
            double x = p.getX(), y = p.getY();
            GeneralPath gp2 = new GeneralPath();
            gp2.moveTo((float) x, (float) (y - radius));
            gp2.lineTo((float) (x + radius * Math.cos(Math.PI * 1.16667)), 
                    (float) (y - radius * Math.sin(Math.PI * 1.16667)));
            gp2.lineTo((float) (x + radius * Math.cos(Math.PI * 1.83333)), 
                    (float) (y - radius * Math.sin(Math.PI * 1.83333)));
            gp2.closePath();
            return gp2;
        }
    }

    /**
     * 5-Pointed Star
     */
    public static class StarShape implements Marker {

        @Override
        public Shape create(Point2D p, double angle, float radius) {
            double x = p.getX(), y = p.getY();
            GeneralPath gp5 = new GeneralPath();
            gp5.moveTo((float) x, (float) (y - radius));
            for (int i = 0; i < 5; i++) {
                double theta = Math.PI / 2 + 2 * Math.PI * i / 5;
                gp5.lineTo((float) (x + radius * Math.cos(theta)), 
                        (float) (y - radius * Math.sin(theta)));
                theta += Math.PI / 5;
                gp5.lineTo((float) (x + radius / Math.sqrt(8) * Math.cos(theta)), 
                        (float) (y - radius / Math.sqrt(8) * Math.sin(theta)));
            }
            gp5.closePath();
            return gp5;
        }
    }

    /**
     * 7-Pointed Star
     */
    public static class Star7Shape implements Marker {

        @Override
        public Shape create(Point2D p, double angle, float radius) {
            double x = p.getX(), y = p.getY();
            GeneralPath gp6 = new GeneralPath();
            gp6.moveTo((float) x, (float) (y - radius));
            for (int i = 0; i < 7; i++) {
                double theta = Math.PI / 2 + 2 * Math.PI * i / 7;
                gp6.lineTo((float) (x + radius * Math.cos(theta)), 
                        (float) (y - radius * Math.sin(theta)));
                theta += Math.PI / 7;
                gp6.lineTo((float) (x + radius / 2 * Math.cos(theta)), 
                        (float) (y - radius / 2 * Math.sin(theta)));
            }
            gp6.closePath();
            return gp6;
        }
    }

    /**
     * 11-Pointed Star
     */
    public static class Star11Shape implements Marker {

        @Override
        public Shape create(Point2D p, double angle, float radius) {
            double x = p.getX(), y = p.getY();
            GeneralPath gp7 = new GeneralPath();
            gp7.moveTo((float) x, (float) (y - radius));
            for (int i = 0; i < 11; i++) {
                double theta = Math.PI / 2 + 2 * Math.PI * i / 11;
                gp7.lineTo((float) (x + radius * Math.cos(theta)), 
                        (float) (y - radius * Math.sin(theta)));
                theta += Math.PI / 11;
                gp7.lineTo((float) (x + radius / 1.5 * Math.cos(theta)), 
                        (float) (y - radius / 1.5 * Math.sin(theta)));
            }
            gp7.closePath();
            return gp7;
        }
    }

    /**
     * A "+" shape
     */
    public static class PlusShape implements Marker {

        @Override
        public Shape create(Point2D p, double angle, float radius) {
            double x = p.getX(), y = p.getY();
            GeneralPath gp3 = new GeneralPath();
            gp3.moveTo((float) x, (float) (y - radius));
            gp3.lineTo((float) x, (float) (y + radius));
            gp3.moveTo((float) (x - radius), (float) y);
            gp3.lineTo((float) (x + radius), (float) y);
            return gp3;
        }
    }

    /**
     * A "x" shape
     */
    public static class CrossShape implements Marker {

        @Override
        public Shape create(Point2D p, double angle, float radius) {
            double x = p.getX(), y = p.getY();
            GeneralPath gp4 = new GeneralPath();
            double r2 = 0.7 * radius;
            gp4.moveTo((float) (x - r2), (float) (y - r2));
            gp4.lineTo((float) (x + r2), (float) (y + r2));
            gp4.moveTo((float) (x - r2), (float) (y + r2));
            gp4.lineTo((float) (x + r2), (float) (y - r2));
            return gp4;
        }
    }

    /**
     * Cross-hairs (path only, no fill)
     */
    public static class CrosshairsShape implements Marker {

        @Override
        public Shape create(Point2D p, double angle, float radius) {
            double x = p.getX(), y = p.getY();
            GeneralPath gp3 = new GeneralPath();
            gp3.moveTo((float) x, (float) (y - radius));
            gp3.lineTo((float) x, (float) (y + radius));
            gp3.moveTo((float) (x - radius), (float) y);
            gp3.lineTo((float) (x + radius), (float) y);
            gp3.append(new Ellipse2D.Double(x - .6 * radius, y - .6 * radius, 1.2 * radius, 1.2 * radius), false);
            return gp3;
        }
    }

    /**
     * Happy face shape
     */
    public static class HappyFaceShape implements Marker {

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
     * House shape
     */
    public static class HouseShape implements Marker {

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

    /**
     * Simple arrow (path only)
     */
    public static class SimpleArrowShape implements Marker {

        @Override
        public Shape create(Point2D p, double angle, float radius) {
            double x = p.getX(), y = p.getY();
            GeneralPath gp = new GeneralPath();
            gp.moveTo((float) (x + radius * Math.cos(Math.PI * 0.667)), 
                    (float) (y - radius * Math.sin(Math.PI * 0.6667)));
            gp.lineTo((float) x, (float) y);
            gp.moveTo((float) x, (float) y);
            gp.lineTo((float) (x + radius * Math.cos(Math.PI * 0.667)),
                    (float) (y + radius * Math.sin(Math.PI * 0.6667)));
            return AffineTransform.getRotateInstance(angle, x, y).createTransformedShape(gp);
        }
    }

    /**
     * Triangle shape (pointed to the side)
     */
    public static class TrianglePointerShape implements Marker {

        @Override
        public Shape create(Point2D p, double angle, float radius) {
            double x = p.getX(), y = p.getY();
            GeneralPath gp9 = new GeneralPath();
            gp9.moveTo((float) (x + radius), (float) y);
            gp9.lineTo((float) (x + radius * Math.cos(Math.PI * 0.6667)), 
                    (float) (y - radius * Math.sin(Math.PI * 0.6667)));
            gp9.lineTo((float) (x + radius * Math.cos(Math.PI * 1.3333)), 
                    (float) (y - radius * Math.sin(Math.PI * 1.3333)));
            gp9.closePath();
            return AffineTransform.getRotateInstance(angle, x, y).createTransformedShape(gp9);
        }
    }

    /**
     * Triangle shape (flag)
     */
    public static class TriangleFlagShape implements Marker {

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
     * Teardrop shape
     */
    public static class TeardropShape implements Marker {

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
     * Car shape
     */
    public static class CarShape implements Marker {

        @Override
        public Shape create(Point2D p, double angle, float radius) {
            double x = p.getX(), y = p.getY();
            GeneralPath gp12 = new GeneralPath();
            gp12.moveTo(1f, 0);
            gp12.lineTo(.67f, 0);
            // top
            gp12.append(new Arc2D.Double(-.33f, -.5f, 1f, 1f, 0, 180, Arc2D.OPEN), true);
            // hood
            gp12.append(new Arc2D.Double(-.83f, 0f, 1f, .67f, 90, 90, Arc2D.OPEN), true); 
            // bumper
            gp12.append(new Arc2D.Double(-1f, .33f, .33f, .33f, 90, 90, Arc2D.OPEN), true); 
            gp12.lineTo(-.7f, .5f);
            // wheel well
            gp12.append(new Arc2D.Double(-.7f, .3f, .4f, .4f, 180, -180, Arc2D.OPEN), true); 
            gp12.lineTo(.3f, .5f);
            // wheel well
            gp12.append(new Arc2D.Double(.3f, .3f, .4f, .4f, 180, -180, Arc2D.OPEN), true); 
            gp12.lineTo(1f, .5f);
            gp12.closePath();
            Area a = new Area(gp12);

            // windows
            a.subtract(new Area(new Arc2D.Double(-.2f, -.4f, .7f, .6f, 90, 90, Arc2D.PIE))); 
            a.subtract(new Area(new Arc2D.Double(-.05f, -.4f, .6f, .6f, 0, 90, Arc2D.PIE))); 

            // wheels
            a.add(new Area(new Ellipse2D.Double(-.67f, .33f, .33f, .33f))); 
            a.add(new Area(new Ellipse2D.Double(.33f, .33f, .33f, .33f)));
            a.transform(new AffineTransform(-radius, 0, 0, radius, x, y));
            return AffineTransform.getRotateInstance(angle, x, y).createTransformedShape(a);
        }
    }
}
