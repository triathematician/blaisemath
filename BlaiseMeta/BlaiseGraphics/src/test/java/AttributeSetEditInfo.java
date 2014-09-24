/**
 * AttributeSetEditInfo.java
 * Created Sep 18, 2014
 */
package com.googlecode.blaisemath.style;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2009 - 2014 Elisha Peterson
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


import com.google.common.collect.Maps;
import com.googlecode.blaisemath.firestarter.PropertyModelSupport;
import com.googlecode.blaisemath.firestarter.BeanEditorSupport;
import com.googlecode.blaisemath.firestarter.PropertySheet;
import java.awt.Component;
import java.util.Map;

/**
 * Describes editable attributes of an {@link AttributeSet}, along with their types.
 * This is useful when you want to make sure an attribute set has a given set
 * of attributes, e.g. when saving/restoring the set, or when editing the attributes
 * of the set.
 * 
 * @author Elisha
 */
public class AttributeSetEditInfo extends PropertyModelSupport {
    
    /** Mapping of attribute names to types */
    private final Map<String,Class> typeMap = Maps.newLinkedHashMap();
    
    /**
     * Create and return panel for editing an attribute set, using the specified
     * collection of editable attributes.
     * @param set the set to edit
     * @param editInfo which parameters to edit
     * @return property component for editing the attribute set
     */
    public static Component editPane(AttributeSet set, AttributeSetEditInfo editInfo) {
        return PropertySheet.create(new AttributeSetBeanEditorSupport(set, editInfo));
    }

    private static class AttributeSetBeanEditorSupport extends BeanEditorSupport {
        public AttributeSetBeanEditorSupport(AttributeSet set, AttributeSetEditInfo editInfo) {
        }
    }
    
    
}
