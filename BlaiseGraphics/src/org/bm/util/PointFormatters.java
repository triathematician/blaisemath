/*
 * PointFormatters.java
 * Created Jan 30, 2011
 */

package org.bm.util;

import java.awt.geom.Point2D;

/**
 * Provides various utilities.
 * 
 * @author elisha
 */
public final class PointFormatters { 
    private PointFormatters() {}

    /**
     * Formats a point with n decimal places
     * @param p the point to format
     * @param n number of decimal places
     */
    public static String formatPoint(Point2D p, int n) {
        return String.format("(%."+n+"f, %."+n+"f)", p.getX(), p.getY());
    }

}
