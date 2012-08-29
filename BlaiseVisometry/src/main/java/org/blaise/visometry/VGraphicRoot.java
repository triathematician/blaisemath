/**
 * VGraphicRoot.java
 * Created Jan 28, 2011
 */
package org.blaise.visometry;

import java.awt.Component;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.blaise.graphics.Graphic;
import org.blaise.graphics.GraphicRoot;

/**
 * <p>
 *      Adds additional {@link Visometry} (local coordinates) support to the parent {@link GraphicRoot}.
 *      Functionally, this class behaves differently than {@code GraphicRoot}. It maintains the {@code Visometry},
 *      as well as the {@link VisometryProcessor} used to convert entries from local coordinates to view coordinates.
 *      It also maintains the {@link PlottableComposite}, which is another tree of plottable elements that can be
 *      <em>recomputed</em>.
 * </p>
 * <p>
 *      So in all, there are three trees maintained by this class:
 * </p>
 * <ul>
 *      <li>the {@link Graphic} tree maintained by the parent {@link GraphicRoot};</li>
 *      <li>the {@link VGraphic} tree maintained within the {@link PlottableComposite};</li>
 *      <li>the {@link Plottable} tree maintained within the {@link PlottableComposite}.</li>
 * </ul>
 * <p>
 *      The {@code Plottable} maintains the generic source object, and has the logic to generate one or more {@code VGraphic}s.
 *      Passing from the {@code Plottable} to the {@code VGraphic} requires <em>recomputation</em>
 *          (see {@link Plottable#recompute()} and {@link Plottable#getGraphicEntry()}).
 *      The {@code VGraphic} maintains a graphics primitive in local coordinates.
 *      Passing from the {@code VGraphic} to the {@code Graphic} requires <em>reconversion</em>
 *          (see {@link VGraphic#setUnconverted(boolean)} and {@link VGraphic#getWindowEntry()}).
 *      The conversion step is completed with the help of the {@code Visometry} and the {@code VisometryProcessor}.
 *      Both the computation and conversion steps are currently completed within
 *      the {@link VGraphicComponent#renderTo(java.awt.Graphics2D)} method.
 * </p>
 * <p>
 *      While in some cases, the duplication of objects may be inefficient,
 *      it allows certain things to be done without expensive recomputation.
 *      This is particularly true when the recomputation steps or conversion steps
 *      take a substantial amount of time, or it makes sense to separate them.
 *      For example, a 3D visometry would maintain 3D graphics primitives, which would
 *      NOT need to be recomputed whenever the view changes. In the above language,
 *      the 3D primitives comprise the {@code VGraphic}.
 * </p>
 * <p>
 *      The separation between {@code Graphic} and {@code VGraphic} also allows for
 *      customizing or manipulating the draw order of the graphics primitives,
 *      which is also important for 3D graphics.
 * </p>
 *
 * @param <C> type of local coordinates
 *
 * @author Elisha Peterson
 */
public class VGraphicRoot<C> extends VGraphicComposite<C> implements ChangeListener {

    /** Parent component upon which the graphics are drawn. */
    protected Component component;

    /** Stores the underlying visometry */
    protected final Visometry<C> visometry;
    /** Stores the processor used to translate coordinates */
    protected final VisometryProcessor<C> processor = new VisometryProcessor<C>();


    //
    // CONSTRUCTOR
    //

    /** Construct with visometry */
    public VGraphicRoot(Visometry<C> vis) {
        if (vis == null)
            throw new IllegalArgumentException("Visometry cannot be null!");
        visometry = vis;
        visometry.addChangeListener(this);
        setUnconverted(true);
    }

    /**
     * Sets the component associated with the graphic tree.
     * @param c the component
     */
    void initComponent(Component c) {
        this.component = c;
    }

    //
    // PROPERTY PATTERNS
    //

    @Override
    public Visometry<C> getVisometry() {
        return visometry;
    }

    //
    // EVENT HANDLING
    //

    /**
     * Repaints when a conversion is needed
     * @param en the requestor
     */
    @Override
    public void conversionNeeded(VGraphic en) {
        if (component != null)
            component.repaint();
    }

    @Override
    protected void fireConversionNeeded() {
        if (component != null)
            component.repaint();
    }

    /**
     * Updates all graphic entries needing conversion from local to window coordinates
     */
    public void reconvert()  {
        convert(visometry, processor);
    }

    public void stateChanged(ChangeEvent e) {
        if (e.getSource() == visometry) {
            setUnconverted(true);
            component.repaint();
        }
    }

}
