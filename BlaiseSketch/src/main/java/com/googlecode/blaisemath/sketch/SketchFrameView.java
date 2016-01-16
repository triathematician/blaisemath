/**
 * BlaiseSketchFrameView.java
 * Created Oct 1, 2014
 */
package com.googlecode.blaisemath.sketch;

/*
 * #%L
 * BlaiseGraphics
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


import com.google.common.collect.Iterables;
import com.googlecode.blaisemath.editor.EditorRegistration;
import com.googlecode.blaisemath.firestarter.PropertySheet;
import com.googlecode.blaisemath.firestarter.PropertySheetDialog;
import com.googlecode.blaisemath.gesture.GestureOrchestrator;
import com.googlecode.blaisemath.gesture.MouseGesture;
import com.googlecode.blaisemath.graphics.core.Graphic;
import com.googlecode.blaisemath.graphics.core.GraphicComposite;
import com.googlecode.blaisemath.graphics.core.PrimitiveGraphicSupport;
import com.googlecode.blaisemath.style.Marker;
import com.googlecode.blaisemath.style.editor.MarkerEditor;
import com.googlecode.blaisemath.svg.SVGRoot;
import com.googlecode.blaisemath.util.MPanel;
import com.googlecode.blaisemath.util.MenuConfig;
import com.googlecode.blaisemath.util.RollupPanel;
import com.googlecode.blaisemath.util.SetSelectionModel;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditorManager;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
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
    public static final String CLIPBOARD_GRAPHIC_PROP = "clipboardGraphic";
    public static final String CLIPBOARD_DIMENSION_PROP = "clipboardDimension";
    public static final String CLIPBOARD_STYLE_PROP = "clipboardStyle";
    
    private final JFileChooser chooser = new JFileChooser();
    
    private final SketchCanvas canvas;
    private final SetSelectionModel<Graphic<Graphics2D>> selectionModel;
    
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
        
        ActionMap am = app.getContext().getActionMap(this);
        canvas = new SketchCanvas(am);
        initGestureListening();
        
        selectionModel = canvas.getSelectionModel();
        initSelectionListening();
        
        detailsPanel = new RollupPanel();
        selectionPanel = new MPanel("Selection", noSelectionLabel);
        overviewPanel = new MPanel("Overview", new JLabel("overview of what's on the canvas..."));
        detailsPanel.add(selectionPanel);
        detailsPanel.add(overviewPanel);

        // set up menus
        try {
            setMenuBar(MenuConfig.readMenuBar(SketchApp.class, this, canvas));
            setToolBar(MenuConfig.readToolBar(SketchApp.class, this, canvas));
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
        
        JSplitPane mainPanel = new JSplitPane();
        mainPanel.setResizeWeight(.9);
        mainPanel.add(canvas.getViewComponent(), JSplitPane.LEFT);
        mainPanel.add(new JScrollPane(detailsPanel), JSplitPane.RIGHT);
        
        setComponent(mainPanel);
    }
    
    //<editor-fold defaultstate="collapsed" desc="INITIALIZERS">
    private void initGestureListening() {
        canvas.addPropertyChangeListener(GestureOrchestrator.ACTIVE_GESTURE_PROP,
            new PropertyChangeListener(){
                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    activeGestureUpdated((MouseGesture) evt.getNewValue());    
                }
            });
    }
    
    private void initSelectionListening() {
        selectionModel.addPropertyChangeListener(new PropertyChangeListener(){
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
        } else if (gfc instanceof Set) {
            Set gfx = (Set) gfc;
            selectionPanel.setTitle(gfx.size()+" graphics selected.");
            selectionPanel.setPrimaryComponent(new JLabel("No options."));
            canvas.selectionTool();
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

    /** Activates the gesture layer, and allows the user to complete the gesture. */
    private void activeGestureUpdated(MouseGesture gesture) {
        setStatusMessage(gesture == null ? "" : gesture.getName()+" | "+gesture.getDesription(), 5000);
    }
    
    /** Updates flags indicating status of clipboard. */
    private void updateClipboardProps() {
        boolean gfc = isClipboardGraphic();
        boolean dim = isClipboardDimension();
        boolean sty = isClipboardStyle();
        firePropertyChange(CLIPBOARD_GRAPHIC_PROP, !gfc, gfc);
        firePropertyChange(CLIPBOARD_DIMENSION_PROP, !dim, dim);
        firePropertyChange(CLIPBOARD_STYLE_PROP, !sty, sty);
    }
    
    //<editor-fold defaultstate="collapsed" desc="PROPERTIES">
    
    public boolean isSelectionEmpty() {
        return selectionModel.getSelection().isEmpty();
    }
    
    public boolean isOneSelected() {
        return selectionModel.getSelection().size() == 1;
    }
    
    public boolean isMoreThanOneSelected() {
        return selectionModel.getSelection().size() > 1;
    }
    
    public boolean isGroupSelected() {
        Set<Graphic<Graphics2D>> gfx = selectionModel.getSelection();
        return gfx.size() == 1 && Iterables.get(gfx, 0) instanceof GraphicComposite;
    }
    
    public boolean isClipboardGraphic() {
        return SketchActions.isClipboardGraphic();
    }
    
    public boolean isClipboardDimension() {
        return SketchActions.isClipboardDimension();
    }
    
    public boolean isClipboardStyle() {
        return SketchActions.isClipboardStyle();
    }
    
    public void setStatusMessage(String msg, int timeout) {
        statusLabel.setText(msg);
        statusLabel.repaint();
    }
    
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="I/O ACTIONS">
    
    @Action
    public void load() {        
        if (JFileChooser.APPROVE_OPTION == chooser.showOpenDialog(getFrame())) {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(chooser.getSelectedFile());
                SVGRoot svg = SVGRoot.load(fis);
                canvas.setCanvasGraphics(SketchIO.fromSVG(svg));
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
                SVGRoot r = SketchIO.toSVG(canvas.getGraphicRoot());
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
            canvas.clearCanvas();
        }
    }
    
    @Action
    public void editCanvas() {
        PropertySheetDialog.show(getFrame(), true, canvas.getCanvasModel());
    }
    
    @Action
    public void selectAll() {
        canvas.selectAll();
    }
    
    @Action(enabledProperty=ONE_SELECTED_PROP)
    public void copySelected() {
        Graphic<Graphics2D> sel = Iterables.getFirst(selectionModel.getSelection(), null);
        SketchActions.copy(sel);
        updateClipboardProps();
    }
    
    @Action(enabledProperty=CLIPBOARD_GRAPHIC_PROP)
    public void pasteGraphic() {
        Point mouseLoc = MouseInfo.getPointerInfo().getLocation();
        Point compLoc = canvas.getViewComponent().getLocationOnScreen();
        canvas.pasteGraphic(new Point(mouseLoc.x-compLoc.x, mouseLoc.y-compLoc.y));
    }
    
    @Action(enabledProperty=ONE_SELECTED_PROP)
    public void copySelectedStyle() {
        Graphic<Graphics2D> sel = Iterables.getFirst(selectionModel.getSelection(), null);
        SketchActions.copyStyle(sel);
        updateClipboardProps();
    }
    
    @Action(disabledProperty=SELECTION_EMPTY_PROP)
    public void pasteSelectedStyle() {
        Set<Graphic<Graphics2D>> sel = selectionModel.getSelection();
        SketchActions.pasteStyle(sel);
    }
    
    @Action(enabledProperty=ONE_SELECTED_PROP)
    public void copySelectedDimension() {
        Graphic<Graphics2D> sel = Iterables.getFirst(selectionModel.getSelection(), null);
        SketchActions.copyDimension(sel);
        updateClipboardProps();
    }
    
    @Action(disabledProperty=SELECTION_EMPTY_PROP)
    public void pasteSelectedDimension() {
        Set<Graphic<Graphics2D>> sel = selectionModel.getSelection();
        SketchActions.pasteDimension(sel);
    }
    
    @Action(disabledProperty=SELECTION_EMPTY_PROP)
    public void clearSelection() {
        canvas.setSelection(Collections.EMPTY_SET);
    }
    
    @Action(enabledProperty=ONE_SELECTED_PROP)
    public void editSelected() {
        Graphic<Graphics2D> sel = Iterables.getFirst(selectionModel.getSelection(), null);
        SketchActions.editGraphic(getFrame(), sel);
    }
    
    @Action(enabledProperty=ONE_SELECTED_PROP)
    public void editSelectedStyle() {
        Graphic<Graphics2D> sel = Iterables.getFirst(selectionModel.getSelection(), null);
        SketchActions.editGraphicStyle(getFrame(), sel);
    }
    
    @Action(disabledProperty=SELECTION_EMPTY_PROP)
    public void addAttributeToSelected() {
        Set<Graphic<Graphics2D>> sel = selectionModel.getSelection();
        SketchActions.addAttribute(getFrame(), sel);
    }
    
    @Action(disabledProperty=SELECTION_EMPTY_PROP)
    public void deleteSelected() {
        SketchActions.deleteSelected(selectionModel);
    }
    
    @Action(enabledProperty=MORE_THAN_ONE_SELECTED_PROP)
    public void groupSelected() {
        SketchActions.groupSelected(canvas.getGraphicRoot(), selectionModel);
    }
    
    @Action(enabledProperty=GROUP_SELECTED_PROP)
    public void ungroupSelected() {
        SketchActions.ungroupSelected(selectionModel);
    }
    
    @Action(disabledProperty=SELECTION_EMPTY_PROP)
    public void lockSelected() {
        SketchActions.setLockPosition(selectionModel.getSelection(), true);
    }
    
    @Action(disabledProperty=SELECTION_EMPTY_PROP)
    public void unlockSelected() {
        SketchActions.setLockPosition(selectionModel.getSelection(), false);
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="ARRANGEMENTS">
    
    @Action(disabledProperty=SELECTION_EMPTY_PROP)
    public void moveForward() {
        SketchActions.moveForward(selectionModel.getSelection());
    }
    
    @Action(disabledProperty=SELECTION_EMPTY_PROP)
    public void moveBackward() {
        SketchActions.moveBackward(selectionModel.getSelection());
    }
    
    @Action(disabledProperty=SELECTION_EMPTY_PROP)
    public void moveToFront() {
        SketchActions.moveToFront(selectionModel.getSelection());
    }
    
    @Action(disabledProperty=SELECTION_EMPTY_PROP)
    public void moveToBack() {
        SketchActions.moveToBack(selectionModel.getSelection());
    }

    @Action(enabledProperty=MORE_THAN_ONE_SELECTED_PROP)
    public void alignHorizontal() {
        SketchActions.alignHorizontal(selectionModel.getSelection());
    }

    @Action(enabledProperty=MORE_THAN_ONE_SELECTED_PROP)
    public void alignVertical() {
        SketchActions.alignVertical(selectionModel.getSelection());        
    }

    @Action(enabledProperty=MORE_THAN_ONE_SELECTED_PROP)
    public void distributeHorizontal() {
        SketchActions.distributeHorizontal(selectionModel.getSelection());
    }

    @Action(enabledProperty=MORE_THAN_ONE_SELECTED_PROP)
    public void distributeVertical() {
        SketchActions.distributeVertical(selectionModel.getSelection());
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Graphic CONTEXT MENU ACTIONS">
    
    @Action
    public void editGraphic(ActionEvent e) {
        SketchActions.editGraphic(getFrame(), (Graphic<Graphics2D>) e.getSource());
    }
    
    @Action
    public void editGraphicStyle(ActionEvent e) {
        SketchActions.editGraphicStyle(getFrame(), (Graphic<Graphics2D>) e.getSource());
    }
    
    @Action
    public void lockGraphic(ActionEvent e) {
        SketchActions.setLockPosition((Graphic<Graphics2D>) e.getSource(), true);
    }
    
    @Action
    public void unlockGraphic(ActionEvent e) {
        SketchActions.setLockPosition((Graphic<Graphics2D>) e.getSource(), false);
    }
    
    @Action
    public void addGraphicAttribute(ActionEvent e) {
        SketchActions.addAttribute(getFrame(), (Graphic<Graphics2D>) e.getSource());
    }
    
    @Action
    public void deleteGraphic(ActionEvent e) {
        SketchActions.deleteGraphic(selectionModel, (Graphic<Graphics2D>) e.getSource());
    }
    
    @Action
    public void copy(ActionEvent e) {
        SketchActions.copy((Graphic<Graphics2D>) e.getSource());
        updateClipboardProps();
    }
    
    @Action
    public void copyStyle(ActionEvent e) {
        SketchActions.copyStyle((Graphic<Graphics2D>) e.getSource());
        updateClipboardProps();
    }
    
    @Action(enabledProperty=CLIPBOARD_STYLE_PROP)
    public void pasteStyle(ActionEvent e) {
        SketchActions.pasteStyle(Collections.singleton((Graphic<Graphics2D>) e.getSource()));
    }
    
    @Action
    public void copyDimension(ActionEvent e) {
        SketchActions.copyDimension((Graphic<Graphics2D>) e.getSource());
        updateClipboardProps();
    }
    
    @Action(enabledProperty=CLIPBOARD_DIMENSION_PROP)
    public void pasteDimension(ActionEvent e) {
        SketchActions.pasteDimension(Collections.singleton((Graphic<Graphics2D>) e.getSource()));
    }
    
    // </editor-fold>
    
}
