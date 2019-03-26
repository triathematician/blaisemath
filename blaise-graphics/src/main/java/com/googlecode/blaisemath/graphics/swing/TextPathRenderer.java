/*
 * TextPathStyle.java
 * Created Dec 2010
 */
package com.googlecode.blaisemath.graphics.swing;

/*
 * #%L
 * BlaiseGraphics
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

import static com.google.common.base.Preconditions.checkNotNull;
import com.googlecode.blaisemath.style.AttributeSet;
import com.googlecode.blaisemath.style.Styles;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;

/**
 * <p>
 *   This style draws the specified text along a path, rather than drawing a regular path.
 * </p>
 * 
 * @author petereb1
 */
public class TextPathRenderer extends PathRenderer {

    protected AttributeSet textStyle = new AttributeSet();
    protected String pathText = "LABEL";
    protected boolean stretch = false;
    
    /** Default constructor. */
    public TextPathRenderer() {
    }
    
    @Override
    public String toString() {
        return String.format("TextPathStyle[textStyle=%s, pathText=%s, stretch=%s",
                textStyle, pathText, stretch);
    }
    
    
    //<editor-fold defaultstate="collapsed" desc="BUILDER PATTERNS">
    //
    // BUILDER PATTERNS
    //
    
    /**
     * Set text style and return pointer to object
     * @param style the style
     * @return this
     */
    public TextPathRenderer textStyle(AttributeSet style) {
        setTextStyle(style);
        return this;
    }
    
    /**
     * Set path text and return pointer to object
     * @param text the new text
     * @return this
     */
    public TextPathRenderer pathText(String text) {
        setPathText(text);
        return this;
    }
    
    /**
     * Set "stretch" attribute and return point to object
     * @param stretch new stretch value
     * @return this
     */
    public TextPathRenderer stretch(boolean stretch) {
        setStretch(stretch);
        return this;
    }    
    
    //</editor-fold>
    
    
    //<editor-fold defaultstate="collapsed" desc="PROPERTY PATTERNS">
    //
    // PROPERTY PATTERNS
    //
    
    /**
     * Get style of text
     * @return text style
     */
    public AttributeSet getTextStyle() {
        return textStyle;
    }
    
    /**
     * Set style of text
     * @param style new style
     */
    public void setTextStyle(AttributeSet style) {
        this.textStyle = checkNotNull(style);
    }
    
    /**
     * Get text drawn on shape
     * @return text
     */
    public String getPathText() {
        return pathText;
    }
    
    /**
     * Set text drawn on shape
     * @param text the new text
     */
    public void setPathText(String text) {
        this.pathText = checkNotNull(text);
    }
    
    /**
     * Get stretch attribute
     * @return stretch
     */
    public boolean isStretch() {
        return stretch;
    }
    
    /**
     * Set stretch attribute
     * @param stretch new stretch value
     */
    public void setStretch(boolean stretch) {
        this.stretch = stretch;
    }
    
    //</editor-fold>
    

    @Override
    public void render(Shape primitive, AttributeSet style, Graphics2D canvas) {
        Font f = Styles.fontOf(textStyle);
        canvas.setFont(f);
        canvas.setStroke(new TextStroke(pathText, f, stretch, false));
        canvas.setColor(textStyle.getColor(Styles.FILL));
        canvas.draw(primitive);
    }

}
