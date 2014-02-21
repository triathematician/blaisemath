/**
 * GraphicHighlighter.java
 * Created Jul 31, 2012
 */
package org.blaise.graphics;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import org.blaise.style.VisibilityHint;

/**
 * <p>
 * Turns on highlight when the mouse is over the graphic, turns off when it leaves.
 * </p>
 * @author elisha
 */
public class GraphicHighlightHandler extends MouseAdapter {

    @Override
    public void mouseEntered(MouseEvent e) {
        Graphic g = ((GraphicMouseEvent)e).getGraphicSource();
        if (!(g instanceof GraphicSupport) || ((GraphicSupport)g).isHighlightEnabled()) {
            g.getVisibilityHints().add(VisibilityHint.HIGHLIGHT);
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
        Graphic g = ((GraphicMouseEvent)e).getGraphicSource();
        g.getVisibilityHints().remove(VisibilityHint.HIGHLIGHT);
    }

}
