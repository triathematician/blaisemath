/**
 * ImmutableAttributeSet.java
 * Created 2016
 */
package com.googlecode.blaisemath.style;

import com.google.common.base.Optional;
import javax.annotation.Nullable;

/*
 * #%L    
 * blaise-common
 * --
 * Copyright (C) 2014 - 2017 Elisha Peterson
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

/**
 * Provides an attribute set that cannot be changed. (It's parent and values
 * may be able to be changed however.)
 * 
 * @author Elisha
 */
final class ImmutableAttributeSet extends AttributeSet {

    // prevent instantiation elsewhere
    private ImmutableAttributeSet() {
    }
    
    /**
     * Makes an immutable copy of the provided attribute set.
     * @param set the set to copy
     * @return an immutable copy
     */
    static ImmutableAttributeSet immutableCopyOf(AttributeSet set) {
        ImmutableAttributeSet res = new ImmutableAttributeSet();
        res.parent = Optional.fromNullable(set.getParent());
        res.attributeMap.putAll(set.attributeMap);
        return res;
    }
    
    /**
     * Makes an immutable copy of the provided attribute set.
     * @param set the set to copy
     * @param par the parent
     * @return an immutable copy
     */
    static ImmutableAttributeSet immutableCopyOf(AttributeSet set, @Nullable AttributeSet par) {
        ImmutableAttributeSet res = new ImmutableAttributeSet();
        res.parent = Optional.fromNullable(par);
        res.attributeMap.putAll(set.attributeMap);
        return res;
    }

    @Override
    public Object remove(String key) {
        notSupported();
        return null;
    }

    @Override
    public Object put(String key, Object value) {
        notSupported();
        return null;
    }

    @Override
    public AttributeSet and(String key, Object val) {
        notSupported();
        return null;
    }
    
    private void notSupported() {
        throw new UnsupportedOperationException("ImmutableAttributeSet cannot be modified.");
    }
    
}
