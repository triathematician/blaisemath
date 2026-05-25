package com.googlecode.blaisemath.json;

/*-
 * #%L
 * blaise-json
 * --
 * Copyright (C) 2019 - 2026 Elisha Peterson
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

import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.SerializationContext;
import com.googlecode.blaisemath.palette.Palette;
import com.googlecode.blaisemath.style.AttributeSet;
import com.googlecode.blaisemath.style.AttributeSetCoder;
import tools.jackson.core.JacksonException;

/**
 * Serializes palettes as json strings, by converting to an attribute set.
 * @author Elisha Peterson
 */
public class PaletteSerializer extends ValueSerializer<Palette> {

    @Override
    public void serialize(Palette value, JsonGenerator gen, SerializationContext serializers) throws JacksonException {
        gen.writeString(new AttributeSetCoder().encode(toAttributeSet(value)));
    }
    
    /**
     * Convert palette to attribute set.
     * @param pal palette
     * @return attribute set
     */
    public static AttributeSet toAttributeSet(Palette pal) {
        return AttributeSet.create(pal.colorMap());
    }

}
