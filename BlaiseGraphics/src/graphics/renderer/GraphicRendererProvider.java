/*
 * GraphicRendererProvider.java
 * Created Jan 22, 2011
 */
package graphics.renderer;

import java.awt.Color;

/**
 * <p>
 *   Provides methods that can be used to retrieve renderers specialized for drawing
 *   basic shapes, points, and text on a graphics canvas.
 * </p>
 *
 * @see GraphicRenderer
 *
 * @author Elisha Peterson
 */
public interface GraphicRendererProvider {

    /** @return a solid shape renderer (may perform both fill and stroke) */
    public ShapeRenderer getSolidRenderer();
    /** @return a path renderer */
    public ShapeRenderer getPathRenderer();
    /** @return a point renderer */
    public PointRenderer getPointRenderer();
    /** @return a string renderer */
    public StringRenderer getStringRenderer();

    /** Default instance of the renderer */
    public static GraphicRendererProvider DEFAULT = new GraphicRendererProvider() {
        final BasicShapeRenderer SOLID = new BasicShapeRenderer(Color.white, Color.black);
        final BasicStrokeRenderer PATH = new BasicStrokeRenderer(Color.black);
        final BasicPointRenderer POINT = new BasicPointRenderer();
        final BasicStringRenderer STRING = new BasicStringRenderer();

        public ShapeRenderer getSolidRenderer() { return SOLID; }
        public ShapeRenderer getPathRenderer() { return PATH; }
        public PointRenderer getPointRenderer() { return POINT; }
        public StringRenderer getStringRenderer() { return STRING; }
    };

    /** Delegates to a parent */
    public static class ParentProvider implements GraphicRendererProvider {
        public final GraphicRendererProvider parent;
        public ParentProvider() { this(GraphicRendererProvider.DEFAULT); }
        public ParentProvider(GraphicRendererProvider parent) { this.parent = parent; }
        public ShapeRenderer getSolidRenderer() { return parent == null ? null : parent.getSolidRenderer(); }
        public ShapeRenderer getPathRenderer() { return parent == null ? null : parent.getPathRenderer(); }
        public PointRenderer getPointRenderer() { return parent == null ? null : parent.getPointRenderer(); }
        public StringRenderer getStringRenderer() { return parent == null ? null : parent.getStringRenderer(); }
    }

    /** Generates a renderer that provides a path renderer only */
    public static class PathProvider extends ParentProvider {
        ShapeRenderer r;
        public PathProvider(ShapeRenderer r) { this.r = r; }
        public PathProvider(GraphicRendererProvider parent, ShapeRenderer r) { super(parent); this.r = r; }
        @Override
        public ShapeRenderer getPathRenderer() { return r; }
    }

}
