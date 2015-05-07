/**
 * AttributeSetAdapter.java
 * Created Sep 27, 2014
 */
package com.googlecode.blaisemath.style.xml;

/*
 * #%L
 * BlaiseSVG
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

import com.google.common.base.Converter;
import com.googlecode.blaisemath.style.AttributeSet;
import com.googlecode.blaisemath.style.AttributeSets;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * <p>
 *   Allows representing {@link AttributeSet}s in XML as strings.
 * </p>
 * @see AttributeSets#stringConverter() 
 * @author elisha
 */
public final class AttributeSetAdapter extends XmlAdapter<String, AttributeSet> {

    private static final Converter<AttributeSet,String> INST = AttributeSets.stringConverter();
    private static final Converter<String,AttributeSet> REV_INST = INST.reverse();
    
    @Override
    public AttributeSet unmarshal(String v) {
        return REV_INST.convert(v);
    }

    @Override
    public String marshal(AttributeSet v) {
        return INST.convert(v);
    }

}
