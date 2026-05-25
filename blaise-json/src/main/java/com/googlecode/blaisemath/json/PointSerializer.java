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
import com.google.common.collect.Range;
import java.awt.Point;
import tools.jackson.core.JacksonException;

/**
 * Serializes a {@link Range} to a {@link RangeProxy}.
 * @author Elisha Peterson
 */
public class PointSerializer extends ValueSerializer<Point> {

    @Override
    public void serialize(Point value, JsonGenerator gen, SerializationContext serializers) throws JacksonException {
        serializers.writeValue(gen, new PointProxy(value));
    }

}
