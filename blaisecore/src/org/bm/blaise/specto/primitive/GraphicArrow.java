/**
 * GraphicArrow.java
 * Created on Sep 6, 2009
 */

package org.bm.blaise.specto.primitive;

import java.awt.geom.Point2D;

/**
 * <p>
 *   <code>GraphicArrow</code> is a graphics primitive that represents a head,
 *   given by an anchor point and a (dx,dy) for the head.
 * </p>
 *
 * @author Elisha Peterson
 */
public class GraphicArrow {

    Point2D anchor;
    Point2D head;

    /** Construct arrow with provided coords.
     * @param anchor point coordinate of the anchor
     * @param head point coordinate of the head (endpoint)
     */
    public GraphicArrow(Point2D anchor, Point2D head) {
        this.anchor = anchor;
        this.head = head;
    }

    public Point2D getAnchor() {
        return anchor;
    }

    public void setAnchor(Point2D anchor) {
        this.anchor = anchor;
    }

    public Point2D getHead() {
        return head;
    }

    public void setHead(Point2D vector) {
        this.head = vector;
    }

    public double magnitude() {
        return anchor.distance(head);
    }

    /**
     * Sets length of vector
     *
     * @param value
     * @param centered whether to scale length from center (<code>true</code>) or from anchor (<code>false</code>)
     */
    public void setMagnitude(double value, boolean centered) {
        double m = magnitude();
        double dx = head.getX() - anchor.getX();
        double dy = head.getY() - anchor.getY();
        if (centered) {
            double cx = (anchor.getX() + head.getX()) / 2;
            double cy = (anchor.getY() + head.getY()) / 2;
            anchor = new Point2D.Double(
                cx - dx * value / m / 2,
                cy - dy * value / m / 2);
            head = new Point2D.Double(
                cx + dx * value / m / 2,
                cy + dy * value / m / 2);
        } else {
            head = new Point2D.Double(
                anchor.getX() + dx * value / m,
                anchor.getY() + dy * value / m);
        }
    }
    
    public void setMagnitude(double value) {
        setMagnitude(value, false);
    }


    /**
     * Scales an array of vectors, ensuring that the maximum value of the length is less than specified max
     *
     * @param maxLength the maximum permissible length
     * @param exponent the exponent to scale the radius by (1 is linear, 0.5 makes fewer small circles, 2 makes fewer large circles)
     * @param centered <code>true</code> if the vectors should scale about their centers
     */
    public static void scaleVectors(GraphicArrow[] vectors, double maxLength, double exponent, boolean centered) {
        double mr = 0;
        for (int i = 0; i < vectors.length; i++) {
            mr = Math.max(mr, Math.abs(vectors[i].magnitude()));
        }
        for (int i = 0; i < vectors.length; i++) {
            vectors[i].setMagnitude(
                    maxLength * Math.signum(vectors[i].magnitude())
                    * Math.pow(Math.abs(vectors[i].magnitude()) / mr, exponent),
                    centered);
        }
    }

    @Override
    public String toString() {
        return "GraphicArrow[" + anchor.getX() + "," + anchor.getY() + " ; " + head.getX() + "," + head.getY() + "]";
    }
}
