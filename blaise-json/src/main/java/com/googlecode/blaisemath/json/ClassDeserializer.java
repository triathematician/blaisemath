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

import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableMap;
import tools.jackson.core.JacksonException;
import java.util.Map;

/**
 * Used for deserialization of class strings, allowing primitive strings to omit the "java.lang" prefix.
 * 
 * @author Elisha Peterson
 */
public final class ClassDeserializer extends ValueDeserializer<Class> {
    
    private static final Map<String,Class> PRIMITIVE_LOOKUP = ImmutableMap.<String,Class>builder()
            .put("long", long.class)
            .put("int", int.class)
            .put("short", short.class)
            .put("byte", byte.class)
            .put("float", float.class)
            .put("double", double.class)
            .put("boolean", boolean.class)
            .put("void", void.class)
            .build();

    @Override
    public Class deserialize(JsonParser p, DeserializationContext ctxt) throws JacksonException {
        String name = ((JsonNode) p.readValueAsTree()).asText();
        if (PRIMITIVE_LOOKUP.containsKey(name)) {
            return PRIMITIVE_LOOKUP.get(name);
        }
        
        try {
            return Class.forName("java.lang."+name);
        } catch (ClassNotFoundException ex) {
            // ignore
        }
        
        try {
            return Class.forName("java.util."+name);
        } catch (ClassNotFoundException ex) {
            // ignore
        }
        
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }
    
}
