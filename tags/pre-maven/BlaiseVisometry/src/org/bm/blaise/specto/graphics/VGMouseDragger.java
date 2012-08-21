/*
 * VGMouseDragger.java
 * Created Jan 12, 2011
 */
package org.bm.blaise.specto.graphics;

import org.bm.util.DraggablePointBean;

/**
 * Implementation of an object dragger using a bean pattern
 *
 * @author elisha
 */
public class VGMouseDragger<C> extends VGMouseListener.Dragger<C> {

    DraggablePointBean<C> bean;
    C beanStart;

    public VGMouseDragger(DraggablePointBean<C> b) {
        this.bean = b;
    }

    @Override
    public void mouseDragInitiated(VGMouseEvent<C> e, C start) {
        beanStart = bean.getPoint();
    }

    @Override
    public void mouseDragInProgress(VGMouseEvent<C> e, C start) {
        bean.setPoint(beanStart, start, e.local);
    }

}
