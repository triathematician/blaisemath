/*
 * Copyright 2014 Elisha.
 *
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
 */

package com.googlecode.blaisemath.style;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2009 - 2015 Elisha Peterson
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
 * Provides an attribute set that cannot be changed.
 * 
 * @author Elisha
 */
public final class ImmutableAttributeSet extends AttributeSet {

    public ImmutableAttributeSet() {
        super();
    }

    public ImmutableAttributeSet(AttributeSet parent) {
        super(parent);
    }
    
    /**
     * Makes an immutable copy of the provided attribute set.
     * @param set the set to copy
     * @return an immutable copy
     */
    public static ImmutableAttributeSet copyOf(AttributeSet set) {
        ImmutableAttributeSet res = new ImmutableAttributeSet(set.getParent());
        res.attributeMap.putAll(set.attributeMap);
        return res;
    }
    
    /**
     * Makes an immutable copy of the provided attribute set.
     * @param set the set to copy
     * @param par the parent
     * @return an immutable copy
     */
    public static ImmutableAttributeSet copyOfWithAlternateParent(AttributeSet set, AttributeSet par) {
        ImmutableAttributeSet res = new ImmutableAttributeSet(par);
        res.attributeMap.putAll(set.attributeMap);
        return res;
    }

    @Override
    public Object remove(String key) {
        throw new UnsupportedOperationException("ImmutableAttributeSet cannot be modified.");
    }

    @Override
    public Object put(String key, Object value) {
        throw new UnsupportedOperationException("ImmutableAttributeSet cannot be modified.");
    }

    @Override
    public AttributeSet and(String key, Object val) {
        throw new UnsupportedOperationException("ImmutableAttributeSet cannot be modified.");
    }
    
}
