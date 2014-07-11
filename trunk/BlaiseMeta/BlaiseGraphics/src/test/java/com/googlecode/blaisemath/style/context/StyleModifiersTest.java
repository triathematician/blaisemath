package com.googlecode.blaisemath.style.context;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2009 - 2014 Elisha Peterson
 * --
 * Licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
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


import com.googlecode.blaisemath.style.context.StyleHintSet;
import com.googlecode.blaisemath.style.context.StyleModifiers;
import java.awt.Color;
import com.googlecode.blaisemath.style.PathStyle;
import com.googlecode.blaisemath.style.PointStyle;
import com.googlecode.blaisemath.style.ShapeStyle;
import com.googlecode.blaisemath.style.TextStyle;
import com.googlecode.blaisemath.util.ColorUtils;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Elisha
 */
public class StyleModifiersTest {

    /**
     * Test of applyHints method, of class StyleModifiers.
     */
    @Test
    public void testApplyHints() {
        System.out.println("applyHints");
        StyleHintSet set = new StyleHintSet();
        assertEquals(new Color(50,0,0,128), StyleModifiers.applyHints(new Color(50,0,0,128), set));
        set.add(StyleHintSet.HIDDEN_HINT);
        assertEquals(new Color(50,0,0,0), StyleModifiers.applyHints(new Color(50,0,0,128), set));
        set.remove(StyleHintSet.HIDDEN_HINT);
        set.add(StyleHintSet.HILITE_HINT);
        assertEquals(new Color(114,64,64,128), StyleModifiers.applyHints(new Color(50,0,0,128), set));
        set.remove(StyleHintSet.HILITE_HINT);
        set.add(StyleHintSet.SELECTED_HINT);
        assertEquals(new Color(114,64,64,128), StyleModifiers.applyHints(new Color(50,0,0,128), set));
    }
    
}
