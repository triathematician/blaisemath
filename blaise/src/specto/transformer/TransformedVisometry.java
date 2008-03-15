/*
 * TransformedVisometry.java
 * Created on Mar 6, 2008
 */

package specto.transformer;

import java.awt.Point;
import java.awt.geom.Point2D.Double;
import java.util.Vector;
import scio.coordinate.Coordinate;
import scio.coordinate.V2;
import specto.*;
import specto.transformer.Transformer;

/**
 * <p>
 * TransformedVisometry is a visometry based on another visometry that "translates coordinates" using a particular coordinate system.
 * </p>
 * @author Elisha Peterson
 */
public class TransformedVisometry<V1 extends Visometry,V2 extends Visometry> extends Visometry {
    Transformer t;
    V1 v;
    
    public TransformedVisometry(V1 v,Transformer t){
        this.v=v;
        this.t=t;
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

