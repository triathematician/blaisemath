/**
 * PlotComponent.java
 * Created on Jul 30, 2009
 */

package visometry;

import org.bm.blaise.graphics.CompositeGraphicEntry;
import visometry.graphics.VGraphicCache;
import visometry.plottable.PlottableGroup;
import visometry.plottable.Plottable;
import org.bm.blaise.graphics.GraphicComponent;
import org.bm.blaise.graphics.GraphicEntry;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.Collection;
import java.util.List;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * <p>
 *   <code>PlotComponent</code> is an automatic implementation of a plot window with an underlying visometry.
 *   Plottable elements are stored within a single <code>PlottableGroup</code>, and this class passes on mouse
 *   events to that subgroup, or to the visometry if no plottable is able to handle it.
 *   We use generic typing to enforce the type of plottable that is added to this panel.
 *   Event handling is set up for a timer that is owned by the panel (<code>ActionEvent</code>s), as well as for any changes
 *   (<code>ChangeEvent</code>s) that occur on the plottables contained herein.
 * </p>
 * <p>
 *   Note that the plottables themselves are responsible for handling any computations
 *   required for displaying or painting. These computations may happen only once, or
 *   they may depend on the painted window, or they may depend on a timer. If a <code>Plottable</code> changes,
 *   and this class is notified, it is assumed that any plottables that must be recomputed
 *   have already achieved this, so no effort is placed into recomputation. But if the
 *   underlying <code>Visometry</code> changes in some way, a global <code>recompute</code>
 *   command is passed along to the <code>Plottable</code>s.
 * </p>
 * <p>
 *   This significant revision of the original <code>PlotComponent</code> class is intended to be very lightweight,
 *   without many extra features that might get in the way of the basic functionality.
 * </p>
 *
 * @param <C> the coordinate type of the plottable
 *
 * @see ClockTimer
 * @see Plottable
 * @see PlottableGroup
 * @see Visometry
 * @see VisometryGraphics
 *
 * @author Elisha Peterson
 */
public class PlotComponent<C> extends GraphicComponent
        implements ChangeListener, ComponentListener {

    // PROPERTIES

    /** Stores the visual components. */
    protected final PlottableGroup<C> plottables;
    /** Stores the cache (maintains visometry and the trees of graphics primitives in local & window coords) */
    protected final VGraphicCache<C> vCache;

    // CONSTRUCTOR

    /** Construction of a generic plot component requires a visometry. */
    public PlotComponent(Visometry<C> vis) {
        super(new VGraphicCache<C>(vis));

        plottables = new PlottableGroup<C>();
        plottables.addChangeListener(this); // receive notification of recompute and redraw requests

        vCache = (VGraphicCache<C>) cache;
        vCache.setPlottableGroup(plottables);
        vCache.addChangeListener(this);
        addComponentListener(this);

        setBackground(Color.WHITE);
        setOpaque(true);
        setPreferredSize(new Dimension(300, 200));

        repaint();
    }

    //
    // PROPERTY PATTERNS (DELEGATE)
    //

    /** @return visometry underlying the component */
    public Visometry<C> getVisometry() { return vCache.getVisometry(); }
    /** Sets up a new visometry for the component */
    public void setVisometry(Visometry<C> visometry) { vCache.setVisometry(visometry); }

    //
    // COMPOSITE METHODS (DELEGATE)
    //

    public void add(Plottable<C> plottable) { plottables.add(plottable); }
    public void add(int index, Plottable<C> plottable) { plottables.add(index, plottable); }
    public void addAll(Collection<? extends Plottable<C>> pp) { plottables.addAll(pp); }
    public boolean remove(Plottable<C> plottable) { return plottables.remove(plottable); }
    public void clear() { plottables.clear(); }
    public Plottable[] getPlottableArray() { return plottables.getPlottable(); }

    //
    // PAINT METHODS
    //

    Graphics2D canvas;

    /** Renders to an alternate graphics object. */
    @Override
    public final synchronized void renderTo(Graphics2D canvas) {
                    long t0 = System.currentTimeMillis();
        // this will recompute objects if necessary
//        System.out.println("Working with " + plottables.getPlottable().length + " plottables");
        plottables.recompute();
                    long t1 = System.currentTimeMillis();
        // this ensures all entries are converted into window coordinates
//        System.out.println("Computed " + plottables.getGraphicEntry().getEntries().size() + " local graphics");
        vCache.reconvert();
                    long t2 = System.currentTimeMillis();
        // this draws the entries using the active renderer
//        System.out.println("Converted to " + ((Collection)((CompositeGraphicEntry)vCache.local.getWindowEntry()).getEntries()).size() + " window primitives");
        super.renderTo(canvas);
        // the default mouse listener might also want to draw something
        renderOverlay(canvas);
                    long t3 = System.currentTimeMillis();
                    instrument(t0, t1, t2, t3);
    }

    /** Hook to render overlay elements. Called after everything else is drawn. */
    protected void renderOverlay(Graphics2D canvas) {}

    /** Minimum time in ms for alert */
    private static final int THRESH = 100;
    /** Counters */
    private static int _rec = 0, _conv = 0, _draw = 0;
    /** Prints warning messages if any of the render steps takes too long */
    private void instrument(long t0, long t1, long t2, long t3) {
        if (t1-t0 > THRESH)
            System.err.println("Long plottables recompute " + (++_rec) + ": " + (t1-t0));
        if (t2-t1 > THRESH)
            System.err.println("Long plottables conversion " + (++_conv) + ": " + (t2-t1));
        if (t3-t2 > THRESH)
            System.err.println("Long redraw " + (++_draw) + ": " + (t3-t2));
    }

    //
    // CHANGE EVENTS
    //

    @Override
    public void stateChanged(ChangeEvent e) {
        if (e.getSource() == plottables)
            repaint();
        else if (e.getSource() == vCache)
            repaint();
    }

    //
    // WINDOW SIZING EVENTS: PASS ON TO VISOMETRY
    //

    public void componentResized(ComponentEvent e) { vCache.getVisometry().setWindowBounds(getVisibleRect()); }
    public void componentShown(ComponentEvent e) { vCache.getVisometry().setWindowBounds(getVisibleRect()); }
    public void componentHidden(ComponentEvent e) {}
    public void componentMoved(ComponentEvent e) {}

}
