package com.googlecode.blaisemath.svg;

/*
 * #%L
 * BlaiseSVG
 * --
 * Copyright (C) 2014 - 2020 Elisha Peterson
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
import com.googlecode.blaisemath.svg.xml.SvgElement;
import com.googlecode.blaisemath.svg.xml.SvgIo;
import com.googlecode.blaisemath.svg.xml.SvgRoot;

import java.awt.Dimension;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Swing component for displaying a single SVG element or elements.
 *
 * @author Elisha Peterson
 */
public class SvgGraphicComponent extends JGraphicComponent {

    private static final Logger LOG = Logger.getLogger(SvgGraphicComponent.class.getName());

    protected final SvgElementGraphic graphic = new SvgElementGraphic();

    public SvgGraphicComponent() {
        addGraphic(graphic);
    }
    
    /**
     * Create the component with the given SVG element
     * @param svg the svg
     * @return component
     */
    public static SvgGraphicComponent create(SvgElement svg) {
        SvgGraphicComponent res = new SvgGraphicComponent();
        res.setElement(svg);
        return res;
    }
    
    /**
     * Create the component with the given SVG string
     * @param svg the svg
     * @return component
     */
    public static SvgGraphicComponent create(String svg) {
        SvgGraphicComponent res = new SvgGraphicComponent();
        res.setSvgText(svg);
        Rectangle2D bounds = res.graphic.getCanvasBounds();
        if (bounds != null) {
            res.setPreferredSize(new Dimension((int) bounds.getWidth() + 1, (int) bounds.getHeight() + 1));
        }
        return res;
    }

    //region PROPERTIES
    
    public SvgElement getElement() {
        return graphic.getElement();
    }

    public void setElement(SvgElement el) {
        graphic.setElement(el);
    }
    
    public String getSvgText() {
        try {
            return SvgIo.writeToString(graphic.getElement());
        } catch (IOException x) {
            LOG.log(Level.WARNING, "Unable to save SVG", x);
            return "<svg/>";
        }
    }
    
    public void setSvgText(String svg) {
        try {
            setElement(SvgIo.read(svg));
        } catch (IOException x) {
            LOG.log(Level.WARNING, "Set SVG Failed", x);
        }
    }
    
    //endregion
    
}
