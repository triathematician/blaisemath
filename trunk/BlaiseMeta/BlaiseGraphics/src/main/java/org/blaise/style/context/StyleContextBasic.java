/**
 * StyleContextDefault.java
 * Created Dec 8, 2012
 */

package org.blaise.style.context;

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

import org.blaise.style.PathStyle;
import org.blaise.style.PointStyle;
import org.blaise.style.ShapeStyle;
import org.blaise.style.Style;
import org.blaise.style.Styles;
import org.blaise.style.TextStyle;

/** 
 * Default instance of the style provider. This is an immutable class that
 * provides non-null, default styles.
 */
public final class StyleContextBasic implements StyleContext<Object> {
    
    private static final StyleContextBasic INST = new StyleContextBasic();

    // this class is only intended for use as a static singleton
    private StyleContextBasic() {
    }
    
    /**
     * Return static singleton instance of this class.
     * @return instance
     */
    public static StyleContextBasic getInstance() {
        return INST;
    }

    @Override
    public <T extends Style> T getStyle(Class<T> cls, Object src, StyleHintSet hints) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
        
    @Override
    public ShapeStyle getShapeStyle(Object o, StyleHintSet hints) {
        return StyleModifiers.apply(Styles.DEFAULT_SHAPE_STYLE, hints);
    }

    @Override
    public PathStyle getPathStyle(Object o, StyleHintSet hints) {
        return StyleModifiers.apply(Styles.DEFAULT_PATH_STYLE, hints);
    }

    @Override
    public PointStyle getPointStyle(Object o, StyleHintSet hints) {
        return StyleModifiers.apply(Styles.DEFAULT_POINT_STYLE, hints);
    }

    @Override
    public TextStyle getTextStyle(Object o, StyleHintSet hints) {
        return StyleModifiers.apply(Styles.DEFAULT_TEXT_STYLE, hints);
    }

}