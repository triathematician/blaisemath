/**
 * BlaiseSlider.java
 * Created Jun 17, 2012
 */
package org.blaise.graphics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import javax.swing.BoundedRangeModel;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.blaise.style.Anchor;
import org.blaise.style.BasicShapeStyle;
import org.blaise.style.BasicStringStyle;
import org.blaise.style.ShapeStyle;

/**
 * <p>
 *  A slider implementation based on the {@code BlaiseGraphics} package. Intended
 *  to be similar in usage to {@link JSlider}.
 * </p>
 * @author elisha
 */
public class BlaiseSlider extends GraphicComponent implements ChangeListener {

    //
    // ATTRIBUTES
    //
    
    /** Tracks slider boundaries and location/extent within boundaries. */
    private BoundedRangeModel model = new DefaultBoundedRangeModel(50, 0, 0, 100);
    /** Component for the slider "track" */
    private final BasicShapeGraphic trackGr;
    /** Component for the position on track */
    private final BasicShapeGraphic posGr;
    /** Label for current value */
    private final BasicStringGraphic strGr;
    
    /** Style for track */
    private ShapeStyle tStyle;
    /** Inserts for track */
    private Insets tIn = new Insets(6,4,6,4);
    /** Rounding for track */
    private int tRnd = 30;
    
    /** Handle style */
    private ShapeStyle hStyle;
    /** Minimum width of handle */
    private int hWid = 24;
    /** Extent of handle beyond track location. */
    private int hExt = -1;
    /** Rounding on handle rectangle */
    private int hRnd = 24;
    
    //
    // CONSTRUCTORS
    //
    
    /** Initialize without arguments */
    public BlaiseSlider() {
        tStyle = new BasicShapeStyle(new Color(96,96,100,192), new Color(128,128,128,192));
        hStyle = new BasicShapeStyle(new Color(192,192,192,192), new Color(64,64,64,192));
        addGraphic(trackGr = new BasicShapeGraphic(null, tStyle));
        addGraphic(posGr = new BasicShapeGraphic(null, hStyle));
        addGraphic(strGr = new BasicStringGraphic(new Point(tIn.left+2,40-tIn.bottom-2), model.getValue()+""));
        strGr.setStyle(new BasicStringStyle(Color.white, hRnd/2f, Anchor.South));
        trackGr.setMouseSupported(false);
        strGr.setMouseSupported(false);
        updateGraphics();
        
        AbstractGraphicDragger dragger = new AbstractGraphicDragger() {
            Integer value0 = null;
            @Override 
            public void mouseDragInitiated(GraphicMouseEvent e, Point start) {
                value0 = (int) ( model.getMinimum()+(model.getMaximum()-model.getMinimum())*(e.getX()-tIn.left-hWid/2)/(double)xwid() );
            }
            @Override
            public void mouseDragInProgress(GraphicMouseEvent e, Point start) {
                int value = (int) ( model.getMinimum()+(model.getMaximum()-model.getMinimum())*(e.getX()-tIn.left-hWid/2)/(double)xwid() );
                model.setValue(value);
            }
            @Override
            public void mouseDragCompleted(GraphicMouseEvent e, Point start) {
                value0 = null;
            }
        };
        posGr.addMouseListener(dragger);
        posGr.addMouseMotionListener(dragger);
        
        setPreferredSize(new Dimension(200,hRnd+tIn.bottom+tIn.top));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        setMinimumSize(new Dimension(50,10));
        
        model.addChangeListener(this);
    }

    
    //<editor-fold defaultstate="collapsed" desc="PROPERTIES">
    //
    // PROPERTIES
    //

    public BoundedRangeModel getModel() {
        return model;
    }

    public void setModel(BoundedRangeModel model) {
        if (model == null)
            throw new IllegalArgumentException();
        if (this.model != model) {
            this.model.removeChangeListener(this);
            this.model = model;
            model.addChangeListener(this);
            updateGraphics();
        }
    }
    
    //</editor-fold>
    
    
    public void stateChanged(ChangeEvent e) {
        assert e.getSource() == model;
        posGr.setTooltip(model.getValue()+"");
        repaint();
    }
    
    private int xwid() {
        return getWidth()-tIn.left-tIn.right-hWid;
    }
    
    private int yht() {
        return getHeight()-tIn.top-tIn.bottom;
    }
    
    private void updateGraphics() {
        int x0 = tIn.left, y0 = tIn.top, xwid = xwid(), yht = yht();
        boolean onePt = model.getExtent() == 0;
        double xc = x0+hWid/2-hExt+(xwid+2*hExt)*((model.getValue()-model.getMinimum())/(double)(model.getMaximum()-model.getMinimum()));
        double xc2 = onePt ? xc : xc+xwid*model.getExtent()/(double)(model.getMaximum()-model.getMinimum());
        trackGr.setPrimitive(new RoundRectangle2D.Double(x0,y0,xwid+hWid,yht, tRnd, tRnd));
        if (onePt) {
            int rad = Math.min(hWid/2, xwid/10);
            posGr.setPrimitive(new RoundRectangle2D.Double(xc-rad, y0-hExt, 2*rad, yht+2*hExt, hRnd, hRnd));
        } else
            posGr.setPrimitive(new RoundRectangle2D.Double(xc, y0-hExt, xc2-xc, yht+2*hExt, hRnd, hRnd));
        strGr.setPoint(new Point2D.Double(xc, getHeight()/2+((BasicStringStyle)strGr.getStyle()).getFontSize()/2));
        strGr.setString(model.getValue()+"");
    }

    @Override
    protected void paintComponent(Graphics g) {
        updateGraphics();
        super.paintComponent(g);
    }
    
    
    
}
