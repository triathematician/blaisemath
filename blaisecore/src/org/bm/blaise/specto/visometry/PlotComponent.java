/**
 * PlotComponent.java
 * Created on Jul 30, 2009
 */
package org.bm.blaise.specto.visometry;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputListener;
import org.bm.blaise.sequor.component.ClockTimer;

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
 *   they may depend on the painted window, or they may depend on a timer. This class
 *   owns a <code>ClockTimer</code> that may be used to repaint <code>AnimatingPlottable</code>s
 *   as that timer fires <code>ActionEvent</code>s. If a <code>Plottable</code> changes,
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
public class PlotComponent<C> extends JPanel implements ActionListener, ChangeListener, ComponentListener, MouseInputListener, MouseWheelListener {

    //
    //
    // PROPERTIES
    //
    //

    /** Stores the group of plottables. */
    protected PlottableGroup<C> plottables;
    
    /** Underlying visometry. */
    protected Visometry<C> visometry;

    /** Graphics object for painting. */
    protected VisometryGraphics<C> visometryGraphics;

    /** Clock timer used for the class. */
    protected ClockTimer timer = null;

    //
    //
    // EVENT HANDLING
    //
    //

    /** Default handler for mouse events. */
    protected VisometryMouseInputListener<C> defaultMouseListener;

    /** Default handler for double-click events. */
    protected CoordinateHandler<C> defaultDoubleClickHandler;

    /** Default handler for mouse wheel events. */
    protected MouseWheelListener defaultMouseWheelListener;


    //
    //
    // CONSTRUCTOR
    //
    //

    /**
     * Initializes the plot panel. Sets up graphics object for painting.
     * Sets up the following event handling:
     * <ul>
     * <li>Listen for change events to the group of plottables.
     * <li>Listen for component changes to the window.
     * <li>Listen for change events to visometry.
     * <li>Listen for mouse events input to the panel.
     * </ul>
     *
     * @param visometry the visometry to use
     */
    public PlotComponent(Visometry<C> visometry) {
        plottables = new PlottableGroup();
        setVisometry(visometry);

        // EVENT HANDLING
        plottables.addChangeListener(this);
        addComponentListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
        
        // LAYOUT
        setBackground(Color.WHITE);
        setOpaque(true);
        setPreferredSize(new Dimension(300, 200));
    }

    //
    //
    // BEAN PATTERNS
    //
    //

    /**
     * Sets this component's clock. The method first removes the component as a
     * listener on the current timer, then adds it back as a listener to the new
     * timer. If the timers are both the same, or if the provided argument is null,
     * no action is performed.
     * 
     * @param clock the underlying timer
     * @throws IllegalArgumentException if a null argument is provided
     */
    public void setClockTimer(ClockTimer clock) {
        if (clock == null) {
            throw new IllegalArgumentException("setClockTimer called with null argument!");
        }
        if (timer != clock) {
            if (timer != null) {
                timer.removeActionListener(this);
            }
            this.timer = clock;
            timer.addActionListener(this);
        }
    }

    /**
     * Returns the visometry for this component.
     *
     * @return current visometry
     */
    public Visometry<C> getVisometry() {
        return visometry;
    }
    
    /**
     * Sets the desired visometry. The method first removes this component as a listener
     * to the current visometry, then replaces it with the new visometry and sets
     * up event listening. Also updates the <code>visometryGraphics</code> property
     * with the new visometry, and sends a <code>repaint</code> command.
     *
     * @param visometry the new visometry
     * @throws IllegalArgumentException if a null argument is provided
     */
    public void setVisometry(Visometry<C> visometry) {
        if (visometry == null) {
            throw new IllegalArgumentException("setVisometry called with null argument!");
        }
        if (visometry != this.visometry) {
            if (this.visometry != null) {
                visometry.removeChangeListener(this);
            }
            this.visometry = visometry;
            this.visometry.setWindowBounds(getVisibleRect());
            this.visometry.addChangeListener(this);
            if (visometryGraphics != null) {
                visometryGraphics.setVisometry(visometry);
            } else {
                visometryGraphics = VisometryGraphics.instance(visometry); // use default graphics object
            }
            repaint();
        }
    }

    public void setDefaultCoordinateHandler(CoordinateHandler<C> ch) {
        this.defaultDoubleClickHandler = ch;
    }
    
    //
    //
    // COMPOSITIONAL
    //
    //

    /**
     * Adds a plottable to the panel.
     * 
     * @param plottable a new plottable
     */
    public void addPlottable(Plottable<? extends C> plottable) {
        plottables.add(plottable);
    }

    /**
     * Removes a plottable from the panel.
     * 
     * @param plottable a new plottable
     * @return result of remove operation
     */
    public boolean removePlottable(Plottable<? extends C> plottable) {
        return plottables.remove(plottable);
    }

    /**
     * Return list of plottables.
     *
     * @return list of plottable elements
     */
    public List<Plottable<? extends C>> getPlottables() {
        return plottables.getElements();
    }

    //
    //
    // COMPUTATION/PAINTING
    //
    //

    /**
     * Paint the components on the plot panel, using the <code>plottables</code>
     * group property to perform the painting. Some of the painting may take some
     * time, depending on the plottables. And the time taken may be longer the first
     * time, if some of the plottables are instances of <code>CachesPrimitives</code>,
     * as they may be told to recompute the first time.
     * 
     * @param g the <code>Graphics</code> object provided by the parent component
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        visometryGraphics.setScreenGraphics((Graphics2D) g);
        plottables.paintComponent(visometryGraphics);
        if (defaultMouseListener instanceof Plottable) {
            ((Plottable) defaultMouseListener).paintComponent(visometryGraphics);
        }
    }

    //
    //
    // EVENT HANDLING : delegate to the plottable group
    //
    //
    
    /**
     * This class listens for changes to any of its <code>Plottable</code>s, and
     * from its <code>Visometry</code>, and responds by repainting the screen.
     * These changes typically occur when a mouse input event causes either the
     * settings for the visometry to change, or one of the plottables to change,
     * e.g. a point is moved somewhere. The method first recomptues any plottables
     * that might have changed, and then repaints the component.
     * 
     * @param e the <code>ChangeEvent</code>
     */
    public void stateChanged(ChangeEvent e) {
        if (e.getSource() == visometry) {
            plottables.visometryChanged(visometry, visometryGraphics);
        }
        repaint();
    }

    /**
     * Here we listen for any action caused by the timer. The method first uses the timer
     * to recompute any plottables that depend on its value, and then repaints the
     * component.
     *
     * @param e the <code>ActionEvent</code>
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == timer) {
            plottables.recomputeAtTime(visometry, visometryGraphics, timer);
            repaint();
        }
    }

    //
    //
    // WINDOW SIZING EVENTS: PASS ON TO VISOMETRY
    //
    //

    public void componentResized(ComponentEvent e) {
//        System.out.println("resize");
        visometry.setWindowBounds(getVisibleRect());
        repaint();
    }

    public void componentMoved(ComponentEvent e) {
        //System.out.println("move");
    }

    public void componentShown(ComponentEvent e) {
        //System.out.println("show");
        visometry.setWindowBounds(getVisibleRect());
        repaint();
    }

    public void componentHidden(ComponentEvent e) {
        //System.out.println("hide");
    }

    //
    //
    // MOUSE EVENTS : delegate to the plottable group
    //
    //

    /** Local variable storing mouse events. */
    private transient VisometryMouseEvent<C> vme;

    /** The class handling the mouse event... called upon a press event. */
    private transient VisometryMouseInputListener<C> vmil = null;

    /** Convenience method for lazy initialization of vme. */
    private void setVME(MouseEvent e) {
        if (vme == null) {
            vme = new VisometryMouseEvent(e, visometry);
        } else {
            vme.setMouseEvent(e);
        }
    }

    /** Convenience method for finding a handler for the mouse event. */
    protected void findMouseHandler() {
        vmil = plottables.getVisometryMouseInputListener(vme);
        if (vmil == null) {
            vmil = defaultMouseListener;
        }
    }

    public void mousePressed(MouseEvent e) {
        setVME(e);
        findMouseHandler();
        if(vmil != null) {
            vmil.mousePressed(vme);
        }
    }

    public void mouseDragged(MouseEvent e) {
        setVME(e);
        if(vmil != null) {
            vmil.mouseDragged(vme);
        }
    }

    public void mouseReleased(MouseEvent e) {
        setVME(e);
        if(vmil != null) {
            vmil.mouseReleased(vme);
        }
        //vmil = null;
    }

    public void mouseClicked(MouseEvent e) {
        setVME(e);
        if (e.getClickCount() > 1 && defaultDoubleClickHandler != null) {
            defaultDoubleClickHandler.handleCoordinate(vme.getCoordinate());
        }
        if(vmil != null) {
            vmil.mouseClicked(vme);
        }
    }

    // these next classes use only the default listeners

    public void mouseEntered(MouseEvent e) {
        setVME(e);
        if (defaultMouseListener != null) {
            defaultMouseListener.mouseEntered(vme);
        }
    }

    public void mouseMoved(MouseEvent e) {
        setVME(e);
        if (defaultMouseListener != null) {
            defaultMouseListener.mouseMoved(vme);
        }
        if (plottables.getVisometryMouseInputListener(vme) != null) { // mouse is moving over an object
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
    }

    public void mouseExited(MouseEvent e) {
        setVME(e);
        if (defaultMouseListener != null) {
            defaultMouseListener.mouseExited(vme);
        }
    }

    public void mouseWheelMoved(MouseWheelEvent e) {
        if (defaultMouseWheelListener != null) {
            defaultMouseWheelListener.mouseWheelMoved(e);
        }
    }
}