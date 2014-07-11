/**
 * styleModifier.java
 * Created Jul 10, 2014
 */

package com.googlecode.blaisemath.style.context;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2009 - 2014 Elisha Peterson
 * --
 * Licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
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


import com.googlecode.blaisemath.style.Style;

/**
 * Provides a method to alter a style of given type, using a {@link StyleHintSet}.
 * @param <S> the style to modify
 * @author Elisha
 */
public interface StyleModifier<S extends Style> {
    
    /**
     * Apply the style modifier for the given hint set.
     * @param style the style to apply to
     * @param hints the modifier to apply
     * @return the modified style
     */
    S apply(S style, StyleHintSet hints);
    
}
