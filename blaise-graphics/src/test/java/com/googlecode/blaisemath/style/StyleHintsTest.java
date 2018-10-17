package com.googlecode.blaisemath.style;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2009 - 2018 Elisha Peterson
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





import com.googlecode.blaisemath.style.AttributeSet;
import com.googlecode.blaisemath.style.StyleHints;
import java.awt.Color;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Elisha Peterson
 */
public class StyleHintsTest {

    /**
     * Test of applyHints method, of class StyleModifiers.
     */
    @Test
    public void testApplyHints() {
        System.out.println("applyHints");
        AttributeSet hints = new AttributeSet();
        
        assertEquals(new Color(50,0,0,128), StyleHints.modifyColorsDefault(new Color(50,0,0,128), hints));
        
        hints.put(StyleHints.HIDDEN_HINT, true);
        assertEquals(new Color(50,0,0,0), StyleHints.modifyColorsDefault(new Color(50,0,0,128), hints));
        hints.put(StyleHints.HIDDEN_HINT, false);
        
        hints.put(StyleHints.HILITE_HINT, true);
        assertEquals(new Color(114,64,64,128), StyleHints.modifyColorsDefault(new Color(50,0,0,128), hints));
        hints.put(StyleHints.HILITE_HINT, false);
        
        hints.put(StyleHints.SELECTED_HINT, true);
        assertEquals(new Color(50,0,0,128), StyleHints.modifyColorsDefault(new Color(50,0,0,128), hints));
        
        assertNull(StyleHints.modifyColorsDefault(null, hints));
    }
    
}
