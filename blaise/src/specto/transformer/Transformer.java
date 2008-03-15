/*
 * Transformer.java
 * Created on Sep 14, 2007, 7:46:26 AM
 */

package specto.transformer;

import scio.coordinate.Coordinate;
import specto.Visometry;

/**
 * This interface is more than meets the eye. Okay, well it handles transformations between
 * two underlying geometries, geometry "from" and geometry "to".
 * 
 * @author ae3263
 */
public abstract class Transformer<V1 extends Visometry,C1 extends Coordinate,V2 extends Visometry,C2 extends Coordinate>{
    public abstract C1 transform(C2 c);
    public abstract C2 inverseTransform(C1 c);
    public abstract V1 transform(V2 v);
    public abstract V2 inverseTransform(V2 v);
    public Transformer getInverseTransform(){
        return new Transformer<V2,C2,V1,C1>(){
            @Override
            public C2 transform(C1 c) {return Transformer.this.inverseTransform(c);}
            @Override
            public C1 inverseTransform(C2 c) {return Transformer.this.transform(c);}

            @Override
            public V2 transform(V1 v) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public V1 inverseTransform(V1 v) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };
    }
}
