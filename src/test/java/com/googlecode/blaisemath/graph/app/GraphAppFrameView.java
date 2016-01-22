/**
 * GraphAppFrameView.class
 * Created Mar 2015
 */
package com.googlecode.blaisemath.graph.app;

/*
 * #%L
 * BlaiseGraphTheory
 * --
 * Copyright (C) 2009 - 2016 Elisha Peterson
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


import com.googlecode.blaisemath.ui.PropertyActionPanel;
import com.google.common.collect.Multisets;
import com.googlecode.blaisemath.app.MenuConfig;
import com.googlecode.blaisemath.editor.EditorRegistration;
import com.googlecode.blaisemath.editor.EnumEditor;
import com.googlecode.blaisemath.graph.Graph;
import com.googlecode.blaisemath.graph.GraphMetric;
import com.googlecode.blaisemath.graph.GraphNodeMetric;
import com.googlecode.blaisemath.graph.GraphSubsetMetric;
import com.googlecode.blaisemath.graph.GraphGenerator;
import com.googlecode.blaisemath.graph.GraphMetrics;
import com.googlecode.blaisemath.graph.GraphServices;
import com.googlecode.blaisemath.graph.IterativeGraphLayout;
import com.googlecode.blaisemath.graph.StaticGraphLayout;
import com.googlecode.blaisemath.graph.view.GraphComponent;
import com.googlecode.blaisemath.style.Anchor;
import com.googlecode.blaisemath.style.Marker;
import com.googlecode.blaisemath.style.editor.MarkerEditor;
import com.googlecode.blaisemath.util.MPanel;
import com.googlecode.blaisemath.util.RollupPanel;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyEditorManager;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.FrameView;

/**
 * Main view for {@link GraphApp}.
 * @author elisha
 */
public final class GraphAppFrameView extends FrameView {
    
    private static final String CANVAS_CM_KEY = "Canvas";

    private final GraphAppCanvas graphComponent;
    
    private GraphGenerator selectedGenerator = null;
    private StaticGraphLayout selectedLayout = null;
    private IterativeGraphLayout selectedIterativeLayout = null;
    
    private final PropertyActionPanel generatorPanel;
    private final JComboBox generatorBox;
    private final PropertyActionPanel staticLayoutPanel;
    private final JComboBox staticLayoutBox;
    private final PropertyActionPanel iterativeLayoutPanel;
    private final JComboBox iterativeLayoutBox;
    
    private final JLabel statusLabel;
    
    public GraphAppFrameView(Application app) {
        super(app);
        
        graphComponent = new GraphAppCanvas();
        
        // set up menus
        ActionMap am = app.getContext().getActionMap(this);
        try {
            setMenuBar(MenuConfig.readMenuBar(GraphApp.class, this, graphComponent));
            setToolBar(MenuConfig.readToolBar(GraphApp.class, this, graphComponent));
            graphComponent.addContextMenuInitializer(GraphComponent.MENU_KEY_GRAPH,
                    MenuConfig.readMenuInitializer(CANVAS_CM_KEY, GraphApp.class, this, graphComponent));    
        } catch (IOException ex) {
            Logger.getLogger(GraphAppFrameView.class.getName()).log(Level.SEVERE, 
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
        PropertyEditorManager.registerEditor(Anchor.class, EnumEditor.class);
        
        generatorPanel = new PropertyActionPanel();
        generatorPanel.setUserOkAction(am.get("applyGenerator"));
        generatorBox  = new JComboBox(GraphServices.generators().toArray());
        generatorBox.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                e.setSource(((JComboBox)e.getSource()).getSelectedItem());
                generateGraph(e);
            }
        });
        generatorPanel.getToolBar().add(generatorBox);
        
        staticLayoutPanel = new PropertyActionPanel();
        staticLayoutPanel.setUserOkAction(am.get("applyStaticLayout"));
        staticLayoutBox  = new JComboBox(GraphServices.staticLayouts().toArray());
        staticLayoutBox.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                e.setSource(((JComboBox)e.getSource()).getSelectedItem());
                staticLayout(e);
            }
        });
        staticLayoutPanel.getToolBar().add(staticLayoutBox);
        
        iterativeLayoutPanel = new PropertyActionPanel();
//        iterativeLayoutPanel.setUserOkAction(am.get("applyIterativeLayout"));
        iterativeLayoutBox  = new JComboBox(GraphServices.iterativeLayouts().toArray());
        iterativeLayoutBox.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                e.setSource(((JComboBox)e.getSource()).getSelectedItem());
                iterativeLayout(e);
            }
        });
        iterativeLayoutPanel.getToolBar().add(iterativeLayoutBox);
        iterativeLayoutPanel.getToolBar().add(am.get("startLayout"));
        iterativeLayoutPanel.getToolBar().add(am.get("stopLayout"));
        
        RollupPanel controlPanel = new RollupPanel();
        controlPanel.add(new MPanel("Graph Generator", generatorPanel));
        controlPanel.add(new MPanel("Static Layout", staticLayoutPanel));
        controlPanel.add(new MPanel("Iterative Layout", iterativeLayoutPanel));
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(graphComponent, BorderLayout.CENTER);
        panel.add(new JScrollPane(controlPanel), BorderLayout.WEST);
        
        setComponent(panel);
        
        setSelectedGenerator((GraphGenerator) generatorBox.getSelectedItem());
        setSelectedLayout((StaticGraphLayout) staticLayoutBox.getSelectedItem());
        setSelectedIterativeLayout((IterativeGraphLayout) iterativeLayoutBox.getSelectedItem());
    }
    
    //<editor-fold defaultstate="collapsed" desc="PROPERTIES">

    public GraphGenerator getSelectedGenerator() {
        return selectedGenerator;
    }

    public void setSelectedGenerator(GraphGenerator selectedGenerator) {
        this.selectedGenerator = selectedGenerator;
        generatorBox.setSelectedItem(selectedGenerator);
        generatorPanel.setBean(selectedGenerator.createParameters());
        ((MPanel)generatorPanel.getParent()).setPrimaryComponent(generatorPanel);
    }
    
    public Object getGeneratorParameters() {
        return generatorPanel.getBean();
    }

    public StaticGraphLayout getSelectedLayout() {
        return selectedLayout;
    }

    public void setSelectedLayout(StaticGraphLayout selectedLayout) {
        this.selectedLayout = selectedLayout;
        staticLayoutBox.setSelectedItem(selectedLayout);
        staticLayoutPanel.setBean(selectedLayout.createParameters());
        ((MPanel)staticLayoutPanel.getParent()).setPrimaryComponent(staticLayoutPanel);
    }
    
    public Object getLayoutParameters() {
        return staticLayoutPanel.getBean();
    }

    public IterativeGraphLayout getSelectedIterativeLayout() {
        return selectedIterativeLayout;
    }

    public void setSelectedIterativeLayout(IterativeGraphLayout selectedIterativeLayout) {
        this.selectedIterativeLayout = selectedIterativeLayout;
        iterativeLayoutBox.setSelectedItem(selectedIterativeLayout);
        Object parm = selectedIterativeLayout.createParameters();
        iterativeLayoutPanel.setBean(parm);
        ((MPanel)iterativeLayoutPanel.getParent()).setPrimaryComponent(iterativeLayoutPanel);
        graphComponent.getLayoutManager().setLayoutAlgorithm(selectedIterativeLayout);
        graphComponent.getLayoutManager().setLayoutParameters(parm);
    }
    
    public Object getIterativeLayoutParameters() {
        return iterativeLayoutPanel.getBean();
    }
    
    //</editor-fold>
    
    // GRAPH ACTIONS
    
    @Action
    public void generateGraph(ActionEvent event) {
        setSelectedGenerator((GraphGenerator) event.getSource());
    }
    
    @Action
    public void applyGenerator(ActionEvent event) {
        Object parm = event.getSource();
        graphComponent.setGraph((Graph) selectedGenerator.apply(parm));
        applyStaticLayout(new ActionEvent(staticLayoutPanel.getBean(), 0, null));
    }
    
    // LAYOUT ACTIONS
    
    @Action
    public void staticLayout(ActionEvent event) {
        setSelectedLayout((StaticGraphLayout<?>) event.getSource());
    }
    
    @Action
    public void applyStaticLayout(ActionEvent event) {
        Object parm = event.getSource();
        AnimationUtils.animateCoordinateChange(graphComponent.getLayoutManager(), 
                selectedLayout, parm, graphComponent, 10.0);
    }
    
    @Action
    public void iterativeLayout(ActionEvent event) {
        setSelectedIterativeLayout((IterativeGraphLayout) event.getSource());
    }
    
    // METRIC ACTIONS
    
    @Action
    public void globalMetric(ActionEvent event) {
        GraphMetric<?> gs = (GraphMetric<?>) event.getSource();
        Object res = gs.apply(graphComponent.getGraph());
        statusLabel.setText(gs+" = "+res);
    }
    
    @Action
    public void nodeMetric(ActionEvent event) {
        GraphNodeMetric<?> gs = (GraphNodeMetric<?>) event.getSource();
        Graph g = graphComponent.getGraph();
        graphComponent.setMetric(gs);
        statusLabel.setText(gs+" = "+Multisets.copyHighestCountFirst(GraphMetrics.computeDistribution(g, gs)));
    }
    
    @Action
    public void subsetMetric(ActionEvent event) {
        GraphSubsetMetric<?> gs = (GraphSubsetMetric<?>) event.getSource();
        statusLabel.setText(gs+" = ...");
    }
    
}
