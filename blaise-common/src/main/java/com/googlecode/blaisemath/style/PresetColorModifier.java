package com.googlecode.blaisemath.style;

/*
 * #%L
 * blaise-graphics
 * --
 * Copyright (C) 2014 - 2022 Elisha Peterson
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

import java.awt.Color;
import java.util.Set;

/** 
 * Modifier that adjusts fill/stroke attributes to preset colors.
 * @author Elisha Peterson
 */
public class PresetColorModifier implements StyleModifier {
    private Color highlightFill = null;
    private Color highlightStroke = null;
    private Color selectFill = null;
    private Color selectStroke = null;

    @Override
    public AttributeSet apply(AttributeSet style, Set<String> hints) {
        AttributeSet res = style;
        if (hints.contains(StyleHints.HIGHLIGHT_HINT)) {
            res = AttributeSet.withParent(res).and(Styles.FILL, highlightFill).and(Styles.STROKE, highlightStroke);
        }
        if (hints.contains(StyleHints.SELECTED_HINT)) {
            res = AttributeSet.withParent(res).and(Styles.FILL, selectFill).and(Styles.STROKE, selectStroke);
        }
        return res;
    }

    //region PROPERTIES

    public Color getHighlightFill() {
        return highlightFill;
    }

    public void setHighlightFill(Color highlightFill) {
        this.highlightFill = highlightFill;
    }

    public Color getHighlightStroke() {
        return highlightStroke;
    }

    public void setHighlightStroke(Color highlightStroke) {
        this.highlightStroke = highlightStroke;
    }

    public Color getSelectFill() {
        return selectFill;
    }

    public void setSelectFill(Color selectFill) {
        this.selectFill = selectFill;
    }

    public Color getSelectStroke() {
        return selectStroke;
    }

    public void setSelectStroke(Color selectStroke) {
        this.selectStroke = selectStroke;
    }

    //endregion
    
}
