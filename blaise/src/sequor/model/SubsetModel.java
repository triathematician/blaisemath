/**
 * SubsetModel.java
 * Created on May 22, 2008
 */

package sequor.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

/**
 * This class maintains a subset of a collection of elements. This can be done as an extension of the
 * <b>BooleanModelGroup</b> class.
 * 
 * @author Elisha Peterson
 */
public class SubsetModel<V> extends BooleanModelGroup {
    
    /** Stores selection values of each element in the underlying set. */
    HashMap<V,BooleanModel> modelMap;

    /** Default constructor
     * @param set a collection of objects representing the complete set to choose from
     */
    public SubsetModel(Collection<V> set) {
        super(false, false);
        initSet(set);
    }
    
    /** Initializes this model to a particular set of elements.
     * @param set a collection of objects representing the complete set to choose from.
     */
    public void initSet(Collection<V> set) {
        models.clear();
        if(modelMap == null) { 
            modelMap = new HashMap<V, BooleanModel> ();
        } else {
            modelMap.clear();
        }
        BooleanModel bm;
        for(V v : set){
            bm = new BooleanModel(true);
            modelMap.put(v, bm);
            add(bm);
        }
    }
    
    /** Returns collection of objects representing the complete set.
     *  @return Collection<V> of objects of given data type
     */
    public Collection<V> getSet() { return modelMap.keySet(); }
    
    /** Accesses a particular underlying model.
     * @param obj the set element used for the model
     * @return BooleanModel corresponding to the input
     */
    public BooleanModel getModel(V v){ return modelMap.get(v); }
    
    /** Returns subset represented by this class.
     * @return HashSet of given data type
     */
    public HashSet<V> getSubset() {
        HashSet<V> result = new HashSet<V>();
        for(V v: modelMap.keySet()){
            if(modelMap.get(v).isTrue()) { result.add(v); }
        }
        return result;
    }
    
    /** Returns subset represented by this class.
     * @return HashSet of given data type
     */
    public HashSet<V> getComplement() {
        HashSet<V> result = new HashSet<V>();
        for(V v: modelMap.keySet()){
            if(!modelMap.get(v).isTrue()) { result.add(v); }
        }
        return result;
    }
}
