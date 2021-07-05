package com.googlecode.blaisemath.json

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.google.common.collect.Range
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
 * Deserializes a [RangeProxy] to a [Range].
 * @author Elisha Peterson
 */
class RangeDeserializer : JsonDeserializer<Range<*>?>() {
    @Throws(IOException::class)
    override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): Range<*>? {
        val proxy = p.readValueAs(RangeProxy::class.java)
        return proxy.toRange()
    }
}