package com.googlecode.blaisemath.graphics.svg;

/*
 * #%L
 * blaise-svg
 * --
 * Copyright (C) 2014 - 2018 Elisha Peterson
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


import static com.googlecode.blaisemath.svg.SVGUtils.parseLength;
import static junit.framework.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author elisha
 */
public class SVGUtilsTest {

    @Test
    public void testParseLength() {
        assertEquals(0.0, parseLength(null));
        assertEquals(0.0, parseLength("not a number"));
        assertEquals(0.0, parseLength("auto"), 1e-3);
        assertEquals(0.0, parseLength("50.5%"), 1e-3);
                
        assertEquals(12.0, parseLength("12"), 1e-3);
        assertEquals(15.5999, parseLength("12px"), 1e-3);
        assertEquals(12.0, parseLength("12pt"), 1e-3);
        assertEquals(864.0, parseLength("12in"), 1e-3);
        assertEquals(143.4599, parseLength("12em"), 1e-3);
        assertEquals(-143.4599, parseLength("-12ex"), 1e-3);
        assertEquals(16.2499, parseLength("12.5px"), 1e-3);
    }
    
}
