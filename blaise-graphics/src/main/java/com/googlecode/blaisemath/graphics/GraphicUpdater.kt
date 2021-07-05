package com.googlecode.blaisemath.graphics

import com.googlecode.blaisemath.graphics.svg.SvgPathCoderTest
import com.googlecode.blaisemath.graphics.swing.ClippedImageTestFrame
import com.googlecode.blaisemath.graphics.swing.ContextMenuTestFrame
import com.googlecode.blaisemath.graphics.swing.HelloWorldTestFrame
import com.googlecode.blaisemath.graphics.swing.PanAndZoomTestFrame
import com.googlecode.blaisemath.graphics.swing.SelectionTestFrame
import com.googlecode.blaisemath.graphics.swing.TooltipTestFrame
import junit.framework.TestCase
import java.awt.geom.Rectangle2D

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
 * Create [Graphic] objects for rendering items.
 * @param <E> item type
 * @param <G> canvas type
 * @author Elisha Peterson
</G></E> */
interface GraphicUpdater<E, G> {
    /**
     * Create or edit graphic object for a specified entity.
     * @param e the entity
     * @param bounds the position of the entity
     * @param existing graphic already associated with the entity (if it exists)
     * @return graphic, possible null
     */
    open fun update(e: E?, bounds: Rectangle2D?, existing: Graphic<G?>?): Graphic<G?>?
}