/**
 * LabeledPointGraphic.java
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



import com.google.common.base.Strings;
import com.googlecode.blaisemath.style.AttributeSet;
import com.googlecode.blaisemath.style.ObjectStyler;
import com.googlecode.blaisemath.style.Renderer;
import com.googlecode.blaisemath.util.AnchoredText;
import java.awt.Point;
import java.awt.geom.Point2D;

/**
 * Uses an {@link ObjectStyler} and a source object to draw a labeled point on a
 * canvas. The style of the point and label is managed by the styler, along with
 * the tooltip.
 *
 * @param <O> source object type
 * @param <G> graphics canvas type
 * @author Elisha
 */
public class LabeledPointGraphic<O,G> extends DelegatingPrimitiveGraphic<O,Point2D,G> {
    
    public static final String LABEL_RENDERER_PROP = "labelRenderer";

    private Renderer<AnchoredText,G> textRenderer;
    
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
    public Renderer<AnchoredText, G> getLabelRenderer() {
        return textRenderer;
    }

    public void setLabelRenderer(Renderer<AnchoredText, G> textRenderer) {
        if (this.textRenderer != textRenderer) {
            Object old = this.textRenderer;
            this.textRenderer = textRenderer;
            pcs.firePropertyChange(LABEL_RENDERER_PROP, old, textRenderer);
        }
    }
    
    //</editor-fold>
    
    /**
     * Return label, if its visible.
     * @return label, or null if there is none visible
     */
    private String visibleLabel() {
        if (styler.getLabelDelegate() == null || getLabelRenderer() == null
                || (styler.getLabelFilter() != null && !styler.getLabelFilter().apply(source))) {
            return null;
        }
        String label = styler.label(source);
        return Strings.isNullOrEmpty(label) ? null : label;
    }
    
    @Override
    public void renderTo(G canvas) {
        super.renderTo(canvas);

        String label = visibleLabel();
        if (label != null) {
            AttributeSet style = styler.labelStyle(source);
            if (style != null) {
                AnchoredText alabel = new AnchoredText(primitive, label);
                getLabelRenderer().render(alabel, style, canvas);
            }
        }
    }


}
