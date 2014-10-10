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


import com.google.common.base.Converter;
import com.googlecode.blaisemath.gesture.SketchGesture;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.googlecode.blaisemath.editor.EditorRegistration;
import com.googlecode.blaisemath.firestarter.PropertySheet;
import com.googlecode.blaisemath.gesture.CreateCircleGesture;
import com.googlecode.blaisemath.gesture.CreateEllipseGesture;
import com.googlecode.blaisemath.gesture.CreateImageGesture;
import com.googlecode.blaisemath.gesture.CreateLineGesture;
import com.googlecode.blaisemath.gesture.CreateMarkerGesture;
import com.googlecode.blaisemath.gesture.CreatePathGesture;
import com.googlecode.blaisemath.gesture.CreateRectangleGesture;
import com.googlecode.blaisemath.gesture.CreateTextGesture;
import com.googlecode.blaisemath.graphics.core.Graphic;
import com.googlecode.blaisemath.graphics.core.GraphicComposite;
import com.googlecode.blaisemath.graphics.svg.SVGElementGraphicConverter;
import com.googlecode.blaisemath.graphics.swing.JGraphicComponent;
import com.googlecode.blaisemath.graphics.swing.JGraphicRoot;
import com.googlecode.blaisemath.graphics.swing.PanAndZoomHandler;
import static com.googlecode.blaisemath.style.Styles.text;
import com.googlecode.blaisemath.svg.SVGElement;
import com.googlecode.blaisemath.svg.SVGRoot;
import com.googlecode.blaisemath.util.MPanel;
import com.googlecode.blaisemath.util.RollupPanel;
import java.awt.Graphics2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
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
    
    private final JFileChooser chooser = new JFileChooser();
    
    private final JGraphicGestureLayerUI activeCanvasLayerUI;
    private final JGraphicComponent activeCanvas;
    
    private final RollupPanel detailsPanel;
    private final MPanel selectionPanel;
    private final JLabel noSelectionLabel = new JLabel("nothing selected");
    private final MPanel overviewPanel;
    
    private final JLabel statusLabel;

    public BlaiseSketchFrameView(BlaiseSketchApp app) {
        super(app);
        
        activeCanvas = new JGraphicComponent();
        activeCanvas.setSelectionEnabled(true);
        new PanAndZoomHandler(activeCanvas);
        activeCanvas.getSelectionModel().addPropertyChangeListener(new PropertyChangeListener(){
            public void propertyChange(PropertyChangeEvent evt) {
                boolean selEmpty = isSelectionEmpty();
                boolean moreThanOne = isMoreThanOneSelected();
                boolean gpSelected = isGroupSelected();
                firePropertyChange("selectionEmpty", !selEmpty, selEmpty);
                firePropertyChange("groupSelected", !gpSelected, gpSelected);
                firePropertyChange("moreThanOneSelected", !moreThanOne, moreThanOne);
                if (selEmpty) {
                    selectionPanel.setPrimaryComponent(noSelectionLabel);
                } else {
                    Set<Graphic> sel = (Set<Graphic>) evt.getNewValue();
                    PropertySheet ps = PropertySheet.forBean(sel.size() == 1
                            ? Iterables.get(sel, 0) : sel);
                    selectionPanel.setPrimaryComponent(ps);
                }
            }
        });
        
        activeCanvasLayerUI = new JGraphicGestureLayerUI();
        JLayer<JGraphicComponent> canvasWithLayer = new JLayer<JGraphicComponent>(activeCanvas, activeCanvasLayerUI);
        
        detailsPanel = new RollupPanel();
        selectionPanel = new MPanel("Selection", noSelectionLabel);
        overviewPanel = new MPanel("Overview", new JLabel("overview of what's on the canvas..."));
        detailsPanel.add(selectionPanel);
        detailsPanel.add(overviewPanel);
        
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
        
        EditorRegistration.registerEditors();
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
    
    public boolean isMoreThanOneSelected() {
        Set<Graphic> gfx = activeCanvas.getSelectionModel().getSelection();
        return gfx.size() > 1;
    }
    
    public boolean isGroupSelected() {
        Set<Graphic> gfx = activeCanvas.getSelectionModel().getSelection();
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
                activeCanvas.addGraphics(fromSVG(r));
            } catch (IOException x) {
                Logger.getLogger(BlaiseSketchFrameView.class.getName()).log(Level.SEVERE, null, x);
            } finally {
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException ex) {
                        Logger.getLogger(BlaiseSketchFrameView.class.getName()).log(Level.SEVERE, null, ex);
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
                Logger.getLogger(BlaiseSketchFrameView.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException ex) {
                        Logger.getLogger(BlaiseSketchFrameView.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }
    
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="TOOL ACTIONS">
    
    @Action
    public void finishGesture() {
        activeCanvasLayerUI.finishGesture();
        setStatusMessage(null, -1);
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
    
    //<editor-fold defaultstate="collapsed" desc="SELECTION ACTIONS">
    
    @Action(disabledProperty="moreThanOneSelected")
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
