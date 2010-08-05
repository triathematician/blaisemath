/*
 * GraphExplorerMain.java
 * Created on May 14, 2010, 10:10:30 AM
 */

package graphexplorer;

import data.propertysheet.PropertySheet;
import data.propertysheet.editor.EditorRegistration;
import graphexplorer.ExplorerStatActions.StatEnum;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import org.bm.blaise.scio.graph.Graph;
import org.bm.blaise.scio.graph.layout.IterativeGraphLayout;
import org.bm.blaise.scio.graph.metrics.NodeMetric;
import org.bm.blaise.scio.graph.metrics.subset.AdditiveSubsetMetric;
import org.bm.blaise.scio.graph.metrics.subset.ContractiveSubsetMetric;
import org.bm.blaise.scio.graph.metrics.subset.CooperationSubsetMetric;
import org.bm.blaise.specto.plane.graph.PlaneGraph;
import org.bm.blaise.specto.plane.graph.PlaneGraphBean;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.YIntervalRenderer;
import org.jfree.data.xy.DefaultIntervalXYDataset;
import visometry.plane.PlanePlotComponent;

/**
 *
 * @author Elisha Peterson
 */
public class GraphExplorerMain extends javax.swing.JFrame
        implements GraphExplorerInterface,
            ChangeListener, PropertyChangeListener {

    /** Controller */
    GraphControllerMaster master;
    /** Controllers and associated components */
    HashMap<GraphController, Component> tabs = new HashMap<GraphController, Component>();
    /** Controllers and associated plane graph elements */
    HashMap<GraphController, PlaneGraph> graphs = new HashMap<GraphController, PlaneGraph>();
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
        initMetricMenu();
        master.addPropertyChangeListener(mainTM);
        toolbar.add(javax.swing.Box.createHorizontalGlue());
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
    private EnumMap<StatEnum, JRadioButtonMenuItem> metricMenuItems;

    private void initMetricMenu() {
        metricMenuItems = new EnumMap<StatEnum, JRadioButtonMenuItem>(StatEnum.class);
        for (StatEnum se : StatEnum.values()) {
            JRadioButtonMenuItem mi = new JRadioButtonMenuItem(actions_stat.actionOf(se));
            metricMenuBG.add(mi);
            metricM.add(mi);
            metricMenuItems.put(se, mi);
            if (se == StatEnum.NONE)
                mi.setSelected(true);
        }
    }

    private void initCharts() {
        JFreeChart distributionFC = ChartFactory.createXYBarChart("Metric Distribution", "Value", false, "Number", null, PlotOrientation.VERTICAL, false, true, false);
        distributionCP = new ChartPanel(distributionFC);
        distributionCP.setPreferredSize(new Dimension(400,300));
        boxP2.add(distributionCP, BorderLayout.CENTER);
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
        mainTM = new graphexplorer.GraphTableModel();
        distributionTableSP = new javax.swing.JScrollPane();
        distributionTable = new javax.swing.JTable();
        newPM = new javax.swing.JPopupMenu();
        emptyMI1 = new javax.swing.JMenuItem();
        circleMI1 = new javax.swing.JMenuItem();
        starMI1 = new javax.swing.JMenuItem();
        wheelMI1 = new javax.swing.JMenuItem();
        completeMI1 = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JPopupMenu.Separator();
        uniform1MI1 = new javax.swing.JMenuItem();
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
        mainSP = new javax.swing.JSplitPane();
        graphTP = new javax.swing.JTabbedPane();
        propertySP = new javax.swing.JScrollPane();
        propertyRP = new gui.RollupPanel();
        boxPanel = new javax.swing.JPanel();
        boxP1 = new javax.swing.JPanel();
        boxTP1 = new javax.swing.JTabbedPane();
        mainTableP = new javax.swing.JPanel();
        mainTableTB = new javax.swing.JToolBar();
        metricL = new javax.swing.JLabel();
        metricCB = new javax.swing.JComboBox();
        mainTableSP = new javax.swing.JScrollPane();
        mainTable = new javax.swing.JTable();
        boxP2 = new javax.swing.JPanel();
        boxP3 = new javax.swing.JPanel();
        boxP4 = new javax.swing.JPanel();
        outputSP = new javax.swing.JScrollPane();
        outputTP = new javax.swing.JTextPane();
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
        preferentialMI = new javax.swing.JMenuItem();
        loadM = new javax.swing.JMenu();
        loadEdgeListMI = new javax.swing.JMenuItem();
        loadPajekMI = new javax.swing.JMenuItem();
        loadPajekMI2 = new javax.swing.JMenuItem();
        loadPajekXMI = new javax.swing.JMenuItem();
        loadPajekXLongMI = new javax.swing.JMenuItem();
        loadPajekMI1 = new javax.swing.JMenuItem();
        jMenu1 = new javax.swing.JMenu();
        saveEdgeListMI = new javax.swing.JMenuItem();
        savePajekMI = new javax.swing.JMenuItem();
        savePajekMI1 = new javax.swing.JMenuItem();
        savePajekXMI = new javax.swing.JMenuItem();
        savePajekXMI2 = new javax.swing.JMenuItem();
        savePajekXMI1 = new javax.swing.JMenuItem();
        closeMI = new javax.swing.JMenuItem();
        jSeparator7 = new javax.swing.JPopupMenu.Separator();
        exportM = new javax.swing.JMenu();
        exportQTMI = new javax.swing.JMenuItem();
        jSeparator6 = new javax.swing.JPopupMenu.Separator();
        quitMI = new javax.swing.JMenuItem();
        layoutM = new javax.swing.JMenu();
        circularMI = new javax.swing.JMenuItem();
        randomMI = new javax.swing.JMenuItem();
        energyM = new javax.swing.JMenu();
        energyStartMI = new javax.swing.JMenuItem();
        energyStopMI = new javax.swing.JMenuItem();
        energyIterateMI = new javax.swing.JMenuItem();
        jSeparator8 = new javax.swing.JPopupMenu.Separator();
        highlightMI = new javax.swing.JMenuItem();
        metricM = new javax.swing.JMenu();
        cooperationMI = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JPopupMenu.Separator();
        helpM = new javax.swing.JMenu();
        aboutMI = new javax.swing.JMenuItem();
        contentMI = new javax.swing.JMenuItem();

        mainTM.addTableModelListener(new javax.swing.event.TableModelListener() {
            public void tableChanged(javax.swing.event.TableModelEvent evt) {
                mainTMTableChanged(evt);
            }
        });

        distributionTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        distributionTableSP.setViewportView(distributionTable);

        emptyMI1.setAction(actions_gen.GENERATE_EMPTY);
        emptyMI1.setText("Empty Graph");
        newPM.add(emptyMI1);

        circleMI1.setAction(actions_gen.GENERATE_CIRCLE);
        circleMI1.setText("Circle Graph");
        newPM.add(circleMI1);

        starMI1.setAction(actions_gen.GENERATE_STAR);
        starMI1.setText("Star Graph");
        newPM.add(starMI1);

        wheelMI1.setAction(actions_gen.GENERATE_WHEEL);
        wheelMI1.setText("Wheel Graph");
        newPM.add(wheelMI1);

        completeMI1.setAction(actions_gen.GENERATE_COMPLETE);
        completeMI1.setText("Complete Graph");
        newPM.add(completeMI1);
        newPM.add(jSeparator5);

        uniform1MI1.setAction(actions_gen.GENERATE_RANDOM);
        uniform1MI1.setText("Uniform random graph...");
        newPM.add(uniform1MI1);

        preferentialMI1.setAction(actions_gen.GENERATE_PREFERENTIAL);
        preferentialMI1.setText("Preferential Attachment...");
        newPM.add(preferentialMI1);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Graph Explorer");

        toolbar.setRollover(true);

        newTBB.setIcon(new javax.swing.ImageIcon(getClass().getResource("/graphexplorer/resources/new-graph.png"))); // NOI18N
        newTBB.setText("New...");
        newTBB.setComponentPopupMenu(newPM);
        newTBB.setFocusable(false);
        newTBB.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        newTBB.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        newTBB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newTBBActionPerformed(evt);
            }
        });
        toolbar.add(newTBB);

        loadTBB.setAction(actions_io.LOAD_PAJEK_ACTION);
        loadTBB.setIcon(new javax.swing.ImageIcon(getClass().getResource("/graphexplorer/resources/load-graph.png"))); // NOI18N
        loadTBB.setText("Load (.net)");
        loadTBB.setFocusable(false);
        loadTBB.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        loadTBB.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolbar.add(loadTBB);

        saveTBB.setAction(actions_io.SAVE_PAJEK_ACTION);
        saveTBB.setIcon(new javax.swing.ImageIcon(getClass().getResource("/graphexplorer/resources/save-graph.png"))); // NOI18N
        saveTBB.setText("Save (.net)");
        saveTBB.setFocusable(false);
        saveTBB.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        saveTBB.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolbar.add(saveTBB);
        toolbar.add(jSeparator2);

        layoutCircleTBB.setAction(actions_layout.LAYOUT_CIRCULAR);
        layoutCircleTBB.setIcon(new javax.swing.ImageIcon(getClass().getResource("/graphexplorer/resources/layout-circle.png"))); // NOI18N
        layoutCircleTBB.setText("Circular Layout");
        layoutCircleTBB.setFocusable(false);
        layoutCircleTBB.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        layoutCircleTBB.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolbar.add(layoutCircleTBB);

        layoutRandomTBB.setAction(actions_layout.LAYOUT_RANDOM);
        layoutRandomTBB.setIcon(new javax.swing.ImageIcon(getClass().getResource("/graphexplorer/resources/layout-random.png"))); // NOI18N
        layoutRandomTBB.setText("Random Layout");
        layoutRandomTBB.setFocusable(false);
        layoutRandomTBB.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        layoutRandomTBB.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolbar.add(layoutRandomTBB);

        layoutEnergyTBB.setIcon(new javax.swing.ImageIcon(getClass().getResource("/graphexplorer/resources/layout-spring.png"))); // NOI18N
        layoutEnergyTBB.setText("Spring Layout (Animating)");
        layoutEnergyTBB.setToolTipText("Plays/pauses energy layout mechanism for current graph");
        layoutEnergyTBB.setFocusable(false);
        layoutEnergyTBB.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        layoutEnergyTBB.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
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
        metricCB1.setMaximumSize(new java.awt.Dimension(32767, 22));

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, metricCB, org.jdesktop.beansbinding.ELProperty.create("${selectedItem}"), metricCB1, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        jPanel1.add(metricCB1);

        toolbar.add(jPanel1);
        toolbar.add(jSeparator9);

        getContentPane().add(toolbar, java.awt.BorderLayout.NORTH);

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

        getContentPane().add(mainSP, java.awt.BorderLayout.CENTER);

        boxPanel.setPreferredSize(new java.awt.Dimension(800, 250));
        boxPanel.setLayout(new javax.swing.BoxLayout(boxPanel, javax.swing.BoxLayout.LINE_AXIS));

        boxP1.setLayout(new java.awt.BorderLayout());

        boxTP1.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);

        mainTableP.setLayout(new java.awt.BorderLayout());

        mainTableTB.setFloatable(false);
        mainTableTB.setRollover(true);

        metricL.setText("Metric: ");
        mainTableTB.add(metricL);

        metricCB.setModel(new DefaultComboBoxModel(graphexplorer.ExplorerStatActions.StatEnum.values()));
        metricCB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                metricCBActionPerformed(evt);
            }
        });
        mainTableTB.add(metricCB);

        mainTableP.add(mainTableTB, java.awt.BorderLayout.PAGE_START);

        mainTable.setModel(mainTM);
        mainTableSP.setViewportView(mainTable);

        mainTableP.add(mainTableSP, java.awt.BorderLayout.CENTER);

        boxTP1.addTab("Table of Vertices", mainTableP);

        boxP1.add(boxTP1, java.awt.BorderLayout.CENTER);

        boxPanel.add(boxP1);

        boxP2.setLayout(new java.awt.BorderLayout());
        boxPanel.add(boxP2);

        boxP3.setLayout(new java.awt.BorderLayout());
        boxPanel.add(boxP3);

        boxP4.setLayout(new java.awt.BorderLayout());

        outputSP.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        outputTP.setBackground(new java.awt.Color(0, 0, 0));
        outputTP.setForeground(new java.awt.Color(204, 204, 255));
        outputTP.setText("== Output ==\n");
        outputTP.setPreferredSize(new java.awt.Dimension(500, 200));
        outputSP.setViewportView(outputTP);

        boxP4.add(outputSP, java.awt.BorderLayout.CENTER);

        boxPanel.add(boxP4);

        getContentPane().add(boxPanel, java.awt.BorderLayout.PAGE_END);

        fileM.setText("File");

        newM.setIcon(new javax.swing.ImageIcon(getClass().getResource("/graphexplorer/resources/new-graph24.png"))); // NOI18N
        newM.setText("New Graph");

        emptyMI.setAction(actions_gen.GENERATE_EMPTY);
        emptyMI.setText("Empty Graph");
        newM.add(emptyMI);

        circleMI.setAction(actions_gen.GENERATE_CIRCLE);
        circleMI.setText("Circle Graph");
        newM.add(circleMI);

        starMI.setAction(actions_gen.GENERATE_STAR);
        starMI.setText("Star Graph");
        newM.add(starMI);

        wheelMI.setAction(actions_gen.GENERATE_WHEEL);
        wheelMI.setText("Wheel Graph");
        newM.add(wheelMI);

        completeMI.setAction(actions_gen.GENERATE_COMPLETE);
        completeMI.setText("Complete Graph");
        newM.add(completeMI);
        newM.add(jSeparator1);

        uniform1MI.setAction(actions_gen.GENERATE_RANDOM);
        uniform1MI.setText("Uniform random graph...");
        newM.add(uniform1MI);

        preferentialMI.setAction(actions_gen.GENERATE_PREFERENTIAL);
        preferentialMI.setText("Preferential Attachment...");
        newM.add(preferentialMI);

        fileM.add(newM);

        loadM.setIcon(new javax.swing.ImageIcon(getClass().getResource("/graphexplorer/resources/load-graph24.png"))); // NOI18N
        loadM.setText("Load graph from file");

        loadEdgeListMI.setAction(actions_io.LOAD_EDGELIST_ACTION);
        loadEdgeListMI.setText("Edge list format (.txt)");
        loadM.add(loadEdgeListMI);

        loadPajekMI.setAction(actions_io.LOAD_PAJEK_ACTION);
        loadPajekMI.setText("Pajek format (.net)");
        loadM.add(loadPajekMI);

        loadPajekMI2.setAction(actions_io.LOAD_PAJEKLONG_ACTION);
        loadPajekMI2.setText("Pajek format - longitudinal (.net)");
        loadM.add(loadPajekMI2);

        loadPajekXMI.setAction(actions_io.LOAD_PAJEKX_ACTION);
        loadPajekXMI.setText("Extended Pajek format (.netx)");
        loadM.add(loadPajekXMI);

        loadPajekXLongMI.setAction(actions_io.LOAD_PAJEKXLONG_ACTION);
        loadPajekXLongMI.setText("Extended Pajek format - longitudinal (.netl)");
        loadM.add(loadPajekXLongMI);

        loadPajekMI1.setAction(actions_io.LOAD_UCINET_ACTION);
        loadPajekMI1.setText("UCINet format (.dat)");
        loadM.add(loadPajekMI1);

        fileM.add(loadM);

        jMenu1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/graphexplorer/resources/save-graph24.png"))); // NOI18N
        jMenu1.setText("Save graph to file");

        saveEdgeListMI.setAction(actions_io.SAVE_EDGELIST_ACTION);
        saveEdgeListMI.setText("Edge list format (.txt)");
        jMenu1.add(saveEdgeListMI);

        savePajekMI.setAction(actions_io.SAVE_PAJEK_ACTION);
        savePajekMI.setText("Pajek format (.net)");
        jMenu1.add(savePajekMI);

        savePajekMI1.setAction(actions_io.SAVE_PAJEKLONG_ACTION);
        savePajekMI1.setText("Pajek format - longitudinal (.net)");
        jMenu1.add(savePajekMI1);

        savePajekXMI.setAction(actions_io.SAVE_PAJEKX_ACTION);
        savePajekXMI.setText("Extended Pajek format (.netx)");
        jMenu1.add(savePajekXMI);

        savePajekXMI2.setAction(actions_io.SAVE_PAJEKXLONG_ACTION);
        savePajekXMI2.setText("Extended Pajek format - longitudinal (.netl)");
        jMenu1.add(savePajekXMI2);

        savePajekXMI1.setAction(actions_io.SAVE_UCINET_ACTION);
        savePajekXMI1.setText("UCINet format (.dat)");
        jMenu1.add(savePajekXMI1);

        fileM.add(jMenu1);

        closeMI.setAction(actions_io.CLOSE_ACTION);
        closeMI.setText("Close current graph");
        fileM.add(closeMI);
        fileM.add(jSeparator7);

        exportM.setText("Export");

        exportQTMI.setText("Quicktime movie (.mov)");
        exportM.add(exportQTMI);

        fileM.add(exportM);
        fileM.add(jSeparator6);

        quitMI.setAction(actions_io.QUIT_ACTION);
        quitMI.setText("Quit");
        fileM.add(quitMI);

        menu.add(fileM);

        layoutM.setText("Layout");

        circularMI.setAction(actions_layout.LAYOUT_CIRCULAR);
        circularMI.setText("Circular");
        layoutM.add(circularMI);

        randomMI.setAction(actions_layout.LAYOUT_RANDOM);
        randomMI.setText("Random");
        layoutM.add(randomMI);

        energyM.setIcon(new javax.swing.ImageIcon(getClass().getResource("/graphexplorer/resources/layout-spring24.png"))); // NOI18N
        energyM.setText("Energy");

        energyStartMI.setAction(actions_layout.LAYOUT_ENERGY_START);
        energyStartMI.setText("Start");
        energyM.add(energyStartMI);

        energyStopMI.setAction(actions_layout.LAYOUT_ENERGY_STOP);
        energyStopMI.setText("Stop");
        energyM.add(energyStopMI);

        energyIterateMI.setAction(actions_layout.LAYOUT_ENERGY_ITERATE);
        energyIterateMI.setText("Iterate");
        energyM.add(energyIterateMI);

        layoutM.add(energyM);
        layoutM.add(jSeparator8);

        highlightMI.setText("Highlight Nodes");
        highlightMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                highlightMIActionPerformed(evt);
            }
        });
        layoutM.add(highlightMI);

        menu.add(layoutM);

        metricM.setText("Metrics");

        cooperationMI.setText("Compute Cooperation");
        cooperationMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cooperationMIActionPerformed(evt);
            }
        });
        metricM.add(cooperationMI);
        metricM.add(jSeparator4);

        menu.add(metricM);

        helpM.setText("Help");

        aboutMI.setAction(actions.ABOUT_ACTION);
        aboutMI.setText("About");
        helpM.add(aboutMI);

        contentMI.setAction(actions.HELP_ACTION);
        contentMI.setText("Content");
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

    private void mainTMTableChanged(javax.swing.event.TableModelEvent evt) {//GEN-FIRST:event_mainTMTableChanged
        // only look at individual cell updates
        if (evt.getType() == TableModelEvent.UPDATE && mainTM.isLabelColumn(evt.getColumn()) && evt.getFirstRow() != TableModelEvent.HEADER_ROW) {
            Object node = activeGraph().nodes().get(evt.getFirstRow());
            String label = (String) mainTM.getValueAt(evt.getFirstRow(), evt.getColumn());
            activeController().setActiveGraphLabel(node, label);
        }
    }//GEN-LAST:event_mainTMTableChanged

    private void metricCBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_metricCBActionPerformed
        GraphController gc = activeController();
        if (gc != null)
            gc.setMetric(((ExplorerStatActions.StatEnum)metricCB.getSelectedItem()).getMetric());
    }//GEN-LAST:event_metricCBActionPerformed

    private void layoutEnergyTBBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_layoutEnergyTBBActionPerformed
        // this button swaps the current status of the layout between playing & paused
        GraphController gc = activeController();
        if (gc != null) {
            if (gc.isAnimating())
                actions_layout.LAYOUT_ENERGY_STOP.actionPerformed(evt);
            else
                actions_layout.LAYOUT_ENERGY_START.actionPerformed(evt);
        } else {
            layoutEnergyTBB.setSelected(false);
        }
    }//GEN-LAST:event_layoutEnergyTBBActionPerformed

    private void cooperationMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cooperationMIActionPerformed
        Graph active = activeGraph();
        if (active == null)
            return;
        CooperationPanel cp = new CooperationPanel();
        JDialog dialog = new JDialog(this, true);
        dialog.add(cp);
        dialog.pack();
        dialog.setVisible(true);
        // wait until user closes
        NodeMetric m = cp.getMetric();
        Collection<Integer> subset = cp.getSubset();
        CooperationSubsetMetric m1 = new CooperationSubsetMetric(new AdditiveSubsetMetric(m));
        CooperationSubsetMetric m2 = new CooperationSubsetMetric(new ContractiveSubsetMetric(m));
        double[] v1 = m1.getValue(active, subset);
        double[] v2 = m2.getValue(active, subset);
        output("Computed additive metric with " + m + "   : selfish = " + v1[0] + ", altruistic = " + v1[1] + ", total = " + (v1[0]+v1[1]));
        output("Computed contractive metric with " + m + ": selfish = " + v2[0] + ", altruistic = " + v2[1] + ", total = " + (v2[0]+v2[1]));
    }//GEN-LAST:event_cooperationMIActionPerformed

    private void newTBBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newTBBActionPerformed
        newPM.show(newTBB, 5, 5);
    }//GEN-LAST:event_newTBBActionPerformed

    private void highlightMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_highlightMIActionPerformed
        List nodes = activeGraph().nodes();
        int[] subset = ExplorerGenerateActions.showIntegerArrayInputDialog(
                "Enter comma-separated list of vertices (by integer index, 0-" + (nodes.size()-1) + ")",
                0, nodes.size()-1);
        if (subset == null)
            activeController().setNodeSubset(null);
        else {
            TreeSet set = new TreeSet();
            for (int i : subset)
                set.add(nodes.get(i));
            activeController().setNodeSubset(set);
        }
    }//GEN-LAST:event_highlightMIActionPerformed

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
    private javax.swing.JPanel boxP4;
    private javax.swing.JPanel boxPanel;
    private javax.swing.JTabbedPane boxTP1;
    private javax.swing.JMenuItem circleMI;
    private javax.swing.JMenuItem circleMI1;
    private javax.swing.JMenuItem circularMI;
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
    private javax.swing.JMenu exportM;
    private javax.swing.JMenuItem exportQTMI;
    private javax.swing.JMenu fileM;
    private javax.swing.JTabbedPane graphTP;
    private javax.swing.JMenu helpM;
    private javax.swing.JMenuItem highlightMI;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparator4;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JPopupMenu.Separator jSeparator6;
    private javax.swing.JPopupMenu.Separator jSeparator7;
    private javax.swing.JPopupMenu.Separator jSeparator8;
    private javax.swing.JToolBar.Separator jSeparator9;
    private data.propertysheet.PropertySheet labelPS;
    private javax.swing.JButton layoutCircleTBB;
    private javax.swing.JToggleButton layoutEnergyTBB;
    private javax.swing.JMenu layoutM;
    private data.propertysheet.PropertySheet layoutPS;
    private javax.swing.JButton layoutRandomTBB;
    private javax.swing.JMenuItem loadEdgeListMI;
    private javax.swing.JMenu loadM;
    private javax.swing.JMenuItem loadPajekMI;
    private javax.swing.JMenuItem loadPajekMI1;
    private javax.swing.JMenuItem loadPajekMI2;
    private javax.swing.JMenuItem loadPajekXLongMI;
    private javax.swing.JMenuItem loadPajekXMI;
    private javax.swing.JButton loadTBB;
    private javax.swing.JSplitPane mainSP;
    private graphexplorer.GraphTableModel mainTM;
    private javax.swing.JTable mainTable;
    private javax.swing.JPanel mainTableP;
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
    private javax.swing.JMenuItem saveEdgeListMI;
    private javax.swing.JMenuItem savePajekMI;
    private javax.swing.JMenuItem savePajekMI1;
    private javax.swing.JMenuItem savePajekXMI;
    private javax.swing.JMenuItem savePajekXMI1;
    private javax.swing.JMenuItem savePajekXMI2;
    private javax.swing.JButton saveTBB;
    private javax.swing.JMenuItem starMI;
    private javax.swing.JMenuItem starMI1;
    private javax.swing.JToolBar toolbar;
    private javax.swing.JMenuItem uniform1MI;
    private javax.swing.JMenuItem uniform1MI1;
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

    PlaneGraph activePlaneGraph() {
        GraphController gc = master.getActiveController();
        return graphs.get(gc);
    }

    /** Updates the property panel with currently active graph and energy layout */
    private void updatePropertyPanel() {
        propertyRP.removeAll();
        PlaneGraph active = activePlaneGraph();
        if (active != null) {
            PlaneGraphBean bean = new PlaneGraphBean(active);
            propertyRP.add(nodePS = new PropertySheet(bean.nodeBean()), "Node Settings");
            propertyRP.add(edgePS = new PropertySheet(bean.edgeBean()), "Edge Settings");
            propertyRP.add(labelPS = new PropertySheet(bean.labelBean()), "Label Settings");
            IterativeGraphLayout layout = activeController().getIterativeLayout();
            if (layout != null)
                propertyRP.add(layoutPS = new PropertySheet(layout), "Iterative Layout Settings");
            else
                layoutPS = null;
        } else {
            nodePS = null;
            edgePS = null;
            labelPS = null;
            layoutPS = null;
        }
    }

    /** Updates metric combo box */
    private void updateMetricComboBox() {
        GraphController gc = activeController();
        if (gc == null) {
            metricCB.setSelectedItem(StatEnum.NONE);
            metricMenuItems.get(StatEnum.NONE).setSelected(true);
        } else {
            NodeMetric nm = activeController().getMetric();
            StatEnum found = null;
            for (StatEnum item : StatEnum.values())
                if (item.getMetric() == nm) {
                    found = item;
                    break;
                }
            metricCB.setSelectedItem(found);
            metricMenuItems.get(found).setSelected(true);
        }
    }

    /** Updates metric chart and table for current status */
    private void updateChart() {
        GraphController gc = activeController();
        List values = gc == null ? null : gc.getMetricValues();
        if (values == null) {
            distributionCP.setChart(null);
            distributionTable.setModel(new DefaultTableModel());
            boxP3.remove(distributionTableSP);
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
            
            boxP3.add(distributionTableSP, BorderLayout.CENTER);
            boxPanel.validate();
        }
    }

    /** Updates the longitudinal metric chart, provided the graph is longitudinal */
    private void updateLongChart() {
        GraphController gc = activeController();
        if (gc.isLongitudinal()) {
            LongitudinalMetricPlot lmp = new LongitudinalMetricPlot(gc);
            longMetricCP.setChart(lmp.getChart());
            boxTP1.add(longMetricCP, "Longitudinal Metric Chart");
        } else {
            longMetricCP.setChart(null);
            boxTP1.remove(longMetricCP);
        }
    }

    /** Updates the sizes of the active nodes */
    private void updateNodeSizes() {
        GraphController gc = activeController();
        if (gc != null) {
            PlaneGraph pg = activePlaneGraph();
            List values = gc.getMetricValues();
            activePlaneGraph().setNodeValues(values);

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
    private void updateGraphTabs() {
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
        PlaneGraph pg = null;
        for (GraphController gc : set) {
            if (gc.isLongitudinal()) {
                c = new LongitudinalGraphPanel(gc);
                pg = ((LongitudinalGraphPanel)c).getPlaneGraph();
            } else {
                c = new PlanePlotComponent();
                ((PlanePlotComponent)c).add(pg = new PlaneGraph(gc.getActiveGraph()));
                pg.highlightNodes(gc.getNodeSubset());
            }
            gc.setPositions(pg.getPositionMap());
            tabs.put(gc, c);
            graphs.put(gc, pg);
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
        if (gc != null)
            gc.addPropertyChangeListener(this);
        if (gc.isLongitudinal())
            exportQTMI.setAction(new ExplorerIOActions.MovieAction(gc, (LongitudinalGraphPanel) tabs.get(gc)));
        else
            exportQTMI.setEnabled(false);
        actions_stat.controller = gc;
        actions_layout.controller = gc;
        updating = false;
    }

    //
    // PROPERTY CHANGES FED FROM THE CONTROLLERS
    //

    boolean updating = false;

    public void stateChanged(ChangeEvent e) {
        if (e.getSource() == activePlaneGraph()) {
            activeController().setPositions(activePlaneGraph().getPositionMap());
        }
    }

    int pcn = 0;

    public void propertyChange(PropertyChangeEvent evt) {
//        if (!evt.getPropertyName().equals("positions"))
//            output("Property change " + (pcn++) + " src=" + evt.getSource() + " & name=" + evt.getPropertyName());
        if (evt.getSource() == master) {
            if (evt.getPropertyName().equals("active")) {
                updateGraphTabs();
            } else if (evt.getPropertyName().equals("output")) {
                output((String) evt.getNewValue());
            } else {
                output("Unknown property change event, name = " + evt.getPropertyName());
            }
        } else if (evt.getSource() == activeController()) {
            GraphController gc = activeController();
            PlaneGraph pg = activePlaneGraph();
            if (evt.getPropertyName().equals("name")) {
                Component c = tabs.get(gc);
                int index = graphTP.indexOfComponent(c);
                graphTP.setTitleAt(index, (String) evt.getNewValue());
            } else if (evt.getPropertyName().equals("layout")) {
                updatePropertyPanel();
            } else if (evt.getPropertyName().equals("animating")) {
                boolean animating = (Boolean) evt.getNewValue();
                layoutEnergyTBB.setSelected(animating);
                layoutEnergyTBB.setText(animating ? "Stop" : "Start");
            } else if (evt.getPropertyName().equals("primary")) {
                // not supported yet!
            } else if (evt.getPropertyName().equals("subset")) {
                pg.highlightNodes(gc.getNodeSubset());
            } else if (evt.getPropertyName().equals("metric")) {
                updateMetricComboBox();
                updateChart();
                updateNodeSizes();
            } else if (evt.getPropertyName().equals("time")) {
                // longitudinal panel works directly with time & will update the active displayed graph
                updateChart();
                updateNodeSizes();
            } else if (evt.getPropertyName().equals("node value")) {
                pg.updateLabels();
            } else if (evt.getPropertyName().equals("positions")) {
                pg.setPositionMap(gc.getPositions());
            } else if(evt.getPropertyName().equals("output")) {
                output((String) evt.getNewValue());
            } else {
                output("Unknown property change event, name = " + evt.getPropertyName());
            }
        }
    }

}
