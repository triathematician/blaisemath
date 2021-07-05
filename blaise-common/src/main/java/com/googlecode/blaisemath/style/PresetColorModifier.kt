package com.googlecode.blaisemath.style

import com.googlecode.blaisemath.encode.ColorCoderTest
import com.googlecode.blaisemath.encode.FontCoderTest
import com.googlecode.blaisemath.encode.PointCoderTest
import com.googlecode.blaisemath.style.AttributeSetCoderTest
import com.googlecode.blaisemath.util.ColorsTest
import junit.framework.TestCase
import org.junit.Before
import java.awt.Color

/*
* #%L
* blaise-graphics
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
 * Modifier that adjusts fill/stroke attributes to preset colors.
 * @author Elisha Peterson
 */
class PresetColorModifier : StyleModifier {
    private var highlightFill: Color? = null
    private var highlightStroke: Color? = null
    private var selectFill: Color? = null
    private var selectStroke: Color? = null
    override fun apply(style: AttributeSet?, hints: MutableSet<String?>?): AttributeSet? {
        var res = style
        if (hints.contains(StyleHints.HIGHLIGHT_HINT)) {
            res = AttributeSet.Companion.withParent(res).and(Styles.FILL, highlightFill).and(Styles.STROKE, highlightStroke)
        }
        if (hints.contains(StyleHints.SELECTED_HINT)) {
            res = AttributeSet.Companion.withParent(res).and(Styles.FILL, selectFill).and(Styles.STROKE, selectStroke)
        }
        return res
    }

    //region PROPERTIES
    fun getHighlightFill(): Color? {
        return highlightFill
    }

    fun setHighlightFill(highlightFill: Color?) {
        this.highlightFill = highlightFill
    }

    fun getHighlightStroke(): Color? {
        return highlightStroke
    }

    fun setHighlightStroke(highlightStroke: Color?) {
        this.highlightStroke = highlightStroke
    }

    fun getSelectFill(): Color? {
        return selectFill
    }

    fun setSelectFill(selectFill: Color?) {
        this.selectFill = selectFill
    }

    fun getSelectStroke(): Color? {
        return selectStroke
    }

    fun setSelectStroke(selectStroke: Color?) {
        this.selectStroke = selectStroke
    } //endregion
}