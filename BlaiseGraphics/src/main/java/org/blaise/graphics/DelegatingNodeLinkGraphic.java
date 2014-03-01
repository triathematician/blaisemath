/**
 * DelegatingPointSetGraphic.java
 * Created Jan 22, 2011
 */
package org.blaise.graphics;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2009 - 2014 Elisha Peterson
 * --
 * Licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
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

import java.awt.geom.Point2D;
import java.util.Map;
import java.util.Set;
import org.blaise.style.ObjectStyler;
import org.blaise.style.PathStyle;
import org.blaise.style.PointStyle;
import org.blaise.util.Edge;
import org.blaise.util.CoordinateManager;

/**
 * A graph with fully-customizable points, edges, and tooltips. The styles and
 * point values are computed at runtime. Edges are maintained as a set of {@link Edge}s.
 *
 * @param <S> source object type
 * @param <E> edge type
 * 
 * @author Elisha Peterson
 */
public class DelegatingNodeLinkGraphic<S,E extends Edge<S>> extends GraphicComposite {

    /** Point graphics */
    private final DelegatingPointSetGraphic<S> pointGraphics;
    /** Edge graphics */
    private final DelegatingEdgeSetGraphic<S,E> edgeGraphics;

    /**
     * Construct with no points
     */
    public DelegatingNodeLinkGraphic() {
        pointGraphics = new DelegatingPointSetGraphic<S>();
        edgeGraphics = new DelegatingEdgeSetGraphic<S,E>(pointGraphics.getCoordinateManager());
        addGraphic(edgeGraphics);
        addGraphic(pointGraphics);
    }
    
    /**
     * Construct with specified coordinate manager
     * @param crdManager in charge of node locations
     */
    public DelegatingNodeLinkGraphic(CoordinateManager<S,Point2D> crdManager) {
        pointGraphics = new DelegatingPointSetGraphic<S>(crdManager);
        edgeGraphics = new DelegatingEdgeSetGraphic<S,E>(crdManager);
        addGraphic(edgeGraphics);
        addGraphic(pointGraphics);
    }
    
    //
    // ACCESSORS
    //

    public DelegatingPointSetGraphic<S> getPointGraphic() {
        return pointGraphics;
    }

    public DelegatingEdgeSetGraphic<S, E> getEdgeGraphic() {
        return edgeGraphics;
    }

    public CoordinateManager<S, Point2D> getCoordinateManager() {
        return pointGraphics.getCoordinateManager();
    }

    public void setCoordinateManager(CoordinateManager ptMgr) {
        pointGraphics.setCoordinateManager(ptMgr);
        edgeGraphics.setCoordinateManager(ptMgr);
    }

    //
    // POINTS
    //

    public Set<? extends S> getNodeSet() {
        return pointGraphics.getObjects();
    }
    
    public Map<S, Point2D> getNodeLocations() {
        return pointGraphics.getCoordinateManager().getCoordinates();
    }

    public void setNodeLocations(Map<S, Point2D> pts) {
        pointGraphics.getCoordinateManager().putAll(pts);
    }

    public ObjectStyler<S, PointStyle> getNodeStyler() {
        return pointGraphics.getStyler();
    }

    public void setNodeStyler(ObjectStyler<S, PointStyle> styler) {
        pointGraphics.setStyler(styler);
    }

    //
    // EDGES
    //

    public Set<? extends E> getEdgeSet() {
        return edgeGraphics.getEdges();
    }

    public void setEdgeSet(Set<? extends E> edges) {
        this.edgeGraphics.setEdges(edges);
    }

    public ObjectStyler<E, PathStyle> getEdgeStyler() {
        return edgeGraphics.getEdgeStyler();
    }

    public void setEdgeStyler(ObjectStyler<E, PathStyle> styler) {
        edgeGraphics.setEdgeStyler(styler);
    }

}
