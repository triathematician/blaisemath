package com.googlecode.blaisemath.primitive;

/*-
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

import com.googlecode.blaisemath.style.AttributeSet;

/**
 * Augments {@link AnchoredText} with style information.
 * @author Elisha Peterson
 */
public class StyledText {

    private final AnchoredText text;
    private final AttributeSet style;

    public StyledText(AnchoredText text, AttributeSet style) {
        this.text = text;
        this.style = style;
    }

    public AnchoredText getText() {
        return text;
    }

    public AttributeSet getStyle() {
        return style;
    }

}
