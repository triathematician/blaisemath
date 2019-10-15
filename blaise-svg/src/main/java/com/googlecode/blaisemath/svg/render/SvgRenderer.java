package com.googlecode.blaisemath.svg.render;

import com.google.common.collect.ImmutableMap;
import com.googlecode.blaisemath.graphics.*;
import com.googlecode.blaisemath.graphics.swing.JGraphicComponent;
import com.googlecode.blaisemath.graphics.swing.PanAndZoomHandler;
import com.googlecode.blaisemath.graphics.swing.render.*;
import com.googlecode.blaisemath.style.AttributeSet;
import com.googlecode.blaisemath.style.Styles;
import com.googlecode.blaisemath.svg.render.todo.*;
import com.googlecode.blaisemath.svg.xml.SvgGroup;
import com.googlecode.blaisemath.svg.xml.SvgRoot;
import com.googlecode.blaisemath.util.Colors;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Map;

public abstract class SvgRenderer<S> implements Renderer<S, SvgTreeBuilder> {

    public static final String BACKGROUND = "background";

    private static final Map<Class<? extends Renderer>, SvgRenderer<?>> RENDER_LOOKUP = ImmutableMap.<Class<? extends Renderer>, SvgRenderer<?>>builder()
            .put(IconRenderer.class, new SvgIconRenderer())
            .put(ImageRenderer.class, new SvgImageRenderer())
            .put(MarkerRenderer.class, new SvgMarkerRenderer())
            .put(PathRenderer.class, new SvgPathRenderer())
            .put(ShapeRenderer.class, new SvgShapeRenderer())
            .put(TextRenderer.class, new SvgTextRenderer())
            .put(WrappedTextRenderer.class, new SvgWrappedTextRenderer())
            .put(ArrowPathRenderer.class, new SvgArrowPathRenderer())
            .put(ClippedImageRenderer.class, new SvgClippedImageRenderer())
//            .put(ClippedMarkerRenderer.class, new SvgClippedMarkerRenderer())
            .put(GradientShapeRenderer.class, new SvgGradientShapeRenderer())
            .put(MultiArcStringRenderer.class, new SvgMultiArcStringRenderer())
            .put(MultilineTextRenderer.class, new SvgMultilineTextRenderer())
            .put(SlopedTextRenderer.class, new SvgSlopedTextRenderer())
            .put(TaperedPathRenderer.class, new SvgTaperedPathRenderer())
            .put(TextPathRenderer.class, new SvgTextPathRenderer())
            .build();

    @Override
    public @Nullable Rectangle2D boundingBox(Object primitive, AttributeSet style, @Nullable SvgTreeBuilder canvas) {
        return null;
    }

    @Override
    public boolean contains(Point2D point, Object primitive, AttributeSet style, @Nullable SvgTreeBuilder canvas) {
        return false;
    }

    @Override
    public boolean intersects(Rectangle2D rect, Object primitive, AttributeSet style, @Nullable SvgTreeBuilder canvas) {
        return false;
    }

    /**
     * Convert a graphic component to an SVG object, including a view box.
     * @param component component to convert
     * @return result
     */
    public static SvgRoot componentToSvg(JGraphicComponent component) {
        SvgTreeBuilder builder = new SvgTreeBuilder();
        builder.getRoot().setWidth(component.getWidth());
        builder.getRoot().setHeight(component.getHeight());
        builder.getRoot().setViewBoxAsRectangle(PanAndZoomHandler.getLocalBounds(component));
        builder.getRoot().addStyle(BACKGROUND, Colors.encode(component.getBackground()));
        builder.getRoot().addStyle(Styles.FONT_SIZE, Styles.DEFAULT_TEXT_STYLE.get(Styles.FONT_SIZE));
        builder.getRoot().addStyle(component.getGraphicRoot().getStyle().getAttributeMap());
        component.getGraphicRoot().getGraphics().forEach(g -> svgRender(g, builder));
        return builder.getRoot();
    }

    private static void svgRender(Graphic<Graphics2D> g, SvgTreeBuilder builder) throws SvgRenderException {
        if (g instanceof GraphicComposite) {
            SvgGroup grp = builder.beginGroup();
            grp.id = StyleWriter.id(g.getStyle());
            grp.style = StyleWriter.toString(g.getStyle());
            ((GraphicComposite<Graphics2D>) g).getGraphics().forEach(g2 -> svgRender(g2, builder));
            builder.endGroup();
        } else if (g instanceof PrimitiveGraphicSupport) {
            PrimitiveGraphicSupport p = (PrimitiveGraphicSupport) g;
            SvgRenderer r = RENDER_LOOKUP.get(p.getRenderer().getClass());
            if (r == null) {
                throw new SvgRenderException("Unsupported primitive renderer: "+p.getRenderer().getClass());
            }
            r.render(p.getPrimitive(), p.getStyle(), builder);
        } else if (g instanceof PrimitiveArrayGraphicSupport) {
            PrimitiveArrayGraphicSupport p = (PrimitiveArrayGraphicSupport) g;
            SvgGroup grp = builder.beginGroup();
            grp.id = StyleWriter.id(g.getStyle());
            grp.style = StyleWriter.toString(g.getStyle());

            SvgRenderer r = RENDER_LOOKUP.get(p.getRenderer().getClass());
            AttributeSet style = p.renderStyle();
            for (Object x : p.getPrimitive()) {
                r.render(x, style, builder);
            }
            builder.endGroup();
        }
    }

}
