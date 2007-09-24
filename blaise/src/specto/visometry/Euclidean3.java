/*
 * Euclidean.java
 * Created on Sep 14, 2007, 8:10:03 AM
 */

package specto.visometry;

import java.awt.Point;
import java.awt.geom.AffineTransform;
import javax.swing.JPanel;
import specto.Coordinate;
import specto.Visometry;
import specto.coordinate.R3;

/**
 * This class handles coordinate transformations betwen standard 3D Cartesian coordinates
 * and the display window.
 * <br><br>
 * @author Elisha Peterson
 */
public class Euclidean3 extends Visometry<R3> {

// PROPERTIES
    AffineTransform at;

// CONSTANTS


// CONSTRUCTORS

    /** Default constructor */
    public Euclidean3(){}

    public Point toWindow(R3 cp) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public R3 toGeometry(Point wp) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setBounds(R3 minPoint, R3 maxPoint) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public double computeTransformation() {
        throw new UnsupportedOperationException("Not supported yet.");
    }


// BEAN PATTERNS: GETTERS & SETTERS


// METHODS:


}
