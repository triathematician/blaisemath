/**
 * GraphicSelector.java
 * Created Aug 1, 2012
 */
package org.blaise.graphics;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.Set;
import javax.swing.JComponent;
import org.blaise.style.BasicShapeStyle;
import org.blaise.style.VisibilityHint;

/**
 * <p>
 *  Mouse handler that enables selection on a composite graphic object.
 * </p>
 * @author elisha
 */
public class GraphicSelector extends MouseAdapter implements MouseMotionListener, CanvasPainter {

    /** Determines which objects can be selected */
    private final GraphicComposite domain;
    /** Model of selected items */
    private final GraphicSelectionModel selection = new GraphicSelectionModel();
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

    public GraphicSelectionModel getSelectionModel() {
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
        if (box.width > 0 && box.height > 0) {
            style.draw(box, canvas, Collections.EMPTY_SET);
        }
    }

    private transient Point pressPt;
    private transient Point dragPt;
    private final transient Rectangle box = new Rectangle(0,0,0,0);
    private transient Point releasePt;

    @Override
    public void mouseClicked(MouseEvent e) {
        int mod = e.getModifiersEx();
        if ((mod & InputEvent.SHIFT_DOWN_MASK) == InputEvent.SHIFT_DOWN_MASK) {
            Graphic g = domain.selectableGraphicAt(e.getPoint());
            selection.addSelection(g);
        } else if ((mod & InputEvent.CTRL_DOWN_MASK) == InputEvent.CTRL_DOWN_MASK) {
            Graphic g = domain.selectableGraphicAt(e.getPoint());
            selection.toggleSelection(g);
        } else {
            // single select
            Graphic g = domain.selectableGraphicAt(e.getPoint());
            if (g == null) {
                selection.setSelection(Collections.EMPTY_SET);
            } else {
                selection.setSelection(Collections.singleton(g));
            }
        }
    }
    
    @Override
    public void mousePressed(MouseEvent e) {
        pressPt = e.getPoint();
        box.setFrameFromDiagonal(pressPt, pressPt);
    }
    
    public void mouseDragged(MouseEvent e) {
        dragPt = e.getPoint();
        box.setFrameFromDiagonal(pressPt, dragPt);
        if (e.getSource() instanceof Component) {
            ((Component)e.getSource()).repaint();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        releasePt = e.getPoint();
        box.setFrameFromDiagonal(pressPt, releasePt);
        if (box.getWidth() > 0 && box.getHeight() > 0) {
            selection.setSelection(domain.selectableGraphicsIn(box));
            box.setBounds(0,0,0,0);
            if (e.getSource() instanceof Component) {
                ((Component)e.getSource()).repaint();
            }
        }
    }

    public void mouseMoved(MouseEvent e) {}

}
