package com.googlecode.blaisemath.graph.app

import com.google.common.collect.Multisets
import com.google.common.graph.Graph
import com.googlecode.blaisemath.app.ApplicationMenuConfig
import com.googlecode.blaisemath.editor.EditorRegistration
import com.googlecode.blaisemath.editor.EnumEditor
import com.googlecode.blaisemath.graph.*
import com.googlecode.blaisemath.graph.layout.SpringLayoutParameters
import com.googlecode.blaisemath.graph.view.GraphComponent
import com.googlecode.blaisemath.graphics.editor.MarkerEditor
import com.googlecode.blaisemath.graphics.svg.SvgElementGraphicConverter
import com.googlecode.blaisemath.graphics.svg.SvgGraphic
import com.googlecode.blaisemath.graphics.svg.SvgGraphicComponent
import com.googlecode.blaisemath.graphics.svg.SvgUtils
import com.googlecode.blaisemath.style.Anchor
import com.googlecode.blaisemath.style.Marker
import com.googlecode.blaisemath.style.xml.AttributeSetAdapter
import com.googlecode.blaisemath.svg.HelloWorldSvg
import com.googlecode.blaisemath.svg.SvgCircle
import com.googlecode.blaisemath.svg.SvgCircle.CircleConverter
import com.googlecode.blaisemath.svg.SvgElement
import com.googlecode.blaisemath.svg.SvgElements
import com.googlecode.blaisemath.svg.SvgEllipse
import com.googlecode.blaisemath.svg.SvgEllipse.EllipseConverter
import com.googlecode.blaisemath.svg.SvgGroup
import com.googlecode.blaisemath.svg.SvgImage
import com.googlecode.blaisemath.svg.SvgImage.ImageConverter
import com.googlecode.blaisemath.svg.SvgIo
import com.googlecode.blaisemath.svg.SvgLine
import com.googlecode.blaisemath.svg.SvgLine.LineConverter
import com.googlecode.blaisemath.svg.SvgNamespaceFilter
import com.googlecode.blaisemath.svg.SvgPath
import com.googlecode.blaisemath.svg.SvgPath.SvgPathOperator
import com.googlecode.blaisemath.svg.SvgPathTest
import com.googlecode.blaisemath.svg.SvgPolygon
import com.googlecode.blaisemath.svg.SvgPolygon.PolygonConverter
import com.googlecode.blaisemath.svg.SvgPolyline
import com.googlecode.blaisemath.svg.SvgPolyline.PolylineConverter
import com.googlecode.blaisemath.svg.SvgRectangle
import com.googlecode.blaisemath.svg.SvgRectangle.RectangleConverter
import com.googlecode.blaisemath.svg.SvgRoot
import com.googlecode.blaisemath.svg.SvgRootTest
import com.googlecode.blaisemath.svg.SvgText
import com.googlecode.blaisemath.svg.SvgText.TextConverter
import com.googlecode.blaisemath.svg.SvgTool
import com.googlecode.blaisemath.ui.PropertyActionPanel
import com.googlecode.blaisemath.util.Images
import com.googlecode.blaisemath.util.MPanel
import com.googlecode.blaisemath.util.RollupPanel
import com.googlecode.blaisemath.util.SetSelectionModel
import org.jdesktop.application.Action
import org.jdesktop.application.Application
import org.jdesktop.application.FrameView
import java.awt.BorderLayout
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.beans.PropertyChangeEvent
import java.beans.PropertyEditorManager
import java.io.IOException
import java.util.logging.Level
import java.util.logging.Logger
import javax.swing.*

/*
* #%L
* BlaiseGraphTheory
* --
* Copyright (C) 2009 - 2021 Elisha Peterson
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
*/ /**
 * Main view for [GraphApp].
 * @author Elisha Peterson
 */
class GraphAppFrameView(app: Application?) : FrameView(app) {
    private val graphCanvas: GraphAppCanvas?
    private var selectedGenerator: GraphGenerator<*, *>? = null
    private var selectedLayout: StaticGraphLayout<*>? = null
    private var selectedIterativeLayout: IterativeGraphLayout<*, *>? = null
    private var pinSelected = false
    private val generatorPanel: PropertyActionPanel?
    private val generatorBox: JComboBox<*>?
    private val staticLayoutPanel: PropertyActionPanel?
    private val staticLayoutBox: JComboBox<*>?
    private val iterativeLayoutPanel: PropertyActionPanel?
    private val iterativeLayoutBox: JComboBox<*>?
    private val statusLabel: JLabel?
    private fun selectionChanged(nodes: MutableSet<String?>?) {
        if (!pinSelected) {
            return
        }
        val parameters = graphCanvas.getLayoutManager().layoutParameters
        if (parameters is SpringLayoutParameters) {
            (parameters as SpringLayoutParameters).constraints.setPinnedNodes(nodes)
        }
    }

    //region PROPERTIES
    fun getSelectedGenerator(): GraphGenerator<*, *>? {
        return selectedGenerator
    }

    fun setSelectedGenerator(selectedGenerator: GraphGenerator<*, *>?) {
        this.selectedGenerator = selectedGenerator
        generatorBox.setSelectedItem(selectedGenerator)
        generatorPanel.setBean(selectedGenerator.createParameters())
        (generatorPanel.getParent() as MPanel).primaryComponent = generatorPanel
    }

    fun getGeneratorParameters(): Any? {
        return generatorPanel.getBean()
    }

    fun getSelectedLayout(): StaticGraphLayout<*>? {
        return selectedLayout
    }

    fun setSelectedLayout(selectedLayout: StaticGraphLayout<*>?) {
        this.selectedLayout = selectedLayout
        staticLayoutBox.setSelectedItem(selectedLayout)
        staticLayoutPanel.setBean(selectedLayout.createParameters())
        (staticLayoutPanel.getParent() as MPanel).primaryComponent = staticLayoutPanel
    }

    fun getLayoutParameters(): Any? {
        return staticLayoutPanel.getBean()
    }

    fun getSelectedIterativeLayout(): IterativeGraphLayout<*, *>? {
        return selectedIterativeLayout
    }

    fun setSelectedIterativeLayout(selectedIterativeLayout: IterativeGraphLayout<*, *>?) {
        this.selectedIterativeLayout = selectedIterativeLayout
        iterativeLayoutBox.setSelectedItem(selectedIterativeLayout)
        val parameters = selectedIterativeLayout.createParameters()
        iterativeLayoutPanel.setBean(parameters)
        (iterativeLayoutPanel.getParent() as MPanel).primaryComponent = iterativeLayoutPanel
        graphCanvas.getLayoutManager().layoutAlgorithm = selectedIterativeLayout
        graphCanvas.getLayoutManager().layoutParameters = parameters
    }

    fun getIterativeLayoutParameters(): Any? {
        return iterativeLayoutPanel.getBean()
    }

    //endregion
    // GRAPH ACTIONS
    @Action
    fun generateGraph(event: ActionEvent?) {
        setSelectedGenerator(event.getSource() as GraphGenerator<*, *>)
    }

    @Action
    fun applyGenerator(event: ActionEvent?) {
        val parameters = event.getSource()
        graphCanvas.setGraph(selectedGenerator.apply(parameters) as Graph)
        applyStaticLayout(ActionEvent(staticLayoutPanel.getBean(), 0, null))
    }

    // LAYOUT ACTIONS
    @Action
    fun pinSelected() {
        pinSelected = !pinSelected
        selectionChanged(graphCanvas.getSelectedNodes())
    }

    @Action
    fun staticLayout(event: ActionEvent?) {
        setSelectedLayout(event.getSource() as StaticGraphLayout<*>)
    }

    @Action
    fun applyStaticLayout(event: ActionEvent?) {
        val parameters = event.getSource()
        AnimationUtils.animateCoordinateChange<Any?, Any?>(graphCanvas.getLayoutManager(), selectedLayout, parameters, graphCanvas, 10.0)
    }

    @Action
    fun iterativeLayout(event: ActionEvent?) {
        setSelectedIterativeLayout(event.getSource() as IterativeGraphLayout<*, *>)
    }

    // METRIC ACTIONS
    @Action
    fun globalMetric(event: ActionEvent?) {
        val gs = event.getSource() as GraphMetric<*>
        val res = gs.apply(graphCanvas.getGraph())
        statusLabel.setText("$gs = $res")
    }

    @Action
    fun nodeMetric(event: ActionEvent?) {
        val gs = event.getSource() as GraphNodeMetric<*>
        val g: Graph? = graphCanvas.getGraph()
        graphCanvas.setMetric(gs)
        statusLabel.setText(gs.toString() + " = " + Multisets.copyHighestCountFirst<Any?>(GraphMetrics.distribution(g, gs)))
    }

    @Action
    fun subsetMetric(event: ActionEvent?) {
        val gs = event.getSource() as GraphSubsetMetric<*>
        statusLabel.setText("$gs = ...")
    }

    companion object {
        private val LOG = Logger.getLogger(GraphAppFrameView::class.java.name)
        private val CANVAS_CM_KEY: String? = "Canvas"
    }

    init {
        graphCanvas = GraphAppCanvas()

        // set up menus
        val am: ActionMap? = app.getContext().getActionMap(this)
        try {
            menuBar = ApplicationMenuConfig.readMenuBar(GraphApp::class.java, this, graphCanvas)
            toolBar = ApplicationMenuConfig.readToolBar(GraphApp::class.java, this, graphCanvas)
            graphCanvas.addContextMenuInitializer(GraphComponent.Companion.MENU_KEY_GRAPH,
                    ApplicationMenuConfig.readMenuInitializer(CANVAS_CM_KEY, GraphApp::class.java, this, graphCanvas))
        } catch (ex: IOException) {
            LOG.log(Level.SEVERE, "Menu config failure.", ex)
        }
        statusLabel = JLabel("no status to report")
        val statusBar = JPanel()
        statusBar.border = BorderFactory.createEmptyBorder(2, 2, 2, 2)
        statusBar.layout = BoxLayout(statusBar, BoxLayout.LINE_AXIS)
        statusBar.add(JLabel("STATUS"))
        statusBar.add(Box.createHorizontalStrut(5))
        statusBar.add(JSeparator(SwingConstants.VERTICAL))
        statusBar.add(Box.createHorizontalStrut(5))
        statusBar.add(statusLabel)
        setStatusBar(statusBar)
        EditorRegistration.registerEditors()
        PropertyEditorManager.registerEditor(Marker::class.java, MarkerEditor::class.java)
        PropertyEditorManager.registerEditor(Anchor::class.java, EnumEditor::class.java)
        generatorPanel = PropertyActionPanel()
        generatorPanel.userOkAction = am.get("applyGenerator")
        generatorBox = JComboBox<Any?>(GraphServices.generators().toTypedArray())
        generatorBox.addActionListener(ActionListener { e: ActionEvent? ->
            e.setSource((e.getSource() as JComboBox<*>).selectedItem)
            generateGraph(e)
        })
        generatorPanel.toolBar.add(generatorBox)
        staticLayoutPanel = PropertyActionPanel()
        staticLayoutPanel.userOkAction = am.get("applyStaticLayout")
        staticLayoutBox = JComboBox<Any?>(GraphServices.staticLayouts().toTypedArray())
        staticLayoutBox.addActionListener(ActionListener { e: ActionEvent? ->
            e.setSource((e.getSource() as JComboBox<*>).selectedItem)
            staticLayout(e)
        })
        staticLayoutPanel.toolBar.add(staticLayoutBox)
        iterativeLayoutPanel = PropertyActionPanel()
        //        iterativeLayoutPanel.setUserOkAction(am.get("applyIterativeLayout"));
        iterativeLayoutBox = JComboBox<Any?>(GraphServices.iterativeLayouts().toTypedArray())
        iterativeLayoutBox.addActionListener(ActionListener { e: ActionEvent? ->
            e.setSource((e.getSource() as JComboBox<*>).selectedItem)
            iterativeLayout(e)
        })
        iterativeLayoutPanel.toolBar.add(iterativeLayoutBox)
        iterativeLayoutPanel.toolBar.add(am.get("startLayout"))
        iterativeLayoutPanel.toolBar.add(am.get("stopLayout"))
        val controlPanel = RollupPanel()
        controlPanel.add(MPanel("Graph Generator", generatorPanel))
        controlPanel.add(MPanel("Static Layout", staticLayoutPanel))
        controlPanel.add(MPanel("Iterative Layout", iterativeLayoutPanel))
        val panel = JPanel(BorderLayout())
        panel.add(graphCanvas, BorderLayout.CENTER)
        panel.add(JScrollPane(controlPanel), BorderLayout.WEST)
        component = panel
        setSelectedGenerator(generatorBox.selectedItem as GraphGenerator<*, *>)
        setSelectedLayout(staticLayoutBox.selectedItem as StaticGraphLayout<*>)
        setSelectedIterativeLayout(iterativeLayoutBox.selectedItem as IterativeGraphLayout<*, *>)
        graphCanvas.selectionModel.addPropertyChangeListener(SetSelectionModel.SELECTION_PROPERTY
        ) { evt: PropertyChangeEvent? -> selectionChanged(graphCanvas.selectedNodes) }
    }
}