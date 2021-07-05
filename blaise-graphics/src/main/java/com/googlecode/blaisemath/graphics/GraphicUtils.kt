package com.googlecode.blaisemath.graphics

import com.google.common.base.Objects
import com.google.common.base.Preconditions
import com.google.common.collect.Lists
import com.google.common.collect.Ordering
import com.googlecode.blaisemath.graphics.svg.SvgPathCoderTest
import com.googlecode.blaisemath.graphics.swing.ClippedImageTestFrame
import com.googlecode.blaisemath.graphics.swing.ContextMenuTestFrame
import com.googlecode.blaisemath.graphics.swing.HelloWorldTestFrame
import com.googlecode.blaisemath.graphics.swing.PanAndZoomTestFrame
import com.googlecode.blaisemath.graphics.swing.SelectionTestFrame
import com.googlecode.blaisemath.graphics.swing.TooltipTestFrame
import com.googlecode.blaisemath.style.StyleHints
import com.googlecode.blaisemath.style.Styles
import junit.framework.TestCase
import java.awt.geom.Rectangle2D
import java.util.*
import java.util.function.Function

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
 * Utility class for working with [Graphic]s.
 * @author Elisha Peterson
 */
object GraphicUtils {
    //region PREDICATES
    /**
     * Return true if graphic is currently invisible
     * @param gr the graphic
     * @return true if hidden
     */
    fun isInvisible(gr: Graphic<*>?): Boolean {
        return StyleHints.isInvisible(gr.getStyleHints())
    }

    /**
     * Return true if graphic is currently invisible
     * @param gr the graphic
     * @return true if hidden
     */
    fun isVisible(gr: Graphic<*>?): Boolean {
        return !StyleHints.isInvisible(gr.getStyleHints())
    }

    /**
     * Return true if graphic is currently functional
     * @param gr the graphic
     * @return true if invisible
     */
    fun isFunctional(gr: Graphic<*>?): Boolean {
        return StyleHints.isFunctional(gr.getStyleHints())
    }
    //endregion
    //region SELECTORS
    /**
     * Search for graphic with the given id in the composite, returning it.
     * The id is stored in the [Styles.ID] attribute of the graphic's style.
     * @param <G> graphics canvas type
     * @param gr regular or composite graphic to search in
     * @param id what to search for
     * @return the found graphic, or null if none is found
    </G> */
    fun <G> select(gr: Graphic<G?>?, id: String?): Graphic<G?>? {
        Preconditions.checkNotNull(id)
        if (Objects.equal(id, gr.getStyle().getString(Styles.ID, null))) {
            return gr
        } else if (gr is GraphicComposite<*>) {
            val gc = gr as GraphicComposite<G?>?
            for (g in gc.getGraphics()) {
                val r = select<G?>(g, id)
                if (r != null) {
                    return r
                }
            }
        }
        return null
    }
    //endregion
    //region COMPARATORS
    /**
     * Return z-order comparator for graphics.
     * @param <G> type of graphic being compared
     * @return comparator
    </G> */
    fun <G : Graphic<*>?> zOrderComparator(): Comparator<G?>? {
        return ZOrderComparator<Any?>() as Comparator<G?>
    }

    /**
     * Sort graphics by z order.
     * @param <G> type of graphic being compared
     * @param graphics graphics to sort
     * @return ordered graphics
    </G> */
    fun <G : Graphic<*>?> zOrderSort(graphics: Iterable<G?>?): MutableList<G?>? {
        return Ordering.from(zOrderComparator<Graphic<*>?>()).sortedCopy(graphics)
    }
    //endregion
    //region BOUNDING BOX UTILS
    /**
     * Get the bounding box surrounding the given set of graphics.
     * @param <G> type of graphic canvas
     * @param entries the graphics
     * @param canvas canvas
     * @return bounding box, or null if the provided iterable is empty
    </G> */
    fun <G> boundingBox(entries: Iterable<out Graphic<G?>?>?, canvas: G?): Rectangle2D? {
        return boundingBox(entries, { g: Graphic<G?>? -> g.boundingBox(canvas) }, null)
    }

    /**
     * Get bounding box from iterable.
     * @param <X> item type
     * @param bounds set of items
     * @param mapper gets rectangles
     * @param def to return if result is null
     * @return bounding box, or def if the provided iterable is empty
    </X> */
    fun <X> boundingBox(bounds: Iterable<X?>?, mapper: Function<X?, Rectangle2D?>?, def: Rectangle2D?): Rectangle2D? {
        var res: Rectangle2D? = null
        for (x in bounds) {
            val r = mapper.apply(x)
            if (r != null) {
                res = if (res == null) r else res.createUnion(r)
            }
        }
        return res ?: def
    }
    //endregion
    //region INNER CLASSES
    /** Comparator for z order of graphics  */
    private class ZOrderComparator<G> : Comparator<Graphic<G?>?> {
        override fun compare(left: Graphic<G?>?, right: Graphic<G?>?): Int {
            if (left === right) {
                return 0
            }

            // find the common parent of the two graphics, then compare position relative to that
            val parLeft = graphicPath(left)
            val parRight = graphicPath(right)
            var firstDiffer = -1
            val commonSize = Math.min(parLeft.size, parRight.size)
            for (i in 0 until commonSize) {
                if (parLeft.get(i) !== parRight.get(i)) {
                    firstDiffer = i
                    break
                }
            }
            return if (firstDiffer == 0) {
                // different trees, default to basic comparison
                Ordering.arbitrary().compare(left, right)
            } else if (firstDiffer == -1) {
                // they agree on overlap, so one must be a parent of the other
                val commonParent: Graphic<*>? = parLeft.get(commonSize - 1)
                if (left === commonParent) {
                    -1
                } else if (right === commonParent) {
                    1
                } else {
                    throw IllegalStateException("unexpected")
                }
            } else {
                // they disagree at some point past the first index
                val commonParent = parLeft.get(firstDiffer - 1) as GraphicComposite<*>?
                val children: MutableList<Graphic<*>?>? = Lists.newArrayList<Any?>(commonParent.getGraphics())
                children.indexOf(parLeft.get(firstDiffer)) - children.indexOf(parRight.get(firstDiffer))
            }
        }

        private fun graphicPath(gfc: Graphic<G?>?): MutableList<Graphic<G?>?>? {
            val res: MutableList<Graphic<G?>?> = Lists.newArrayList()
            var cur = gfc
            while (cur != null) {
                res.add(0, cur)
                cur = cur.getParent()
            }
            return res
        }
    } //endregion
}