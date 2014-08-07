/**
 * DelegatingPointGraphic.java
 * Created Aug 21, 2012
 */
package com.googlecode.blaisemath.graphics.core;

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



import com.googlecode.blaisemath.graphics.core.DelegatingPrimitiveGraphic;
import com.google.common.base.Strings;
import com.googlecode.blaisemath.style.Renderer;
import com.googlecode.blaisemath.util.geom.LabeledPoint;
import com.googlecode.blaisemath.graphics.swing.TextRenderer;
import com.googlecode.blaisemath.style.ObjectStyler;
import com.googlecode.blaisemath.style.AttributeSet;
import java.awt.Point;
import java.awt.geom.Point2D;

/**
 * Uses an {@link ObjectStyler} and a source object to draw a labeled point on a canvas.
 *
 * @param <O> source object type
 * @author Elisha
 */
public class LabeledPointGraphic<O,G> extends DelegatingPrimitiveGraphic<O,Point2D,G> {
    
    public static final String LABEL_RENDERER_PROP = "labelRenderer";

    private Renderer<LabeledPoint,G> textRenderer;
    
    public LabeledPointGraphic() {
        this(null, new Point(), new ObjectStyler<O>());
    }

    public LabeledPointGraphic(O source, Point2D primitive, ObjectStyler<O> styler) {
        super(source, primitive, styler, null);
    }
    
    //<editor-fold defaultstate="collapsed" desc="PROPERTY PATTERNS">
    //
    // PROPERTY PATTERNS
    //
    public Renderer<LabeledPoint, G> getLabelRenderer() {
        return textRenderer;
    }

    public void setLabelRenderer(Renderer<LabeledPoint, G> textRenderer) {
        if (this.textRenderer != textRenderer) {
            Object old = this.textRenderer;
            this.textRenderer = textRenderer;
            pcs.firePropertyChange(LABEL_RENDERER_PROP, old, textRenderer);
        }
    }
    
    //</editor-fold>
    
    
    @Override
    public void renderTo(G canvas) {
        super.renderTo(canvas);

        if (styler.getLabelDelegate() != null) {
            String label = styler.label(source);
            if (!Strings.isNullOrEmpty(label)) {
                AttributeSet style = styler.labelStyle(source);
                if (style != null) {
                    LabeledPoint alabel = new LabeledPoint(primitive, label);
                    getLabelRenderer().render(alabel, style, canvas);
                }
            }
        }
    }


}
