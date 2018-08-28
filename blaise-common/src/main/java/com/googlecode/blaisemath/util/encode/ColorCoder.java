package com.googlecode.blaisemath.util.encode;

/*-
 * #%L
 * blaise-common
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
import java.awt.Color;

/**
 * Converts colors to/from strings. Requires non-null colors and strings.
 * 
 * @author Elisha Peterson
 */
public class ColorCoder implements StringEncoder<Color>, StringDecoder<Color> {

    @Override
    public String encode(Color obj) {
        return Colors.encode(obj);
    }

    @Override
    public Color decode(String str) {
        return Colors.decode(str);
    }
    
}