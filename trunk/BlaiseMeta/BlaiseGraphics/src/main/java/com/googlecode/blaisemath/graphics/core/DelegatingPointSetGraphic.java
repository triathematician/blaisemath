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
import java.awt.geom.Point2D;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.swing.JPopupMenu;
import com.googlecode.blaisemath.style.ObjectStyler;
import com.googlecode.blaisemath.graphics.swing.PointRenderer;
import com.googlecode.blaisemath.style.Renderer;
import com.googlecode.blaisemath.util.coordinate.CoordinateChangeEvent;
import com.googlecode.blaisemath.util.coordinate.CoordinateListener;
import com.googlecode.blaisemath.util.coordinate.CoordinateManager;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

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

    /** Graphic objects for individual points */
    protected final Map<S, DelegatingPrimitiveGraphic<S,Point2D,G>> points = Maps.newHashMap();
    
    /** Manages locations of points */
    protected CoordinateManager<S,Point2D> manager;
    /** Selects styles for graphics */
    @Nonnull 
    protected ObjectStyler<S> styler = ObjectStyler.create();
    /** Selects renderer for points */
    protected Renderer<Point2D,G> renderer;

    /** Indicates points are being updated */
    protected boolean updatingPoint = false;
    /** Responds to point change events */
    private final PropertyChangeListener pointListener;
    /** Responds to coordinate update events */
    private final CoordinateListener coordListener;
    
    
    /**
     * Construct with no points
     */
    public DelegatingPointSetGraphic() {
        this(new CoordinateManager<S, Point2D>());
    }

    /**
     * Construct with source objects and locations as a map
     * @param crdManager manages point locations
     */
    public DelegatingPointSetGraphic(CoordinateManager<S, Point2D> crdManager) {
        styler.setTipDelegate(Functions.toStringFunction());
        
        pointListener = new PropertyChangeListener(){
            @Override
            public void propertyChange(PropertyChangeEvent e) {
                if (!updatingPoint && e.getSource() instanceof LabeledPointGraphic) {
                    synchronized(DelegatingPointSetGraphic.this) {
                        LabeledPointGraphic<S,G> dpg = (LabeledPointGraphic<S,G>) e.getSource();
                        manager.put(dpg.getSourceObject(), dpg.getPrimitive());
                    }
                }
            }
        };
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
                    dpg = new DelegatingPrimitiveGraphic<S,Point2D,G>(en.getKey(), 
                            en.getValue(), styler, renderer);
                    points.put(src, dpg);
                    dpg.setObjectStyler(styler);
                    dpg.addPropertyChangeListener(PrimitiveGraphicSupport.PRIMITIVE_PROP, pointListener);
                    addMe.add(dpg);
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

    //</editor-fold>

    
    //<editor-fold defaultstate="collapsed" desc="PROPERTY PATTERNS">
    //
    // PROPERTY PATTERNS
    //
    
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
     * Return source objects
     * @return source objects
     */
    public synchronized Set<? extends S> getObjects() {
        return manager.getObjects();
    }

    /**
     * Returns object used to style points
     * @return styler object styler
     */
    public synchronized ObjectStyler<S> getStyler() {
        return styler;
    }
    
    /**
     * Sets object used to style points
     * @param styler object styler
     */
    public synchronized void setStyler(ObjectStyler<S> styler) {
        if (this.styler != checkNotNull(styler)) {
            this.styler = styler;
            fireGraphicChanged();
        }
    }
    
    //</editor-fold>
    
    
    //<editor-fold defaultstate="collapsed" desc="MUTATORS">

    /**
     * Adds objects to the graphic
     * @param obj objects to put
     */
    public final synchronized void addObjects(Map<S, Point2D> obj) {
        manager.putAll(obj);
    }
    
    //</editor-fold>


    @Override
    public void initContextMenu(JPopupMenu menu, Graphic src, Point2D point, Object focus, Set selection) {
        // provide additional info for context menu
        Graphic gfc = graphicAt(point);
        super.initContextMenu(menu, this, point, 
                gfc instanceof LabeledPointGraphic ? ((LabeledPointGraphic)gfc).getSourceObject() : focus, 
                selection);
    }
    
}
