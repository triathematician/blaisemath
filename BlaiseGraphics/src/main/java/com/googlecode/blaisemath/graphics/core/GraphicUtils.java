/**
 * GraphicUtils.java
 * Created Jul 11, 2014
 */
package com.googlecode.blaisemath.graphics.core;

/*
 * #%L
 * BlaiseGraphics
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


import com.google.common.base.Objects;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.googlecode.blaisemath.style.StyleHints;
import com.googlecode.blaisemath.style.Styles;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.util.Comparator;
import java.util.List;
import javax.annotation.Nullable;

/**
 * Utility class for working with {@link Graphic}s.
 * @author Elisha
 */
public class GraphicUtils {
    
    //<editor-fold defaultstate="collapsed" desc="SINGLETONS">
    
    /** Filter that can be applied to pass only visible graphics */
    private static final Predicate<Graphic> VISIBLE_FILTER = new Predicate<Graphic>(){
        @Override
        public boolean apply(Graphic input) { 
            return !StyleHints.isInvisible(input.getStyleHints());
        }
    };
    
    /** Filter that can be applied to pass only visible graphics */
    private static final Predicate<Graphic> FUNCTIONAL_FILTER = new Predicate<Graphic>(){
        @Override
        public boolean apply(Graphic input) { 
            return StyleHints.isFunctional(input.getStyleHints());
        }
    };
    
    /** Comparator for z-order of graphics */
    private static final Comparator<Graphic> Z_COMPARATOR = new ZOrderComparator();
    
    //</editor-fold>
    
    // utility class
    private GraphicUtils() {
    }
    
    //<editor-fold defaultstate="collapsed" desc="PREDICATES">
    
    /**
     * Return visibility filter for graphics.
     * @param <G> type of graphic being filtered
     * @return visible filter
     */
    public static <G extends Graphic> Predicate<G> visibleFilter() {
        return (Predicate<G>) VISIBLE_FILTER;
    }
    
    /**
     * Return functional filter for graphics.
     * @param <G> type of graphic being filtered
     * @return functional filter
     */
    public static <G extends Graphic> Predicate<G> functionalFilter() {
        return (Predicate<G>) FUNCTIONAL_FILTER;
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
    
    //<editor-fold defaultstate="collapsed" desc="COMPARATORS">
    
    /**
     * Return z-order comparator for graphics.
     * @param <G> type of graphic being compared
     * @return comparator
     */
    public static <G extends Graphic> Comparator<G> zOrderComparator() {
        return (Comparator<G>) Z_COMPARATOR;
    }
    
    /**
     * Sort graphics by z order.
     * @param <G> type of graphic being compared
     * @param graphics graphics to sort
     * @return ordered graphics
     */
    public static <G extends Graphic> List<G> zOrderSort(Iterable<G> graphics) {
        return Ordering.from(GraphicUtils.zOrderComparator()).sortedCopy(graphics);
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="BOUNDING BOX UTILS">
    
    /**
     * Get the bounding box surrounding the given set of graphics.
     * @param <G> type of graphic canvas
     * @param entries the graphics
     * @return bounding box, or null if the provided iterable is empty
     */
    @Nullable
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
    
    /**
     * Get the bounding box surrounding the given set of rectangles.
     * @param shapes the rectangular shapes
     * @return bounding box, or null if the provided iterable is empty
     */
    @Nullable
    public static Rectangle2D boundingBoxRect(Iterable<? extends RectangularShape> shapes) {
        Rectangle2D res = null;
        for (RectangularShape sh : shapes) {
            if (sh != null) {
                res = res == null ? sh.getBounds2D() : res.createUnion(sh.getBounds2D());
            }
        }
        return res;
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="INNER CLASSES">
    
    /** Comparator for z order of graphics */
    private static class ZOrderComparator implements Comparator<Graphic> {
        @Override
        public int compare(Graphic left, Graphic right) {
            if (left == right) {
                return 0;
            }
            
            // find the common parent of the two graphics, then compare position relative to that
            List<Graphic> parLeft = graphicPath(left);
            List<Graphic> parRight = graphicPath(right);
            int firstDiffer = -1;
            int commonSize = Math.min(parLeft.size(), parRight.size());
            for (int i = 0; i < commonSize; i++) {
                if (parLeft.get(i) != parRight.get(i)) {
                    firstDiffer = i;
                    break;
                }
            }
            
            if (firstDiffer == 0) {
                // different trees, default to basic comparison
                return Ordering.arbitrary().compare(left, right);
            } else if (firstDiffer == -1) {
                // they agree on overlap, so one must be a parent of the other
                Graphic commonParent = parLeft.get(commonSize-1);
                if (left == commonParent) {
                    return -1;
                } else if (right == commonParent) {
                    return 1;
                } else {
                    throw new IllegalStateException("unexpected");
                }
            } else {
                // they disagree at some point past the first index
                GraphicComposite commonParent = (GraphicComposite) parLeft.get(firstDiffer-1);
                List<Graphic> children = Lists.newArrayList(commonParent.getGraphics());
                return children.indexOf(parLeft.get(firstDiffer)) - children.indexOf(parRight.get(firstDiffer));
            }
        }

        private List<Graphic> graphicPath(Graphic gfc) {
            List<Graphic> res = Lists.newArrayList();
            Graphic cur = gfc;
            while (cur != null) {
                res.add(0, cur);
                cur = cur.getParent();
            }
            return res;
        }
    }
    
    //</editor-fold>
    
}
