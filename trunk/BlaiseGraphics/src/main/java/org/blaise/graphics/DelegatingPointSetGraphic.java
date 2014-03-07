/**
 * DelegatingPointSetGraphic.java
 * Created Jan 22, 2011
 */
package org.blaise.graphics;

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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.blaise.style.ObjectStyler;
import org.blaise.style.PointStyle;
import org.blaise.util.CoordinateChangeEvent;
import org.blaise.util.CoordinateListener;
import org.blaise.util.CoordinateManager;

/**
 * Manages a collection of points that are maintained as separate {@link Graphic}s,
 * and therefore fully customizable. Point locations are handled by a {@link CoordinateManager}.
 *
 * @param <S> the type of object being displayed
 *
 * @see PointStyle
 * @see BasicPointSetGraphic
 *
 * @author Elisha Peterson
 */
public class DelegatingPointSetGraphic<S> extends GraphicComposite {

    /** Graphic objects for individual points */
    protected final Map<S, DelegatingPointGraphic<S>> points = Maps.newHashMap();
    
    /** Manages locations of points */
    protected CoordinateManager<S,Point2D> manager;
    /** Generates styles for graphics */
    @Nonnull 
    protected ObjectStyler<S, PointStyle> styler = ObjectStyler.create();

    /** Indicates points are being updated */
    protected boolean updatingPoint = false;
    /** Responds to point change events */
    private final ChangeListener pointListener;
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
        
        pointListener = new ChangeListener(){
            @Override
            public void stateChanged(ChangeEvent e) {
                if (!updatingPoint && e.getSource() instanceof DelegatingPointGraphic) {
                    synchronized(DelegatingPointSetGraphic.this) {
                        DelegatingPointGraphic<S> dpg = (DelegatingPointGraphic<S>) e.getSource();
                        manager.put(dpg.getSourceObject(), dpg.getPoint());
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
                DelegatingPointGraphic<S> dpg = points.get(src);
                if (dpg == null) {
                    dpg = new DelegatingPointGraphic<S>(en.getKey(), en.getValue());
                    points.put(src, dpg);
                    dpg.setStyler(styler);
                    dpg.addChangeListener(pointListener);
                    addMe.add(dpg);
                } else {
                    // this should not result in manager changing
                    updatingPoint = true;
                    dpg.setPoint(en.getValue());
                    updatingPoint = false;
                }
            }
        }
        Set<DelegatingPointGraphic<S>> removeMe = Sets.newHashSet();
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
    public synchronized ObjectStyler<S, PointStyle> getStyler() {
        return styler;
    }
    
    /**
     * Sets object used to style points
     * @param styler object styler
     */
    public synchronized void setStyler(ObjectStyler<S, PointStyle> styler) {
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
                gfc instanceof DelegatingPointGraphic ? ((DelegatingPointGraphic)gfc).getSourceObject() : focus, 
                selection);
    }
    
}
