/*
 * Styles.java
 * Created May 9, 2013
 */
package com.googlecode.blaisemath.style;

/*
 * #%L
 * blaise-graphics
 * --
 * Copyright (C) 2009 - 2017 Elisha Peterson
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
 * Modifier that modifies stroke-width in the supplied style using the supplied hints.
 * @author Elisha
 */
public class StrokeWidthModifier implements StyleModifier {

    @Override
    public AttributeSet apply(AttributeSet style, AttributeSet hints) {
        return AttributeSet.createWithParent(style).and(Styles.STROKE_WIDTH,
                StyleHints.modifyStrokeWidthDefault(style.getFloat(Styles.STROKE_WIDTH), hints));
    }
    
}
