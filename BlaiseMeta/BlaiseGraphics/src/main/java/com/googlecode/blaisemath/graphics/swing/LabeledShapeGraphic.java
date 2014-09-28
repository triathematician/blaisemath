/*
 * LabeledShapeGraphic.java
 * Created on Oct 15, 2012
 */

package com.googlecode.blaisemath.graphics.swing;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2009 - 2014 Elisha Peterson
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



import com.google.common.base.Strings;
import com.googlecode.blaisemath.graphics.core.DelegatingPrimitiveGraphic;
import com.googlecode.blaisemath.style.AttributeSet;
import com.googlecode.blaisemath.style.ObjectStyler;
import com.googlecode.blaisemath.style.Renderer;
import com.googlecode.blaisemath.util.AnchoredText;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.RectangularShape;

/**
 * Customizable graphic that represents a labeled item.
 * Uses an {@link ObjectStyler} to customize appearance.
 *
 * @param <O> type of item
 *
 * @author petereb1
 */
public class LabeledShapeGraphic<O> extends DelegatingPrimitiveGraphic<O,Shape,Graphics2D> {

    private Renderer<AnchoredText, Graphics2D> textRenderer = new WrappedTextRenderer();
    
    public LabeledShapeGraphic() {
        this(null, new Rectangle(), new ObjectStyler<O>());
    }

    public LabeledShapeGraphic(O source, Shape primitive, ObjectStyler<O> styler) {
        super(source, primitive, styler, ShapeRenderer.getInstance());
    }
    
    //<editor-fold defaultstate="collapsed" desc="PROPERTIES">
    //
    // PROPERTIES
    //
    
    public Renderer<AnchoredText, Graphics2D> getTextRenderer() {
        return textRenderer;
    }

    public void setTextRenderer(Renderer<AnchoredText, Graphics2D> textRenderer) {
        if (this.textRenderer != textRenderer) {
            this.textRenderer = textRenderer;
            fireGraphicChanged();
        }
    }
    
    //</editor-fold>
    
    @Override
    public void renderTo(Graphics2D canvas) {
        super.renderTo(canvas);

        if (styler.getLabelDelegate() != null) {
            String label = styler.label(source);
            AttributeSet style = styler.labelStyle(source);
            if (!Strings.isNullOrEmpty(label) && style != null) {
                if (textRenderer instanceof WrappedTextRenderer) {
                    WrappedTextRenderer wtr = (WrappedTextRenderer) textRenderer;
                    wtr.setClipPath(primitive instanceof RectangularShape
                            ? (RectangularShape) primitive
                            : primitive.getBounds2D());
                }
                textRenderer.render(new AnchoredText(label), style, canvas);
            }
        }
    }

}
