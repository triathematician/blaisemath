/**
 * PlottableGroup.java
 * Created on Feb 25, 2008
 */
package org.bm.blaise.specto.visometry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;
import javax.swing.event.ChangeListener;
import org.bm.blaise.sequor.component.TimeClock;

/**
 * <p>
 *  Represents a group of plottables.
 * </p>
 *
 * @param <C> class representing the underlying coordinate
 *
 * @author Elisha Peterson
 */
public class PlottableGroup<C> extends AbstractDynamicPlottable<C> implements AnimatingPlottable<C>, VisometryChangeListener, ChangeListener {

    /** Stores the elements in the group. */
    protected ArrayList<Plottable<? extends C>> plottables;
    
    /** Name of the group. */
    protected String name = "";
    
    /** Whether this element animates. */
    public boolean animationOn = true;

    //
    //
    // CONSTRUCTOR
    //
    //

    /**
     * Constructs, initializing list of plottables.
     */
    public PlottableGroup() {
        plottables = new ArrayList<Plottable<? extends C>>();
    }

    //
    //
    // BEANS
    //
    //

    /**
     * @return name of the group
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the group.
     * @param name the new name.
     */
    public void setName(String name) {
        if (name == null) {
            throw new NullPointerException("Name should not be null!");
        }
        this.name = name;
    }

    public boolean isAnimationOn() {
        return animationOn;
    }

    public void setAnimationOn(boolean newValue) {
        animationOn = newValue;
    }

    /**
     * Gets the list of elements.
     * @return list of plottable elements owned by this group
     */
    public List<Plottable<? extends C>> getElements() {
        return plottables;
    }

    /**
     * Overrides <code>toString()</code> to return the name of the group also.
     * @return the name of the group as well as the type of the class.
     */
    @Override
    public String toString() {
        return "PlottableGroup("+name+")";
    }


    //
    //
    // COMPOSITIONAL
    //
    //

    /** 
     * Remove all plottables from the group. Also de-registers each as a listener.
     */
    public void clear() {
        for (Plottable p : plottables) {
            p.removeChangeListener(this);
        }
        plottables.clear();
    }

    /**
     * Adds specified plottable to the group. Also sets up change listening.
     * @param plottable the plottable to add.
     */
    public void add(Plottable<? extends C> plottable) {
        if (plottable == null) {
            throw new NullPointerException("Plottable should not be null!");
        }
        plottables.add(plottable);
        plottable.addChangeListener(this);
    }

    /**
     * Adds specified plottables to the group. Also sets up change listening.
     * @param plottables the plottables to add.
     */
    public void addAll(Collection<? extends Plottable<? extends C>> plottables) {
        this.plottables.addAll(plottables);
        for (Plottable p : plottables) {
            p.addChangeListener(this);
        }
    }

    /**
     * Removes specified plottable from the group, and removes this class as a listener.
     * @param plottable the plottable to remove.
     * @return value of remove operation
     */
    public boolean remove(Plottable<? extends C> plottable) {
        if (plottable == null) {
            throw new NullPointerException("Plottable should not be null!");
        }
        plottable.removeChangeListener(this);
        return plottables.remove(plottable);
    }

    //
    //
    // COMPUTATIONAL/VISUAL
    //
    //

    /**
     * Tells all subclasses to recompute. Implements the <code>VisometryChangeListener</code>
     * itnerface.
     *
     * @param vis the visometry used for computing window coordinates
     * @param canvas the underlying canvas for painting objects
     */
    public void visometryChanged(Visometry vis, VisometryGraphics canvas) {
        for (Plottable p : plottables) {
            if (p instanceof VisometryChangeListener) {
                ((VisometryChangeListener) p).visometryChanged(vis, canvas);
            }
        }
    }

    /**
     * Tells only animating subclasses to recompute. Implements the <code>AnimatingPlottable</code>
     * interface.
     *
     * @param vis the visometry used for computing window coordinates
     * @param canvas the underlying canvas for painting objects
     * @param clock the clock for recomputing
     */
    public void recomputeAtTime(Visometry<C> vis, VisometryGraphics<C> canvas, TimeClock clock) {
        for (Plottable p : plottables) {
            if (p instanceof AnimatingPlottable && ((AnimatingPlottable) p).isAnimationOn()) {
                ((AnimatingPlottable) p).recomputeAtTime(vis, canvas, clock);
            }
        }
    }

    /**
     * Broadcast paint to all visible sub-elements in the group. This will handle
     * regular <code>Plottable</code>s, <code>AnimatingPlottable</code>s, and
     * <code>VisometryChangeListener</code> all in different ways. Objects are painted
     * ONLY if their <code>visible</code> property is set to <code>TRUE</code>.
     *
     * @param canvas the underlying canvas for painting objects
     */
    public void paintComponent(VisometryGraphics<C> canvas) {
        for (Plottable p : plottables) {
            if (p.isVisible()) {
                p.paintComponent(canvas);
                if (p instanceof AnimatingPlottable) {
                    // TODO - fill in
                } else if (p instanceof VisometryChangeListener) {
                    // TODO - fill in
                }
            }
        }
    }

    //
    //
    // MOUSE HANDLING
    //
    //

    /**
     * The group delegates mouse events to subclasses. This identifies the particular subclass
     * which handles the incoming events. Everything delegates to it.
     */
    protected VisometryMouseInputListener<C> mover;

    /**
     * Returns true if any subcomponent is close to the coordinate
     * @param e the coordinate to check
     * @return true if any subcomponent is close to the coordinate
     */
    public boolean isClickablyCloseTo(VisometryMouseEvent<C> e) {
        for (Plottable<? extends C> dp : plottables) {
            if ((dp instanceof AbstractDynamicPlottable) && ((AbstractDynamicPlottable<C>) dp).isClickablyCloseTo(e)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets list of subcomponents that are close to the specified coordinate
     * @param e the coordinate to check
     * @return a list of handlers that are close to the coordinate
     */
    protected List<VisometryMouseInputListener<C>> getHits(VisometryMouseEvent<C> e) {
        Vector<VisometryMouseInputListener<C>> result = new Vector<VisometryMouseInputListener<C>>();
        for (Plottable<? extends C> dp : plottables) {
            if (dp instanceof PlottableGroup) {
                result.addAll(((PlottableGroup) dp).getHits(e));
            } else if ((dp instanceof AbstractDynamicPlottable) && ((AbstractDynamicPlottable<C>) dp).isClickablyCloseTo(e)) {
                result.add((AbstractDynamicPlottable<C>) dp);
            }
        }
        return result;
    }

    /**
     * Finds an object to handle the event.
     */
    protected VisometryMouseInputListener<C> getVisometryMouseInputListener(VisometryMouseEvent<C> e) {
        List<VisometryMouseInputListener<C>> hits = getHits(e);
        if (hits.size() > 0) {
            return hits.get(0);
        } 
        return null;
    }

//    /**
//     * Passes the press event onto the first component that can handle it. The first class to handle it will
//     * set the source of the event to null, indicating that it has been handled.
//     * @param e an input mouse event
//     */
//    @Override
//    public void mousePressed(VisometryMouseEvent<C> e) {
//        List<VisometryMouseInputListener<C>> hits = getHits(e);
//        handlingMouse = ! hits.isEmpty();
//        if (handlingMouse) {
//            mover = hits.get(0);
//        }
//    }
//
//    /**
//     * Uses the current delegated "mover" to handle the event. The first class to handle it will
//     * set the source of the event to null, indicating that it has been handled.
//     * @param e the mouse event
//     */
//    @Override
//    public void mouseDragged(VisometryMouseEvent<C> e) {
//        handlingMouse = mover != null;
//        if (handlingMouse) {
//            mover.mouseDragged(e);
//        }
//    }
//
//    /**
//     * Uses the current delegated "mover" to handle the event. The first class to handle it will
//     * set the source of the event to null, indicating that it has been handled.
//     * @param e the mouse event
//     */
//    @Override
//    public void mouseReleased(VisometryMouseEvent<C> e) {
//        handlingMouse = mover != null;
//        if (handlingMouse) {
//            mover.mouseReleased(e);
//        }
//        mover = null;
//    }
//
//    /**
//     * Passes the click event onto the first component that can handle it. The first class to handle it will
//     * set the source of the event to null, indicating that it has been handled.
//     * @param e an input mouse event
//     */
//    @Override
//    public void mouseClicked(VisometryMouseEvent<C> e) {
//        List<VisometryMouseInputListener<C>> hits = getHits(e);
//        handlingMouse = ! hits.isEmpty();
//        if (handlingMouse) {
//            hits.get(0).mouseClicked(e);
//        }
//    }
//
//    @Override
//    public void mouseEntered(VisometryMouseEvent<C> e) {
//        handlingMouse = false;
//    }
//
//    @Override
//    public void mouseExited(VisometryMouseEvent<C> e) {
//        handlingMouse = false;
//    }
//
//    @Override
//    public void mouseMoved(VisometryMouseEvent<C> e) {
//        handlingMouse = false;
//    }


}
