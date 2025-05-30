package com.googlecode.blaisemath.style.ui;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2019 - 2025 Elisha Peterson
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

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.googlecode.blaisemath.style.AttributeSet;
import com.googlecode.blaisemath.firestarter.property.PropertyModelSupport;
import com.googlecode.blaisemath.firestarter.property.PropertySheet;

import java.awt.Component;
import java.util.List;
import java.util.Map;

/**
 * Describes editable attributes of an {@link AttributeSet}, along with their types.
 * This is useful when you want to make sure an attribute set has a given set
 * of attributes, e.g. when saving/restoring the set, or when editing the attributes
 * of the set.
 *
 * @author Elisha Peterson
 */
public class AttributeSetPropertyModel extends PropertyModelSupport {
    
    /** List of expected attribute names */
    private final List<String> attributes = Lists.newArrayList();
    /** Mapping of expected attribute names and types */
    private final Map<String, Class<?>> typeMap = Maps.newLinkedHashMap();
    /** The attribute set for editing */
    private final AttributeSet aSet;

    public AttributeSetPropertyModel(AttributeSet aSet, Map<String, Class<?>> typeMap) {
        this.aSet = checkNotNull(aSet);
        this.typeMap.putAll(typeMap);
        this.attributes.addAll(typeMap.keySet());
    }
    
    /**
     * Create and return panel for editing an attribute set, using the specified
     * collection of editable attributes.
     * @param model describes edit object and parameters
     * @return property component for editing the attribute set
     */
    public static Component editPane(AttributeSetPropertyModel model) {
        return new PropertySheet(model);
    }

    @Override
    public int getSize() {
        return attributes.size();
    }

    @Override
    public String getElementAt(int index) {
        return attributes.get(index);
    }

    @Override
    public Class<?> getPropertyType(int i) {
        return typeMap.get(attributes.get(i));
    }

    @Override
    public boolean isWritable(int i) {
        return true;
    }

    @Override
    public Object getPropertyValue(int i) {
        return aSet.get(attributes.get(i));
    }

    @Override
    public void setPropertyValue(int i, Object o) {
        aSet.put(attributes.get(i), o);
    }

}
