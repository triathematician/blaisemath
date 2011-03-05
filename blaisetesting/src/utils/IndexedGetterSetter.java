package utils;

/**
 * Gets and sets elements by index
 */
public interface IndexedGetterSetter<C> extends IndexedGetter<C> {
    /** Sets the i'th point */
    public void setElement(int i, C point);

    /** Allows setting by relative amount from a start position */
    public static interface Relative<C> extends IndexedGetterSetter<C> {
        /** Sets the i'th point by relative movement from an initial point */
        public void setElement(int i, C initial, C dragStart, C dragFinish);
    }
}
