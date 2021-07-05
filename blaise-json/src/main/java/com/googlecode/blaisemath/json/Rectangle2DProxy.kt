package com.googlecode.blaisemath.json

import java.awt.geom.Rectangle2D

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
 * Serializable proxy for a [Rectangle2D].
 * @author Elisha Peterson
 */
class Rectangle2DProxy {
    private var x = 0.0
    private var y = 0.0
    private var width = 0.0
    private var height = 0.0

    constructor() {}
    constructor(rect: Rectangle2D?) {
        x = rect.getX()
        y = rect.getY()
        width = rect.getWidth()
        height = rect.getHeight()
    }

    fun toRectangle(): Rectangle2D.Double? {
        return Rectangle2D.Double(x, y, width, height)
    }

    //<editor-fold defaultstate="collapsed" desc="PROPERTIES">
    fun getX(): Double {
        return x
    }

    fun setX(x: Double) {
        this.x = x
    }

    fun getY(): Double {
        return y
    }

    fun setY(y: Double) {
        this.y = y
    }

    fun getWidth(): Double {
        return width
    }

    fun setWidth(width: Double) {
        this.width = width
    }

    fun getHeight(): Double {
        return height
    }

    fun setHeight(height: Double) {
        this.height = height
    } //</editor-fold>
}