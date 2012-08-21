/*
 * IndexedGraphicHighlighter.java
 * Created on Jul 18, 2012
 */
package org.blaise.graphics;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import org.blaise.style.VisibilityHint;

/**
 *
 * @author petereb1
 */
public class IndexedGraphicHighlighter extends MouseAdapter {

    @Override
    public void mouseEntered(MouseEvent e) {
        Graphic g = ((GraphicMouseEvent)e).graphic;
        if (g instanceof IndexedVisibilityGraphic && g.getVisibility() != VisibilityHint.Hidden) {
            IndexedVisibilityGraphic b = (IndexedVisibilityGraphic) g;
            int i = b.indexOf(e.getPoint(), null);
            b.setVisibility(i, VisibilityHint.Highlight);
        }
    }
    @Override
    public void mouseExited(MouseEvent e) {
        Graphic g = ((GraphicMouseEvent)e).graphic;
        if (g instanceof IndexedVisibilityGraphic)
            ((IndexedVisibilityGraphic)g).setVisibility(-1, VisibilityHint.Regular);
    }

}
