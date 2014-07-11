/**
 * BlaiseSlider.java
 * Created Jun 17, 2012
 */
package org.blaise.widgets;

/*
 * #%L
 * BlaiseWidgets
 * --
 * Copyright (C) 2009 - 2014 Elisha Peterson
 * --
 * Licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import javax.swing.BoundedRangeModel;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import com.googlecode.blaisemath.graphics.AbstractGraphicDragger;
import com.googlecode.blaisemath.graphics.BasicShapeGraphic;
import com.googlecode.blaisemath.graphics.BasicTextGraphic;
import com.googlecode.blaisemath.graphics.GraphicComponent;
import com.googlecode.blaisemath.graphics.GraphicMouseEvent;
import com.googlecode.blaisemath.style.Anchor;
import com.googlecode.blaisemath.style.ShapeStyle;
import com.googlecode.blaisemath.style.Styles;
import com.googlecode.blaisemath.style.TextStyleBasic;

/**
 * <p>
 *  Visual control for a {@link BoundedRangeModel}, constructed using the
 *  {@code BlaiseGraphics} package. Similar to {@link javax.swing.JSlider}, but probably
 *  easier to configure style/appearance.
 * </p>
 * @author elisha
 */
public class BlaiseSlider extends GraphicComponent {

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
    private final BasicTextGraphic strGr;
    
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
        tStyle = Styles.fillStroke(new Color(96,96,100,192), new Color(128,128,128,192));
        hStyle = Styles.fillStroke(new Color(192,192,192,192), new Color(64,64,64,192));
        addGraphic(trackGr = new BasicShapeGraphic(null, tStyle));
        addGraphic(posGr = new BasicShapeGraphic(null, hStyle));
        addGraphic(strGr = new BasicTextGraphic(new Point(tIn.left+2,40-tIn.bottom-2), model.getValue()+""));
        strGr.setStyle(new TextStyleBasic().fill(Color.white).fontSize(hRnd/2f).textAnchor(Anchor.SOUTH));
        trackGr.setMouseEnabled(false);
        strGr.setMouseEnabled(false);
        updateGraphics();
        
        AbstractGraphicDragger dragger = new AbstractGraphicDragger() {
            Integer value0 = null;
            @Override 
            public void mouseDragInitiated(GraphicMouseEvent e, Point2D start) {
                value0 = (int) ( model.getMinimum()+(model.getMaximum()-model.getMinimum())*(e.getX()-tIn.left-hWid/2)/(double)xwid() );
            }
            @Override
            public void mouseDragInProgress(GraphicMouseEvent e, Point2D start) {
                int value = (int) ( model.getMinimum()+(model.getMaximum()-model.getMinimum())*(e.getX()-tIn.left-hWid/2)/(double)xwid() );
                model.setValue(value);
            }
            @Override
            public void mouseDragCompleted(GraphicMouseEvent e, Point2D start) {
                value0 = null;
            }
        };
        posGr.addMouseListener(dragger);
        posGr.addMouseMotionListener(dragger);
        
        setPreferredSize(new Dimension(200,hRnd+tIn.bottom+tIn.top));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        setMinimumSize(new Dimension(50,10));
        
        model.addChangeListener(MODEL_LISTENER);
    }

    private final ChangeListener MODEL_LISTENER = new ChangeListener(){
        public void stateChanged(ChangeEvent e) {
            assert e.getSource() == model;
            posGr.setDefaultTooltip(model.getValue()+"");
            repaint();
        }
    };
            
    
    
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
            this.model.removeChangeListener(MODEL_LISTENER);
            this.model = model;
            model.addChangeListener(MODEL_LISTENER);
            updateGraphics();
        }
    }
    
    //</editor-fold>
    
    

    @Override
    protected void paintComponent(Graphics g) {
        updateGraphics();
        super.paintComponent(g);
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
        strGr.setPoint(new Point2D.Double(xc, getHeight()/2+((TextStyleBasic)strGr.getStyle()).getFontSize()/2));
        strGr.setString(model.getValue()+"");
    }
    
    
    
}
