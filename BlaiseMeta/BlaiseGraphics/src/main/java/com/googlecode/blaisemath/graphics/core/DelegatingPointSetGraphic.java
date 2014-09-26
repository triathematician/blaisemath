/**
 * DelegatingPointSetGraphic.java
 * Created Jan 22, 2011
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

import com.google.common.base.Functions;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import static com.googlecode.blaisemath.graphics.core.LabeledPointGraphic.LABEL_RENDERER_PROP;
import static com.googlecode.blaisemath.graphics.core.PrimitiveGraphicSupport.RENDERER_PROP;
import com.googlecode.blaisemath.graphics.swing.PointRenderer;
import com.googlecode.blaisemath.style.ObjectStyler;
import com.googlecode.blaisemath.style.Renderer;
import com.googlecode.blaisemath.style.Styles;
import com.googlecode.blaisemath.util.coordinate.CoordinateChangeEvent;
import com.googlecode.blaisemath.util.coordinate.CoordinateListener;
import com.googlecode.blaisemath.util.coordinate.CoordinateManager;
import com.googlecode.blaisemath.util.geom.AnchoredText;
import java.awt.geom.Point2D;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.JPopupMenu;

/**
 * Manages a collection of points that are maintained as separate {@link Graphic}s,
 * and therefore fully customizable. Point locations are handled by a {@link CoordinateManager}.
 *
 * @param <S> the type of object being displayed
 *
 * @see PointRenderer
 * @see BasicPointSetGraphic
 *
 * @author Elisha Peterson
 */
public class DelegatingPointSetGraphic<S,G> extends GraphicComposite<G> {
    
    /** Key for flag allowing individual points to be selected */
    public static final String POINT_SELECTION_ENABLED = "point-selection-enabled";

    /** Graphic objects for individual points */
    protected final Map<S, DelegatingPrimitiveGraphic<S,Point2D,G>> points = Maps.newHashMap();
    /** Whether points can be dragged */
    protected boolean dragEnabled = false;

    /** Manages locations of points */
    protected CoordinateManager<S,Point2D> manager;
    /** Selects styles for graphics */
    @Nonnull 
    protected ObjectStyler<S> styler = ObjectStyler.create();
    /** Selects renderer for points */
    protected Renderer<Point2D,G> renderer;
    /** Renderer for point labels */
    protected Renderer<AnchoredText,G> textRenderer;

    /** Indicates points are being updated */
    protected boolean updatingPoint = false;
    /** Responds to coordinate update events */
    private final CoordinateListener coordListener;
    
    
    /**
     * Construct with no points
     */
    public DelegatingPointSetGraphic() {
        this(new CoordinateManager<S, Point2D>(), null, null);
    }
    
    /**
     * Construct with no points
     * @param renderer
     * @param labelRenderer
     */
    public DelegatingPointSetGraphic(@Nullable Renderer<Point2D, G> renderer,
            @Nullable Renderer<AnchoredText, G> labelRenderer) {
        this(new CoordinateManager<S, Point2D>(), renderer, labelRenderer);
    }

    /**
     * Construct with source objects and locations as a map
     * @param crdManager manages point locations
     * @param renderer used for drawing the points
     * @param labelRenderer
     */
    public DelegatingPointSetGraphic(CoordinateManager<S, Point2D> crdManager, 
            @Nullable Renderer<Point2D, G> renderer,
            @Nullable Renderer<AnchoredText, G> labelRenderer) {
        setRenderer(renderer);
        setLabelRenderer(labelRenderer);
        
        styler.setStyleConstant(Styles.DEFAULT_POINT_STYLE);
        styler.setTipDelegate(Functions.toStringFunction());
        
        coordListener = new CoordinateListener(){
            @Override
            public void coordinatesChanged(CoordinateChangeEvent evt) {
                updatePointGraphics(evt.getAdded(), evt.getRemoved());
            }
        };
        
        setCoordinateManager(crdManager);
    }
    

    //<editor-fold defaultstate="collapsed" desc="EVENT HANDLERS">
    //
    // EVENT HANDLERS
    //
    
    private synchronized void updatePointGraphics(Map<S,Point2D> added, Set<S> removed) {
        List<Graphic> addMe = Lists.newArrayList();
        if (added != null) {
            for (Entry<S, Point2D> en : added.entrySet()) {
                S src = en.getKey();
                DelegatingPrimitiveGraphic<S,Point2D,G> dpg = points.get(src);
                if (dpg == null) {
                    LabeledPointGraphic<S,G> lpg = new LabeledPointGraphic<S,G>(en.getKey(), en.getValue(), styler);
                    lpg.setRenderer(renderer);
                    lpg.setLabelRenderer(textRenderer);
                    lpg.setDragEnabled(dragEnabled);
                    lpg.setSelectionEnabled(isPointSelectionEnabled());
                    points.put(src, lpg);
                    addMe.add(lpg);
                } else {
                    // this should not result in manager changing
                    updatingPoint = true;
                    dpg.setPrimitive(en.getValue());
                    updatingPoint = false;
                }
            }
        }
        Set<DelegatingPrimitiveGraphic<S,Point2D,G>> removeMe = Sets.newHashSet();
        if (removed != null) {
            for (S s : removed) {
                removeMe.add(points.get(s));
                points.remove(s);
            }
        }
        replaceGraphics(removeMe, addMe);
    }

    @Override
    public void graphicChanged(Graphic source) {
        if (!updatingPoint && source instanceof LabeledPointGraphic) {
            LabeledPointGraphic<S,G> dpg = (LabeledPointGraphic<S,G>) source;
            manager.put(dpg.getSourceObject(), dpg.getPrimitive());
        }
        super.graphicChanged(source);
    }

    //</editor-fold>

    
    //<editor-fold defaultstate="collapsed" desc="PROPERTY PATTERNS">
    //
    // PROPERTY PATTERNS
    //
    
    /**
     * Returns true if individual points can be selected.
     * @return true if points can be selected
     */
    public boolean isPointSelectionEnabled() {
        return styleHints.getBoolean(POINT_SELECTION_ENABLED, false);
    }

    public void setPointSelectionEnabled(boolean val) {
        if (isPointSelectionEnabled() != val) {
            styleHints.put(POINT_SELECTION_ENABLED, val);
            for (DelegatingPrimitiveGraphic<S,Point2D,G> dpg : points.values()) {
                dpg.setSelectionEnabled(val);
            }
        }
    }
    
    /**
     * Manager responsible for tracking point locations
     * @return manager
     */
    public CoordinateManager<S, Point2D> getCoordinateManager() {
        return manager;
    }
    
    /**
     * Set manager responsible for tracking point locations
     * @param mgr manager
     */
    public final void setCoordinateManager(CoordinateManager<S, Point2D> mgr) {
        if (this.manager != checkNotNull(mgr)) {
            if (this.manager != null) {
                this.manager.removeCoordinateListener(coordListener);
            }
            this.manager = mgr;
            this.manager.addCoordinateListener(coordListener);
            updatePointGraphics(mgr.getCoordinates(), Collections.EMPTY_SET);
        }
    }

    /**
     * Returns object used to style points
     * @return styler object styler
     */
    public ObjectStyler<S> getStyler() {
        return styler;
    }
    
    /**
     * Sets object used to style points
     * @param styler object styler
     */
    public void setStyler(ObjectStyler<S> styler) {
        if (this.styler != checkNotNull(styler)) {
            this.styler = styler;
            fireGraphicChanged();
        }
    }

    @Nullable 
    public Renderer<Point2D, G> getRenderer() {
        return renderer;
    }

    public final void setRenderer(@Nullable Renderer<Point2D, G> renderer) {
        if (this.renderer != renderer) {
            Object old = this.renderer;
            this.renderer = renderer;
            for (DelegatingPrimitiveGraphic<S,Point2D,G> dpg : points.values()) {
                dpg.setRenderer(renderer);
            }
            fireGraphicChanged();
            pcs.firePropertyChange(RENDERER_PROP, old, renderer);
        }
    }

    @Nullable 
    public Renderer<AnchoredText, G> getLabelRenderer() {
        return textRenderer;
    }

    public final void setLabelRenderer(@Nullable Renderer<AnchoredText, G> renderer) {
        if (this.textRenderer != renderer) {
            Object old = this.renderer;
            this.textRenderer = renderer;
            fireGraphicChanged();
            pcs.firePropertyChange(LABEL_RENDERER_PROP, old, renderer);
        }
    }
    
    public boolean isDragEnabled() {
        return dragEnabled;
    }
    
    public void setDragEnabled(boolean val) {
        if (this.dragEnabled != val) {
            this.dragEnabled = val;
            for (DelegatingPrimitiveGraphic<S,Point2D,G> dpg : points.values()) {
                dpg.setDragEnabled(val);
            }
        }
    }

    /**
     * Return source objects
     * @return source objects
     */
    public Set<? extends S> getObjects() {
        return manager.getObjects();
    }
    
    //</editor-fold>
    
    
    //<editor-fold defaultstate="collapsed" desc="MUTATORS">

    /**
     * Adds objects to the graphic
     * @param obj objects to put
     */
    public final void addObjects(Map<S, Point2D> obj) {
        manager.putAll(obj);
    }
    
    //</editor-fold>


    @Override
    public void initContextMenu(JPopupMenu menu, Graphic src, Point2D point, Object focus, Set selection) {
        // provide additional info for context menu
        Graphic gfc = graphicAt(point);
        super.initContextMenu(menu, this, point, 
                gfc instanceof DelegatingPrimitiveGraphic ? ((DelegatingPrimitiveGraphic)gfc).getSourceObject() : focus, 
                selection);
    }
    
}
