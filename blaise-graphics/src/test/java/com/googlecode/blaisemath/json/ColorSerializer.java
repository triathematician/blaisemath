package com.googlecode.blaisemath.json;

/*
 * #%L
 * blaise-graphics
 * --
 * Copyright (C) 2009 - 2017 Elisha Peterson
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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.googlecode.blaisemath.util.Colors;
import java.awt.Color;
import java.io.IOException;

/**
 *
 * @author elisha
 */
public class ColorSerializer extends JsonSerializer<Color> {

    @Override
    public void serialize(Color t, JsonGenerator jg, SerializerProvider sp) throws IOException, JsonProcessingException {
        jg.writeString(Colors.stringConverter().convert(t));
    }
    
}
