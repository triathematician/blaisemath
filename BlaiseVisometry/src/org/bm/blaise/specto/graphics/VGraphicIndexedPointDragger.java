/*
 * VGraphicIndexedPointDragger.java
 * Created Jan 12, 2011
 */
package org.bm.blaise.specto.graphics;

import org.bm.util.DraggableIndexedPointBean;

/** 
 * Implementation of an object dragger using an indexed bean pattern.
 *
 * @author elisha
 */
public class VGraphicIndexedPointDragger<C> extends VGraphicMouseListener.Dragger<C> {
    
    private DraggableIndexedPointBean<C> bean;
    private transient C beanStart;
    private transient int indexStart;

    public VGraphicIndexedPointDragger(DraggableIndexedPointBean<C> b) {
        this.bean = b;
    }

    @Override
    public void mouseDragInitiated(VGraphicMouseEvent<C> e, C start) {
        indexStart = bean.indexOf(e.getPoint(), start);
        if (indexStart != -1)
            beanStart = bean.getPoint(indexStart);
    }

    @Override
    public void mouseDragInProgress(VGraphicMouseEvent<C> e, C start) {
        if (indexStart != -1)
            bean.setPoint(indexStart, beanStart, start, e.local);
    }
    
}
