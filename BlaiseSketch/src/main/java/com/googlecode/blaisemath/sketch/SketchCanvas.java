/**
 * SketchCanvas.java
 * Created Oct 17, 2015
 */
package com.googlecode.blaisemath.sketch;

/*
 * #%L
 * BlaiseSketch
 * --
 * Copyright (C) 2014 - 2015 Elisha Peterson
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
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.googlecode.blaisemath.gesture.GestureOrchestrator;
import com.googlecode.blaisemath.gesture.MouseGesture;
import com.googlecode.blaisemath.gesture.swing.ControlBoxGesture;
import com.googlecode.blaisemath.gesture.swing.ControlLineGesture;
import com.googlecode.blaisemath.gesture.swing.JGraphicGestureLayerUI;
import com.googlecode.blaisemath.gesture.swing.SelectGesture;
import com.googlecode.blaisemath.graphics.core.Graphic;
import com.googlecode.blaisemath.graphics.core.GraphicComposite;
import com.googlecode.blaisemath.graphics.core.PrimitiveGraphicSupport;
import com.googlecode.blaisemath.graphics.swing.JGraphicComponent;
import com.googlecode.blaisemath.graphics.swing.PanAndZoomHandler;
import static com.googlecode.blaisemath.sketch.SketchFrameView.SELECTION_EMPTY_PROP;
import com.googlecode.blaisemath.util.MenuConfig;
import com.googlecode.blaisemath.util.SetSelectionModel;
import com.googlecode.blaisemath.util.swing.ActionMapContextMenuInitializer;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ActionMap;
import javax.swing.JLayer;

/**
 * <p>
 *   Main drawing canvas for use with the sketch app. Provides support for
 *   canvas gestures and canvas selections.
 * </p>
 * 
 * @author elisha
 */
public final class SketchCanvas {
        
    private static final String SELECTION_CM_KEY = "Selection";
    private static final String CANVAS_CM_KEY = "Canvas";
    
    private final SketchCanvasModel canvasModel;
    private final JGraphicComponent canvas;
    private final JGraphicGestureLayerUI gestureUI;
    private final JLayer<JGraphicComponent> layer;
    
    /** Handles property listening */
    protected final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    /**
     * Initialize the canvas.
     * @param actions provides actions for menus
     */
    public SketchCanvas(ActionMap actions) {
        canvasModel = new SketchCanvasModel();
        
        canvas = new JGraphicComponent();
        canvas.setSelectionEnabled(true);
        PanAndZoomHandler.install(canvas);
        canvas.getUnderlays().add(canvasModel.underlay());

        // set up menus
        try {
            List<String> selCm = (List<String>) MenuConfig.readConfig(SketchApp.class).get(SELECTION_CM_KEY);
            canvas.getGraphicRoot().addContextMenuInitializer(new ActionMapContextMenuInitializer<Graphic<Graphics2D>>(
                    "Selection", actions, selCm.toArray(new String[0])));
            List<String> cnvCm = (List<String>) MenuConfig.readConfig(SketchApp.class).get(CANVAS_CM_KEY); 
            canvas.getGraphicRoot().addContextMenuInitializer(new ActionMapContextMenuInitializer<Graphic<Graphics2D>>(
                    null, actions, cnvCm.toArray(new String[0])));
        } catch (IOException ex) {
            Logger.getLogger(SketchFrameView.class.getName()).log(Level.SEVERE, 
                    "Menu config failure.", ex);
        }

        //<editor-fold defaultstate="collapsed" desc="create gesture controller with overlay for intercepting events and rendering gesture hints">
        gestureUI = new JGraphicGestureLayerUI();
        gestureUI.addPropertyChangeListener(GestureOrchestrator.ACTIVE_GESTURE_PROP,
            new PropertyChangeListener(){
                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    pcs.firePropertyChange(evt);
                }
            });
        layer = new JLayer<JGraphicComponent>(canvas, gestureUI);
        GestureOrchestrator go = gestureUI.getGestureOrchestrator();
        assert go != null;
        go.addConfigurer(Graphic.class, SketchGraphicUtils.configurer());
        go.setDefaultGesture(new SelectGesture(go));
        //</editor-fold>
        
        initSelectionHandling();
    }
    
    /** Initialize selection handling */
    private void initSelectionHandling() {
        canvas.getSelectionModel().addPropertyChangeListener(new PropertyChangeListener(){
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                pcs.firePropertyChange(evt);
                
                // propagate flags
                boolean selEmpty = getSelection().isEmpty();
                pcs.firePropertyChange(SELECTION_EMPTY_PROP, !selEmpty, selEmpty);
                
                // set active gesture
                Set<Graphic> sel = (Set<Graphic>) evt.getNewValue();
                Graphic gfc = Iterables.getFirst(sel, null);
                if (gfc instanceof PrimitiveGraphicSupport) {
                    PrimitiveGraphicSupport pgfc = (PrimitiveGraphicSupport) gfc;
                    if (pgfc.getPrimitive() instanceof Line2D) {
                        gestureUI.setActiveGesture(new ControlLineGesture(gestureUI.getGestureOrchestrator(), pgfc));
                    } else if (ControlBoxGesture.supports(pgfc.getPrimitive())) {
                        gestureUI.setActiveGesture(new ControlBoxGesture(gestureUI.getGestureOrchestrator(), pgfc));
                    } else {
                        // controls not available
                    }
                }
            }
        });
    }

    //<editor-fold defaultstate="collapsed" desc="PROPERTY PATTERNS">
    
    /**
     * Get the view component to be added to other views.
     * @return view component
     */
    public Component getViewComponent() {
        return layer;
    }

    /**
     * Get the root for graphics displayed on the canvas.
     * @return root graphic
     */
    public GraphicComposite<Graphics2D> getGraphicRoot() {
        return canvas.getGraphicRoot();
    }

    /**
     * Get the model for the selected items on the canvas.
     * @return selection model
     */
    public SetSelectionModel<Graphic<Graphics2D>> getSelectionModel() {
        return canvas.getSelectionModel();
    }

    /**
     * Get the selected components on the canvas.
     * @return selection
     */
    public Set<Graphic<Graphics2D>> getSelection() {
        return canvas.getSelectionModel().getSelection();
    }
    
    /**
     * Set the selected components on the canvas.
     * @param sel selection
     */
    public void setSelection(Set<Graphic<Graphics2D>> sel) {
        canvas.getSelectionModel().setSelection(sel);
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="CANVAS ACTIONS">

    /**
     * Remove all graphics from the canvas.
     */
    public void clearCanvas() {
        canvas.clearGraphics();
    }

    /**
     * Replace the entire set of graphics on the canvas.
     * @param gfcs new set of graphics
     */
    public void setCanvasGraphics(Iterable<Graphic<Graphics2D>> gfcs) {
        canvas.clearGraphics();
        canvas.addGraphics(gfcs);
        SketchGraphicUtils.configureGraphicTree(canvas.getGraphicRoot(), false);
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="SELECTION ACTIONS">
    
    /** Select all graphics on the canvas. */
    public void selectAll() {
        setSelection(Sets.newHashSet(canvas.getGraphicRoot().getGraphics()));
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="ZOOM ACTIONS">
    
    public void zoomToAll() {
        canvas.zoomToAll();
    }
    
    public void zoomToSelected() {
        canvas.zoomToSelected();
    }
    
    public void resetTransform() {
        canvas.resetTransform();
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="GESTURE HANDLING">
    
    /** Activates the gesture layer, and allows the user to complete the gesture. */
    <G extends MouseGesture> void enableGesture(Class<G> gType) {
        checkNotNull(gType);
        try {
            Constructor<G> con = gType.getConstructor(GestureOrchestrator.class);
            G gesture = con.newInstance(gestureUI.getGestureOrchestrator());
            gestureUI.setActiveGesture(gesture);
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException 
                | SecurityException | IllegalArgumentException | InvocationTargetException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="COPY/PASTE UTILITIES">

    void pasteGraphic(Point winLoc) {
        Point2D canvasLoc = canvas.toGraphicCoordinate(winLoc);
        SketchActions.paste(canvas, canvasLoc);
    }
    
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="PROPERTY CHANGE LISTENING">
    //
    // PROPERTY CHANGE LISTENING
    //
    public void addPropertyChangeListener(PropertyChangeListener pl) {
        pcs.addPropertyChangeListener(pl);
    }
    
    public void addPropertyChangeListener(String string, PropertyChangeListener pl) {
        pcs.addPropertyChangeListener(string, pl);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener pl) {
        pcs.removePropertyChangeListener(pl);
    }
    
    public void removePropertyChangeListener(String string, PropertyChangeListener pl) {
        pcs.removePropertyChangeListener(string, pl);
    }

    //</editor-fold>
    
}
