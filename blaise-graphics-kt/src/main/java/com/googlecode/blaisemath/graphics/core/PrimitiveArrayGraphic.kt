package com.googlecode.blaisemath.graphics.core

import com.googlecode.blaisemath.style.AttributeSet
import com.googlecode.blaisemath.style.Renderer

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

/**
 * A basic graphic object with a style set.
 * @param <O> type of primitive
 * @param <G> type of graphics canvas to render to
 */
open class PrimitiveArrayGraphic<O : Any, G : Any>() : PrimitiveArrayGraphicSupport<O, G>() {

    /** The style set for this graphic  */
    override var style = AttributeSet()
        set(value) {
            field = value
            fireGraphicChanged()
        }

    constructor(primitives: List<O>, style: AttributeSet, renderer: Renderer<O, G>): this() {
        this.primitives = primitives.toMutableList()
        this.style = style
        this.renderer = renderer
    }

}