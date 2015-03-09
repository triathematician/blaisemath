/**
 * BlaiseSketchFrameView.java
 * Created Oct 1, 2014
 */
package com.googlecode.blaisemath.sketch;

/*
 * #%L
 * BlaiseGraphics
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


import com.google.common.base.Converter;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.googlecode.blaisemath.editor.EditorRegistration;
import com.googlecode.blaisemath.firestarter.PropertySheet;
import com.googlecode.blaisemath.gesture.GestureOrchestrator;
import com.googlecode.blaisemath.gesture.MouseGesture;
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
import com.googlecode.blaisemath.gesture.swing.JGraphicGestureLayerUI;
import com.googlecode.blaisemath.gesture.swing.CanvasHandlerGesture;
import com.googlecode.blaisemath.gesture.swing.SelectGesture;
import com.googlecode.blaisemath.graphics.core.Graphic;
import com.googlecode.blaisemath.graphics.core.GraphicComposite;
import com.googlecode.blaisemath.graphics.core.PrimitiveGraphicSupport;
import com.googlecode.blaisemath.graphics.svg.SVGElementGraphicConverter;
import com.googlecode.blaisemath.graphics.swing.JGraphicComponent;
import com.googlecode.blaisemath.graphics.swing.JGraphicRoot;
import com.googlecode.blaisemath.graphics.swing.PanAndZoomHandler;
import com.googlecode.blaisemath.style.Marker;
import com.googlecode.blaisemath.style.editor.MarkerEditor;
import com.googlecode.blaisemath.svg.SVGElement;
import com.googlecode.blaisemath.svg.SVGRoot;
import com.googlecode.blaisemath.util.MPanel;
import com.googlecode.blaisemath.util.MenuConfig;
import com.googlecode.blaisemath.util.RollupPanel;
import com.googlecode.blaisemath.util.swing.ActionMapContextMenuInitializer;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditorManager;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JLayer;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.SwingConstants;
import org.jdesktop.application.Action;
import org.jdesktop.application.FrameView;

/**
 * Root frame for Blaise drawing.
 * 
 * @author Elisha
 */
public final class SketchFrameView extends FrameView {
    
    public static final String SELECTION_EMPTY_PROP = "selectionEmpty";
    public static final String MORE_THAN_ONE_SELECTED_PROP = "moreThanOneSelected";
    public static final String ONE_SELECTED_PROP = "oneSelected";
    public static final String GROUP_SELECTED_PROP = "groupSelected";
        
    private static final String SELECTION_CM_KEY = "Selection";
    
    private final JFileChooser chooser = new JFileChooser();
    
    private final JGraphicGestureLayerUI gestureUI;
    private final JGraphicComponent activeCanvas;
    
    private final RollupPanel detailsPanel;
    private final MPanel selectionPanel;
    private final JLabel noSelectionLabel = new JLabel("nothing selected");
    private final MPanel overviewPanel;
    
    private final JLabel statusLabel;

    /**
     * Initialize the view
     * @param app application
     */
    public SketchFrameView(SketchApp app) {
        super(app);
        
        activeCanvas = new JGraphicComponent();
        activeCanvas.setSelectionEnabled(true);
        PanAndZoomHandler.install(activeCanvas);
        initSelectionListening();
        
        detailsPanel = new RollupPanel();
        selectionPanel = new MPanel("Selection", noSelectionLabel);
        overviewPanel = new MPanel("Overview", new JLabel("overview of what's on the canvas..."));
        detailsPanel.add(selectionPanel);
        detailsPanel.add(overviewPanel);

        // set up menus
        ActionMap am = app.getContext().getActionMap(this);
        try {
            setMenuBar(MenuConfig.readMenuBar(SketchApp.class, am));
            setToolBar(MenuConfig.readToolBar(SketchApp.class, am));
            List<String> selCm = (List<String>) MenuConfig.readConfig(SketchApp.class).get(SELECTION_CM_KEY);
            activeCanvas.getGraphicRoot().addContextMenuInitializer(new ActionMapContextMenuInitializer<Graphic<Graphics2D>>(
                    "Selection", am, selCm.toArray(new String[0])));
        } catch (IOException ex) {
            Logger.getLogger(SketchFrameView.class.getName()).log(Level.SEVERE, 
                    "Menu config failure.", ex);
        }
        
        statusLabel = new JLabel("no status to report");
        JPanel statusBar = new JPanel();
        statusBar.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        statusBar.setLayout(new BoxLayout(statusBar, BoxLayout.LINE_AXIS));
        statusBar.add(new JLabel("STATUS"));
        statusBar.add(Box.createHorizontalStrut(5));
        statusBar.add(new JSeparator(SwingConstants.VERTICAL));
        statusBar.add(Box.createHorizontalStrut(5));
        statusBar.add(statusLabel);
        setStatusBar(statusBar);
        
        EditorRegistration.registerEditors();
        PropertyEditorManager.registerEditor(Marker.class, MarkerEditor.class);

        //<editor-fold defaultstate="collapsed" desc="create gesture controller with overlay for intercepting events and rendering gesture hints">
        gestureUI = new JGraphicGestureLayerUI();
        gestureUI.addPropertyChangeListener(GestureOrchestrator.ACTIVE_GESTURE_PROP,
            new PropertyChangeListener(){
                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    activeGestureUpdated((MouseGesture) evt.getNewValue());
                }
            });
        JLayer<JGraphicComponent> canvasWithLayer = new JLayer<JGraphicComponent>(activeCanvas, gestureUI);
        GestureOrchestrator go = gestureUI.getGestureOrchestrator();
        assert go != null;
        go.addConfigurer(Graphic.class, SketchGraphicUtils.configurer());
        go.setDefaultGesture(new SelectGesture(go));
        //</editor-fold>
        
        JSplitPane mainPanel = new JSplitPane();
        mainPanel.setResizeWeight(.9);
        mainPanel.add(canvasWithLayer, JSplitPane.LEFT);
        mainPanel.add(new JScrollPane(detailsPanel), JSplitPane.RIGHT);
        
        setComponent(mainPanel);
    }
    
    //<editor-fold defaultstate="collapsed" desc="INITIALIZERS">
    private void initSelectionListening() {
        activeCanvas.getSelectionModel().addPropertyChangeListener(new PropertyChangeListener(){
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                boolean selEmpty = isSelectionEmpty();
                boolean one = isOneSelected();
                boolean moreThanOne = isMoreThanOneSelected();
                boolean gpSelected = isGroupSelected();
                firePropertyChange(SELECTION_EMPTY_PROP, !selEmpty, selEmpty);
                firePropertyChange(GROUP_SELECTED_PROP, !gpSelected, gpSelected);
                firePropertyChange(ONE_SELECTED_PROP, !one, one);
                firePropertyChange(MORE_THAN_ONE_SELECTED_PROP, !moreThanOne, moreThanOne);
                
                Set<Graphic> sel = (Set<Graphic>) evt.getNewValue();
                setSelectedGraphic(selEmpty ? null 
                        : sel.size() == 1 ? Iterables.get(sel, 0)
                        : sel);
            }
        });
    }
    //</editor-fold>
    
    public static ActionMap getActionMap() {
        SketchApp app = (SketchApp) SketchApp.getInstance();
        SketchFrameView view = (SketchFrameView) app.getMainView();
        return app.getContext().getActionMap(view);
    }
    
    private void setSelectedGraphic(Object gfc) {
        if (gfc == null) {
            selectionPanel.setTitle("Selection");
            selectionPanel.setPrimaryComponent(noSelectionLabel);
        } else if (gfc instanceof PrimitiveGraphicSupport) {
            // activate controls gesture
            PrimitiveGraphicSupport pgfc = (PrimitiveGraphicSupport) gfc;
            PropertySheet ps = PropertySheet.forBean(new GraphicStyleProxy(pgfc));
            selectionPanel.setTitle(gfc+"");
            selectionPanel.setPrimaryComponent(ps);
            if (pgfc.getPrimitive() instanceof Line2D) {
                gestureUI.setActiveGesture(new ControlLineGesture(gestureUI.getGestureOrchestrator(), pgfc));
            } else if (ControlBoxGesture.supports(pgfc.getPrimitive())) {
                gestureUI.setActiveGesture(new ControlBoxGesture(gestureUI.getGestureOrchestrator(), pgfc));
            } else {
                // controls not available
            }
        } else if (gfc instanceof Set) {
            Set gfx = (Set) gfc;
            selectionPanel.setTitle(gfx.size()+" graphics selected.");
            selectionPanel.setPrimaryComponent(new JLabel("No options."));
            selectionTool();
        } else if (gfc instanceof GraphicComposite) {
            // activate controls gesture
            GraphicComposite gc = (GraphicComposite) gfc;
            PropertySheet ps = PropertySheet.forBean(new GraphicStyleProxy(gc));
            selectionPanel.setTitle(gfc+"");
            selectionPanel.setPrimaryComponent(ps);
        } else {
            Logger.getLogger(SketchFrameView.class.getName()).log(Level.WARNING,
                    "Unable to handle selection of: {0}", gfc);
        }
    }
    
    //<editor-fold defaultstate="collapsed" desc="PROPERTIES">
    
    public JGraphicComponent getActiveCanvas() {
        return activeCanvas;
    }
    
    public boolean isSelectionEmpty() {
        return activeCanvas.getSelectionModel().isEmpty();
    }
    
    public boolean isOneSelected() {
        Set<Graphic<Graphics2D>> gfx = activeCanvas.getSelectionModel().getSelection();
        return gfx.size() == 1;
    }
    
    public boolean isMoreThanOneSelected() {
        Set<Graphic<Graphics2D>> gfx = activeCanvas.getSelectionModel().getSelection();
        return gfx.size() > 1;
    }
    
    public boolean isGroupSelected() {
        Set<Graphic<Graphics2D>> gfx = activeCanvas.getSelectionModel().getSelection();
        return gfx.size() == 1 && Iterables.get(gfx, 0) instanceof GraphicComposite;
    }
    
    public void setStatusMessage(String msg, int timeout) {
        statusLabel.setText(msg);
        statusLabel.repaint();
    }
    
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="I/O ACTIONS">
    
    private SVGRoot toSVG() {
        Converter<Graphic<Graphics2D>, SVGElement> conv = SVGElementGraphicConverter.getInstance().reverse();
        JGraphicRoot gr = activeCanvas.getGraphicRoot();
        SVGRoot r = new SVGRoot();
        for (Graphic<Graphics2D> g : gr.getGraphics()) {
            r.addElement(conv.convert(g));
        }
        return r;
    }
    
    private Iterable<Graphic<Graphics2D>> fromSVG(SVGRoot r) {
        Converter<SVGElement, Graphic<Graphics2D>> conv = SVGElementGraphicConverter.getInstance();
        List<Graphic<Graphics2D>> res = Lists.newArrayList();
        for (SVGElement el : r.getElements()) {
            res.add(conv.convert(el));
        }
        return res;
    }
    
    @Action
    public void load() {        
        if (JFileChooser.APPROVE_OPTION == chooser.showOpenDialog(getFrame())) {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(chooser.getSelectedFile());
                SVGRoot r = SVGRoot.load(fis);
                activeCanvas.clearGraphics();
                Iterable<Graphic<Graphics2D>> gfcs = fromSVG(r);
                activeCanvas.addGraphics(gfcs);
                SketchGraphicUtils.configureGraphicTree(activeCanvas.getGraphicRoot(), false);
            } catch (IOException x) {
                Logger.getLogger(SketchFrameView.class.getName()).log(Level.SEVERE, null, x);
            } finally {
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException ex) {
                        Logger.getLogger(SketchFrameView.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }
    
    @Action
    public void save() {        
        if (JFileChooser.APPROVE_OPTION == chooser.showSaveDialog(getFrame())) {
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(chooser.getSelectedFile());
                SVGRoot r = toSVG();
                SVGRoot.save(r, out);
            } catch (IOException ex) {
                Logger.getLogger(SketchFrameView.class.getName()).log(Level.SEVERE, "Save failed", ex);
                JOptionPane.showMessageDialog(getFrame(), ex, "Save failed", JOptionPane.ERROR_MESSAGE);
            } finally {
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException ex) {
                        Logger.getLogger(SketchFrameView.class.getName()).log(Level.SEVERE, "Close failed", ex);
                    }
                }
            }
        }
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="EDIT ACTIONS">
    
    @Action
    public void clearCanvas() {
        if (JOptionPane.showConfirmDialog(getFrame(), "Are you sure?") == JOptionPane.OK_OPTION) {
            activeCanvas.clearGraphics();
        }
    }
    
    @Action(enabledProperty=ONE_SELECTED_PROP)
    public void copySelected() {
        Graphic<Graphics2D> sel = Iterables.getFirst(activeCanvas.getSelectionModel().getSelection(), null);
        SketchActions.copy(sel, activeCanvas);
    }
    
    @Action
    public void pasteGraphic() {
        Point mouseLoc = MouseInfo.getPointerInfo().getLocation();
        Point compLoc = activeCanvas.getLocationOnScreen();
        Point2D canvasLoc = activeCanvas.toGraphicCoordinate(new Point(mouseLoc.x-compLoc.x, mouseLoc.y-compLoc.y));
        SketchActions.paste(activeCanvas, canvasLoc);
    }
    
    @Action(enabledProperty=ONE_SELECTED_PROP)
    public void copySelectedStyle() {
        Graphic<Graphics2D> sel = Iterables.getFirst(activeCanvas.getSelectionModel().getSelection(), null);
        SketchActions.copyStyle(sel);
    }
    
    @Action(disabledProperty=SELECTION_EMPTY_PROP)
    public void pasteSelectedStyle() {
        Set<Graphic<Graphics2D>> sel = activeCanvas.getSelectionModel().getSelection();
        SketchActions.pasteStyle(sel);
    }
    
    @Action(enabledProperty=ONE_SELECTED_PROP)
    public void copySelectedDimension() {
        Graphic<Graphics2D> sel = Iterables.getFirst(activeCanvas.getSelectionModel().getSelection(), null);
        SketchActions.copyDimension(sel);
    }
    
    @Action(disabledProperty=SELECTION_EMPTY_PROP)
    public void pasteSelectedDimension() {
        Set<Graphic<Graphics2D>> sel = activeCanvas.getSelectionModel().getSelection();
        SketchActions.pasteDimension(sel);
    }
    
    @Action
    public void selectAll() {
        activeCanvas.getSelectionModel().setSelection(Sets.newHashSet(activeCanvas.getGraphicRoot().getGraphics()));
    }
    
    @Action(disabledProperty=SELECTION_EMPTY_PROP)
    public void clearSelection() {
        activeCanvas.getSelectionModel().setSelection(Collections.EMPTY_SET);
    }
    
    @Action(enabledProperty=ONE_SELECTED_PROP)
    public void editSelected() {
        Graphic<Graphics2D> sel = Iterables.getFirst(activeCanvas.getSelectionModel().getSelection(), null);
        SketchActions.editGraphic(sel, activeCanvas);
    }
    
    @Action(enabledProperty=ONE_SELECTED_PROP)
    public void editSelectedStyle() {
        Graphic<Graphics2D> sel = Iterables.getFirst(activeCanvas.getSelectionModel().getSelection(), null);
        SketchActions.editGraphicStyle(sel, activeCanvas);
    }
    
    @Action(disabledProperty=SELECTION_EMPTY_PROP)
    public void addAttributeToSelected() {
        Set<Graphic<Graphics2D>> sel = activeCanvas.getSelectionModel().getSelection();
        SketchActions.addAttribute(sel, activeCanvas);
    }
    
    @Action(disabledProperty=SELECTION_EMPTY_PROP)
    public void deleteSelected() {
        SketchActions.deleteSelected(activeCanvas);
    }
    
    @Action(enabledProperty=MORE_THAN_ONE_SELECTED_PROP)
    public void groupSelected() {
        SketchActions.groupSelected(activeCanvas);
    }
    
    @Action(enabledProperty=GROUP_SELECTED_PROP)
    public void ungroupSelected() {
        SketchActions.ungroupSelected(activeCanvas);
    }
    
    @Action(disabledProperty=SELECTION_EMPTY_PROP)
    public void lockSelected() {
        SketchActions.setLockPosition(activeCanvas.getSelectionModel().getSelection(), activeCanvas, true);
    }
    
    @Action(disabledProperty=SELECTION_EMPTY_PROP)
    public void unlockSelected() {
        SketchActions.setLockPosition(activeCanvas.getSelectionModel().getSelection(), activeCanvas, false);
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="ARRANGEMENTS">
    
    @Action(disabledProperty=SELECTION_EMPTY_PROP)
    public void moveForward() {
        SketchActions.moveForward(activeCanvas.getSelectionModel().getSelection(), activeCanvas);
    }
    
    @Action(disabledProperty=SELECTION_EMPTY_PROP)
    public void moveBackward() {
        SketchActions.moveBackward(activeCanvas.getSelectionModel().getSelection(), activeCanvas);
    }
    
    @Action(disabledProperty=SELECTION_EMPTY_PROP)
    public void moveToFront() {
        SketchActions.moveToFront(activeCanvas.getSelectionModel().getSelection(), activeCanvas);
    }
    
    @Action(disabledProperty=SELECTION_EMPTY_PROP)
    public void moveToBack() {
        SketchActions.moveToBack(activeCanvas.getSelectionModel().getSelection(), activeCanvas);
    }

    @Action(enabledProperty=MORE_THAN_ONE_SELECTED_PROP)
    public void alignHorizontal() {
        SketchActions.alignHorizontal(activeCanvas.getSelectionModel().getSelection());
    }

    @Action(enabledProperty=MORE_THAN_ONE_SELECTED_PROP)
    public void alignVertical() {
        SketchActions.alignVertical(activeCanvas.getSelectionModel().getSelection());        
    }

    @Action(enabledProperty=MORE_THAN_ONE_SELECTED_PROP)
    public void distributeHorizontal() {
        SketchActions.distributeHorizontal(activeCanvas.getSelectionModel().getSelection());
    }

    @Action(enabledProperty=MORE_THAN_ONE_SELECTED_PROP)
    public void distributeVertical() {
        SketchActions.distributeVertical(activeCanvas.getSelectionModel().getSelection());
    }
    
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="TOOL ACTIONS">
    
    @Action
    public void canvasTool() {
        enableGesture(CanvasHandlerGesture.class);
    }
    
    @Action
    public void selectionTool() {
        enableGesture(SelectGesture.class);
    }
    
    @Action
    public void createMarker() {
        enableGesture(CreateMarkerGesture.class);
    }
    
    @Action
    public void createLine() {
        enableGesture(CreateLineGesture.class);
    }
    
    @Action
    public void createPath() {
        enableGesture(CreatePathGesture.class);
    }
    
    @Action
    public void createRect() {
        enableGesture(CreateRectangleGesture.class);
    }
    
    @Action
    public void createCircle() {
        enableGesture(CreateCircleGesture.class);
    }
    
    @Action
    public void createEllipse() {
        enableGesture(CreateEllipseGesture.class);
    }
    
    @Action
    public void createText() {
        enableGesture(CreateTextGesture.class);
    }
    
    @Action
    public void createImage() {
        enableGesture(CreateImageGesture.class);
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="ZOOM ACTIONS">
    
    @Action
    public void zoomAll() {
        activeCanvas.zoomToAll();
    }
    
    @Action(disabledProperty=SELECTION_EMPTY_PROP)
    public void zoomSelected() {
        activeCanvas.zoomToSelected();
    }
    
    @Action
    public void zoom100() {
        activeCanvas.resetTransform();
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Graphic CONTEXT MENU ACTIONS">
    
    @Action
    public void editGraphic(ActionEvent e) {
        SketchActions.editGraphic((Graphic<Graphics2D>) e.getSource(), activeCanvas);
    }
    
    @Action
    public void editGraphicStyle(ActionEvent e) {
        SketchActions.editGraphicStyle((Graphic<Graphics2D>) e.getSource(), activeCanvas);
    }
    
    @Action
    public void lockGraphic(ActionEvent e) {
        SketchActions.setLockPosition((Graphic<Graphics2D>) e.getSource(), activeCanvas, true);
    }
    
    @Action
    public void unlockGraphic(ActionEvent e) {
        SketchActions.setLockPosition((Graphic<Graphics2D>) e.getSource(), activeCanvas, false);
    }
    
    @Action
    public void addGraphicAttribute(ActionEvent e) {
        SketchActions.addAttribute((Graphic<Graphics2D>) e.getSource(), activeCanvas);
    }
    
    @Action
    public void deleteGraphic(ActionEvent e) {
        SketchActions.deleteGraphic((Graphic<Graphics2D>) e.getSource(), activeCanvas);
    }
    
    @Action
    public void copy(ActionEvent e) {
        SketchActions.copy((Graphic<Graphics2D>) e.getSource(), activeCanvas);
    }
    
    @Action
    public void copyStyle(ActionEvent e) {
        SketchActions.copyStyle((Graphic<Graphics2D>) e.getSource());
    }
    
    @Action
    public void pasteStyle(ActionEvent e) {
        SketchActions.pasteStyle(Collections.singleton((Graphic<Graphics2D>) e.getSource()));
    }
    
    @Action
    public void copyDimension(ActionEvent e) {
        SketchActions.copyDimension((Graphic<Graphics2D>) e.getSource());
    }
    
    @Action
    public void pasteDimension(ActionEvent e) {
        SketchActions.pasteDimension(Collections.singleton((Graphic<Graphics2D>) e.getSource()));
    }
    
    // </editor-fold>
    
    /** Activates the gesture layer, and allows the user to complete the gesture. */
    private <G extends MouseGesture> void enableGesture(Class<G> gType) {
        checkNotNull(gType);
        try {
            Constructor<G> con = gType.getConstructor(GestureOrchestrator.class);
            G gesture = con.newInstance(gestureUI.getGestureOrchestrator());
            gestureUI.setActiveGesture(gesture);
        } catch (InstantiationException ex) {
            throw new IllegalArgumentException(ex);
        } catch (IllegalAccessException ex) {
            throw new IllegalArgumentException(ex);
        } catch (NoSuchMethodException ex) {
            throw new IllegalArgumentException(ex);
        } catch (SecurityException ex) {
            throw new IllegalArgumentException(ex);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException(ex);
        } catch (InvocationTargetException ex) {
            throw new IllegalArgumentException(ex);
        }
    }
    
    /** Activates the gesture layer, and allows the user to complete the gesture. */
    private void activeGestureUpdated(MouseGesture gesture) {
        setStatusMessage(gesture == null ? "" : gesture.getName()+" | "+gesture.getDesription(), 5000);
    }

    
}
