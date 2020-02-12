package com.googlecode.blaisemath.json;

/*-
 * #%L
 * blaise-json
 * --
 * Copyright (C) 2019 - 2020 Elisha Peterson
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

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;

/**
 * Used for deserialization of class strings, allowing primitive strings to omit the "java.lang" prefix.
 * 
 * @author Elisha Peterson
 */
public final class ClassSerializer extends JsonSerializer<Class> {

    @Override
    public void serialize(Class value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        String nm = value.getName();
        if (nm.startsWith("java.lang.") || nm.startsWith("java.util.")) {
            nm = nm.substring(10);
        }
        gen.writeString(nm);
    }
    
}
