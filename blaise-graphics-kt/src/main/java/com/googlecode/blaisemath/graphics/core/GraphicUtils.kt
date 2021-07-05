package com.googlecode.blaisemath.graphics.core

import com.googlecode.blaisemath.style.StyleHints
import com.googlecode.blaisemath.style.Styles
import com.googlecode.blaisemath.util.geom.createUnion
import java.awt.geom.Rectangle2D
import kotlin.math.min

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
 */

//region EXTENSION FUNCTIONS

val Graphic<*>.isInvisible
    get() = StyleHints.isInvisible(styleHints)
val Graphic<*>.isVisible
    get() = !StyleHints.isInvisible(styleHints)
val Graphic<*>.isFunctional
    get() = StyleHints.isFunctional(styleHints)
val Graphic<*>.id
    get() = style.getString(Styles.ID, null)

//endregion

//region SELECTORS

/** Return graphic with given id, this if the id matches or a child graphic if this is a composite and a child matches. */
fun Graphic<*>.select(lookupId: String): Graphic<*>? = id?.let { this } ?: (this as? GraphicComposite<*>)?.selectIn(lookupId)
/** Return child graphic matching given id. */
fun GraphicComposite<*>.selectIn(lookupId: String): Graphic<*>? {
    graphics.forEach { g -> g.select(lookupId)?.let { return it } }
    return null
}

//endregion

//region COMPARATORS

/** z-order comparator for graphics. */
fun <R : Graphic<*>> zOrderComparator() = ZOrderComparator<Any?>() as Comparator<R>
/** Sort graphics by z order. */
fun <R : Graphic<*>> zOrderSort(graphics: List<R>) = graphics.toSortedSet(zOrderComparator())

//endregion

//region BOUNDING BOX UTILS

/** Get the bounding box surrounding the given set of graphics. */
fun <G> boundingBox(entries: List<Graphic<G>>, canvas: G?) = boundingBox(entries, { it.boundingBox(canvas) }, null)
/** Get bounding box from iterable. */
fun <X> boundingBox(items: List<X>, mapper: (X) -> Rectangle2D?, def: Rectangle2D?) = items.mapNotNull(mapper).createUnion() ?: def

//endregion

//region INNER CLASSES

/** Comparator for z order of graphics  */
private class ZOrderComparator<G> : Comparator<Graphic<G>> {
    override fun compare(left: Graphic<G>, right: Graphic<G>): Int {
        if (left === right) return 0

        // find the common parent of the two graphics, then compare position relative to that
        val parLeft = graphicPath(left)
        val parRight = graphicPath(right)
        val commonSize = min(parLeft.size, parRight.size)
        val firstDiffer = (0 until commonSize).firstOrNull { parLeft[it] !== parRight[it] } ?: -1

        return when (firstDiffer) {
            // different trees, default to basic comparison
            0 -> Ordering.arbitrary().compare(left, right)
            // they agree on overlap, so one must be a parent of the other
            -1 -> {
                val commonParent = parLeft[commonSize - 1]
                when {
                    left === commonParent -> -1
                    right === commonParent -> 1
                    else -> throw IllegalStateException("unexpected")
                }
            }
            // they disagree at some point past the first index
            else -> {
                val commonParent = parLeft[firstDiffer - 1] as GraphicComposite<*>
                val children = commonParent.graphics.toList()
                children.indexOf(parLeft[firstDiffer]) - children.indexOf(parRight[firstDiffer])
            }
        }
    }

    /** This and all of its parents. */
    private fun graphicPath(gfc: Graphic<G>): List<Graphic<G>> = listOf(gfc) + (gfc.parent?.let { graphicPath(it) } ?: listOf())
}

//endregion