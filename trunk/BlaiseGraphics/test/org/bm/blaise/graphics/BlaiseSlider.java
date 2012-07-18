/**
 * BlaiseSlider.java
 * Created Jun 17, 2012
 */
package org.bm.blaise.graphics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.geom.RoundRectangle2D;
import javax.swing.BoundedRangeModel;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.bm.blaise.style.BasicShapeStyle;
import org.bm.blaise.style.ShapeStyle;

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
    
    /** Style for track */
    private ShapeStyle tStyle;
    /** Inserts for track */
    private Insets tIn = new Insets(8,3,8,3);
    /** Rounding for track */
    private int tRnd = 20;
    
    /** Handle style */
    private ShapeStyle hStyle;
    /** Minimum width of handle */
    private int hWid = 20;
    /** Extent of handle beyond track location. */
    private int hExt = 4;
    /** Rounding on handle rectangle */
    private int hRnd = 10;
    
    //
    // CONSTRUCTORS
    //
    
    /** Initialize without arguments */
    public BlaiseSlider() {
//        setBackground(Color.black);
        tStyle = new BasicShapeStyle(new Color(96,96,100,192), new Color(128,128,128,192));
        hStyle = new BasicShapeStyle(new Color(192,192,192,192), new Color(128,128,128,192));
        addGraphic(trackGr = new BasicShapeGraphic(null, tStyle));
        addGraphic(posGr = new BasicShapeGraphic(null, hStyle));
        updateGraphics();
        
        posGr.addMouseListener(new GMouseDragger.Dragger() {
            @Override 
            public void mouseDragInitiated(GMouseEvent e, Point start) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
            @Override
            public void mouseDragInProgress(GMouseEvent e, Point start) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        });
        
        setPreferredSize(new Dimension(200,40));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        setMinimumSize(new Dimension(50,10));
    }

    public void stateChanged(ChangeEvent e) {
        assert e.getSource() == model;
        
    }
    
    private void updateGraphics() {
        int wid = getWidth(), ht = getHeight();
        int x0 = tIn.left, y0 = tIn.top, xwid = wid-tIn.left-tIn.right, yht = ht-tIn.top-tIn.bottom;
        boolean onePt = model.getExtent() == 0;
        double xc = x0+xwid*((model.getValue()-model.getMinimum())/(double)(model.getMaximum()-model.getMinimum()));
        double xc2 = onePt ? xc  : xc+xwid*model.getExtent()/(double)(model.getMaximum()-model.getMinimum());
        trackGr.setPrimitive(new RoundRectangle2D.Double(x0,y0,xwid,yht, tRnd, tRnd));
        if (onePt) {
            int rad = Math.min(hWid/2, xwid/10);
            posGr.setPrimitive(new RoundRectangle2D.Double(xc-rad, y0-hExt, 2*rad, yht+2*hExt, hRnd, hRnd));
        } else
            posGr.setPrimitive(new RoundRectangle2D.Double(xc, y0-hExt, xc2-xc, yht+2*hExt, hRnd, hRnd));
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

    @Override
    protected void paintComponent(Graphics g) {
        updateGraphics();
        super.paintComponent(g);
    }
    
    
    
}
