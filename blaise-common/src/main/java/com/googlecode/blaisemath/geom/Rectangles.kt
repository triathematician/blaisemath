package com.googlecode.blaisemath.geom

import com.googlecode.blaisemath.encode.ColorCoderTest
import com.googlecode.blaisemath.encode.FontCoderTest
import com.googlecode.blaisemath.encode.PointCoderTest
import com.googlecode.blaisemath.style.AttributeSetCoderTest
import com.googlecode.blaisemath.util.ColorsTest
import junit.framework.TestCase
import org.junit.Before
import java.awt.geom.Rectangle2D

/*
* #%L
* BlaiseCommon
* --
* Copyright (C) 2014 - 2021 Elisha Peterson
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
 * Utility class for working with rectangles.
 * @author Elisha Peterson
 */
object Rectangles {
    /**
     * Return rectangle that is bounding box of all provided.
     * @param rects rectangles
     * @return smallest box enclosing provided rectangles, null if argument is empty
     */
    fun boundingBox(rects: Iterable<out Rectangle2D?>?): Rectangle2D.Double? {
        var res: Rectangle2D? = null
        for (r in rects) {
            res = if (res == null) r else res.createUnion(r)
        }
        return toDouble(res)
    }

    /**
     * Converts a general [Rectangle2D] to a [Rectangle2D.Double], returning the argument if it already is.
     * If the input is null, returns null.
     * @param rect input rectangle
     * @return converted rectangle
     */
    fun toDouble(rect: Rectangle2D?): Rectangle2D.Double? {
        return if (rect == null) {
            null
        } else if (rect is Rectangle2D.Double) {
            rect as Rectangle2D.Double?
        } else {
            val res = Rectangle2D.Double()
            res.frame = rect
            res
        }
    }
}