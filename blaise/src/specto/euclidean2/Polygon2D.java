/*
 * Triangle2D.java
 * Created on Mar 6, 2008
 */

package specto.euclidean2;

/**
 * <p>
 * Triangle2D is ...
 * </p>
 * @author Elisha Peterson
 */
public class Polygon2D extends DynamicPointSet2D {
    int sides;
    public Polygon2D(){this(5);}
    public Polygon2D(int sides){
        this.sides=sides;
        for(int i=0;i<sides;i++){
            add(new Point2D(Math.cos(i*2*Math.PI/sides),Math.sin(i*2*Math.PI/sides)));
        }
        style.setIValue(STYLE_FILLED);
    }
}
