/*
 * Copyright 2014 Elisha.
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

package com.googlecode.blaisemath.editor;

/*
 * #%L
 * Firestarter
 * --
 * Copyright (C) 2009 - 2017 Elisha Peterson
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


import java.awt.Color;
import java.awt.Font;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Elisha
 */
public class EditorRegistrationTest {

    private static void checkEditor(Class objectType, Class editorType) {
        PropertyEditor pe = PropertyEditorManager.findEditor(objectType);
        assertNotNull(pe);
        assertEquals(editorType, pe.getClass());
    }
    
    /**
     * Test of registerEditors method, of class EditorRegistration.
     */
    @Test
    public void testRegisterEditors() {
        System.out.println("registerEditors");
        EditorRegistration.registerEditors();
        
        // basic editors

        checkEditor(boolean.class, BooleanEditor.class);
        checkEditor(Boolean.class, BooleanEditor.class);
        
        checkEditor(String.class, StringEditor.class);
        
        checkEditor(Enum.class, EnumEditor.class);
        
        // number editors

        checkEditor(Byte.class, NumberEditor.ByteEditor.class);
        checkEditor(byte.class, NumberEditor.ByteEditor.class);
        
        checkEditor(Short.class, NumberEditor.ShortEditor.class);
        checkEditor(short.class, NumberEditor.ShortEditor.class);
        
        checkEditor(Integer.class, NumberEditor.IntegerEditor.class);
        checkEditor(int.class, NumberEditor.IntegerEditor.class);
        
        checkEditor(Long.class, NumberEditor.LongEditor.class);
        checkEditor(long.class, NumberEditor.LongEditor.class);
        
        checkEditor(Float.class, NumberEditor.FloatEditor.class);
        checkEditor(float.class, NumberEditor.FloatEditor.class);
        
        checkEditor(Double.class, NumberEditor.DoubleEditor.class);
        checkEditor(double.class, NumberEditor.DoubleEditor.class);

        // array editors

        checkEditor(new String[]{}.getClass(), IndexedPropertyEditor.class);

        // point editors

        checkEditor(java.awt.Point.class, PointEditor.class);
        checkEditor(java.awt.Dimension.class, DimensionEditor.class);
        checkEditor(java.awt.Rectangle.class, RectangleEditor.class);
        checkEditor(java.awt.Insets.class, InsetsEditor.class);

        checkEditor(java.awt.geom.Point2D.Double.class, Point2DEditor.class);
        checkEditor(java.awt.geom.Line2D.Double.class, Line2DEditor.class);
        checkEditor(java.awt.geom.Ellipse2D.Double.class, RectangularShapeEditor.class);
        checkEditor(java.awt.geom.Rectangle2D.Double.class, RectangularShapeEditor.class);
        checkEditor(java.awt.geom.RectangularShape.class, RectangularShapeEditor.class);

        // complex editors

        checkEditor(Color.class, ColorEditor.class);
        checkEditor(Font.class, FontEditor.class);
    }
    
}
