/*
 * Transformer.java
 * Created on Sep 14, 2007, 7:46:26 AM
 */

package specto;

import scio.coordinate.Coordinate;

/**
 * This interface is more than meets the eye. Okay, well it handles transformations between
 * two underlying geometries, geometry "from" and geometry "to".
 * <br><br>
 * @author ae3263
 */
public abstract class Transformer<C extends Coordinate,D extends Coordinate> {
    public abstract D transform(C c);
    public abstract C inverseTransform(D c);
    public Transformer getInverseTransform(){
        return new Transformer<D,C>(){
            @Override
            public C transform(D c) {return Transformer.this.inverseTransform(c);}
            @Override
            public D inverseTransform(C c) {return Transformer.this.transform(c);}            
        };
    }
}
