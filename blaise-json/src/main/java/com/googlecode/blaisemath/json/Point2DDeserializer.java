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
import java.awt.geom.Point2D;
import tools.jackson.core.JacksonException;

/**
 * Deserializes a {@link Point2D.Double}.
 * @author Elisha Peterson
 */
public class Point2DDeserializer extends ValueDeserializer<Point2D.Double> {

    @Override
    public Point2D.Double deserialize(JsonParser p, DeserializationContext ctxt) throws JacksonException {
        return p.readValueAs(Point2D.Double.class);
    }

}
