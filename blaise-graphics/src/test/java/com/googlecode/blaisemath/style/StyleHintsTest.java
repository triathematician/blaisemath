package com.googlecode.blaisemath.style;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2009 - 2021 Elisha Peterson
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

import com.google.common.collect.Sets;
import org.junit.Test;

import java.awt.*;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class StyleHintsTest {

    @Test
    public void testApplyHints() {
        Set<String> hints = Sets.newHashSet();

        assertEquals(new Color(50, 0, 0, 128), StyleHints.modifyColorsDefault(new Color(50, 0, 0, 128), hints));

        hints.add(StyleHints.HIDDEN_HINT);
        assertEquals(new Color(50, 0, 0, 0), StyleHints.modifyColorsDefault(new Color(50, 0, 0, 128), hints));
        hints.remove(StyleHints.HIDDEN_HINT);

        hints.add(StyleHints.HIGHLIGHT_HINT);
        assertEquals(new Color(114, 64, 64, 128), StyleHints.modifyColorsDefault(new Color(50, 0, 0, 128), hints));
        hints.remove(StyleHints.HIGHLIGHT_HINT);

        hints.add(StyleHints.SELECTED_HINT);
        assertEquals(new Color(50, 0, 0, 128), StyleHints.modifyColorsDefault(new Color(50, 0, 0, 128), hints));

        assertNull(StyleHints.modifyColorsDefault(null, hints));
    }
    
}
