package com.googlecode.blaisemath.json;

/*-
 * #%L
 * blaise-json
 * --
 * Copyright (C) 2019 Elisha Peterson
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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.google.common.collect.Maps;
import com.googlecode.blaisemath.encode.ColorCoder;
import com.googlecode.blaisemath.palette.MapPalette;
import com.googlecode.blaisemath.palette.Palette;
import com.googlecode.blaisemath.style.AttributeSet;
import com.googlecode.blaisemath.style.AttributeSetCoder;
import com.googlecode.blaisemath.util.Colors;
import java.awt.Color;
import java.io.IOException;
import java.util.Map;

/**
 * Deserializes an {@link AttributeSet} from a string.
 * @author Elisha Peterson
 */
public class PaletteDeserializer extends JsonDeserializer<Palette> {

    @Override
    public Palette deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        String val = p.readValueAs(String.class);
        return toPalette(new AttributeSetCoder().decode(val));
    }
    
    /**
     * Convert attribute set to palette, by restricting to just values that are colors or color strings.
     * @param attr attribute set
     * @return palette
     */
    public static Palette toPalette(AttributeSet pal) {
        Map<String, Color> cols = Maps.newLinkedHashMap();
        pal.getAttributeMap().entrySet().forEach(en -> {
            Object val = en.getValue();
            if (val instanceof String) {
                val = ((String) val).trim();
            }
            if (val instanceof Color) {
                cols.put(en.getKey(), (Color) val);
            } else if (val instanceof String && ColorCoder.decodable((String) val)) {
                cols.put(en.getKey(), Colors.decode((String) val));
            }
        });
        return MapPalette.create(cols);
    }

}
