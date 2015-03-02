/**
 * SketchGraphics.java
 * Created Oct 11, 2014
 */
package com.googlecode.blaisemath.sketch;

/*
 * #%L
 * BlaiseSketch
 * --
 * Copyright (C) 2014 - 2015 Elisha Peterson
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


import static com.google.common.base.Preconditions.checkArgument;
import com.googlecode.blaisemath.graphics.core.Graphic;
import com.googlecode.blaisemath.graphics.core.GraphicComposite;
import com.googlecode.blaisemath.graphics.core.PrimitiveGraphic;
import com.googlecode.blaisemath.graphics.core.PrimitiveGraphicSupport;
import com.googlecode.blaisemath.graphics.swing.ShapeRenderer;
import com.googlecode.blaisemath.style.AttributeSet;
import com.googlecode.blaisemath.style.Styles;
import com.googlecode.blaisemath.util.AnchoredImage;
import com.googlecode.blaisemath.util.AnchoredText;
import com.googlecode.blaisemath.util.Configurer;
import com.googlecode.blaisemath.util.MenuConfig;
import com.googlecode.blaisemath.util.OrientedPoint2D;
import com.googlecode.blaisemath.util.swing.ActionMapContextMenuInitializer;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ActionMap;

/**
 * Utility class for working with sketch graphics.
 * @author elisha
 */
public class SketchGraphicUtils {
    
    private static final String GRAPHIC_CM_KEY = "Graphic";
    private static final AttributeSet BOUNDING_BOX_STYLE = Styles.strokeWidth(Color.red, .5f).immutable();
    private static final List<Class> SUPPORTED_PRIMITIVES = Arrays.<Class>asList(
        Rectangle2D.Double.class, Ellipse2D.Double.class, Line2D.Double.class,
        GeneralPath.class, OrientedPoint2D.class, AnchoredText.class, AnchoredImage.class
    );
    
    private SketchGraphicUtils() {
    }
    
    /**
     * Return class that can be used to configure a graphic.
     * @return configurer
     */
    public static Configurer<Graphic<Graphics2D>> configurer() {
        return new Configurer<Graphic<Graphics2D>>(){
            @Override
            public void configure(Graphic<Graphics2D> obj) {
                SketchGraphicUtils.configureGraphic(obj);
            }
        };
    }

    /**
     * Generate a copy of the provided graphic.
     * @param sel graphic to copy
     * @return copy of it
     */
    public static Graphic<Graphics2D> copy(Graphic<Graphics2D> sel) {
        checkArgument(supported(sel));
        if (sel instanceof GraphicComposite) {
            return copyComposite((GraphicComposite<Graphics2D>) sel);
        } else {
            return copyPrimitive((PrimitiveGraphic<?, Graphics2D>) sel);
        }
    }
    
    /** Copy composite along with child graphics */
    private static GraphicComposite<Graphics2D> copyComposite(GraphicComposite<Graphics2D> comp) {
        GraphicComposite<Graphics2D> res = new GraphicComposite<Graphics2D>();
        copyGraphicProperties(comp, res);
        for (Graphic<Graphics2D> gr : comp.getGraphics()) {
            res.addGraphic(copy(gr));
        }
        res.setStyle(comp.getStyle().copy());
        return res;
    }

    /** Copy primitive graphic */
    private static <T> PrimitiveGraphic<T,Graphics2D> copyPrimitive(PrimitiveGraphic<T,Graphics2D> src) {
        T prim = src.getPrimitive();
        T resPrim = null;
        if (prim.getClass() == Rectangle2D.Double.class) {
            Rectangle2D.Double rprim = (Rectangle2D.Double) prim;
            resPrim = (T) new Rectangle2D.Double(rprim.x, rprim.y, rprim.width, rprim.height);
        } else if (prim.getClass() == Ellipse2D.Double.class) {
            Ellipse2D.Double rprim = (Ellipse2D.Double) prim;
            resPrim = (T) new Ellipse2D.Double(rprim.x, rprim.y, rprim.width, rprim.height);
        } else if (prim.getClass() == Line2D.Double.class) {
            Line2D.Double rprim = (Line2D.Double) prim;
            resPrim = (T) new Line2D.Double(rprim.x1, rprim.y1, rprim.x2, rprim.y2);
        } else if (prim.getClass() == GeneralPath.class) {
            GeneralPath gprim = (GeneralPath) prim;
            resPrim = (T) new GeneralPath(gprim);
        } else if (prim.getClass() == OrientedPoint2D.class) {
            OrientedPoint2D gprim = (OrientedPoint2D) prim;
            resPrim = (T) new OrientedPoint2D(gprim);
        } else if (prim.getClass() == AnchoredText.class) {
            AnchoredText gprim = (AnchoredText) prim;
            resPrim = (T) new AnchoredText(gprim, gprim.getText());
        } else if (prim.getClass() == AnchoredImage.class) {
            AnchoredImage gprim = (AnchoredImage) prim;
            resPrim = (T) new AnchoredImage(gprim.x, gprim.y, 
                    gprim.getWidth(), gprim.getHeight(),
                    gprim.getOriginalImage(), gprim.getReference());
        } else {
            throw new IllegalStateException("Unexpected");
        }
        PrimitiveGraphic<T, Graphics2D> res = new PrimitiveGraphic<T,Graphics2D>(
                resPrim, src.getStyle().copy(), src.getRenderer());
        copyGraphicProperties(src, res);
        return res;
    }
    
    /**
     * Copy graphic properties from source to target.
     */
    private static void copyGraphicProperties(Graphic<Graphics2D> src, Graphic<Graphics2D> tgt) {
        tgt.setContextMenuEnabled(src.isContextMenuEnabled());
        tgt.setDefaultTooltip(src.getDefaultTooltip());
        tgt.setTooltipEnabled(src.isTooltipEnabled());
        tgt.setHighlightEnabled(src.isHighlightEnabled());
        tgt.setMouseEnabled(src.isMouseEnabled());
        tgt.setSelectionEnabled(src.isSelectionEnabled());
        // do not copy style hints, as those are set by user behavior
    }
    
    /**
     * Return true if the provided graphic is "valid" in the context of the Sketch
     * application. Requires a {@link PrimitiveGraphic} or a {@link GraphicComposite}.
     * In the former case, supports only certain kinds of primitives.
     */
    static boolean supported(Graphic<Graphics2D> gfc) {
        if (gfc instanceof GraphicComposite) {
            return true;
        } else if (gfc instanceof PrimitiveGraphic) {
            return SUPPORTED_PRIMITIVES.contains(((PrimitiveGraphic)gfc).getPrimitive().getClass());
        } else {
            return false;
        }
    }
    
    /**
     * Return a user-friendly name for the primitive of the graphic.
     * @param gfc graphic
     * @return user-friendly name
     */
    static String friendlyName(Graphic<Graphics2D> gfc) {
        if (gfc instanceof PrimitiveGraphicSupport) {
            Object primitive = ((PrimitiveGraphicSupport)gfc).getPrimitive();
            if (primitive instanceof Rectangle2D.Double) {
                return "Rectangle";
            } else if (primitive instanceof Ellipse2D.Double) {
                return "Ellipse";
            } else if (primitive instanceof GeneralPath) {
                return "Path";
            } else if (primitive instanceof OrientedPoint2D) {
                return "Point";
            } else if (primitive instanceof Line2D.Double) {
                return "Line";
            } else if (primitive instanceof AnchoredText) {
                return "Text";
            } else if (primitive instanceof AnchoredImage) {
                return "Image";
            } else {
                return primitive.getClass().getName();
            }
        } else if (gfc instanceof GraphicComposite) {
            return "Group";
        } else {
            return gfc.toString();
        }
    }

    /**
     * Configure the given graphic (optionally), and all child graphics. This includes
     * setting up selection state, mouse handling, drag support, context menus,
     * and bounding box behavior.
     * 
     * @param gfc the graphic to configure
     * @param configureThis if true, configure the provided graphic and its children; otherwise, just its children
     */
    static void configureGraphicTree(Graphic<Graphics2D> gfc, boolean configureThis) {
        if (configureThis) {
            configureGraphic(gfc);
        }
        if (gfc instanceof GraphicComposite) {
            for (Graphic<Graphics2D> g : ((GraphicComposite<Graphics2D>) gfc).getGraphics()) {
                configureGraphicTree(g, true);
            }
        }
    }
    

    /**
     * Configure the given graphic. This includes setting up selection state,
     * mouse handling, drag support, context menus, and bounding box behavior.
     * 
     * @param gfc the graphic to configure
     */
    public static void configureGraphic(Graphic<Graphics2D> gfc) {
        gfc.setSelectionEnabled(true);
        gfc.setMouseEnabled(true);
        if (gfc instanceof PrimitiveGraphicSupport && ((PrimitiveGraphicSupport)gfc).isDragCapable()) {
            ((PrimitiveGraphicSupport)gfc).setDragEnabled(true);
        }

        try {
            ActionMap am = SketchFrameView.getActionMap();
            List<String> list = (List<String>) MenuConfig.readConfig(SketchApp.class).get(GRAPHIC_CM_KEY);
            gfc.addContextMenuInitializer(new ActionMapContextMenuInitializer<Graphic<Graphics2D>>(
                    friendlyName(gfc), am, list.toArray(new String[0])));
        } catch (IOException x) {
            Logger.getLogger(SketchGraphicUtils.class.getName()).log(Level.SEVERE,
                    "Invalid context menu config", x);
        }
        
        if (gfc instanceof GraphicComposite) {
            GraphicComposite<Graphics2D> gc = (GraphicComposite<Graphics2D>) gfc;
            gc.setBoundingBoxStyle(BOUNDING_BOX_STYLE);
            gc.setBoundingBoxRenderer(ShapeRenderer.getInstance());
            gc.setBoundingBoxVisible(true);
            gc.setSelectionEnabled(true);
        }
    }
    
}
