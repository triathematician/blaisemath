/*
 * VGMouseIndexedDragger.java
 * Created Jan 12, 2011
 */
package org.bm.blaise.specto.graphics;

import org.bm.util.DraggableIndexedPointBean;

/**
 * Implementation of an object dragger using an indexed bean pattern.
 *
 * @author elisha
 */
public class VGMouseIndexedDragger<C> extends VGMouseListener.Dragger<C> {

    private DraggableIndexedPointBean<C> bean;
    private transient C beanStart;
    private transient int indexStart;

    public VGMouseIndexedDragger(DraggableIndexedPointBean<C> b) {
        this.bean = b;
    }

    @Override
    public void mouseDragInitiated(VGMouseEvent<C> e, C start) {
        indexStart = bean.indexOf(e.getPoint(), start);
        if (indexStart != -1)
            beanStart = bean.getPoint(indexStart);
    }

    @Override
    public void mouseDragInProgress(VGMouseEvent<C> e, C start) {
        if (indexStart != -1)
            bean.setPoint(indexStart, beanStart, start, e.local);
    }

    /**
     * Returns the index of the (most recently) active coordinate that is being dragged
     * @return index, or -1 if there is none
     */
    public int getActiveIndex() {
        return indexStart;
    }

}
