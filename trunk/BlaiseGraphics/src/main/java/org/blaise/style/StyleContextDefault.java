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
public final class StyleContextDefault implements StyleContext<Object> {
    
    private static final StyleContextDefault INST = new StyleContextDefault();
    
    protected static final ShapeStyle SOLID = new ShapeStyleBasic().fill(Color.white).stroke(Color.black);
    protected static final PathStyle PATH = new PathStyleBasic().stroke(Color.black);
    protected static final PointStyle POINT = new PointStyleBasic();
    protected static final TextStyle STRING = new TextStyleBasic();

    private StyleContextDefault() {
    }
    
    public static StyleContextDefault getInstance() {
        return INST;
    }
        
    public ShapeStyle getShapeStyle(Object o) {
        return SOLID;
    }

    public PathStyle getPathStyle(Object o) {
        return PATH;
    }

    public PointStyle getPointStyle(Object o) {
        return POINT;
    }

    public TextStyle getStringStyle(Object o) {
        return STRING;
    }

}