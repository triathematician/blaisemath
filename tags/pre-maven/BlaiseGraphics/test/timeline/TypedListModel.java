/**
 * TypedListModel.java
 * Created Jul 20, 2012
 */
package timeline;

import javax.swing.ListModel;

/**
 * <p>
 *  A list model whose elements have a known data type.
 * </p>
 * @author elisha
 */
public interface TypedListModel<D> extends ListModel {
    
    public D getElementAt(int index);
    
}
