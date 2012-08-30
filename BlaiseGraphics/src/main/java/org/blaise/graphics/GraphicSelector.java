/**
 * GraphicSelector.java
 * Created Aug 1, 2012
 */
package org.blaise.graphics;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JComponent;
import org.blaise.style.BasicShapeStyle;
import org.blaise.style.VisibilityHint;
import org.blaise.util.SetSelectionModel;

/**
 * <p>
 *  Mouse handler that enables selection on a composite graphic object.
 *  Control must be down for any selection capability.
 * </p>
 * @author elisha
 */
public class GraphicSelector extends MouseAdapter implements MouseMotionListener, CanvasPainter {

    /** Determines which objects can be selected */
    private final GraphicComposite domain;
    /** Model of selected items */
    private final SetSelectionModel<Graphic> selection = new SetSelectionModel<Graphic>();
    /** Style for drawing */
    private BasicShapeStyle style = new BasicShapeStyle(new Color(128,128,255,32), new Color(0,0,128,64));

    /** Initialize for specified component */
    public GraphicSelector(GraphicComponent gc) {
        this.domain = gc.getGraphicRoot();
        gc.addMouseListener(this);
        gc.addMouseMotionListener(this);
        gc.getOverlays().add(this);

        // highlight updates
        selection.addPropertyChangeListener(new PropertyChangeListener(){
            public void propertyChange(PropertyChangeEvent evt) {
                Set<Graphic> old = (Set<Graphic>) evt.getOldValue();
                Set<Graphic> nue = (Set<Graphic>) evt.getNewValue();
                for (Graphic g : old) {
                    if (!nue.contains(g)) {
                        g.setVisibilityHint(VisibilityHint.Highlight, false);
                    }
                }
                for (Graphic g : nue) {
                    g.setVisibilityHint(VisibilityHint.Highlight, true);
                }
            }
        });
    }

    //<editor-fold defaultstate="collapsed" desc="PROPERTIES">
    //
    // PROPERTIES
    //

    public BasicShapeStyle getStyle() {
        return style;
    }

    public void setStyle(BasicShapeStyle style) {
        this.style = style;
    }

    public SetSelectionModel getSelectionModel() {
        return selection;
    }

    //</editor-fold>


    public void paint(JComponent component, Graphics2D canvas) {
        synchronized(selection) {
            Rectangle bds;
            for (Graphic g : selection.getSelection()) {
                // XXX - add bounding box to selection to show the user
            }
        }
        if (box != null && box.width > 0 && box.height > 0) {
            style.draw(box, canvas, Collections.EMPTY_SET);
        }
    }

    private transient Point pressPt;
    private transient Point dragPt;
    private transient Rectangle box = null;
    private transient Point releasePt;

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.isConsumed() || !e.isControlDown()) {
            if (!e.isConsumed()) {
                selection.setSelection(Collections.EMPTY_SET);
            }
            return;
        }
        Graphic g = domain.selectableGraphicAt(e.getPoint());
        if (e.isShiftDown()) {
            selection.removeSelection(g);
        } else if (e.isAltDown()) {
            selection.addSelection(g);
        } else {
            selection.toggleSelection(g);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.isConsumed() || !e.isControlDown()) {
            return;
        }
        pressPt = e.getPoint();
        if (box == null) {
            box = new Rectangle();
        }
        box.setFrameFromDiagonal(pressPt, pressPt);
        e.consume();
    }

    public void mouseDragged(MouseEvent e) {
        if (e.isConsumed() || box == null) {
            return;
        }
        dragPt = e.getPoint();
        box.setFrameFromDiagonal(pressPt, dragPt);
        if (e.getSource() instanceof Component) {
            ((Component)e.getSource()).repaint();
        }
        e.consume();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.isConsumed() || box == null || pressPt == null) {
            return;
        }
        releasePt = e.getPoint();
        box.setFrameFromDiagonal(pressPt, releasePt);
        if (box.getWidth() > 0 && box.getHeight() > 0) {
            Set<Graphic> gg = domain.selectableGraphicsIn(box);
            if (e.isShiftDown()) {
                Set<Graphic> res = new HashSet<Graphic>(selection.getSelection());
                res.removeAll(gg);
                gg = res;
            } else if (e.isAltDown()) {
                gg.addAll(selection.getSelection());
            }
            selection.setSelection(gg);
            box = null;
            if (e.getSource() instanceof Component) {
                ((Component)e.getSource()).repaint();
            }
        }
        pressPt = null;
        dragPt = null;
        releasePt = null;
        e.consume();
    }

    public void mouseMoved(MouseEvent e) {}

}
