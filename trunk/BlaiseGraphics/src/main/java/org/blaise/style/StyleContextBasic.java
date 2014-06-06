/**
 * StyleContextDefault.java
 * Created Dec 8, 2012
 */

package org.blaise.style;

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

import java.awt.Color;

/** 
 * Default instance of the style provider. This is an immutable class that
 * provides non-null, default styles.
 */
public final class StyleContextBasic implements StyleContext<Object> {
    
    private static final StyleContextBasic INST = new StyleContextBasic();
    
    protected static final ShapeStyle DEFAULT_SHAPE_STYLE = new ShapeStyleBasic().fill(Color.white).stroke(Color.black);
    protected static final PathStyle DEFAULT_PATH_STYLE = new PathStyleBasic().stroke(Color.black);
    protected static final PointStyle DEFAULT_POINT_STYLE = new PointStyleBasic();
    protected static final TextStyle DEFAULT_STRING_STYLE = new TextStyleBasic();

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
    public ShapeStyle getShapeStyle(Object o, StyleHintSet hints) {
        return StyleModifiers.apply(DEFAULT_SHAPE_STYLE, hints);
    }

    @Override
    public PathStyle getPathStyle(Object o, StyleHintSet hints) {
        return DEFAULT_PATH_STYLE;
    }

    @Override
    public PointStyle getPointStyle(Object o, StyleHintSet hints) {
        return DEFAULT_POINT_STYLE;
    }

    @Override
    public TextStyle getTextStyle(Object o, StyleHintSet hints) {
        return DEFAULT_STRING_STYLE;
    }

}