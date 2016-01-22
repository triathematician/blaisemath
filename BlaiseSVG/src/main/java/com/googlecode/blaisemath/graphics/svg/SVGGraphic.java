/**
 * SVGGraphic.java
 * Created Sep 27, 2014
 */

package com.googlecode.blaisemath.graphics.svg;

/*
 * #%L
 * BlaiseSVG
 * --
 * Copyright (C) 2014 - 2016 Elisha Peterson
 * --
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
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

import static com.google.common.base.Preconditions.checkNotNull;
import com.googlecode.blaisemath.graphics.core.Graphic;
import com.googlecode.blaisemath.graphics.core.GraphicComposite;
import com.googlecode.blaisemath.graphics.swing.JGraphicComponent;
import com.googlecode.blaisemath.svg.SVGElement;
import java.awt.Graphics2D;
import java.util.Collections;

/**
 * <p>
 *   Uses an {@link SVGElement} as a primitive to be rendered on a
 *   {@link JGraphicComponent}.
 * </p>
 * @author elisha
 */
public class SVGGraphic extends GraphicComposite<Graphics2D> {
    
    private SVGElement element;
    private Graphic<Graphics2D> primitiveElement;
    
    public static SVGGraphic create(SVGElement element) {
        checkNotNull(element);
        SVGGraphic res = new SVGGraphic();
        res.setElement(element);
        return res;
    }
    
    private void updateGraphics() {
        Graphic<Graphics2D> nue = SVGElementGraphicConverter.getInstance()
                .convert(element);
        if (primitiveElement == null) {
            addGraphic(nue);
        } else {
            replaceGraphics(Collections.singleton(primitiveElement), Collections.singleton(nue));
        }
        primitiveElement = nue;
    }
    
    //<editor-fold defaultstate="collapsed" desc="PROPERTY PATTERNS">
    //
    // PROPERTY PATTERNS
    //
    
    public SVGElement getElement() {
        return element;
    }
    
    public void setElement(SVGElement el) {
        Object old = this.element;
        if (old != el) {
            this.element = el;
            updateGraphics();
        }
    }
    
    //</editor-fold>

}
