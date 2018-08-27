package com.googlecode.blaisemath.util.xml;

/*
 * #%L
 * BlaiseCommon
 * --
 * Copyright (C) 2014 - 2018 Elisha Peterson
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

import com.googlecode.blaisemath.util.Colors;
import com.googlecode.blaisemath.util.encode.ColorCoder;
import java.awt.Color;

/**
 * Adapter converting colors to/from hex strings. Supports #AARRGGBB, #RRGGBB, and #RGB notations.
 * Uses {@link Colors#encode(java.awt.Color)} and {@link Colors#decode(java.lang.String)} 
 * to perform the conversion, but provides additional flexibility for handling nulls.
 *
 * @see Color#decode(java.lang.String)
 *
 * @author Elisha Peterson
 */
public final class ColorAdapter extends StringXmlAdapter<Color> {

    public ColorAdapter() {
        super(new ColorCoder());
    }
    
}
