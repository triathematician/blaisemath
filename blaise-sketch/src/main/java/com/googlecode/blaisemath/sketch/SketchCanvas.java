/**
 * SketchCanvas.java
 * Created Oct 17, 2015
 */
package com.googlecode.blaisemath.sketch;

/*
 * #%L
 * BlaiseSketch
 * --
 * Copyright (C) 2014 - 2016 Elisha Peterson
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


import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.googlecode.blaisemath.app.ActionMenuInitializer;
import com.googlecode.blaisemath.app.MenuConfig;
import com.googlecode.blaisemath.gesture.GestureOrchestrator;
import com.googlecode.blaisemath.gesture.MouseGesture;
import com.googlecode.blaisemath.gesture.swing.CreateCircleGesture;
import com.googlecode.blaisemath.gesture.swing.CreateEllipseGesture;
import com.googlecode.blaisemath.gesture.swing.CreateImageGesture;
import com.googlecode.blaisemath.gesture.swing.CreateLineGesture;
import com.googlecode.blaisemath.gesture.swing.CreateMarkerGesture;
import com.googlecode.blaisemath.gesture.swing.CreatePathGesture;
import com.googlecode.blaisemath.gesture.swing.CreateRectangleGesture;
import com.googlecode.blaisemath.gesture.swing.CreateTextGesture;
import com.googlecode.blaisemath.gesture.GestureLayerUI;
import com.googlecode.blaisemath.graphics.core.Graphic;
import com.googlecode.blaisemath.graphics.core.GraphicComposite;
import com.googlecode.blaisemath.graphics.core.PrimitiveGraphicSupport;
import com.googlecode.blaisemath.graphics.swing.JGraphicComponent;
import com.googlecode.blaisemath.graphics.swing.PanAndZoomHandler;
import static com.googlecode.blaisemath.sketch.SketchFrameView.SELECTION_EMPTY_PROP;
import com.googlecode.blaisemath.util.CanvasPainter;
import com.googlecode.blaisemath.util.SetSelectionModel;
import com.sun.glass.events.KeyEvent;
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

    private static final Logger LOG = Logger.getLogger(SketchCanvas.class.getName());
    
    //<editor-fold defaultstate="collapsed" desc="CONSTANTS">
    
    private static final String SELECTION_CM_KEY = "Selection";
    private static final String CANVAS_CM_KEY = "Canvas";
    
    private static final String P_SELECT_TOOL_ACTIVE = "selectToolActive";
    private static final String P_HAND_TOOL_ACTIVE = "handToolActive";
    private static final String P_MARKER_TOOL_ACTIVE = "markerToolActive";
    private static final String P_LINE_TOOL_ACTIVE = "lineToolActive";
    private static final String P_PATH_TOOL_ACTIVE = "pathToolActive";
    private static final String P_RECTANGLE_TOOL_ACTIVE = "rectangleToolActive";
    private static final String P_CIRCLE_TOOL_ACTIVE = "circleToolActive";
    private static final String P_ELLIPSE_TOOL_ACTIVE = "ellipseToolActive";
    private static final String P_TEXT_TOOL_ACTIVE = "textToolActive";
    private static final String P_IMAGE_TOOL_ACTIVE = "imageToolActive";
    
    /** For events indicating the component has been repainted. */
    private static final String P_REPAINT = "repaint";
    
    private static final ImmutableMap<Class<? extends MouseGesture>,String> TOOL_MAP 
            = ImmutableMap.<Class<? extends MouseGesture>,String>builder()
                .put(SelectionGesture.class, P_SELECT_TOOL_ACTIVE)
                .put(HandGesture.class, P_HAND_TOOL_ACTIVE)
                .put(CreateMarkerGesture.class, P_MARKER_TOOL_ACTIVE)
                .put(CreateLineGesture.class, P_LINE_TOOL_ACTIVE)
                .put(CreatePathGesture.class, P_PATH_TOOL_ACTIVE)
                .put(CreateRectangleGesture.class, P_RECTANGLE_TOOL_ACTIVE)
                .put(CreateCircleGesture.class, P_CIRCLE_TOOL_ACTIVE)
                .put(CreateEllipseGesture.class, P_ELLIPSE_TOOL_ACTIVE)
                .put(CreateTextGesture.class, P_TEXT_TOOL_ACTIVE)
                .put(CreateImageGesture.class, P_IMAGE_TOOL_ACTIVE)
                .build();
    
    //</editor-fold>

    /** The layer component wrapping the graphic canvas, responsible for delegating events. */
    private final JLayer<JGraphicComponent> layer;
    /** The graphic canvas. */
    private final JGraphicComponent canvas;
    /** Data model for the canvas, with dimensions and style. */
    private final SketchCanvasModel canvasModel;
    
    /** The gesture orchestrator for the canvas. */
    private final GestureOrchestrator<JGraphicComponent> orchestrator;
    
    /** Handles property listening */
    protected final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    /**
     * Initialize the canvas.
     * @param actions provides actions for menus
     */
    public SketchCanvas(ActionMap actions) {
        canvas = new JGraphicComponent();
        canvas.setSelectionEnabled(false);
        canvas.getOverlays().add((component, canvas) -> pcs.firePropertyChange(P_REPAINT, false, true));
        
        canvasModel = new SketchCanvasModel();
        canvas.getUnderlays().add(canvasModel.underlay());

        initMenus(actions);

        // set up gestures
        GestureLayerUI gestureUI = new GestureLayerUI();
        layer = new JLayer<>(canvas, gestureUI);
        orchestrator = gestureUI.getGestureOrchestrator();
        initGestureOrchestrator();

        initSelectionHandling();
    }
    
    private void initMenus(ActionMap actions) {
        try {
            List<String> selCm = (List<String>) MenuConfig.readConfig(SketchApp.class).get(SELECTION_CM_KEY);
            canvas.getGraphicRoot().addContextMenuInitializer(new ActionMenuInitializer<>(
                    "Selection", actions, selCm.toArray(new String[0])));
            List<String> cnvCm = (List<String>) MenuConfig.readConfig(SketchApp.class).get(CANVAS_CM_KEY); 
            canvas.getGraphicRoot().addContextMenuInitializer(new ActionMenuInitializer<>(
                    null, actions, cnvCm.toArray(new String[0])));
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Menu config failure.", ex);
        }
    }
    
    private void initGestureOrchestrator() {
        orchestrator.addPropertyChangeListener(GestureOrchestrator.P_ACTIVE_GESTURE, evt -> {
            orchestratorPropertyChange(evt);
        });
        orchestrator.addConfigurer(Graphic.class, SketchGraphicUtils.configurer());
        orchestrator.setDefaultGesture(new SelectionGesture(orchestrator));
        orchestrator.addKeyGesture(KeyEvent.VK_SPACE, new HandGesture(orchestrator));
    }

    private void orchestratorPropertyChange(PropertyChangeEvent evt) {
        MouseGesture old = (MouseGesture) evt.getOldValue();
        MouseGesture nue = (MouseGesture) evt.getNewValue();
        pcs.firePropertyChange(old == null ? P_SELECT_TOOL_ACTIVE : TOOL_MAP.get(old.getClass()), true, false);
        pcs.firePropertyChange(nue == null ? P_SELECT_TOOL_ACTIVE : TOOL_MAP.get(nue.getClass()), false, true);
        pcs.firePropertyChange(evt);
        canvas.requestFocusInWindow();
    }
    
    /** Initialize selection handling */
    private void initSelectionHandling() {
        canvas.setSelectionEnabled(true);
        canvas.getSelectionModel().addPropertyChangeListener(evt -> {
            pcs.firePropertyChange(evt);
            
            // propagate flags
            boolean selEmpty = getSelection().isEmpty();
            pcs.firePropertyChange(SELECTION_EMPTY_PROP, !selEmpty, selEmpty);
            
            // set active gesture
            Set<Graphic> sel = (Set<Graphic>) evt.getNewValue();
            if (!sel.isEmpty()) {
                initSelectionGesture(sel);
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
//                    orchestrator.addGesture(new ControlLineGesture(orchestrator, pgfc));
                } else if (MouseRectangleControlHandler.supports(pgfc.getPrimitive())) {
//                    orchestrator.addGesture(new MouseRectangleControlHandler(orchestrator, pgfc));
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
     * Get the visible boundaries of the canvas, in local coordinates.
     * @return boundaries
     */
    public Rectangle2D getVisibleBounds() {
        return PanAndZoomHandler.getLocalBounds(canvas);
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
    
    public boolean isSelectToolActive() {
        return isFocalGesture(null);
    }
    
    public void setSelectToolActive(boolean val) {
        setFocalGesture(null, val);
    }
    
    @Action(selectedProperty = P_SELECT_TOOL_ACTIVE)
    public void selectTool() {
    }
    
    public boolean isHandToolActive() {
        return isFocalGesture(HandGesture.class);
    }
    
    public void setHandToolActive(boolean val) {
        setFocalGesture(HandGesture.class, val);
    }
    
    @Action(selectedProperty = P_HAND_TOOL_ACTIVE)
    public void handTool() {
    }
    
    public boolean isMarkerToolActive() {
        return isFocalGesture(CreateMarkerGesture.class);
    }
    
    public void setMarkerToolActive(boolean val) {
        setFocalGesture(CreateMarkerGesture.class, val);
    }
    
    @Action(selectedProperty = P_MARKER_TOOL_ACTIVE)
    public void markerTool() {
    }
    
    public boolean isLineToolActive() {
        return isFocalGesture(CreateLineGesture.class);
    }
    
    public void setLineToolActive(boolean val) {
        setFocalGesture(CreateLineGesture.class, val);
    }
    
    @Action(selectedProperty = P_LINE_TOOL_ACTIVE)
    public void lineTool() {
    }
    
    public boolean isPathToolActive() {
        return isFocalGesture(CreatePathGesture.class);
    }
    
    public void setPathToolActive(boolean val) {
        setFocalGesture(CreatePathGesture.class, val);
    }
    
    @Action(selectedProperty = P_PATH_TOOL_ACTIVE)
    public void pathTool() {
    }
    
    public boolean isRectangleToolActive() {
        return isFocalGesture(CreateRectangleGesture.class);
    }
    
    public void setRectangleToolActive(boolean val) {
        setFocalGesture(CreateRectangleGesture.class, val);
    }
    
    @Action(selectedProperty = P_RECTANGLE_TOOL_ACTIVE)
    public void rectangleTool() {
    }
    
    public boolean isCircleToolActive() {
        return isFocalGesture(CreateCircleGesture.class);
    }
    
    public void setCircleToolActive(boolean val) {
        setFocalGesture(CreateCircleGesture.class, val);
    }
    
    @Action(selectedProperty = P_CIRCLE_TOOL_ACTIVE)
    public void circleTool() {
    }
    
    public boolean isEllipseToolActive() {
        return isFocalGesture(CreateEllipseGesture.class);
    }
    
    public void setEllipseToolActive(boolean val) {
        setFocalGesture(CreateEllipseGesture.class, val);
    }
    
    @Action(selectedProperty = P_ELLIPSE_TOOL_ACTIVE)
    public void ellipseTool() {
    }
    
    public boolean isTextToolActive() {
        return isFocalGesture(CreateTextGesture.class);
    }
    
    public void setTextToolActive(boolean val) {
        setFocalGesture(CreateTextGesture.class, val);
    }
    
    @Action(selectedProperty = P_TEXT_TOOL_ACTIVE)
    public void textTool() {
    }
    
    public boolean isImageToolActive() {
        return isFocalGesture(CreateImageGesture.class);
    }
    
    public void setImageToolActive(boolean val) {
        setFocalGesture(CreateImageGesture.class, val);
    }
    
    @Action(selectedProperty = P_IMAGE_TOOL_ACTIVE)
    public void imageTool() {
    }
    
    /** Checks to see if focal gesture has given type */
    private <G extends MouseGesture> boolean isFocalGesture(Class<G> type) {
        return type == null ? orchestrator.getActiveGesture() == null
                : type.isInstance(orchestrator.getActiveGesture());
    }
    
    /** Turns focal gesture of given type on or off */
    private <G extends MouseGesture> void setFocalGesture(Class<G> type, boolean val) {
        if (type != null && val) {
            try {
                Constructor<G> con = type.getConstructor(GestureOrchestrator.class);
                G gesture = con.newInstance(orchestrator);
                orchestrator.activateGesture(gesture);
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException 
                    | SecurityException | IllegalArgumentException | InvocationTargetException ex) {
                throw new IllegalArgumentException(ex);
            }
        } else {
            orchestrator.completeActiveGesture();
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
