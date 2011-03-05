/**
 * IndexedGetter.java
 * Created Jan 29, 2011
 */
package utils;

import java.util.List;

/**
 * A generic, parametrized indexed getter.
 * @author Elisha
 */
public interface IndexedGetter<T> {
    /** @return the number of points */
    public int getSize();
    /** @return the i'th point */
    public T getElement(int i);

    public static class ListInstance<T> implements IndexedGetter<T> {
        private final List<T> list;
        public ListInstance(List<T> list) { this.list = list; }
        public int getSize() { return list.size(); }
        public T getElement(int i) { return list.get(i); }
    }
}
