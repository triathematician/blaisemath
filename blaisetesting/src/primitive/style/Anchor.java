/**
 * Anchor.java
 * Created Sep 2009
 */

package primitive.style;

/**
 * Anchor point constants.
 */
public enum Anchor {
    Center(0), 
    West(0),
    Northwest(0.25 * Math.PI),
    North(0.5 * Math.PI),
    Northeast(0.75 * Math.PI),
    East(Math.PI),
    Southeast(1.25 * Math.PI),
    South(1.5 * Math.PI),
    Southwest(1.75 * Math.PI);

    /** Represents the relative offset angle for the specified anchor */
    public double angle;

    /** Construct with specified angle */
    Anchor(double angle) { this.angle = angle; }
}
