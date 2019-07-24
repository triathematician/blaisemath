package com.googlecode.blaisemath.graphics.svg;

/*-
 * #%L
 * blaise-graphics
 * --
 * Copyright (C) 2009 - 2019 Elisha Peterson
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

import com.google.common.annotations.Beta;
import com.googlecode.blaisemath.graphics.Graphic;
import com.googlecode.blaisemath.style.AttributeSet;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

/**
 * A graphic that represents SVG.
 * @author Elisha Peterson
 */
@Beta
public abstract class SvgGraphic extends Graphic<Graphics2D> {
    
    /** Size of the content. */
    private Dimension size = new Dimension(100, 100);
    /** View box of the content to be drawn (in SVG coordinates) */
    private Rectangle2D viewBox;
    /** Boundary of view box in target canvas. */
    private Rectangle2D canvasBounds;
    /** Style string. */
    private AttributeSet style = new AttributeSet();
    
    //region PROPERTIES

    public Dimension getSize() {
        return size;
    }

    public void setSize(Dimension size) {
        this.size = size;
        fireGraphicChanged();
    }
    
    public int getWidth() {
        return size.width;
    }
    
    public int getHeight() {
        return size.height;
    }

    public Rectangle2D getViewBox() {
        return viewBox;
    }

    public void setViewBox(Rectangle2D viewBox) {
        this.viewBox = viewBox;
        fireGraphicChanged();
    }

    public Rectangle2D getCanvasBounds() {
        return canvasBounds;
    }

    public void setCanvasBounds(Rectangle2D bounds) {
        this.canvasBounds = bounds;
        fireGraphicChanged();
    }

    @Override
    public AttributeSet getStyle() {
        return style;
    }

    public void setStyle(AttributeSet style) {
        this.style = style;
        fireGraphicChanged();
    }
    
    //endregion

}
