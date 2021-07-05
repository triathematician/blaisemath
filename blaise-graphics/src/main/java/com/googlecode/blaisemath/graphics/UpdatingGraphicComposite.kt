package com.googlecode.blaisemath.graphics

import com.google.common.annotations.Beta
import com.google.common.collect.*
import com.googlecode.blaisemath.graphics.svg.SvgPathCoderTest
import com.googlecode.blaisemath.graphics.swing.ClippedImageTestFrame
import com.googlecode.blaisemath.graphics.swing.ContextMenuTestFrame
import com.googlecode.blaisemath.graphics.swing.HelloWorldTestFrame
import com.googlecode.blaisemath.graphics.swing.PanAndZoomTestFrame
import com.googlecode.blaisemath.graphics.swing.SelectionTestFrame
import com.googlecode.blaisemath.graphics.swing.TooltipTestFrame
import junit.framework.TestCase
import java.awt.geom.Rectangle2D
import java.util.*
import java.util.function.Function

/*
* #%L
* blaise-graphics
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
 * Encapsulates a set of graphics as a composite, along with elements used
 * to create/update the graphics.
 *
 * @author Elisha Peterson
 * @param <T> type of object represented by the composite
</T> */
@Beta
class UpdatingGraphicComposite<T, G>(updater: GraphicUpdater<T?, G?>?) {
    /** Contains the graphic elements  */
    private val composite: GraphicComposite<G?>? = GraphicComposite()

    /** Cache of source objects and their locations  */
    private val bounds: MutableMap<T?, Rectangle2D?>? = Maps.newLinkedHashMap()

    /** Index for the graphics, based on source object  */
    private val index: BiMap<T?, Graphic<G?>?>? = HashBiMap.create()

    /** Creates/updates the graphics  */
    private var updater: GraphicUpdater<T?, G?>?

    //region PROPERTIES
    fun getGraphic(): GraphicComposite<G?>? {
        return composite
    }

    fun getUpdater(): GraphicUpdater<T?, G?>? {
        return updater
    }

    fun setUpdater(gr: GraphicUpdater<T?, G?>?) {
        updater = gr
    }

    fun setObjects(data: Iterable<T?>?, locMap: Function<T?, Rectangle2D?>?) {
        val cData = if (data is MutableCollection<*>) data as MutableCollection<T?>? else Lists.newArrayList(data)
        bounds.keys.retainAll(cData)
        val toRemove: MutableSet<Graphic<G?>?>? = Sets.newHashSet(composite.getGraphics())
        for (t in data) {
            bounds[t] = locMap.apply(t)
            if (index.containsKey(t)) {
                toRemove.remove(index.get(t))
            }
        }
        composite.removeGraphics(toRemove)
        index.keys.retainAll(cData)
        updateItemGraphics()
    }

    private fun updateItemGraphics() {
        for (obj in bounds.keys) {
            val existing = index.get(obj)
            val loc = bounds.get(obj)
            if (loc == null && existing != null) {
                composite.removeGraphic(existing)
                index.remove(obj)
            } else if (loc != null) {
                val gfc: Graphic<*>? = updater.update(obj, loc, existing)
                if (existing == null) {
                    index[obj] = gfc
                    composite.addGraphic(gfc)
                }
            }
        }
    }

    //endregion
    //region LOOKUPS
    fun objectOf(gfc: Graphic<G?>?): T? {
        return index.inverse()[gfc]
    }

    fun graphicOf(obj: T?): Graphic<G?>? {
        return index.get(obj)
    } //endregion

    companion object {
        fun <T, G> create(updater: GraphicUpdater<T?, G?>?): UpdatingGraphicComposite<T?, G?>? {
            return UpdatingGraphicComposite(updater)
        }
    }

    init {
        this.updater = Objects.requireNonNull(updater)
    }
}