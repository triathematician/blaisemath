/*
 * GraphExplorerMain.java
 * Created on May 14, 2010, 10:10:30 AM
 */

package graphexplorer;

import data.propertysheet.PropertySheet;
import data.propertysheet.editor.EditorRegistration;
import graphexplorer.ExplorerStatActions.GlobalStatEnum;
import graphexplorer.ExplorerStatActions.StatEnum;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import org.bm.blaise.scio.graph.Graph;
import org.bm.blaise.scio.graph.ValuedGraph;
import org.bm.blaise.scio.graph.layout.IterativeGraphLayout;
import org.bm.blaise.scio.graph.metrics.NodeMetric;
import org.bm.blaise.specto.plane.graph.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.YIntervalRenderer;
import org.jfree.data.xy.DefaultIntervalXYDataset;
import visometry.PlotComponent;
import visometry.plane.PlanePlotComponent;

/**
 *
 * @author Elisha Peterson
 */
public class GraphExplorerMain extends javax.swing.JFrame
        implements GraphExplorerInterface,
            ChangeListener, PropertyChangeListener {

    /** Controller */
    final GraphControllerMaster master;
    /** Controllers and associated components */
    HashMap<GraphController, Component> tabs = new HashMap<GraphController, Component>();
    /** Controllers and associated plane graph elements */
    HashMap<GraphController, AbstractPlaneGraph> graphs = new HashMap<GraphController, AbstractPlaneGraph>();
    /** Chart displaying statistical data */
    ChartPanel distributionCP;
    /** Chart displaying longitudinal metric data */
    ChartPanel longMetricCP;

    /** General actions */
    ExplorerActions actions;
    /** File/IO actions */
    ExplorerIOActions actions_io;
    /** Statistics/metric actions */
    ExplorerStatActions actions_stat;
    /** Layout actions */
    ExplorerLayoutActions actions_layout;
    /** Graph-generation actions */
    ExplorerGenerateActions actions_gen;

    /** Creates new form GraphExplorerMain */
    public GraphExplorerMain() {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) { }
        EditorRegistration.registerEditors();

        master = new GraphControllerMaster();
        master.addPropertyChangeListener(this);

        initActions();
        initComponents();

        for (Component c : toolbar.getComponents())
            if (c instanceof AbstractButton && ((AbstractButton)c).getIcon() != null) {
                ((AbstractButton)c).setText(null);
            }
        toolbar.add(javax.swing.Box.createHorizontalGlue());

        initMetricMenus();
        master.addPropertyChangeListener(mainTable);
        initCharts();
    }

    private void initActions() {
        actions_io = new ExplorerIOActions(master);
        actions_gen = new ExplorerGenerateActions(master);
        actions_layout = new ExplorerLayoutActions(null); // needs single controller
        actions_stat = new ExplorerStatActions(null); // needs single controller

        actions = new ExplorerActions(this);
    }
    
    /** Stores menu items corresponding to metrics */
    private EnumMap<StatEnum, JRadioButtonMenuItem> locMetricMI;

    private void initMetricMenus() {
        locMetricMI = new EnumMap<StatEnum, JRadioButtonMenuItem>(StatEnum.class);
        for (StatEnum se : StatEnum.values()) {
            JRadioButtonMenuItem mi = new JRadioButtonMenuItem(actions_stat.actionOf(se));
            metricMenuBG.add(mi);
            localMetricM.add(mi);
            locMetricMI.put(se, mi);
            if (se == StatEnum.NONE)
                mi.setSelected(true);
        }
        for (GlobalStatEnum se : GlobalStatEnum.values())
            globalMetricM.add(new JMenuItem(actions_stat.actionOf(se)));
    }

    private void initCharts() {
        JFreeChart distributionFC = ChartFactory.createXYBarChart("Metric Distribution", "Value", false, "Number", null, PlotOrientation.VERTICAL, false, true, false);
        distributionCP = new ChartPanel(distributionFC);
        distributionCP.setPreferredSize(new Dimension(400,300));
        boxTP2.add(distributionCP, "Metric Chart", 0);
        longMetricCP = new ChartPanel(null);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        labelPS = new data.propertysheet.PropertySheet();
        edgePS = new data.propertysheet.PropertySheet();
        nodePS = new data.propertysheet.PropertySheet();
        layoutPS = new data.propertysheet.PropertySheet();
        newPM = new javax.swing.JPopupMenu();
        emptyMI1 = new javax.swing.JMenuItem();
        circleMI1 = new javax.swing.JMenuItem();
        starMI1 = new javax.swing.JMenuItem();
        wheelMI1 = new javax.swing.JMenuItem();
        completeMI1 = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JPopupMenu.Separator();
        uniform1MI1 = new javax.swing.JMenuItem();
        sequenceMI1 = new javax.swing.JMenuItem();
        wattsMI1 = new javax.swing.JMenuItem();
        preferentialMI1 = new javax.swing.JMenuItem();
        metricMenuBG = new javax.swing.ButtonGroup();
        toolbar = new javax.swing.JToolBar();
        newTBB = new javax.swing.JButton();
        loadTBB = new javax.swing.JButton();
        saveTBB = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        layoutCircleTBB = new javax.swing.JButton();
        layoutRandomTBB = new javax.swing.JButton();
        layoutEnergyTBB = new javax.swing.JToggleButton();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        metricCB1 = new javax.swing.JComboBox();
        jSeparator9 = new javax.swing.JToolBar.Separator();
        mainP = new javax.swing.JPanel();
        mainSP = new javax.swing.JSplitPane();
        graphTP = new javax.swing.JTabbedPane();
        propertySP = new javax.swing.JScrollPane();
        propertyRP = new gui.RollupPanel();
        boxPanel = new javax.swing.JPanel();
        boxP1 = new javax.swing.JPanel();
        mainTableTB = new javax.swing.JToolBar();
        metricL = new javax.swing.JLabel();
        metricCB = new javax.swing.JComboBox();
        mainTableSP = new javax.swing.JScrollPane();
        mainTable = new graphexplorer.GraphTable();
        boxP2 = new javax.swing.JPanel();
        boxTP2 = new javax.swing.JTabbedPane();
        distributionTableSP = new javax.swing.JScrollPane();
        distributionTable = new javax.swing.JTable();
        boxP3 = new javax.swing.JPanel();
        outputSP = new javax.swing.JScrollPane();
        outputTP = new javax.swing.JTextPane();
        statusB = new javax.swing.JPanel();
        statusL = new javax.swing.JLabel();
        menu = new javax.swing.JMenuBar();
        fileM = new javax.swing.JMenu();
        newM = new javax.swing.JMenu();
        emptyMI = new javax.swing.JMenuItem();
        circleMI = new javax.swing.JMenuItem();
        starMI = new javax.swing.JMenuItem();
        wheelMI = new javax.swing.JMenuItem();
        completeMI = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        uniform1MI = new javax.swing.JMenuItem();
        sequenceMI = new javax.swing.JMenuItem();
        wattsMI = new javax.swing.JMenuItem();
        preferentialMI = new javax.swing.JMenuItem();
        loadMI = new javax.swing.JMenuItem();
        saveMI = new javax.swing.JMenuItem();
        saveMI1 = new javax.swing.JMenuItem();
        closeMI = new javax.swing.JMenuItem();
        jSeparator7 = new javax.swing.JPopupMenu.Separator();
        exportImageM = new javax.swing.JMenu();
        exportMovieM = new javax.swing.JMenu();
        export_movMI = new javax.swing.JMenuItem();
        jSeparator6 = new javax.swing.JPopupMenu.Separator();
        quitMI = new javax.swing.JMenuItem();
        layoutM = new javax.swing.JMenu();
        circularMI = new javax.swing.JMenuItem();
        randomMI = new javax.swing.JMenuItem();
        circularMI1 = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JPopupMenu.Separator();
        energyM = new javax.swing.JMenu();
        energyStartMI = new javax.swing.JMenuItem();
        energyIterateMI = new javax.swing.JMenuItem();
        energyStopMI = new javax.swing.JMenuItem();
        metricM = new javax.swing.JMenu();
        localMetricM = new javax.swing.JMenu();
        globalMetricM = new javax.swing.JMenu();
        specialM = new javax.swing.JMenu();
        highlightMI = new javax.swing.JMenuItem();
        cooperationMI = new javax.swing.JMenuItem();
        helpM = new javax.swing.JMenu();
        aboutMI = new javax.swing.JMenuItem();
        contentMI = new javax.swing.JMenuItem();

        emptyMI1.setAction(actions_gen.GENERATE_EMPTY);
        newPM.add(emptyMI1);

        circleMI1.setAction(actions_gen.GENERATE_CIRCLE);
        newPM.add(circleMI1);

        starMI1.setAction(actions_gen.GENERATE_STAR);
        newPM.add(starMI1);

        wheelMI1.setAction(actions_gen.GENERATE_WHEEL);
        newPM.add(wheelMI1);

        completeMI1.setAction(actions_gen.GENERATE_COMPLETE);
        newPM.add(completeMI1);
        newPM.add(jSeparator5);

        uniform1MI1.setAction(actions_gen.GENERATE_RANDOM);
        uniform1MI1.setText("Uniform (Erdos-Renyi) random graph...");
        newPM.add(uniform1MI1);

        sequenceMI1.setAction(actions_gen.GENERATE_SEQUENCE);
        sequenceMI1.setText("Degree sequence random graph...");
        newPM.add(sequenceMI1);

        wattsMI1.setAction(actions_gen.GENERATE_WS);
        wattsMI1.setText("Watts-Strogatz random graph...");
        newPM.add(wattsMI1);

        preferentialMI1.setAction(actions_gen.GENERATE_PREFERENTIAL);
        preferentialMI1.setText("Preferential Attachment...");
        newPM.add(preferentialMI1);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Graph Explorer");

        toolbar.setRollover(true);

        newTBB.setIcon(new javax.swing.ImageIcon(getClass().getResource("/graphexplorer/resources/new-graph24.png"))); // NOI18N
        newTBB.setComponentPopupMenu(newPM);
        newTBB.setFocusable(false);
        newTBB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newTBBActionPerformed(evt);
            }
        });
        toolbar.add(newTBB);

        loadTBB.setAction(actions_io.LOAD_ACTION);
        loadTBB.setIcon(new javax.swing.ImageIcon(getClass().getResource("/graphexplorer/resources/load-graph24.png"))); // NOI18N
        loadTBB.setFocusable(false);
        toolbar.add(loadTBB);

        saveTBB.setAction(actions_io.SAVE_ACTION);
        saveTBB.setIcon(new javax.swing.ImageIcon(getClass().getResource("/graphexplorer/resources/save-graph24.png"))); // NOI18N
        saveTBB.setFocusable(false);
        toolbar.add(saveTBB);
        toolbar.add(jSeparator2);

        layoutCircleTBB.setAction(actions_layout.LAYOUT_CIRCULAR);
        layoutCircleTBB.setIcon(new javax.swing.ImageIcon(getClass().getResource("/graphexplorer/resources/layout-circle24.png"))); // NOI18N
        layoutCircleTBB.setFocusable(false);
        toolbar.add(layoutCircleTBB);

        layoutRandomTBB.setAction(actions_layout.LAYOUT_RANDOM);
        layoutRandomTBB.setIcon(new javax.swing.ImageIcon(getClass().getResource("/graphexplorer/resources/layout-random24.png"))); // NOI18N
        layoutRandomTBB.setFocusable(false);
        toolbar.add(layoutRandomTBB);

        layoutEnergyTBB.setIcon(new javax.swing.ImageIcon(getClass().getResource("/graphexplorer/resources/layout-spring24.png"))); // NOI18N
        layoutEnergyTBB.setToolTipText("Start/stop animation of the spring layout algorithm for currently displayed graph.");
        layoutEnergyTBB.setFocusable(false);
        layoutEnergyTBB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                layoutEnergyTBBActionPerformed(evt);
            }
        });
        toolbar.add(layoutEnergyTBB);
        toolbar.add(jSeparator3);

        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.X_AXIS));

        jLabel1.setFont(new java.awt.Font("Tahoma", 2, 13));
        jLabel1.setForeground(new java.awt.Color(102, 102, 102));
        jLabel1.setText("Metric: ");
        jPanel1.add(jLabel1);

        metricCB1.setModel(new DefaultComboBoxModel(graphexplorer.ExplorerStatActions.StatEnum.values()));
        metricCB1.setToolTipText("<html>\nCompute and display the specified metric.<br>\n<font color=\"gray\"><i>\nCurrently, the values \"None\", \"Other\",\nand \"Decay centrality (custom parameter)\" are nonfunctional.\n</i></font>");
        metricCB1.setMaximumSize(new java.awt.Dimension(32767, 22));

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, metricCB, org.jdesktop.beansbinding.ELProperty.create("${selectedItem}"), metricCB1, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        jPanel1.add(metricCB1);

        toolbar.add(jPanel1);
        toolbar.add(jSeparator9);

        getContentPane().add(toolbar, java.awt.BorderLayout.NORTH);

        mainP.setLayout(new java.awt.BorderLayout());

        mainSP.setDividerSize(8);
        mainSP.setOneTouchExpandable(true);

        graphTP.setPreferredSize(new java.awt.Dimension(800, 600));
        graphTP.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                graphTPStateChanged(evt);
            }
        });
        mainSP.setRightComponent(graphTP);

        propertySP.setPreferredSize(new java.awt.Dimension(200, 400));
        propertySP.setViewportView(propertyRP);

        mainSP.setLeftComponent(propertySP);

        mainP.add(mainSP, java.awt.BorderLayout.CENTER);

        boxPanel.setPreferredSize(new java.awt.Dimension(800, 250));
        boxPanel.setLayout(new javax.swing.BoxLayout(boxPanel, javax.swing.BoxLayout.LINE_AXIS));

        boxP1.setLayout(new java.awt.BorderLayout());

        mainTableTB.setFloatable(false);
        mainTableTB.setRollover(true);

        metricL.setText("Metric: ");
        mainTableTB.add(metricL);

        metricCB.setModel(new DefaultComboBoxModel(graphexplorer.ExplorerStatActions.StatEnum.values()));
        metricCB.setToolTipText("<html>\nCompute and display the specified metric.<br>\n<font color=\"gray\"><i>\nCurrently, the values \"None\", \"Other\",\nand \"Decay centrality (custom parameter)\" are nonfunctional.\n</i></font>");
        metricCB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                metricCBActionPerformed(evt);
            }
        });
        mainTableTB.add(metricCB);

        boxP1.add(mainTableTB, java.awt.BorderLayout.PAGE_START);

        mainTableSP.setViewportView(mainTable);

        boxP1.add(mainTableSP, java.awt.BorderLayout.CENTER);

        boxPanel.add(boxP1);

        boxP2.setLayout(new java.awt.BorderLayout());

        boxTP2.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);

        distributionTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        distributionTableSP.setViewportView(distributionTable);

        boxTP2.addTab("Metric Table", distributionTableSP);

        boxP2.add(boxTP2, java.awt.BorderLayout.CENTER);

        boxPanel.add(boxP2);

        boxP3.setLayout(new java.awt.BorderLayout());

        outputSP.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        outputTP.setBackground(new java.awt.Color(0, 0, 0));
        outputTP.setForeground(new java.awt.Color(204, 204, 255));
        outputTP.setText("== Output ==\n");
        outputTP.setPreferredSize(new java.awt.Dimension(500, 200));
        outputSP.setViewportView(outputTP);

        boxP3.add(outputSP, java.awt.BorderLayout.CENTER);

        boxPanel.add(boxP3);

        mainP.add(boxPanel, java.awt.BorderLayout.PAGE_END);

        getContentPane().add(mainP, java.awt.BorderLayout.CENTER);

        statusB.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));

        statusL.setFont(new java.awt.Font("Tahoma", 0, 12));
        statusL.setText("Status: ...");
        statusL.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        org.jdesktop.layout.GroupLayout statusBLayout = new org.jdesktop.layout.GroupLayout(statusB);
        statusB.setLayout(statusBLayout);
        statusBLayout.setHorizontalGroup(
            statusBLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(statusL, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 1111, Short.MAX_VALUE)
        );
        statusBLayout.setVerticalGroup(
            statusBLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(statusL)
        );

        getContentPane().add(statusB, java.awt.BorderLayout.PAGE_END);

        fileM.setText("File");

        newM.setIcon(new javax.swing.ImageIcon(getClass().getResource("/graphexplorer/resources/new-graph18.png"))); // NOI18N
        newM.setText("New Graph");

        emptyMI.setAction(actions_gen.GENERATE_EMPTY);
        emptyMI.setText("Empty graph...");
        newM.add(emptyMI);

        circleMI.setAction(actions_gen.GENERATE_CIRCLE);
        circleMI.setText("Circle graph...");
        newM.add(circleMI);

        starMI.setAction(actions_gen.GENERATE_STAR);
        starMI.setText("Star graph...");
        newM.add(starMI);

        wheelMI.setAction(actions_gen.GENERATE_WHEEL);
        wheelMI.setText("Wheel graph...");
        newM.add(wheelMI);

        completeMI.setAction(actions_gen.GENERATE_COMPLETE);
        completeMI.setText("Complete graph...");
        newM.add(completeMI);
        newM.add(jSeparator1);

        uniform1MI.setAction(actions_gen.GENERATE_RANDOM);
        uniform1MI.setText("Uniform (Erdos-Renyi) random graph...");
        newM.add(uniform1MI);

        sequenceMI.setAction(actions_gen.GENERATE_SEQUENCE);
        sequenceMI.setText("Degree sequence random graph...");
        newM.add(sequenceMI);

        wattsMI.setAction(actions_gen.GENERATE_WS);
        wattsMI.setText("Watts-Strogatz random graph...");
        newM.add(wattsMI);

        preferentialMI.setAction(actions_gen.GENERATE_PREFERENTIAL);
        preferentialMI.setText("Preferential Attachment...");
        newM.add(preferentialMI);

        fileM.add(newM);

        loadMI.setAction(actions_io.LOAD_ACTION);
        loadMI.setText("Load graph from file");
        fileM.add(loadMI);

        saveMI.setAction(actions_io.SAVE_ACTION);
        saveMI.setText("Save graph");
        fileM.add(saveMI);

        saveMI1.setAction(actions_io.SAVE_AS_ACTION);
        saveMI1.setText("Save graph as ...");
        fileM.add(saveMI1);

        closeMI.setAction(actions_io.CLOSE_ACTION);
        closeMI.setText("Close current graph");
        fileM.add(closeMI);
        fileM.add(jSeparator7);

        exportImageM.setText("Export image");
        exportImageM.setEnabled(false);
        fileM.add(exportImageM);

        exportMovieM.setText("Export movie");
        exportMovieM.setEnabled(false);

        export_movMI.setEnabled(false);
        exportMovieM.add(export_movMI);

        fileM.add(exportMovieM);
        fileM.add(jSeparator6);

        quitMI.setAction(actions.QUIT_ACTION);
        quitMI.setText("Quit");
        fileM.add(quitMI);

        menu.add(fileM);

        layoutM.setText("Layout");

        circularMI.setAction(actions_layout.LAYOUT_CIRCULAR);
        layoutM.add(circularMI);

        randomMI.setAction(actions_layout.LAYOUT_RANDOM);
        layoutM.add(randomMI);

        circularMI1.setAction(actions_layout.LAYOUT_SPRING_STATIC);
        layoutM.add(circularMI1);
        layoutM.add(jSeparator4);

        energyM.setIcon(new javax.swing.ImageIcon(getClass().getResource("/graphexplorer/resources/layout-spring18.png"))); // NOI18N
        energyM.setText("Animating layouts");

        energyStartMI.setAction(actions_layout.LAYOUT_ENERGY_START);
        energyStartMI.setIcon(new javax.swing.ImageIcon(getClass().getResource("/graphexplorer/resources/play18.png"))); // NOI18N
        energyM.add(energyStartMI);

        layoutM.add(energyM);

        energyIterateMI.setAction(actions_layout.LAYOUT_ITERATE);
        layoutM.add(energyIterateMI);

        energyStopMI.setAction(actions_layout.LAYOUT_STOP);
        layoutM.add(energyStopMI);

        menu.add(layoutM);

        metricM.setText("Metrics");

        localMetricM.setIcon(new javax.swing.ImageIcon(getClass().getResource("/graphexplorer/resources/metric-node18.png"))); // NOI18N
        localMetricM.setText("Local");
        metricM.add(localMetricM);

        globalMetricM.setIcon(new javax.swing.ImageIcon(getClass().getResource("/graphexplorer/resources/metric-graph18.png"))); // NOI18N
        globalMetricM.setText("Global");
        metricM.add(globalMetricM);

        menu.add(metricM);

        specialM.setText("Special");

        highlightMI.setAction(actions_stat.HIGHLIGHT);
        specialM.add(highlightMI);

        cooperationMI.setAction(actions_stat.COOPERATION);
        specialM.add(cooperationMI);

        menu.add(specialM);

        helpM.setText("Help");

        aboutMI.setAction(actions.ABOUT_ACTION);
        aboutMI.setText("About");
        helpM.add(aboutMI);

        contentMI.setAction(actions.HELP_ACTION);
        contentMI.setText("Show Help File");
        helpM.add(contentMI);

        menu.add(helpM);

        setJMenuBar(menu);

        bindingGroup.bind();

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void graphTPStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_graphTPStateChanged
        // update active graph based on selected tab
        Component active = graphTP.getSelectedComponent();
        if (active == null)
            master.setActiveController(null);
        else
            for (Entry<GraphController, Component> en : tabs.entrySet())
                if (en.getValue() == active) {
                    master.setActiveController(en.getKey());
                    return;
                }
    }//GEN-LAST:event_graphTPStateChanged

    private void metricCBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_metricCBActionPerformed
        GraphController gc = activeController();
        if (gc != null)
            gc.setMetric(((ExplorerStatActions.StatEnum)metricCB.getSelectedItem()).getMetric());
    }//GEN-LAST:event_metricCBActionPerformed

    private void layoutEnergyTBBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_layoutEnergyTBBActionPerformed
        // this button swaps the current reportStatus of the layout between playing & paused
        GraphController gc = activeController();
        if (gc != null) {
            if (gc.isAnimating())
                actions_layout.LAYOUT_STOP.actionPerformed(evt);
            else
                actions_layout.LAYOUT_ENERGY_START.actionPerformed(evt);
        } else {
            layoutEnergyTBB.setSelected(false);
        }
    }//GEN-LAST:event_layoutEnergyTBBActionPerformed

    private void newTBBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newTBBActionPerformed
        newPM.show(newTBB, 5, 5);
    }//GEN-LAST:event_newTBBActionPerformed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
        public void run() {
                new GraphExplorerMain().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem aboutMI;
    private javax.swing.JPanel boxP1;
    private javax.swing.JPanel boxP2;
    private javax.swing.JPanel boxP3;
    private javax.swing.JPanel boxPanel;
    private javax.swing.JTabbedPane boxTP2;
    private javax.swing.JMenuItem circleMI;
    private javax.swing.JMenuItem circleMI1;
    private javax.swing.JMenuItem circularMI;
    private javax.swing.JMenuItem circularMI1;
    private javax.swing.JMenuItem closeMI;
    private javax.swing.JMenuItem completeMI;
    private javax.swing.JMenuItem completeMI1;
    private javax.swing.JMenuItem contentMI;
    private javax.swing.JMenuItem cooperationMI;
    private javax.swing.JTable distributionTable;
    private javax.swing.JScrollPane distributionTableSP;
    private data.propertysheet.PropertySheet edgePS;
    private javax.swing.JMenuItem emptyMI;
    private javax.swing.JMenuItem emptyMI1;
    private javax.swing.JMenuItem energyIterateMI;
    private javax.swing.JMenu energyM;
    private javax.swing.JMenuItem energyStartMI;
    private javax.swing.JMenuItem energyStopMI;
    private javax.swing.JMenu exportImageM;
    private javax.swing.JMenu exportMovieM;
    private javax.swing.JMenuItem export_movMI;
    private javax.swing.JMenu fileM;
    private javax.swing.JMenu globalMetricM;
    private javax.swing.JTabbedPane graphTP;
    private javax.swing.JMenu helpM;
    private javax.swing.JMenuItem highlightMI;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparator4;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JPopupMenu.Separator jSeparator6;
    private javax.swing.JPopupMenu.Separator jSeparator7;
    private javax.swing.JToolBar.Separator jSeparator9;
    private data.propertysheet.PropertySheet labelPS;
    private javax.swing.JButton layoutCircleTBB;
    private javax.swing.JToggleButton layoutEnergyTBB;
    private javax.swing.JMenu layoutM;
    private data.propertysheet.PropertySheet layoutPS;
    private javax.swing.JButton layoutRandomTBB;
    private javax.swing.JMenuItem loadMI;
    private javax.swing.JButton loadTBB;
    private javax.swing.JMenu localMetricM;
    private javax.swing.JPanel mainP;
    private javax.swing.JSplitPane mainSP;
    private graphexplorer.GraphTable mainTable;
    private javax.swing.JScrollPane mainTableSP;
    private javax.swing.JToolBar mainTableTB;
    private javax.swing.JMenuBar menu;
    private javax.swing.JComboBox metricCB;
    private javax.swing.JComboBox metricCB1;
    private javax.swing.JLabel metricL;
    private javax.swing.JMenu metricM;
    private javax.swing.ButtonGroup metricMenuBG;
    private javax.swing.JMenu newM;
    private javax.swing.JPopupMenu newPM;
    private javax.swing.JButton newTBB;
    private data.propertysheet.PropertySheet nodePS;
    private javax.swing.JScrollPane outputSP;
    private javax.swing.JTextPane outputTP;
    private javax.swing.JMenuItem preferentialMI;
    private javax.swing.JMenuItem preferentialMI1;
    private gui.RollupPanel propertyRP;
    private javax.swing.JScrollPane propertySP;
    private javax.swing.JMenuItem quitMI;
    private javax.swing.JMenuItem randomMI;
    private javax.swing.JMenuItem saveMI;
    private javax.swing.JMenuItem saveMI1;
    private javax.swing.JButton saveTBB;
    private javax.swing.JMenuItem sequenceMI;
    private javax.swing.JMenuItem sequenceMI1;
    private javax.swing.JMenu specialM;
    private javax.swing.JMenuItem starMI;
    private javax.swing.JMenuItem starMI1;
    private javax.swing.JPanel statusB;
    private javax.swing.JLabel statusL;
    private javax.swing.JToolBar toolbar;
    private javax.swing.JMenuItem uniform1MI;
    private javax.swing.JMenuItem uniform1MI1;
    private javax.swing.JMenuItem wattsMI;
    private javax.swing.JMenuItem wattsMI1;
    private javax.swing.JMenuItem wheelMI;
    private javax.swing.JMenuItem wheelMI1;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //
    // UTILITIES
    //

    public Component dialogComponent() {
        return this;
    }

    public void output(String output) {
        Document d = outputTP.getDocument();
        try {
            d.insertString(d.getLength(), output + "\n\n", null);
        } catch (BadLocationException ex) {
            Logger.getLogger(GraphExplorerMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public GraphController activeController() {
        return master.getActiveController();
    }

    Graph activeGraph() {
        GraphController gc = master.getActiveController();
        return gc == null ? null : gc.getActiveGraph();
    }

    AbstractPlaneGraph activePlaneGraph() {
        GraphController gc = master.getActiveController();
        return graphs.get(gc);
    }

    PlotComponent activePlotComponent() {
        GraphController gc = master.getActiveController();
        Component c = tabs.get(gc);
        if (c instanceof PlotComponent)
            return (PlotComponent) c;
        else if (c instanceof LongitudinalGraphPanel) {
            return ((LongitudinalGraphPanel)c).plot;
        }
        return null;
    }

    /** Updates the property panel with currently active graph and energy layout */
    private void updatePropertyPanel() {
        propertyRP.removeAll();
        AbstractPlaneGraph active = activePlaneGraph();
        if (active == null) {
            nodePS = null;
            edgePS = null;
            labelPS = null;
            layoutPS = null;
        } else {
            if (active instanceof PlaneGraph) {
                PlaneGraphBean bean = new PlaneGraphBean((PlaneGraph)active);
                propertyRP.add(nodePS = new PropertySheet(bean.nodeBean()), "Node Settings");
                propertyRP.add(edgePS = new PropertySheet(bean.edgeBean()), "Edge Settings");
                propertyRP.add(labelPS = new PropertySheet(bean.labelBean()), "Label Settings");
            } else if (active instanceof ImageGraph) {
                ImageGraphBean bean = new ImageGraphBean((ImageGraph)active);
                propertyRP.add(nodePS = new PropertySheet(bean.nodeBean()), "Node Settings");
                propertyRP.add(edgePS = new PropertySheet(bean.edgeBean()), "Edge Settings");
            }
            IterativeGraphLayout layout = activeController().getIterativeLayout();
            if (layout != null)
                propertyRP.add(layoutPS = new PropertySheet(layout), "Iterative Layout Settings");
            else
                layoutPS = null;
        }
    }

    /** Updates metric combo box */
    private void updateMetricComboBox() {
        GraphController gc = activeController();
        if (gc == null) {
            metricCB.setSelectedItem(StatEnum.NONE);
            locMetricMI.get(StatEnum.NONE).setSelected(true);
        } else {
            NodeMetric nm = activeController().getMetric();
            StatEnum found = null;
            for (StatEnum item : StatEnum.values())
                if (item.getMetric() == nm) {
                    found = item;
                    break;
                }
            metricCB.setSelectedItem(found);
            locMetricMI.get(found).setSelected(true);
        }
    }

    /** Updates metric chart and table for current reportStatus */
    private void updateChart() {
        GraphController gc = activeController();
        List values = gc == null ? null : gc.getMetricValues();
        if (values == null) {
            distributionCP.setChart(null);
            distributionTable.setModel(new DefaultTableModel());
            boxPanel.validate();
        } else {
            DistributionTableModel dtm = new DistributionTableModel(values);
            distributionTable.setModel(dtm);

            int[] counts = dtm.counts;
            int nSamples = counts.length;
            double[][] data = new double[6][counts.length];
            double x, y;
            for (int i = 0; i < nSamples; i++) {
                x = ((Number)dtm.values[i]).doubleValue();
                y = dtm.counts[i];
                data[0][i] = data[1][i] = data[2][i] = x;
                data[3][i] = data[5][i] = y;
                data[4][i] = 0;
            }

            // set up the chart
            DefaultIntervalXYDataset chartData = new DefaultIntervalXYDataset();
                chartData.addSeries(gc.getMetric().toString() + " counts", data);
            NumberAxis xAxis = new NumberAxis(gc.getMetric().toString());
                xAxis.setAutoRangeIncludesZero(false);
            NumberAxis yAxis = new NumberAxis("Number of Nodes");
            YIntervalRenderer renderer = new YIntervalRenderer();
            renderer.setBaseToolTipGenerator(new org.jfree.chart.labels.StandardXYToolTipGenerator());
            XYPlot plot = new XYPlot(chartData, xAxis, yAxis, renderer);
                plot.setOrientation(PlotOrientation.VERTICAL);
            distributionCP.setChart(new JFreeChart(
                    gc.getMetric().toString() + " distribution", JFreeChart.DEFAULT_TITLE_FONT,
                    plot, false));
            
            boxPanel.validate();
        }
    }

    /** Updates the longitudinal metric chart, provided the graph is longitudinal */
    private void updateLongChart() {
        GraphController gc = activeController();
        if (gc != null && gc.isLongitudinal()) {
            LongitudinalMetricPlot lmp = new LongitudinalMetricPlot(gc);
            longMetricCP.setChart(lmp.getChart());
            boxTP2.add(longMetricCP, "Longitudinal Metric Chart");
        } else {
            longMetricCP.setChart(null);
            boxTP2.remove(longMetricCP);
        }
    }

    /** Updates the sizes of the active nodes */
    private void updateNodeSizes() {
        GraphController gc = activeController();
        if (gc != null && activePlaneGraph() instanceof PlaneGraph) {
            PlaneGraph pg = (PlaneGraph) activePlaneGraph();
            List values = gc.getMetricValues();
            pg.setNodeValues(values);

            if (values == null) {
                pg.getPointStyle().setRadiusMultiplier(1.0);
            } else {
                double max = 0;
                for(int i = 0; i < values.size(); i++)
                    max = Math.max(max, ((Number) values.get(i)).doubleValue());
                pg.getPointStyle().setRadiusMultiplier(1.0/Math.sqrt(max));
            }
        }
    }

    /** Updates the currently active graph */
    private void updateActiveGraph() {
//        System.out.println("ugt: " + master.getControllers().size() + " controllers");
        // remove anything not in the master controller
        HashSet<GraphController> set = new HashSet<GraphController>();
        for (GraphController gc : tabs.keySet())
            if (!master.containsController(gc))
                set.add(gc);

        for (GraphController gc : set) {
            gc.removePropertyChangeListener(this);
            graphs.get(gc).removeChangeListener(this);
            graphTP.remove(tabs.get(gc));
            tabs.remove(gc);
            graphs.remove(gc);
        }

        // add anything from the master controller
        set.clear();
        for (GraphController gc : master.getControllers())
            if (!(tabs.containsKey(gc)))
                set.add(gc);

        Component c = null;
        boolean imageGraph = false;

        for (GraphController gc : set) {
            // check to see if graph contains images (ad-hoc)
            Graph ag = gc.getActiveGraph();
            List nodes = ag.nodes();
            if (ag instanceof ValuedGraph && nodes.size() > 0) {
                ValuedGraph vg = (ValuedGraph) ag;
                Object o1 = vg.getValue(nodes.get(0));
                if (o1.getClass().equals(Object[].class)) {
                    Object[] o2 = (Object[]) o1;
                    if (o2.length >= 2 && Image.class.isAssignableFrom(o2[1].getClass()))
                        imageGraph = true;
                }
            }

            if (gc.isLongitudinal()) {
                c = new LongitudinalGraphPanel(gc);
                PlaneGraph plottedGraph = ((LongitudinalGraphPanel)c).getPlaneGraph();
                graphs.put(gc, plottedGraph);
                gc.setPositions(plottedGraph.getPositionMap());
            } else if (imageGraph) {
                c = new PlanePlotComponent();
                ImageGraph plottedGraph = new ImageGraph(ag);
                ((PlanePlotComponent)c).add(plottedGraph);
                graphs.put(gc, plottedGraph);
                gc.setPositions(plottedGraph.getPositionMap());
            } else {
                c = new PlanePlotComponent();
                PlaneGraph plottedGraph = new PlaneGraph(gc.getActiveGraph());
                ((PlanePlotComponent)c).add(plottedGraph);
                graphs.put(gc, plottedGraph);
                plottedGraph.highlightNodes(gc.getNodeSubset());
                gc.setPositions(plottedGraph.getPositionMap());
            }
            tabs.put(gc, c);
            graphTP.add(c, gc.getName());
        }

        // set up for the active controller
        GraphController gc = activeController();
        updating = true;
        if (graphTP.getSelectedComponent() != tabs.get(gc))
            graphTP.setSelectedComponent(tabs.get(gc));
        updatePropertyPanel();
        updateMetricComboBox();
        updateChart();
        updateNodeSizes();
        updateLongChart();
        if (activePlaneGraph() != null)
            activePlaneGraph().addChangeListener(this);

        if (gc != null) {
            gc.addPropertyChangeListener(this);
            exportImageM.setEnabled(true);
            exportImageM.removeAll();
            for (Action a : actions_io.imageActions(activePlotComponent()))
                exportImageM.add(a);
        } else {
            exportImageM.setEnabled(false);
            exportImageM.removeAll();
        }

        if (gc != null && gc.isLongitudinal()) {
            export_movMI.setAction(new ExplorerIOActions.MovieAction(gc, (LongitudinalGraphPanel) tabs.get(gc)));
            exportMovieM.setEnabled(true);
        } else {
            export_movMI.setEnabled(false);
            exportMovieM.setEnabled(false);
        }

        actions_stat.setController(gc);
        actions_layout.setController(gc);
        updating = false;
    }

    //
    // PROPERTY CHANGES FED FROM THE CONTROLLERS
    //

    boolean updating = false;

    public void stateChanged(ChangeEvent e) {
        if (e.getSource() == activePlaneGraph())
            activeController().setPositions(activePlaneGraph().getPositionMap());
    }

    int pcn = 0;

    public void propertyChange(PropertyChangeEvent evt) {
//        if (!evt.getPropertyName().equals("positions"))
//            reportOutput("Property change " + (pcn++) + " src=" + evt.getSource() + " & name=" + evt.getPropertyName());
        if (evt.getPropertyName().equals("output"))
            output((String) evt.getNewValue());
        else if (evt.getPropertyName().equals("status"))
            statusL.setText((String) evt.getNewValue());
        else if (evt.getSource() == master) {
            if (evt.getPropertyName().equals("active"))
                updateActiveGraph();
            else
                output("Unknown property change event, name = " + evt.getPropertyName());
        } else if (evt.getSource() == activeController()) {
            GraphController gc = activeController();
            if (evt.getPropertyName().equals("primary")) {
                // not supported yet!
            } else if (evt.getPropertyName().equals("name")) {
                Component c = tabs.get(gc);
                int index = graphTP.indexOfComponent(c);
                graphTP.setTitleAt(index, (String) evt.getNewValue());
            } else if (evt.getPropertyName().equals("animating")) {
                boolean animating = (Boolean) evt.getNewValue();
                layoutEnergyTBB.setSelected(animating);
                layoutEnergyTBB.setText(animating ? "Stop" : "Start");
            } else if (evt.getPropertyName().equals("metric")) {
                updateMetricComboBox();
            } else if (evt.getPropertyName().equals("time") || evt.getPropertyName().equals("values")) {
                // longitudinal panel works directly with time & will update the active displayed graph
                updateChart();
                updateNodeSizes();
            } else if (evt.getPropertyName().equals("layout"))
                updatePropertyPanel();
            else if (evt.getPropertyName().equals("node value"))
                activePlaneGraph().updateLabels();
            else if (evt.getPropertyName().equals("positions"))
                activePlaneGraph().setPositionMap(gc.getPositions());
            else if (evt.getPropertyName().equals("subset"))
                activePlaneGraph().highlightNodes(gc.getNodeSubset());
            else
                output("Unknown property change event, name = " + evt.getPropertyName());
        }
    }

}
