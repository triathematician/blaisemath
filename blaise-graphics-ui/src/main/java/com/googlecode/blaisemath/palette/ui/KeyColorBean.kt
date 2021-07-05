package com.googlecode.blaisemath.palette.ui

import com.googlecode.blaisemath.app.ApplicationMenuConfig
import com.googlecode.blaisemath.graphics.testui.AnchorTestFrame
import com.googlecode.blaisemath.palette.ui.PaletteEditorTestUi
import com.googlecode.blaisemath.palette.ui.PaletteIconsTestUi
import com.googlecode.blaisemath.primitive.Marker
import com.googlecode.blaisemath.style.ui.AttributeSetPropertyModelTestFrame
import java.awt.Color

/*
* #%L
* blaise-graphics
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
 * Bean that tracks a key/name associated with a color (and optionally a marker).
 *
 * @author Elisha Peterson
 */
class KeyColorBean {
    private var name: String? = null
    private var color: Color? = null
    private var marker: Marker? = null

    //    /**
    //     * Convert an attribute set to a simple style, using the keys {@link Styles.FILL}
    //     * for color, {@link Styles.MARKER} for marker, and {@link Styles.ID} for name.
    //     * @param as attribute set
    //     * @return simple style
    //     */
    //    public static SimpleStyle create(AttributeSet as) {
    //        Color c = getOrDefault(as, Styles.FILL, Color.class, Colors::decode, null);
    //        Marker m = getOrDefault(as, Styles.MARKER, Marker.class, Markers2::toMarker, null);
    //        String n = getOrDefault(as, Styles.ID, String.class, null, null);
    //        SimpleStyle res = new SimpleStyle();
    //        res.setName(n);
    //        res.setColor(c);
    //        res.setMarker(m);
    //        return res;
    //    }
    //endregion
    //region PROPERTIES
    fun getName(): String? {
        return name
    }

    fun setName(name: String?) {
        this.name = name
    }

    fun getColor(): Color? {
        return color
    }

    fun setColor(color: Color?) {
        this.color = color
    }

    fun getMarker(): Marker? {
        return marker
    }

    fun setMarker(marker: Marker?) {
        this.marker = marker
    } //endregion

    companion object {
        //region FACTORIES
        /**
         * Create bean with name and color
         * @param name style name
         * @param col color
         * @return style
         */
        fun create(name: String?, col: Color?): KeyColorBean? {
            val res = KeyColorBean()
            res.setName(name)
            res.setColor(col)
            return res
        }

        /**
         * Get style for a color.
         * @param col color
         * @return style
         */
        fun create(col: Color?): KeyColorBean? {
            val res = KeyColorBean()
            res.setColor(col)
            return res
        }

        /**
         * Get style for a color.
         * @param mark marker
         * @return style
         */
        fun create(mark: Marker?): KeyColorBean? {
            val res = KeyColorBean()
            res.setMarker(mark)
            return res
        }

        /**
         * Creates copy of another scale.
         * @param style what to copy
         * @return copy
         */
        fun copyOf(style: KeyColorBean?): KeyColorBean? {
            val res = KeyColorBean()
            res.name = style.name
            res.color = style.color
            res.marker = style.marker
            return res
        }
    }
}