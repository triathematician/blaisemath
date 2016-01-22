/*
 * VCustomPointSet.java
 * Created Oct 5, 2011
 */
package com.googlecode.blaisemath.visometry;

/*
 * #%L
 * BlaiseVisometry
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


import com.googlecode.blaisemath.graphics.core.DelegatingNodeLinkGraphic;
import com.googlecode.blaisemath.style.ObjectStyler;
import com.googlecode.blaisemath.util.Edge;
import com.googlecode.blaisemath.util.coordinate.CoordinateManager;
import java.util.LinkedHashSet;
import java.util.Set;

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
public class VCustomGraph<C,S,E extends Edge<S>,G> extends VCustomPointSet<C,S,G> {
    
    private static final int NODE_CACHE_SIZE = 20000;

    /** Maintains collection of edges */
    protected final DelegatingNodeLinkGraphic<S,E,G> gwindow;

    /**
     * Initialize without any points or edges
     */
    public VCustomGraph() {
        this(CoordinateManager.<S,C>create(NODE_CACHE_SIZE));
    }

    /**
     * Initialize with specified coordinate manager
     * @param mgr coordinate manager
     */
    public VCustomGraph(CoordinateManager<S, C> mgr) {
        super(mgr);
        
        // change the window graphic from default specified by parent class
        window.getCoordinateManager().removeCoordinateListener(coordinateListener);
        gwindow = new DelegatingNodeLinkGraphic<S,E,G>();
        window = gwindow.getPointGraphic();
        window.getCoordinateManager().addCoordinateListener(coordinateListener);
    }

    //
    // PROPERTIES
    //

    @Override
    public DelegatingNodeLinkGraphic<S,E,G> getWindowGraphic() {
        return gwindow;
    }

    public Set<E> getEdges() {
        return gwindow.getEdgeSet();
    }

    public void setEdges(Set<E> edges) {
        // make a copy to prevent errors in updating edges
        gwindow.setEdgeSet(new LinkedHashSet<E>(edges));
    }

    public void setEdgeStyler(ObjectStyler<E> styler) {
        gwindow.setEdgeStyler(styler);
    }

    public ObjectStyler<E> getEdgeStyler() {
        return gwindow.getEdgeStyler();
    }

}
