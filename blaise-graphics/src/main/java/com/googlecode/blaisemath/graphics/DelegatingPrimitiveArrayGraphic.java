package com.googlecode.blaisemath.graphics;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2009 - 2025 Elisha Peterson
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

import com.googlecode.blaisemath.style.AttributeSet;
import com.googlecode.blaisemath.style.ObjectStyler;
import java.awt.geom.Point2D;
import java.util.Set;
import javax.swing.JPopupMenu;

/**
 * A graphic that maintains a source object and uses an {@link ObjectStyler}
 * delegate to retrieve its style set.
 * 
 * @param <S> type of source object
 * @param <O> type of primitive
 * @param <G> type of graphics canvas to render to
 * 
 * @author Elisha Peterson
 */
public class DelegatingPrimitiveArrayGraphic<S,O,G> extends PrimitiveArrayGraphicSupport<O,G> {
   
    /** The source object */
    protected S source;
    /** The style set for this graphic */
    protected ObjectStyler<S> styler;

    public DelegatingPrimitiveArrayGraphic() {
    }

    public DelegatingPrimitiveArrayGraphic(S source, O[] primitive, ObjectStyler<S> styler, Renderer<O, G> renderer) {
        setPrimitive(primitive);
        setSourceObject(source);
        setObjectStyler(styler);
        setRenderer(renderer);
    }
    
    //region PROPERTIES

    @Override
    public AttributeSet getStyle() {
        return styler.style(source);
    }

    public S getSourceObject() {
        return source;
    }

    public final void setSourceObject(S source) {
        this.source = source;
        sourceGraphicUpdated();
    }

    public ObjectStyler<S> getObjectStyler() {
        return styler;
    }

    public final void setObjectStyler(ObjectStyler<S> styler) {
        this.styler = styler;
        sourceGraphicUpdated();
    }
    
    //endregion

    @Override
    public void initContextMenu(JPopupMenu menu, Graphic<G> src, Point2D point, Object focus, Set<Graphic<G>> selection, G canvas) {
        // switch focus to source object
        super.initContextMenu(menu, src, point, source, selection, canvas);
    }
    
    /**
     * Hook method for updating the shape attributes after the source graphic or style has changed.
     * This version of the method updates the tooltip and notifies listeners that the
     * graphic has changed.
     */
    protected void sourceGraphicUpdated() {
        setDefaultTooltip(styler.tooltip(source, null));
        fireGraphicChanged();
    }
    
}
