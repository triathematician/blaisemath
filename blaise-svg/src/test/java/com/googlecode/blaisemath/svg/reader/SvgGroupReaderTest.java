package com.googlecode.blaisemath.svg.reader;

/*-
 * #%L
 * blaise-svg
 * --
 * Copyright (C) 2014 - 2020 Elisha Peterson
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

import com.googlecode.blaisemath.svg.xml.SvgCircle;
import com.googlecode.blaisemath.svg.xml.SvgElement;
import com.googlecode.blaisemath.svg.xml.SvgGroup;
import org.junit.Test;

import java.util.Arrays;

import static com.googlecode.blaisemath.svg.reader.SvgReaderTest.assertSvgReadException;
import static org.junit.Assert.assertEquals;

public class SvgGroupReaderTest {

    @Test
    public void createPrimitive() {
        assertSvgReadException(() -> new SvgGroupReader().createPrimitive(null));

        SvgElement e1 = SvgCircle.create(1, 2, 3);
        SvgGroup g1 = SvgGroup.create(e1);
        assertEquals(Arrays.asList(e1), new SvgGroupReader().createPrimitive(g1));
    }

}
