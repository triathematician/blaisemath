/*
 * Copyright 2016 elisha.
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
 * blaise-graphics
 * --
 * Copyright (C) 2009 - 2016 Elisha Peterson
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


import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author elisha
 */
public class AttributeSetTest {
    
    @Test
    public void testOf() throws Exception {
        AttributeSet.of("key", null);
        // shouldn't throw an exception to have null values
        AttributeSet.of("key", "val");
    }
    
    @Test
    public void testNullWithParent() {
        AttributeSet par = AttributeSet.of("key", "val");
        
        AttributeSet set1 = AttributeSet.createWithParent(par);
        assertEquals("val", set1.get("key"));
        
        AttributeSet set2 = AttributeSet.createWithParent(par)
                .and("key", null);
        assertEquals(null, set2.get("key"));
    }
    
}
