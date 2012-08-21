/*
 * GMouseIndexedHighlighter.java
 * Created on Jul 18, 2012
 */
package org.bm.blaise.graphics;

import java.awt.event.MouseEvent;
import org.bm.blaise.style.VisibilityKey;

/**
 *
 * @author petereb1
 */
public class GMouseIndexedHighlighter extends GMouseListener.Adapter {

    @Override
    public void mouseEntered(MouseEvent e) {
        Graphic g = ((GMouseEvent)e).graphic;
        if (g instanceof IndexedVisibilityGraphic && g.getVisibility() != VisibilityKey.Invisible) {
            IndexedVisibilityGraphic b = (IndexedVisibilityGraphic) g;
            int i = b.indexOf(e.getPoint(), null);
            b.setVisibility(i, VisibilityKey.Highlight);
        }
    }
    @Override
    public void mouseExited(MouseEvent e) {
        Graphic g = ((GMouseEvent)e).graphic;
        if (g instanceof IndexedVisibilityGraphic)
            ((IndexedVisibilityGraphic)g).setVisibility(-1, VisibilityKey.Regular);
    }

}
