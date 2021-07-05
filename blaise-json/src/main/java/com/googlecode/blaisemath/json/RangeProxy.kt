package com.googlecode.blaisemath.json

import com.google.common.collect.BoundType
import com.google.common.collect.Range

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
 * Proxy object for serializing ranges. Allows for serialization of most ranges,
 * but does not support serializing the "all" range, because it does not save the
 * range's class.
 *
 * @author Elisha Peterson
 */
class RangeProxy {
    private var min: Any? = null
    private var max: Any? = null
    private var maxType: BoundType? = null
    private var minType: BoundType? = null

    constructor() {}
    constructor(r: Range<*>?) {
        min = if (r.hasLowerBound()) r.lowerEndpoint() else null
        max = if (r.hasUpperBound()) r.upperEndpoint() else null
        minType = if (r.hasLowerBound()) r.lowerBoundType() else null
        maxType = if (r.hasUpperBound()) r.upperBoundType() else null
    }

    fun toRange(): Range<*>? {
        val minComp = min == null || min is Comparable<*>
        val maxComp = max == null || max is Comparable<*>
        val compatible = min == null || max == null || min.javaClass == max.javaClass
        return if (min == null && max == null || !minComp || !maxComp || !compatible) {
            throw IllegalStateException("Invalid range: $min, $max")
        } else if (minType == null) {
            Range.upTo(max as Comparable<*>?, maxType)
        } else if (maxType == null) {
            Range.downTo(min as Comparable<*>?, minType)
        } else {
            Range.range(min as Comparable<*>?, minType, max as Comparable<*>?, maxType)
        }
    }

    //<editor-fold defaultstate="collapsed" desc="PROPERTIES">
    fun getMin(): Any? {
        return min
    }

    fun setMin(min: Any?) {
        this.min = min
    }

    fun getMax(): Any? {
        return max
    }

    fun setMax(max: Any?) {
        this.max = max
    }

    fun getMaxType(): BoundType? {
        return maxType
    }

    fun setMaxType(maxType: BoundType?) {
        this.maxType = maxType
    }

    fun getMinType(): BoundType? {
        return minType
    }

    fun setMinType(minType: BoundType?) {
        this.minType = minType
    } //</editor-fold>
}