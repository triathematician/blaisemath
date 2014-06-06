/**
 * GraphicHighlighter.java
 * Created Jul 31, 2012
 */
package org.blaise.graphics;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2009 - 2014 Elisha Peterson
 * --
 * Licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import org.blaise.style.StyleHints;

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
            g.getStyleHints().add(StyleHints.HIGHLIGHT_HINT);
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
        Graphic g = ((GraphicMouseEvent)e).getGraphicSource();
        g.getStyleHints().remove(StyleHints.HIGHLIGHT_HINT);
    }

}
