/*
 * PathStyleBasic.java
 * Created Jan 12, 2011
 */

package com.googlecode.blaisemath.style;

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

import com.googlecode.blaisemath.style.context.StyleHintSet;
import com.googlecode.blaisemath.util.ColorUtils;
import static com.google.common.base.Preconditions.checkNotNull;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;

/**
 * Draws a path with arbitrary color and width.
 *
 * @author Elisha
 */
public class PathStyleBasic implements PathStyle {

    protected Color stroke = Color.black;
    protected float strokeWidth = 1f;

    public PathStyleBasic() {
    }
    
    @Override
    public String toString() {
        return String.format("PathStyleBasic[stroke=%s, strokeWidth=%.1f]", 
                stroke, strokeWidth);
    }
    
    //<editor-fold defaultstate="collapsed" desc="BUILDER PATTERNS">

    public PathStyleBasic stroke(Color stroke) {
        setStroke(stroke);
        return this;
    }

    public PathStyleBasic strokeWidth(float width) {
        setStrokeWidth(width);
        return this;
    }
    
    //</editor-fold>
    
    
    //<editor-fold defaultstate="collapsed" desc="PROPERTY PATTERNS">
    //
    // PROPERTY PATTERNS
    //

    @Override
    public Color getStroke() {
        return stroke;
    }

    public void setStroke(Color stroke) {
        this.stroke = checkNotNull(stroke);
    }

    @Override
    public float getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(float thickness) {
        this.strokeWidth = thickness;
    }
    
    //</editor-fold>

    
    @Override
    public Shape shapeOfPath(Shape primitive) {
        return new BasicStroke(strokeWidth).createStrokedShape(primitive);
    }

    @Override
    public void draw(Shape s, Graphics2D canvas) {
        if(getStrokeWidth() <= 0f || getStroke() == null) {
            return;
        }

        canvas.setStroke(new BasicStroke(getStrokeWidth()));
        canvas.setColor(getStroke());
        canvas.draw(s);

        canvas.setStroke(Styles.DEFAULT_STROKE);
    }

}
