package com.googlecode.blaisemath.json

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import java.awt.Point
import java.io.IOException

/*-
* #%L
* blaise-json
* --
* Copyright (C) 2019 - 2021 Elisha Peterson
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
*/ /**
 * Serializes a [Range] to a [RangeProxy].
 * @author Elisha Peterson
 */
class PointSerializer : JsonSerializer<Point?>() {
    @Throws(IOException::class)
    override fun serialize(value: Point?, gen: JsonGenerator?, serializers: SerializerProvider?) {
        gen.writeObject(PointProxy(value))
    }
}