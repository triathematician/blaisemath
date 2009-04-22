
package Function;

import Euclidean.PPoint;

/*
 * VectorField.java
 *
 * Created on March 1, 2007, 12:09 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

public abstract class VectorField {
    public VectorField(){}
    public PPoint get(PPoint point){return new PPoint(0,0);} 
}
