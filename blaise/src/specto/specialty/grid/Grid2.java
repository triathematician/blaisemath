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
    
    /** Determines the margin around the window before the grid starts. */    
    int margin=10;
        
    /** The transformation between Grid coordinates and the window */
    private AffineTransform at;
    
    public Grid2(){
        super();
        at=new AffineTransform();
        xBounds=new IntegerRangeModel(0,0,5,1);
        yBounds=new IntegerRangeModel(0,0,5,1);
        xBounds.addChangeListener(this);
        yBounds.addChangeListener(this);
    }
    public Grid2(PlotPanel p){
        super(p);
        at=new AffineTransform();
        xBounds=new IntegerRangeModel(0,0,5,1);
        yBounds=new IntegerRangeModel(0,0,5,1);
        xBounds.addChangeListener(this);
        yBounds.addChangeListener(this);
    }
    
    
    // CONVERSION FUNCTIONS

    @Override
    public Point2D.Double toWindow(I2 cp) {return new Point2D.Double(at.getScaleX()*cp.x+at.getTranslateX(),at.getScaleY()*cp.y+at.getTranslateY());}
    @Override
    public I2 toGeometry(Point wp) {return new I2((int)((wp.x-at.getTranslateX())/at.getScaleX()),(int)((wp.y-at.getTranslateY())/at.getScaleY()));}

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
        at.scale((getWindowWidth()-2*margin)/xBounds.getRange(),(getWindowHeight()-2*margin)/yBounds.getRange());
        at.translate(-xBounds.getMinimum(),-yBounds.getMaximum());
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
