package testutil;

/*
 * #%L
 * BlaiseSVG
 * --
 * Copyright (C) 2014 - 2019 Elisha Peterson
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.IOException;
import java.io.PrintStream;

import static java.util.Objects.requireNonNull;
import static org.junit.Assert.assertEquals;

public class XmlTestUtils {

    /** 
     * Tests a marshal/unmarshal cycle to ensure object compatibility 
     * @param testEquals whether to use a .equals test on the recycled object
     * @return recycled object
     */
    public static <C> C testRecycleObject(C o, @Nullable XmlMapper m,
            boolean testEquals, boolean testStringEquals, PrintStream w) throws IOException {
        requireNonNull(o);

        ObjectMapper mapper = m == null ? new XmlMapper() : m;
        String s1 = mapper.writeValueAsString(o);
        C m2 = (C) mapper.readValue(s1, o.getClass());
        if (testEquals) {
            assertEquals(m2, o);
        }
        if (testStringEquals) {
            assertEquals(""+o, ""+m2);
        }

        String s2 = mapper.writeValueAsString(m2);
        assertEquals(s1, s2);
        return m2;
    }
    
}
