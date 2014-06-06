/*
 * TextPathStyle.java
 * Created Dec 2010
 */
package org.blaise.style;

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

import static com.google.common.base.Preconditions.*;
import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Shape;

/**
 * <p>
 *   This style draws the specified text along a path, rather than drawing a regular path.
 * </p>
 * 
 * @author petereb1
 */
public class TextPathStyle implements ShapeStyle {

    protected TextStyleBasic textStyle = new TextStyleBasic();
    protected String pathText = "LABEL";
    protected boolean stretch = false;
    
    /** Default constructor. */
    public TextPathStyle() {
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
     * Set text style & return pointer to object
     * @param style the style
     * @return this
     */
    public TextPathStyle textStyle(TextStyleBasic style) {
        setTextStyle(style);
        return this;
    }
    
    /**
     * Set path text & return pointer to object
     * @param text the new text
     * @return this
     */
    public TextPathStyle pathText(String text) {
        setPathText(text);
        return this;
    }
    
    /**
     * Set "stretch" attribute & return point to object
     * @param stretch new stretch value
     * @return this
     */
    public TextPathStyle stretch(boolean stretch) {
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
    public TextStyleBasic getTextStyle() {
        return textStyle;
    }
    
    /**
     * Set style of text
     * @param style new style
     */
    public void setTextStyle(TextStyleBasic style) {
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
    public void draw(Shape primitive, Graphics2D canvas, StyleHintSet hints) {
        canvas.setFont(textStyle.getFont());
        canvas.setStroke(new TextStroke(pathText, textStyle.getFont(), stretch, false));
        canvas.setColor(textStyle.getFill());
        canvas.draw(primitive);
        canvas.setStroke(new BasicStroke());
    }

}
