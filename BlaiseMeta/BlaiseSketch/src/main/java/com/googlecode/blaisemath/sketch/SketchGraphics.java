/**
 * SketchGraphics.java
 * Created Oct 11, 2014
 */
package com.googlecode.blaisemath.sketch;

/*
 * #%L
 * BlaiseSketch
 * --
 * Copyright (C) 2009 - 2014 Elisha Peterson
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


import com.googlecode.blaisemath.graphics.core.Graphic;
import com.googlecode.blaisemath.graphics.core.GraphicComposite;
import com.googlecode.blaisemath.graphics.core.PrimitiveGraphicSupport;
import com.googlecode.blaisemath.graphics.swing.ShapeRenderer;
import com.googlecode.blaisemath.style.AttributeSet;
import com.googlecode.blaisemath.style.Styles;
import com.googlecode.blaisemath.util.swing.ActionMapContextMenuInitializer;
import java.awt.Color;
import java.awt.Graphics2D;
import javax.swing.ActionMap;

/**
 * Utility class for working with sketch graphics.
 * @author elisha
 */
public class SketchGraphics {
    
    private static final AttributeSet BOUNDING_BOX_STYLE = Styles.strokeWidth(Color.red, .5f).immutable();
    
    private SketchGraphics() {
    }

    /** Configure the given graphics, and all child graphics */
    public static void configureGraphicTree(Graphic<Graphics2D> gfc, boolean configureThis) {
        if (configureThis) {
            configureGraphic(gfc);
        }
        if (gfc instanceof GraphicComposite) {
            for (Graphic<Graphics2D> g : ((GraphicComposite<Graphics2D>) gfc).getGraphics()) {
                configureGraphicTree(g, true);
            }
        }
    }
    
    public static void configureGraphic(Graphic<Graphics2D> gfc) {
        gfc.setSelectionEnabled(true);
        gfc.setMouseEnabled(true);
        if (gfc instanceof PrimitiveGraphicSupport && ((PrimitiveGraphicSupport)gfc).isDragCapable()) {
            ((PrimitiveGraphicSupport)gfc).setDragEnabled(true);
        }

        ActionMap am = BlaiseSketchFrameView.getActionMap();
        gfc.addContextMenuInitializer(new ActionMapContextMenuInitializer<Graphic<Graphics2D>>(
            am, "editGraphic", "editGraphicStyle", "addGraphicAttribute", "deleteGraphic", null,
                "copyStyle", "pasteStyle", null,
                "groupSelected", "ungroupSelected", "deleteSelected"));
        
        if (gfc instanceof GraphicComposite) {
            GraphicComposite<Graphics2D> gc = (GraphicComposite<Graphics2D>) gfc;
            gc.setBoundingBoxStyle(BOUNDING_BOX_STYLE);
            gc.setBoundingBoxRenderer(ShapeRenderer.getInstance());
            gc.setBoundingBoxVisible(true);
            gc.setSelectionEnabled(true);
        }
    }
    
}
