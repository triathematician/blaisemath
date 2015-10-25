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
import com.googlecode.blaisemath.gesture.swing.CanvasHandlerGesture;
import com.googlecode.blaisemath.gesture.swing.ControlBoxGesture;
import com.googlecode.blaisemath.gesture.swing.ControlLineGesture;
import com.googlecode.blaisemath.gesture.swing.CreateCircleGesture;
import com.googlecode.blaisemath.gesture.swing.CreateEllipseGesture;
import com.googlecode.blaisemath.gesture.swing.CreateImageGesture;
import com.googlecode.blaisemath.gesture.swing.CreateLineGesture;
import com.googlecode.blaisemath.gesture.swing.CreateMarkerGesture;
import com.googlecode.blaisemath.gesture.swing.CreatePathGesture;
import com.googlecode.blaisemath.gesture.swing.CreateRectangleGesture;
import com.googlecode.blaisemath.gesture.swing.CreateTextGesture;
import com.googlecode.blaisemath.gesture.GestureLayerUI;
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
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ActionMap;
import javax.swing.JLayer;
import org.jdesktop.application.Action;

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

    /** The layer component wrapping the graphic canvas. */
    private final JLayer<JGraphicComponent> layer;
    /** The graphic canvas. */
    private final JGraphicComponent canvas;
    /** Data model for the canvas, with dimensions and style. */
    private final SketchCanvasModel canvasModel;
    
    /** The gesture orchestrator for the canvas. */
    private final GestureOrchestrator<JGraphicComponent> orchestrator;
    /** The select gesture (default gesture). */
    private final MouseGesture selectGesture;
    
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
        GestureLayerUI gestureUI = new GestureLayerUI();
        layer = new JLayer<JGraphicComponent>(canvas, gestureUI);
        orchestrator = gestureUI.getGestureOrchestrator();
        assert orchestrator != null;
        orchestrator.addPropertyChangeListener(GestureOrchestrator.ACTIVE_GESTURE_PROP,
            new PropertyChangeListener(){
                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    pcs.firePropertyChange(evt);
                }
            });
        orchestrator.addConfigurer(Graphic.class, SketchGraphicUtils.configurer());
        selectGesture = new SelectGesture(orchestrator);
        orchestrator.addGesture(selectGesture);
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
                if (!sel.isEmpty()) {
                    initSelectionGesture(sel);
                }
            }
        });
    }
    
    /** Initializes selection gesture for the given selection. */
    private void initSelectionGesture(Set<Graphic> sel) {
        if (sel.size() == 1) {
            Graphic gfc = Iterables.getFirst(sel, null);
            if (gfc instanceof PrimitiveGraphicSupport) {
                PrimitiveGraphicSupport pgfc = (PrimitiveGraphicSupport) gfc;
                if (pgfc.getPrimitive() instanceof Line2D) {
                    orchestrator.addGesture(new ControlLineGesture(orchestrator, pgfc));
                } else if (ControlBoxGesture.supports(pgfc.getPrimitive())) {
                    orchestrator.addGesture(new ControlBoxGesture(orchestrator, pgfc));
                } else {
                    // controls not available
                }
            }
        } else if (sel.size() > 1) {
            // TODO - set up bounding box gesture for multiple items
        }
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
     * Get model object for the canvas.
     * @return model object
     */
    public SketchCanvasModel getCanvasModel() {
        return canvasModel;
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
    
    public boolean isSelectionEmpty() {
        return canvas.getSelectionModel().getSelection().isEmpty();
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="CANVAS ACTIONS">

    /**
     * Remove all graphics from the canvas.
     */
    @Action
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
    @Action
    public void selectAll() {
        setSelection(Sets.newHashSet(canvas.getGraphicRoot().getGraphics()));
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="ZOOM ACTIONS">
    
    @Action
    public void zoomToCanvas() {
        Rectangle2D bounds = canvasModel.getBoundingBox();
        PanAndZoomHandler.zoomCoordBoxAnimated(canvas, 
                new Point2D.Double(bounds.getMinX()-10, bounds.getMinY()-10),
                new Point2D.Double(bounds.getMaxX()+10, bounds.getMaxY()+10));
    }

    @Action
    public void zoomIn() {
        canvas.zoomIn();
    }

    @Action
    public void zoomOut() {
        canvas.zoomOut();
    }

    @Action
    public void zoomToAll() {
        canvas.zoomToAll();
    }
    
    @Action(disabledProperty=SELECTION_EMPTY_PROP)
    public void zoomToSelected() {
        canvas.zoomToSelected();
    }
    
    @Action
    public void resetTransform() {
        canvas.resetTransform();
    }
    
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="TOOL/GESTURE ACTIONS">
    
    @Action
    public void canvasTool() {
        setFocalGesture(CanvasHandlerGesture.class);
    }
    
    @Action
    public void selectionTool() {
        setFocalGesture(SelectGesture.class);
    }
    
    @Action
    public void createMarker() {
        setFocalGesture(CreateMarkerGesture.class);
    }
    
    @Action
    public void createLine() {
        setFocalGesture(CreateLineGesture.class);
    }
    
    @Action
    public void createPath() {
        setFocalGesture(CreatePathGesture.class);
    }
    
    @Action
    public void createRect() {
        setFocalGesture(CreateRectangleGesture.class);
    }
    
    @Action
    public void createCircle() {
        setFocalGesture(CreateCircleGesture.class);
    }
    
    @Action
    public void createEllipse() {
        setFocalGesture(CreateEllipseGesture.class);
    }
    
    @Action
    public void createText() {
        setFocalGesture(CreateTextGesture.class);
    }
    
    @Action
    public void createImage() {
        setFocalGesture(CreateImageGesture.class);
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="GESTURE HANDLING">
    
    /** 
     * Places the given gesture at the top of the gesture stack of the orchestrator.
     */
    <G extends MouseGesture> void setFocalGesture(Class<G> gType) {
        checkNotNull(gType);
        try {
            Constructor<G> con = gType.getConstructor(GestureOrchestrator.class);
            G gesture = con.newInstance(orchestrator);
            orchestrator.setGestureStack(Arrays.asList(gesture, selectGesture));
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
