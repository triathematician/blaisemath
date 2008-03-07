/*
 * TransformedVisometry.java
 * Created on Mar 6, 2008
 */

package specto;

import java.awt.Point;
import java.awt.geom.Point2D.Double;
import java.util.Vector;
import scio.coordinate.Coordinate;

/**
 * <p>
 * TransformedVisometry is a visometry based on another visometry that "translates coordinates" using a particular coordinate system.
 * </p>
 * @author Elisha Peterson
 */
public class TransformedVisometry<V extends Visometry> extends Visometry {
    Transformer t;
    Visometry v;

    public TransformedVisometry(Visometry v,Transformer t){
        this.t=t;
        this.v=v;
    }
    
    @Override
    public Double toWindow(Coordinate cp) {return v.toWindow(t.transform(cp));}
    @Override
    public Coordinate toGeometry(Point wp) {return t.inverseTransform(v.toGeometry(wp));}
    @Override
    public void setBounds(Coordinate minPoint, Coordinate maxPoint) {v.setBounds(minPoint, maxPoint);}
    @Override
    public double computeTransformation() {return v.computeTransformation();}
    @Override
    public Vector getMenuItems() {return v.getMenuItems();}
}
