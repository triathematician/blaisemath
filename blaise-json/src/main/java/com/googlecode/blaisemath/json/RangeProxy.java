package com.googlecode.blaisemath.json;

/*-
 * #%L
 * blaise-json
 * --
 * Copyright (C) 2019 - 2022 Elisha Peterson
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

import com.google.common.collect.BoundType;
import com.google.common.collect.Range;

/**
 * Proxy object for serializing ranges. Allows for serialization of most ranges,
 * but does not support serializing the "all" range, because it does not save the
 * range's class.
 * 
 * @author Elisha Peterson
 */
public class RangeProxy {
    
    private Object min;
    private Object max;
    private BoundType maxType;
    private BoundType minType;

    public RangeProxy() {
    }
    
    public RangeProxy(Range r) {
        this.min = r.hasLowerBound() ? r.lowerEndpoint() : null;
        this.max = r.hasUpperBound() ? r.upperEndpoint() : null;
        this.minType = r.hasLowerBound() ? r.lowerBoundType() : null;
        this.maxType = r.hasUpperBound() ? r.upperBoundType() : null;
    }

    public Range toRange() {
        boolean minComp = min == null || min instanceof Comparable;
        boolean maxComp = max == null || max instanceof Comparable;
        boolean compatible = min == null || max == null || min.getClass() == max.getClass();
        
        if ((min == null && max == null) || !minComp || !maxComp || !compatible) {
            throw new IllegalStateException("Invalid range: "+min+", "+max);
        } else if (minType == null) {
            return Range.upTo((Comparable) max, maxType);
        } else if (maxType == null) {
            return Range.downTo((Comparable) min, minType);
        } else {
            return Range.range((Comparable) min, minType, (Comparable) max, maxType);
        }
    }
    
    //<editor-fold defaultstate="collapsed" desc="PROPERTIES">

    public Object getMin() {
        return min;
    }

    public void setMin(Object min) {
        this.min = min;
    }

    public Object getMax() {
        return max;
    }

    public void setMax(Object max) {
        this.max = max;
    }

    public BoundType getMaxType() {
        return maxType;
    }

    public void setMaxType(BoundType maxType) {
        this.maxType = maxType;
    }

    public BoundType getMinType() {
        return minType;
    }

    public void setMinType(BoundType minType) {
        this.minType = minType;
    }

    //</editor-fold>
    
}
