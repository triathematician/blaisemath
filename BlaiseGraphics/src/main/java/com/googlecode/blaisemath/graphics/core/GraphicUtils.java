/**
 * GraphicUtils.java
 * Created Jul 11, 2014
 */
package com.googlecode.blaisemath.graphics.core;

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


import com.google.common.base.Objects;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.Predicate;
import com.googlecode.blaisemath.style.StyleHints;
import com.googlecode.blaisemath.style.Styles;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;

/**
 * Utility class for working with {@link Graphic}s.
 * @author Elisha
 */
public class GraphicUtils {
    
    //<editor-fold defaultstate="collapsed" desc="SINGLETONS">
    
    /** Filter that can be applied to pass only visible graphics */
    private static final Predicate<Graphic<?>> VISIBLE_FILTER = new Predicate<Graphic<?>>(){
        @Override
        public boolean apply(Graphic<?> input) { 
            return !StyleHints.isInvisible(input.getStyleHints());
        }
    };
    
    /** Filter that can be applied to pass only visible graphics */
    private static final Predicate<Graphic<?>> FUNCTIONAL_FILTER = new Predicate<Graphic<?>>(){
        @Override
        public boolean apply(Graphic<?> input) { 
            return StyleHints.isFunctional(input.getStyleHints());
        }
    };
    
    //</editor-fold>
    
    // utility class
    private GraphicUtils() {
    }
    
    //<editor-fold defaultstate="collapsed" desc="PREDICATES">
    
    /**
     * Return visibility filter for graphics.
     * @return visible filter
     */
    public static Predicate<Graphic<?>> visibleFilter() {
        return VISIBLE_FILTER;
    }
    
    /**
     * Return functional filter for graphics.
     * @return functional filter
     */
    public static Predicate<Graphic<?>> functionalFilter() {
        return FUNCTIONAL_FILTER;
    }

    /**
     * Return true if graphic is currently invisible
     * @param gr the graphic
     * @return true if hidden
     */
    public static boolean isInvisible(Graphic<?> gr) {
        return !visibleFilter().apply(gr);
    }

    /**
     * Return true if graphic is currently functional
     * @param gr the graphic
     * @return true if invisible
     */
    public static boolean isFunctional(Graphic<?> gr) {
        return functionalFilter().apply(gr);
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="SELECTORS">
    
    /**
     * Search for graphic with the given id in the composite, returning it.
     * The id is stored in the {@link Styles#ID} attribute of the graphic's style.
     * @param <G> graphics canvas type
     * @param gr regular or composite graphic to search in
     * @param id what to search for
     * @return the found graphic, or null if none is found
     */
    public <G> Graphic<G> select(Graphic<G> gr, String id) {
        checkNotNull(id);
        if (Objects.equal(id, gr.getStyle().getString(Styles.ID, null))) {
            return gr;
        } else if (gr instanceof GraphicComposite) {
            GraphicComposite<G> gc = (GraphicComposite<G>) gr;
            for (Graphic<G> g : gc.getGraphics()) {
                Graphic<G> r = select(g, id);
                if (r != null) {
                    return r;
                }
            }
        }
        return null;
    }
    
    //</editor-fold>
    
    public static <G> Rectangle2D boundingBox(Iterable<? extends Graphic<G>> entries) {
        Rectangle2D res = null;
        for (Graphic<G> en : entries) {
            Rectangle2D enBox = en.boundingBox();
            if (enBox != null) {
                res = res == null ? enBox : res.createUnion(enBox);
            }
        }
        return res;
    }
    
    public static Rectangle2D boundingBoxRect(Iterable<? extends RectangularShape> shapes) {
        Rectangle2D res = null;
        for (RectangularShape sh : shapes) {
            if (sh != null) {
                res = res == null ? sh.getBounds2D() : res.createUnion(sh.getBounds2D());
            }
        }
        return res;
    }
    
    
}
