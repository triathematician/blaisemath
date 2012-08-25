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
 * Class that turns on highlight key whenever the mouse is over the graphic.
 * </p>
 * @author elisha
 */
public class GraphicHighlighter extends MouseAdapter {
    
    @Override
    public void mouseEntered(MouseEvent e) {
        Graphic g = ((GraphicMouseEvent)e).graphic;
        g.setVisibilityHint(VisibilityHint.Highlight, true);
    }
    
    @Override
    public void mouseExited(MouseEvent e) {
        Graphic g = ((GraphicMouseEvent)e).graphic;
        g.setVisibilityHint(VisibilityHint.Highlight, false);
    }

}
