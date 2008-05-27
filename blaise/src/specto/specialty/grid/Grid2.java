/**
 * Grid2.java
 * Created on Apr 8, 2008
 */

package specto.specialty.grid;

import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.Vector;
import javax.swing.JMenuItem;
import javax.swing.event.ChangeEvent;
import scio.coordinate.I2;
import sequor.model.IntegerRangeModel;
import specto.PlotPanel;
import specto.Visometry;

/**
 * This represents an "integer grid visometry", essentially allowing the user to get/reference points via a coordinate system.
 * 
 * @author Elisha Peterson
 */
public class Grid2 extends Visometry<I2> {
    
    protected IntegerRangeModel xBounds;
    protected IntegerRangeModel yBounds;
        
    /** The transformation between Grid coordinates and the window */
    private AffineTransform at;
    
    public Grid2(){
        super();
        at=new AffineTransform();
        xBounds=new IntegerRangeModel(0,0,5,1);
        yBounds=new IntegerRangeModel(0,0,5,1);
        xBounds.addChangeListener(this);
        yBounds.addChangeListener(this);
        super.initContainer(container);
    }
    public Grid2(PlotPanel p){
        super();
        at=new AffineTransform();
        xBounds=new IntegerRangeModel(0,0,5,1);
        yBounds=new IntegerRangeModel(0,0,5,1);
        xBounds.addChangeListener(this);
        yBounds.addChangeListener(this);
        initContainer(p);
    }
    
    
    // CONVERSION FUNCTIONS

    @Override
    public Point2D.Double toWindow(I2 cp) {
        Point2D.Double result = new Point2D.Double(
                at.getScaleX()*(cp.x-xBounds.getMinimum())+at.getTranslateX(),
                at.getScaleY()*(cp.y-yBounds.getMinimum())+at.getTranslateY());
        //System.out.println("input: "+cp+" and output: "+result);
        //System.out.println(" at: "+at);
        return result;
    }
    @Override
    public I2 toGeometry(Point wp) {
        I2 result = new I2(
                (int)Math.round((wp.x-at.getTranslateX())/at.getScaleX()),
                (int)Math.round((wp.y-at.getTranslateY())/at.getScaleY()));
        //System.out.println("input: "+wp+" and output: "+result);
        return result;
    }

    @Override
    public void setBounds(I2 minPoint, I2 maxPoint) {
        xBounds.setRangeProperties(minPoint.x,minPoint.x,maxPoint.x);
        yBounds.setRangeProperties(minPoint.y,minPoint.y,maxPoint.y);
    }

    /** Computes affine transformation for converting between window and geometry coordinates.
     * This will technically compute transformations for doubles, although the geometry only supports
     * integer coordinates.
     * @return
     */
    @Override
    public double computeTransformation() {
        at.setToIdentity(); 
        at.scale(getWindowWidth()/(1+xBounds.getRange()),-getWindowHeight()/(1+yBounds.getRange()));
        at.translate(.5,-yBounds.getRange()-.5);
        //System.out.println("WinSize: "+getWindowWidth()+","+getWindowHeight()+", xBounds: "+xBounds.toLongString()+", yBounds: "+yBounds.toLongString());
        //System.out.println(" AT: "+at);
        fireStateChanged();
        return 1;
    }
    
    
    
    // EVENT HANDLING

    @Override
    public void stateChanged(ChangeEvent e) {
        if(e.getSource().equals(xBounds)||e.getSource().equals(yBounds)){
            computeTransformation();
        }else{
            super.stateChanged(e);
        }
    }
    
    
    
    // OTHER
    
    @Override
    public Vector<JMenuItem> getMenuItems() {
        return new Vector<JMenuItem>();
    }
}
