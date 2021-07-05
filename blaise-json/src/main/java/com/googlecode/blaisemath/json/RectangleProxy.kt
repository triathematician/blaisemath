package com.googlecode.blaisemath.json

import java.awt.Rectangle

/*-
* #%L
* blaise-json
* --
* Copyright (C) 2019 - 2021 Elisha Peterson
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
 * Serializable proxy for a [Rectangle].
 * @author Elisha Peterson
 */
class RectangleProxy {
    private var x = 0
    private var y = 0
    private var width = 0
    private var height = 0

    constructor() {}
    constructor(rect: Rectangle?) {
        x = rect.x
        y = rect.y
        width = rect.width
        height = rect.height
    }

    fun toRectangle(): Rectangle? {
        return Rectangle(x, y, width, height)
    }

    //<editor-fold defaultstate="collapsed" desc="PROPERTIES">
    fun getX(): Int {
        return x
    }

    fun setX(x: Int) {
        this.x = x
    }

    fun getY(): Int {
        return y
    }

    fun setY(y: Int) {
        this.y = y
    }

    fun getWidth(): Int {
        return width
    }

    fun setWidth(width: Int) {
        this.width = width
    }

    fun getHeight(): Int {
        return height
    }

    fun setHeight(height: Int) {
        this.height = height
    } //</editor-fold>
}