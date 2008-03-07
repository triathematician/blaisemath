/*
 * LogarithmicTransformer.java
 * Created on Mar 6, 2008
 */

package specto.transformer;

import scio.coordinate.Coordinate;
import scio.coordinate.R2;
import specto.Transformer;

/**
 * LogarithmicTransformer is a class which converts between log coordinates and regular coordinates.
 * 
 * @author Elisha Peterson
 */
public class LogarithmicTransformer extends Transformer<R2,R2> {
    boolean logX;
    boolean logY;

    public LogarithmicTransformer(boolean logX, boolean logY) {
        this.logX = logX;
        this.logY = logY;
    }    
    
    public R2 transform(R2 c) {
        double xResult=(logX)?Math.log(c.x):c.x;
        double yResult=(logY)?Math.log(c.y):c.y;
        return new R2(xResult,yResult);
    }

    public R2 inverseTransform(R2 c) {
        double xResult=(logX)?Math.exp(c.x):c.x;
        double yResult=(logY)?Math.exp(c.y):c.y;
        return new R2(xResult,yResult);
    }
}
