package com.googlecode.blaisemath.graphics.impl

import com.google.common.graph.EndpointPair
import com.googlecode.blaisemath.coordinate.CoordinateManager
import com.googlecode.blaisemath.graphics.GraphicComposite
import com.googlecode.blaisemath.graphics.Renderer
import com.googlecode.blaisemath.graphics.svg.SvgPathCoderTest
import com.googlecode.blaisemath.graphics.swing.ClippedImageTestFrame
import com.googlecode.blaisemath.graphics.swing.ContextMenuTestFrame
import com.googlecode.blaisemath.graphics.swing.HelloWorldTestFrame
import com.googlecode.blaisemath.graphics.swing.PanAndZoomTestFrame
import com.googlecode.blaisemath.graphics.swing.SelectionTestFrame
import com.googlecode.blaisemath.graphics.swing.TooltipTestFrame
import com.googlecode.blaisemath.primitive.AnchoredText
import com.googlecode.blaisemath.style.ObjectStyler
import junit.framework.TestCase
import java.awt.Shape
import java.awt.geom.Point2D

/*
* #%L
* BlaiseGraphics
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
 * A graph with fully-customizable nodes, edges, and tooltips. The styles and
 * point values are computed at runtime. Edges are maintained as a set of [EndpointPair]s.
 *
 * @param <S> source object type
 * @param <E> edge type
 * @param <G> graphics canvas type
 *
 * @author Elisha Peterson
</G></E></S> */
class DelegatingNodeLinkGraphic<S, E : EndpointPair<S?>?, G>(
        crdManager: CoordinateManager<S?, Point2D.Double?>?,
        nodeRenderer: Renderer<Point2D?, G?>?,
        labelRenderer: Renderer<AnchoredText?, G?>?,
        edgeRenderer: Renderer<Shape?, G?>?
) : GraphicComposite<G?>() {
    /** Point graphics  */
    private val pointGraphics: DelegatingPointSetGraphic<S?, G?>?

    /** Edge graphics  */
    private val edgeGraphics: DelegatingEdgeSetGraphic<S?, E?, G?>?
    /**
     * Construct with no points.
     * @param nodeRenderer how nodes will be rendered
     * @param labelRenderer how node labels will be rendered
     * @param edgeRenderer how edges will be rendered
     */
    //region CONSTRUCTORS
    /**
     * Construct with no points and default renderers.
     */
    @JvmOverloads
    constructor(
            nodeRenderer: Renderer<Point2D?, G?>? = null,
            labelRenderer: Renderer<AnchoredText?, G?>? = null,
            edgeRenderer: Renderer<Shape?, G?>? = null
    ) : this(CoordinateManager.create(DEFAULT_NODE_CACHE_SIZE), nodeRenderer, labelRenderer, edgeRenderer) {
    }

    //endregion
    //region DELEGATES - POINTS
    fun getPointGraphic(): DelegatingPointSetGraphic<S?, G?>? {
        return pointGraphics
    }

    fun getCoordinateManager(): CoordinateManager<S?, Point2D.Double?>? {
        return pointGraphics.getCoordinateManager()
    }

    fun setCoordinateManager(ptMgr: CoordinateManager<S?, Point2D.Double?>?) {
        pointGraphics.setCoordinateManager(ptMgr)
        edgeGraphics.setCoordinateManager(ptMgr)
    }

    fun getNodeSet(): MutableSet<S?>? {
        return pointGraphics.getObjects()
    }

    fun getNodeLocations(): MutableMap<S?, Point2D.Double?>? {
        return pointGraphics.getCoordinateManager().activeLocationCopy
    }

    fun setNodeLocations(pts: MutableMap<S?, Point2D.Double?>?) {
        pointGraphics.getCoordinateManager().putAll(pts)
    }

    fun getNodeStyler(): ObjectStyler<S?>? {
        return pointGraphics.getStyler()
    }

    fun setNodeStyler(styler: ObjectStyler<S?>?) {
        pointGraphics.setStyler(styler)
    }

    fun getNodeRenderer(): Renderer<Point2D?, G?>? {
        return pointGraphics.getRenderer()
    }

    fun setNodeRenderer(renderer: Renderer<Point2D?, G?>?) {
        pointGraphics.setRenderer(renderer)
    }

    fun getLabelRenderer(): Renderer<AnchoredText?, G?>? {
        return pointGraphics.getLabelRenderer()
    }

    fun setLabelRenderer(renderer: Renderer<AnchoredText?, G?>?) {
        pointGraphics.setLabelRenderer(renderer)
    }

    fun isDragEnabled(): Boolean {
        return pointGraphics.isDragEnabled()
    }

    fun setDragEnabled(`val`: Boolean) {
        pointGraphics.setDragEnabled(`val`)
    }

    fun isPointSelectionEnabled(): Boolean {
        return pointGraphics.isPointSelectionEnabled()
    }

    fun setPointSelectionEnabled(`val`: Boolean) {
        pointGraphics.setPointSelectionEnabled(`val`)
    }

    //endregion
    //region DELEGATES - EDGES
    fun getEdgeGraphic(): DelegatingEdgeSetGraphic<S?, E?, G?>? {
        return edgeGraphics
    }

    fun getEdgeSet(): MutableSet<E?>? {
        return edgeGraphics.getEdges()
    }

    fun setEdgeSet(edges: MutableSet<out E?>?) {
        edgeGraphics.setEdges(edges)
    }

    fun getEdgeStyler(): ObjectStyler<E?>? {
        return edgeGraphics.getEdgeStyler()
    }

    fun setEdgeStyler(styler: ObjectStyler<E?>?) {
        edgeGraphics.setEdgeStyler(styler)
    }

    fun getEdgeRenderer(): Renderer<Shape?, G?>? {
        return edgeGraphics.getEdgeRenderer()
    }

    fun setEdgeRenderer(renderer: Renderer<Shape?, G?>?) {
        edgeGraphics.setEdgeRenderer(renderer)
    } //endregion

    companion object {
        private const val DEFAULT_NODE_CACHE_SIZE = 20000
    }

    /**
     * Construct with specified coordinate manager.
     * @param crdManager in charge of node locations
     * @param nodeRenderer draws the nodes
     * @param labelRenderer draws labels
     * @param edgeRenderer draws edges
     */
    init {
        pointGraphics = DelegatingPointSetGraphic(crdManager, nodeRenderer, labelRenderer)
        edgeGraphics = DelegatingEdgeSetGraphic(crdManager, edgeRenderer)
        addGraphic(edgeGraphics)
        addGraphic(pointGraphics)
    }
}