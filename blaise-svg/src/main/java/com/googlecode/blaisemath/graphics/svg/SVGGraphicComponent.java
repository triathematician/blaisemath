package com.googlecode.blaisemath.graphics.svg;

/*
 * #%L
 * BlaiseSVG
 * --
 * Copyright (C) 2014 - 2017 Elisha Peterson
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

import com.googlecode.blaisemath.graphics.swing.JGraphicComponent;
import com.googlecode.blaisemath.svg.SVGElement;

/**
 * Swing component for displaying a single SVG element or elements.
 * 
 * @author petereb1
 */
public class SVGGraphicComponent extends JGraphicComponent {

    protected final transient SVGGraphic graphic = new SVGGraphic();

    public SVGGraphicComponent() {
        addGraphic(graphic);
    }

    //<editor-fold defaultstate="collapsed" desc="PROPERTIES">
    //
    // PROPERTIES
    //
    
    public SVGElement getElement() {
        return graphic.getElement();
    }

    public void setElement(SVGElement el) {
        graphic.setElement(el);
    }
    
    //</editor-fold>
    
}
