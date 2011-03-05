/**
 * MapGetter.java
 * Created Jan 29, 2011
 */
package utils;

import java.util.List;
import java.util.Map;

/**
 * Provides a generic "getter" method that returns an object of specified type.
 * @param <T> return type of getter
 * @author Elisha
 */
public interface MapGetter<T> {

    /**
     * Look up entry for specified object. Implementations should never throw
     * an exception, instead returning null when necessary.
     * @return the object corresponding to o, or null if there is none
     */
    public T getElement(Object o);

    /** Wraps a map as a MapGetter */
    public static class MapInstance<T> implements MapGetter<T> {
        private final Map<?,T> base;
        public MapInstance(Map<?,T> base) { this.base = base; }
        public T getElement(Object o) { return base.get(o); }
    }

    /** Uses a mapgetter and another indexed getter to generate an indexed getter */
    public static class IndexedAdapter<O,T> implements IndexedGetter<T> {
        private final IndexedGetter<O> base;
        private final MapGetter<T> index;
        public IndexedAdapter(IndexedGetter<O> base, MapGetter<T> index) {
            this.base = base;
            this.index = index;
        }
        public IndexedAdapter(List<O> base, MapGetter<T> index) {
            this.base = new IndexedGetter.ListInstance<O>(base);
            this.index = index;
        }
        public IndexedAdapter(IndexedGetter<O> base, Map<?,T> index) {
            this.base = base;
            this.index = new MapInstance(index);
        }
        public int getSize() { return base.getSize(); }
        public T getElement(int i) { return index.getElement(base.getElement(i)); }
        public MapGetter<T> getMap() { return index; }
    }

}
