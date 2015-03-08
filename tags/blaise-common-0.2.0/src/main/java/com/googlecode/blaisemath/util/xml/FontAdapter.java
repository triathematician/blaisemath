/*
 * FontAdapter.java
 * Created on May 7, 2013
 */

package com.googlecode.blaisemath.util.xml;

/*
 * #%L
 * BlaiseCommon
 * --
 * Copyright (C) 2014 - 2015 Elisha Peterson
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

import java.awt.Color;
import java.awt.Font;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Adapter converting fonts to/from strings.
 *
 * @see Color#decode(java.lang.String)
 *
 * @author Elisha Peterson
 */
public final class FontAdapter extends XmlAdapter<String, Font> {

    @Override
    public Font unmarshal(String v) {
        Font res = Font.decode(v);
        return res;
    }

    @Override
    public String marshal(Font c) {
        String styStr = c.isPlain() ? "PLAIN"
                : c.isBold() && c.isItalic() ? "BOLDITALIC"
                : c.isBold() ? "BOLD"
                : "ITALIC";
        String res = String.format("%s-%s-%s", c.getFamily(), styStr, ""+c.getSize());
        return res;
    }
}