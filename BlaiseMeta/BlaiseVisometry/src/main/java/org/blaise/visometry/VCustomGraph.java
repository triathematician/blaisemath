/*
 * VCustomPointSet.java
 * Created Oct 5, 2011
 */
package org.blaise.visometry;

/*
 * #%L
 * BlaiseVisometry
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


import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.blaise.graphics.DelegatingNodeLinkGraphic;
import org.blaise.graphics.Graphic;
import org.blaise.style.ObjectStyler;
import org.blaise.style.PathStyle;
import org.blaise.util.CoordinateManager;
import org.blaise.util.Edge;

/**
 * A set of draggable points defined in local coordinates. Properties of the objects
 * (including style, tooltips, locations, etc.) are managed by delegates.
 *
 * @param <C> the local coordinate
 * @param <S> the type of object being displayed
 * @param <E> object type of edge
 *
 * @author elisha
 */
public class VCustomGraph<C,S,E extends Edge<S>> extends VCustomPointSet<C, S> {

    /** Maintains collection of edges */
    protected final DelegatingNodeLinkGraphic<S,E> gwindow;

    /**
     * Initialize without any points or edges
     */
    public VCustomGraph() {
        this(Collections.EMPTY_MAP);
    }

    /**
     * Initialize with specified coordinate manager
     * @param mgr coordinate manager
     */
    public VCustomGraph(CoordinateManager<S, C> mgr) {
        super(mgr);
        window = null;
        gwindow = new DelegatingNodeLinkGraphic<S,E>();
        
        // change the window graphic from default specified by parent class
        window.getCoordinateManager().removeCoordinateListener(coordinateListener);
        window = gwindow.getPointGraphic();
        window.getCoordinateManager().addCoordinateListener(coordinateListener);
    }

    /**
     * Construct point set with specified objects.
     * @param loc initial locations of points
     */
    public VCustomGraph(Map<S,? extends C> loc) {
        super(loc);
        window = null;
        gwindow = new DelegatingNodeLinkGraphic<S,E>();
        
        // change the window graphic from default specified by parent class
        window.getCoordinateManager().removeCoordinateListener(coordinateListener);
        window = gwindow.getPointGraphic();
        window.getCoordinateManager().addCoordinateListener(coordinateListener);
    }

    //
    // PROPERTIES
    //

    @Override
    public Graphic getWindowGraphic() {
        return gwindow;
    }

    public Set<? extends E> getEdges() {
        return gwindow.getEdgeSet();
    }

    public void setEdges(Set<? extends E> edges) {
        // make a copy to prevent errors in updating edges
        gwindow.setEdgeSet(new LinkedHashSet<E>(edges));
    }

    public void setEdgeStyler(ObjectStyler<E, PathStyle> styler) {
        gwindow.setEdgeStyler(styler);
    }

    public ObjectStyler<E, PathStyle> getEdgeStyler() {
        return gwindow.getEdgeStyler();
    }

}
