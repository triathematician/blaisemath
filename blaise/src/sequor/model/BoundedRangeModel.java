/**
 * BoundedRangeModel.java
 * Created on Mar 3, 2008
 */

package sequor.model;

import java.util.Vector;

/**
 * Abstract class for models which have the ability to increment their value and get/set the number of steps. These should eventually be compatible with
 * the RangeTimer method.
 * @author Elisha Peterson
 */
public abstract class BoundedRangeModel<V> extends FiresChangeEvents {
    public abstract boolean increment(boolean loop);    
    public abstract void setNumSteps(int numSteps,boolean inclusive);
    public abstract int getNumSteps();    
    public abstract Vector<V> getValueRange(boolean inclusive,V shift);
}
