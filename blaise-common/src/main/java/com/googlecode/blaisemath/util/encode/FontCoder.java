package com.googlecode.blaisemath.util.encode;

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

import java.awt.Font;

/**
 * Adapter converting fonts to/from strings. Requires non-null values.
 *
 * @author Elisha Peterson
 */
public final class FontCoder implements StringEncoder<Font>, StringDecoder<Font> {

    @Override
    public String encode(Font c) {
        String styStr = c.isPlain() ? "PLAIN"
                : c.isBold() && c.isItalic() ? "BOLDITALIC"
                : c.isBold() ? "BOLD"
                : "ITALIC";
        return String.format("%s-%s-%s", c.getFamily(), styStr, ""+c.getSize());
    }

    @Override
    public Font decode(String v) {
        return Font.decode(v);
    }
    
}
