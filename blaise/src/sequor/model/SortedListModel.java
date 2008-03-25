/**
 * SortedListModel.java
 * Created on Mar 24, 2008
 */

package sequor.model;

import java.beans.PropertyChangeEvent;
import java.util.Set;
import java.util.TreeSet;
import sequor.FiresChangeEvents;

/**
 * Maintains a sorted list of elements.
 * @author Elisha Peterson
 */
public class SortedListModel<O> extends FiresChangeEvents {
    public TreeSet<O> list;
    
    public SortedListModel(Set<O> objects){setList(objects);}
    
    
    // BEAN PATTERNS
    
    public void setList(Set<O> newList){
        TreeSet<O> testList=new TreeSet<O>(newList);
        if(!list.equals(testList)){
            list=testList;
            fireStateChanged();
        }
    }
    TreeSet<O> getList() {return list;}
    

    @Override
    public FiresChangeEvents clone() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void copyValuesFrom(FiresChangeEvents parent) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setValue(String s) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public PropertyChangeEvent getChangeEvent(String s) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
