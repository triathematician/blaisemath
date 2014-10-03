/**
 * BlaiseSketchFrameView.java
 * Created Oct 1, 2014
 */
package com.googlecode.blaisemath.sketch;

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


import com.googlecode.blaisemath.gesture.SketchGesture;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.Iterables;
import com.googlecode.blaisemath.gesture.CreateMarkerGesture;
import com.googlecode.blaisemath.graphics.core.Graphic;
import com.googlecode.blaisemath.graphics.core.GraphicComposite;
import com.googlecode.blaisemath.graphics.swing.JGraphicComponent;
import com.googlecode.blaisemath.gesture.SketchGesture.CreateCircleGesture;
import com.googlecode.blaisemath.gesture.SketchGesture.CreateEllipseGesture;
import com.googlecode.blaisemath.gesture.SketchGesture.CreateImageGesture;
import com.googlecode.blaisemath.gesture.SketchGesture.CreateLineGesture;
import com.googlecode.blaisemath.gesture.SketchGesture.CreatePathGesture;
import com.googlecode.blaisemath.gesture.SketchGesture.CreateRectangleGesture;
import com.googlecode.blaisemath.gesture.SketchGesture.CreateTextGesture;
import com.googlecode.blaisemath.graphics.swing.PanAndZoomHandler;
import com.googlecode.blaisemath.util.RollupPanel;
import java.util.Set;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JLayer;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import org.jdesktop.application.Action;
import org.jdesktop.application.FrameView;

/**
 * Root frame for Blaise drawing.
 * 
 * @author Elisha
 */
public class BlaiseSketchFrameView extends FrameView {
    
    private static final String[] TOOLBAR = {
        "load", "save", null,
        "createMarker", "createLine", "createPath", 
        "createRect", "createCircle", "createEllipse", 
        "createText", "createImage", null,
        "finishGesture", null, 
        "groupSelected", "ungroupSelected"
    };
    
    private final JGraphicGestureLayerUI activeCanvasLayerUI;
    private final JGraphicComponent activeCanvas;
    private final RollupPanel detailsPanel;
    private final JLabel statusLabel;

    public BlaiseSketchFrameView(BlaiseSketchApp app) {
        super(app);
        
        activeCanvas = new JGraphicComponent();
        activeCanvas.setSelectionEnabled(true);
        new PanAndZoomHandler(activeCanvas);
        
        activeCanvasLayerUI = new JGraphicGestureLayerUI();
        JLayer<JGraphicComponent> canvasWithLayer = new JLayer<JGraphicComponent>(activeCanvas, activeCanvasLayerUI);
        
        detailsPanel = new RollupPanel();
        detailsPanel.add("Selection", new JLabel("nothing selected"));
        detailsPanel.add("Overview", new JLabel("overview of what's on the canvas..."));
        
        JSplitPane mainPanel = new JSplitPane();
        mainPanel.setResizeWeight(.9);
        mainPanel.add(canvasWithLayer, JSplitPane.LEFT);
        mainPanel.add(new JScrollPane(detailsPanel), JSplitPane.RIGHT);
        
        setComponent(mainPanel);

        ActionMap am = app.getContext().getActionMap(this);
        setMenuBar(createMenuBar(am));
        setToolBar(createToolBar(am));
        
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
    }
    
    //<editor-fold defaultstate="collapsed" desc="INITIALIZATION">
    
    private JMenuBar createMenuBar(ActionMap am) {
        return null;
    }
    
    private JToolBar createToolBar(ActionMap am) {
        JToolBar res = new JToolBar();
        for (String s : TOOLBAR) {
            if (s == null) {
                res.addSeparator();
            } else {
                res.add(am.get(s));
            }
        }
        return res;
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="PROPERTIES">
    
    public JGraphicComponent getActiveCanvas() {
        return activeCanvas;
    }
    
    public boolean isSelectionEmpty() {
        return activeCanvas.getSelectionModel().isEmpty();
    }
    
    public boolean isGroupSelected() {
        Set<Graphic> gfx = activeCanvas.getSelectionModel().getSelection();
        return gfx.size() == 1 && Iterables.get(gfx, 1) instanceof GraphicComposite;
    }
    
    public void setStatusMessage(String msg, int timeout) {
        statusLabel.setText(msg);
        statusLabel.repaint();
    }
    
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="I/O ACTIONS">
    
    @Action
    public void load() {
    }
    
    @Action
    public void save() {
    }
    
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="TOOL ACTIONS">
    
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
    
    @Action
    public void finishGesture() {
        activeCanvasLayerUI.finishGesture();
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="SELECTION ACTIONS">
    
    @Action(disabledProperty="selectionEmpty")
    public void groupSelected() {
    }
    
    @Action(enabledProperty="groupSelected")
    public void ungroupSelected() {
    }
    
    //</editor-fold>

    /** Activates the gesture layer, and allows the user to complete the gesture. */
    private <G extends SketchGesture> void enableGesture(Class<G> gType) {
        checkNotNull(gType);
        try {
            G gesture = gType.newInstance();
            activeCanvasLayerUI.setActiveGesture(gesture);
            setStatusMessage(gesture.getName()+" | "+gesture.getDesription(), 5000);
        } catch (InstantiationException ex) {
            throw new IllegalArgumentException(ex);
        } catch (IllegalAccessException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    
}
