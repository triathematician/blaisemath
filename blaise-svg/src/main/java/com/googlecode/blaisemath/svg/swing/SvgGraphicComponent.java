package com.googlecode.blaisemath.svg.swing;

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

    protected final SvgRootGraphic graphic = new SvgRootGraphic();

    public SvgGraphicComponent() {
        addGraphic(graphic);
    }
    
    /**
     * Create the component with the given SVG element
     * @param svg the svg
     * @return component
     */
    public static SvgGraphicComponent create(SvgRoot svg) {
        SvgGraphicComponent res = new SvgGraphicComponent();
        res.setElement(svg);
        return res;
    }
    
    /**
     * Create the component with the given SVG string
     * @param svg the svg
     * @return component
     * @throws IOException if SVG is invalid
     */
    public static SvgGraphicComponent create(String svg) throws IOException {
        SvgGraphicComponent res = new SvgGraphicComponent();
        res.setSvgText(svg);
        Rectangle2D bounds = res.graphic.getViewport();
        res.setPreferredSize(new Dimension((int) bounds.getWidth() + 1, (int) bounds.getHeight() + 1));
        return res;
    }

    //region PROPERTIES
    
    public SvgRoot getElement() {
        return graphic.getElement();
    }

    public void setElement(SvgRoot el) {
        graphic.setElement(el);
    }
    
    public String getSvgText() throws IOException {
        return SvgIo.writeToString(graphic.getElement());
    }
    
    public void setSvgText(String svg) throws IOException {
        setElement(SvgIo.read(svg));
    }

    public String tryGetSvgText() {
        try {
            return getSvgText();
        } catch (IOException x) {
            LOG.log(Level.WARNING, "Unable to save SVG", x);
            return "<svg/>";
        }
    }

    public void trySetSvgText(String svg) {
        try {
            setSvgText(svg);
        } catch (IOException x) {
            LOG.log(Level.WARNING, "Set SVG Failed", x);
        }
    }

    public boolean isRenderViewport() {
        return graphic.isRenderViewport();
    }

    public void setRenderViewport(boolean value) {
        graphic.setRenderViewport(value);
    }

    public boolean isRenderViewBox() {
        return graphic.isRenderViewBox();
    }

    public void setRenderViewBox(boolean value) {
        graphic.setRenderViewBox(value);
    }
    
    //endregion
    
}
