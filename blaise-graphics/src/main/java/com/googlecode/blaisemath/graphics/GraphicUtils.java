package com.googlecode.blaisemath.graphics;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2009 - 2025 Elisha Peterson
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

import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.googlecode.blaisemath.style.StyleHints;
import com.googlecode.blaisemath.style.Styles;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.awt.geom.Rectangle2D;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

/**
 * Utility class for working with {@link Graphic}s.
 * @author Elisha Peterson
 */
public class GraphicUtils {
    
    // utility class
    private GraphicUtils() {
    }
    
    //region PREDICATES

    /**
     * Return true if graphic is currently invisible
     * @param gr the graphic
     * @return true if hidden
     */
    public static boolean isInvisible(Graphic<?> gr) {
        return StyleHints.isInvisible(gr.getStyleHints());
    }

    /**
     * Return true if graphic is currently invisible
     * @param gr the graphic
     * @return true if hidden
     */
    public static boolean isVisible(Graphic<?> gr) {
        return !StyleHints.isInvisible(gr.getStyleHints());
    }

    /**
     * Return true if graphic is currently functional
     * @param gr the graphic
     * @return true if invisible
     */
    public static boolean isFunctional(Graphic<?> gr) {
        return StyleHints.isFunctional(gr.getStyleHints());
    }
    
    //endregion
    
    //region SELECTORS
    
    /**
     * Search for graphic with the given id in the composite, returning it.
     * The id is stored in the {@link Styles#ID} attribute of the graphic's style.
     * @param <G> graphics canvas type
     * @param gr regular or composite graphic to search in
     * @param id what to search for
     * @return the found graphic, or null if none is found
     */
    public static <G> Graphic<G> select(Graphic<G> gr, String id) {
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
    
    //endregion

    //region COMPARATORS

    /**
     * Return z-order comparator for graphics.
     * @param <G> type of graphic being compared
     * @return comparator
     */
    public static <G extends Graphic> Comparator<G> zOrderComparator() {
        return (Comparator<G>) new ZOrderComparator();
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
    
    //endregion

    //region BOUNDING BOX UTILS
    
    /**
     * Get the bounding box surrounding the given set of graphics.
     * @param <G> type of graphic canvas
     * @param entries the graphics
     * @param canvas canvas
     * @return bounding box, or null if the provided iterable is empty
     */
    public static <G> @Nullable Rectangle2D boundingBox(Iterable<? extends Graphic<G>> entries, G canvas) {
        return boundingBox(entries, g -> g.boundingBox(canvas), null);
    }

    /**
     * Get bounding box from iterable.
     * @param <X> item type
     * @param bounds set of items
     * @param mapper gets rectangles
     * @param def to return if result is null
     * @return bounding box, or def if the provided iterable is empty
     */
    public static @Nullable <X> Rectangle2D boundingBox(Iterable<X> bounds, Function<X, Rectangle2D> mapper, @Nullable Rectangle2D def) {
        Rectangle2D res = null;
        for (X x : bounds) {
            Rectangle2D r = mapper.apply(x);
            if (r != null) {
                res = res == null ? r : res.createUnion(r);
            }
        }
        return res == null ? def : res;
    }
    
    //endregion

    //region INNER CLASSES
    
    /** Comparator for z order of graphics */
    private static class ZOrderComparator<G> implements Comparator<Graphic<G>> {
        @Override
        public int compare(Graphic<G> left, Graphic<G> right) {
            if (left == right) {
                return 0;
            }
            
            // find the common parent of the two graphics, then compare position relative to that
            List<Graphic<G>> parLeft = graphicPath(left);
            List<Graphic<G>> parRight = graphicPath(right);
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

        private List<Graphic<G>> graphicPath(Graphic<G> gfc) {
            List<Graphic<G>> res = Lists.newArrayList();
            Graphic<G> cur = gfc;
            while (cur != null) {
                res.add(0, cur);
                cur = cur.getParent();
            }
            return res;
        }
    }
    
    //endregion
    
}
