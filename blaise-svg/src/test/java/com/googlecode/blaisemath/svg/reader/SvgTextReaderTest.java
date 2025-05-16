package com.googlecode.blaisemath.svg.reader;

/*-
 * #%L
 * blaise-svg
 * --
 * Copyright (C) 2014 - 2025 Elisha Peterson
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

import com.googlecode.blaisemath.primitive.AnchoredText;
import com.googlecode.blaisemath.svg.xml.SvgText;
import org.junit.Test;

import static com.googlecode.blaisemath.svg.reader.SvgReaderTest.assertSvgReadException;
import static org.junit.Assert.assertEquals;

public class SvgTextReaderTest {

    @Test
    public void createPrimitive() {
        assertSvgReadException(() -> new SvgTextReader().createPrimitive(null));
        AnchoredText at = new SvgTextReader().createPrimitive(SvgText.create(1, 2, "text"));
        assertEquals(1, at.x, .01f);
        assertEquals(2, at.y, .01f);
        assertEquals("text", at.getText());
    }

}
