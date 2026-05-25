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
import java.awt.geom.Rectangle2D;
import tools.jackson.core.JacksonException;

/**
 * Deserializes a {@link Rectangle2DProxy} to a {@link Rectangle2D}.
 * @author Elisha Peterson
 */
public class Rectangle2DDeserializer extends ValueDeserializer<Rectangle2D.Double> {

    @Override
    public Rectangle2D.Double deserialize(JsonParser p, DeserializationContext ctxt) throws JacksonException {
        Rectangle2DProxy proxy = p.readValueAs(Rectangle2DProxy.class);
        return proxy.toRectangle();
    }

}
