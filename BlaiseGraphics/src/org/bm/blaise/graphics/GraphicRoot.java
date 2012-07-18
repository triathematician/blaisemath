/*
 * GraphicRoot.java
 * Created Jan 12, 2011
 */

package org.bm.blaise.graphics;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.Action;
import javax.swing.event.PopupMenuEvent;
import org.bm.blaise.style.StyleProvider;
import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.AbstractAction;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuListener;
import org.bm.blaise.style.StyleUtils.DefaultStyleProvider;

/**
 * <p>
 *      Manages the entries on a {@link GraphicComponent}.
 *      The primary additional behavior implemented by {@code GraphicRoot}, beyond that of its parent
 *      {@code GraphicComposite}, is listening to mouse events on the component and
 *      generating {@link GMouseEvent}s from them.
 * </p>
 * <p>
 *      Subclasses might provide additional behavior such as (i) caching the shapes to be drawn
 *      to avoid expensive recomputation, or (ii) sorting the shapes into an alternate draw order
 *      (e.g. for projections from 3D to 2D).
 * </p>
 *
 * @author Elisha
 */
public class GraphicRoot extends GraphicComposite {

    /** Parent component upon which the graphics are drawn. */
    protected JComponent component;
    /** Popup context menu */
    protected JPopupMenu popup;

    /** Default mouse listener */
    protected MouseListener defaultListener;
    /** Provides a pluggable way to generate mouse events */
    protected GMouseEvent.Factory mouseFactory = new GMouseEvent.Factory();

    /** Construct a default instance */
    public GraphicRoot() {
        setStyleProvider(new DefaultStyleProvider());
        popup = new JPopupMenu();
        popup.addPopupMenuListener(new PopupMenuListener(){
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                Graphic g = (Graphic) (mouseGraphic == null ? GraphicRoot.this : mouseGraphic);
                JMenuItem i = new JMenuItem("<html><i>"+g+"</i>"); i.setEnabled(false); i.setForeground(Color.gray);
                popup.add(i);
                popup.addSeparator();
                List<Action> actions = g.getActions();
                if (actions != null)
                    for (Action a : actions)
                        popup.add(a);
            }
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) { popup.removeAll(); }
            public void popupMenuCanceled(PopupMenuEvent e) { popup.removeAll(); }
        });
        actions.add(new AbstractAction("Set background color...") {
            public void actionPerformed(ActionEvent e) {
                Color c = JColorChooser.showDialog(component, "Select background color", component.getBackground());
                if (c != null)
                    component.setBackground(c);
            }
        });
    }

    @Override
    public String toString() {
        return "Graphics Canvas";
    }

    /**
     * Sets the component associated with the graphic tree.
     * Sets up mouse handling, and allows repainting to occur when the graphics change.
     * @param c the component
     */
    void initComponent(JComponent c) {
        if (this.component != c) {
            if (this.component != null) {
                this.component.removeMouseListener(this);
                this.component.removeMouseMotionListener(this);
                this.component.setComponentPopupMenu(null);
            }
            this.component = c;
            if (this.component != null) {
                c.addMouseListener(this);
                c.addMouseMotionListener(this);
                c.setComponentPopupMenu(popup);
            }
        }
    }

    //
    // PROPERTIES
    //

    @Override
    public final void setStyleProvider(StyleProvider rend) {
        if (rend == null)
            throw new IllegalArgumentException("GraphicRoot must have a non-null StyleProvider!");
        super.setStyleProvider(rend);
    }

    /**
     * Returns default mouse listener
     * @return default listener, or null if there is none
     */
    public MouseListener getDefaultMouseListener() {
        return defaultListener;
    }

    /**
     * Sets default mouse listener
     * @param l the default listener
     */
    public void setDefaultMouseListener(MouseListener l) {
        defaultListener = l;
    }

    /**
     * Modifies how mouse events are created.
     * @param factory responsible for generating mouse events
     */
    public void setMouseEventFactory(GMouseEvent.Factory factory) {
        this.mouseFactory = factory;
    }


    //
    // EVENT HANDLING
    //

    @Override
    protected void fireGraphicChanged() {
        if (component != null)
            component.repaint();
    }

    @Override
    public void fireAppearanceChanged() {
        if (component != null)
            component.repaint();
    }


    //<editor-fold defaultstate="collapsed" desc="MOUSE HANDLING">
    //
    // MOUSE HANDLING
    //
    
    /** Current owner of mouse events */
    private transient Graphic mouseGraphic;

    private void updateMouseGraphic(MouseEvent e) {
        if (mouseGraphic != null && mouseGraphic.interestedIn(e))
            return;
        Graphic nue = getGraphic(e);
        if (mouseGraphic != nue) {
            if (mouseGraphic != null)
                mouseGraphic.mouseExited(mouseFactory.createEvent(e, mouseGraphic));
            mouseGraphic = nue;
            if (mouseGraphic != null)
                mouseGraphic.mouseEntered(mouseFactory.createEvent(e, mouseGraphic));
        }
    }
    
    /**
     * Forwards the click event to a graphic if possible; otherwise uses a default handler.
     * @param e the mouse event
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        updateMouseGraphic(e);
        if (mouseGraphic != null)
            mouseGraphic.mouseClicked(mouseFactory.createEvent(e, mouseGraphic));
        else if (defaultListener != null)
            defaultListener.mouseClicked(mouseFactory.createEvent(e, this));
    }


    /**
     * Forwards the move event to a graphic if possible (possibly generating an enter
     * or exit event on that graphic); otherwise uses a default handler.
     * @param e the mouse event
     */
    @Override
    public void mouseMoved(MouseEvent e) {
        updateMouseGraphic(e);
        if (mouseGraphic != null)
            component.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        else if (defaultListener == null || !(defaultListener instanceof MouseMotionListener))
            component.setCursor(Cursor.getDefaultCursor());
        else
            ((MouseMotionListener)defaultListener).mouseMoved(mouseFactory.createEvent(e, this));
    }


    /**
     * Forwards the press event to a graphic if possible; otherwise uses a default handler.
     * @param e the mouse event
     */
    @Override
    public void mousePressed(MouseEvent e) {
        updateMouseGraphic(e);
        if (mouseGraphic != null) {
            mouseGraphic.mousePressed(mouseFactory.createEvent(e, mouseGraphic));
            e.consume();
        } else if (defaultListener != null)
            defaultListener.mousePressed(mouseFactory.createEvent(e, this));
    }


    /**
     * Forwards the drag event to a graphic if possible; otherwise uses a default handler.
     * @param e the mouse event
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        if (mouseGraphic != null) {
            mouseGraphic.mouseDragged(mouseFactory.createEvent(e, mouseGraphic));
            e.consume();
        } else if (defaultListener != null && defaultListener instanceof MouseMotionListener)
            ((MouseMotionListener)defaultListener).mouseDragged(mouseFactory.createEvent(e, this));
    }


    /**
     * Forwards the release event to a graphic if possible; otherwise uses a default handler.
     * @param e the mouse event
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        if (mouseGraphic != null) {
            mouseGraphic.mouseReleased(mouseFactory.createEvent(e, mouseGraphic));
            e.consume();
        } else if (defaultListener != null)
            defaultListener.mouseReleased(mouseFactory.createEvent(e, this));
    }

    /**
     * Forwards the enter event to a default handler.
     * @param e the mouse event
     */
    @Override
    public void mouseEntered(MouseEvent e) {
        if (defaultListener != null)
            defaultListener.mouseEntered(mouseFactory.createEvent(e, this));
    }

    /**
     * Forwards the exit event to a default handler.
     * @param e the mouse event
     */
    @Override
    public void mouseExited(MouseEvent e) {
        if (mouseGraphic != null)
            mouseGraphic.mouseExited(mouseFactory.createEvent(e, mouseGraphic));
        if (defaultListener != null)
            defaultListener.mouseExited(mouseFactory.createEvent(e, this));
    }
    
    //</editor-fold>
    
}
