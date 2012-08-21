/**
 * GraphicSelector.java
 * Created Aug 1, 2012
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.blaise.graphics;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import javax.swing.JComponent;
import org.blaise.style.BasicShapeStyle;
import org.blaise.style.VisibilityHint;

/**
 * <p>
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
        if (box.width > 0 && box.height > 0)
            style.draw(box, canvas, VisibilityHint.Regular);
    }

    private transient Point pressPt;
    private transient Point dragPt;
    private final transient Rectangle box = new Rectangle(0,0,0,0);
    private transient Point releasePt;
    
    @Override
    public void mousePressed(MouseEvent e) {
        pressPt = e.getPoint();
        box.setFrameFromDiagonal(pressPt, pressPt);
    }
    
    public void mouseDragged(MouseEvent e) {
        dragPt = e.getPoint();
        box.setFrameFromDiagonal(pressPt, dragPt);
        if (e.getSource() instanceof Component)
            ((Component)e.getSource()).repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        releasePt = e.getPoint();
        box.setFrameFromDiagonal(pressPt, releasePt);
        selection.setSelection(domain.selectGraphics(box));
        box.setBounds(0,0,0,0);
        if (e.getSource() instanceof Component)
            ((Component)e.getSource()).repaint();
    }

    public void mouseMoved(MouseEvent e) {}

}
