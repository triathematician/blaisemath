/*
 * StyleContext.java
 * Created Jan 22, 2011
 */
package org.blaise.style.context;

import org.blaise.style.PathStyle;
import org.blaise.style.PointStyle;
import org.blaise.style.ShapeStyle;
import org.blaise.style.Style;
import org.blaise.style.TextStyle;

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

/**
 * Delegates all styles to a parent style. This class is designed to be overridden
 * so that subclasses only need to implement overrides for a few styles.
 * 
 * @param <S> type of object to be styled
 * 
 * @author elisha
 */
public abstract class StyleContextDecorated<S> implements StyleContext<S> {
    
    protected final StyleContext<S> parent;

    public StyleContextDecorated() {
        this(StyleContextBasic.getInstance());
    }

    public StyleContextDecorated(StyleContext parent) {
        this.parent = parent;
    }

    /**
     * Return the parent factory used for delegation
     * @return parent
     */
    public StyleContext getParentContext() {
        return parent;
    }

    @Override
    public <T extends Style> T getStyle(Class<T> cls, S src, StyleHintSet hints) {
        return parent == null ? null : parent.getStyle(cls, src, hints);
    }

    @Override
    public ShapeStyle getShapeStyle(S src, StyleHintSet hints) {
        return parent == null ? null : parent.getShapeStyle(src, hints);
    }

    @Override
    public PathStyle getPathStyle(S src, StyleHintSet hints) {
        return parent == null ? null : parent.getPathStyle(src, hints);
    }

    @Override
    public PointStyle getPointStyle(S src, StyleHintSet hints) {
        return parent == null ? null : parent.getPointStyle(src, hints);
    }

    @Override
    public TextStyle getTextStyle(S src, StyleHintSet hints) {
        return parent == null ? null : parent.getTextStyle(src, hints);
    }
    
}
