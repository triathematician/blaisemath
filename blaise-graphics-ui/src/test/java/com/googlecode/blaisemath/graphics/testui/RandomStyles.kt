package com.googlecode.blaisemath.graphics.testui

import com.googlecode.blaisemath.style.AttributeSet
import com.googlecode.blaisemath.style.Styles
import java.awt.Color

/*
* #%L
* BlaiseGraphics
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
*/internal object RandomStyles {
    fun color(): Color? {
        return Color(Math.random() as Float, Math.random() as Float, Math.random() as Float)
    }

    fun strokeWidth(): Float {
        return (2 * Math.random()) as Float
    }

    fun markerRadius(): Int {
        return (25 * Math.random()) as Int
    }

    fun fontSize(): Int {
        return (5 + 10 * Math.random()) as Int
    }

    fun fontWeight(): String? {
        return if (Math.random() < .25) Styles.FONT_WEIGHT_BOLD else null
    }

    fun fontStyle(): String? {
        return if (Math.random() < .25) Styles.FONT_STYLE_ITALIC else null
    }

    fun point(): AttributeSet? {
        return AttributeSet.of(Styles.FILL, color(), Styles.STROKE, color(), Styles.STROKE_WIDTH, strokeWidth())
                .and(Styles.MARKER_RADIUS, markerRadius())
    }

    fun path(): AttributeSet? {
        val res = AttributeSet.of(Styles.STROKE, color(), Styles.STROKE_WIDTH, strokeWidth())
        if (Math.random() < .95) {
            res.and(Styles.STROKE_DASHES, Math.random() as Float.toString() + "," + Math.random() as Float)
        }
        return res
    }

    fun shape(): AttributeSet? {
        return AttributeSet.of(Styles.FILL, color(), Styles.STROKE, color(), Styles.STROKE_WIDTH, strokeWidth())
    }

    fun string(): AttributeSet? {
        return AttributeSet.of(Styles.FILL, color(), Styles.FONT_SIZE, fontSize())
                .and(Styles.FONT_WEIGHT, fontWeight())
                .and(Styles.FONT_STYLE, fontStyle())
    }
}